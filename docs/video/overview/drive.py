#!/usr/bin/env python3
"""
drive.py — post real CoreGraphics input events to a running FlexGanttFX demo.

Used together with capture.sh to produce the `CAPTURE` shots in storyboard.md
with genuine, repeatable UI motion instead of hand-held mouse work.

AppleScript's "System Events ... click" does NOT reach JavaFX canvases — those
are accessibility-level actions and the toolkit ignores them. This script posts
real CGEvents at the HID tap, which JavaFX handles exactly like a physical
mouse.

Requirements:
    python3 -m venv .venv
    .venv/bin/pip install pyobjc-framework-Quartz
    Accessibility permission for the terminal running the script.

All coordinates are SCREEN points (not pixels). With the demo window placed at
100,100 by capture.sh, window-relative point (x, y) is screen (100+x, 100+y).

Usage:
    drive.py click   X Y
    drive.py scroll  X Y LINES [STEPS] [DELAY]     # vertical, negative = up
    drive.py hscroll X Y LINES [STEPS] [DELAY]     # horizontal
    drive.py drag    X1 Y1 X2 Y2 [STEPS] [DELAY]
    drive.py zoom    X Y CLICKS [DELAY]            # ctrl+scroll

Note: `zoom` only does something if the demo binds ctrl+scroll. Several demos
do not — for those, click the toolbar zoom buttons repeatedly instead, e.g.
`drive.py click 337 187` for the zoom-in button of the Airport demo at the
default 1280x720 window placement.

Examples:
    # slow vertical scroll through the rows, 4 seconds of motion
    drive.py scroll 800 500 -40 120 0.03

    # pan the timeline sideways
    drive.py hscroll 800 400 -60 220 0.04
"""

import sys
import time

import Quartz

BUTTON = Quartz.kCGMouseButtonLeft
TAP = Quartz.kCGHIDEventTap


def _post(event):
    Quartz.CGEventPost(TAP, event)


def move(x, y):
    _post(Quartz.CGEventCreateMouseEvent(None, Quartz.kCGEventMouseMoved, (x, y), 0))


def click(x, y):
    move(x, y)
    time.sleep(0.05)
    _post(Quartz.CGEventCreateMouseEvent(None, Quartz.kCGEventLeftMouseDown, (x, y), BUTTON))
    time.sleep(0.05)
    _post(Quartz.CGEventCreateMouseEvent(None, Quartz.kCGEventLeftMouseUp, (x, y), BUTTON))


def _scroll(x, y, lines, steps, delay, axis, flags=0):
    """Emit many small scroll events so the motion reads as smooth on video."""
    move(x, y)
    time.sleep(0.1)
    per_step = lines / float(steps)
    carry = 0.0
    for _ in range(steps):
        carry += per_step
        tick = int(carry)
        if tick:
            carry -= tick
            if axis == "v":
                ev = Quartz.CGEventCreateScrollWheelEvent(None, Quartz.kCGScrollEventUnitLine, 1, tick)
            else:
                ev = Quartz.CGEventCreateScrollWheelEvent(None, Quartz.kCGScrollEventUnitLine, 2, 0, tick)
            if flags:
                Quartz.CGEventSetFlags(ev, flags)
            _post(ev)
        time.sleep(delay)


def scroll(x, y, lines, steps=100, delay=0.03):
    _scroll(x, y, lines, steps, delay, "v")


def hscroll(x, y, lines, steps=100, delay=0.03):
    _scroll(x, y, lines, steps, delay, "h")


def zoom(x, y, clicks, delay=0.06):
    """Ctrl + scroll — the usual zoom gesture on a Gantt graphics area."""
    _scroll(x, y, clicks, abs(int(clicks)), delay, "v", Quartz.kCGEventFlagMaskControl)


def drag(x1, y1, x2, y2, steps=60, delay=0.02):
    move(x1, y1)
    time.sleep(0.1)
    _post(Quartz.CGEventCreateMouseEvent(None, Quartz.kCGEventLeftMouseDown, (x1, y1), BUTTON))
    for i in range(1, steps + 1):
        t = i / float(steps)
        # ease in/out so the drag does not start or stop abruptly on camera
        t = t * t * (3 - 2 * t)
        x = x1 + (x2 - x1) * t
        y = y1 + (y2 - y1) * t
        _post(Quartz.CGEventCreateMouseEvent(None, Quartz.kCGEventLeftMouseDragged, (x, y), BUTTON))
        time.sleep(delay)
    _post(Quartz.CGEventCreateMouseEvent(None, Quartz.kCGEventLeftMouseUp, (x2, y2), BUTTON))


COMMANDS = {
    "click": (click, 2),
    "scroll": (scroll, 3),
    "hscroll": (hscroll, 3),
    "zoom": (zoom, 3),
    "drag": (drag, 4),
}


def main(argv):
    if len(argv) < 2 or argv[1] not in COMMANDS:
        print(__doc__)
        return 2
    fn, required = COMMANDS[argv[1]]
    args = argv[2:]
    if len(args) < required:
        print(f"{argv[1]} needs at least {required} arguments")
        return 2
    numbers = [float(a) if "." in a else int(a) for a in args]
    fn(*numbers)
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv))
