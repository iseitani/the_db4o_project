package com.example.finalthesis.db4o_the_project.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "constraints"
})
public class ConstraintsJsonData {

    @JsonProperty("constraints")
    private List<MyConstraint> constraints = new ArrayList<>();

    /**
     * @return The constraints
     */
    @JsonProperty("constraints")
    public List<MyConstraint> getConstraints() {
        return constraints;
    }

    /**
     * @param constraints The constraints
     */
    @JsonProperty("constraints")
    public void setConstraints(List<MyConstraint> constraints) {
        this.constraints = constraints;
    }

}