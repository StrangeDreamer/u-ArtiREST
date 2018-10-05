package me.daisyliao.artifactmodel.repository;

import me.daisyliao.artifactmodel.domain.ArtifactModel;
import org.springframework.stereotype.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the ArtifactModel entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ArtifactModelRepository extends MongoRepository<ArtifactModel, String> {

}
