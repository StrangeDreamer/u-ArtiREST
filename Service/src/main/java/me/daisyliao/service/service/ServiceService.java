package me.daisyliao.service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import me.daisyliao.service.client.ArtifactModelClient;
import me.daisyliao.service.client.BusinessRuleClient;
import me.daisyliao.service.client.ProcessClient;
import me.daisyliao.service.domain.*;
import me.daisyliao.service.domain.Process;
import me.daisyliao.service.repository.ServiceModelRepository;
import me.daisyliao.service.web.rest.ServiceResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ServiceService {

    private final Logger log = LoggerFactory.getLogger(ServiceService.class);

    @Autowired
    private ArtifactModelClient artifactModelClient;

    @Autowired
    private BusinessRuleClient businessRuleClient;

    @Autowired
    private ProcessClient processClient;

    private final ServiceModelRepository serviceModelRepository;

    public ServiceService(ServiceModelRepository serviceModelRepository) {
        this.serviceModelRepository = serviceModelRepository;
    }

    public BusinessRuleModel invokeHumanService(Process process, ServiceModel service, ProcessModel processModel) throws Exception {

        Set<BusinessRuleModel> rules = findServiceRelatedRules(service.name, process);

        BusinessRuleModel firstRuleSatisfied = null;
        for (BusinessRuleModel rule : rules) {
            Map<String, String> map = new HashMap<>();
            ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
            String processInString = mapper.writeValueAsString(process);
            String ruleInString = mapper.writeValueAsString(rule);
            String processModelInString = mapper.writeValueAsString(processModel);
            map.put("process", processInString);
            map.put("rule", ruleInString);
            map.put("processModel", processModelInString);
            if (businessRuleClient.verifyAtomConditions(map)
            ) {
                firstRuleSatisfied = rule;
                log.debug(firstRuleSatisfied.toString());
                break;
            }
        }

        if (!rules.isEmpty() && firstRuleSatisfied == null) {
            throw new Exception("PreConditions of business rules for service " + service.name + " are not satisfied.");

        }

        if (firstRuleSatisfied != null) {
            log.debug("first business rule: {}", firstRuleSatisfied.name);
        }


        return firstRuleSatisfied;
    }

    public Set<BusinessRuleModel> findServiceRelatedRules(String serviceName, Process process) {
        Set<BusinessRuleModel> rules = new HashSet<>();

//        for (BusinessRuleModel rule : processModel.businessRules) {
        for (String businessRuleId : process.businessRuleIds) {
            BusinessRuleModel rule = businessRuleClient.getBusinessRule(businessRuleId);
            if (rule.action.service.equals(serviceName)) {
                rules.add(rule);
                log.debug("findServiceRelatedRules");
            }
        }

        return rules;
    }

    public List<ServiceModel> getAvailableServices(Process process) {
        List<ServiceModel> services = new ArrayList<>();

        for (String artifactId : process.getArtifactIds()) {
            services.addAll(availableServices(artifactId, process));
        }

        if (process.getArtifactIds().isEmpty()) {
            services.addAll(availableBeginServices(process));
        }

        return services;
    }

    public List<ServiceModel> availableBeginServices(Process process) {
        List<ServiceModel> services = new ArrayList<>();

        String processModelId = process.getProcessModelId();
        ProcessModel processModel = artifactModelClient.getProcessModelFromArtifactModelService(processModelId);
        //log.debug("processModel", processModel.toString());
        //for (BusinessRuleModel rule : processModel.businessRules) {
        for (String businessRuleId : process.businessRuleIds) {
            BusinessRuleModel rule = businessRuleClient.getBusinessRule(businessRuleId);
            if (rule.action == null) {
                log.debug("rule.action == null");
                continue;
            }

            for (BusinessRuleModel.Transition transition : rule.action.transitions) {
                StateModel stateModel = findState(transition.fromState, transition.artifact, processModel);
                if (stateModel != null && stateModel.type == StateModel.StateType.START) {
                    services.add(findService(rule.action.service, process));
                    break;
                }
            }
        }

        return services;
    }

    public List<ServiceModel> availableServices(String artifactId, Process process) {
        List<ServiceModel> services = new ArrayList<>();
        List<String> availables = new ArrayList<>();

        Artifact artifact = processClient.getArtifact(artifactId);

        List<String> servicesWithRules = new ArrayList<>();

        for (String businessRuleId : process.businessRuleIds) {
            BusinessRuleModel rule = businessRuleClient.getBusinessRule(businessRuleId);
            if (rule.action != null) {
                servicesWithRules.add(rule.action.service);
            }

            boolean stateSatisfied = false;

            for (BusinessRuleModel.Atom atom : rule.preConditions) {
                if (atom.artifact.equals(artifact.getName())
                    && atom.type.equals(BusinessRuleModel.AtomType.INSTATE)
                    && artifact.getCurrentState().equals(atom.state)) {

                    stateSatisfied = true;
                }
            }

            if (stateSatisfied) {
                availables.add(rule.action.service);
            }
        }

        for (String serviceId : process.serviceIds) {
            ServiceModel service = findOne(serviceId);
            if (!servicesWithRules.contains(service.name) || availables.contains(service.name)) {
                services.add(service);
            }
        }

        return services;
    }

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

    public ServiceModel findService(String serviceClass, Process process) {
        log.debug("service class is ", serviceClass);
//        for (ServiceModel service : processModel.services) {
        for (String serviceId : process.serviceIds) {
            ServiceModel service = findOne(serviceId);
            if (service.serviceClass.equals(serviceClass)) {
                log.debug("service.name.equals(rule.action.service)");
                return service;
            }
            else {
                log.debug("service name not match");
            }
        }

        return null;
    }

    /**
     * Save a serviceModel.
     *
     * @param serviceModel the entity to save
     * @return the persisted entity
     */
    public ServiceModel save(ServiceModel serviceModel) {
        log.debug("Request to save ServiceModel : {}", serviceModel);
        return serviceModelRepository.save(serviceModel);
    }

    /**
     * Get all the serviceModels.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    public Page<ServiceModel> findAll(Pageable pageable) {
        log.debug("Request to get all ServiceModels");
        return serviceModelRepository.findAll(pageable);
    }

    /**
     * Get one serviceModel by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    public ServiceModel findOne(String id) {
        log.debug("Request to get ServiceModel : {}", id);
        return serviceModelRepository.findOne(id);
    }

    /**
     * Delete the serviceModel by id.
     *
     * @param id the id of the entity
     */
    public void delete(String id) {
        log.debug("Request to delete ServiceModel : {}", id);
        serviceModelRepository.delete(id);
    }

    public List<ServiceModel> findByServiceClass(String serviceClass){
        log.debug("Request to get all servicees by serviceClass" + serviceClass);
        return serviceModelRepository.findByServiceClass(serviceClass);
    }

}
