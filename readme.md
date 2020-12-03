## Parallel processing
### Task
Process jobs stored in the database with several workers in parallel.
### Solved with
Spring Boot, H2 embedded database, Spring WebFlux, Lombok, ExecutorService
### Running configuration
Number of workers can be configured via ``number.of.workers`` property in ``application.properties`` file.
### Running log
Started DistributedWorkersApplication in 4.234 seconds (JVM running for 5.262)  
Jobs before processing:  
Job{id=1, url='https://proxify.io', status=NEW, httpCode='null'}  
Job{id=2, url='https://reddit.com', status=NEW, httpCode='null'}  
Job{id=3, url='https://github.com', status=NEW, httpCode='null'}  
Job{id=4, url='http://szabodev.com', status=NEW, httpCode='null'}  
Job{id=5, url='http://cant-reach.com', status=NEW, httpCode='null'}  
Worker (pool-1-thread-1) is processing: Job{id=1, url='https://proxify.io', status=PROCESSING, httpCode='null'}  
Worker (pool-1-thread-2) is processing: Job{id=2, url='https://reddit.com', status=PROCESSING, httpCode='null'}  
Worker (pool-1-thread-3) is processing: Job{id=3, url='https://github.com', status=PROCESSING, httpCode='null'}  
Worker (pool-1-thread-2) is storing status code for https://reddit.com: 301 MOVED_PERMANENTLY  
Worker (pool-1-thread-2) is processing: Job{id=4, url='http://szabodev.com', status=PROCESSING, httpCode='null'}  
Worker (pool-1-thread-3) is storing status code for https://github.com: 200 OK  
Worker (pool-1-thread-3) is processing: Job{id=5, url='http://cant-reach.com', status=PROCESSING, httpCode='null'}  
Worker (pool-1-thread-2) is storing status code for http://szabodev.com: 200 OK  
Worker (pool-1-thread-3) found error when processing http://cant-reach.com: Search domain query failed. Original hostname: 'cant-reach.com' failed to resolve 'cant-reach.com.home' after 3 queries  
Worker (pool-1-thread-1) is storing status code for https://proxify.io: 200 OK  
Jobs after processing:  
Job{id=1, url='https://proxify.io', status=DONE, httpCode='200'}  
Job{id=2, url='https://reddit.com', status=DONE, httpCode='301'}  
Job{id=3, url='https://github.com', status=DONE, httpCode='200'}  
Job{id=4, url='http://szabodev.com', status=DONE, httpCode='200'}  
Job{id=5, url='http://cant-reach.com', status=ERROR, httpCode='null'}  
