package com.example.templatejava.order.infrastructure.job;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.templatejava.order.application.usecase.ExpireOrdersUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderExpirationJobTest {

    @Test
    @DisplayName("should execute scheduled order expiration routine without errors")
    void shouldExecuteExpirationRoutineSuccessfully() {
        FakeExpireOrdersUseCase useCase = new FakeExpireOrdersUseCase(3);
        OrderExpirationJob job = new OrderExpirationJob(useCase);

        job.expireStaleOrders();

        assertThat(useCase.invocations).isEqualTo(1);
    }

    @Test
    @DisplayName("should throw NullPointerException when useCase is null")
    void shouldThrowWhenUseCaseIsNull() {
        assertThatThrownBy(() -> new OrderExpirationJob(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("expireOrdersUseCase must not be null");
    }

    private static class FakeExpireOrdersUseCase implements ExpireOrdersUseCase {
        private final int countToReturn;
        private int invocations = 0;

        FakeExpireOrdersUseCase(int countToReturn) {
            this.countToReturn = countToReturn;
        }

        @Override
        public int execute() {
            invocations++;
            return countToReturn;
        }
    }
}
