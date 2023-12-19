package io.jenkins.plugins.synopsys.security.scan.extension.freestyle;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.*;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import hudson.util.ListBoxModel;
import io.jenkins.plugins.synopsys.security.scan.exception.PluginExceptionHandler;
import io.jenkins.plugins.synopsys.security.scan.exception.ScannerException;
import io.jenkins.plugins.synopsys.security.scan.extension.SecurityScan;
import io.jenkins.plugins.synopsys.security.scan.factory.ScanParametersFactory;
import io.jenkins.plugins.synopsys.security.scan.global.ExceptionMessages;
import io.jenkins.plugins.synopsys.security.scan.global.enums.SecurityProduct;
import jenkins.tasks.SimpleBuildStep;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;


public class SecurityScanFreestyle extends Builder implements SecurityScan, SimpleBuildStep {
    private String product;
    private String blackduck_url;
    private transient String blackduck_token;
    private String blackduck_install_directory;
    private Boolean blackduck_scan_full;
    private Boolean blackduckIntelligentScan;
    private String blackduck_scan_failure_severities;
    //    private Boolean blackduck_automation_fixpr;
    private Boolean blackduck_automation_prcomment;
    private String blackduck_download_url;

    private String coverity_url;
    private String coverity_user;
    private transient String coverity_passphrase;
    private String coverity_project_name;
    private String coverity_stream_name;
    private String coverity_policy_view;
    private String coverity_install_directory;
    private Boolean coverity_automation_prcomment;
    private String coverity_version;
    private Boolean coverity_local;

    private String polaris_server_url;
    private transient String polaris_access_token;
    private String polaris_application_name;
    private String polaris_project_name;
    private String polaris_assessment_types;
    private String polaris_triage;
    private String polaris_branch_name;
    //    private String polaris_branch_parent_name;

    private transient String bitbucket_token;

    private String synopsys_bridge_download_url;
    private String synopsys_bridge_download_version;
    private String synopsys_bridge_install_directory;
    private Boolean include_diagnostics;
    private Boolean network_airgap;

    @DataBoundConstructor
    public SecurityScanFreestyle() {
        // this block is kept empty intentionally
    }

    public String getProduct() {
        return product;
    }

    public String getBlackduck_url() {
        return blackduck_url;
    }

    public String getBlackduck_token() {
        return blackduck_token;
    }

    public String getBlackduck_install_directory() {
        return blackduck_install_directory;
    }

    public Boolean isBlackduck_scan_full() {
        return blackduck_scan_full;
    }

    public Boolean isBlackduckIntelligentScan() {
        return blackduckIntelligentScan;
    }

    public String getBlackduck_scan_failure_severities() {
        return blackduck_scan_failure_severities;
    }

    public Boolean isBlackduck_automation_prcomment() {
        return blackduck_automation_prcomment;
    }

    public String getBlackduck_download_url() {
        return blackduck_download_url;
    }

    public String getCoverity_url() {
        return coverity_url;
    }

    public String getCoverity_user() {
        return coverity_user;
    }

    public String getCoverity_passphrase() {
        return coverity_passphrase;
    }

    public String getCoverity_project_name() {
        return coverity_project_name;
    }

    public String getCoverity_stream_name() {
        return coverity_stream_name;
    }

    public String getCoverity_policy_view() {
        return coverity_policy_view;
    }

    public String getCoverity_install_directory() {
        return coverity_install_directory;
    }

    public Boolean isCoverity_automation_prcomment() {
        return coverity_automation_prcomment;
    }

    public String getCoverity_version() {
        return coverity_version;
    }

    public Boolean isCoverity_local() {
        return coverity_local;
    }

    public String getPolaris_server_url() {
        return polaris_server_url;
    }

    public String getPolaris_access_token() {
        return polaris_access_token;
    }

    public String getPolaris_application_name() {
        return polaris_application_name;
    }

    public String getPolaris_project_name() {
        return polaris_project_name;
    }

    public String getPolaris_assessment_types() {
        return polaris_assessment_types;
    }

    public String getPolaris_triage() {
        return polaris_triage;
    }

    public String getPolaris_branch_name() {
        return polaris_branch_name;
    }

    public String getBitbucket_token() {
        return bitbucket_token;
    }

    public String getSynopsys_bridge_download_url() {
        return synopsys_bridge_download_url;
    }

    public String getSynopsys_bridge_download_version() {
        return synopsys_bridge_download_version;
    }

    public String getSynopsys_bridge_install_directory() {
        return synopsys_bridge_install_directory;
    }

    public Boolean isInclude_diagnostics() {
        return include_diagnostics;
    }

    public Boolean isNetwork_airgap() {
        return network_airgap;
    }

    @DataBoundSetter
    public void setProduct(String product) {
        this.product = product;
    }

    @DataBoundSetter
    public void setBlackduck_url(String blackduck_url) {
        this.blackduck_url = blackduck_url;
    }

    @DataBoundSetter
    public void setBlackduck_token(String blackduck_token) {
        this.blackduck_token = blackduck_token;
    }

    @DataBoundSetter
    public void setBlackduck_install_directory(String blackduck_install_directory) {
        this.blackduck_install_directory = blackduck_install_directory;
    }

    @DataBoundSetter
    public void setBlackduck_scan_full(Boolean blackduck_scan_full) {
        if (blackduck_scan_full) {
            this.blackduckIntelligentScan = true;
        }
        if (!blackduck_scan_full) {
            this.blackduckIntelligentScan = false;
        }
        this.blackduck_scan_full = blackduck_scan_full ? true : null;
    }

    @DataBoundSetter
    public void setBlackduck_scan_failure_severities(String blackduck_scan_failure_severities) {
        this.blackduck_scan_failure_severities = Util.fixEmptyAndTrim(blackduck_scan_failure_severities);
    }

    @DataBoundSetter
    public void setBlackduck_automation_prcomment(Boolean blackduck_automation_prcomment) {
        this.blackduck_automation_prcomment = blackduck_automation_prcomment ? true : null;
    }

    @DataBoundSetter
    public void setBlackduck_download_url(String blackduck_download_url) {
        this.blackduck_download_url = Util.fixEmptyAndTrim(blackduck_download_url);
    }

    @DataBoundSetter
    public void setCoverity_url(String coverity_url) {
        this.coverity_url = coverity_url;
    }

    @DataBoundSetter
    public void setCoverity_user(String coverity_user) {
        this.coverity_user = coverity_user;
    }

    @DataBoundSetter
    public void setCoverity_passphrase(String coverity_passphrase) {
        this.coverity_passphrase = coverity_passphrase;
    }

    @DataBoundSetter
    public void setCoverity_project_name(String coverity_project_name) {
        this.coverity_project_name = Util.fixEmptyAndTrim(coverity_project_name);
    }

    @DataBoundSetter
    public void setCoverity_stream_name(String coverity_stream_name) {
        this.coverity_stream_name = Util.fixEmptyAndTrim(coverity_stream_name);
    }

    @DataBoundSetter
    public void setCoverity_policy_view(String coverity_policy_view) {
        this.coverity_policy_view = Util.fixEmptyAndTrim(coverity_policy_view);
    }

    @DataBoundSetter
    public void setCoverity_install_directory(String coverity_install_directory) {
        this.coverity_install_directory = coverity_install_directory;
    }

    @DataBoundSetter
    public void setCoverity_automation_prcomment(Boolean coverity_automation_prcomment) {
        this.coverity_automation_prcomment = coverity_automation_prcomment ? true : null;
    }

    @DataBoundSetter
    public void setCoverity_version(String coverity_version) {
        this.coverity_version = Util.fixEmptyAndTrim(coverity_version);
    }

    @DataBoundSetter
    public void setCoverity_local(Boolean coverity_local) {
        this.coverity_local = coverity_local ? true : null;
    }

    @DataBoundSetter
    public void setPolaris_server_url(String polaris_server_url) {
        this.polaris_server_url = polaris_server_url;
    }

    @DataBoundSetter
    public void setPolaris_access_token(String polaris_access_token) {
        this.polaris_access_token = polaris_access_token;
    }

    @DataBoundSetter
    public void setPolaris_application_name(String polaris_application_name) {
        this.polaris_application_name = Util.fixEmptyAndTrim(polaris_application_name);
    }

    @DataBoundSetter
    public void setPolaris_project_name(String polaris_project_name) {
        this.polaris_project_name = Util.fixEmptyAndTrim(polaris_project_name);
    }

    @DataBoundSetter
    public void setPolaris_assessment_types(String polaris_assessment_types) {
        this.polaris_assessment_types = Util.fixEmptyAndTrim(polaris_assessment_types);
    }

    @DataBoundSetter
    public void setPolaris_triage(String polaris_triage) {
        this.polaris_triage = Util.fixEmptyAndTrim(polaris_triage);
    }

    @DataBoundSetter
    public void setPolaris_branch_name(String polaris_branch_name) {
        this.polaris_branch_name = Util.fixEmptyAndTrim(polaris_branch_name);
    }

    @DataBoundSetter
    public void setBitbucket_token(String bitbucket_token) {
        this.bitbucket_token = bitbucket_token;
    }

    @DataBoundSetter
    public void setSynopsys_bridge_download_url(String synopsys_bridge_download_url) {
        this.synopsys_bridge_download_url = synopsys_bridge_download_url;
    }

    @DataBoundSetter
    public void setSynopsys_bridge_download_version(String synopsys_bridge_download_version) {
        this.synopsys_bridge_download_version = synopsys_bridge_download_version;
    }

    @DataBoundSetter
    public void setSynopsys_bridge_install_directory(String synopsys_bridge_install_directory) {
        this.synopsys_bridge_install_directory = synopsys_bridge_install_directory;
    }

    @DataBoundSetter
    public void setInclude_diagnostics(Boolean include_diagnostics) {
        this.include_diagnostics = include_diagnostics ? true : null;
    }

    @DataBoundSetter
    public void setNetwork_airgap(Boolean network_airgap) {
        this.network_airgap = network_airgap ? true : null;
    }

    private Map<String, Object> getParametersMap(FilePath workspace, TaskListener listener)
            throws PluginExceptionHandler {
        return ScanParametersFactory.preparePipelineParametersMap(
                this, ScanParametersFactory.getGlobalConfigurationValues(workspace, listener), listener);
    }


    @Override
    public void perform(@NonNull Run<?, ?> run, @NonNull FilePath workspace, @NonNull EnvVars env, @NonNull Launcher launcher, @NonNull TaskListener listener) throws InterruptedException, IOException {

        listener.getLogger().println("**************************** START EXECUTION OF SYNOPSYS SECURITY SCAN ****************************");

        int result;

        try {
            result = ScanParametersFactory.createPipelineCommand(run, listener, env, launcher, null, workspace)
                    .initializeScanner(getParametersMap(workspace, listener));
        } catch (Exception e) {
            if (e instanceof PluginExceptionHandler) {
                try {
                    throw new PluginExceptionHandler("Workflow failed! " + e.getMessage());
                } catch (PluginExceptionHandler ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                try {
                    throw new ScannerException(ExceptionMessages.scannerFailureMessage(e.getMessage()));
                } catch (ScannerException ex) {
                    throw new RuntimeException(ex);
                }
            }
        } finally {
            listener.getLogger().println("**************************** END EXECUTION OF SYNOPSYS SECURITY SCAN ****************************");
        }

    }

    @Extension
    public static class Descriptor extends BuildStepDescriptor<Builder> {

        @Override
        public String getDisplayName() {
            return "Synopsys Security Scan";
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return jobType.isAssignableFrom(FreeStyleProject.class);
        }


        public ListBoxModel doFillProductItems() {
            ListBoxModel items = new ListBoxModel();
            Map<String, String> customLabels = new HashMap<>();

            items.add(new ListBoxModel.Option("Select", "select"));
            customLabels.put(SecurityProduct.BLACKDUCK.name().toLowerCase(), "Black Duck");
            customLabels.put(SecurityProduct.COVERITY.name().toLowerCase(), "Coverity");
            customLabels.put(SecurityProduct.POLARIS.name().toLowerCase(), "Polaris");

            for (SecurityProduct product : SecurityProduct.values()) {
                String value = product.name().toLowerCase();
                String label = customLabels.getOrDefault(value, product.name());
                items.add(new ListBoxModel.Option(label, value));
            }
            return items;
        }
    }
}
