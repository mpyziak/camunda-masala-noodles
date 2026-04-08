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
public class OrderOnline implements Tasklet {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Cooking is no child's play. Let's order online
     *
     * @param contribution : step contribution
     * @param chunkContext : chunk context providing access to job parameters and execution context
     * @return RepeatStatus.FINISHED
     */
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {

        WorkflowLogger.info(logger, "Order Online", "Veg masala noodles was no success.. Let's order online...");

        ExecutionContext jobContext = chunkContext.getStepContext().getStepExecution()
                .getJobExecution().getExecutionContext();
        jobContext.put(Constants.DID_WE_EAT_NOODLES, false);

        WorkflowLogger.info(logger, "Order Online", "Ordering is not part of this flow yet... Try your local apps...");
        jobContext.put(Constants.ORDER_ONLINE, true);

        return RepeatStatus.FINISHED;
    }
}
