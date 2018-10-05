package me.daisyliao.businessrule.service;

import me.daisyliao.businessrule.client.ArtifactModelClient;
import me.daisyliao.businessrule.client.ProcessClient;
import me.daisyliao.businessrule.domain.Process;
import me.daisyliao.businessrule.domain.*;
import me.daisyliao.businessrule.repository.BusinessRuleModelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;


/**
 * Service Implementation for managing BusinessRule.
 */
@Service
public class BusinessRuleService {

    private final Logger log = LoggerFactory.getLogger(BusinessRuleService.class);

    private final BusinessRuleModelRepository businessRuleModelRepository;

    private ArtifactModelClient artifactModelClient;

    private ProcessClient processClient;

    public BusinessRuleService(BusinessRuleModelRepository businessRuleModelRepository, ArtifactModelClient artifactModelClient, ProcessClient processClient) {
        this.businessRuleModelRepository = businessRuleModelRepository;
        this.artifactModelClient = artifactModelClient;
        this.processClient = processClient;
    }

    public boolean verifyAtomConditions(Process process, BusinessRuleModel rule, ProcessModel processModel) {
        log.debug("begin verifyAtomConditions");

        Set<BusinessRuleModel.Atom> atoms = rule.preConditions;
        boolean satisfied = true;

        for (BusinessRuleModel.Atom atom : atoms) {
            Artifact a = findArtifactByName(process, atom.artifact);
            log.debug("find artifact: ");
            log.debug(a.getName());

            if (atom.type.equals(BusinessRuleModel.AtomType.INSTATE)) {
                log.debug("a.currentState INSTATE: ");
                log.debug(a.getCurrentState());
                satisfied = satisfied
                    && a != null
                    && a.getCurrentState() != null
                    && a.getCurrentState().equals(atom.state);
            }

            if (atom.type.equals(BusinessRuleModel.AtomType.ATTRIBUTE_DEFINED)) {
                log.debug("a.attribute ATTRIBUTE_DEFINED: ");
                log.debug(a.getAttribute(atom.attribute).getName());
                if(a.getAttribute(atom.attribute).getValue() == null){
                    log.debug("a.attribute is null");
                }
                log.debug(String.valueOf(a.getAttribute(atom.attribute).getValue()));
                satisfied = satisfied
                    && a != null
                    && a.getAttribute(atom.attribute) != null
                    && a.getAttribute(atom.attribute).getValue() != null;
            }

            if (atom.type.equals(BusinessRuleModel.AtomType.SCALAR_COMPARISON)) {
                satisfied = satisfied
                    && a != null
                    && a.getAttribute(atom.attribute) != null
                    && a.getAttribute(atom.attribute).getValue() != null;

                if (!satisfied) {
                    break;
                }

                Attribute attribute = a.getAttribute(atom.attribute);

                AttributeModel attributeModel = findAttributeModel(processModel, atom.artifact, atom.attribute);

                switch (atom.operator) {
                    case EQUAL:
                        satisfied = satisfied && attribute.getValue().equals(atom.value);
                        break;
                    case LESS:
                        switch (attributeModel.getType()) {
                            case "Integer":
                                satisfied = satisfied && ((Integer) attribute.getValue() < (Integer) atom.value);
                                break;
                            case "Long":
                                satisfied = satisfied && ((Long) attribute.getValue() < (Long) atom.value);
                                break;
                            case "Double":
                                satisfied = satisfied && ((Double) attribute.getValue() < (Double) atom.value);
                                break;
                            default:
                                ;
                        }
                        break;
                    case LARGER:
                        switch (attributeModel.getType()) {
                            case "Integer":
                                satisfied = satisfied && ((Integer) attribute.getValue() > (Integer) atom.value);
                                break;
                            case "Long":
                                satisfied = satisfied && ((Long) attribute.getValue() > (Long) atom.value);
                                break;
                            case "Double":
                                satisfied = satisfied && ((Double) attribute.getValue() > (Double) atom.value);
                                break;
                            default:
                                ;
                        }
                        break;
                    default:
                        ;
                }
            }

            if (!satisfied) {
                break;
            }
        }


        return satisfied;
    }

    public Artifact findArtifactByName(Process process, String artifactName) {
        Artifact artifact = null;

        for (String artifactId: process.getArtifactIds()) {
            Artifact a = processClient.getArtifact(artifactId);
            if (a.getName().equals(artifactName)) {
                artifact = a;
                break;
            }
        }

        return artifact;
    }

    public AttributeModel findAttributeModel(ProcessModel processModel, String artifactName, String attributeName) {
        ArtifactModel artifactModel = findArtifactModel(artifactName, processModel);
        AttributeModel attributeModel = null;

        if (artifactModel != null) {
            for (AttributeModel attribute : artifactModel.attributes) {
                if (attribute.getName().equals(attributeName)) {
                    attributeModel = attribute;
                    break;
                }
            }
        }

        return attributeModel;
    }

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
     * Save a businessRule.
     *
     * @param businessRuleModel the entity to save
     * @return the persisted entity
     */
    public BusinessRuleModel save(BusinessRuleModel businessRuleModel) {
        log.debug("Request to save BusinessRule : {}", businessRuleModel);
        return businessRuleModelRepository.save(businessRuleModel);
    }

    /**
     * Get all the businessRules.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    public Page<BusinessRuleModel> findAll(Pageable pageable) {
        log.debug("Request to get all BusinessRules");
        return businessRuleModelRepository.findAll(pageable);
    }

    /**
     * Get one businessRule by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    public BusinessRuleModel findOne(String id) {
        log.debug("Request to get BusinessRule : {}", id);
        return businessRuleModelRepository.findOne(id);
    }

    /**
     * Delete the businessRule by id.
     *
     * @param id the id of the entity
     */
    public void delete(String id) {
        log.debug("Request to delete BusinessRule : {}", id);
        businessRuleModelRepository.delete(id);
    }

    public List<BusinessRuleModel> findByBusinessRuleClass(String businessRuleClass){
        log.debug("Request to get all businessRules by businessRuleClass" + businessRuleClass);
        return businessRuleModelRepository.findByBusinessRuleClass(businessRuleClass);
    }
}
