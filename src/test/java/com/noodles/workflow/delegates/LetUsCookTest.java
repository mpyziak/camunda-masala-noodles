package com.noodles.workflow.delegates;

import com.noodles.util.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.repeat.RepeatStatus;

import static java.lang.Boolean.TRUE;

/**
 * Unit test for LetUsCook Tasklet
 */
class LetUsCookTest {

    private LetUsCook letUsCook;

    @BeforeEach
    void setUp() {
        letUsCook = new LetUsCook();
    }

    @Test
    void test_I_have_all_ingredients() {
        JobParameters params = new JobParametersBuilder()
                .addString(Constants.NOODLES, "true")
                .addString(Constants.WATER, "true")
                .addString(Constants.PAN_SPATULA, "true")
                .addString(Constants.ONION, "true")
                .addString(Constants.TOMATO, "true")
                .addString(Constants.CHEESE, "true")
                .addString(Constants.CARROT, "true")
                .addString(Constants.CAPSICUM, "true")
                .toJobParameters();

        StepExecution stepExecution = createStepExecution(params);
        StepContribution contribution = new StepContribution(stepExecution);
        ChunkContext chunkContext = new ChunkContext(new StepContext(stepExecution));

        RepeatStatus status = letUsCook.execute(contribution, chunkContext);

        Assertions.assertEquals(RepeatStatus.FINISHED, status);
        Assertions.assertEquals(TRUE, stepExecution.getJobExecution().getExecutionContext().get(Constants.IS_IT_COOKING));
    }

    @Test
    void test_I_have_minimum_ingredients() {
        JobParameters params = new JobParametersBuilder()
                .addString(Constants.NOODLES, "true")
                .addString(Constants.WATER, "true")
                .addString(Constants.PAN_SPATULA, "true")
                .toJobParameters();

        StepExecution stepExecution = createStepExecution(params);
        StepContribution contribution = new StepContribution(stepExecution);
        ChunkContext chunkContext = new ChunkContext(new StepContext(stepExecution));

        RepeatStatus status = letUsCook.execute(contribution, chunkContext);

        Assertions.assertEquals(RepeatStatus.FINISHED, status);
        Assertions.assertEquals(TRUE, stepExecution.getJobExecution().getExecutionContext().get(Constants.IS_IT_COOKING));
    }

    private StepExecution createStepExecution(JobParameters params) {
        JobInstance jobInstance = new JobInstance(1L, "cookNoodlesJob");
        JobExecution jobExecution = new JobExecution(jobInstance, params);
        return new StepExecution("letUsCookStep", jobExecution);
    }
}
