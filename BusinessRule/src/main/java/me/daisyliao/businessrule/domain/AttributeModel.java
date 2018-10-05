package me.daisyliao.businessrule.domain;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "attribute")
public class AttributeModel implements Serializable {

    private String name;

    private String comment;

    private String type;

    public AttributeModel() {

    }

    public AttributeModel(String name, String type, String comment) {
        this.name = name;
        this.comment = comment;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
