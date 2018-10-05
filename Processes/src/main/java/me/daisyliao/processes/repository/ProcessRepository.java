package me.daisyliao.processes.repository;

import me.daisyliao.processes.domain.Process;
import me.daisyliao.processes.domain.ProcessModel;
import me.daisyliao.processes.service.ProcessService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the Process entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProcessRepository extends MongoRepository<Process, String> {
    Page<Process> findByProcessModelId(String processModelId, Pageable pageable);

    @Cacheable(value = ProcessService.CACHE_NAME)
    Process findOne(String id);
}
