package com.noodles.configuration;

import com.noodles.workflow.delegates.CheckIngredients;
import com.noodles.workflow.delegates.LetUsCook;
import com.noodles.workflow.delegates.LetUsEat;
import com.noodles.workflow.delegates.OrderOnline;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestClientException;

/**
 * Spring Batch configuration that replaces the Camunda BPMN process definition.
 *
 * <p>Flow:
 * <pre>
 *   checkIngredientsStep --[FAILED]--> orderOnlineStep --> END
 *                        --[  *   ]--> letUsCookStep --[FAILED]--> orderOnlineStep --> END
 *                                                    --[  *   ]--> letUsEatStep --> END
 * </pre>
 */
@Configuration
public class BatchConfig {

    // ── Job Launchers ──────────────────────────────────────────────────────

    /**
     * Async JobLauncher with thread pool for high-throughput production use.
     * The default synchronous jobLauncher is auto-configured by Spring Boot.
     */
    @Bean
    public JobLauncher asyncJobLauncher(JobRepository jobRepository) {
        TaskExecutorJobLauncher launcher = new TaskExecutorJobLauncher();
        launcher.setJobRepository(jobRepository);

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("batch-");
        executor.initialize();

        launcher.setTaskExecutor(executor);
        return launcher;
    }

    // ── Steps ──────────────────────────────────────────────────────────────

    @Bean
    public Step checkIngredientsStep(JobRepository jobRepository,
                                     PlatformTransactionManager transactionManager,
                                     CheckIngredients checkIngredients) {
        return new StepBuilder("checkIngredientsStep", jobRepository)
                .tasklet(checkIngredients, transactionManager)
                .build();
    }

    @Bean
    public Step letUsCookStep(JobRepository jobRepository,
                              PlatformTransactionManager transactionManager,
                              LetUsCook letUsCook) {
        return new StepBuilder("letUsCookStep", jobRepository)
                .tasklet(letUsCook, transactionManager)
                .build();
    }

    @Bean
    public Step letUsEatStep(JobRepository jobRepository,
                             PlatformTransactionManager transactionManager,
                             LetUsEat letUsEat) {
        return new StepBuilder("letUsEatStep", jobRepository)
                .tasklet(letUsEat, transactionManager)
                .build();
    }

    @Bean
    public Step orderOnlineStep(JobRepository jobRepository,
                                PlatformTransactionManager transactionManager,
                                OrderOnline orderOnline) {
        RetryTemplate retryTemplate = RetryTemplate.builder()
                .maxAttempts(3)
                .retryOn(RestClientException.class)
                .exponentialBackoff(1000, 2, 10000)
                .build();

        Tasklet retryingTasklet = (contribution, chunkContext) ->
                retryTemplate.execute(ctx -> orderOnline.execute(contribution, chunkContext));

        return new StepBuilder("orderOnlineStep", jobRepository)
                .tasklet(retryingTasklet, transactionManager)
                .build();
    }

    // ── Job (conditional flow) ─────────────────────────────────────────────

    @Bean
    public Job cookNoodlesJob(JobRepository jobRepository,
                              @Qualifier("checkIngredientsStep") Step checkIngredientsStep,
                              @Qualifier("letUsCookStep") Step letUsCookStep,
                              @Qualifier("letUsEatStep") Step letUsEatStep,
                              @Qualifier("orderOnlineStep") Step orderOnlineStep) {
        return new JobBuilder("cookNoodlesJob", jobRepository)
                .start(checkIngredientsStep).on("FAILED").to(orderOnlineStep)
                .from(checkIngredientsStep).on("*").to(letUsCookStep)
                .from(letUsCookStep).on("FAILED").to(orderOnlineStep)
                .from(letUsCookStep).on("*").to(letUsEatStep)
                .end()
                .build();
    }
}



