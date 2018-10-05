package me.daisyliao.artifactmodel.web.rest;

import com.codahale.metrics.annotation.Timed;
import me.daisyliao.artifactmodel.domain.ArtifactModel;
import me.daisyliao.artifactmodel.domain.BusinessRuleModel;
import me.daisyliao.artifactmodel.domain.ProcessModel;
import me.daisyliao.artifactmodel.domain.ServiceModel;
import me.daisyliao.artifactmodel.service.ArtifactModelService;
import me.daisyliao.artifactmodel.service.ProcessModelService;
import me.daisyliao.artifactmodel.web.rest.errors.BadRequestAlertException;
import me.daisyliao.artifactmodel.web.rest.util.HeaderUtil;
import me.daisyliao.artifactmodel.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * REST controller for managing ProcessModel.
 */
@RestController
@RequestMapping("/api")
public class ProcessModelResource {

    private final Logger log = LoggerFactory.getLogger(ProcessModelResource.class);

    private static final String ENTITY_NAME = "processModel";

    private final ProcessModelService processModelService;

    @Autowired
    private ArtifactModelService artifactModelService;

    public ProcessModelResource(ProcessModelService processModelService) {
        this.processModelService = processModelService;
    }

    /**
     * POST  /process-models : Create a new processModel.
     *
     * @param processModel the processModel to create
     * @return the ResponseEntity with status 201 (Created) and with body the new processModel, or with status 400 (Bad Request) if the processModel has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/process-models")
    @Timed
    public ResponseEntity<ProcessModel> createProcessModel(@RequestBody ProcessModel processModel) throws URISyntaxException {
        log.debug("REST request to save ProcessModel : {}", processModel);
        if (processModel.getId() != null) {
            throw new BadRequestAlertException("A new processModel cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ArtifactModel artifactModel = new ArtifactModel();
        artifactModel.setName("Artifact");
        artifactModel = artifactModelService.save(artifactModel);
        processModel.artifactModelIds.add(artifactModel.getId());
        ProcessModel result = processModelService.save(processModel);
        return ResponseEntity.created(new URI("/api/process-models/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId()))
            .body(result);
    }

    /**
     * PUT  /process-models : Updates an existing processModel.
     *
     * @param processModel the processModel to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated processModel,
     * or with status 400 (Bad Request) if the processModel is not valid,
     * or with status 500 (Internal Server Error) if the processModel couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/process-models")
    @Timed
    public ResponseEntity<ProcessModel> updateProcessModel(@RequestBody ProcessModel processModel) throws URISyntaxException {
        log.debug("REST request to update ProcessModel : {}", processModel);
        if (processModel.getId() == null) {
            return createProcessModel(processModel);
        }
        ProcessModel result = processModelService.save(processModel);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, processModel.getId()))
            .body(result);
    }

//    @RequestMapping(value = "/process-models/{id}/services",
//        method = RequestMethod.POST,
//        produces = MediaType.APPLICATION_JSON_VALUE)
//    @Transactional
//    public ResponseEntity<ProcessModel> saveProcessServices(@RequestBody Set<ServiceModel> serviceModels, @PathVariable(value = "id") String id) throws URISyntaxException {
//        ProcessModel processModel = processModelService.findOne(id);
//        processModel.services = serviceModels;
//        ProcessModel result = processModelService.save(processModel);
//
//        return ResponseEntity.ok()
//            .headers(HeaderUtil.createEntityUpdateAlert("processModel", processModel.getId()))
//            .body(result);
//    }
//
//    @RequestMapping(value = "/process-models/{id}/businessRules",
//        method = RequestMethod.POST,
//        produces = MediaType.APPLICATION_JSON_VALUE)
//    @Transactional
//    public ResponseEntity<ProcessModel> saveBusinessRules(@RequestBody Set<BusinessRuleModel> businessRuleModels, @PathVariable(value = "id") String id) throws URISyntaxException {
//        ProcessModel processModel = processModelService.findOne(id);
//        processModel.businessRules = businessRuleModels;
//        ProcessModel result = processModelService.save(processModel);
//
//        return ResponseEntity.ok()
//            .headers(HeaderUtil.createEntityUpdateAlert("processModel", processModel.getId()))
//            .body(result);
//    }

    /**
     * GET  /process-models : get all the processModels.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of processModels in body
     */
    @GetMapping("/process-models")
    @Timed
    public ResponseEntity<List<ProcessModel>> getAllProcessModels(Pageable pageable) {
        log.debug("REST request to get a page of ProcessModels");
        Page<ProcessModel> page = processModelService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/process-models");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /process-models/:id : get the "id" processModel.
     *
     * @param id the id of the processModel to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the processModel, or with status 404 (Not Found)
     */
    @GetMapping("/process-models/{id}")
    @Timed
    public ResponseEntity<ProcessModel> getProcessModel(@PathVariable(value = "id") String id) {
        log.debug("REST request to get ProcessModel : {}", id);
        ProcessModel processModel = processModelService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(processModel));
    }

//    @RequestMapping(value = "/processModels/create_test_models",
//        method = RequestMethod.POST,
//        produces = MediaType.APPLICATION_JSON_VALUE)
//    @Timed
//    public ResponseEntity<ProcessModel> createTestModel(@RequestParam(required = false) String model) {
//        log.debug("REST request to create test process model : {}", model);
//
//        ProcessModel processModel = null;
//
//        if (model != null && model.equals("loan")) {
//            processCreateService.createLoanProcessModel();
//        } else {
//            processCreateService.createOrderProcessModel();
//        }
//
//        return Optional.ofNullable(processModel)
//            .map(result -> new ResponseEntity<>(
//                result,
//                HttpStatus.OK))
//            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
//    }

    /**
     * DELETE  /process-models/:id : delete the "id" processModel.
     *
     * @param id the id of the processModel to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/process-models/{id}")
    @Timed
    public ResponseEntity<Void> deleteProcessModel(@PathVariable(value = "id") String id) {
        log.debug("REST request to delete ProcessModel : {}", id);
        processModelService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id)).build();
    }
}
