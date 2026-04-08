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

@Component
public class LetUsEat implements Tasklet {

    public static final String EAT_NOODLES = "Eat Noodles";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * We will eat what we cooked if it was not burnt
     *
     * @param contribution : step contribution
     * @param chunkContext : chunk context providing access to job parameters and execution context
     * @return RepeatStatus.FINISHED
     */
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {

        WorkflowLogger.info(logger, EAT_NOODLES, "Veg masala noodles is ready. Let's eat... But first serve it..");

        WorkflowLogger.info(logger, EAT_NOODLES, "Transfer to a serving bowl and sprinkle a pinch of chaat masala or oregano over the noodles to make it even more flavorful.");

        boolean cheese = getBooleanParam(chunkContext, Constants.CHEESE);
        if (cheese)
            WorkflowLogger.info(logger, EAT_NOODLES, "Add grated cheese over it. ");

        WorkflowLogger.info(logger, EAT_NOODLES, "Serve it hot to enjoy!! ");

        ExecutionContext jobContext = chunkContext.getStepContext().getStepExecution()
                .getJobExecution().getExecutionContext();
        jobContext.put(Constants.DID_WE_EAT_NOODLES, true);

        return RepeatStatus.FINISHED;
    }

    private boolean getBooleanParam(ChunkContext chunkContext, String paramName) {
        String value = chunkContext.getStepContext().getStepExecution()
                .getJobParameters().getString(paramName, "false");
        return Boolean.parseBoolean(value);
    }
}
