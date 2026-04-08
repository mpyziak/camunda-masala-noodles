package com.noodles.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class WashDishesControllerTest {

    @InjectMocks
    private WashDishesController washDishesController;

    @Mock
    private JobLauncher jobLauncher;

    @Mock
    private Job washDishesJob;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(washDishesController, "jobLauncher", jobLauncher);
        ReflectionTestUtils.setField(washDishesController, "washDishesJob", washDishesJob);
    }

    @Test
    void test_wash_dishes_success() throws Exception {
        JobInstance jobInstance = new JobInstance(1L, "washDishesJob");
        JobExecution jobExecution = new JobExecution(jobInstance, new JobParametersBuilder().toJobParameters());
        jobExecution.setStatus(BatchStatus.COMPLETED);
        jobExecution.setExitStatus(ExitStatus.COMPLETED);

        when(jobLauncher.run(any(Job.class), any(JobParameters.class))).thenReturn(jobExecution);

        ResponseEntity<String> responseEntity = washDishesController.washDishes();

        Assertions.assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        Assertions.assertTrue(responseEntity.getBody().contains("Status: COMPLETED"));
    }

    @Test
    void test_wash_dishes_fails_with_exception() throws Exception {
        when(jobLauncher.run(any(Job.class), any(JobParameters.class)))
                .thenThrow(new RuntimeException("Test exception"));

        ResponseEntity<String> responseEntity = washDishesController.washDishes();

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        Assertions.assertTrue(responseEntity.getBody().contains("Test exception"));
    }
}

