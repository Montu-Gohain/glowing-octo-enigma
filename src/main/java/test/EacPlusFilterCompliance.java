package test;

import java.io.FileInputStream;
import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class EacPlusFilterCompliance {

    private static final String BASE_URL = "https://development.dc2zst92kum0.amplifyapp.com/";

    private static final String REPORT_PATH = "target/EacPlus/ExtentReportsForEacPlus_Filter_compliance_"
            + new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date())
            + ".html";

    private static final String TEST_DATA_PATH = "testdata.properties";

    public static void main(String[] args) {

        ExtentReports extent = new ExtentReports();

        ExtentSparkReporter spark = new ExtentSparkReporter(REPORT_PATH);

        spark.config().setTheme(Theme.DARK);
        spark.config().setReportName("Eac Plus Filter Compliance Automation Report");
        spark.config().setDocumentTitle("Filter Compliance Infractions");

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

            String filteredWarehouseName = requireProperty(properties, "Filtered_Warehouse_Name");
            String filteredComplianceName = requireProperty(properties, "Filtered_Compliance_Name");
            String filteredStatusName = requireProperty(properties, "Filtered_Status_Name");

            // The Compliance dropdown option is prefixed with the warehouse name
            // (e.g. "Dollar General - BLC - Poor Quality Pallets"), but the results
            // table only displays the trailing compliance type (e.g. "Poor Quality
            // Pallets"). Strip the "<Warehouse> - " prefix, if present, to get the
            // value we actually expect to see in the table.
            String warehousePrefix = filteredWarehouseName + " - ";
            String filteredComplianceDisplayName = filteredComplianceName.startsWith(warehousePrefix)
                    ? filteredComplianceName.substring(warehousePrefix.length()).trim()
                    : filteredComplianceName;

            String fromDateValue = requireProperty(properties, "START_DATE");
            String toDateValue = requireProperty(properties, "END_DATE");

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
            // Filter Compliance Infraction flow
            // =========================

            ExtentTest filterTest = extent.createTest(
                    "Filter Compliance Infraction",
                    "Open the Filter panel, select Warehouse / Compliance Type / Status, "
                            + "submit, and verify the resulting table only contains "
                            + "matching rows");

            try {

                // =========================
                // Click the Filter button and wait 5s for the panel to render
                // =========================

                By filterButton = By.xpath("//button/span[text()=\"Filter\"]");

                WebElement filterButtonEl = wait
                        .until(ExpectedConditions.elementToBeClickable(filterButton));
                clickRobustly(driver, filterButtonEl);

                filterTest.info("Clicked Filter button");
                System.out.println("Clicked Filter button");

                Thread.sleep(5000); // wait 5 seconds for the filter panel to fully render

                // =========================
                // Select a Warehouse
                // =========================

                By warehouseSelectLocator = By.xpath("//select[@name=\"WarehouseID\"]");

                WebElement warehouseSelectEl = wait
                        .until(ExpectedConditions
                                .elementToBeClickable(warehouseSelectLocator));
                clickRobustly(driver, warehouseSelectEl);

                new Select(warehouseSelectEl).selectByVisibleText(filteredWarehouseName);

                filterTest.info("Selected Warehouse: " + filteredWarehouseName);
                System.out.println("Selected Warehouse: " + filteredWarehouseName);

                Thread.sleep(1000); // at least 1s before every click

                // =========================
                // Select a Warehouse Compliance Type
                // =========================

                By complianceSelectLocator = By.xpath("//select[@name=\"IDWHCompliance\"]");

                WebElement complianceSelectEl = wait
                        .until(ExpectedConditions
                                .elementToBeClickable(complianceSelectLocator));
                clickRobustly(driver, complianceSelectEl);

                new Select(complianceSelectEl).selectByVisibleText(filteredComplianceName);

                filterTest.info("Selected Compliance Type: " + filteredComplianceName);
                System.out.println("Selected Compliance Type: " + filteredComplianceName);

                Thread.sleep(1000); // at least 1s before every click

                // =========================
                // Select a Status
                // =========================

                By statusSelectLocator = By.xpath("//select[@name=\"Status\"]");

                WebElement statusSelectEl = wait
                        .until(ExpectedConditions.elementToBeClickable(statusSelectLocator));
                clickRobustly(driver, statusSelectEl);

                new Select(statusSelectEl).selectByValue(filteredStatusName);

                filterTest.info("Selected Status: " + filteredStatusName);
                System.out.println("Selected Status: " + filteredStatusName);

                Thread.sleep(1000); // at least 1s before every click

                // =========================
                // Set the From Date
                // =========================

                By fromDateLocator = By.xpath("//input[@name=\"FromDate\"]");

                WebElement fromDateEl = wait
                        .until(ExpectedConditions.elementToBeClickable(fromDateLocator));
                clickRobustly(driver, fromDateEl);
                fromDateEl.clear();
                fromDateEl.sendKeys(fromDateValue);

                filterTest.info("Set From Date: " + fromDateValue);
                System.out.println("Set From Date: " + fromDateValue);

                Thread.sleep(1000); // at least 1s before every click

                // =========================
                // Set the To Date
                // =========================

                By toDateLocator = By.xpath("//input[@name=\"ToDate\"]");

                WebElement toDateEl = wait
                        .until(ExpectedConditions.elementToBeClickable(toDateLocator));
                clickRobustly(driver, toDateEl);
                toDateEl.clear();
                toDateEl.sendKeys(toDateValue);

                filterTest.info("Set To Date: " + toDateValue);
                System.out.println("Set To Date: " + toDateValue);

                Thread.sleep(1000); // at least 1s before every click

                // =========================
                // Click Submit and wait 3s for the results to load
                // =========================

                By submitButton = By.xpath("//button[text()=\"Submit\"]");

                WebElement submitButtonEl = wait
                        .until(ExpectedConditions.elementToBeClickable(submitButton));
                clickRobustly(driver, submitButtonEl);

                filterTest.info("Clicked Submit button on Filter form");
                System.out.println("Clicked Submit button on Filter form");

                Thread.sleep(3000); // wait 3 seconds for the filtered results to load

                // =========================
                // Verify the results table only contains rows matching
                // the selected filters
                // =========================

                By resultRows = By.xpath("//table//tbody/tr");

                wait.until(ExpectedConditions.presenceOfElementLocated(resultRows));

                List<WebElement> rows = driver.findElements(resultRows);

                filterTest.info("Rows returned after filtering: " + rows.size());
                System.out.println("Rows returned after filtering: " + rows.size());

                if (rows.isEmpty()) {
                    filterTest.fail(
                            "No rows were returned after applying the filters; "
                                    + "unable to verify filtered data");
                    System.out.println("No rows were returned after applying the filters");
                } else {

                    boolean allRowsMatch = true;

                    for (int i = 0; i < rows.size(); i++) {

                        WebElement row = rows.get(i);

                        String rowWarehouseName = getCellText(row, 2);
                        String rowComplianceName = getCellText(row, 3);
                        String rowStatus = getCellText(row, 8);

                        boolean warehouseMatches = rowWarehouseName
                                .equalsIgnoreCase(filteredWarehouseName);
                        boolean complianceMatches = rowComplianceName
                                .equalsIgnoreCase(filteredComplianceDisplayName);
                        boolean statusMatches = rowStatus
                                .equalsIgnoreCase(filteredStatusName);

                        boolean rowMatches = warehouseMatches && complianceMatches
                                && statusMatches;

                        if (!rowMatches) {
                            allRowsMatch = false;
                        }

                        String rowSummary = "Row " + (i + 1) + " -> Warehouse: '"
                                + rowWarehouseName + "', Compliance: '"
                                + rowComplianceName + "', Status: '"
                                + rowStatus + "' | Match: " + rowMatches;

                        filterTest.info(rowSummary);
                        System.out.println(rowSummary);
                    }

                    if (allRowsMatch) {
                        filterTest.pass(
                                "All " + rows.size()
                                        + " row(s) match the selected filters "
                                        + "(Warehouse: '" + filteredWarehouseName
                                        + "', Compliance: '"
                                        + filteredComplianceDisplayName
                                        + "', Status: '" + filteredStatusName
                                        + "', From: '" + fromDateValue
                                        + "', To: '" + toDateValue + "')");
                        System.out.println("Filter verification passed: all rows match");
                    } else {
                        filterTest.fail(
                                "One or more rows do not match the selected filters "
                                        + "(Warehouse: '" + filteredWarehouseName
                                        + "', Compliance: '"
                                        + filteredComplianceDisplayName
                                        + "', Status: '" + filteredStatusName
                                        + "', From: '" + fromDateValue
                                        + "', To: '" + toDateValue + "')");
                        System.out.println(
                                "Filter verification failed: mismatched row(s) found");
                    }
                }

            } catch (Exception exception) {

                exception.printStackTrace();

                filterTest.fail(
                        "Filter Compliance Infraction flow failed: "
                                + exception.getMessage());

                System.out.println("Filter Compliance Infraction flow failed");
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
     * throughout the results table).
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