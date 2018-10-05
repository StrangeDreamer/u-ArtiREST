package me.daisyliao.processes.service;

import me.daisyliao.processes.client.ArtifactModelClient;
import me.daisyliao.processes.domain.*;
import me.daisyliao.processes.repository.ArtifactRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Service Implementation for managing Artifact.
 */
@Service
public class ArtifactService {

    private final Logger log = LoggerFactory.getLogger(ArtifactService.class);

    private final ArtifactRepository artifactRepository;

    @Inject
    private ArtifactModelClient artifactModelClient;

    public ArtifactService(ArtifactRepository artifactRepository, ArtifactModelClient artifactModelClient) {
        this.artifactRepository = artifactRepository;
        this.artifactModelClient = artifactModelClient;
    }

    /**
     * Save a artifact.
     *
     * @param artifact the entity to save
     * @return the persisted entity
     */
    public Artifact save(Artifact artifact) {
        log.debug("Request to save Artifact : {}", artifact);
        return artifactRepository.save(artifact);
    }

    /**
     * Get all the artifacts.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    public Page<Artifact> findAll(Pageable pageable) {
        log.debug("Request to get all Artifacts");
        return artifactRepository.findAll(pageable);
    }

    /**
     * Get one artifact by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    public Artifact findOne(String id) {
        log.debug("Request to get Artifact : {}", id);
        return artifactRepository.findOne(id);
    }

    /**
     * Delete the artifact by id.
     *
     * @param id the id of the entity
     */
    public void delete(String id) {
        log.debug("Request to delete Artifact : {}", id);
        artifactRepository.delete(id);
    }

    //used in both service and process app
    public StateModel findState(String name, String artifactName, ProcessModel processModel) {
        ArtifactModel artifactModel = null;
        for (String artifactModelId : processModel.artifactModelIds) {
            ArtifactModel a = artifactModelClient.getArtifactModelFromArtifactModelService(artifactModelId);
            log.debug(artifactName);
            log.debug(a.getName());
            if (a.getName().equals(artifactName)) {
                log.debug(a.states.toString());
                artifactModel = a;
                break;
            }
        }

        if (artifactModel == null) {
            log.debug("null artifactModel");
            return null;
        }

        log.debug(name);
        for (StateModel state : artifactModel.states) {
            if (state.name.equals(name)) {
                log.debug("state.name.equals(transition.fromState)");
                return state;
            }
            else{
                log.debug("state name not match");
            }
        }

        return null;
    }
}
