package io.jenkins.plugins.synopsys.security.scan.service.scm;

import com.cloudbees.jenkins.plugins.bitbucket.BitbucketSCMSource;
import hudson.EnvVars;
import hudson.model.TaskListener;
import io.jenkins.plugins.gitlabbranchsource.GitLabSCMSource;
import io.jenkins.plugins.synopsys.security.scan.exception.PluginExceptionHandler;
import io.jenkins.plugins.synopsys.security.scan.global.ApplicationConstants;
import io.jenkins.plugins.synopsys.security.scan.service.scm.bitbucket.BitbucketRepositoryService;
import io.jenkins.plugins.synopsys.security.scan.service.scm.github.GithubRepositoryService;
import io.jenkins.plugins.synopsys.security.scan.service.scm.gitlab.GitlabRepositoryService;
import java.util.Map;
import jenkins.model.Jenkins;
import jenkins.scm.api.SCMSource;
import jenkins.scm.api.SCMSourceOwner;
import org.jenkinsci.plugins.github_branch_source.GitHubSCMSource;

public class SCMRepositoryService {
    private final TaskListener listener;
    private final EnvVars envVars;

    public SCMRepositoryService(TaskListener listener, EnvVars envVars) {
        this.listener = listener;
        this.envVars = envVars;
    }

    public Object fetchSCMRepositoryDetails(
            Map<String, Boolean> installedBranchSourceDependencies,
            Map<String, Object> scanParameters,
            boolean isFixPrOrPrComment)
            throws PluginExceptionHandler {
        Integer projectRepositoryPullNumber = envVars.get(ApplicationConstants.ENV_CHANGE_ID_KEY) != null
                ? Integer.parseInt(envVars.get(ApplicationConstants.ENV_CHANGE_ID_KEY))
                : null;

        SCMSource scmSource = findSCMSource();
        if (scmSource instanceof BitbucketSCMSource
                && installedBranchSourceDependencies.containsKey(
                        ApplicationConstants.BITBUCKET_BRANCH_SOURCE_PLUGIN_NAME)
                && installedBranchSourceDependencies.get(ApplicationConstants.BITBUCKET_BRANCH_SOURCE_PLUGIN_NAME)) {
            BitbucketRepositoryService bitbucketRepositoryService = new BitbucketRepositoryService(listener);
            BitbucketSCMSource bitbucketSCMSource = (BitbucketSCMSource) scmSource;
            return bitbucketRepositoryService.fetchBitbucketRepositoryDetails(
                    scanParameters, bitbucketSCMSource, projectRepositoryPullNumber, isFixPrOrPrComment);
        } else if (scmSource instanceof GitHubSCMSource
                && installedBranchSourceDependencies.containsKey(ApplicationConstants.GITHUB_BRANCH_SOURCE_PLUGIN_NAME)
                && installedBranchSourceDependencies.get(ApplicationConstants.GITHUB_BRANCH_SOURCE_PLUGIN_NAME)) {
            GithubRepositoryService githubRepositoryService = new GithubRepositoryService(listener);
            GitHubSCMSource gitHubSCMSource = (GitHubSCMSource) scmSource;

            String repositoryOwner = gitHubSCMSource.getRepoOwner();
            String repositoryName = gitHubSCMSource.getRepository();
            String branchName = envVars.get(ApplicationConstants.BRANCH_NAME);
            String repositoryUrl = envVars.get(ApplicationConstants.GIT_URL);

            return githubRepositoryService.createGithubObject(
                    scanParameters,
                    repositoryName,
                    repositoryOwner,
                    projectRepositoryPullNumber,
                    branchName,
                    repositoryUrl,
                    isFixPrOrPrComment);
        } else if (scmSource instanceof GitLabSCMSource
                && installedBranchSourceDependencies.containsKey(ApplicationConstants.GITLAB_BRANCH_SOURCE_PLUGIN_NAME)
                && installedBranchSourceDependencies.get(ApplicationConstants.GITLAB_BRANCH_SOURCE_PLUGIN_NAME)) {
            GitlabRepositoryService gitlabRepositoryService = new GitlabRepositoryService(listener);
            GitLabSCMSource gitLabSCMSource = (GitLabSCMSource) scmSource;

            String repositoryUrl = envVars.get(ApplicationConstants.GIT_URL);
            String branchName = envVars.get(ApplicationConstants.BRANCH_NAME);
            String repositoryName = gitLabSCMSource.getProjectPath();

            return gitlabRepositoryService.createGitlabObject(
                    scanParameters,
                    repositoryName,
                    projectRepositoryPullNumber,
                    branchName,
                    repositoryUrl,
                    isFixPrOrPrComment);
        }
        return null;
    }

    public SCMSource findSCMSource() {
        String jobName = envVars.get(ApplicationConstants.ENV_JOB_NAME_KEY)
                .substring(0, envVars.get(ApplicationConstants.ENV_JOB_NAME_KEY).indexOf("/"));
        Jenkins jenkins = Jenkins.getInstanceOrNull();
        SCMSourceOwner owner = jenkins != null ? jenkins.getItemByFullName(jobName, SCMSourceOwner.class) : null;
        if (owner != null) {
            for (SCMSource scmSource : owner.getSCMSources()) {
                if (owner.getSCMSource(scmSource.getId()) != null) {
                    return scmSource;
                }
            }
        }
        return null;
    }
}
