package io.jenkins.plugins.security.scan.service.scan;

import static org.junit.jupiter.api.Assertions.*;

import hudson.EnvVars;
import hudson.model.TaskListener;
import io.jenkins.plugins.security.scan.exception.PluginExceptionHandler;
import io.jenkins.plugins.security.scan.global.ApplicationConstants;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ScanParametersServiceTest {
    private ScanParametersService scanParametersService;
    private final TaskListener listenerMock = Mockito.mock(TaskListener.class);
    private final EnvVars envVarsMock = Mockito.mock(EnvVars.class);

    @BeforeEach
    void setUp() {
        scanParametersService = new ScanParametersService(listenerMock);
        Mockito.when(listenerMock.getLogger()).thenReturn(Mockito.mock(PrintStream.class));
    }

    @Test
    void performScanParameterValidationSuccessForBlackDuckTest() throws PluginExceptionHandler {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ApplicationConstants.PRODUCT_KEY, "blackducksca");
        parameters.put(ApplicationConstants.BLACKDUCKSCA_URL_KEY, "https://fake.blackduck.url");
        parameters.put(ApplicationConstants.BLACKDUCKSCA_TOKEN_KEY, "MDJDSROSVC56FAKEKEY");

        assertTrue(scanParametersService.performScanParameterValidation(parameters, envVarsMock));
    }

    @Test
    void performScanParameterValidationFailureForBlackDuckAndPolarisTest() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ApplicationConstants.PRODUCT_KEY, "blackduck, polaris");
        parameters.put(ApplicationConstants.BLACKDUCKSCA_URL_KEY, "https://fake.blackduck.url");
        parameters.put(ApplicationConstants.BLACKDUCKSCA_TOKEN_KEY, "MDJDSROSVC56FAKEKEY");

        assertThrows(
                PluginExceptionHandler.class,
                () -> scanParametersService.performScanParameterValidation(parameters, envVarsMock));
    }

    @Test
    void performScanParameterValidationSuccessForBlackDuckAndPolarisTest() throws PluginExceptionHandler {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ApplicationConstants.PRODUCT_KEY, "blackduck, polaris");

        parameters.put(ApplicationConstants.BLACKDUCKSCA_URL_KEY, "https://fake.blackduck.url");
        parameters.put(ApplicationConstants.BLACKDUCKSCA_TOKEN_KEY, "MDJDSROSVC56FAKEKEY");

        parameters.put(ApplicationConstants.POLARIS_SERVER_URL_KEY, "https://fake.polaris.url");
        parameters.put(ApplicationConstants.POLARIS_ACCESS_TOKEN_KEY, "MDJDSRRTRDFYJGH66FAKEKEY");
        parameters.put(ApplicationConstants.POLARIS_APPLICATION_NAME_KEY, "test-application");
        parameters.put(ApplicationConstants.POLARIS_PROJECT_NAME_KEY, "test-project");
        parameters.put(ApplicationConstants.POLARIS_ASSESSMENT_TYPES_KEY, "SCA, SAST");
        parameters.put(ApplicationConstants.POLARIS_BRANCH_NAME_KEY, "test-branch");

        assertTrue(scanParametersService.performScanParameterValidation(parameters, envVarsMock));
    }

    @Test
    void performScanParameterValidationSuccessForSrmTest() throws PluginExceptionHandler {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ApplicationConstants.PRODUCT_KEY, "srm");
        parameters.put(ApplicationConstants.SRM_URL_KEY, "https://fake.srm.url");
        parameters.put(ApplicationConstants.SRM_APIKEY_KEY, "MDJDSROSVC56FAKEKEY");
        parameters.put(ApplicationConstants.SRM_ASSESSMENT_TYPES_KEY, "SCA");
        parameters.put(ApplicationConstants.SRM_PROJECT_NAME_KEY, "test-project");
        parameters.put(ApplicationConstants.SRM_PROJECT_ID_KEY, "fake-id");
        parameters.put(ApplicationConstants.SRM_BRANCH_NAME_KEY, "test");
        parameters.put(ApplicationConstants.SRM_BRANCH_PARENT_KEY, "main");

        assertTrue(scanParametersService.performScanParameterValidation(parameters, envVarsMock));
    }

    @Test
    void performScanParameterValidationFailureForSrmTest() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ApplicationConstants.PRODUCT_KEY, "srm");
        parameters.put(ApplicationConstants.SRM_URL_KEY, "https://fake.srm.url");

        assertThrows(
                PluginExceptionHandler.class,
                () -> scanParametersService.performScanParameterValidation(parameters, envVarsMock));
    }

    @Test
    public void getSynopsysSecurityPlatformsTest() {
        Map<String, Object> scanParametersWithMultiplePlatforms = new HashMap<>();
        Map<String, Object> scanParametersWithSinglePlatform = new HashMap<>();
        scanParametersWithMultiplePlatforms.put(ApplicationConstants.PRODUCT_KEY, "blackduck, polaris");
        scanParametersWithSinglePlatform.put(ApplicationConstants.PRODUCT_KEY, "");

        Set<String> multiplePlatforms = scanParametersService.getSecurityProducts(scanParametersWithMultiplePlatforms);
        Set<String> singlePlatform = scanParametersService.getSecurityProducts(scanParametersWithSinglePlatform);

        assertEquals(2, multiplePlatforms.size());
        assertEquals(1, singlePlatform.size());
    }
}
