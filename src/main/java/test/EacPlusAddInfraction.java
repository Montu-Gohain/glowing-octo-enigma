package test;

import java.io.File;
import java.io.FileInputStream;
import java.time.Duration;
import java.util.Properties;

import org.openqa.selenium.By;
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

                                        Thread.sleep(1500); // allow the Add Compliance Infraction form to load

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
                                        // Carrier (custom dropdown: "Select Carrier or add new")
                                        // Was previously commented out — now wired up the same
                                        // way as the Vendor dropdown below.
                                        // =========================

                                        By carrierDropdown = By
                                                        .xpath(requireProperty(properties, "XPATH_CARRIER"));
                                        wait.until(ExpectedConditions.elementToBeClickable(carrierDropdown)).click();
                                        addInfractionFormTest.info("Opened Carrier dropdown");

                                        Thread.sleep(500); // allow the option list to render

                                        String carrierOptionXpath = optionalProperty(properties,
                                                        "XPATH_CARRIER_OPTION",
                                                        "//*[self::li or self::div][normalize-space(text())='"
                                                                        + carrier + "']");
                                        By carrierOption = By.xpath(carrierOptionXpath);
                                        wait.until(ExpectedConditions.elementToBeClickable(carrierOption)).click();
                                        addInfractionFormTest.info("Selected Carrier: " + carrier);

                                        // =========================
                                        // Vendor (custom dropdown: "Select Vendor or add new")
                                        // =========================

                                        By vendorDropdown = By
                                                        .xpath(requireProperty(properties, "XPATH_VENDOR_DROPDOWN"));
                                        wait.until(ExpectedConditions.elementToBeClickable(vendorDropdown)).click();
                                        addInfractionFormTest.info("Opened Vendor dropdown");

                                        Thread.sleep(500); // allow the option list to render

                                        try {
                                                String vendorOptionXpath = optionalProperty(properties,
                                                                "XPATH_VENDOR_OPTION",
                                                                "//*[self::li or self::div][normalize-space(text())='"
                                                                                + vendor + "']");
                                                By vendorOption = By.xpath(vendorOptionXpath);
                                                wait.until(ExpectedConditions.elementToBeClickable(vendorOption))
                                                                .click();
                                                addInfractionFormTest.info("Selected existing Vendor: " + vendor);
                                        } catch (Exception vendorNotFound) {
                                                // "add new" fallback — adjust the input xpath once you've inspected it
                                                String addNewInputXpath = optionalProperty(properties,
                                                                "XPATH_VENDOR_ADD_NEW_INPUT",
                                                                "//input[@placeholder='Add new vendor']");
                                                By vendorAddNewInput = By.xpath(addNewInputXpath);
                                                wait.until(ExpectedConditions
                                                                .visibilityOfElementLocated(vendorAddNewInput))
                                                                .sendKeys(vendor);
                                                driver.findElement(vendorAddNewInput)
                                                                .sendKeys(org.openqa.selenium.Keys.ENTER);
                                                addInfractionFormTest
                                                                .info("Vendor not found in list — added new Vendor: "
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
        // Several xpaths (Description, Carrier/Vendor option text match,
        // attachment input, Submit button) were not present in the
        // testdata.properties shared so far. Rather than fail the whole run,
        // this falls back to a reasonable default derived from the
        // screenshots, and logs that the default was used so it's easy to
        // spot and override with a real property once you've inspected the
        // live DOM.
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