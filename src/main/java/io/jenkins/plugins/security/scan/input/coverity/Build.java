package io.jenkins.plugins.security.scan.input.coverity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Build {
    @JsonProperty("command")
    private String command;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
