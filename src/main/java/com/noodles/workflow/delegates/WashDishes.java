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
public class WashDishes implements Tasklet {

    private static final String WASH_DISHES = "Wash Dishes";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Time to clean up! Wash all the dishes used during cooking or eating.
     *
     * @param contribution : step contribution
     * @param chunkContext : chunk context providing access to job parameters and execution context
     * @return RepeatStatus.FINISHED
     */
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {

        WorkflowLogger.info(logger, WASH_DISHES, "Time to wash the dishes...");
        WorkflowLogger.info(logger, WASH_DISHES, "Collect all the used pans, spatulas, bowls and plates.");
        WorkflowLogger.info(logger, WASH_DISHES, "Rinse, scrub with soap, and rinse again.");
        WorkflowLogger.info(logger, WASH_DISHES, "Place them on the drying rack. All clean!");

        ExecutionContext jobContext = chunkContext.getStepContext().getStepExecution()
                .getJobExecution().getExecutionContext();
        jobContext.put(Constants.DISHES_WASHED, true);

        return RepeatStatus.FINISHED;
    }
}

