# Tractor-Maven-Plugin ![Build Status](https://travis-ci.org/metalmynds/tractor-maven-plugin.svg?branch=master)
Amazon Device Farm Test Execution and Result Management Maven Plugin

Project is ready for beta testing, it supports all framework types, please report any issues.

Example POM

    <build>
        <plugins>
            <plugin>
                <groupId>io.metalmynds.tractor</groupId>
                <artifactId>tractor-maven-plugin</artifactId>
                <version>1.0-SNAPSHOT</version>
                <configuration>
                    <projectName>My_Device_Farm_Project</projectName>
                    <uploadApplicationFilename>app-debug.apk</uploadApplicationFilename>
                    <uploadTestFilename>framework-zip-with-dependencies.zip</uploadTestFilename>
                    <testFrameworkType>APPIUM_JAVA_TESTNG</testFrameworkType>
                    <runTimeoutMinutes>5</runTimeoutMinutes>
                    <devicePoolName>Exploratory</devicePoolName>
                    <uploadAuxiliaryFilenames>app-debug-aux1.apk,app-debug-aux2.apk</uploadAuxiliaryFilenames>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>

Sample Output

    [INFO] ------------------------------------------------------------------------
    [INFO] Building maven-plugin-tester 1.0-SNAPSHOT
    [INFO] ------------------------------------------------------------------------
    [INFO]
    [INFO] --- maven-clean-plugin:2.5:clean (default-clean) @ maven-plugin-tester ---
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
    
    
    [INFO] Test Framework APPIUM_JAVA_TESTNG
    [INFO] Appium Version 1.6.5
    [INFO] Connecting to Device Farm
    [INFO] Locating Project JUnitTestNG
    [INFO] Located Device Pool Exploratory
    [INFO] Uploading Application app-debug.apk
    [INFO] Uploading Auxiliary Application app-debug-aux1.apk
    [INFO] Uploading Auxiliary Application app-debug-aux2.apk
    [INFO] Uploading Test Package framework-zip-with-dependencies.zip
    [INFO] Starting Execution of Run_15ab0ac3-9477-4ef9-b0d9-d386170e0216
    [INFO] Run Status Updated to SCHEDULING at Tue Oct 10 12:48:43 BST 2017
    [INFO] Run Status Updated to PENDING at Tue Oct 10 12:48:53 BST 2017
    [INFO] Run Status Updated to RUNNING at Tue Oct 10 12:49:09 BST 2017
    [INFO] Run Status Updated to COMPLETED at Tue Oct 10 12:57:42 BST 2017
    [INFO] Device Metered Usage 0 Minute(s)
    [INFO] Downloading Run Artifacts into C:\work\mavenplugintester\target\artifacts
    [INFO] Downloaded 17 Run Artifact File(s)
    [INFO] Execution Exited Normally
    [INFO] ------------------------------------------------------------------------
    [INFO] BUILD SUCCESS
    [INFO] ------------------------------------------------------------------------
    [INFO] Total time: 10:28 min
    [INFO] Finished at: 2017-10-10T12:58:01+01:00
    [INFO] Final Memory: 13M/205M
    [INFO] ------------------------------------------------------------------------
