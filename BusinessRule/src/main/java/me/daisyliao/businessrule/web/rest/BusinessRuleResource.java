package me.daisyliao.businessrule.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import me.daisyliao.businessrule.domain.BusinessRuleModel;
import me.daisyliao.businessrule.domain.ProcessModel;
import me.daisyliao.businessrule.domain.Process;
import me.daisyliao.businessrule.service.BusinessRuleService;
import me.daisyliao.businessrule.web.rest.errors.BadRequestAlertException;
import me.daisyliao.businessrule.web.rest.util.HeaderUtil;
import me.daisyliao.businessrule.web.rest.util.PaginationUtil;
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
import java.util.Map;
import java.util.Optional;

/**
 * REST controller for managing BusinessRule.
 */
@RestController
@RequestMapping("/api")
public class BusinessRuleResource {

    private final Logger log = LoggerFactory.getLogger(BusinessRuleResource.class);

    private static final String ENTITY_NAME = "businessRule";

    private final BusinessRuleService businessRuleService;

    public BusinessRuleResource(BusinessRuleService businessRuleService) {
        this.businessRuleService = businessRuleService;
    }

    @RequestMapping(value = "/business-rules/verify_atom_conditions",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    //真的要加timed吗？
    @Timed
    public ResponseEntity<Boolean> verifyAtomConditions(@RequestParam Map<String, String> map) throws Exception {
        log.debug("verifyAtomConditions");

        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

        Process process = mapper.readValue(map.get("process"), Process.class);
        BusinessRuleModel rule = mapper.readValue(map.get("rule"), BusinessRuleModel.class);
        ProcessModel processModel = mapper.readValue(map.get("processModel"), ProcessModel.class);

        log.debug(process.toString());
        log.debug(rule.preConditions.toString());
        log.debug(processModel.toString());

        Boolean result = businessRuleService.verifyAtomConditions(process, rule, processModel);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * POST  /business-rules : Create a new businessRule.
     *
     * @param businessRuleModel the businessRule to create
     * @return the ResponseEntity with status 201 (Created) and with body the new businessRule, or with status 400 (Bad Request) if the businessRule has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/business-rules")
    @Timed
    public ResponseEntity<BusinessRuleModel> createBusinessRule(@RequestBody BusinessRuleModel businessRuleModel) throws URISyntaxException {
        log.debug("REST request to save BusinessRule : {}", businessRuleModel);
        if (businessRuleModel.getId() != null) {
            throw new BadRequestAlertException("A new businessRule cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BusinessRuleModel result = businessRuleService.save(businessRuleModel);
        return ResponseEntity.created(new URI("/api/business-rules/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /business-rules : Updates an existing businessRule.
     *
     * @param businessRuleModel the businessRule to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated businessRule,
     * or with status 400 (Bad Request) if the businessRule is not valid,
     * or with status 500 (Internal Server Error) if the businessRule couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/business-rules")
    @Timed
    public ResponseEntity<BusinessRuleModel> updateBusinessRule(@RequestBody BusinessRuleModel businessRuleModel) throws URISyntaxException {
        log.debug("REST request to update BusinessRule : {}", businessRuleModel);
        if (businessRuleModel.getId() == null) {
            return createBusinessRule(businessRuleModel);
        }
        BusinessRuleModel result = businessRuleService.save(businessRuleModel);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, businessRuleModel.getId().toString()))
            .body(result);
    }

    /**
     * GET  /business-rules : get all the businessRules.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of businessRules in body
     */
    @GetMapping("/business-rules")
    @Timed
    public ResponseEntity<List<BusinessRuleModel>> getAllBusinessRules(Pageable pageable) {
        log.debug("REST request to get a page of BusinessRules");
        Page<BusinessRuleModel> page = businessRuleService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/business-rules");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /business-rules/:id : get the "id" businessRule.
     *
     * @param id the id of the businessRule to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the businessRule, or with status 404 (Not Found)
     */
    @GetMapping("/business-rules/{id}")
    @Timed
    public ResponseEntity<BusinessRuleModel> getBusinessRule(@PathVariable String id) {
        log.debug("REST request to get BusinessRule : {}", id);
        BusinessRuleModel businessRuleModel = businessRuleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(businessRuleModel));
    }

    /**
     * DELETE  /business-rules/:id : delete the "id" businessRule.
     *
     * @param id the id of the businessRule to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/business-rules/{id}")
    @Timed
    public ResponseEntity<Void> deleteBusinessRule(@PathVariable String id) {
        log.debug("REST request to delete BusinessRule : {}", id);
        businessRuleService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id)).build();
    }

    @GetMapping("/business-rules/class/{businessRuleClass}")
    @Timed
    public ResponseEntity<List<BusinessRuleModel>> getBusinessRuleByClass(@PathVariable String businessRuleClass) {
        log.debug("REST request to get BusinessRule by businessRuleClass : {}", businessRuleClass);
        List<BusinessRuleModel> businessRules = businessRuleService.findByBusinessRuleClass(businessRuleClass);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(businessRules));
    }

}
