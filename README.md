# Tractor-Maven-Plugin
Amazon Device Farm Test Execution and Result Management Maven Plugin

Project is currently an alpha, but almost complete, please report any issues.

Example POM

    <build>
        <plugins>
            <plugin>
                <groupId>io.metalmynds</groupId>
                <artifactId>tractor-maven-plugin</artifactId>
                <version>1.0-SNAPSHOT</version>
                <configuration>
                    <projectName>MyTestingProject</projectName>
                    <uploadApplicationFilename>C:\work\app-debug.apk</uploadApplicationFilename>
                    <uploadTestFilename>C:\work\Serentiy-JUnit-with-dependencies.zip</uploadTestFilename>
                    <testFrameworkType>APPIUM_JAVA_JUNIT</testFrameworkType>
                    <runTimeoutMinutes>5</runTimeoutMinutes>
                    <devicePoolName>Exploratory</devicePoolName>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>

Sample Output


    [INFO] Scanning for projects...
    [INFO] 
    [INFO] ------------------------------------------------------------------------
    [INFO] Building maven-plugin-tester 1.0-SNAPSHOT
    [INFO] ------------------------------------------------------------------------
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
    [INFO] Uploading Application C:\work\app-debug.apk
    [INFO] Uploading Test Package C:\work\MyMapp-zip-with-dependencies.zip
    [INFO] Starting Run Name: Run_6b4fe7fa-23fb-4a6c-9ce1-acd35450eff3
    [INFO] Run Status Updated to SCHEDULING at Tue Oct 10 09:22:00 BST 2017
    [INFO] Run Status Updated to PENDING at Tue Oct 10 09:22:10 BST 2017
    [INFO] Run Status Updated to RUNNING at Tue Oct 10 09:22:15 BST 2017
    [INFO] Run Status Updated to COMPLETED at Tue Oct 10 09:22:26 BST 2017
    [INFO] Downloading Run Artifacts into C:\work\mavenplugintester\target\artifacts
    [INFO] Downloaded 1 Run Artifact File(s)
    [INFO] Execution Exited Normally
    [INFO] ------------------------------------------------------------------------
    [INFO] BUILD SUCCESS
    [INFO] ------------------------------------------------------------------------
    [INFO] Total time: 01:15 min
    [INFO] Finished at: 2017-10-10T09:22:26+01:00
    [INFO] Final Memory: 13M/208M
    [INFO] ------------------------------------------------------------------------
