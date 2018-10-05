package me.daisyliao.processes.web.rest;

import com.codahale.metrics.annotation.Timed;
import me.daisyliao.processes.domain.Artifact;
import me.daisyliao.processes.domain.Process;
import me.daisyliao.processes.domain.ServiceModel;
import me.daisyliao.processes.service.ArtifactService;
import me.daisyliao.processes.service.ProcessService;
import me.daisyliao.processes.web.rest.errors.BadRequestAlertException;
import me.daisyliao.processes.web.rest.util.HeaderUtil;
import me.daisyliao.processes.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Process.
 */
@RestController
@RequestMapping("/api")
public class ProcessResource {

    private final Logger log = LoggerFactory.getLogger(ProcessResource.class);

    private static final String ENTITY_NAME = "process";

    //private final ArtifactService artifactService;

    private final ProcessService processService;

    public ProcessResource(ProcessService processService) {
        this.processService = processService;
    }


    @RequestMapping(value = "/process-models/{id}/processes",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Process>> getAllProcesssOfModel(
        @PathVariable(value = "id") String processModelId, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Processes");

        Page<Process> page = processService.findInstances(processModelId, pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/processes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/process-models/{id}/processes",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Process> createProcessFromModel(
        @PathVariable(value = "id") String processModelId,
        @RequestBody Process process) throws URISyntaxException {

        log.debug("REST request to save Process : {}", process);
        if (process.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("process", "idexists", "A new process cannot already have an ID")).body(null);
        }

        Process result = processService.createProcessInstance(processModelId, process);


        return ResponseEntity.created(new URI("/api/processes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("process", result.getId().toString()))
            .body(result);
    }

    @RequestMapping(
        value = "/processes/{processId}/services/{service}",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Timed
    public ResponseEntity<Process> invokeService(
        @PathVariable(value = "processId") String processId,
        @PathVariable(value = "service") String service,
        @RequestBody Artifact artifact) throws Exception {
        log.debug("REST request to invoke a service {} of process {}", service, processId);

        //why need to save posted artifact first while the original artirest dosen't?
        //otherwise there will be bug 5
        //artifactService.save(artifact);

        Process process = processService.findOne(processId);

        if (process == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Process result = processService.invokeService(service, process, artifact);
        processService.afterInvokingService(result);
        processService.cacheSave(process);


        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * POST  /processes : Create a new process.
     *
     * @param process the process to create
     * @return the ResponseEntity with status 201 (Created) and with body the new process, or with status 400 (Bad Request) if the process has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/processes")
    @Timed
    public ResponseEntity<Process> createProcess(@RequestBody Process process) throws URISyntaxException {
        log.debug("REST request to save Process : {}", process);
        if (process.getId() != null) {
            throw new BadRequestAlertException("A new process cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Process result = processService.save(process);
        return ResponseEntity.created(new URI("/api/processes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /processes : Updates an existing process.
     *
     * @param process the process to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated process,
     * or with status 400 (Bad Request) if the process is not valid,
     * or with status 500 (Internal Server Error) if the process couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/processes")
    @Timed
    public ResponseEntity<Process> updateProcess(@RequestBody Process process) throws URISyntaxException {
        log.debug("REST request to update Process : {}", process);
        if (process.getId() == null) {
            return createProcess(process);
        }
        Process result = processService.save(process);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, process.getId().toString()))
            .body(result);
    }

    /**
     * GET  /processes : get all the processes.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of processes in body
     */
    @GetMapping("/processes")
    @Timed
    public ResponseEntity<List<Process>> getAllProcesses(Pageable pageable) {
        log.debug("REST request to get a page of Processes");
        Page<Process> page = processService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/processes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /processes/:id : get the "id" process.
     *
     * @param id the id of the process to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the process, or with status 404 (Not Found)
     */
    @GetMapping("/processes/{id}")
    @Timed
    public ResponseEntity<Process> getProcess(@PathVariable String id) {
        log.debug("REST request to get Process : {}", id);
        Process process = processService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(process));
    }

    /**
     * DELETE  /processes/:id : delete the "id" process.
     *
     * @param id the id of the process to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/processes/{id}")
    @Timed
    public ResponseEntity<Void> deleteProcess(@PathVariable String id) {
        log.debug("REST request to delete Process : {}", id);
        processService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id)).build();
    }
}
