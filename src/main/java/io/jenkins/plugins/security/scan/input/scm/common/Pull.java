package io.jenkins.plugins.security.scan.input.scm.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Pull {
    @JsonProperty("number")
    private Integer number;

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
}
