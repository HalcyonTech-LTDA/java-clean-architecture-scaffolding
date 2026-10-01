import { sleep } from 'k6';
import { getConfig } from '../config/environments.js';
import { ApiClient } from '../helpers/http-client.js';
import { createCustomerFlow, getCustomerByIdFlow } from '../scenarios/customer-flows.js';
import { createOrderFlow } from '../scenarios/order-flows.js';

export const options = {
  stages: [
    { duration: '20s', target: 5 },    // Low baseline traffic
    { duration: '10s', target: 120 },  // Fast, aggressive spike!
    { duration: '45s', target: 120 },  // Sustain the surge
    { duration: '10s', target: 5 },    // Sudden drop back to baseline
    { duration: '40s', target: 5 },    // Recovery observation window
    { duration: '15s', target: 0 },    // Full cooldown
  ],
  thresholds: {
    http_req_failed: ['rate<0.08'],    // Error rate under 8% during spike
    http_req_duration: ['p(95)<2000'], // 95% under 2s during spike
  },
};

const config = getConfig();
const client = new ApiClient(config);

export default function () {
  // Concurrent customer creation and order placement burst
  const customer = createCustomerFlow(client);

  if (customer && customer.id) {
    getCustomerByIdFlow(client, customer.id);
    createOrderFlow(client, customer.id);
  }

  // Quick sleep during burst
  sleep(0.3);
}
