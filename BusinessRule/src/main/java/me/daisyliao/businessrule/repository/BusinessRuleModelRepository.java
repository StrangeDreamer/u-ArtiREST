package me.daisyliao.businessrule.repository;

import me.daisyliao.businessrule.domain.BusinessRuleModel;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Spring Data MongoDB repository for the BusinessRuleModel entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BusinessRuleModelRepository extends MongoRepository<BusinessRuleModel, String> {
    List<BusinessRuleModel> findByBusinessRuleClass(String businessRuleClass);
}
