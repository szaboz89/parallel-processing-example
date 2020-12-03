package com.szabodev.distributedworkers.model;

import lombok.*;

import javax.persistence.*;

@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "url")
    private String url;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private JobStatus status;

    @Column(name = "http_code")
    private String httpCode;

    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", status=" + status +
                ", httpCode='" + httpCode + '\'' +
                '}';
    }
}
