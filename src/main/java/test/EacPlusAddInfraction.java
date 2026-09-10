package test;

import java.io.File;
import java.io.FileInputStream;
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

public class EacPlusAddInfraction {

        private static final String BASE_URL = "https://development.dc2zst92kum0.amplifyapp.com/";

        private static final String REPORT_PATH = "target/EacPlus/ExtentReportsForEacPlus_Add_new_infraction_"
                        + new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date())
                        + ".html";

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
                        // Select Warehouse
                        // =========================

                        ExtentTest warehouseSelectionTest = extent.createTest(
                                        "Select Warehouse",
                                        "Select a warehouse and click on Proceed button");

                        try {

                                By warehouseProceedBtn = By
                                                .xpath("//h3[text()='" + warehouseToBeSelected
                                                                + "']/parent::div//button");

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

                                ExtentTest addInfractionFormTest = extent.createTest(
                                                "Fill Add Compliance Infraction Form",
                                                "Open 'Add New Infraction', pick infraction type, then fill PO#, Quantity, Shipment#, Carrier, Vendor, Description and upload attachment");

                                try {

                                        // =========================
                                        // Load remaining form-related test data
                                        // =========================

                                        String poNumber = requireProperty(properties, "PO_NUMBER");
                                        String quantity = requireProperty(properties, "QUANTITY");
                                        String shipment = requireProperty(properties, "SHIPMENT");
                                        String carrier = requireProperty(properties, "CARRIER");
                                        String vendor = requireProperty(properties, "VENDOR");
                                        String description = requireProperty(properties, "DESCRIPTION");
                                        String attachmentRelativePath = requireProperty(properties,
                                                        "ATTACHMENT_FILEPATH");

                                        // =========================
                                        // Click "Add New Infraction" button
                                        // =========================

                                        By addNewInfractionButton = By.xpath(
                                                        requireProperty(properties,
                                                                        "XPATH_ADD_NEW_INFRACTION_BUTTON"));
                                        wait.until(ExpectedConditions.elementToBeClickable(addNewInfractionButton))
                                                        .click();
                                        addInfractionFormTest.info("Clicked 'Add New Infraction' button");
                                        System.out.println("Clicked 'Add New Infraction' button");
                                        Thread.sleep(2100);
                                        // =========================
                                        // Select Infraction Type from the list that appears
                                        // (e.g. "Late to appt") — this step was missing before
                                        // and the script was jumping straight to the form fields.
                                        // =========================

                                        Thread.sleep(800); // allow the infraction type list to render

                                        By infractionTypeOption = By
                                                        .xpath(requireProperty(properties, "XPATH_INFRACTION_TYPE"));
                                        wait.until(ExpectedConditions.elementToBeClickable(infractionTypeOption))
                                                        .click();
                                        addInfractionFormTest.info("Selected infraction type from the list");
                                        System.out.println("Selected infraction type from the list");

                                        Thread.sleep(2000); // wait 2s after reaching the Add Compliance Infraction page
                                                            // for it to fully load

                                        // =========================
                                        // PO#
                                        // =========================

                                        By poField = By.xpath(requireProperty(properties, "XPATH_PO"));
                                        wait.until(ExpectedConditions.visibilityOfElementLocated(poField)).clear();
                                        driver.findElement(poField).sendKeys(poNumber);
                                        addInfractionFormTest.info("Entered PO#: " + poNumber);

                                        // =========================
                                        // Quantity
                                        // =========================

                                        By quantityField = By.xpath(requireProperty(properties, "XPATH_QUANTITY"));
                                        wait.until(ExpectedConditions.visibilityOfElementLocated(quantityField))
                                                        .clear();
                                        driver.findElement(quantityField).sendKeys(quantity);
                                        addInfractionFormTest.info("Entered Quantity: " + quantity);

                                        // =========================
                                        // Shipment# (type-to-search, then select suggestion)
                                        // =========================

                                        By shipmentField = By.xpath(requireProperty(properties, "XPATH_SHIPMENT"));
                                        wait.until(ExpectedConditions.elementToBeClickable(shipmentField))
                                                        .sendKeys(shipment);
                                        addInfractionFormTest.info("Typed Shipment#: " + shipment);

                                        Thread.sleep(1500); // allow the suggestion list to populate

                                        By shipmentOption = By
                                                        .xpath(requireProperty(properties, "XPATH_SHIPMENT_OPTION"));
                                        wait.until(ExpectedConditions.elementToBeClickable(shipmentOption)).click();
                                        addInfractionFormTest.info("Selected Shipment# suggestion: " + shipment);

                                        // =========================
                                        // Carrier (searchable dropdown: click to open, type to
                                        // filter, then select the matching suggestion).
                                        //
                                        // Like Shipment# above, this custom combobox only renders
                                        // its option list once you start typing — clicking alone
                                        // (the old behavior) never produced any option to click.
                                        // =========================

                                        By carrierDropdown = By
                                                        .xpath(requireProperty(properties, "XPATH_CARRIER"));
                                        wait.until(ExpectedConditions.elementToBeClickable(carrierDropdown)).click();
                                        addInfractionFormTest.info("Opened Carrier dropdown");

                                        // The visible field is often a styled wrapper around a real
                                        // <input> that only becomes usable after the click above.
                                        // Default guess: an <input> nested inside the same element.
                                        // Override XPATH_CARRIER_SEARCH_INPUT in testdata.properties
                                        // once you've confirmed the real element in DevTools.
                                        String carrierSearchInputXpath = optionalProperty(properties,
                                                        "XPATH_CARRIER_SEARCH_INPUT",
                                                        requireProperty(properties, "XPATH_CARRIER") + "//input");
                                        By carrierSearchInput = By.xpath(carrierSearchInputXpath);
                                        wait.until(ExpectedConditions
                                                        .visibilityOfElementLocated(carrierSearchInput))
                                                        .sendKeys(carrier);
                                        addInfractionFormTest.info("Typed Carrier: " + carrier);

                                        Thread.sleep(1000); // allow the filtered option list to render

                                        // Scope to role="option": virtually every custom-select
                                        // library (React Select, MUI Autocomplete, Ant Design,
                                        // Downshift, etc.) tags real suggestion rows with
                                        // role="option", but never the input/control itself. This
                                        // avoids accidentally matching the typed text that's
                                        // sitting visibly in the search box (which also "contains"
                                        // the same string). Match the full text exactly and
                                        // case-insensitively since the UI may render the option in
                                        // uppercase (e.g. "TEST VENDOR") via CSS while the
                                        // underlying text is "Test Vendor".
                                        String carrierOptionXpath = optionalProperty(properties,
                                                        "XPATH_CARRIER_OPTION",
                                                        "//*[@role='option'][translate(normalize-space(.), "
                                                                        + "'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='"
                                                                        + carrier.toLowerCase() + "']");
                                        By carrierOption = By.xpath(carrierOptionXpath);
                                        WebElement carrierOptionEl = wait
                                                        .until(ExpectedConditions.elementToBeClickable(carrierOption));
                                        clickRobustly(driver, carrierOptionEl);
                                        addInfractionFormTest.info("Selected Carrier: " + carrier);

                                        // =========================
                                        // Vendor (searchable dropdown: click to open, type to
                                        // filter, then select the matching suggestion; falls back
                                        // to clicking an "Add new" row if no match appears).
                                        // =========================

                                        By vendorDropdown = By
                                                        .xpath(requireProperty(properties, "XPATH_VENDOR_DROPDOWN"));
                                        wait.until(ExpectedConditions.elementToBeClickable(vendorDropdown)).click();
                                        addInfractionFormTest.info("Opened Vendor dropdown");

                                        String vendorSearchInputXpath = optionalProperty(properties,
                                                        "XPATH_VENDOR_SEARCH_INPUT",
                                                        requireProperty(properties, "XPATH_VENDOR_DROPDOWN")
                                                                        + "//input");
                                        By vendorSearchInput = By.xpath(vendorSearchInputXpath);
                                        wait.until(ExpectedConditions
                                                        .visibilityOfElementLocated(vendorSearchInput))
                                                        .sendKeys(vendor);
                                        addInfractionFormTest.info("Typed Vendor: " + vendor);

                                        Thread.sleep(1000); // allow the filtered option list to render

                                        try {
                                                // Same role="option" scoping + exact-match reasoning
                                                // as Carrier above — avoids matching the typed text
                                                // still visible in the search box itself.
                                                String vendorOptionXpath = optionalProperty(properties,
                                                                "XPATH_VENDOR_OPTION",
                                                                "//*[@role='option'][translate(normalize-space(.), "
                                                                                + "'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')='"
                                                                                + vendor.toLowerCase() + "']");
                                                By vendorOption = By.xpath(vendorOptionXpath);
                                                WebElement vendorOptionEl = wait.until(
                                                                ExpectedConditions.elementToBeClickable(vendorOption));
                                                clickRobustly(driver, vendorOptionEl);
                                                addInfractionFormTest.info("Selected existing Vendor: " + vendor);
                                        } catch (Exception vendorNotFound) {
                                                // "add new" fallback — the typed text is kept in the
                                                // search input, and the panel typically shows an
                                                // "Add new" row instead of a match. Kept as
                                                // role="option" + contains() since the row's full
                                                // text usually includes both "Add new" and the typed
                                                // value (e.g. "+ Add new \"Test\""). Adjust the xpath
                                                // once you've inspected the live DOM for this state.
                                                String addNewOptionXpath = optionalProperty(properties,
                                                                "XPATH_VENDOR_ADD_NEW_OPTION",
                                                                "//*[@role='option'][contains(translate(normalize-space(.), "
                                                                                + "'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'add new')]");
                                                By vendorAddNewOption = By.xpath(addNewOptionXpath);
                                                WebElement vendorAddNewOptionEl = wait.until(
                                                                ExpectedConditions.elementToBeClickable(
                                                                                vendorAddNewOption));
                                                clickRobustly(driver, vendorAddNewOptionEl);
                                                addInfractionFormTest
                                                                .info("Vendor not found in list — clicked 'Add new' for Vendor: "
                                                                                + vendor);
                                        }

                                        // =========================
                                        // Description
                                        // =========================

                                        String descriptionXpath = optionalProperty(properties, "XPATH_DESCRIPTION",
                                                        "//label[normalize-space(text())='Description']/following::textarea[1]");
                                        By descriptionField = By.xpath(descriptionXpath);
                                        wait.until(ExpectedConditions.visibilityOfElementLocated(descriptionField))
                                                        .clear();
                                        driver.findElement(descriptionField).sendKeys(description);
                                        addInfractionFormTest.info("Entered Description: " + description);

                                        // =========================
                                        // Attachment upload
                                        //
                                        // Note: we do NOT click the "Click here to Browse" wrapper
                                        // div — that opens the native OS file picker, which Selenium
                                        // cannot drive and will hang the script. Instead we locate the
                                        // real <input type="file"> (usually visually hidden inside that
                                        // wrapper) and sendKeys an ABSOLUTE path directly to it.
                                        // =========================

                                        File attachmentFile = resolveTestResource(attachmentRelativePath);

                                        if (!attachmentFile.exists()) {
                                                throw new IllegalStateException(
                                                                "Attachment file not found at: "
                                                                                + attachmentFile.getAbsolutePath());
                                        }

                                        String wrapperXpath = optionalProperty(properties,
                                                        "XPATH_ATTACHMENT_WRAPPER",
                                                        "//div[@class='file-upload-wrapper']");
                                        wait.until(ExpectedConditions
                                                        .presenceOfElementLocated(By.xpath(wrapperXpath)));

                                        // The hidden <input type="file"> always lives inside the wrapper
                                        // div, and this locator already uploads successfully — no need
                                        // for a separate, independently-configurable property for it.
                                        By attachmentInput = By.xpath(wrapperXpath + "//input[@type='file']");

                                        // If the real <input type="file"> is hidden via CSS (display:none / opacity:0),
                                        // sendKeys still works in Chrome even though it's not "visible", so don't wrap
                                        // this in ExpectedConditions.visibilityOfElementLocated — use
                                        // presenceOfElementLocated.
                                        WebElement fileInput = wait.until(
                                                        ExpectedConditions.presenceOfElementLocated(attachmentInput));
                                        fileInput.sendKeys(attachmentFile.getAbsolutePath());

                                        addInfractionFormTest.info(
                                                        "Uploaded attachment: " + attachmentFile.getAbsolutePath());

                                        Thread.sleep(3500); // allow upload/preview to complete
                                        System.out.println("Image uploaded successfully ");
                                        // =========================
                                        // Submit
                                        // =========================

                                        WebElement submitButton = driver.findElement(
                                                        By.xpath("//button[text()='Submit']"));
                                        wait.until(ExpectedConditions.elementToBeClickable(submitButton)).click();
                                        addInfractionFormTest.info("Clicked Submit");
                                        System.out.println("Submit button clicked");

                                        addInfractionFormTest
                                                        .pass("Add Compliance Infraction form submitted successfully");
                                        System.out.println("Add Compliance Infraction form submitted successfully");

                                        Thread.sleep(4000);

                                        // =========================
                                        // Verify redirect + new row on the Compliance Infraction list page
                                        // =========================

                                        wait.until(ExpectedConditions.urlContains("/ComplianceInfraction"));

                                        String currentUrl = driver.getCurrentUrl();

                                        if (!currentUrl.endsWith("/ComplianceInfraction")) {
                                                throw new IllegalStateException(
                                                                "Expected URL to end with '/ComplianceInfraction' but was: "
                                                                                + currentUrl);
                                        }

                                        addInfractionFormTest.info(
                                                        "Redirected to Compliance Infraction list page: " + currentUrl);

                                        By newInfractionRow = By.xpath(
                                                        "//tr[@class=\"primary-row \"]/td/span[text()=\""
                                                                        + poNumber + "\"]");

                                        wait.until(ExpectedConditions.visibilityOfElementLocated(newInfractionRow));

                                        addInfractionFormTest.pass(
                                                        "Infraction created successfully — found row with PO#: "
                                                                        + poNumber);
                                        System.out.println(
                                                        "Infraction created successfully — found row with PO#: "
                                                                        + poNumber);

                                        Thread.sleep(3000);

                                } catch (Exception exception) {

                                        exception.printStackTrace();

                                        addInfractionFormTest.fail(
                                                        "Add Compliance Infraction form fill/submit/verification failed: "
                                                                        + exception.getMessage());

                                        System.out.println(
                                                        "Add Compliance Infraction form fill/submit/verification failed");
                                }

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

        /**
         * Clicks an element normally, falling back to a JS click if the normal
         * click is intercepted (e.g. by an overlay during a dropdown's open
         * animation) or otherwise fails.
         */
        private static void clickRobustly(WebDriver driver, WebElement element) {
                try {
                        element.click();
                } catch (Exception normalClickFailed) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
                }
        }

        /**
         * Resolves a path the same way loadProperties() resolves testdata.properties:
         * relative to the working directory the test is launched from (project root
         * in Eclipse/Maven runs). Keep ATTACHMENT_FILEPATH in testdata.properties
         * pointed at e.g. "testdata/images/infraction-sample.jpeg".
         */
        private static File resolveTestResource(String relativePath) {
                return new File(relativePath);
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

        // =========================
        // Get Optional Property (with fallback default)
        //
        // Several xpaths (Description, Carrier/Vendor search input and option
        // text match, Vendor "add new" row, attachment input, Submit button)
        // were not present in the testdata.properties shared so far. Rather
        // than fail the whole run, this falls back to a reasonable default
        // derived from the screenshots, and logs that the default was used so
        // it's easy to spot and override with a real property once you've
        // inspected the live DOM.
        // =========================

        private static String optionalProperty(
                        Properties properties,
                        String key,
                        String defaultValue) {

                String value = properties.getProperty(key);

                if (value == null || value.trim().isEmpty()) {
                        System.out.println(
                                        "Property '" + key + "' not set in " + TEST_DATA_PATH
                                                        + " — using default: " + defaultValue);
                        return defaultValue;
                }

                return value.trim();
        }
}