package me.daisyliao.processes.domain;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A Process.
 */
@Document(collection = "process")
public class Process implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("name")
    private String name;

    @Field("is_running")
    private Boolean isRunning;

    @Field("created_at")
    @CreatedDate
    private ZonedDateTime createdAt;

    @Field("updated_at")
    @LastModifiedDate
    private ZonedDateTime updatedAt;

    @XmlElementWrapper(name = "artifactIds")
    @XmlElement(name = "artifactId")
    private Set<String> artifactIds = new HashSet<>();

    private String processModelId;

    @XmlElementWrapper(name = "serviceIds")
    @XmlElement(name = "serviceId")
    public Set<String> serviceIds = new HashSet<>();

    @XmlElementWrapper(name = "businessRuleIds")
    @XmlElement(name = "businessRuleIds")
    public Set<String> businessRuleIds = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Process name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isIsRunning() {
        return isRunning;
    }

    public Process isRunning(Boolean isRunning) {
        this.isRunning = isRunning;
        return this;
    }

    public void setIsRunning(Boolean isRunning) {
        this.isRunning = isRunning;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public Process createdAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Process updatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<String> getBusinessRuleIds() {
        return businessRuleIds;
    }

    public void setBusinessRuleIds(Set<String> businessRuleIds) {
        this.businessRuleIds = businessRuleIds;
    }

    public Set<String> getArtifactIds() {
        return artifactIds;
    }

    public void setArtifactIds(Set<String> artifactIds) {
        this.artifactIds = artifactIds;
    }

    public Set<String> getServiceIds() {
        return serviceIds;
    }

    public void setServiceIds(Set<String> serviceIds) {
        this.serviceIds = serviceIds;
    }

    public String getProcessModelId(){
        return processModelId;
    }
    public void setProcessModelId(String processModelId){
        this.processModelId = processModelId;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Process process = (Process) o;
        if (process.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), process.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Process{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", isRunning='" + isIsRunning() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
