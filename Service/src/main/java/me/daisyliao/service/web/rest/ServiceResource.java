package me.daisyliao.service.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.jhipster.web.util.ResponseUtil;
import me.daisyliao.service.client.ProcessClient;
import me.daisyliao.service.domain.*;
import me.daisyliao.service.domain.Process;

import me.daisyliao.service.service.ServiceService;
import me.daisyliao.service.web.rest.errors.BadRequestAlertException;
import me.daisyliao.service.web.rest.util.HeaderUtil;
import me.daisyliao.service.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
@RequestMapping("/api")
public class ServiceResource {

    private final Logger log = LoggerFactory.getLogger(ServiceResource.class);

    private static final String ENTITY_NAME = "service";

    @Autowired
    private ServiceService serviceService;

    private final ProcessClient processClient;

    public ServiceResource(ProcessClient processClient, ServiceService serviceService){
        this.processClient = processClient;
        this.serviceService = serviceService;
    }

    @RequestMapping(value = "/service/invoke_human_service",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<BusinessRuleModel> invokeHumanService(@RequestParam Map<String, String> map)throws Exception{

        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

        Process process = mapper.readValue(map.get("process"), Process.class);
        ServiceModel service = mapper.readValue(map.get("service"), ServiceModel.class);
        ProcessModel processModel = mapper.readValue(map.get("processModel"), ProcessModel.class);

        BusinessRuleModel result = serviceService.invokeHumanService(process, service, processModel);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/processes/{id}/available_services",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<ServiceModel>> getAvailableServices(@PathVariable String id) {
        log.debug("REST request to get available services of process : {}", id);

        Process process = processClient.getProcessFromProcessService(id);

        return Optional.ofNullable(process)
            .map(result -> new ResponseEntity<>(
                serviceService.getAvailableServices(process),
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * POST  /service-models : Create a new serviceModel.
     *
     * @param serviceModel the serviceModel to create
     * @return the ResponseEntity with status 201 (Created) and with body the new serviceModel, or with status 400 (Bad Request) if the serviceModel has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/service-models")
    @Timed
    public ResponseEntity<ServiceModel> createServiceModel(@RequestBody ServiceModel serviceModel) throws URISyntaxException {
        log.debug("REST request to save ServiceModel : {}", serviceModel);
        if (serviceModel.getId() != null) {
            throw new BadRequestAlertException("A new serviceModel cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ServiceModel result = serviceService.save(serviceModel);
        return ResponseEntity.created(new URI("/api/service-models/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /service-models : Updates an existing serviceModel.
     *
     * @param serviceModel the serviceModel to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated serviceModel,
     * or with status 400 (Bad Request) if the serviceModel is not valid,
     * or with status 500 (Internal Server Error) if the serviceModel couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/service-models")
    @Timed
    public ResponseEntity<ServiceModel> updateServiceModel(@RequestBody ServiceModel serviceModel) throws URISyntaxException {
        log.debug("REST request to update ServiceModel : {}", serviceModel);
        if (serviceModel.getId() == null) {
            return createServiceModel(serviceModel);
        }
        ServiceModel result = serviceService.save(serviceModel);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, serviceModel.getId().toString()))
            .body(result);
    }

    /**
     * GET  /service-models : get all the serviceModels.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of serviceModels in body
     */
    @GetMapping("/service-models")
    @Timed
    public ResponseEntity<List<ServiceModel>> getAllServiceModels(Pageable pageable) {
        log.debug("REST request to get a page of ServiceModels");
        Page<ServiceModel> page = serviceService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/service-models");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /service-models/:id : get the "id" serviceModel.
     *
     * @param id the id of the serviceModel to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the serviceModel, or with status 404 (Not Found)
     */
    @GetMapping("/service-models/{id}")
    @Timed
    public ResponseEntity<ServiceModel> getServiceModel(@PathVariable String id) {
        log.debug("REST request to get ServiceModel : {}", id);
        ServiceModel serviceModel = serviceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(serviceModel));
    }

    /**
     * DELETE  /service-models/:id : delete the "id" serviceModel.
     *
     * @param id the id of the serviceModel to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/service-models/{id}")
    @Timed
    public ResponseEntity<Void> deleteServiceModel(@PathVariable String id) {
        log.debug("REST request to delete ServiceModel : {}", id);
        serviceService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id)).build();
    }

    @GetMapping("/service-models/class/{serviceClass}")
    @Timed
    public ResponseEntity<List<ServiceModel>> getServiceByClass(@PathVariable String serviceClass) {
        log.debug("REST request to get Service by serviceClass : {}", serviceClass);
        List<ServiceModel> services = serviceService.findByServiceClass(serviceClass);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(services));
    }

}
