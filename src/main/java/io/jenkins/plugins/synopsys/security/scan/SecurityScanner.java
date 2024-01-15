package io.jenkins.plugins.synopsys.security.scan;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.ArtifactArchiver;
import io.jenkins.plugins.synopsys.security.scan.exception.PluginExceptionHandler;
import io.jenkins.plugins.synopsys.security.scan.global.ApplicationConstants;
import io.jenkins.plugins.synopsys.security.scan.global.LogMessages;
import io.jenkins.plugins.synopsys.security.scan.global.LoggerWrapper;
import io.jenkins.plugins.synopsys.security.scan.global.enums.ReportType;
import io.jenkins.plugins.synopsys.security.scan.global.enums.SecurityProduct;
import io.jenkins.plugins.synopsys.security.scan.service.ScannerArgumentService;
import io.jenkins.plugins.synopsys.security.scan.service.diagnostics.UploadReportService;
import io.jenkins.plugins.synopsys.security.scan.service.scan.ScanParametersService;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class SecurityScanner {
    private final Run<?, ?> run;
    private final TaskListener listener;
    private final LoggerWrapper logger;
    private final Launcher launcher;
    private final FilePath workspace;
    private final EnvVars envVars;
    private final ScannerArgumentService scannerArgumentService;

    public SecurityScanner(
            Run<?, ?> run,
            TaskListener listener,
            Launcher launcher,
            FilePath workspace,
            EnvVars envVars,
            ScannerArgumentService scannerArgumentService) {
        this.run = run;
        this.listener = listener;
        this.launcher = launcher;
        this.workspace = workspace;
        this.envVars = envVars;
        this.scannerArgumentService = scannerArgumentService;
        this.logger = new LoggerWrapper(listener);
    }

    public int runScanner(Map<String, Object> scanParams, FilePath bridgeInstallationPath)
            throws PluginExceptionHandler {
        int scanner = -1;

        List<String> commandLineArgs = scannerArgumentService.getCommandLineArgs(scanParams, bridgeInstallationPath);

        logger.info("Executable command line arguments: "
                + commandLineArgs.stream()
                        .map(arg -> arg.concat(" "))
                        .collect(Collectors.joining())
                        .trim());

        try {
            logger.println();
            logger.println(
                    "******************************* %s *******************************",
                    "START EXECUTION OF SYNOPSYS BRIDGE");

            scanner = launcher.launch()
                    .cmds(commandLineArgs)
                    .envs(envVars)
                    .pwd(workspace)
                    .stdout(listener)
                    .quiet(true)
                    .join();
        } catch (Exception e) {
            logger.error(LogMessages.EXCEPTION_OCCURRED_WHILE_INVOKING_SYNOPSYS_BRIDGE, e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            logger.println(
                    "******************************* %s *******************************",
                    "END EXECUTION OF SYNOPSYS BRIDGE");

            scannerArgumentService.removeTemporaryInputJson(commandLineArgs);

            if (Objects.equals(scanParams.get(ApplicationConstants.INCLUDE_DIAGNOSTICS_KEY), true)) {
                UploadReportService uploadReportService = new UploadReportService(
                        run,
                        listener,
                        launcher,
                        envVars,
                        new ArtifactArchiver(ApplicationConstants.ALL_FILES_WILDCARD_SYMBOL));
                uploadReportService.archiveReports(
                        workspace.child(ApplicationConstants.BRIDGE_REPORT_DIRECTORY), ReportType.DIAGNOSTIC);
            }

            if (Objects.equals(scanParams.get(ApplicationConstants.BLACKDUCK_REPORTS_SARIF_CREATE_KEY), true)) {

                ScanParametersService scanParametersService = new ScanParametersService(listener);
                Set<String> scanType = scanParametersService.getSynopsysSecurityProducts(scanParams);
                boolean isBlackDuckScan = scanType.contains(SecurityProduct.BLACKDUCK.name());

                String defaultSarifReportFilePath = isBlackDuckScan
                        ? ApplicationConstants.DEFAULT_BLACKDUCK_SARIF_REPORT_FILE_PATH
                                + ApplicationConstants.SARIF_REPORT_FILENAME
                        : "";

                String customSarifReportFilePath = isBlackDuckScan
                        ? (String) scanParams.get(ApplicationConstants.BLACKDUCK_REPORTS_SARIF_FILE_PATH_KEY)
                        : "";

                String reportFilePath =
                        customSarifReportFilePath != null ? customSarifReportFilePath : defaultSarifReportFilePath;
                String reportFileName = customSarifReportFilePath != null
                        ? new File(customSarifReportFilePath).getName()
                        : ApplicationConstants.SARIF_REPORT_FILENAME;

                UploadReportService uploadReportService =
                        new UploadReportService(run, listener, launcher, envVars, new ArtifactArchiver(reportFileName));
                uploadReportService.archiveReports(workspace.child(reportFilePath), ReportType.SARIF);
            }
        }

        return scanner;
    }
}
