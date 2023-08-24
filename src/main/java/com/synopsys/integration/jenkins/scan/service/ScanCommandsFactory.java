package com.synopsys.integration.jenkins.scan.service;

import com.synopsys.integration.jenkins.scan.ScanPipelineCommands;
import com.synopsys.integration.jenkins.scan.SecurityScanner;
import com.synopsys.integration.jenkins.scan.extension.global.ScannerGlobalConfig;
import com.synopsys.integration.jenkins.scan.extension.pipeline.SecurityScanStep;
import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import com.synopsys.integration.jenkins.scan.global.ExceptionMessages;
import com.synopsys.integration.jenkins.scan.global.Utility;
import com.synopsys.integration.jenkins.scan.global.enums.ScanType;
import hudson.AbortException;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Node;
import hudson.model.Run;
import hudson.model.TaskListener;
import java.util.HashMap;
import java.util.Map;
import jenkins.model.GlobalConfiguration;

public class ScanCommandsFactory {
    private final TaskListener listener;
    private final EnvVars envVars;
    private final FilePath workspace;

    private ScanCommandsFactory(TaskListener listener, EnvVars envVars, FilePath workspace) throws AbortException {
        this.listener = listener;
        this.envVars = envVars;

        if (workspace == null) {
            throw new AbortException(ExceptionMessages.NULL_WORKSPACE);
        }
        this.workspace = workspace;
    }

    public static ScanPipelineCommands createPipelineCommand(Run<?, ?> run, TaskListener listener,
                                                             EnvVars envVars, Launcher launcher,
                                                             Node node, FilePath workspace) {
        return new ScanPipelineCommands(
            new SecurityScanner(run, listener, launcher, workspace, envVars,
                new ScannerArgumentService(listener, envVars, workspace)), workspace, listener);
    }

    public static Map<String, Object> preparePipelineParametersMap(SecurityScanStep scanStep) {
        Map<String, Object> parametersMap = new HashMap<>(getGlobalConfigurationValues());

        ScanType scanType = null;
        if (parametersMap.containsKey(ApplicationConstants.SCAN_TYPE_KEY)) {
            scanType = ScanType.valueOf(parametersMap.get(ApplicationConstants.SCAN_TYPE_KEY).toString());
        }
        if (!Utility.isStringNullOrBlank(scanStep.getScan_type())) {
            scanType = ScanType.valueOf(scanStep.getScan_type().trim().toUpperCase());
        }
        if (scanType == null) {
            scanType = ScanType.BLACKDUCK;
        }
        parametersMap.put(ApplicationConstants.SCAN_TYPE_KEY, scanType.name());
        
        if (scanType.equals(ScanType.COVERITY)) {
            parametersMap.putAll(prepareCoverityParametersMap(scanStep));
        } else if (scanType.equals(ScanType.POLARIS)) {
            parametersMap.putAll(preparePolarisParametersMap(scanStep));
        } else {
            parametersMap.putAll(prepareBlackDuckParametersMap(scanStep));
        }

        if (!Utility.isStringNullOrBlank(scanStep.getBitbucket_token())) {
            parametersMap.put(ApplicationConstants.BITBUCKET_TOKEN_KEY, scanStep.getBitbucket_token());
        }

        parametersMap.putAll(prepareBridgeParametersMap(scanStep));

        return parametersMap;
    }

    private static Map<String, Object> getGlobalConfigurationValues() {
        Map<String, Object> globalParameters = new HashMap<>();

        ScannerGlobalConfig config = GlobalConfiguration.all().get(ScannerGlobalConfig.class);
        if (config != null) {
            if (!Utility.isStringNullOrBlank(config.getScanType())) {
                globalParameters.put(ApplicationConstants.SCAN_TYPE_KEY, config.getScanType());
            }

            if (!Utility.isStringNullOrBlank(config.getBlackDuckUrl())) {
                globalParameters.put(ApplicationConstants.BLACKDUCK_URL_KEY, config.getBlackDuckUrl());
            }
            if (!Utility.isStringNullOrBlank(config.getBlackDuckApiToken())) {
                globalParameters.put(ApplicationConstants.BLACKDUCK_API_TOKEN_KEY, config.getBlackDuckApiToken());
            }

            if (!Utility.isStringNullOrBlank(config.getCoverityConnectUrl())) {
                globalParameters.put(ApplicationConstants.COVERITY_CONNECT_URL_KEY, config.getCoverityConnectUrl());
            }
            if (!Utility.isStringNullOrBlank(config.getCoverityConnectUserName())) {
                globalParameters.put(ApplicationConstants.COVERITY_CONNECT_USER_NAME_KEY, config.getCoverityConnectUserName());
            }
            if (!Utility.isStringNullOrBlank(config.getCoverityConnectUserPassword())) {
                globalParameters.put(ApplicationConstants.COVERITY_CONNECT_USER_PASSWORD_KEY, config.getCoverityConnectUserPassword());
            }

            if (!Utility.isStringNullOrBlank(config.getBitbucketToken())) {
                globalParameters.put(ApplicationConstants.BITBUCKET_TOKEN_KEY, config.getBitbucketToken());
            }

            if (!Utility.isStringNullOrBlank(config.getSynopsysBridgeDownloadUrl())) {
                globalParameters.put(ApplicationConstants.BRIDGE_DOWNLOAD_URL, config.getSynopsysBridgeDownloadUrl());
            }
        }

        return globalParameters;
    }

    private static Map<String, Object> prepareBlackDuckParametersMap(SecurityScanStep scanStep) {
        Map<String, Object> blackDuckParameters = new HashMap<>();

        if (!Utility.isStringNullOrBlank(scanStep.getBlackduck_url())) {
            blackDuckParameters.put(ApplicationConstants.BLACKDUCK_URL_KEY, scanStep.getBlackduck_url());
        }
        if (!Utility.isStringNullOrBlank(scanStep.getBlackduck_api_token())) {
            blackDuckParameters.put(ApplicationConstants.BLACKDUCK_API_TOKEN_KEY, scanStep.getBlackduck_api_token());
        }
        if (!Utility.isStringNullOrBlank(scanStep.getBlackduck_install_directory())) {
            blackDuckParameters.put(ApplicationConstants.BLACKDUCK_INSTALL_DIRECTORY_KEY, scanStep.getBlackduck_install_directory());
        }
        if (!Utility.isStringNullOrBlank(scanStep.getBlackduck_scan_failure_severities())) {
            blackDuckParameters.put(ApplicationConstants.BLACKDUCK_SCAN_FAILURE_SEVERITIES_KEY, scanStep.getBlackduck_scan_failure_severities().toUpperCase());
        }
        blackDuckParameters.put(ApplicationConstants.BLACKDUCK_SCAN_FULL_KEY, scanStep.getBlackduck_scan_full());
        blackDuckParameters.put(ApplicationConstants.BLACKDUCK_AUTOMATION_FIXPR_KEY, scanStep.getBlackduck_automation_fixpr());
        blackDuckParameters.put(ApplicationConstants.BLACKDUCK_AUTOMATION_PRCOMMENT_KEY, scanStep.getBlackduck_automation_prcomment());

        return blackDuckParameters;
    }

    private static Map<String, Object> prepareCoverityParametersMap(SecurityScanStep scanStep) {
        Map<String, Object> coverityParameters = new HashMap<>();

        if (!Utility.isStringNullOrBlank(scanStep.getCoverity_connect_url())) {
            coverityParameters.put(ApplicationConstants.COVERITY_CONNECT_URL_KEY, scanStep.getCoverity_connect_url());
        }
        if (!Utility.isStringNullOrBlank(scanStep.getCoverity_connect_user_name())) {
            coverityParameters.put(ApplicationConstants.COVERITY_CONNECT_USER_NAME_KEY, scanStep.getCoverity_connect_user_name());
        }
        if (!Utility.isStringNullOrBlank(scanStep.getCoverity_connect_user_password())) {
            coverityParameters.put(ApplicationConstants.COVERITY_CONNECT_USER_PASSWORD_KEY, scanStep.getCoverity_connect_user_password());
        }
        if (!Utility.isStringNullOrBlank(scanStep.getCoverity_connect_project_name())) {
            coverityParameters.put(ApplicationConstants.COVERITY_CONNECT_PROJECT_NAME_KEY, scanStep.getCoverity_connect_project_name());
        }
        if (!Utility.isStringNullOrBlank(scanStep.getCoverity_connect_stream_name())) {
            coverityParameters.put(ApplicationConstants.COVERITY_CONNECT_STREAM_NAME_KEY, scanStep.getCoverity_connect_stream_name());
        }
        if (!Utility.isStringNullOrBlank(scanStep.getCoverity_connect_policy_view())) {
            coverityParameters.put(ApplicationConstants.COVERITY_CONNECT_POLICY_VIEW_KEY, scanStep.getCoverity_connect_policy_view());
        }
        if (!Utility.isStringNullOrBlank(scanStep.getCoverity_install_directory())) {
            coverityParameters.put(ApplicationConstants.COVERITY_INSTALL_DIRECTORY_KEY, scanStep.getCoverity_install_directory());
        }
        coverityParameters.put(ApplicationConstants.COVERITY_AUTOMATION_PRCOMMENT_KEY, scanStep.getCoverity_automation_prcomment());

        return coverityParameters;
    }

    private static Map<String, Object> prepareBridgeParametersMap(SecurityScanStep scanStep) {
        Map<String, Object> bridgeParameters = new HashMap<>();

        if (!Utility.isStringNullOrBlank(scanStep.getBridge_download_url())) {
          bridgeParameters.put(ApplicationConstants.BRIDGE_DOWNLOAD_URL, scanStep.getBridge_download_url());
        }
        if (!Utility.isStringNullOrBlank(scanStep.getBridge_download_version())) {
          bridgeParameters.put(ApplicationConstants.BRIDGE_DOWNLOAD_VERSION, scanStep.getBridge_download_version());
        }
        if (!Utility.isStringNullOrBlank(scanStep.getSynopsys_bridge_path())) {
          bridgeParameters.put(ApplicationConstants.BRIDGE_INSTALLATION_PATH, scanStep.getSynopsys_bridge_path());
        }
        bridgeParameters.put(ApplicationConstants.INCLUDE_DIAGNOSTICS_KEY, scanStep.getInclude_diagnostics());

        return bridgeParameters;
    }

    private static Map<String, Object> preparePolarisParametersMap(SecurityScanStep scanStep) {
        Map<String, Object> polarisParametersMap = new HashMap<>();

        if (!Utility.isStringNullOrBlank(scanStep.getBridge_polaris_serverurl())) {
            polarisParametersMap.put(ApplicationConstants.POLARIS_SERVER_URL_KEY, scanStep.getBridge_polaris_serverurl());
        }
        if (!Utility.isStringNullOrBlank(scanStep.getBridge_polaris_accesstoken())) {
            polarisParametersMap.put(ApplicationConstants.POLARIS_ACCESS_TOKEN_KEY, scanStep.getBridge_polaris_accesstoken());
        }
        if (!Utility.isStringNullOrBlank(scanStep.getBridge_polaris_application_name())) {
            polarisParametersMap.put(ApplicationConstants.POLARIS_APPLICATION_NAME_KEY, scanStep.getBridge_polaris_application_name());
        }
        if (!Utility.isStringNullOrBlank(scanStep.getBridge_polaris_project_name())) {
            polarisParametersMap.put(ApplicationConstants.POLARIS_PROJECT_NAME_KEY, scanStep.getBridge_polaris_project_name());
        }
        if (!Utility.isStringNullOrBlank(scanStep.getBridge_polaris_assessment_types())) {
            polarisParametersMap.put(ApplicationConstants.POLARIS_ASSESSMENT_TYPES_KEY, scanStep.getBridge_polaris_assessment_types());
        }
        polarisParametersMap.put(ApplicationConstants.INCLUDE_DIAGNOSTICS_KEY, scanStep.getInclude_diagnostics());

        return polarisParametersMap;
    }

}
