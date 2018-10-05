package me.daisyliao.processes.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import me.daisyliao.processes.client.ArtifactModelClient;
import me.daisyliao.processes.client.ServiceClient;
import me.daisyliao.processes.domain.*;
import me.daisyliao.processes.domain.Process;
import me.daisyliao.processes.domain.ServiceModel;
import me.daisyliao.processes.domain.enumeration.ServiceType;
import me.daisyliao.processes.repository.ArtifactRepository;
import me.daisyliao.processes.repository.ProcessRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.*;


/**
 * Service Implementation for managing Process.
 */
@Service
public class ProcessService {

    private final Logger log = LoggerFactory.getLogger(ProcessService.class);

    private final ProcessRepository processRepository;

    private final ArtifactRepository artifactRepository;

    private final ArtifactService artifactService;

    private final LogService logService;

    @Inject
    private ArtifactModelClient artifactModelClient;

    private ServiceClient serviceClient;

    public ProcessService(ProcessRepository processRepository, ArtifactRepository artifactRepository, ArtifactService artifactService, ArtifactModelClient artifactModelClient, LogService logService, ServiceClient serviceClient) {
        this.processRepository = processRepository;
        this.artifactRepository = artifactRepository;
        this.artifactService = artifactService;
        this.artifactModelClient = artifactModelClient;
        this.logService = logService;
        this.serviceClient = serviceClient;
    }

    public static final String CACHE_NAME = "artirest.process";

    /**
     * Save a process.
     *
     * @param process the entity to save
     * @return the persisted entity
     */
    public Process save(Process process) {
        log.debug("Request to save Process : {}", process);
        return processRepository.save(process);
    }

    /**
     * Get all the processes.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    public Page<Process> findAll(Pageable pageable) {
        log.debug("Request to get all Processes");
        return processRepository.findAll(pageable);
    }

    public Page<Process> findInstances(String processModelId, Pageable pageable) {
        Page<Process> processes = processRepository.findByProcessModelId(processModelId, pageable);
        return processes;
    }

    /**
     * Get one process by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Cacheable(CACHE_NAME)
    public Process findOne(String id) {
        log.debug("Request to get Process : {}", id);
        Process process = processRepository.findOne(id);
        String processModelId = process.getProcessModelId();

        ProcessModel processModel = artifactModelClient.getProcessModelFromArtifactModelService(processModelId);

        processModel.getId();
//        processModel.businessRules.size();
        processModel.artifactModelIds.size();
//        processModel.services.size();
        process.getArtifactIds().size();
        processModel.businessRuleClass.size();
        processModel.serviceClass.size();
        return process;
    }

    @CachePut(value = CACHE_NAME, key = "#process.id")
    public Process cacheSave(Process process) {
        log.debug("Save process to cache: {}", process.getId());
        return process;
    }

    /**
     * Delete the process by id.
     *
     * @param id the id of the entity
     */
    @CacheEvict(CACHE_NAME)
    public void delete(String id) {
        log.debug("Request to delete Process : {}", id);
        processRepository.delete(id);
    }

    public Process createProcessInstance(String processModelId, Process p) {
        Process process = new Process();

        ProcessModel processModel = artifactModelClient.getProcessModelFromArtifactModelService(processModelId);

        log.debug("process model", processModel);

        process.setName(processModel.getName());
        process.setProcessModelId(processModelId);
        process.setCreatedAt(ZonedDateTime.now());
        process.setUpdatedAt(ZonedDateTime.now());
        process.setBusinessRuleIds(p.getBusinessRuleIds());
        process.setServiceIds(p.getServiceIds());

        processRepository.save(process);
        return process;
    }


    public Artifact newArtifactFromModel(ArtifactModel model) {
        Artifact artifact = new Artifact();
        artifact.setArtifactModelId(model.getId());
        artifact.setName(model.getName());

        StateModel startState = model.getStartState();
        artifact.setCurrentState(startState == null ? "" : startState.name);


        for (AttributeModel attr : model.attributes) {
            switch (attr.getType()) {
                case "Integer":
                    artifact.getAttributes().add(new Attribute(attr.getName(), attr.getComment(), 0));
                    break;
                case "Double":
                    artifact.getAttributes().add(new Attribute(attr.getName(), attr.getComment(), 0D));
                    break;
                case "Long":
                    artifact.getAttributes().add(new Attribute(attr.getName(), attr.getComment(), 0L));
                    break;
                case "String":
                    artifact.getAttributes().add(new Attribute(attr.getName(), attr.getComment(), null));
                    break;
                case "Date":
                    artifact.getAttributes().add(new Attribute(attr.getName(), attr.getComment(), null));
                    break;
                default:
                    if (attr.getType().startsWith("List<")) {
                        String itemType = attr.getType().substring(5, attr.getType().length() - 1);
                        switch (itemType) {
                            case "Integer":
                                artifact.getAttributes().add(new Attribute(attr.getName(), attr.getComment(), new ArrayList<Integer>()));
                                break;
                            case "Double":
                                artifact.getAttributes().add(new Attribute(attr.getName(), attr.getComment(), new ArrayList<Double>()));
                                break;
                            case "Long":
                                artifact.getAttributes().add(new Attribute(attr.getName(), attr.getComment(), new ArrayList<Long>()));
                                break;
                            case "Date":
                                artifact.getAttributes().add(new Attribute(attr.getName(), attr.getComment(), new ArrayList<Date>()));
                                break;
                            case "String":
                                artifact.getAttributes().add(new Attribute(attr.getName(), attr.getComment(), new ArrayList<String>()));
                                break;
                            default:

                        }
                    }
            }

        }

        return artifact;
    }


    public Artifact createArtifact(String processId, String artifactModelId) {
        ArtifactModel artifactModel = artifactModelClient.getArtifactModelFromArtifactModelService(artifactModelId);
        Artifact artifact = newArtifactFromModel(artifactModel);

        artifactRepository.save(artifact);

        Process instance = processRepository.findOne(processId);
        instance.getArtifactIds().add(artifact.getId());

        // comment here to enable cache
        processRepository.save(instance);

        return artifact;
    }

    //service part
    public ServiceModel findService(String serviceName, Process process) {
        //for (ServiceModel service : processModel.services) {
        for (String serviceId : process.serviceIds) {
            ServiceModel service = serviceClient.getService(serviceId);
            if (service.name.equals(serviceName)) {
                return service;
            }
        }

        return null;
    }

    //used in process and br app
    public ArtifactModel findArtifactModel(String artifactName, ProcessModel processModel) {
        for (String artifactModelId : processModel.artifactModelIds) {
            ArtifactModel a = artifactModelClient.getArtifactModelFromArtifactModelService(artifactModelId);
            if (a.getName().equals(artifactName)) {
                return a;
            }
        }

        return null;
    }

    /**
     * 目前只允许: 一个流程实例里一个Artifact的名字只能有一个实例
     */
    public Artifact findArtifactByName(Process process, String artifactName) {
        Artifact artifact = null;

        for (String artifactId: process.getArtifactIds()) {
            Artifact a = artifactRepository.findOne(artifactId);
            if (a.getName().equals(artifactName)) {
                artifact = a;
                break;
            }
        }

        return artifact;
    }

    public Artifact createProcessArtifact(Process process, String artifactName, ProcessModel processModel) throws Exception {

        ArtifactModel artifactModel = findArtifactModel(artifactName, processModel);

        if (artifactModel == null) {
            throw new Exception("Illegal artifact model: " + artifactName);
        }

        Artifact artifact = newArtifactFromModel(artifactModel);

        artifact.setCurrentState(artifactModel.getStartState().name);
        artifact = artifactRepository.save(artifact);

        process.getArtifactIds().add(artifact.getId());

        // comment here to enable cache
        processRepository.save(process);

        return artifact;
    }

    public Process setArtifactAttributes(Process process, Artifact inputArtifact, ServiceModel serviceModel, ProcessModel processModel) throws Exception {
        Artifact artifact = findArtifactByName(process, inputArtifact.getName());

        if (artifact == null) {
            artifact = createProcessArtifact(process, inputArtifact.getName(), processModel);
        }

        log.debug("setArtifactAttributes");

        for (Attribute attribute : inputArtifact.getAttributes()) {
            if (serviceModel.inputParams.contains(attribute.getName())) {
                artifact.setAttribute(attribute.getName(), attribute);
            }
        }

        process.getArtifactIds().add(artifact.getId());

        artifactRepository.save(artifact);

        return process;
    }

    //call service app from here
    public Process invokeService(String serviceName, Process process, Artifact artifact) throws Exception {
        String processModelId = process.getProcessModelId();
        ProcessModel processModel = artifactModelClient.getProcessModelFromArtifactModelService(processModelId);

        ServiceModel serviceModel = findService(serviceName, process);

        process = setArtifactAttributes(process, artifact, serviceModel, processModel);

        if (serviceModel.type == ServiceType.HUMAN_TASK) {
            Map<String, String> map = new HashMap<>();
            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
            String processInString = mapper.writeValueAsString(process);
            String serviceInString = mapper.writeValueAsString(serviceModel);
            String processModelInString = mapper.writeValueAsString(processModel);
            map.put("process", processInString);
            map.put("service", serviceInString);
            map.put("processModel", processModelInString);
            BusinessRuleModel firstRuleSatisfied = serviceClient.invokeHumanService(map);
            //return invokeHumanService(process, artifact, serviceModel, processModel);
            return afterInvokingHumanService(firstRuleSatisfied, process, artifact, serviceModel);

        } else if (serviceModel.type == ServiceType.INVOKE_SERVICE) {
            // TODO: how to invoke auto service
        }

        return process;
    }

    private Process afterInvokingHumanService(BusinessRuleModel firstRuleSatisfied, Process process, Artifact artifact, ServiceModel service){

        // comment here to enable cache
        //这里为啥要单独存一次？原本是需要save一下的
        processRepository.save(process);

        artifact = findArtifactByName(process, artifact.getName());

        logService.callService(process.getId(), service.name);
        logService.updateArtifact(process.getId(), artifact.getId(), service.name);


        if (firstRuleSatisfied != null) {
            doTransitions(process, firstRuleSatisfied, service);
        }

        // 后置条件没有计算, 问题:
        // 如果后置条件不满足怎么办?

        // return processRepository.findOne(process.getId());

        return process;
    }

    //Service part
    public void afterInvokingService(Process process){
        if (isProcessEnded(process)){
            //???why need to save artifact explicitly and i need to find it first and save it???
            for (String artifactId : process.getArtifactIds()) {
                Artifact a = artifactRepository.findOne(artifactId);
                artifactRepository.save(a);
            }
            processRepository.save(process);
        }
    }

    /**
     * Perform state transitions after invoking a service
     */
    //Life cycle part
    private void doTransitions(Process process, BusinessRuleModel rule, ServiceModel service) {
        if (rule != null && !rule.action.transitions.isEmpty()) {
            for (BusinessRuleModel.Transition transition : rule.action.transitions) {
                for (String artifactId : process.getArtifactIds()) {
                    Artifact a = artifactRepository.findOne(artifactId);
                    if (a.getName().equals(transition.artifact) && a.getCurrentState().equals(transition.fromState)) {

                        // Do transition
                        a.setCurrentState(transition.toState);

                        // comment here to enable cache
                        artifactRepository.save(a);

                        logService.stateTransition(process.getId(), a.getId(), transition.fromState, transition.toState, service.name);
                    }
                }
            }
        }
    }

    /**
     * If the process is ended
     */
    public boolean isProcessEnded(Process process) {
        if (process.getArtifactIds().isEmpty()) {
            return false;
        }

        for (String artifactId : process.getArtifactIds()) {
            Artifact artifact = artifactRepository.findOne(artifactId);
            if (artifact.getCurrentState() != null) {
                String processModelId = process.getProcessModelId();
                ProcessModel processModel = artifactModelClient.getProcessModelFromArtifactModelService(processModelId);
                StateModel stateModel = artifactService.findState(artifact.getCurrentState(), artifact.getName(), processModel);
                if (stateModel == null || stateModel.type != StateModel.StateType.FINAL) {
                    return false;
                }
            }
        }

        return true;
    }
}
