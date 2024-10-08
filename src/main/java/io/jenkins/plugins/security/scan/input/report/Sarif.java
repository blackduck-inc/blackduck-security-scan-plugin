package io.jenkins.plugins.security.scan.input.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Sarif {
    @JsonProperty("create")
    private Boolean create;

    @JsonProperty("issue")
    private Issue issue;

    @JsonProperty("file")
    private File file;

    @JsonProperty("severities")
    private List<String> severities;

    @JsonProperty("groupSCAIssues")
    private Boolean groupSCAIssues;

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Boolean getGroupSCAIssues() {
        return groupSCAIssues;
    }

    public void setGroupSCAIssues(Boolean groupSCAIssues) {
        this.groupSCAIssues = groupSCAIssues;
    }

    public Boolean getCreate() {
        return create;
    }

    public void setCreate(Boolean create) {
        this.create = create;
    }

    public List<String> getSeverities() {
        return severities;
    }

    public void setSeverities(List<String> severities) {
        this.severities = severities;
    }
}
