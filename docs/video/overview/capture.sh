#!/usr/bin/env bash
#
# capture.sh — record b-roll of a running FlexGanttFX demo on macOS.
#
# Produces the `CAPTURE` shots described in storyboard.md: a 60 fps H.264 clip
# and a matching PNG still, cropped to the demo window at native retina
# resolution.
#
# Requirements:
#   - macOS with ffmpeg (brew install ffmpeg)
#   - Screen Recording permission for the terminal running this script
#     (System Settings ▸ Privacy & Security ▸ Screen Recording)
#   - Accessibility permission for window positioning
#     (System Settings ▸ Privacy & Security ▸ Accessibility)
#
# Usage:
#   ./capture.sh <name> [seconds] [window-title-substring] [width] [height]
#
# Example workflow:
#   mvn -pl FlexGanttFXDemos/FlexGanttFXAirport javafx:run &   # start a demo
#   ./capture.sh shot06-scrolling 12 "Frankfurt Airport"       # record it
#
# The window-title substring matters: several JavaFX windows are often open at
# once (showcase, IDE previews, other demos). Without it the script grabs
# whichever java window happens to be first, which is rarely the one you want.
#
# List candidate window titles with:
#   ./capture.sh --list
#
# Output goes to ./captures/ which is intentionally NOT committed.

set -euo pipefail

if [ "${1:-}" = "--list" ]; then
  osascript <<'EOF'
set out to ""
tell application "System Events"
  repeat with p in (every process whose name contains "java")
    repeat with w in (every window of p)
      set out to out & (name of w) & linefeed
    end repeat
  end repeat
end tell
return out
EOF
  exit 0
fi

NAME="${1:?usage: capture.sh <name> [seconds] [window-title-substring] [width] [height]}"
SECONDS_TO_RECORD="${2:-10}"
WIN_TITLE="${3:-FlexGanttFX}"
WIN_W="${4:-1280}"
WIN_H="${5:-720}"
WIN_X=100
WIN_Y=100

OUT_DIR="$(cd "$(dirname "$0")" && pwd)/captures"
mkdir -p "$OUT_DIR"

# --- locate and position the demo window -------------------------------------
echo "==> positioning window matching '${WIN_TITLE}' to ${WIN_W}x${WIN_H} at ${WIN_X},${WIN_Y}"
MATCHED=$(osascript <<EOF
tell application "System Events"
  repeat with p in (every process whose name contains "java")
    repeat with w in (every window of p)
      if (name of w) contains "$WIN_TITLE" then
        set frontmost of p to true
        tell w
          perform action "AXRaise"
          set position to {$WIN_X, $WIN_Y}
          set size to {$WIN_W, $WIN_H}
        end tell
        return name of w
      end if
    end repeat
  end repeat
  return ""
end tell
EOF
)
: "${MATCHED:?no java window title contains '${WIN_TITLE}' — run ./capture.sh --list}"
echo "==> matched window: ${MATCHED}"

# Give the toolkit a moment to finish relayout after the resize.
sleep 2

# --- optional pre-record click ------------------------------------------------
# PRE_CLICK="x,y" (window-relative points) clicks a control before recording,
# e.g. the Airport demo's "Simulate" button, so the clip contains real motion
# without a visible cursor drag.
if [ -n "${PRE_CLICK:-}" ]; then
  CX=$(( WIN_X + ${PRE_CLICK%,*} ))
  CY=$(( WIN_Y + ${PRE_CLICK#*,} ))
  echo "==> pre-click at screen point ${CX},${CY}"
  osascript -e "tell application \"System Events\" to click at {$CX, $CY}" >/dev/null 2>&1 || \
    echo "    (click failed — some JavaFX builds ignore synthetic AX clicks; click manually instead)"
  sleep "${PRE_CLICK_SETTLE:-2}"
fi

# --- still frame + backing scale factor (1x vs retina 2x) --------------------
# screencapture -R takes points, but writes native pixels, so the resulting
# still tells us the scale factor directly.
screencapture -x -R"${WIN_X},${WIN_Y},${WIN_W},${WIN_H}" -t png "$OUT_DIR/${NAME}.png"
PIXEL_W=$(python3 -c "import struct,sys;print(struct.unpack('>II',open(sys.argv[1],'rb').read(24)[16:24])[0])" "$OUT_DIR/${NAME}.png")
SCALE=$(python3 -c "print(max(1, round($PIXEL_W/$WIN_W)))")
echo "==> still:  $OUT_DIR/${NAME}.png (${PIXEL_W}px wide, scale ${SCALE}x)"

CROP_W=$((WIN_W * SCALE))
CROP_H=$((WIN_H * SCALE))
CROP_X=$((WIN_X * SCALE))
CROP_Y=$((WIN_Y * SCALE))

# --- find the ffmpeg avfoundation index of the main screen -------------------
# ffmpeg exits non-zero after listing devices, hence the `|| true`.
SCREEN_IDX=$( { ffmpeg -f avfoundation -list_devices true -i "" 2>&1 || true; } \
  | sed -n 's/.*\[\([0-9]*\)\] Capture screen 0.*/\1/p' | head -1)
: "${SCREEN_IDX:?could not find 'Capture screen 0' — check Screen Recording permission}"
echo "==> capturing avfoundation device ${SCREEN_IDX}, crop ${CROP_W}x${CROP_H}+${CROP_X}+${CROP_Y}"

# --- motion clip -------------------------------------------------------------
# DRIVE="scroll 800 500 -40 120 0.03" runs drive.py concurrently with the
# recording so the clip contains real UI motion. DRIVE_DELAY holds the opening
# frames still before the motion starts, giving the editor a clean handle.
if [ -n "${DRIVE:-}" ]; then
  PYBIN="${DRIVE_PYTHON:-python3}"
  ( sleep "${DRIVE_DELAY:-1}"; "$PYBIN" "$(dirname "$0")/drive.py" $DRIVE ) &
  DRIVE_PID=$!
  echo "==> driving: $DRIVE"
fi

# -capture_cursor 0 hides the pointer; the storyboard only wants it visible when
# an interaction is the subject of the shot.
ffmpeg -y -hide_banner -loglevel error \
  -f avfoundation -capture_cursor 0 -framerate 60 -pixel_format bgr0 -i "$SCREEN_IDX" \
  -t "$SECONDS_TO_RECORD" \
  -vf "crop=${CROP_W}:${CROP_H}:${CROP_X}:${CROP_Y}" \
  -c:v libx264 -crf 16 -preset slow -pix_fmt yuv420p \
  "$OUT_DIR/${NAME}.mp4"

if [ -n "${DRIVE:-}" ]; then
  wait "$DRIVE_PID" 2>/dev/null || true
fi

echo "==> clip:   $OUT_DIR/${NAME}.mp4"
ffprobe -v error -show_entries stream=width,height,r_frame_rate,nb_frames \
  -of csv=p=0 "$OUT_DIR/${NAME}.mp4"
