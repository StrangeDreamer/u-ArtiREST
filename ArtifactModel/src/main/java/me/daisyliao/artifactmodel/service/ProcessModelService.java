package me.daisyliao.artifactmodel.service;

import me.daisyliao.artifactmodel.domain.ProcessModel;
import me.daisyliao.artifactmodel.repository.ProcessModelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


/**
 * Service Implementation for managing ProcessModel.
 */
@Service
public class ProcessModelService {

    private final Logger log = LoggerFactory.getLogger(ProcessModelService.class);

    private final ProcessModelRepository processModelRepository;

    public ProcessModelService(ProcessModelRepository processModelRepository) {
        this.processModelRepository = processModelRepository;
    }

    /**
     * Save a processModel.
     *
     * @param processModel the entity to save
     * @return the persisted entity
     */
    public ProcessModel save(ProcessModel processModel) {
        log.debug("Request to save ProcessModel : {}", processModel);
        return processModelRepository.save(processModel);
    }

    /**
     * Get all the processModels.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    public Page<ProcessModel> findAll(Pageable pageable) {
        log.debug("Request to get all ProcessModels");
        return processModelRepository.findAll(pageable);
    }

    /**
     * Get one processModel by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    public ProcessModel findOne(String id) {
        log.debug("Request to get ProcessModel : {}", id);
        return processModelRepository.findOne(id);
    }

    /**
     * Delete the processModel by id.
     *
     * @param id the id of the entity
     */
    public void delete(String id) {
        log.debug("Request to delete ProcessModel : {}", id);
        processModelRepository.delete(id);
    }
}
