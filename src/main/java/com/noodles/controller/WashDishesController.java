package com.noodles.controller;

import com.noodles.util.WorkflowLogger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Webhook controller that launches the wash-dishes job asynchronously.
 */
@RestController
public class WashDishesController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final JobLauncher jobLauncher;
    private final Job washDishesJob;

    @Autowired
    public WashDishesController(@Qualifier("asyncJobLauncher") JobLauncher jobLauncher,
                                @Qualifier("washDishesJob") Job washDishesJob) {
        this.jobLauncher = jobLauncher;
        this.washDishesJob = washDishesJob;
    }

    @PostMapping("/noodles/wash-dishes")
    @Operation(summary = "wash the dishes after cooking or ordering", tags = {"noodles"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Job accepted", content = {@Content(schema = @Schema(hidden = true))}),
            @ApiResponse(responseCode = "500", description = "Unexpected Error", content = {@Content(schema = @Schema(hidden = true))})})
    public ResponseEntity<String> washDishes() {

        WorkflowLogger.info(logger, "Wash Dishes", "Starting wash dishes batch job");

        try {
            JobParameters params = new JobParametersBuilder()
                    .addString("runId", UUID.randomUUID().toString())
                    .toJobParameters();

            JobExecution execution = jobLauncher.run(washDishesJob, params);

            return ResponseEntity.accepted()
                    .body("Job id: " + execution.getId()
                            + ", Status: " + execution.getStatus()
                            + ", ExitStatus: " + execution.getExitStatus().getExitCode());
        } catch (Exception e) {
            WorkflowLogger.error(logger, "Wash Dishes", "Unknown Exception", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to launch wash-dishes job. Message: " + e.getMessage());
        }
    }
}

