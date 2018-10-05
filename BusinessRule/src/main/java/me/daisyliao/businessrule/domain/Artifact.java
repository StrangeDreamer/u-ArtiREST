package me.daisyliao.businessrule.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A Artifact.
 */
@Document(collection = "artifact")
public class Artifact implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("name")
    private String name;

    @Field("current_state")
    private String currentState;

    @Field("created_at")
    @CreatedDate
    private ZonedDateTime createdAt;

    @Field("updated_at")
    @LastModifiedDate
    private ZonedDateTime updatedAt;

    private String artifactModelId;

    private Set<Attribute> attributes = new HashSet<>();

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

    public Artifact name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrentState() {
        return currentState;
    }

    public Artifact currentState(String currentState) {
        this.currentState = currentState;
        return this;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public Artifact createdAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Artifact updatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<Attribute> attributes) {
        this.attributes = attributes;
    }

    public String getArtifactModelId() {
        return artifactModelId;
    }

    public void setArtifactModelId(String artifactModelId) {
        this.artifactModelId = artifactModelId;
    }

    public void setAttribute(String name, Attribute attr){
        boolean exist = false;
        for (Attribute _attr : this.attributes){
            if (_attr.getName().equals(name)){
                exist = true;
                _attr.setValue(attr.getValue());
                break;
            }
        }

        if (!exist){
            this.attributes.add(attr);
        }
    }

    @XmlTransient
    @JsonIgnore
    public Attribute getAttribute(String name){
        for (Attribute attr : this.attributes){
            if (attr.getName().equals(name)){
                return attr;
            }
        }

        return null;
    }


    public void updateAttribute(String name, Object value){
        Attribute attr = this.getAttribute(name);
        attr.setValue(value);
        this.setAttribute(name, attr);
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
        Artifact artifact = (Artifact) o;
        if (artifact.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), artifact.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Artifact{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", currentState='" + getCurrentState() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
