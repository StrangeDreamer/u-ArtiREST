package me.daisyliao.artifactmodel.domain;

import me.daisyliao.artifactmodel.domain.enumeration.ServiceType;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A ServiceModel.
 */
public class ServiceModel implements Serializable {

    private static final long serialVersionUID = 1L;

    public String id;

    public String name;

    public String serviceClass;

    public String url;

    public RestMethod method;

    public String inputArtifact;

    public String outputArtifact;

    public String comment;

    public ServiceType type;

    @XmlElementWrapper(name = "inputParams")
    @XmlElement(name = "inputParam")
    public Set<String> inputParams = new HashSet<>();

    @XmlRootElement
    public static enum RestMethod {
        GET, PUT, POST, DELETE, PATCH
    }

    public ServiceModel() {

    }

    public ServiceModel(String name, String serviceClass, String url, RestMethod method, String inputArtifact, String outputArtifact) {
        this.name = name;
        this.serviceClass = serviceClass;
        this.url = url;
        this.method = method;
        this.inputArtifact = inputArtifact;
        this.outputArtifact = outputArtifact;
    }

    public ServiceModel(String name, String serviceClass, String url, RestMethod method, String inputArtifact, String outputArtifact, String comment) {
        this.name = name;
        this.serviceClass = serviceClass;
        this.url = url;
        this.method = method;
        this.inputArtifact = inputArtifact;
        this.outputArtifact = outputArtifact;
        this.comment = comment;
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

    public ServiceModel name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServiceClass() {
        return serviceClass;
    }

    public ServiceModel serviceClass(String serviceClass) {
        this.serviceClass = serviceClass;
        return this;
    }

    public void setServiceClass(String serviceClass) {
        this.serviceClass = serviceClass;
    }

    public String getUrl() {
        return url;
    }

    public ServiceModel url(String url) {
        this.url = url;
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public RestMethod getMethod() {
        return method;
    }

    public ServiceModel method(RestMethod method) {
        this.method = method;
        return this;
    }

    public void setMethod(RestMethod method) {
        this.method = method;
    }

    public String getInputArtifact() {
        return inputArtifact;
    }

    public ServiceModel inputArtifact(String inputArtifact) {
        this.inputArtifact = inputArtifact;
        return this;
    }

    public void setInputArtifact(String inputArtifact) {
        this.inputArtifact = inputArtifact;
    }

    public String getOutputArtifact() {
        return outputArtifact;
    }

    public ServiceModel outputArtifact(String outputArtifact) {
        this.outputArtifact = outputArtifact;
        return this;
    }

    public void setOutputArtifact(String outputArtifact) {
        this.outputArtifact = outputArtifact;
    }

    public String getComment() {
        return comment;
    }

    public ServiceModel comment(String comment) {
        this.comment = comment;
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ServiceType getType() {
        return type;
    }

    public ServiceModel type(ServiceType type) {
        this.type = type;
        return this;
    }

    public void setType(ServiceType type) {
        this.type = type;
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
        ServiceModel serviceModel = (ServiceModel) o;
        if (serviceModel.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), serviceModel.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ServiceModel{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", serviceClass='" + getServiceClass() + "'" +
            ", url='" + getUrl() + "'" +
            ", method='" + getMethod() + "'" +
            ", inputArtifact='" + getInputArtifact() + "'" +
            ", outputArtifact='" + getOutputArtifact() + "'" +
            ", comment='" + getComment() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }
}
