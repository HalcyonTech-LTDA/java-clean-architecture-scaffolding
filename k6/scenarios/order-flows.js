import { check } from 'k6';
import { randomOrder, randomUuid } from '../helpers/data-generator.js';

/**
 * Order creation user journey.
 * Note: An order creation may return 201 (Created) if the customer is ACTIVE/eligible,
 * or 422 (Unprocessable Entity) if the customer is PENDING_BUREAU_ENRICHMENT / not eligible.
 * Both are valid business responses defined by domain rules and not HTTP 5xx errors.
 */
export function createOrderFlow(apiClient, customerId) {
  const payload = randomOrder(customerId);
  const response = apiClient.post('/orders', payload, { name: 'POST /orders' });

  const isValidResponse = check(response, {
    'POST /orders status is 201 or 422': (r) => r.status === 201 || r.status === 422,
    'POST /orders returns valid response body': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body !== null && typeof body === 'object';
      } catch (e) {
        return false;
      }
    },
  });

  if (response.status === 201) {
    try {
      return JSON.parse(response.body);
    } catch (e) {
      return null;
    }
  }

  return null;
}

/**
 * Order retrieval by ID user journey.
 */
export function getOrderByIdFlow(apiClient, orderId) {
  if (!orderId) return false;

  const response = apiClient.get(`/orders/${orderId}`, { name: 'GET /orders/{id}' });

  return check(response, {
    'GET /orders/{id} status is 200': (r) => r.status === 200,
    'GET /orders/{id} returns matching id': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body && body.id === orderId;
      } catch (e) {
        return false;
      }
    },
  });
}

/**
 * Negative scenario: get non-existent order.
 */
export function getNonExistentOrderFlow(apiClient) {
  const fakeId = `missing-${randomUuid()}`;
  const response = apiClient.get(`/orders/${fakeId}`, { name: 'GET /orders/{id} [404]' });

  return check(response, {
    'GET /orders/{missing-id} status is 404': (r) => r.status === 404,
  });
}
