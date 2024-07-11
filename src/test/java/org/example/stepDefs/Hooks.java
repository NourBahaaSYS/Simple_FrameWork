package org.example.stepDefs;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.commons.io.FileUtils;
import org.example.Utilities.DataUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

public class Hooks {

    public static WebDriver driver;

    @Before
    public void OpenBrowser() throws IOException {

        String BrowserName = DataUtils.getDataFromProperties("BrowserName").toLowerCase();

        if (BrowserName.contains("chrome")) {
            driver = new ChromeDriver();
        } else if (BrowserName.contains("edge")) {
            driver = new EdgeDriver();
        } else if (BrowserName.contains("firefox")) {
            driver = new FirefoxDriver();
        } else driver = new ChromeDriver();

        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));



        String URL = DataUtils.
                getDataFromProperties("BaseURL");

        driver.navigate().to(URL);

    }

    @After
    public void Quit(Scenario sc) {

        if (sc.isFailed()) {
            saveScreenshotAsFile(sc, "FAIL", "failed");
        } else {
            saveScreenshotAsFile(sc, "PASS", "passed");
        }

      driver.quit();
    }

    private void saveScreenshotAsFile(Scenario sc, String screenshotName, String status) {
        TakesScreenshot ts = (TakesScreenshot) driver;
        File srcFile = ts.getScreenshotAs(OutputType.FILE);
        String timestamp = new SimpleDateFormat("dd-MM-yyyy#HH-mm-ss").format(new Date());
        String scenarioName = sc.getName().replaceAll("[^a-zA-Z0-9]", "_");
        File destFile = new File("src/test/resources/test-outputs/" + status + "/" + screenshotName + "_" + scenarioName + "_" + timestamp + ".png");

        try {
            FileUtils.copyFile(srcFile, destFile);
            // Attach the screenshot to the scenario
            byte[] screenshot = ts.getScreenshotAs(OutputType.BYTES);
            sc.attach(screenshot, "image/png", screenshotName + " - " + scenarioName + " - " + timestamp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createDirectoriesIfNotExist() {
        new File("src/test/resources/test-outputs/passed").mkdirs();
        new File("src/test/resources/test-outputs/failed").mkdirs();
    }
}
