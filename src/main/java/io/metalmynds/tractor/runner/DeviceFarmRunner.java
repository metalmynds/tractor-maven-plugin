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
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.devicefarm.model.*;
import com.amazonaws.util.StringUtils;
import io.metalmynds.tractor.AWSDeviceFarm;
import io.metalmynds.tractor.AWSDeviceFarmException;
import io.metalmynds.tractor.frameworks.*;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Goal which touches a timestamp file.
 *
 * @goal touch
 * @phase process-sources
 */
public class DeviceFarmRunner
        extends AbstractMojo {
    /**
     * Location of the file.
     *
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File outputDirectory;

    @Parameter(name = "project.name", required = true)
    private String projectName;

    @Parameter(name = "appium.framework.version", defaultValue = "1.6.5")
    private String appiumVersion;

    @Parameter(name = "test.framework.type", required = true)
    private TestType frameworkType;

    @Parameter(name = "upload.test.filename", required = true)
    private String testFilename;

    @Parameter(name = "device.pool.name", required = true)
    private String devicePoolName;

    @Parameter(name = "upload.application.filename", required = true)
    private String applicationFilename;

    @Parameter(name = "aws.role.arn")
    private String roleArn;

    @Parameter(name = "upload.test.data.filename")
    private String testDataFilename;

    @Parameter(name = "upload.auxiliary.filenames") // NOT IMPLEMENTED
    private String auxiliaryFilenames;

    @Parameter(name = "device.state.wifi", defaultValue = "true")
    private Boolean deviceWifi;

    @Parameter(name = "device.state.bluetooth", defaultValue = "false")
    private Boolean deviceBluetooth;

    @Parameter(name = "device.state.gps", defaultValue = "false")
    private Boolean deviceGps;

    @Parameter(name = "device.state.nfc", defaultValue = "true")
    private Boolean deviceStateNfc;

    @Parameter(name = "device.state.latitude", defaultValue = "47.6204")
    private Double deviceLatitude;

    @Parameter(name = "device.state.longitude", defaultValue = "-122.3491")
    private Double deviceLongitude;

    @Parameter(name = "device.state.locale", defaultValue = "en_US")
    private String deviceLocale;

    @Parameter(name = "run.name.prefix", defaultValue = "Run")
    private String runNamePrefix;

    @Parameter(name = "run.timeout.minutes", required = true)
    private Integer runTimeoutMinutes;

    @Parameter(name = "run.billing.method", defaultValue = "METERED")
    private String runBillingMethod;

    @Parameter(name = "host.artifact.paths", defaultValue = "$WORKING_DIRECTORY")
    private String hostArtifactPaths;

    @Parameter(name = "device.ios.artifact.paths")
    private String deviceiOSArtifactPaths;

    @Parameter(name = "device.android.artifact.paths")
    private String deviceAndroidArtifactPaths;

    @Parameter(name = "device.network.profile.type", defaultValue = "PRIVATE")
    private NetworkProfileType deviceNetworkProfileType;

    @Parameter(name = "device.network.profile.name", defaultValue = "FULL")
    private String deviceNetworkProfileName;

    @Parameter(name = "framework.fuzz.event.count")
    private String frameworkFuzzEventCount;

    @Parameter(name = "framework.fuzz.throttle")
    private String frameworkFuzzThrottle;

    @Parameter(name = "framework.fuzz.seed")
    private String frameworkFuzzSeed;

    @Parameter(name = "framework.explorer.username")
    private String frameworkExplorerUsername;

    @Parameter(name = "framework.explorer.password")
    private String frameworkExplorerPassword;

    @Parameter(name = "framework.appium.version", defaultValue = "1.6.5")
    private String frameworkAppiumVersion;

    @Parameter(name = "framework.appium.tests")
    private String frameworkAppiumTests;

    @Parameter(name = "framework.calabash.features")
    private String frameworkCalabashFeatures;

    @Parameter(name = "framework.calabash.tags")
    private String frameworkCalabashTags;

    @Parameter(name = "framework.calabash.profile")
    private String frameworkCalabashProfile;

    @Parameter(name = "framework.instrumentation.junit.artifact")
    private String frameworkInstrumentationjUnitArtifact;

    @Parameter(name = "framework.instrumentation.junit.filter")
    private String frameworkInstrumentationjUnitFilter;

    @Parameter(name = "framework.uiautomator.artifact")
    private String frameworkUIAutomatorArtifact;

    @Parameter(name = "framework.uiautomator.filter")
    private String frameworkUIAutomatorFilter;

    @Parameter(name = "framework.uiautomation.artifact")
    private String frameworkUIAutomationArtifact;

    @Parameter(name = "framework.xctest.artifact")
    private String frameworkXCTestArtifact;

    @Parameter(name = "framework.xctest.filter")
    private String frameworkXCTestFilter;

    @Parameter(name = "framework.xctest.ui.artifact")
    private String frameworkXCTestUIArtifact;

    @Parameter(name = "framework.xctest.ui.filter")
    private String frameworkXCTestUIFilter;


    public void execute()
            throws MojoExecutionException {
        AWSDeviceFarm client;
        AWSCredentials credentials;

        if (StringUtils.isNullOrEmpty(roleArn)) {
            client = new AWSDeviceFarm(roleArn);
        } else {
            credentials = (AWSCredentials) new EnvironmentVariableCredentialsProvider();
            client = new AWSDeviceFarm(credentials);
        }

        Project project = null;

        try {
            project = client.getProject(projectName);
            getLog().debug(String.format("Device Farm Project Arn: %s", project.getArn()));
        } catch (Exception ex) {
            throw new MojoExecutionException(String.format("Get Device Farm Project '%s' failed!", projectName), ex);
        }

        Upload appUpload = null;

        try {
            if (!StringUtils.isNullOrEmpty(applicationFilename)) {
                if (FileUtils.fileExists(applicationFilename)) {
                    appUpload = client.uploadApp(project, applicationFilename);
                    getLog().debug(String.format("Upload Mobile Application Arn: %s", appUpload.getArn()));
                } else {
                    throw new FileNotFoundException(applicationFilename);
                }
            }
        } catch (Exception ex) {
            throw new MojoExecutionException("Upload System Under Test Package Failed!", ex);
        }

        ScheduleRunTest testSchedule = new ScheduleRunTest();

        try {

            if (!StringUtils.isNullOrEmpty(testFilename)) {

                if (Files.exists(Paths.get(testFilename))) {

                    switch (frameworkType) {

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
                                    .withType(frameworkType)
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
                                    .withType(frameworkType)
                                    .withParameters(parameters);
                            break;
                        }

                        case APPIUM_JAVA_JUNIT: {
                            AppiumJavaJUnitTest test = new AppiumJavaJUnitTest.Builder()
                                    .withTests(frameworkAppiumTests)
                                    .build();

                            Upload upload = client.uploadTest(project, test);

                            testSchedule
                                    .withType(frameworkType)
                                    .withTestPackageArn(upload.getArn());

                            testSchedule.addParametersEntry("appium_version", frameworkAppiumVersion);
                            break;
                        }

                        case APPIUM_JAVA_TESTNG: {
                            AppiumJavaTestNGTest test = new AppiumJavaTestNGTest.Builder()
                                    .withTests(frameworkAppiumTests)
                                    .build();

                            Upload upload = client.uploadTest(project, test);

                            testSchedule
                                    .withType(frameworkType)
                                    .withTestPackageArn(upload.getArn());

                            testSchedule.addParametersEntry("appium_version", frameworkAppiumVersion);
                            break;
                        }

                        case APPIUM_PYTHON: {
                            AppiumPythonTest test = new AppiumPythonTest.Builder()
                                    .withTests(frameworkAppiumTests)
                                    .build();

                            Upload upload = client.uploadTest(project, test);

                            testSchedule
                                    .withType(frameworkType)
                                    .withTestPackageArn(upload.getArn());

                            testSchedule.addParametersEntry("appium_version", frameworkAppiumVersion);
                            break;
                        }

                        case APPIUM_WEB_JAVA_JUNIT: {
                            AppiumWebJavaJUnitTest test = new AppiumWebJavaJUnitTest.Builder()
                                    .withTests(frameworkAppiumTests)
                                    .build();

                            Upload upload = client.uploadTest(project, test);

                            testSchedule
                                    .withType(frameworkType)
                                    .withTestPackageArn(upload.getArn());

                            testSchedule.addParametersEntry("appium_version", frameworkAppiumVersion);
                            break;
                        }

                        case APPIUM_WEB_JAVA_TESTNG: {
                            AppiumWebJavaTestNGTest test = new AppiumWebJavaTestNGTest.Builder()
                                    .withTests(frameworkAppiumTests)
                                    .build();

                            Upload upload = client.uploadTest(project, test);

                            testSchedule
                                    .withType(frameworkType)
                                    .withTestPackageArn(upload.getArn());

                            testSchedule.addParametersEntry("appium_version", frameworkAppiumVersion);
                            break;
                        }

                        case APPIUM_WEB_PYTHON: {
                            AppiumWebPythonTest test = new AppiumWebPythonTest.Builder()
                                    .withTests(frameworkAppiumTests)
                                    .build();

                            Upload upload = client.uploadTest(project, test);

                            testSchedule
                                    .withType(frameworkType)
                                    .withTestPackageArn(upload.getArn());

                            testSchedule.addParametersEntry("appium_version", frameworkAppiumVersion);
                            break;
                        }

                        case CALABASH: {
                            CalabashTest test = new CalabashTest.Builder()
                                    .withFeatures(frameworkCalabashFeatures)
                                    .withTags(frameworkCalabashTags)
                                    .withProfile(frameworkCalabashProfile)
                                    .build();

                            Upload upload = client.uploadTest(project, test);

                            Map<String, String> parameters = new HashMap<String, String>();
                            if (test.getTags() != null && !test.getTags().isEmpty()) {
                                parameters.put("tags", test.getTags());
                            }
                            if (test.getProfile() != null && !test.getProfile().isEmpty()) {
                                parameters.put("profile", test.getProfile());
                            }
                            testSchedule
                                    .withType(frameworkType)
                                    .withTestPackageArn(upload.getArn())
                                    .withParameters(parameters);
                            break;
                        }

                        case INSTRUMENTATION: {
                            InstrumentationTest test = new InstrumentationTest.Builder()
                                    .withArtifact(frameworkInstrumentationjUnitArtifact)
                                    .withFilter(frameworkInstrumentationjUnitFilter)
                                    .build();

                            Upload upload = client.uploadTest(project, test);

                            testSchedule
                                    .withType(frameworkType)
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

                            Upload upload = client.uploadTest(project, test);

                            testSchedule
                                    .withType(frameworkType)
                                    .withTestPackageArn(upload.getArn())
                                    .withParameters(new HashMap<String, String>())
                                    .withFilter(test.getFilter());

                            break;
                        }

                        case UIAUTOMATION: {
                            UIAutomationTest test = new UIAutomationTest.Builder()
                                    .withTests(frameworkUIAutomationArtifact)
                                    .build();

                            Upload upload = client.uploadTest(project, test);

                            testSchedule
                                    .withType(frameworkType)
                                    .withTestPackageArn(upload.getArn());
                            break;
                        }

                        case XCTEST: {
                            XCTestTest test = new XCTestTest.Builder()
                                    .withTests(frameworkXCTestArtifact)
                                    .withFilter(frameworkXCTestFilter)
                                    .build();

                            Upload upload = client.uploadTest(project, test);

                            testSchedule
                                    .withType(frameworkType)
                                    .withFilter(test.getFilter())
                                    .withTestPackageArn(upload.getArn());
                            break;
                        }

                        case XCTEST_UI: {
                            XCTestUITest test = new XCTestUITest.Builder()
                                    .withTests(frameworkXCTestUIArtifact)
                                    .withFilter(frameworkXCTestUIFilter)
                                    .build();

                            Upload upload = client.uploadTest(project, test);

                            testSchedule
                                    .withType(frameworkType)
                                    .withFilter(test.getFilter())
                                    .withTestPackageArn(upload.getArn());
                            break;
                        }

                    }

                } else {
                    throw new FileNotFoundException(testFilename);
                }
            }
        } catch (Exception ex) {
            throw new MojoExecutionException("Upload Test Package Failed!", ex);
        }

        ScheduleRunConfiguration runConfiguration = new ScheduleRunConfiguration();

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

        if (!StringUtils.isNullOrEmpty(testDataFilename)) {

            String testData;

            try {

                if (Files.exists(Paths.get(testDataFilename))) {
                    testData = new String(Files.readAllBytes(Paths.get(testDataFilename)));
                } else {
                    throw new FileNotFoundException(testDataFilename);
                }

                Upload extraData = client.uploadExtraData(project, testData);

                runConfiguration.setExtraDataPackageArn(extraData.getArn());

            } catch (Exception ex) {
                throw new MojoExecutionException(String.format("Failed Uploading Test Data from %s", testDataFilename), ex);
            }

        }

        DevicePool devicePool;

        try {

            devicePool = client.getDevicePool(project, devicePoolName);

        } catch (AWSDeviceFarmException ex) {
            throw new MojoExecutionException(String.format("Finding Device Pool '%s' Failed!", devicePoolName), ex);
        }

        runConfiguration.setBillingMethod(runBillingMethod);

        Location location = new Location();

        location.setLatitude(deviceLatitude);

        location.setLongitude(deviceLongitude);

        runConfiguration.setLocation(location);

        Radios radio = new Radios();

        radio.setWifi(deviceWifi);

        radio.setBluetooth(deviceBluetooth);

        radio.setGps(deviceGps);

        radio.setNfc(deviceStateNfc);

        runConfiguration.setRadios(radio);

        NetworkProfile networkProfile = new NetworkProfile().withType(deviceNetworkProfileType).withName(deviceNetworkProfileName);

        runConfiguration.setNetworkProfileArn(networkProfile.getArn());

        runConfiguration.setCustomerArtifactPaths(customerArtifactPaths);

        String runName = String.format("%s_%s", runNamePrefix, UUID.randomUUID().toString());

        getLog().info(String.format("Starting Run! Name: %s", runName));

        ScheduleRunResult run = client.scheduleRun(project.getArn(), runName, testSchedule.getTestPackageArn(), devicePool.getArn(), testSchedule, runTimeoutMinutes, runConfiguration);

        getLog().debug(String.format("Scheduled Run Arn: %s", run.getRun().getArn()));



    }
}
