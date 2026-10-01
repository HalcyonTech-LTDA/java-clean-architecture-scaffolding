package com.example.templatejava.customer.infrastructure.job;

import com.example.templatejava.customer.application.usecase.SyncCustomersUseCase;
import java.util.Objects;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("customer-sync-job")
public class CustomerSyncJob {

    private static final Logger log = LoggerFactory.getLogger(CustomerSyncJob.class);

    private final SyncCustomersUseCase syncCustomersUseCase;

    public CustomerSyncJob(SyncCustomersUseCase syncCustomersUseCase) {
        this.syncCustomersUseCase =
                Objects.requireNonNull(
                        syncCustomersUseCase, "syncCustomersUseCase must not be null");
    }

    @Scheduled(fixedDelayString = "${app.customer.sync-job.cadence}")
    @SchedulerLock(
            name = "customerSyncJob",
            lockAtLeastFor = "${app.customer.sync-job.lock-at-least-for}",
            lockAtMostFor = "${app.customer.sync-job.lock-at-most-for}")
    public void syncCustomers() {
        log.info("Executing scheduled customer synchronization routine");
        int updated = syncCustomersUseCase.execute();
        log.info("Customer synchronization routine completed. Updated {} customers.", updated);
    }
}
