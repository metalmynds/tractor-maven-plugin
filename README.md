# tractor-maven-plugin
Amazon Device Farm Test Execution and Result Management Maven Plugin

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
'
