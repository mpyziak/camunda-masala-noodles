package com.noodles.configuration;

import com.noodles.util.WorkflowLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduled cleanup of completed Spring Batch job metadata.
 * Removes only jobs that finished with clean COMPLETED status
 * and are older than 24 hours. Processes in pages to avoid
 * loading all job instances into memory at once.
 */
@Component
public class JobCleanupScheduler {

  private static final String JOB_NAME = "cookNoodlesJob";
  private static final String CLEANUP = "Job Cleanup";
  private static final int PAGE_SIZE = 1000;

  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final JobExplorer jobExplorer;
  private final JobRepository jobRepository;

  public JobCleanupScheduler(JobExplorer jobExplorer, JobRepository jobRepository) {
    this.jobExplorer = jobExplorer;
    this.jobRepository = jobRepository;
  }

  /**
   * Runs every hour. Deletes job executions that:
   * - have BatchStatus.COMPLETED
   * - ended more than 24 hours ago
   */
  @Scheduled(cron = "0 0 * * * *")
  public void cleanupCompletedJobs() {
    WorkflowLogger.info(logger, CLEANUP, "Starting cleanup of completed job executions older than 24 hours");

    LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
    int start = 0;
    boolean hasMore = true;

    while (hasMore) {
      List<JobInstance> page = jobExplorer.findJobInstancesByJobName(JOB_NAME, start, PAGE_SIZE);
      hasMore = page.size() == PAGE_SIZE;

      for (JobInstance instance : page) {
        deleteIfCompleted(instance, cutoff);
      }

      start += PAGE_SIZE;
    }

    WorkflowLogger.info(logger, CLEANUP, "Cleanup complete.");
  }

  private void deleteIfCompleted(JobInstance instance, LocalDateTime cutoff) {
    List<JobExecution> executions = jobExplorer.getJobExecutions(instance);
    LocalDateTime endTime;
    for (JobExecution execution : executions) {
      if (execution.getStatus() != BatchStatus.COMPLETED) {
        return;
      }
      endTime = execution.getEndTime();
      if (endTime == null || !endTime.isBefore(cutoff)) {
        return;
      }
    }

    for (JobExecution execution : executions) {
      jobRepository.deleteJobExecution(execution);
    }
    jobRepository.deleteJobInstance(instance);
  }
}
