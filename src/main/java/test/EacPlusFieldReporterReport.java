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

public class EacPlusFieldReporterReport {

    private static final String BASE_URL = "https://development.dc2zst92kum0.amplifyapp.com/";

    private static final String REPORT_PATH = "target/EacPlus/ExtentReportsForEacPlus_FieldReporterReport_"
            + new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date())
            + ".html";

    private static final String TEST_DATA_PATH = "testdata.properties";

    // Which row (0-based) of the Field Reporter Report table to click the
    // Field Reporter link on. Change this to exercise a different warehouse /
    // reporter combination.
    private static final int ROW_INDEX = 0;

    public static void main(String[] args) {

        ExtentReports extent = new ExtentReports();

        ExtentSparkReporter spark = new ExtentSparkReporter(REPORT_PATH);

        spark.config().setTheme(Theme.DARK);
        spark.config().setReportName("Eac Plus Field Reporter Report Automation Report");
        spark.config().setDocumentTitle("Field Reporter Report Drilldown");

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

            wait.until(ExpectedConditions.visibilityOfElementLocated(usernameField))
                    .sendKeys(userEmail);
            loginTest.info("Entered username");
            System.out.println("Entered username");

            wait.until(ExpectedConditions.visibilityOfElementLocated(passwordField))
                    .sendKeys(userPassword);
            loginTest.info("Entered password");
            System.out.println("Entered password");

            Thread.sleep(1000); // at least 1s before every click

            wait.until(ExpectedConditions.elementToBeClickable(loginButton)).click();
            loginTest.info("Clicked login button");
            System.out.println("Clicked login button");

            wait.until(
                    ExpectedConditions.or(
                            ExpectedConditions.urlContains("/userRoles"),
                            ExpectedConditions.invisibilityOfElementLocated(loginButton)));

            Thread.sleep(3000); // wait for the page navigation after login to complete

            loginTest.pass("Login completed successfully");
            System.out.println("Login completed successfully");

            // =========================
            // Select Warehouse
            // =========================

            ExtentTest warehouseSelectionTest = extent.createTest(
                    "Select Warehouse",
                    "Select a warehouse and click on Proceed button");

            By warehouseProceedBtn = By
                    .xpath("//h3[text()='" + warehouseToBeSelected + "']/parent::div//button");

            wait.until(ExpectedConditions.elementToBeClickable(warehouseProceedBtn)).click();

            Thread.sleep(3000); // wait after clicking the Proceed button

            warehouseSelectionTest.info(
                    "Selected " + warehouseToBeSelected
                            + " warehouse and clicked on Proceed button");
            System.out.println(
                    "Selected " + warehouseToBeSelected
                            + " warehouse and clicked on Proceed button.");

            Thread.sleep(5000); // wait for refresh

            warehouseSelectionTest.pass("Clicked on Proceed button on our desired warehouse");
            System.out.println("Clicked Proceed button");

            Thread.sleep(5000); // wait for load status page

            Thread.sleep(3000); // wait for the page redirect after warehouse selection to complete

            warehouseSelectionTest.pass("Warehouse selected successfully");

            // =========================
            // Navigate to Field Reporter Report
            // =========================

            ExtentTest navigateTest = extent.createTest(
                    "Navigate to Field Reporter Report",
                    "Open the hamburger menu and click the Field Reporter Report link in the "
                            + "side nav bar");

            By hamburgerMenu = By.xpath("//div[@class=\"ham-menu\"]");

            WebElement hamburgerMenuEl = wait
                    .until(ExpectedConditions.elementToBeClickable(hamburgerMenu));
            clickRobustly(driver, hamburgerMenuEl);

            navigateTest.info("Clicked hamburger menu icon");
            System.out.println("Clicked hamburger menu icon");

            Thread.sleep(3000); // wait for the side nav bar to open

            By fieldReporterReportNav = By.xpath("//*[@id=\"sidenav-main\"]/div/ul/a[9]/li");

            WebElement fieldReporterReportNavEl = wait
                    .until(ExpectedConditions.elementToBeClickable(fieldReporterReportNav));
            clickRobustly(driver, fieldReporterReportNavEl);

            navigateTest.info("Clicked Field Reporter Report nav link");
            System.out.println("Clicked Field Reporter Report nav link");

            Thread.sleep(3000); // wait for the page navigation to Field Reporter Report to complete

            By fieldReporterTableRows = By.xpath("//table[@class=\"dynamicEacList\"]/tbody/tr");

            wait.until(ExpectedConditions.presenceOfElementLocated(fieldReporterTableRows));

            navigateTest.pass("Field Reporter Report table loaded");
            System.out.println("Field Reporter Report table loaded");

            Thread.sleep(3000);
            // =========================
            // Select a row, click its Field Reporter link, and verify
            // the resulting All Compliance Infraction table
            // =========================

            ExtentTest drilldownTest = extent.createTest(
                    "Verify Field Reporter Report Drilldown",
                    "Click a Field Reporter link and verify the All Compliance Infraction "
                            + "table matches the selected Warehouse Name, Waived count, "
                            + "Approved count, and Total count");

            try {

                List<WebElement> fieldReporterRows = driver.findElements(fieldReporterTableRows);

                if (fieldReporterRows.isEmpty() || ROW_INDEX >= fieldReporterRows.size()) {
                    throw new IllegalStateException(
                            "Field Reporter Report table does not have a row at index "
                                    + ROW_INDEX);
                }

                WebElement selectedRow = fieldReporterRows.get(ROW_INDEX);

                // =========================
                // Capture Warehouse Name, Field Reporter, Waived,
                // Approved, and Total before navigating away
                // =========================

                String selectedWarehouseName = getCellText(selectedRow, 1);
                String selectedFieldReporter = getCellText(selectedRow, 2);
                String selectedWaivedText = getCellText(selectedRow, 3);
                String selectedApprovedText = getCellText(selectedRow, 4);
                String selectedTotalText = getCellText(selectedRow, 5);

                int selectedWaived = Integer.parseInt(selectedWaivedText);
                int selectedApproved = Integer.parseInt(selectedApprovedText);
                int selectedTotal = Integer.parseInt(selectedTotalText);

                drilldownTest.info(
                        "Captured row " + ROW_INDEX + " -> Warehouse: '"
                                + selectedWarehouseName + "', Field Reporter: '"
                                + selectedFieldReporter + "', Waived: "
                                + selectedWaived + ", Approved: " + selectedApproved
                                + ", Total: " + selectedTotal);
                System.out.println(
                        "Captured row " + ROW_INDEX + " -> Warehouse: '"
                                + selectedWarehouseName + "', Field Reporter: '"
                                + selectedFieldReporter + "', Waived: "
                                + selectedWaived + ", Approved: " + selectedApproved
                                + ", Total: " + selectedTotal);

                Thread.sleep(1000); // at least 1s before every click

                // =========================
                // Click the Field Reporter link for the selected row
                // =========================

                By fieldReporterCell = By.xpath("./td[2]/span");

                WebElement fieldReporterEl = selectedRow.findElement(fieldReporterCell);
                clickRobustly(driver, fieldReporterEl);

                drilldownTest.info("Clicked Field Reporter link: '" + selectedFieldReporter + "'");
                System.out.println("Clicked Field Reporter link: '" + selectedFieldReporter + "'");

                Thread.sleep(3000); // wait for the page navigation to All Compliance Infraction

                // =========================
                // Verify the All Compliance Infraction table rows
                // all match the selected Warehouse Name, and tally
                // Approved / Waived counts
                // =========================

                By infractionRows = By.xpath("//table[@class=\"dynamicEacList\"]/tbody/tr");

                wait.until(ExpectedConditions.presenceOfElementLocated(infractionRows));

                List<WebElement> resultRows = driver.findElements(infractionRows);

                drilldownTest.info(
                        "Rows returned in All Compliance Infraction table: "
                                + resultRows.size());
                System.out.println(
                        "Rows returned in All Compliance Infraction table: "
                                + resultRows.size());

                if (resultRows.isEmpty()) {
                    drilldownTest.fail(
                            "No rows were returned on the All Compliance Infraction "
                                    + "page; unable to verify drilldown data");
                    System.out.println(
                            "No rows were returned on the All Compliance Infraction page");
                } else {

                    boolean allRowsMatch = true;
                    int actualApprovedCount = 0;
                    int actualWaivedCount = 0;

                    for (int i = 0; i < resultRows.size(); i++) {

                        WebElement row = resultRows.get(i);

                        String rowWarehouseName = getCellText(row, 2);
                        String rowStatus = getCellText(row, 8);

                        boolean warehouseMatches = rowWarehouseName
                                .equalsIgnoreCase(selectedWarehouseName);

                        boolean statusIsApprovedOrWaived = rowStatus
                                .equalsIgnoreCase("Approved")
                                || rowStatus.equalsIgnoreCase("Waived");

                        if (rowStatus.equalsIgnoreCase("Approved")) {
                            actualApprovedCount++;
                        } else if (rowStatus.equalsIgnoreCase("Waived")) {
                            actualWaivedCount++;
                        }

                        boolean rowMatches = warehouseMatches && statusIsApprovedOrWaived;

                        if (!rowMatches) {
                            allRowsMatch = false;
                        }

                        String rowSummary = "Row " + (i + 1) + " -> Warehouse: '"
                                + rowWarehouseName + "', Status: '" + rowStatus
                                + "' | Match: " + rowMatches;

                        drilldownTest.info(rowSummary);
                        System.out.println(rowSummary);
                    }

                    // =========================
                    // Verify the total row count shown in pagination
                    // matches the Total count captured from the
                    // Field Reporter Report table
                    // =========================

                    By paginationTotalCount = By.xpath(
                            "//*[@id=\"__next\"]/div[2]/div[2]/div[2]/div/div/div/div/div/div[2]/div[3]/div/div[3]/span");

                    WebElement paginationTotalCountEl = wait.until(
                            ExpectedConditions
                                    .visibilityOfElementLocated(paginationTotalCount));

                    String paginationText = paginationTotalCountEl.getText().trim();

                    int actualTotalCount = extractTotalCount(paginationText);

                    boolean totalCountMatches = actualTotalCount == selectedTotal;
                    boolean approvedCountMatches = actualApprovedCount == selectedApproved;
                    boolean waivedCountMatches = actualWaivedCount == selectedWaived;

                    drilldownTest.info(
                            "Pagination text: '" + paginationText
                                    + "' -> Parsed total: " + actualTotalCount
                                    + " | Expected total: " + selectedTotal
                                    + " | Match: " + totalCountMatches);
                    System.out.println(
                            "Pagination total: " + actualTotalCount
                                    + " | Expected total: " + selectedTotal
                                    + " | Match: " + totalCountMatches);

                    drilldownTest.info(
                            "Approved count on page: " + actualApprovedCount
                                    + " | Expected Approved: " + selectedApproved
                                    + " | Match: " + approvedCountMatches);
                    System.out.println(
                            "Approved count on page: " + actualApprovedCount
                                    + " | Expected Approved: " + selectedApproved
                                    + " | Match: " + approvedCountMatches);

                    drilldownTest.info(
                            "Waived count on page: " + actualWaivedCount
                                    + " | Expected Waived: " + selectedWaived
                                    + " | Match: " + waivedCountMatches);
                    System.out.println(
                            "Waived count on page: " + actualWaivedCount
                                    + " | Expected Waived: " + selectedWaived
                                    + " | Match: " + waivedCountMatches);

                    if (resultRows.size() < actualTotalCount) {
                        drilldownTest.info(
                                "Note: only " + resultRows.size()
                                        + " row(s) are visible on the "
                                        + "current page while the table "
                                        + "reports " + actualTotalCount
                                        + " total row(s); the Approved/"
                                        + "Waived tallies above only reflect "
                                        + "the current page");
                    }

                    if (allRowsMatch && totalCountMatches && approvedCountMatches
                            && waivedCountMatches) {
                        drilldownTest.pass(
                                "All Compliance Infraction table matches the "
                                        + "selected Warehouse ('"
                                        + selectedWarehouseName
                                        + "'), Waived count ("
                                        + selectedWaived
                                        + "), Approved count ("
                                        + selectedApproved
                                        + "), and Total count ("
                                        + selectedTotal + ")");
                        System.out.println("Drilldown verification passed");
                    } else {
                        drilldownTest.fail(
                                "Drilldown verification failed -> Row data match: "
                                        + allRowsMatch
                                        + ", Total count match: "
                                        + totalCountMatches
                                        + ", Approved count match: "
                                        + approvedCountMatches
                                        + ", Waived count match: "
                                        + waivedCountMatches);
                        System.out.println("Drilldown verification failed");
                    }
                }

            } catch (Exception exception) {

                exception.printStackTrace();

                drilldownTest.fail(
                        "Field Reporter Report drilldown flow failed: "
                                + exception.getMessage());

                System.out.println("Field Reporter Report drilldown flow failed");
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