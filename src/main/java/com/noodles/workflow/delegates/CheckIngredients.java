package com.noodles.workflow.delegates;

import com.noodles.util.Constants;
import com.noodles.util.WorkflowLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
public class CheckIngredients implements Tasklet {

    public static final String CHECK_INGREDIENTS = "Check Ingredients";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * This will be used to check if we have all ingredients to cook.
     * Sets ExitStatus.FAILED when mandatory ingredients are missing,
     * so the job flow can branch to the OrderOnline step.
     *
     * @param contribution : step contribution to set exit status
     * @param chunkContext : chunk context providing access to job parameters and execution context
     * @return RepeatStatus.FINISHED
     */
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {

        WorkflowLogger.info(logger, CHECK_INGREDIENTS, "Check ingredients to make veg masala noodles");

        //in all the ingredients,
        //mandatory : instant noodles, water, pan & spatula
        //optional : veggies

        boolean instantNoodles = getBooleanParam(chunkContext, Constants.NOODLES);
        boolean water = getBooleanParam(chunkContext, Constants.WATER);
        boolean panSpatula = getBooleanParam(chunkContext, Constants.PAN_SPATULA);

        if (instantNoodles && water && panSpatula) {
            WorkflowLogger.info(logger, CHECK_INGREDIENTS, "we can make veg masala noodles");
            getJobExecutionContext(chunkContext).put(Constants.INGREDIENTS_AVAILABLE, true);
            contribution.setExitStatus(ExitStatus.COMPLETED);
        } else {
            WorkflowLogger.error(logger, CHECK_INGREDIENTS, "we cannot make veg masala noodles as required ingredient is missing. Instant Noodles, Water, Pan and Spatula are required.");
            getJobExecutionContext(chunkContext).put(Constants.INGREDIENTS_AVAILABLE, false);
            contribution.setExitStatus(ExitStatus.FAILED);
        }

        return RepeatStatus.FINISHED;
    }

    /**
     * @param chunkContext : chunk context to extract job parameters
     * @param paramName    : parameter name to be extracted
     * @return boolean value of the parameter, defaults to false
     */
    private boolean getBooleanParam(ChunkContext chunkContext, String paramName) {
        String value = chunkContext.getStepContext().getStepExecution()
                .getJobParameters().getString(paramName, "false");
        return Boolean.parseBoolean(value);
    }

    private org.springframework.batch.item.ExecutionContext getJobExecutionContext(ChunkContext chunkContext) {
        return chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
    }
}
