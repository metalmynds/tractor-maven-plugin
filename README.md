# Tractor-Maven-Plugin ![Build Status](https://travis-ci.org/metalmynds/tractor-maven-plugin.svg?branch=master)
Amazon Device Farm Test Execution and Result Management Maven Plugin.

The project supports all framework types and allows for the execution of several more including [Cucumber-JVM](https://cucumber.io/docs/reference/jvm), [Serenity BDD](http://www.thucydides.info/#/) and [JBehave](https://github.com/serenity-bdd/serenity-jbehave).

Example POM

    <plugin>
        <groupId>io.metalmynds.tractor</groupId>
        <artifactId>tractor-maven-plugin</artifactId>
        <version>1.0-SNAPSHOT</version>
        <configuration>
            <projectName>JUnitTestNG</projectName>
            <uploadApplicationFilename>app-debug.apk</uploadApplicationFilename>
            <uploadTestFilename>framework-zip-with-dependencies.zip</uploadTestFilename>
            <testFrameworkType>APPIUM_JAVA_TESTNG</testFrameworkType>
                <!-- Supported Test Types:
                    BUILTIN_FUZZ
                    BUILTIN_EXPLORER
                    APPIUM_JAVA_JUNIT
                    APPIUM_JAVA_TESTNG
                    APPIUM_PYTHON
                    APPIUM_WEB_JAVA_JUNIT
                    APPIUM_WEB_JAVA_TESTNG
                    APPIUM_WEB_PYTHON
                    CALABASH
                    INSTRUMENTATION
                    UIAUTOMATION
                    UIAUTOMATOR
                    XCTEST
                    XCTEST_UI
                -->
            <runTimeoutMinutes>5</runTimeoutMinutes>
            <devicePoolName>Exploratory</devicePoolName>
            <uploadAuxiliaryFilenames>app-debug-aux1.apk,app-debug-aux2.apk</uploadAuxiliaryFilenames>
            <downloadArtifactPath>/artifact</downloadArtifactPath>
        </configuration>
    </plugin>

Build Output

    [INFO] ------------------------------------------------------------------------
    [INFO] Building Tractor Tester 1.0-SNAPSHOT
    [INFO] ------------------------------------------------------------------------
    [INFO]
    [INFO] --- maven-clean-plugin:2.5:clean (default-clean) @ maven-plugin-tester ---
    [INFO] Deleting C:\work\mavenplugintester\target
    [INFO]
    [INFO] --- tractor-maven-plugin:1.0-SNAPSHOT:drive (default-cli) @ maven-plugin-tester ---
            ,----,
          ,/   .`|
        ,`   .'  :                              ___
      ;    ;     /                            ,--.'|_
    .'___,/    ,' __  ,-.                     |  | :,'   ,---.    __  ,-.
    |    :     |,' ,'/ /|                     :  : ' :  '   ,'\ ,' ,'/ /|
    ;    |.';  ;'  | |' | ,--.--.     ,---. .;__,'  /  /   /   |'  | |' |
    `----'  |  ||  |   ,'/       \   /     \|  |   |  .   ; ,. :|  |   ,'
        '   :  ;'  :  / .--.  .-. | /    / ':__,'| :  '   | |: :'  :  /
        |   |  '|  | '   \__\/: . ..    ' /   '  : |__'   | .; :|  | '
        '   :  |;  : |   ," .--.; |'   ; :__  |  | '.'|   :    |;  : |
        ;   |.' |  , ;  /  /  ,.  |'   | '.'| ;  :    ;\   \  / |  , ;
        '---'    ---'  ;  :   .'   \   :    : |  ,   /  `----'   ---'
                       |  ,     .-./\   \  /   ---`-'
                        `--`---'     `----'
                        
        Amazon Device Farm Cultivation
        
    [INFO] Test Framework APPIUM_JAVA_TESTNG
    [INFO] Appium Version 1.6.5
    [INFO] Connecting to Device Farm
    [INFO] Locating Project 'JUnitTestNG'
    [INFO] Located Device Pool 'Exploratory'
    [INFO] Uploading Application app-debug.apk
    [INFO] Uploading Auxiliary Application app-debug-aux1.apk
    [INFO] Uploading Auxiliary Application app-debug-aux2.apk
    [INFO] Uploading Test Package framework-zip-with-dependencies.zip
    [INFO] Starting Execution of Run_287f8358-483b-4db2-9ff0-ed0deaf5eaab
    [INFO] Run Status Updated to SCHEDULING at Thu Oct 12 16:30:46 BST 2017
    [INFO] Run Status Updated to PENDING at Thu Oct 12 16:30:56 BST 2017
    [INFO] Run Status Updated to RUNNING at Thu Oct 12 16:48:08 BST 2017
    [INFO] Run Status Updated to COMPLETED at Thu Oct 12 16:55:52 BST 2017
    [INFO] Device Metered Usage 0 Minute(s)
    [INFO] Downloading Run Artifacts into C:\work\mavenplugintester\target\artifact
    [INFO] Downloaded 17 Run Artifact File(s)
    [INFO] ------------------------------------------------------------------------
    [INFO] BUILD SUCCESS
    [INFO] ------------------------------------------------------------------------
    [INFO] Total time: 32:58 min
    [INFO] Finished at: 2017-10-12T16:56:09+01:00
    [INFO] Final Memory: 14M/205M
    [INFO] ------------------------------------------------------------------------

