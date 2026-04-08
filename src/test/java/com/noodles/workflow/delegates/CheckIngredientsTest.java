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
 * Unit test for CheckIngredients Tasklet
 */
class CheckIngredientsTest {

    private CheckIngredients checkIngredients;

    @BeforeEach
    void setUp() {
        checkIngredients = new CheckIngredients();
    }

    @Test
    void test_I_have_noodles_water_pan_spatula() {
        JobParameters params = new JobParametersBuilder()
                .addString(Constants.NOODLES, "true")
                .addString(Constants.WATER, "true")
                .addString(Constants.PAN_SPATULA, "true")
                .toJobParameters();

        StepExecution stepExecution = createStepExecution(params);
        StepContribution contribution = new StepContribution(stepExecution);
        ChunkContext chunkContext = new ChunkContext(new StepContext(stepExecution));

        RepeatStatus status = checkIngredients.execute(contribution, chunkContext);

        Assertions.assertEquals(RepeatStatus.FINISHED, status);
        Assertions.assertEquals(ExitStatus.COMPLETED, contribution.getExitStatus());
        Assertions.assertEquals(true, stepExecution.getJobExecution().getExecutionContext().get(Constants.INGREDIENTS_AVAILABLE));
    }

    @Test
    void test_I_have_noodles_water_pan_spatula_vegetables_cheese() {
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

        RepeatStatus status = checkIngredients.execute(contribution, chunkContext);

        Assertions.assertEquals(RepeatStatus.FINISHED, status);
        Assertions.assertEquals(ExitStatus.COMPLETED, contribution.getExitStatus());
        Assertions.assertEquals(true, stepExecution.getJobExecution().getExecutionContext().get(Constants.INGREDIENTS_AVAILABLE));
    }

    @Test
    void test_I_do_not_have_noodles() {
        JobParameters params = new JobParametersBuilder()
                .addString(Constants.WATER, "true")
                .addString(Constants.PAN_SPATULA, "true")
                .toJobParameters();

        StepExecution stepExecution = createStepExecution(params);
        StepContribution contribution = new StepContribution(stepExecution);
        ChunkContext chunkContext = new ChunkContext(new StepContext(stepExecution));

        RepeatStatus status = checkIngredients.execute(contribution, chunkContext);

        Assertions.assertEquals(RepeatStatus.FINISHED, status);
        Assertions.assertEquals(ExitStatus.FAILED, contribution.getExitStatus());
        Assertions.assertEquals(false, stepExecution.getJobExecution().getExecutionContext().get(Constants.INGREDIENTS_AVAILABLE));
    }

    @Test
    void test_I_do_not_have_water() {
        JobParameters params = new JobParametersBuilder()
                .addString(Constants.NOODLES, "true")
                .addString(Constants.PAN_SPATULA, "true")
                .toJobParameters();

        StepExecution stepExecution = createStepExecution(params);
        StepContribution contribution = new StepContribution(stepExecution);
        ChunkContext chunkContext = new ChunkContext(new StepContext(stepExecution));

        RepeatStatus status = checkIngredients.execute(contribution, chunkContext);

        Assertions.assertEquals(RepeatStatus.FINISHED, status);
        Assertions.assertEquals(ExitStatus.FAILED, contribution.getExitStatus());
        Assertions.assertEquals(false, stepExecution.getJobExecution().getExecutionContext().get(Constants.INGREDIENTS_AVAILABLE));
    }

    @Test
    void test_I_do_not_have_pan_and_spatula() {
        JobParameters params = new JobParametersBuilder()
                .addString(Constants.NOODLES, "true")
                .addString(Constants.WATER, "true")
                .toJobParameters();

        StepExecution stepExecution = createStepExecution(params);
        StepContribution contribution = new StepContribution(stepExecution);
        ChunkContext chunkContext = new ChunkContext(new StepContext(stepExecution));

        RepeatStatus status = checkIngredients.execute(contribution, chunkContext);

        Assertions.assertEquals(RepeatStatus.FINISHED, status);
        Assertions.assertEquals(ExitStatus.FAILED, contribution.getExitStatus());
        Assertions.assertEquals(false, stepExecution.getJobExecution().getExecutionContext().get(Constants.INGREDIENTS_AVAILABLE));
    }

    private StepExecution createStepExecution(JobParameters params) {
        JobInstance jobInstance = new JobInstance(1L, "cookNoodlesJob");
        JobExecution jobExecution = new JobExecution(jobInstance, params);
        return new StepExecution("checkIngredientsStep", jobExecution);
    }
}

