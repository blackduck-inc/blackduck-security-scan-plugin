package io.jenkins.plugins.security.scan.input.report;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Reports {
    @JsonProperty("sarif")
    private Sarif sarif;

    public Sarif getSarif() {
        return sarif;
    }

    public void setSarif(Sarif sarif) {
        this.sarif = sarif;
    }
}
