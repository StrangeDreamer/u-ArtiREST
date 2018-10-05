package me.daisyliao.processes.web.rest;

import com.codahale.metrics.annotation.Timed;
import me.daisyliao.processes.domain.Artifact;
import me.daisyliao.processes.service.ArtifactService;
import me.daisyliao.processes.web.rest.errors.BadRequestAlertException;
import me.daisyliao.processes.web.rest.util.HeaderUtil;
import me.daisyliao.processes.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Artifact.
 */
@RestController
@RequestMapping("/api")
public class ArtifactResource {

    private final Logger log = LoggerFactory.getLogger(ArtifactResource.class);

    private static final String ENTITY_NAME = "artifact";

    private final ArtifactService artifactService;

    public ArtifactResource(ArtifactService artifactService) {
        this.artifactService = artifactService;
    }

    /**
     * POST  /artifacts : Create a new artifact.
     *
     * @param artifact the artifact to create
     * @return the ResponseEntity with status 201 (Created) and with body the new artifact, or with status 400 (Bad Request) if the artifact has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/artifacts")
    @Timed
    public ResponseEntity<Artifact> createArtifact(@RequestBody Artifact artifact) throws URISyntaxException {
        log.debug("REST request to save Artifact : {}", artifact);
        if (artifact.getId() != null) {
            throw new BadRequestAlertException("A new artifact cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Artifact result = artifactService.save(artifact);
        return ResponseEntity.created(new URI("/api/artifacts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /artifacts : Updates an existing artifact.
     *
     * @param artifact the artifact to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated artifact,
     * or with status 400 (Bad Request) if the artifact is not valid,
     * or with status 500 (Internal Server Error) if the artifact couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/artifacts")
    @Timed
    public ResponseEntity<Artifact> updateArtifact(@RequestBody Artifact artifact) throws URISyntaxException {
        log.debug("REST request to update Artifact : {}", artifact);
        if (artifact.getId() == null) {
            return createArtifact(artifact);
        }
        Artifact result = artifactService.save(artifact);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, artifact.getId().toString()))
            .body(result);
    }

    /**
     * GET  /artifacts : get all the artifacts.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of artifacts in body
     */
    @GetMapping("/artifacts")
    @Timed
    public ResponseEntity<List<Artifact>> getAllArtifacts(Pageable pageable) {
        log.debug("REST request to get a page of Artifacts");
        Page<Artifact> page = artifactService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/artifacts");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /artifacts/:id : get the "id" artifact.
     *
     * @param id the id of the artifact to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the artifact, or with status 404 (Not Found)
     */
    @GetMapping("/artifacts/{id}")
    @Timed
    public ResponseEntity<Artifact> getArtifact(@PathVariable String id) {
        log.debug("REST request to get Artifact : {}", id);
        Artifact artifact = artifactService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(artifact));
    }

    /**
     * DELETE  /artifacts/:id : delete the "id" artifact.
     *
     * @param id the id of the artifact to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/artifacts/{id}")
    @Timed
    public ResponseEntity<Void> deleteArtifact(@PathVariable String id) {
        log.debug("REST request to delete Artifact : {}", id);
        artifactService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id)).build();
    }
}
