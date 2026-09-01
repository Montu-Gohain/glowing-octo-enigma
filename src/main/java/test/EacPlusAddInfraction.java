package test;

import java.io.File;
import java.io.FileInputStream;
import java.time.Duration;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class EacPlusAddInfraction {

        private static final String BASE_URL = "https://development.dc2zst92kum0.amplifyapp.com/";

        private static final String REPORT_PATH = "target/EacPlus/ExtentReportsForEacPlus_Add_new_infraction_1September.html";

        private static final String TEST_DATA_PATH = "testdata.properties";

        public static void main(String[] args) {

                ExtentReports extent = new ExtentReports();

                ExtentSparkReporter spark = new ExtentSparkReporter(REPORT_PATH);

                spark.config().setTheme(Theme.DARK);
                spark.config().setReportName("Eac Plus Add new Infraction Automation Report");
                spark.config().setDocumentTitle("Add new Infraction");

                extent.attachReporter(spark);

                ExtentTest loginTest = extent.createTest(
                                "EAC+ Login",
                                "Open EAC+ site and log in with credentials from testdata.properties");

                WebDriver driver = null;

                try {

                        // =========================
                        // Load Test Data
                        // =========================

                        Properties properties = loadProperties();

                        String userEmail = requireProperty(properties, "userEmail");

                        String userPassword = requireProperty(properties, "userPassword");

                        String warehouseToBeSelected = requireProperty(properties, "warehouseToBeSelected");

                        // =========================
                        // Launch Chrome
                        // =========================

                        ChromeOptions options = new ChromeOptions();
                        options.addArguments("--remote-allow-origins=*");

                        driver = new ChromeDriver(options);

                        driver.manage().window().maximize();

                        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

                        // =========================
                        // Open EAC+ Application
                        // =========================

                        driver.get(BASE_URL);

                        loginTest.info("Opened Eac Plus login page");

                        // =========================
                        // Login
                        // =========================

                        By usernameField = By.id("email");

                        By passwordField = By.id("password");

                        By loginButton = By.xpath("//button[text()=\"LOG IN\"]");

                        wait.until(
                                        ExpectedConditions.visibilityOfElementLocated(usernameField))
                                        .sendKeys(userEmail);

                        loginTest.info("Entered username");
                        System.out.println("Entered username");

                        wait.until(
                                        ExpectedConditions.visibilityOfElementLocated(passwordField))
                                        .sendKeys(userPassword);

                        loginTest.info("Entered password");
                        System.out.println("Entered password");

                        wait.until(
                                        ExpectedConditions.elementToBeClickable(loginButton)).click();

                        loginTest.info("Clicked login button");
                        System.out.println("Clicked login button");

                        wait.until(
                                        ExpectedConditions.or(
                                                        ExpectedConditions.urlContains("/userRoles"),
                                                        ExpectedConditions.invisibilityOfElementLocated(loginButton)));

                        loginTest.pass("Login completed successfully");
                        System.out.println("Login completed successfully");

                        // =========================
                        // Todo : Select Warehouse
                        // =========================

                        ExtentTest warehouseSelectionTest = extent.createTest(
                                        "Select Warehouse",
                                        "Select a warehouse and click on Proceed button");

                        try {

                                By warehouseProceedBtn = By
                                                .xpath("//p[text()='" + warehouseToBeSelected + "']/..//button");

                                // Select warehouse
                                wait.until(
                                                ExpectedConditions.elementToBeClickable(warehouseProceedBtn)).click();

                                warehouseSelectionTest.info(
                                                "Selected " + warehouseToBeSelected
                                                                + " warehouse and clicked on Proceed button");

                                System.out.println(
                                                "Selected " + warehouseToBeSelected
                                                                + " warehouse and clicked on Proceed button.");

                                // Wait for refresh
                                Thread.sleep(5000);

                                warehouseSelectionTest.pass("Clicked on Proceed button on our desired warehouse");
                                System.out.println("Clicked Proceed button");

                                // =========================
                                // Wait for Load Status page
                                // =========================

                                Thread.sleep(5000);

                                warehouseSelectionTest.pass(
                                                "Warehouse selected successfully");

                        } catch (Exception exception) {

                                exception.printStackTrace();

                                warehouseSelectionTest.fail(
                                                "Select Warehouse failed: "
                                                                + exception.getMessage());

                                System.out.println(
                                                "Select Warehouse failed");

                        }

                } catch (Exception exception) {

                        exception.printStackTrace();

                        loginTest.fail(
                                        "Login failed: "
                                                        + exception.getMessage());

                        System.out.println("Login failed");

                } finally {

                        // =========================
                        // Close Browser
                        // =========================

                        if (driver != null) {
                                driver.quit();
                        }

                        // =========================
                        // Generate Extent Report
                        // =========================

                        extent.flush();

                        System.out.println(
                                        "Extent report generated at: "
                                                        + REPORT_PATH);
                }
        }

        // =========================
        // Load Properties
        // =========================

        private static Properties loadProperties() throws Exception {

                Properties properties = new Properties();

                File propertiesFile = new File(TEST_DATA_PATH);

                if (!propertiesFile.exists()) {

                        throw new IllegalStateException(
                                        "Properties file not found: "
                                                        + propertiesFile.getAbsolutePath());
                }

                try (FileInputStream inputStream = new FileInputStream(propertiesFile)) {

                        properties.load(inputStream);
                }

                return properties;
        }

        // =========================
        // Get Required Property
        // =========================

        private static String requireProperty(
                        Properties properties,
                        String key) {

                String value = properties.getProperty(key);

                if (value == null || value.trim().isEmpty()) {

                        throw new IllegalStateException(
                                        "Missing or empty property '"
                                                        + key
                                                        + "' in "
                                                        + TEST_DATA_PATH);
                }

                return value.trim();
        }
}
