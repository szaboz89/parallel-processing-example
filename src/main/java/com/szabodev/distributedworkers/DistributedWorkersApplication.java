package com.szabodev.distributedworkers;

import com.szabodev.distributedworkers.model.Job;
import com.szabodev.distributedworkers.model.JobStatus;
import com.szabodev.distributedworkers.repository.JobRepository;
import com.szabodev.distributedworkers.service.JobService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class DistributedWorkersApplication {

    public static void main(String[] args) {
        SpringApplication.run(DistributedWorkersApplication.class, args);
    }

    @Profile("!test")
    @Bean
    public ApplicationRunner applicationRunner(JobRepository jobRepository, JobService jobService) {
        return args -> {
            jobRepository.save(Job.builder().url("https://proxify.io").status(JobStatus.NEW).build());
            jobRepository.save(Job.builder().url("https://reddit.com").status(JobStatus.NEW).build());
            jobRepository.save(Job.builder().url("https://github.com").status(JobStatus.NEW).build());
            jobRepository.save(Job.builder().url("http://szabodev.com").status(JobStatus.NEW).build());
            jobRepository.save(Job.builder().url("http://cant-reach.com").status(JobStatus.NEW).build());

            jobService.processAvailableJobs();
        };
    }

    @Bean
    public ExecutorService fixedThreadPool(@Value("${number.of.workers}") String numberOfWorkers) {
        return Executors.newFixedThreadPool(Integer.parseInt(numberOfWorkers));
    }
}
