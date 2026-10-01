package com.example.templatejava.customer.infrastructure.job;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.templatejava.customer.application.usecase.SyncCustomersUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CustomerSyncJobTest {

    @Test
    @DisplayName("should execute scheduled customer synchronization routine without errors")
    void shouldExecuteSyncRoutineSuccessfully() {
        FakeSyncCustomersUseCase useCase = new FakeSyncCustomersUseCase(5);
        CustomerSyncJob job = new CustomerSyncJob(useCase);

        job.syncCustomers();

        assertThat(useCase.invocations).isEqualTo(1);
    }

    @Test
    @DisplayName("should throw NullPointerException when useCase is null")
    void shouldThrowWhenUseCaseIsNull() {
        assertThatThrownBy(() -> new CustomerSyncJob(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("syncCustomersUseCase must not be null");
    }

    private static class FakeSyncCustomersUseCase implements SyncCustomersUseCase {
        private final int countToReturn;
        private int invocations = 0;

        FakeSyncCustomersUseCase(int countToReturn) {
            this.countToReturn = countToReturn;
        }

        @Override
        public int execute() {
            invocations++;
            return countToReturn;
        }
    }
}
