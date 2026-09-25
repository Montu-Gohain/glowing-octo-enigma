# 🚀 Selenium Automation Test Suite

A robust, scalable, and maintainable automated testing framework built with **Selenium WebDriver**. Designed for end-to-end (E2E) web application testing with cross-browser compatibility, page object model (POM) design pattern, and detailed reporting.

![Selenium Automation Trends](https://aquarianconsult.com/wp-content/uploads/2026/04/Latest-Trends-in-Selenium-Automation-Testing.jpg)

---

## 📌 Table of Contents

- [Overview](#-overview)
- [Key Features](#-key-features)
- [Framework Architecture](#-framework-architecture)
- [Prerequisites](#-prerequisites)
- [Installation & Setup](#-installation--setup)
- [Running Tests](#-running-tests)
- [Project Structure](#-project-structure)
- [Reporting](#-reporting)
- [CI/CD Integration](#-cicd-integration)
- [Best Practices](#-best-practices)
- [Contributing](#-contributing)
- [License](#-license)

---

## 💡 Overview

This repository provides an automated test execution suite to validate web application functionality across multiple environments and browsers. It leverages modern test automation trends, including automated driver management, parallel test execution, headless execution, and rich visual HTML reports.

---

## ✨ Key Features

- **Page Object Model (POM):** Clean separation of page UI locators/actions from test scripts.
- **Cross-Browser Testing:** Seamless execution on Chrome, Firefox, Edge, and Safari.
- **Parallel Execution:** Fast feedback loops through concurrent test runs.
- **Headless & Grid Support:** Execution support for headless modes and Selenium Grid / Cloud hubs (BrowserStack, SauceLabs).
- **Data-Driven Testing:** Support for dynamic data sets via JSON, CSV, or Excel configuration.
- **Detailed Reporting:** Automated HTML execution reports with failed test screenshots.
- **CI/CD Ready:** Built-in configuration templates for GitHub Actions, Jenkins, and GitLab CI.

---

## 🏗 Framework Architecture

```text
├── config/                # Environment & browser configurations
├── pages/                 # Page Object Model (Locators & Page Actions)
├── tests/                 # Test suites and execution scenarios
├── utils/                 # Helpers (WebDriver factory, Excel/JSON readers, Wait helpers)
├── reports/               # Auto-generated execution reports & screenshots
├── drivers/               # Webdrivers (Managed automatically via Driver Manager)
├── .gitignore             # Git ignore patterns
├── README.md              # Project documentation
└── pom.xml / requirements.txt  # Project dependencies
```

---

## ⚙️ Prerequisites

Ensure you have the following installed on your local machine:

- **Programming Runtime:** Java JDK 11+ (or Python 3.8+ / Node.js depending on language binding)
- **Build Tool:** Maven / Gradle (or `pip` for Python)
- **Browsers:** Latest versions of Google Chrome, Mozilla Firefox, or Microsoft Edge
- **IDE:** IntelliJ IDEA, Eclipse, or VS Code

---

## 📥 Installation & Setup

1. **Clone the Repository:**

   ```bash
   git clone https://github.com/your-username/selenium-automation-suite.git
   cd selenium-automation-suite
   ```

2. **Install Dependencies:**
   - **For Java (Maven):**
     ```bash
     mvn clean install -DskipTests
     ```
   - **For Python:**
     ```bash
     pip install -r requirements.txt
     ```

---

## 🧪 Running Tests

### 1. Run All Tests

```bash
# Maven
mvn test

# Pytest
pytest
```

### 2. Run Tests on Specific Browsers

```bash
# Chrome (Default)
mvn test -Dbrowser=chrome

# Firefox
mvn test -Dbrowser=firefox

# Edge
mvn test -Dbrowser=edge
```

### 3. Run Tests in Headless Mode

```bash
mvn test -Dbrowser=chrome -Dheadless=true
```

### 4. Parallel Execution

```bash
mvn test -DthreadCount=4
```

---

## 📊 Reporting

Upon test execution, comprehensive HTML reports are automatically generated in the `reports/` directory.

- **Failed Test Capture:** Automatic screenshots are taken and linked directly into the HTML report upon test failure.
- **Viewing Reports:** Open `reports/index.html` in any modern web browser.

---

## 🔄 CI/CD Integration

This project includes pre-configured workflow pipelines for continuous integration:

- **GitHub Actions:** See `.github/workflows/maven-test.yml`
- **Jenkins:** Pipeline code available in `Jenkinsfile`

---

## 🛠 Best Practices Followed

- **Explicit Waits:** Avoid `Thread.sleep()`; use `WebDriverWait` and Expected Conditions.
- **Robust Locators:** Prioritize IDs, Data-Test attributes, and optimized CSS/XPath selectors.
- **Clean Code:** Standardized naming conventions, reusable helper utilities, and DRY (Don't Repeat Yourself) principles.

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the Project.
2. Create your Feature Branch (`git checkout -b feature/AwesomeFeature`).
3. Commit your Changes (`git commit -m 'Add some AwesomeFeature'`).
4. Push to the Branch (`git push origin feature/AwesomeFeature`).
5. Open a Pull Request.

---

## 📜 License

Distributed under the MIT License. See `LICENSE` for more information.

<!-- Todo : Test case : Top 10 Chargeback -->

## Xpaths

Hamburger menu :

Top 10 Chargeback nav link :

> In Top 10 Chargeback page Click on the total infractions in any warehouse name with Violation Category
