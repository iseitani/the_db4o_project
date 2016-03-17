package com.example.finalthesis.db4o_the_project.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "path",
        "value",
        "operator",
        "reflectFieldType"
})
public class MyConstraint {

    @JsonProperty("path")
    private List<String> path = new ArrayList<String>();
    @JsonProperty("value")
    private String value;
    @JsonProperty("operator")
    private Integer operator;
    @JsonProperty("reflectFieldType")
    private String reflectFieldType;

    /**
     * @return The path
     */
    @JsonProperty("path")
    public List<String> getPath() {
        return path;
    }

    /**
     * @param path The path
     */
    @JsonProperty("path")
    public void setPath(List<String> path) {
        this.path = path;
    }

    /**
     * @return The value
     */
    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    /**
     * @param value The value
     */
    @JsonProperty("value")
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return The operator
     */
    @JsonProperty("operator")
    public Integer getOperator() {
        return operator;
    }

    /**
     * @param operator The operator
     */
    @JsonProperty("operator")
    public void setOperator(Integer operator) {
        this.operator = operator;
    }

    /**
     * @return The reflectFieldType
     */
    @JsonProperty("reflectFieldType")
    public String getReflectFieldType() {
        return reflectFieldType;
    }

    /**
     * @param reflectFieldType The reflectFieldType
     */
    @JsonProperty("reflectFieldType")
    public void setReflectFieldType(String reflectFieldType) {
        this.reflectFieldType = reflectFieldType;
    }
}
