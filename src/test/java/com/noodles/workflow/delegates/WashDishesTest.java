package com.noodles.workflow.delegates;

import com.noodles.util.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.repeat.RepeatStatus;

/**
 * Unit test for WashDishes Tasklet
 */
class WashDishesTest {

    private WashDishes washDishes;

    @BeforeEach
    void setUp() {
        washDishes = new WashDishes();
    }

    @Test
    void test_wash_dishes() {
        JobParameters params = new JobParametersBuilder().toJobParameters();

        StepExecution stepExecution = createStepExecution(params);
        StepContribution contribution = new StepContribution(stepExecution);
        ChunkContext chunkContext = new ChunkContext(new StepContext(stepExecution));

        RepeatStatus status = washDishes.execute(contribution, chunkContext);

        Assertions.assertEquals(RepeatStatus.FINISHED, status);
        Assertions.assertEquals(true, stepExecution.getJobExecution().getExecutionContext().get(Constants.DISHES_WASHED));
    }

    private StepExecution createStepExecution(JobParameters params) {
        JobInstance jobInstance = new JobInstance(1L, "washDishesJob");
        JobExecution jobExecution = new JobExecution(jobInstance, params);
        return new StepExecution("washDishesStep", jobExecution);
    }
}

