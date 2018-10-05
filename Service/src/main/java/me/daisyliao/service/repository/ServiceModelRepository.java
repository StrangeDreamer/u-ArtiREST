package me.daisyliao.service.repository;

import me.daisyliao.service.domain.ServiceModel;
import org.springframework.stereotype.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Spring Data MongoDB repository for the ServiceModel entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ServiceModelRepository extends MongoRepository<ServiceModel, String> {
    List<ServiceModel> findByServiceClass(String serviceClass);
}
