package com.noodles.configuration;

import com.noodles.workflow.delegates.CheckIngredients;
import com.noodles.workflow.delegates.LetUsCook;
import com.noodles.workflow.delegates.LetUsEat;
import com.noodles.workflow.delegates.OrderOnline;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

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
        return new StepBuilder("orderOnlineStep", jobRepository)
                .tasklet(orderOnline, transactionManager)
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



