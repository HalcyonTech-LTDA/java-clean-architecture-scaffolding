import { sleep } from 'k6';
import { getConfig } from '../config/environments.js';
import { ApiClient } from '../helpers/http-client.js';
import { createCustomerFlow, getCustomerByIdFlow } from '../scenarios/customer-flows.js';
import { createOrderFlow, getOrderByIdFlow } from '../scenarios/order-flows.js';

export const options = {
  stages: [
    { duration: '30s', target: 10 },   // Warm-up to normal load
    { duration: '30s', target: 50 },   // Scale past standard peak
    { duration: '1m', target: 50 },    // Sustain higher load
    { duration: '30s', target: 100 },  // Push to stress threshold
    { duration: '1m', target: 100 },   // Sustain high stress
    { duration: '30s', target: 150 },  // Extreme stress (find breaking point)
    { duration: '1m', target: 150 },   // Sustain extreme stress
    { duration: '45s', target: 0 },    // Cooldown / recovery
  ],
  thresholds: {
    http_req_failed: ['rate<0.05'],    // Under stress, allow max 5% error rate
    http_req_duration: ['p(95)<1500'], // 95% of requests below 1.5s under stress
  },
};

const config = getConfig();
const client = new ApiClient(config);

export default function () {
  // Heavy interaction loop
  const customer = createCustomerFlow(client);

  if (customer && customer.id) {
    getCustomerByIdFlow(client, customer.id);

    const order = createOrderFlow(client, customer.id);
    if (order && order.id) {
      getOrderByIdFlow(client, order.id);
    }
  }

  // Short pacing to keep pressure on thread and connection pools
  sleep(0.5);
}
