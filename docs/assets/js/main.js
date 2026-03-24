/* FlexGanttFX — main.js */

// ── Copyright year ───────────────────────────────────────────
const yearEl = document.getElementById('copyright-year');
if (yearEl) yearEl.textContent = new Date().getFullYear();

// ── Mobile menu ──────────────────────────────────────────────
const hamburger = document.getElementById('hamburger');
const mobileMenu = document.getElementById('mobile-menu');
if (hamburger && mobileMenu) {
  hamburger.addEventListener('click', () => {
    mobileMenu.classList.toggle('open');
    hamburger.classList.toggle('active');
  });
}

// ── FAQ accordion ────────────────────────────────────────────
document.querySelectorAll('.faq-question').forEach(btn => {
  btn.addEventListener('click', () => {
    const item = btn.closest('.faq-item');
    const isOpen = item.classList.contains('open');
    document.querySelectorAll('.faq-item.open').forEach(el => el.classList.remove('open'));
    if (!isOpen) item.classList.add('open');
  });
});

// ── Video modal ──────────────────────────────────────────────
const videoModal    = document.getElementById('video-modal');
const videoIframe   = document.getElementById('video-modal-iframe');
const closeBtn      = document.getElementById('video-modal-close');
const backdrop      = document.getElementById('video-modal-backdrop');

function openVideoModal(videoId) {
  if (!videoModal || !videoIframe) return;
  videoIframe.src = `https://www.youtube-nocookie.com/embed/${videoId}?autoplay=1&rel=0`;
  videoModal.classList.add('open');
  document.body.style.overflow = 'hidden';
  closeBtn && closeBtn.focus();
}

function closeVideoModal() {
  if (!videoModal) return;
  videoModal.classList.remove('open');
  videoIframe.src = '';          // stop playback
  document.body.style.overflow = '';
}

document.querySelectorAll('.video-card[data-video-id]').forEach(card => {
  card.addEventListener('click', () => openVideoModal(card.dataset.videoId));
});

closeBtn   && closeBtn.addEventListener('click', closeVideoModal);
backdrop   && backdrop.addEventListener('click', closeVideoModal);

document.addEventListener('keydown', e => {
  if (e.key === 'Escape') closeVideoModal();
});

// ── Scroll fade-in ───────────────────────────────────────────
const observer = new IntersectionObserver((entries) => {
  entries.forEach(entry => {
    if (entry.isIntersecting) {
      entry.target.classList.add('visible');
      observer.unobserve(entry.target);
    }
  });
}, { threshold: 0.1, rootMargin: '0px 0px -40px 0px' });

document.querySelectorAll('.fade-in').forEach(el => observer.observe(el));

// ── Active nav highlight ─────────────────────────────────────
(function() {
  const current = window.location.pathname;
  document.querySelectorAll('.sidebar-nav a').forEach(a => {
    if (a.getAttribute('href') && current.endsWith(a.getAttribute('href').replace('../', '').replace('./', ''))) {
      a.classList.add('active');
    }
  });
})();

// ── Open external links in new tab ───────────────────────────
document.querySelectorAll('a[href^="http"]').forEach(a => {
  if (!a.hostname || a.hostname !== window.location.hostname) {
    a.setAttribute('target', '_blank');
    a.setAttribute('rel', 'noopener noreferrer');
  }
});

// ── Stagger grid children ────────────────────────────────────
document.querySelectorAll('.features-grid, .demos-grid, .videos-grid, .testimonials-grid').forEach(grid => {
  Array.from(grid.children).forEach((child, i) => {
    child.style.transitionDelay = `${i * 60}ms`;
    child.classList.add('fade-in');
    observer.observe(child);
  });
});
