package test;

import java.io.FileInputStream;
import java.io.File;
import java.time.Duration;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class EacPlusApproveInfraction {

        private static final String BASE_URL = "https://development.dc2zst92kum0.amplifyapp.com/";

        private static final String REPORT_PATH = "target/EacPlus/ExtentReportsForEacPlus_Approve_infraction_"
                        + new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date())
                        + ".html";

        private static final String TEST_DATA_PATH = "testdata.properties";

        public static void main(String[] args) {

                ExtentReports extent = new ExtentReports();

                ExtentSparkReporter spark = new ExtentSparkReporter(REPORT_PATH);

                spark.config().setTheme(Theme.DARK);
                spark.config().setReportName("Eac Plus Approve Infraction Automation Report");
                spark.config().setDocumentTitle("Approve Infraction");

                extent.attachReporter(spark);

                ExtentTest loginTest = extent.createTest(
                                "EAC+ Auditor Login",
                                "Open EAC+ site and log in with Auditor credentials from testdata.properties");

                WebDriver driver = null;

                try {

                        // =========================
                        // Load Test Data
                        // =========================

                        Properties properties = loadProperties();

                        String auditorEmail = requireProperty(properties, "AuditorUserEmail");
                        String auditorPassword = requireProperty(properties, "AuditorUserPassword");
                        String targetPO = requireProperty(properties, "AuditorTargetPO");
                        String intededAction = requireProperty(properties, "IntendedAction");

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

                        wait.until(ExpectedConditions.visibilityOfElementLocated(usernameField))
                                        .sendKeys(auditorEmail);
                        loginTest.info("Entered Auditor username");
                        System.out.println("Entered Auditor username");

                        wait.until(ExpectedConditions.visibilityOfElementLocated(passwordField))
                                        .sendKeys(auditorPassword);
                        loginTest.info("Entered Auditor password");
                        System.out.println("Entered Auditor password");

                        Thread.sleep(1000); // at least 1s before every click

                        wait.until(ExpectedConditions.elementToBeClickable(loginButton)).click();
                        loginTest.info("Clicked login button");
                        System.out.println("Clicked login button");

                        wait.until(
                                        ExpectedConditions.or(
                                                        ExpectedConditions.urlContains("/userRoles"),
                                                        ExpectedConditions.invisibilityOfElementLocated(loginButton)));

                        loginTest.pass("Auditor login completed successfully");
                        System.out.println("Auditor login completed successfully");

                        // =========================
                        // Wait 2 seconds after login before interacting with the list
                        // =========================

                        Thread.sleep(2000);

                        // =========================
                        // Approve Infraction flow
                        // =========================

                        ExtentTest approveTest = extent.createTest(
                                        "Approve Compliance Infraction",
                                        "Open the target compliance record by PO#, capture its Status, "
                                                        + "click Approve, and capture the Status again");

                        try {

                                // =========================
                                // Locate the target row by PO# and read its current Status
                                // before clicking into it
                                // =========================

                                By complianceIdCell = By.xpath(
                                                "//tr[.//span[normalize-space()='" + targetPO + "']]//td[1]/span");

                                By statusCell = By.xpath(
                                                "//tr[.//span[normalize-space()='" + targetPO + "']]//td[8]/span");

                                wait.until(ExpectedConditions.visibilityOfElementLocated(complianceIdCell));

                                String statusBeforeUpdate = wait
                                                .until(ExpectedConditions.visibilityOfElementLocated(statusCell))
                                                .getText();

                                approveTest.info(
                                                "Status for PO# " + targetPO + " before approval: "
                                                                + statusBeforeUpdate);
                                System.out.println(
                                                "Status for PO# " + targetPO + " before approval: "
                                                                + statusBeforeUpdate);

                                Thread.sleep(1000); // at least 1s before every click

                                // =========================
                                // Click the compliance ID to open the record
                                // =========================

                                WebElement complianceIdEl = wait
                                                .until(ExpectedConditions.elementToBeClickable(complianceIdCell));
                                clickRobustly(driver, complianceIdEl);

                                approveTest.info("Clicked compliance ID for PO#: " + targetPO);
                                System.out.println("Clicked compliance ID for PO#: " + targetPO);

                                Thread.sleep(2000); // allow the infraction detail page to load

                                // =========================
                                // Click the Approve button
                                // =========================

                                By approveButton = By.xpath("//button[text()='" + intededAction + "']");

                                Thread.sleep(1000); // at least 1s before every click

                                WebElement approveButtonEl = wait
                                                .until(ExpectedConditions.elementToBeClickable(approveButton));
                                clickRobustly(driver, approveButtonEl);

                                approveTest.info("Clicked Approve button");
                                System.out.println("Clicked Approve button");

                                // =========================
                                // Approve opens a reason form — enter a description and
                                // submit it
                                // =========================

                                Thread.sleep(1500); // allow the reason form to render

                                By reasonTextarea = By.xpath("//textarea[@name=\"reason\"]");

                                wait.until(ExpectedConditions.visibilityOfElementLocated(reasonTextarea))
                                                .sendKeys("Test description");

                                approveTest.info("Entered reason: Test description");
                                System.out.println("Entered reason: Test description");

                                Thread.sleep(1000); // at least 1s before every click

                                By submitButton = By.xpath("//button[text()=\"Submit\"]");

                                WebElement submitButtonEl = wait
                                                .until(ExpectedConditions.elementToBeClickable(submitButton));
                                clickRobustly(driver, submitButtonEl);

                                approveTest.info("Clicked Submit button on Approve reason form");
                                System.out.println("Clicked Submit button on Approve reason form");

                                // =========================
                                // Wait to land back on the list page, then re-read Status
                                // =========================

                                Thread.sleep(2000); // allow navigation back to the list page

                                String statusAfterUpdate = wait
                                                .until(ExpectedConditions.visibilityOfElementLocated(statusCell))
                                                .getText();

                                approveTest.info(
                                                "Status for PO# " + targetPO + " after approval: "
                                                                + statusAfterUpdate);
                                System.out.println(
                                                "Status for PO# " + targetPO + " after approval: "
                                                                + statusAfterUpdate);

                                approveTest.pass(
                                                "Infraction for PO# " + targetPO
                                                                + " approved. Status changed from '"
                                                                + statusBeforeUpdate + "' to '"
                                                                + statusAfterUpdate + "'");
                                System.out.println(
                                                "Infraction updated. Status changed from '" + statusBeforeUpdate
                                                                + "' to '" + statusAfterUpdate + "'");

                        } catch (Exception exception) {

                                exception.printStackTrace();

                                approveTest.fail(
                                                "Approve Compliance Infraction flow failed: "
                                                                + exception.getMessage());

                                System.out.println("Approve Compliance Infraction flow failed");
                        }

                } catch (Exception exception) {

                        exception.printStackTrace();

                        loginTest.fail("Login failed: " + exception.getMessage());

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

                        System.out.println("Extent report generated at: " + REPORT_PATH);
                }
        }

        /**
         * Clicks an element normally, falling back to a JS click if the normal
         * click is intercepted (e.g. by an overlay) or otherwise fails.
         */
        private static void clickRobustly(WebDriver driver, WebElement element) {
                try {
                        element.click();
                } catch (Exception normalClickFailed) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
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
                                        "Properties file not found: " + propertiesFile.getAbsolutePath());
                }

                try (FileInputStream inputStream = new FileInputStream(propertiesFile)) {
                        properties.load(inputStream);
                }

                return properties;
        }

        // =========================
        // Get Required Property
        // =========================

        private static String requireProperty(Properties properties, String key) {

                String value = properties.getProperty(key);

                if (value == null || value.trim().isEmpty()) {
                        throw new IllegalStateException(
                                        "Missing or empty property '" + key + "' in " + TEST_DATA_PATH);
                }

                return value.trim();
        }
}