package test;

import java.io.FileInputStream;
import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class EacPlusTopTenChargeback {

        private static final String BASE_URL = "https://development.dc2zst92kum0.amplifyapp.com/";

        private static final String REPORT_PATH = "target/EacPlus/ExtentReportsForEacPlus_TopTenChargeback_"
                        + new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date())
                        + ".html";

        private static final String TEST_DATA_PATH = "testdata.properties";

        // Which row (0-based) of the Top 10 Chargeback table to click Total
        // Infractions on. Change this to exercise a different warehouse row.
        private static final int ROW_INDEX = 0;

        public static void main(String[] args) {

                ExtentReports extent = new ExtentReports();

                ExtentSparkReporter spark = new ExtentSparkReporter(REPORT_PATH);

                spark.config().setTheme(Theme.DARK);
                spark.config().setReportName("Eac Plus Top 10 Chargeback Automation Report");
                spark.config().setDocumentTitle("Top 10 Chargeback Drilldown");

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
                        // Wait 2 seconds after login before interacting with the menu
                        // =========================

                        Thread.sleep(4000);

                        // =========================
                        // Navigate to Top 10 Chargeback
                        // =========================

                        ExtentTest navigateTest = extent.createTest(
                                        "Navigate to Top 10 Chargeback",
                                        "Open the hamburger menu and click the Top 10 Chargeback nav link");

                        By hamburgerMenu = By.xpath("//div[@class=\"ham-menu\"]");

                        WebElement hamburgerMenuEl = wait
                                        .until(ExpectedConditions.elementToBeClickable(hamburgerMenu));
                        clickRobustly(driver, hamburgerMenuEl);

                        navigateTest.info("Clicked hamburger menu icon");
                        System.out.println("Clicked hamburger menu icon");

                        Thread.sleep(1000); // at least 1s before every click

                        By topTenChargebackNav = By.xpath(
                                        "//div[@class=\"eac-shipmentSummart\" and @title=\"Top 10 Chargeback\"]/../..");

                        WebElement topTenChargebackNavEl = wait
                                        .until(ExpectedConditions.elementToBeClickable(topTenChargebackNav));
                        clickRobustly(driver, topTenChargebackNavEl);

                        navigateTest.info("Clicked Top 10 Chargeback nav link");
                        System.out.println("Clicked Top 10 Chargeback nav link");

                        Thread.sleep(4000); // allow the Top 10 Chargeback page to load

                        By chargebackTableRows = By.xpath("//table[@class=\"dynamicEacList\"]/tbody/tr");

                        wait.until(ExpectedConditions.presenceOfElementLocated(chargebackTableRows));

                        navigateTest.pass("Top 10 Chargeback table loaded");
                        System.out.println("Top 10 Chargeback table loaded");

                        // =========================
                        // Select a row, click its Total Infractions count, and
                        // verify the resulting All Infractions table
                        // =========================

                        ExtentTest drilldownTest = extent.createTest(
                                        "Verify Top 10 Chargeback Drilldown",
                                        "Click a Total Infractions count and verify the All Infractions table "
                                                        + "matches the selected Warehouse Name, Violation Category, "
                                                        + "and Total Infractions count");

                        try {

                                List<WebElement> chargebackRows = driver.findElements(chargebackTableRows);

                                if (chargebackRows.isEmpty() || ROW_INDEX >= chargebackRows.size()) {
                                        throw new IllegalStateException(
                                                        "Top 10 Chargeback table does not have a row at index "
                                                                        + ROW_INDEX);
                                }

                                WebElement selectedRow = chargebackRows.get(ROW_INDEX);

                                // =========================
                                // Capture Warehouse Name, Violation Category, and
                                // Total Infractions before navigating away
                                // =========================

                                String selectedWarehouseName = getCellText(selectedRow, 1);
                                String selectedViolationCategory = getCellText(selectedRow, 2);
                                String selectedTotalInfractionsText = getCellText(selectedRow, 3);

                                int selectedTotalInfractions = Integer.parseInt(selectedTotalInfractionsText);

                                drilldownTest.info(
                                                "Captured row " + ROW_INDEX + " -> Warehouse: '"
                                                                + selectedWarehouseName + "', Violation Category: '"
                                                                + selectedViolationCategory
                                                                + "', Total Infractions: " + selectedTotalInfractions);
                                System.out.println(
                                                "Captured row " + ROW_INDEX + " -> Warehouse: '"
                                                                + selectedWarehouseName + "', Violation Category: '"
                                                                + selectedViolationCategory
                                                                + "', Total Infractions: " + selectedTotalInfractions);

                                Thread.sleep(5000); // at least 1s before every click

                                // =========================
                                // Click the Total Infractions count for the selected row
                                // =========================

                                By totalInfractionsCell = By.xpath("./td[3]/span");

                                WebElement totalInfractionsEl = selectedRow.findElement(totalInfractionsCell);
                                clickRobustly(driver, totalInfractionsEl);

                                drilldownTest.info(
                                                "Clicked Total Infractions count (" + selectedTotalInfractions
                                                                + ") for Warehouse: '" + selectedWarehouseName + "'");
                                System.out.println(
                                                "Clicked Total Infractions count for Warehouse: '"
                                                                + selectedWarehouseName + "'");

                                Thread.sleep(4000); // allow the All Infractions page to load

                                // =========================
                                // Verify the All Infractions table rows all match the
                                // selected Warehouse Name and Violation Category
                                // (Violation Category == Compliance Name column here)
                                // =========================

                                By infractionRows = By.xpath("//table[@class=\"dynamicEacList\"]/tbody/tr");

                                wait.until(ExpectedConditions.presenceOfElementLocated(infractionRows));

                                List<WebElement> resultRows = driver.findElements(infractionRows);

                                drilldownTest.info("Rows returned in All Infractions table: " + resultRows.size());
                                System.out.println("Rows returned in All Infractions table: " + resultRows.size());

                                if (resultRows.isEmpty()) {
                                        drilldownTest.fail(
                                                        "No rows were returned on the All Infractions page; "
                                                                        + "unable to verify drilldown data");
                                        System.out.println("No rows were returned on the All Infractions page");
                                } else {

                                        boolean allRowsMatch = true;

                                        for (int i = 0; i < resultRows.size(); i++) {

                                                WebElement row = resultRows.get(i);

                                                String rowWarehouseName = getCellText(row, 2);
                                                String rowComplianceName = getCellText(row, 3);

                                                boolean warehouseMatches = rowWarehouseName
                                                                .equalsIgnoreCase(selectedWarehouseName);
                                                boolean complianceMatches = rowComplianceName
                                                                .equalsIgnoreCase(selectedViolationCategory);

                                                boolean rowMatches = warehouseMatches && complianceMatches;

                                                if (!rowMatches) {
                                                        allRowsMatch = false;
                                                }

                                                String rowSummary = "Row " + (i + 1) + " -> Warehouse: '"
                                                                + rowWarehouseName + "', Compliance: '"
                                                                + rowComplianceName + "' | Match: " + rowMatches;

                                                drilldownTest.info(rowSummary);
                                                System.out.println(rowSummary);
                                        }

                                        // =========================
                                        // Verify the total row count shown in pagination
                                        // matches the Total Infractions count captured
                                        // from the Top 10 Chargeback table
                                        // =========================

                                        By paginationTotalCount = By.xpath(
                                                        "//*[@id=\"__next\"]/div[2]/div[2]/div[2]/div/div/div/div/div/div[2]/div[3]/div/div[3]/span");

                                        WebElement paginationTotalCountEl = wait.until(
                                                        ExpectedConditions
                                                                        .visibilityOfElementLocated(
                                                                                        paginationTotalCount));

                                        String paginationText = paginationTotalCountEl.getText().trim();

                                        int actualTotalCount = extractTotalCount(paginationText);

                                        boolean totalCountMatches = actualTotalCount == selectedTotalInfractions;

                                        drilldownTest.info(
                                                        "Pagination text: '" + paginationText
                                                                        + "' -> Parsed total: " + actualTotalCount
                                                                        + " | Expected total: "
                                                                        + selectedTotalInfractions
                                                                        + " | Match: " + totalCountMatches);
                                        System.out.println(
                                                        "Pagination total: " + actualTotalCount
                                                                        + " | Expected total: "
                                                                        + selectedTotalInfractions
                                                                        + " | Match: " + totalCountMatches);

                                        if (allRowsMatch && totalCountMatches) {
                                                drilldownTest.pass(
                                                                "All Infractions table matches the selected "
                                                                                + "Warehouse ('" + selectedWarehouseName
                                                                                + "'), Violation Category ('"
                                                                                + selectedViolationCategory
                                                                                + "'), and Total Infractions count ("
                                                                                + selectedTotalInfractions + ")");
                                                System.out.println("Drilldown verification passed");
                                        } else {
                                                drilldownTest.fail(
                                                                "Drilldown verification failed -> Row data match: "
                                                                                + allRowsMatch
                                                                                + ", Total count match: "
                                                                                + totalCountMatches);
                                                System.out.println("Drilldown verification failed");
                                        }
                                }

                        } catch (Exception exception) {

                                exception.printStackTrace();

                                drilldownTest.fail(
                                                "Top 10 Chargeback drilldown flow failed: "
                                                                + exception.getMessage());

                                System.out.println("Top 10 Chargeback drilldown flow failed");
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

        /**
         * Reads the trimmed text of the span inside the given 1-based column
         * index of a table row (mirrors the "td[n]/span" cell structure used
         * throughout the results tables).
         */
        private static String getCellText(WebElement row, int columnIndex) {

                By cellSpan = By.xpath("./td[" + columnIndex + "]/span");

                List<WebElement> spans = row.findElements(cellSpan);

                if (!spans.isEmpty()) {
                        return spans.get(0).getText().trim();
                }

                // Fallback in case the cell has no nested span
                By cellOnly = By.xpath("./td[" + columnIndex + "]");
                return row.findElement(cellOnly).getText().trim();
        }

        /**
         * Extracts the trailing total count from a pagination string such as
         * "Showing 1 - 10 of 15".
         */
        private static int extractTotalCount(String paginationText) {

                Pattern pattern = Pattern.compile("of\\s+(\\d+)", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(paginationText);

                if (matcher.find()) {
                        return Integer.parseInt(matcher.group(1));
                }

                throw new IllegalStateException(
                                "Could not parse total count from pagination text: '" + paginationText + "'");
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