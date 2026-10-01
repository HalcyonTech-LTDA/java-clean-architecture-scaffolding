import http from 'k6/http';

/**
 * Standardized HTTP client wrapper for k6 scenarios.
 */
export class ApiClient {
  constructor(config) {
    this.baseUrl = config.baseUrl;
    this.defaultParams = {
      headers: config.headers,
      timeout: config.timeout,
    };
  }

  get(endpoint, tags = {}) {
    const url = `${this.baseUrl}${endpoint}`;
    const params = Object.assign({}, this.defaultParams, {
      tags: Object.assign({ endpoint }, tags),
    });
    return http.get(url, params);
  }

  post(endpoint, payload, tags = {}) {
    const url = `${this.baseUrl}${endpoint}`;
    const body = typeof payload === 'string' ? payload : JSON.stringify(payload);
    const params = Object.assign({}, this.defaultParams, {
      tags: Object.assign({ endpoint }, tags),
    });
    return http.post(url, body, params);
  }
}
