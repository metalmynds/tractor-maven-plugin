package io.metalmynds.tractor.runner;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.devicefarm.model.*;
import com.amazonaws.util.StringUtils;
import io.metalmynds.tractor.AWSDeviceFarm;
import io.metalmynds.tractor.AWSDeviceFarmException;
import io.metalmynds.tractor.frameworks.*;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * Goal which executes amazon device farm tests.
 */
@Mojo(name = "drive", defaultPhase = LifecyclePhase.INTEGRATION_TEST)
public class DeviceFarmRunner
        extends AbstractMojo {
    /**
     * Project Build Directory
     */
    @Parameter(defaultValue = "${project.build.directory}", readonly = true)
    private String projectBuildDir;

    /**
     * Name of Device Farm Project.
     */
    @Parameter(required = true)
    private String projectName;

    /**
     * Version of Appium.
     */
    @Parameter(defaultValue = "1.6.5")
    private String appiumVersion;

    /**
     * Framework Type BUILTIN_EXPLORER, APPIUM_JAVA_JUNIT, APPIUM_JAVA_TESTNG, APPIUM_PYTHON, APPIUM_WEB_JAVA_JUNIT, APPIUM_WEB_JAVA_TESTNG, APPIUM_WEB_PYTHON, CALABASH, INSTRUMENTATION, UIAUTOMATION, UIAUTOMATOR, XCTEST, XCTEST_UI
     */
    @Parameter(required = true)
    private TestType testFrameworkType;

    /**
     * Filename of the Zipped Tests generated by Maven.
     */
    @Parameter(required = true)
    private String uploadTestFilename;

    /**
     * Device Pool Name.
     */
    @Parameter(required = true)
    private String devicePoolName;

    /**
     * Filename of the System Under Test Package Android or iOS to upload.
     */
    @Parameter(required = true)
    private String uploadApplicationFilename;

    /**
     * AWS Role ARN
     */
    @Parameter
    private String awsRoleArn;

    /**
     * AWS Access Key
     */
    @Parameter
    private String awsAccessKey;

    /**
     * AWS Secret Key
     */
    @Parameter
    private String awsSecretKey;

    /**
     * Filename of a file containing test data to upload.
     */
    @Parameter
    private String uploadTestDataFilename;

    /**
     * NOT IMPLEMENTED Filenames of auxiliary Packages Android or iOS to upload.
     */
    @Parameter // NOT IMPLEMENTED
    private String uploadAuxiliaryFilenames;

    /**
     * Device Wifi Enabled
     */
    @Parameter(defaultValue = "true")
    private Boolean deviceStateWifi;

    /**
     * Device Bluetooth Enabled
     */
    @Parameter(defaultValue = "false")
    private Boolean deviceStateBluetooth;

    /**
     * Device GPS Enabled
     */
    @Parameter(defaultValue = "false")
    private Boolean deviceStateGps;

    /**
     * Device NFC Enabled
     */
    @Parameter(defaultValue = "true")
    private Boolean deviceStateNfc;

    /**
     * Device Latitude
     */
    @Parameter(defaultValue = "47.6204")
    private Double deviceStateLatitude;

    /**
     * Device Longitude
     */
    @Parameter(defaultValue = "-122.3491")
    private Double deviceStateLongitude;

    /**
     * Device Locale en_US
     */
    @Parameter(defaultValue = "en_US")
    private String deviceStateLocale;

    /**
     * Prefix to Test Run Name Generated.
     */
    @Parameter(defaultValue = "Run")
    private String runNamePrefix;

    /**
     * Test Run Timeout in Minutes.
     */
    @Parameter(required = true)
    private Integer runTimeoutMinutes;

    /**
     * Billing Method
     */
    @Parameter(defaultValue = "METERED")
    private String runBillingMethod;

    /**
     * Comma separated list of paths to retrieve artifacts from host machine. E.g $WORKING_DIRECTORY/report/fooa.txt,$WORKING_DIRECTORY/foob.txt
     */
    @Parameter(defaultValue = "$WORKING_DIRECTORY")
    private String hostArtifactPaths;

    /**
     * Comma separated list of paths to retrieve artifacts from iOS device.
     */
    @Parameter
    private String deviceiOSArtifactPaths;

    /**
     * Comma separated list of paths to retrieve artifacts from Android device.
     */
    @Parameter
    private String deviceAndroidArtifactPaths;

    /**
     * Type of Network profile.
     */
    @Parameter(defaultValue = "PRIVATE")
    private NetworkProfileType deviceNetworkProfileType;

    /**
     * Name of Network profile to use.
     */
    @Parameter(defaultValue = "FULL")
    private String deviceNetworkProfileName;

    /**
     * Fuzz Event Count value.
     */
    @Parameter
    private String frameworkFuzzEventCount;

    /**
     * Fuzz Throttle value.
     */
    @Parameter
    private String frameworkFuzzThrottle;

    /**
     * Fuzz Seed value.
     */
    @Parameter
    private String frameworkFuzzSeed;

    /**
     * Explorer Username.
     */
    @Parameter
    private String frameworkExplorerUsername;

    /**
     * Explorer Password.
     */
    @Parameter
    private String frameworkExplorerPassword;

//    /**
//     * Tests to Execute using the JUnit, Python, TestNG frameworks both Web and Native.
//     */
//    @Parameter
//    private String frameworkAppiumTests;

    /**
     * Calabash Features to execute.
     */
    @Parameter
    private String frameworkCalabashFeatures;

    /**
     * Calabash Tags to use.
     */
    @Parameter
    private String frameworkCalabashTags;

    /**
     * Calabash Profile to use.
     */
    @Parameter
    private String frameworkCalabashProfile;

    /**
     * Instrumentation JUnit Artifact to use.
     */
    @Parameter
    private String frameworkInstrumentationJUnitArtifact;

    /**
     * Instrumentation JUnit Filter to apply.
     */
    @Parameter
    private String frameworkInstrumentationJUnitFilter;

    /**
     * UIAutomator Artifact to use.
     */
    @Parameter
    private String frameworkUIAutomatorArtifact;

    /**
     * UIAutomator Filter to apply.
     */
    @Parameter
    private String frameworkUIAutomatorFilter;

    /**
     * UIAutomation Artifact to use.
     */
    @Parameter
    private String frameworkUIAutomationArtifact;

    /**
     * XCTest Artifact to use.
     */
    @Parameter
    private String frameworkXCTestArtifact;

    /**
     * XCTest Filter to apply.
     */
    @Parameter
    private String frameworkXCTestFilter;

    /**
     * XCTest UI Artifact to use.
     */
    @Parameter
    private String frameworkXCTestUIArtifact;

    /**
     * XCTest UI Filter to apply.
     */
    @Parameter
    private String frameworkXCTestUIFilter;

    /**
     * Tractor Test Driver Pooling Interval.
     */
    @Parameter(defaultValue = "5000")
    private long statusPollingInterval;

    /**
     * Tractor Test Driver Pooling Interval.
     */
    @Parameter(defaultValue = "/artifacts")
    private String downloadArtifactPath;

    public void execute()
            throws MojoExecutionException {

        displayBanner();

        AWSDeviceFarm client;
        AWSCredentials credentials;

        getLog().info(String.format("Test Framework %s", testFrameworkType.toString()));

        if (testFrameworkType != TestType.BUILTIN_FUZZ && testFrameworkType != TestType.BUILTIN_EXPLORER) {
            getLog().info(String.format("Appium Version %s", appiumVersion));
        }

        getLog().info("Connecting to Device Farm");

        if (!StringUtils.isNullOrEmpty(awsRoleArn)) {
            client = new AWSDeviceFarm(awsRoleArn);
        } else {

            if (!StringUtils.isNullOrEmpty(awsAccessKey) && !StringUtils.isNullOrEmpty(awsSecretKey)) {
                credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
            } else {
                credentials = new DefaultAWSCredentialsProviderChain().getCredentials();
            }

            client = new AWSDeviceFarm(credentials);
        }

        getLog().info(String.format("Locating Project %s", projectName));

        Project project;

        try {
            project = client.getProject(projectName);
            getLog().debug(String.format("Project Arn: %s", project.getArn()));
        } catch (Exception ex) {
            throw new MojoExecutionException(String.format("Locate Device Farm Project '%s' failed!", projectName), ex);
        }

        // Setup Device Pool

        DevicePool devicePool;

        try {

            devicePool = client.getDevicePool(project, devicePoolName);

            getLog().info(String.format("Located Device Pool %s", devicePoolName));

        } catch (AWSDeviceFarmException ex) {
            throw new MojoExecutionException(String.format("Finding Device Pool '%s' Failed!", devicePoolName), ex);
        }

        // Verify test package exists before starting upload of application.

        try {
            if (testFrameworkType != TestType.BUILTIN_FUZZ && testFrameworkType != TestType.BUILTIN_EXPLORER) {
                if (!StringUtils.isNullOrEmpty(uploadTestFilename)) {
                    if (!Files.exists(Paths.get(uploadTestFilename))) {
                        throw new FileNotFoundException(uploadTestFilename);
                    }
                } else {
                    throw new RuntimeException("uploadTestFilename configuration property is not set!");
                }
            }
        } catch (Exception ex) {
            throw new MojoExecutionException("Upload Test Package Failed!", ex);
        }

        // Upload Application Under Test

        Upload appUpload = null;

        try {
            if (Files.exists(Paths.get(uploadApplicationFilename))) {
                getLog().info(String.format("Uploading Application %s", uploadApplicationFilename));
                appUpload = client.uploadApp(project, uploadApplicationFilename);
                getLog().debug(String.format("Upload Application Arn: %s", appUpload.getArn()));
            } else {
                throw new FileNotFoundException(uploadApplicationFilename);
            }
        } catch (Exception ex) {
            throw new MojoExecutionException("Upload Application Package Failed!", ex);
        }

        // Begin Scheduling Run

        ScheduleRunTest testSchedule = new ScheduleRunTest();

        try {

            switch (testFrameworkType) {

                case BUILTIN_FUZZ: {
                    Map<String, String> parameters = new HashMap<String, String>();

                    if (frameworkFuzzEventCount != null && !frameworkFuzzEventCount.isEmpty()) {
                        parameters.put("event_count", frameworkFuzzEventCount);
                    }

                    if (frameworkFuzzThrottle != null && !frameworkFuzzThrottle.isEmpty()) {
                        parameters.put("throttle", frameworkFuzzThrottle);
                    }

                    if (frameworkFuzzSeed != null && !frameworkFuzzSeed.isEmpty()) {
                        parameters.put("seed", frameworkFuzzSeed);
                    }

                    testSchedule
                            .withType(testFrameworkType)
                            .withParameters(parameters);
                    break;
                }

                case BUILTIN_EXPLORER: {
                    Map<String, String> parameters = new HashMap<String, String>();

                    if (frameworkExplorerUsername != null && !frameworkExplorerUsername.isEmpty()) {
                        parameters.put("username", frameworkExplorerUsername);
                    }

                    if (frameworkExplorerPassword != null && !frameworkExplorerPassword.isEmpty()) {
                        parameters.put("password", frameworkExplorerPassword);
                    }

                    testSchedule
                            .withType(testFrameworkType)
                            .withParameters(parameters);
                    break;
                }

                case APPIUM_JAVA_JUNIT: {
                    AppiumJavaJUnitTest test = new AppiumJavaJUnitTest.Builder()
                            .withTests(uploadTestFilename)
                            .build();

                    getLog().info(String.format("Uploading Test Package %s", uploadTestFilename));

                    Upload upload = client.uploadTest(project, test);

                    testSchedule
                            .withType(testFrameworkType)
                            .withTestPackageArn(upload.getArn());

                    testSchedule.addParametersEntry("appium_version", appiumVersion);
                    break;
                }

                case APPIUM_JAVA_TESTNG: {
                    AppiumJavaTestNGTest test = new AppiumJavaTestNGTest.Builder()
                            .withTests(uploadTestFilename)
                            .build();

                    getLog().info(String.format("Uploading Test Package %s", uploadTestFilename));

                    Upload upload = client.uploadTest(project, test);

                    testSchedule
                            .withType(testFrameworkType)
                            .withTestPackageArn(upload.getArn());

                    testSchedule.addParametersEntry("appium_version", appiumVersion);
                    break;
                }

                case APPIUM_PYTHON: {
                    AppiumPythonTest test = new AppiumPythonTest.Builder()
                            .withTests(uploadTestFilename)
                            .build();

                    getLog().info(String.format("Uploading Test Package %s", uploadTestFilename));

                    Upload upload = client.uploadTest(project, test);

                    testSchedule
                            .withType(testFrameworkType)
                            .withTestPackageArn(upload.getArn());

                    testSchedule.addParametersEntry("appium_version", appiumVersion);
                    break;
                }

                case APPIUM_WEB_JAVA_JUNIT: {
                    AppiumWebJavaJUnitTest test = new AppiumWebJavaJUnitTest.Builder()
                            .withTests(uploadTestFilename)
                            .build();

                    getLog().info(String.format("Uploading Test Package %s", uploadTestFilename));

                    Upload upload = client.uploadTest(project, test);

                    testSchedule
                            .withType(testFrameworkType)
                            .withTestPackageArn(upload.getArn());

                    testSchedule.addParametersEntry("appium_version", appiumVersion);
                    break;
                }

                case APPIUM_WEB_JAVA_TESTNG: {
                    AppiumWebJavaTestNGTest test = new AppiumWebJavaTestNGTest.Builder()
                            .withTests(uploadTestFilename)
                            .build();

                    getLog().info(String.format("Uploading Test Package %s", uploadTestFilename));

                    Upload upload = client.uploadTest(project, test);

                    testSchedule
                            .withType(testFrameworkType)
                            .withTestPackageArn(upload.getArn());

                    testSchedule.addParametersEntry("appium_version", appiumVersion);
                    break;
                }

                case APPIUM_WEB_PYTHON: {
                    AppiumWebPythonTest test = new AppiumWebPythonTest.Builder()
                            .withTests(uploadTestFilename)
                            .build();

                    getLog().info(String.format("Uploading Test Package %s", uploadTestFilename));

                    Upload upload = client.uploadTest(project, test);

                    testSchedule
                            .withType(testFrameworkType)
                            .withTestPackageArn(upload.getArn());

                    testSchedule.addParametersEntry("appium_version", appiumVersion);
                    break;
                }

                case CALABASH: {
                    CalabashTest test = new CalabashTest.Builder()
                            .withFeatures(frameworkCalabashFeatures)
                            .withTags(frameworkCalabashTags)
                            .withProfile(frameworkCalabashProfile)
                            .build();

                    getLog().info(String.format("Uploading Test Package %s", uploadApplicationFilename));

                    Upload upload = client.uploadTest(project, test);

                    Map<String, String> parameters = new HashMap<String, String>();
                    if (test.getTags() != null && !test.getTags().isEmpty()) {
                        parameters.put("tags", test.getTags());
                    }
                    if (test.getProfile() != null && !test.getProfile().isEmpty()) {
                        parameters.put("profile", test.getProfile());
                    }
                    testSchedule
                            .withType(testFrameworkType)
                            .withTestPackageArn(upload.getArn())
                            .withParameters(parameters);
                    break;
                }

                case INSTRUMENTATION: {
                    InstrumentationTest test = new InstrumentationTest.Builder()
                            .withArtifact(frameworkInstrumentationJUnitArtifact)
                            .withFilter(frameworkInstrumentationJUnitFilter)
                            .build();

                    getLog().info(String.format("Uploading Test Package %s", uploadApplicationFilename));

                    Upload upload = client.uploadTest(project, test);

                    testSchedule
                            .withType(testFrameworkType)
                            .withTestPackageArn(upload.getArn())
                            .withParameters(new HashMap<String, String>())
                            .withFilter(test.getFilter());
                    break;
                }

                case UIAUTOMATOR: {
                    UIAutomatorTest test = new UIAutomatorTest.Builder()
                            .withTests(frameworkUIAutomatorArtifact)
                            .withFilter(frameworkUIAutomatorFilter)
                            .build();

                    getLog().info(String.format("Uploading Test Package %s", uploadApplicationFilename));

                    Upload upload = client.uploadTest(project, test);

                    testSchedule
                            .withType(testFrameworkType)
                            .withTestPackageArn(upload.getArn())
                            .withParameters(new HashMap<String, String>())
                            .withFilter(test.getFilter());

                    break;
                }

                case UIAUTOMATION: {
                    UIAutomationTest test = new UIAutomationTest.Builder()
                            .withTests(frameworkUIAutomationArtifact)
                            .build();

                    getLog().info(String.format("Uploading Test Package %s", uploadApplicationFilename));

                    Upload upload = client.uploadTest(project, test);

                    testSchedule
                            .withType(testFrameworkType)
                            .withTestPackageArn(upload.getArn());
                    break;
                }

                case XCTEST: {
                    XCTestTest test = new XCTestTest.Builder()
                            .withTests(frameworkXCTestArtifact)
                            .withFilter(frameworkXCTestFilter)
                            .build();

                    getLog().info(String.format("Uploading Test Package %s", uploadApplicationFilename));

                    Upload upload = client.uploadTest(project, test);

                    testSchedule
                            .withType(testFrameworkType)
                            .withFilter(test.getFilter())
                            .withTestPackageArn(upload.getArn());
                    break;
                }

                case XCTEST_UI: {
                    XCTestUITest test = new XCTestUITest.Builder()
                            .withTests(frameworkXCTestUIArtifact)
                            .withFilter(frameworkXCTestUIFilter)
                            .build();

                    getLog().info(String.format("Uploading Test Package %s", uploadApplicationFilename));

                    Upload upload = client.uploadTest(project, test);

                    testSchedule
                            .withType(testFrameworkType)
                            .withFilter(test.getFilter())
                            .withTestPackageArn(upload.getArn());
                    break;
                }


            }
        } catch (Exception ex) {
            throw new MojoExecutionException("Upload Test Package Failed!", ex);
        }


        // Setup Customer Artifacts

        CustomerArtifactPaths customerArtifactPaths = new CustomerArtifactPaths();

        if (!StringUtils.isNullOrEmpty(hostArtifactPaths)) {
            customerArtifactPaths.setDeviceHostPaths(Arrays.asList(hostArtifactPaths.split(",")));
        }

        if (!StringUtils.isNullOrEmpty(deviceiOSArtifactPaths)) {
            customerArtifactPaths.setIosPaths(Arrays.asList(deviceiOSArtifactPaths.split(",")));
        }

        if (!StringUtils.isNullOrEmpty(deviceAndroidArtifactPaths)) {
            customerArtifactPaths.setAndroidPaths(Arrays.asList(deviceAndroidArtifactPaths.split(",")));
        }

        // Start Run Configuration

        ScheduleRunConfiguration runConfiguration = new ScheduleRunConfiguration();

        runConfiguration.setBillingMethod(runBillingMethod);

        // Upload Test Data

        if (!StringUtils.isNullOrEmpty(uploadTestDataFilename)) {

            String testData;

            try {

                if (Files.exists(Paths.get(uploadTestDataFilename))) {
                    testData = new String(Files.readAllBytes(Paths.get(uploadTestDataFilename)));
                } else {
                    throw new FileNotFoundException(uploadTestDataFilename);
                }

                Upload extraData = client.uploadExtraData(project, testData);

                runConfiguration.setExtraDataPackageArn(extraData.getArn());

            } catch (Exception ex) {
                throw new MojoExecutionException(String.format("Failed Uploading Test Data from %s", uploadTestDataFilename), ex);
            }

        }

        // Setup Device State

        Location location = new Location();

        location.setLatitude(deviceStateLatitude);

        location.setLongitude(deviceStateLongitude);

        runConfiguration.setLocation(location);

        Radios radio = new Radios();

        radio.setWifi(deviceStateWifi);

        radio.setBluetooth(deviceStateBluetooth);

        radio.setGps(deviceStateGps);

        radio.setNfc(deviceStateNfc);

        runConfiguration.setRadios(radio);

        // Setup Network Profile

        NetworkProfile networkProfile = new NetworkProfile().withType(deviceNetworkProfileType).withName(deviceNetworkProfileName);

        runConfiguration.setNetworkProfileArn(networkProfile.getArn());

        runConfiguration.setCustomerArtifactPaths(customerArtifactPaths);

        // Begin Execution of Tests

        String runName = String.format("%s_%s", runNamePrefix, UUID.randomUUID().toString());

        getLog().info(String.format("Starting Execution of %s", runName));

        ScheduleRunResult runResult = client.scheduleRun(project.getArn(), runName, testSchedule.getTestPackageArn(), devicePool.getArn(), testSchedule, runTimeoutMinutes, runConfiguration);

        getLog().debug(String.format("Run Result Arn: %s", runResult.getRun().getArn()));

        ExecutionStatus status = null;

        // Wait for Run to Complete

        while (true) {

            GetRunResult latestRunResult = client.describeRun(runResult.getRun().getArn());

            Run run = latestRunResult.getRun();

            ExecutionStatus latestStatus = ExecutionStatus.fromValue(run.getStatus());

            if (status == null || status != latestStatus) {

                getLog().info(String.format("Run Status Updated to %s at %s", run.getStatus(), new Date().toString()));

                status = latestStatus;

            }

            if (status == ExecutionStatus.COMPLETED) {
                break;
            }
            try {
                Thread.sleep(statusPollingInterval);
            } catch (InterruptedException ex) {
                throw new MojoExecutionException(String.format("Run Polling Thread interrupted while waiting for the Run to complete!"));
            }
        }

        DeviceMinutes minutesUsed = runResult.getRun().getDeviceMinutes();

        getLog().info(String.format("Device Metered Usage %s Minute(s)", minutesUsed == null ? "0" : minutesUsed.getMetered() ));

        File outputDirectory = new File(projectBuildDir, downloadArtifactPath);

        getLog().info(String.format("Downloading Run Artifacts into %s", outputDirectory.getAbsolutePath()));

        String runArn = runResult.getRun().getArn();

        try {
            Map<String, File> files = client.getArtifacts(runArn, outputDirectory);

            getLog().info(String.format("Downloaded %s Run Artifact File(s)", files.size()));

            if (getLog().isDebugEnabled()) {
                for (String filename : files.keySet()) {
                    getLog().debug(String.format("Downloaded Artifact %s", files.get(filename).getAbsolutePath()));
                }
            }
        } catch (Exception ex) {
            throw new MojoExecutionException(String.format("Error Downloading Test Run Artifacts! Run Arn: %s", runArn), ex);
        }

        getLog().info("Execution Exited Normally");
    }

    private void displayBanner() {
        System.out.println(String.format("        ,----,                                                        \n      ,/   .`|                                                        \n    ,`   .'  :                              ___                       \n  ;    ;     /                            ,--.'|_                     \n.'___,/    ,' __  ,-.                     |  | :,'   ,---.    __  ,-. \n|    :     |,' ,'/ /|                     :  : ' :  '   ,'\\ ,' ,'/ /| \n;    |.';  ;'  | |' | ,--.--.     ,---. .;__,'  /  /   /   |'  | |' | \n`----'  |  ||  |   ,'/       \\   /     \\|  |   |  .   ; ,. :|  |   ,' \n    '   :  ;'  :  / .--.  .-. | /    / ':__,'| :  '   | |: :'  :  /   \n    |   |  '|  | '   \\__\\/: . ..    ' /   '  : |__'   | .; :|  | '    \n    '   :  |;  : |   ,\" .--.; |'   ; :__  |  | '.'|   :    |;  : |    \n    ;   |.' |  , ;  /  /  ,.  |'   | '.'| ;  :    ;\\   \\  / |  , ;    \n    '---'    ---'  ;  :   .'   \\   :    : |  ,   /  `----'   ---'     \n                   |  ,     .-./\\   \\  /   ---`-'                     \n                    `--`---'     `----'                               \n                                                                      \n"));
    }
}
