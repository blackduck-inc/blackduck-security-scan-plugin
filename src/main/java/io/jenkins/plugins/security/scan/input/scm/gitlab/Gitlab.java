package io.jenkins.plugins.security.scan.input.scm.gitlab;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Gitlab {

    @JsonProperty("api")
    private Api api;

    @JsonProperty("user")
    private User user;

    @JsonProperty("repository")
    private Repository repository;

    public Gitlab() {
        user = new User();
        repository = new Repository();
    }

    public Api getApi() {
        return api;
    }

    public void setApi(Api api) {
        this.api = api;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }
}
