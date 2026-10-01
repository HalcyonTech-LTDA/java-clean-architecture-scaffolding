import { check } from 'k6';
import { randomCustomer, randomUuid } from '../helpers/data-generator.js';

/**
 * Customer creation user journey.
 */
export function createCustomerFlow(apiClient) {
  const payload = randomCustomer(__VU, __ITER);
  const response = apiClient.post('/customers', payload, { name: 'POST /customers' });

  const isCreated = check(response, {
    'POST /customers status is 201': (r) => r.status === 201,
    'POST /customers returns customer ID': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body && typeof body.id === 'string' && body.id.length > 0;
      } catch (e) {
        return false;
      }
    },
    'POST /customers response contains correct email': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body && body.email === payload.email;
      } catch (e) {
        return false;
      }
    },
  });

  if (isCreated) {
    try {
      return JSON.parse(response.body);
    } catch (e) {
      return null;
    }
  }
  return null;
}

/**
 * Customer retrieval by ID user journey.
 */
export function getCustomerByIdFlow(apiClient, customerId) {
  if (!customerId) return false;

  const response = apiClient.get(`/customers/${customerId}`, { name: 'GET /customers/{id}' });

  return check(response, {
    'GET /customers/{id} status is 200': (r) => r.status === 200,
    'GET /customers/{id} returns matching id': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body && body.id === customerId;
      } catch (e) {
        return false;
      }
    },
  });
}

/**
 * Negative scenario: get non-existent customer.
 */
export function getNonExistentCustomerFlow(apiClient) {
  const fakeId = `missing-${randomUuid()}`;
  const response = apiClient.get(`/customers/${fakeId}`, { name: 'GET /customers/{id} [404]' });

  return check(response, {
    'GET /customers/{missing-id} status is 404': (r) => r.status === 404,
  });
}
