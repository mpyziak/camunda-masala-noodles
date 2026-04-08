package com.noodles.controller;


import com.noodles.util.Constants;
import com.noodles.util.WorkflowLogger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @implSpec : Controller to start cooking of veg masala noodles via Spring Batch Job
 */
@RestController
public class NoodlesController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final JobLauncher jobLauncher;
    private final Job cookNoodlesJob;

    @Autowired
    public NoodlesController(@Qualifier("asyncJobLauncher") JobLauncher jobLauncher, Job cookNoodlesJob) {
        this.jobLauncher = jobLauncher;
        this.cookNoodlesJob = cookNoodlesJob;
    }

    @PostMapping("/noodles/cook")
    @Operation(summary = "cook instant noodles", tags = {"noodles"})
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Job started", content = {@Content(schema = @Schema(hidden = true))}),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Unexpected Error", content = {@Content(schema = @Schema(hidden = true))})})
    public ResponseEntity<String> cookNoodles(
            @RequestParam(defaultValue = "false") boolean noodles,
            @RequestParam(defaultValue = "false") boolean water,
            @RequestParam(name = "pan_and_spatula", defaultValue = "false") boolean panAndSpatula,
            @RequestParam(defaultValue = "false") boolean onion,
            @RequestParam(defaultValue = "false") boolean tomato,
            @RequestParam(defaultValue = "false") boolean cheese,
            @RequestParam(defaultValue = "false") boolean carrot,
            @RequestParam(defaultValue = "false") boolean capsicum
    ) {

        WorkflowLogger.info(logger, "Cook Noodles", "Starting cook noodles batch job");

        try {
            JobParameters params = new JobParametersBuilder()
                    .addString(Constants.NOODLES, String.valueOf(noodles))
                    .addString(Constants.WATER, String.valueOf(water))
                    .addString(Constants.PAN_SPATULA, String.valueOf(panAndSpatula))
                    .addString(Constants.ONION, String.valueOf(onion))
                    .addString(Constants.TOMATO, String.valueOf(tomato))
                    .addString(Constants.CHEESE, String.valueOf(cheese))
                    .addString(Constants.CARROT, String.valueOf(carrot))
                    .addString(Constants.CAPSICUM, String.valueOf(capsicum))
                    .addString("runId", UUID.randomUUID().toString())
                    .toJobParameters();

            JobExecution execution = jobLauncher.run(cookNoodlesJob, params);

            return ResponseEntity.ok("Job id: " + execution.getId()
                    + ", Status: " + execution.getStatus()
                    + ", ExitStatus: " + execution.getExitStatus().getExitCode());
        } catch (Exception e) {
            WorkflowLogger.error(logger, "Cook Noodles", "Unknown Exception", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to launch job. Message: " + e.getMessage());
        }

    }

}
