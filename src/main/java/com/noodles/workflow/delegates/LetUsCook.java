package com.noodles.workflow.delegates;

import com.noodles.util.Constants;
import com.noodles.util.WorkflowLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.util.StringJoiner;

@Component
public class LetUsCook implements Tasklet {

    public static final String STEP = "STEP ";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * This will be used to fetch ingredients and begin to cook following the recipe
     *
     * @param contribution : step contribution
     * @param chunkContext : chunk context providing access to job parameters and execution context
     * @return RepeatStatus.FINISHED
     */
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {

        WorkflowLogger.info(logger, "Prepare Noodles", "Follow below to make veg masala noodles");

        WorkflowLogger.info(logger, STEP + 1, "Take a deep-bottomed pan over medium flame and add water in it and bring it to a boil.");

        StringJoiner vegetables = new StringJoiner(", ");
        if (getBooleanParam(chunkContext, Constants.ONION))
            vegetables.add(Constants.ONION);
        if (getBooleanParam(chunkContext, Constants.TOMATO))
            vegetables.add(Constants.TOMATO);
        if (getBooleanParam(chunkContext, Constants.CARROT))
            vegetables.add(Constants.CARROT);
        if (getBooleanParam(chunkContext, Constants.CAPSICUM))
            vegetables.add(Constants.CAPSICUM);

        if (vegetables.length() < 4)
            WorkflowLogger.info(logger, STEP + 2, "While the water boils, take a chopping board and chop " + vegetables);
        else
            WorkflowLogger.info(logger, STEP + 2, "While the water starts to boil, check if you received IMs on your mobile.");

        WorkflowLogger.info(logger, STEP + 3, "Once the water boils, add chopped vegetables, add 1 packet of instant noodles and stir it.");

        WorkflowLogger.info(logger, STEP + 4, "Add the taste-maker to it and give it another stir");

        if (getBooleanParam(chunkContext, Constants.CHEESE))
            WorkflowLogger.info(logger, STEP + 5, "Add grated cheese and close the lid");
        else
            WorkflowLogger.info(logger, STEP + 5, "Close the lid");

        WorkflowLogger.info(logger, "Cooking in Process", "You can play with your mobile as it cooks for sometime...");

        ExecutionContext jobContext = chunkContext.getStepContext().getStepExecution()
                .getJobExecution().getExecutionContext();
        jobContext.put(Constants.IS_IT_COOKING, true);

        return RepeatStatus.FINISHED;
    }

    private boolean getBooleanParam(ChunkContext chunkContext, String paramName) {
        String value = chunkContext.getStepContext().getStepExecution()
                .getJobParameters().getString(paramName, "false");
        return Boolean.parseBoolean(value);
    }
}
