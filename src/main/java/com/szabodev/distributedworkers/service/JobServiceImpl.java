package com.szabodev.distributedworkers.service;

import com.szabodev.distributedworkers.model.Job;
import com.szabodev.distributedworkers.model.JobStatus;
import com.szabodev.distributedworkers.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;

    private final ExecutorService fixedThreadPool;

    @Override
    public void processAvailableJobs() {
        log.info("Jobs before processing:");
        jobRepository.findAll().forEach(job -> log.info(String.valueOf(job)));

        AtomicBoolean notFoundForProcessing = new AtomicBoolean(false);
        while (!notFoundForProcessing.get()) {
            fixedThreadPool.submit(() -> {
                Optional<Job> nextAvailableJob = getNextAvailableJob();
                if (nextAvailableJob.isPresent()) {
                    callUrlAndStoreStatus(nextAvailableJob.get());
                } else {
                    notFoundForProcessing.set(true);
                }
            });
        }
        awaitTerminationAfterShutdown(fixedThreadPool);

        log.info("Jobs after processing:");
        jobRepository.findAll().forEach(job -> log.info(String.valueOf(job)));
    }

    private synchronized Optional<Job> getNextAvailableJob() {
        Optional<Job> nextAvailableJob = jobRepository.findFirstByStatus(JobStatus.NEW);
        nextAvailableJob.ifPresent(job -> {
            job.setStatus(JobStatus.PROCESSING);
            jobRepository.save(job);
        });
        return nextAvailableJob;
    }

    @SuppressWarnings("ConstantConditions")
    private void callUrlAndStoreStatus(Job job) {
        String currentThreadName = Thread.currentThread().getName();
        log.info("Worker ({}) is processing: {}", currentThreadName, job);
        try {
            WebClient webClient = WebClient.create(job.getUrl());
            ResponseEntity<Void> stringResponseEntity = webClient.get().retrieve().toBodilessEntity().block();
            HttpStatus statusCode = stringResponseEntity.getStatusCode();
            log.info("Worker ({}) is storing status code for {}: {}", currentThreadName, job.getUrl(), statusCode);
            job.setHttpCode(String.valueOf(statusCode.value()));
            job.setStatus(JobStatus.DONE);
        } catch (Exception e) {
            log.error("Worker ({}) found error when processing {}: {}", currentThreadName, job.getUrl(), e.getMessage());
            job.setStatus(JobStatus.ERROR);
        }
        jobRepository.save(job);
    }

    private void awaitTerminationAfterShutdown(ExecutorService threadPool) {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(1, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
