/**
 * Test data generator for k6 performance tests.
 * Generates unique and deterministic customer and order payloads.
 */

const FIRST_NAMES = ['Alice', 'Bob', 'Carlos', 'Diana', 'Eduardo', 'Fernanda', 'Gabriel', 'Helena', 'Igor', 'Julia'];
const LAST_NAMES = ['Silva', 'Santos', 'Oliveira', 'Souza', 'Pereira', 'Lima', 'Ferreira', 'Costa', 'Rodrigues', 'Almeida'];

export function randomElement(array) {
  return array[Math.floor(Math.random() * array.length)];
}

export function randomUuid() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
    const r = (Math.random() * 16) | 0;
    const v = c === 'x' ? r : (r & 0x3) | 0x8;
    return v.toString(16);
  });
}

export function randomCustomer(vuId = 0, iter = 0) {
  const firstName = randomElement(FIRST_NAMES);
  const lastName = randomElement(LAST_NAMES);
  const uniqueSuffix = `${vuId}_${iter}_${Date.now()}_${Math.floor(Math.random() * 1000)}`;

  return {
    name: `${firstName} ${lastName}`,
    email: `perf_${firstName.toLowerCase()}.${lastName.toLowerCase()}.${uniqueSuffix}@example.com`,
  };
}

export function randomOrder(customerId, minAmount = 25.50, maxAmount = 1500.00) {
  const randomAmount = (Math.random() * (maxAmount - minAmount) + minAmount).toFixed(2);
  return {
    customerId: customerId || randomUuid(),
    amount: parseFloat(randomAmount),
  };
}
