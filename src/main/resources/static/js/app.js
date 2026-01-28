// CSRF header を fetch に付与する補助（必要な画面だけで利用）
(function () {
  const csrf = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
  const header = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

  window.bmFetch = async (url, options = {}) => {
    const opts = { credentials: 'include', ...options };
    opts.headers = new Headers(opts.headers || {});
    if (csrf && header) opts.headers.set(header, csrf);
    return fetch(url, opts);
  };
})();
