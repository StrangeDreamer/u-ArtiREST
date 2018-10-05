package me.daisyliao.artifactmodel.web.rest;

import com.codahale.metrics.annotation.Timed;
import me.daisyliao.artifactmodel.domain.ArtifactModel;
import me.daisyliao.artifactmodel.service.ArtifactModelService;
import me.daisyliao.artifactmodel.web.rest.errors.BadRequestAlertException;
import me.daisyliao.artifactmodel.web.rest.util.HeaderUtil;
import me.daisyliao.artifactmodel.web.rest.util.PaginationUtil;
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
 * REST controller for managing ArtifactModel.
 */
@RestController
@RequestMapping("/api")
public class ArtifactModelResource {

    private final Logger log = LoggerFactory.getLogger(ArtifactModelResource.class);

    private static final String ENTITY_NAME = "artifactModel";

    private final ArtifactModelService artifactModelService;

    public ArtifactModelResource(ArtifactModelService artifactModelService) {
        this.artifactModelService = artifactModelService;
    }

    /**
     * POST  /artifact-models : Create a new artifactModel.
     *
     * @param artifactModel the artifactModel to create
     * @return the ResponseEntity with status 201 (Created) and with body the new artifactModel, or with status 400 (Bad Request) if the artifactModel has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/artifact-models")
    @Timed
    public ResponseEntity<ArtifactModel> createArtifactModel(@RequestBody ArtifactModel artifactModel) throws URISyntaxException {
        log.debug("REST request to save ArtifactModel : {}", artifactModel);
        if (artifactModel.getId() != null) {
            throw new BadRequestAlertException("A new artifactModel cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ArtifactModel result = artifactModelService.save(artifactModel);
        return ResponseEntity.created(new URI("/api/artifact-models/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /artifact-models : Updates an existing artifactModel.
     *
     * @param artifactModel the artifactModel to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated artifactModel,
     * or with status 400 (Bad Request) if the artifactModel is not valid,
     * or with status 500 (Internal Server Error) if the artifactModel couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/artifact-models")
    @Timed
    public ResponseEntity<ArtifactModel> updateArtifactModel(@RequestBody ArtifactModel artifactModel) throws URISyntaxException {
        log.debug("REST request to update ArtifactModel : {}", artifactModel);
        if (artifactModel.getId() == null) {
            return createArtifactModel(artifactModel);
        }
        ArtifactModel result = artifactModelService.save(artifactModel);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, artifactModel.getId().toString()))
            .body(result);
    }

    /**
     * GET  /artifact-models : get all the artifactModels.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of artifactModels in body
     */
    @GetMapping("/artifact-models")
    @Timed
    public ResponseEntity<List<ArtifactModel>> getAllArtifactModels(Pageable pageable) {
        log.debug("REST request to get a page of ArtifactModels");
        Page<ArtifactModel> page = artifactModelService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/artifact-models");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /artifact-models/:id : get the "id" artifactModel.
     *
     * @param id the id of the artifactModel to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the artifactModel, or with status 404 (Not Found)
     */
    @GetMapping("/artifact-models/{id}")
    @Timed
    public ResponseEntity<ArtifactModel> getArtifactModel(@PathVariable(value = "id") String id) {
        log.debug("REST request to get ArtifactModel : {}", id);
        ArtifactModel artifactModel = artifactModelService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(artifactModel));
    }

    /**
     * DELETE  /artifact-models/:id : delete the "id" artifactModel.
     *
     * @param id the id of the artifactModel to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/artifact-models/{id}")
    @Timed
    public ResponseEntity<Void> deleteArtifactModel(@PathVariable(value = "id") String id) {
        log.debug("REST request to delete ArtifactModel : {}", id);
        artifactModelService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id)).build();
    }
}
