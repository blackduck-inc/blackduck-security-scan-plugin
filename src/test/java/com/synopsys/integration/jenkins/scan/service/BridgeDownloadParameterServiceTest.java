package com.synopsys.integration.jenkins.scan.service;

import com.synopsys.integration.jenkins.scan.bridge.BridgeDownloadParameters;
import com.synopsys.integration.jenkins.scan.global.ApplicationConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

public class BridgeDownloadParameterServiceTest {
    private BridgeDownloadParametersService bridgeDownloadParametersService;
    private  BridgeDownloadParameters bridgeDownloadParameters;

    @BeforeEach
    void setUp() {
        bridgeDownloadParameters = new BridgeDownloadParameters();
        bridgeDownloadParametersService = new BridgeDownloadParametersService();
    }

    @Test
    void isValidUrlTest() {
        String validUrl = "https://fake.url.com";
        assertTrue(bridgeDownloadParametersService.isValidUrl(validUrl));

        String ip = "https://102.118.100.102/";
        assertTrue(bridgeDownloadParametersService.isValidUrl(ip));

        String emptyUrl = "";
        assertFalse(bridgeDownloadParametersService.isValidUrl(emptyUrl));

        String invalidUrl = "invalid url";
        assertFalse(bridgeDownloadParametersService.isValidUrl(invalidUrl));
    }

    @Test
    void isValidVersionTest() {
        String validVersion = "1.2.3";
        assertTrue(bridgeDownloadParametersService.isValidVersion(validVersion));
        assertTrue(bridgeDownloadParametersService.isValidVersion("latest"));

        String invalidVersion = "x.x.x";
        assertFalse(bridgeDownloadParametersService.isValidVersion(invalidVersion));
    }

    @Test
    void isValidInstallationPathTest() {
        String os = System.getProperty("os.name").toLowerCase();
        String userHome = System.getProperty("user.home");

        String validPath = null;
        String invalidPath = null;
        if (os.contains("win")) {
            validPath = String.join("\\", userHome, ApplicationConstants.DEFAULT_DIRECTORY_NAME);
            invalidPath = String.join("\\", "\\path\\absent", ApplicationConstants.DEFAULT_DIRECTORY_NAME);
        }
        else if (os.contains("nix") || os.contains("nux") || os.contains("mac")) {
            validPath = String.join("/", userHome, ApplicationConstants.DEFAULT_DIRECTORY_NAME);
            invalidPath = String.join("/", "/path/absent", ApplicationConstants.DEFAULT_DIRECTORY_NAME);
        }

        assertTrue(bridgeDownloadParametersService.isValidInstallationPath(validPath));
        assertFalse(bridgeDownloadParametersService.isValidInstallationPath(invalidPath));
    }

    @Test
    void getBridgeDownloadParamsTest() {
        Map<String, Object> scanParams = new HashMap<>();

        String bridgeDownloadUrl = "https://myown.repo.com/release/synopsys-bridge/latest/synopsys-bridge-linux64.zip";
        scanParams.put(ApplicationConstants.BRIDGE_DOWNLOAD_VERSION, "3.0.0");
        scanParams.put(ApplicationConstants.BRIDGE_DOWNLOAD_URL, bridgeDownloadUrl );

        BridgeDownloadParameters result = bridgeDownloadParametersService.getBridgeDownloadParams(scanParams, bridgeDownloadParameters);

        assertEquals(bridgeDownloadUrl, result.getBridgeDownloadUrl());
        assertNotEquals("3.0.0", result.getBridgeDownloadVersion());

        Map<String, Object> scanParamsWithoutUrl = new HashMap<>();

        scanParamsWithoutUrl.put(ApplicationConstants.BRIDGE_DOWNLOAD_VERSION, "3.0.0");
        String bridgeDownloadUrlWithVersion = String.join("/", ApplicationConstants.BRIDGE_ARTIFACTORY_URL, "3.0.0",
                ApplicationConstants.getSynopsysBridgeZipFileName(bridgeDownloadParametersService.getPlatform(), "3.0.0"));
        BridgeDownloadParameters resultWithoutUrl = bridgeDownloadParametersService.getBridgeDownloadParams(scanParamsWithoutUrl, bridgeDownloadParameters);

        assertEquals(bridgeDownloadUrlWithVersion, resultWithoutUrl.getBridgeDownloadUrl());
    }

    @Test
    void getBridgeDownloadParamsNullTest() {
        Map<String, Object> scanParamsNull = new HashMap<>();

        BridgeDownloadParameters result = bridgeDownloadParametersService.getBridgeDownloadParams(scanParamsNull, bridgeDownloadParameters);

        assertNotNull(result);
        assertNotNull(result.getBridgeDownloadVersion());
    }
}
