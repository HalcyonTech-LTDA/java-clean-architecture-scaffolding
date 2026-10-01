/**
 * Environment configuration for k6 performance test suites.
 * Allows switching target environments via K6_ENV or TARGET_URL environment variables.
 *
 * Usage:
 *   k6 run -e K6_ENV=local k6/tests/01-smoke-test.js
 *   k6 run -e K6_ENV=dev k6/tests/02-load-test.js
 *   k6 run -e TARGET_URL=http://my-host:8080 k6/tests/03-stress-test.js
 */

export const ENVIRONMENTS = {
  local: {
    name: 'local',
    baseUrl: 'http://localhost:8080',
    dockerBaseUrl: 'http://host.docker.internal:8080',
    timeout: '10s',
  },
  dev: {
    name: 'dev',
    baseUrl: 'https://dev-api.example.com',
    dockerBaseUrl: 'https://dev-api.example.com',
    timeout: '15s',
  },
  staging: {
    name: 'staging',
    baseUrl: 'https://staging-api.example.com',
    dockerBaseUrl: 'https://staging-api.example.com',
    timeout: '15s',
  },
  prod: {
    name: 'prod',
    baseUrl: 'https://api.example.com',
    dockerBaseUrl: 'https://api.example.com',
    timeout: '20s',
  },
};

export function getConfig() {
  const envName = (__ENV.K6_ENV || 'local').toLowerCase();
  const envConfig = ENVIRONMENTS[envName] || ENVIRONMENTS.local;

  let resolvedBaseUrl = envConfig.baseUrl;

  if (__ENV.TARGET_URL) {
    resolvedBaseUrl = __ENV.TARGET_URL;
  } else if (__ENV.RUNNING_IN_DOCKER === 'true' && envName === 'local') {
    resolvedBaseUrl = envConfig.dockerBaseUrl;
  }

  return {
    envName,
    baseUrl: resolvedBaseUrl,
    timeout: envConfig.timeout,
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
      'User-Agent': 'k6-load-test-agent/1.0',
    },
  };
}
