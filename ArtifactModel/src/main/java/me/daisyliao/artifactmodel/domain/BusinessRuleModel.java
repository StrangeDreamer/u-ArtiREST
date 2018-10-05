package me.daisyliao.artifactmodel.domain;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A BusinessRule.
 */
public class BusinessRuleModel implements Serializable {
    public String id;

    public String name;

    public String businessRuleClass;

    @XmlElementWrapper(name = "preConditions")
    @XmlElement(name = "condition")
    public Set<Atom> preConditions = new HashSet<>();

    @XmlElementWrapper(name = "postConditions")
    @XmlElement(name = "condition")
    public Set<Atom> postConditions = new HashSet<>();

    public Action action;

    @XmlRootElement(name = "condition")
    public static class Condition implements Serializable{
        public ConditionType type;

        @XmlElementWrapper(name = "atoms")
        @XmlElement(name = "atom")
        public Set<Atom> atoms = new HashSet<>();
    }

    @XmlRootElement
    public enum ConditionType {
        AND, OR
    }

    /**
     * Atom types 1. instate(artifact, state) 2. defined(artifact, attribute) 3. Condition(artifact,
     * attribute, operator, value)
     */
    @XmlRootElement
    public enum AtomType {
        INSTATE, ATTRIBUTE_DEFINED, SCALAR_COMPARISON
    }

    public enum Operator{
        LESS, LARGER, EQUAL,
        LESS_EQUAL, LARGER_EQUAL
    }

    @XmlRootElement(name = "atom")
    public static class Atom implements Serializable{
        public String artifact;

        public String attribute;

        public String state;

        public Operator operator;

        public AtomType type;

        public Object value; // TODO value can have multiple types

        public Atom() {

        }

        public Atom(String artifact, String attribute, String state, Operator operator, AtomType type, Object value) {
            this.artifact = artifact;
            this.attribute = attribute;
            this.state = state;
            this.operator = operator;
            this.type = type;
            this.value = value;
        }
    }

    @XmlRootElement(name = "action")
    public static class Action implements Serializable{
        public String name;
        public String service;

        @XmlElementWrapper(name = "transitions")
        @XmlElement(name = "transition")
        public Set<Transition> transitions = new HashSet<>();

        public Action() {

        }

        public Action(String name, String service, Set<Transition> transitions) {
            this.name = name;
            this.service = service;
            this.transitions = transitions;
        }
    }

    @XmlRootElement(name = "transition")
    public static class Transition implements Serializable{
        public String artifact;

        public String fromState;

        public String toState;

        public Transition() {

        }

        public Transition(String artifact, String fromState, String toState) {
            this.artifact = artifact;
            this.fromState = fromState;
            this.toState = toState;
        }
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

    public BusinessRuleModel name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBusinessRuleClass() {
        return businessRuleClass;
    }

    public BusinessRuleModel businessRuleClass(String businessRuleClass) {
        this.businessRuleClass = businessRuleClass;
        return this;
    }

    public void setBusinessRuleClass(String businessRuleClass) {
        this.businessRuleClass = businessRuleClass;
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
        BusinessRuleModel businessRule = (BusinessRuleModel) o;
        if (businessRule.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), businessRule.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "BusinessRule{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", businessRuleClass='" + getBusinessRuleClass() + "'" +
            "}";
    }
}
