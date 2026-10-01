import { sleep } from 'k6';
import { getConfig } from '../config/environments.js';
import { ApiClient } from '../helpers/http-client.js';
import { createCustomerFlow, getCustomerByIdFlow } from '../scenarios/customer-flows.js';
import { createOrderFlow, getOrderByIdFlow } from '../scenarios/order-flows.js';

export const options = {
  stages: [
    { duration: '30s', target: 10 },  // Ramp-up to 10 VUs
    { duration: '1m', target: 10 },   // Sustain 10 VUs (baseline traffic)
    { duration: '30s', target: 30 },  // Ramp-up to 30 VUs (expected peak)
    { duration: '2m', target: 30 },   // Sustain peak traffic
    { duration: '30s', target: 0 },   // Graceful ramp-down
  ],
  thresholds: {
    http_req_failed: ['rate<0.01'],    // Error rate must remain under 1%
    http_req_duration: ['p(90)<250', 'p(95)<400', 'p(99)<800'], // Latency SLAs
  },
};

const config = getConfig();
const client = new ApiClient(config);

export default function () {
  // Realistic user journey with think time
  const customer = createCustomerFlow(client);

  if (customer && customer.id) {
    sleep(0.5);
    getCustomerByIdFlow(client, customer.id);

    // 50% probability of placing an order immediately after registration
    if (Math.random() > 0.5) {
      sleep(1);
      const order = createOrderFlow(client, customer.id);
      if (order && order.id) {
        sleep(0.5);
        getOrderByIdFlow(client, order.id);
      }
    }
  }

  // Random pacing / think time between 1 and 3 seconds
  sleep(Math.random() * 2 + 1);
}
