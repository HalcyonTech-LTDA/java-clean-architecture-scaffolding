import { check, sleep } from 'k6';
import { getConfig } from '../config/environments.js';
import { ApiClient } from '../helpers/http-client.js';
import { createCustomerFlow, getCustomerByIdFlow, getNonExistentCustomerFlow } from '../scenarios/customer-flows.js';
import { createOrderFlow, getOrderByIdFlow, getNonExistentOrderFlow } from '../scenarios/order-flows.js';

export const options = {
  vus: 2,
  duration: '15s',
  thresholds: {
    http_req_failed: ['rate<0.01'], // Less than 1% failure rate
    http_req_duration: ['p(95)<300'], // 95% of requests should be below 300ms
  },
};

const config = getConfig();
const client = new ApiClient(config);

export default function () {
  // 1. Actuator Health Check
  const healthRes = client.get('/actuator/health', { name: 'GET /actuator/health' });
  check(healthRes, {
    'actuator health is UP': (r) => r.status === 200,
  });

  // 2. Customer Flow
  const customer = createCustomerFlow(client);
  if (customer && customer.id) {
    getCustomerByIdFlow(client, customer.id);
  }
  getNonExistentCustomerFlow(client);

  // 3. Order Flow
  const customerId = customer ? customer.id : null;
  const order = createOrderFlow(client, customerId);
  if (order && order.id) {
    getOrderByIdFlow(client, order.id);
  }
  getNonExistentOrderFlow(client);

  sleep(1);
}
