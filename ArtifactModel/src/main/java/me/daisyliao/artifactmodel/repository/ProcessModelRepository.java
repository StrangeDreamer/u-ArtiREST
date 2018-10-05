package me.daisyliao.artifactmodel.repository;

import me.daisyliao.artifactmodel.domain.ProcessModel;
import org.springframework.stereotype.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the ProcessModel entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProcessModelRepository extends MongoRepository<ProcessModel, String> {

}
