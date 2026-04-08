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
 * Unit test for LetUsEat Tasklet
 */
class LetUsEatTest {

    private LetUsEat letUsEat;

    @BeforeEach
    void setUp() {
        letUsEat = new LetUsEat();
    }

    @Test
    void test_With_Cheese() {
        JobParameters params = new JobParametersBuilder()
                .addString(Constants.CHEESE, "true")
                .toJobParameters();

        StepExecution stepExecution = createStepExecution(params);
        StepContribution contribution = new StepContribution(stepExecution);
        ChunkContext chunkContext = new ChunkContext(new StepContext(stepExecution));

        RepeatStatus status = letUsEat.execute(contribution, chunkContext);

        Assertions.assertEquals(RepeatStatus.FINISHED, status);
        Assertions.assertEquals(true, stepExecution.getJobExecution().getExecutionContext().get(Constants.DID_WE_EAT_NOODLES));
    }

    @Test
    void test_Without_Cheese() {
        JobParameters params = new JobParametersBuilder().toJobParameters();

        StepExecution stepExecution = createStepExecution(params);
        StepContribution contribution = new StepContribution(stepExecution);
        ChunkContext chunkContext = new ChunkContext(new StepContext(stepExecution));

        RepeatStatus status = letUsEat.execute(contribution, chunkContext);

        Assertions.assertEquals(RepeatStatus.FINISHED, status);
        Assertions.assertEquals(true, stepExecution.getJobExecution().getExecutionContext().get(Constants.DID_WE_EAT_NOODLES));
    }

    private StepExecution createStepExecution(JobParameters params) {
        JobInstance jobInstance = new JobInstance(1L, "cookNoodlesJob");
        JobExecution jobExecution = new JobExecution(jobInstance, params);
        return new StepExecution("letUsEatStep", jobExecution);
    }
}
