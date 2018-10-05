package me.daisyliao.processes.repository;

import me.daisyliao.processes.domain.Artifact;
import org.springframework.stereotype.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the Artifact entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ArtifactRepository extends MongoRepository<Artifact, String> {

}
