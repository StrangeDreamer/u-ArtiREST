package me.daisyliao.artifactmodel.service;

import me.daisyliao.artifactmodel.domain.ArtifactModel;
import me.daisyliao.artifactmodel.repository.ArtifactModelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


/**
 * Service Implementation for managing ArtifactModel.
 */
@Service
public class ArtifactModelService {

    private final Logger log = LoggerFactory.getLogger(ArtifactModelService.class);

    private final ArtifactModelRepository artifactModelRepository;

    public ArtifactModelService(ArtifactModelRepository artifactModelRepository) {
        this.artifactModelRepository = artifactModelRepository;
    }

    /**
     * Save a artifactModel.
     *
     * @param artifactModel the entity to save
     * @return the persisted entity
     */
    public ArtifactModel save(ArtifactModel artifactModel) {
        log.debug("Request to save ArtifactModel : {}", artifactModel);
        return artifactModelRepository.save(artifactModel);
    }

    /**
     * Get all the artifactModels.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    public Page<ArtifactModel> findAll(Pageable pageable) {
        log.debug("Request to get all ArtifactModels");
        return artifactModelRepository.findAll(pageable);
    }

    /**
     * Get one artifactModel by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    public ArtifactModel findOne(String id) {
        log.debug("Request to get ArtifactModel : {}", id);
        return artifactModelRepository.findOne(id);
    }

    /**
     * Delete the artifactModel by id.
     *
     * @param id the id of the entity
     */
    public void delete(String id) {
        log.debug("Request to delete ArtifactModel : {}", id);
        artifactModelRepository.delete(id);
    }
}
