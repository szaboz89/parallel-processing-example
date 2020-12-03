package com.szabodev.distributedworkers.repository;

import com.szabodev.distributedworkers.model.Job;
import com.szabodev.distributedworkers.model.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Long> {

    Optional<Job> findFirstByStatus(JobStatus status);
}
