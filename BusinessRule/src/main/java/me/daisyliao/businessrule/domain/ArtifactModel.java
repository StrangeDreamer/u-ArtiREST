package me.daisyliao.businessrule.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A ArtifactModel.
 */
public class ArtifactModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String name;

    private String comment;

    private ZonedDateTime createdAt;

    private ZonedDateTime updatedAt;

    @XmlElementWrapper(name = "attributes")
    @XmlElement(name = "attribute")
    public Set<AttributeModel> attributes = new HashSet<>();

    @XmlElementWrapper(name = "states")
    @XmlElement(name = "state")
    public Set<StateModel> states = new HashSet<>();

    @XmlTransient
    @JsonIgnore
    public StateModel getStartState() {
        for (StateModel state : this.states) {
            if (state.type == StateModel.StateType.START) {
                return state;
            }
        }

        return null;
    }

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

    public ArtifactModel name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public ArtifactModel comment(String comment) {
        this.comment = comment;
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public ArtifactModel createdAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public ArtifactModel updatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
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
        ArtifactModel artifactModel = (ArtifactModel) o;
        if (artifactModel.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), artifactModel.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ArtifactModel{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", comment='" + getComment() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
