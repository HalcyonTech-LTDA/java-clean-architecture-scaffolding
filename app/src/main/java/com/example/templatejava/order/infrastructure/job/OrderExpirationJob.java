package com.example.templatejava.order.infrastructure.job;

import com.example.templatejava.order.application.usecase.ExpireOrdersUseCase;
import java.util.Objects;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("order-expiration-job")
public class OrderExpirationJob {

    private static final Logger log = LoggerFactory.getLogger(OrderExpirationJob.class);

    private final ExpireOrdersUseCase expireOrdersUseCase;

    public OrderExpirationJob(ExpireOrdersUseCase expireOrdersUseCase) {
        this.expireOrdersUseCase =
                Objects.requireNonNull(expireOrdersUseCase, "expireOrdersUseCase must not be null");
    }

    @Scheduled(fixedDelayString = "${app.order.expiration-job.cadence}")
    @SchedulerLock(
            name = "orderExpirationJob",
            lockAtLeastFor = "${app.order.expiration-job.lock-at-least-for}",
            lockAtMostFor = "${app.order.expiration-job.lock-at-most-for}")
    public void expireStaleOrders() {
        log.info("Executing scheduled order expiration routine");
        int expired = expireOrdersUseCase.execute();
        log.info("Order expiration routine completed. Expired {} orders.", expired);
    }
}
