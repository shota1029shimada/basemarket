/**
 * BaseMarket - 共通フロント用スクリプト
 * CSRF付きfetch・フラッシュメッセージなど
 */
(function () {
  'use strict';

  /** CSRFトークン取得（metaタグから） */
  function getCsrfToken() {
    var meta = document.querySelector('meta[name="_csrf"]');
    return meta ? meta.getAttribute('content') : null;
  }

  /** CSRFヘッダ名取得 */
  function getCsrfHeader() {
    var meta = document.querySelector('meta[name="_csrf_header"]');
    return meta ? meta.getAttribute('content') : 'X-CSRF-TOKEN';
  }

  /**
   * CSRF付きfetch
   * @param {string} url - リクエスト先URL
   * @param {RequestInit} options - fetchのオプション（headersはマージされる）
   * @returns {Promise<Response>}
   */
  window.fetchWithCsrf = function (url, options) {
    options = options || {};
    var token = getCsrfToken();
    var headerName = getCsrfHeader();
    var headers = options.headers || {};
    if (token && headerName) {
      if (headers instanceof Headers) {
        headers.append(headerName, token);
      } else if (Array.isArray(headers)) {
        headers = headers.concat([[headerName, token]]);
      } else {
        headers[headerName] = token;
      }
    }
    options.headers = headers;
    // Cookieを送信するためにcredentialsを設定（デフォルトはsame-origin）
    options.credentials = options.credentials || 'include';
    return fetch(url, options);
  };

  /**
   * フラッシュメッセージ表示
   * @param {string} msg - 表示するメッセージ
   * @param {string} type - 'success' | 'error'（省略時はsuccess）
   * @param {number} duration - 表示時間（ミリ秒、0で消さない）
   */
  window.flash = function (msg, type, duration) {
    type = type || 'success';
    duration = duration !== undefined ? duration : 3000;
    var el = document.getElementById('flash');
    if (!el) return;
    el.textContent = msg;
    el.className = 'flash-message' + (type === 'error' ? ' error' : '');
    el.style.display = 'block';
    if (duration > 0) {
      setTimeout(function () {
        el.textContent = '';
        el.style.display = '';
      }, duration);
    }
  };
})();
