/* FlexGanttFX — lang.js
   Locale detection, one-time auto-redirect and language preference handling.
   Must be included in <head> (synchronously) so redirects happen before paint. */

(function () {
  'use strict';

  var STORAGE_KEY = 'fgfx-lang';
  var REDIRECT_KEY = 'fgfx-redirected';
  var SUPPORTED = ['en', 'de', 'fr', 'zh'];

  // ── Current page language from URL prefix ──────────────────
  var match = window.location.pathname.match(/^\/(de|fr|zh)\//);
  var currentLang = match ? match[1] : 'en';

  function mapLocale(locale) {
    var l = String(locale || '').toLowerCase();
    if (l.indexOf('de') === 0) return 'de';
    if (l.indexOf('fr') === 0) return 'fr';
    if (l.indexOf('zh') === 0) return 'zh';
    if (l.indexOf('en') === 0) return 'en';
    return null;
  }

  function preferredLang() {
    var stored = null;
    try { stored = window.localStorage.getItem(STORAGE_KEY); } catch (e) { /* ignore */ }
    if (stored && SUPPORTED.indexOf(stored) >= 0) return stored;
    var locales = navigator.languages || [navigator.language || 'en'];
    for (var i = 0; i < locales.length; i++) {
      var lang = mapLocale(locales[i]);
      if (lang) return lang;
    }
    return 'en';
  }

  // Translate the current pathname into the equivalent page of another language.
  function pageUrlFor(lang) {
    var path = window.location.pathname.replace(/^\/(de|fr|zh)(?=\/)/, '');
    if (lang !== 'en') path = '/' + lang + path;
    return path + window.location.search + window.location.hash;
  }

  // ── One-time auto-redirect to the preferred language ───────
  var redirected = null;
  try { redirected = window.sessionStorage.getItem(REDIRECT_KEY); } catch (e) { /* ignore */ }
  var preferred = preferredLang();
  if (preferred !== currentLang && !redirected) {
    try { window.sessionStorage.setItem(REDIRECT_KEY, '1'); } catch (e) { /* ignore */ }
    window.location.replace(pageUrlFor(preferred));
    return;
  }

  // ── Remember explicit language choices ─────────────────────
  document.addEventListener('DOMContentLoaded', function () {
    var links = document.querySelectorAll('a[data-lang]');
    for (var i = 0; i < links.length; i++) {
      links[i].addEventListener('click', function () {
        try {
          window.localStorage.setItem(STORAGE_KEY, this.getAttribute('data-lang'));
          window.sessionStorage.setItem(REDIRECT_KEY, '1');
        } catch (e) { /* ignore */ }
      });
    }
  });
})();
