package com.noodles;

import com.noodles.util.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Integration test for the full cook noodles batch job flow.
 */
@SpringBatchTest
@SpringBootTest
class NoodlesFlowTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    void testCompleteTask() throws Exception {

        JobParameters params = new JobParametersBuilder()
                .addString(Constants.NOODLES, "true")
                .addString(Constants.WATER, "true")
                .addString(Constants.PAN_SPATULA, "true")
                .addString(Constants.ONION, "true")
                .addString(Constants.TOMATO, "true")
                .addString(Constants.CHEESE, "true")
                .addString(Constants.CARROT, "true")
                .addString(Constants.CAPSICUM, "true")
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        JobExecution execution = jobLauncherTestUtils.launchJob(params);

        // job should complete successfully via the happy path:
        // checkIngredients(COMPLETED) -> letUsCook -> letUsEat
        Assertions.assertEquals(BatchStatus.COMPLETED, execution.getStatus());
        Assertions.assertEquals(true, execution.getExecutionContext().get(Constants.DID_WE_EAT_NOODLES));
        Assertions.assertEquals(true, execution.getExecutionContext().get(Constants.IS_IT_COOKING));
    }

    @Test
    void testMissingIngredientsTask() throws Exception {

        // missing noodles -> checkIngredients returns FAILED -> orderOnline
        JobParameters params = new JobParametersBuilder()
                .addString(Constants.WATER, "true")
                .addString(Constants.PAN_SPATULA, "true")
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        JobExecution execution = jobLauncherTestUtils.launchJob(params);

        // job should complete via the fallback path:
        // checkIngredients(FAILED) -> orderOnline
        Assertions.assertEquals(BatchStatus.COMPLETED, execution.getStatus());
        Assertions.assertEquals(false, execution.getExecutionContext().get(Constants.DID_WE_EAT_NOODLES));
        Assertions.assertEquals(true, execution.getExecutionContext().get(Constants.ORDER_ONLINE));
    }

}
