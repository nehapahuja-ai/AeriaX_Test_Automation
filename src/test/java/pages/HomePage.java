package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ConfigReader;

import com.github.javafaker.Faker;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomePage extends BasePage {
    private static final String AERIA_SELECT_ACCOUNT_URL =
            "https://staging.dashboard.aeria.world/auth/select-account?_ref=info-section";
    private static final Pattern OTP_PATTERN = Pattern.compile(
            "(?i)(?:otp|verification\\s*code|code)\\D{0,80}(\\d{4,8})"
    );
    private static final Pattern FALLBACK_OTP_PATTERN = Pattern.compile("\\b\\d{6}\\b");
    private static final Faker FAKER = new Faker(new Locale("en"));
    private final List<String> browserTabs = new ArrayList<>();

    public void openHomePage() {
        open(ConfigReader.get("baseUrl", "https://example.com"));
    }

    public void openUrl(String url) {
        open(url);
        rememberCurrentTab();
    }

    public void openAeriaSelectAccountPage() {
        open(AERIA_SELECT_ACCOUNT_URL);
        rememberCurrentTab();
    }

    public void enterEmail(String email) {
        type(firstVisible(
                By.cssSelector("input[type='email']"),
                By.cssSelector("input[name*='email']"),
                By.cssSelector("input[id*='email']"),
                By.xpath("//input[contains(translate(@placeholder,'EMAIL','email'),'email')]"),
                By.xpath("//input[@type='text']")
        ), email);
    }

    public void clickContinueButton() {
        By continueButton = firstClickable(
                By.xpath("//button[normalize-space()='Continue']"),
                By.xpath("//button[contains(normalize-space(),'Continue')]"),
                By.cssSelector("button[type='submit']"),
                By.xpath("//*[@role='button' and contains(normalize-space(),'Continue')]")
        );

        WebElement button = waitForClickable(continueButton);
        scrollIntoView(button);
        clickElement(button);
    }

    public void openUrlInNewTab(String url) {
        rememberCurrentTab();
        ((JavascriptExecutor) driver).executeScript("window.open(arguments[0], '_blank');", url);
        switchToNewestTab();
    }

    public void enterBasicAuthCredentials(String username, String password) {
        String currentUrl = driver.getCurrentUrl();

        try {
            driver.switchTo().alert().sendKeys(username + "\t" + password);
            driver.switchTo().alert().accept();
            return;
        } catch (WebDriverException ignored) {
            open(buildAuthenticatedUrl(currentUrl, username, password));
        }
    }

    public String readFirstMailAndGetOtp() {
        waitForInboxPage();
        clickLatestOtpEmail();
        waitForOpenedEmail();

        return waitForOtpFromPage();
    }

    public void switchToFirstTab() {
        if (browserTabs.isEmpty()) {
            rememberCurrentTab();
        }
        driver.switchTo().window(browserTabs.get(0));
    }

    public void enterOtp(String otp) {
        type(firstVisible(
                By.cssSelector("input[autocomplete='one-time-code']"),
                By.cssSelector("input[name*='otp']"),
                By.cssSelector("input[id*='otp']"),
                By.cssSelector("input[name*='code']"),
                By.cssSelector("input[id*='code']"),
                By.xpath("//input[contains(translate(@placeholder,'OTP CODE','otp code'),'otp') or contains(translate(@placeholder,'OTP CODE','otp code'),'code')]"),
                By.xpath("//input[@type='text' or @type='tel' or @type='number']")
        ), otp);
    }

    public String getTitle() {
        return title();
    }

    public String getCurrentUrl() {
        return currentUrl();
    }

    public boolean isAeriaSelectAccountPageDisplayed() {
        return urlContains("/auth/select-account");
    }

    public boolean isSelectAccountPageDisplayed() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicitWait", 15)));
        return wait.until(ExpectedConditions.and(
                ExpectedConditions.urlContains("/auth/select-account"),
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[normalize-space()='Select Account']")),
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(normalize-space(),\"Please choose the one you'd like to access\")]"))
        ));
    }

    public void selectTenant(String tenantName) {
        By tenantCard = By.xpath(
                "//*[normalize-space()='" + tenantName + "' or @title='" + tenantName + "' or normalize-space()='@ " + tenantName + "']" +
                        "/ancestor::*[self::button or self::label or self::div[contains(@class,'card') or contains(@class,'cursor') or @role='button']][1]"
        );

        WebElement tenant = waitForVisible(tenantCard);
        scrollIntoView(tenant);
        clickElement(tenant);
    }

    public void clickSideStepperMenu(String menuName) {
        String menuText = xpathLiteral(menuName);
        By menuItem = By.xpath(
                "//aside//a[normalize-space()=" + menuText + " or .//*[normalize-space()=" + menuText + "]]"
        );

        WebElement item = waitForClickable(menuItem);
        scrollIntoView(item);
        clickElement(item);
    }

    public void clickButton(String buttonName) {
        String buttonText = xpathLiteral(buttonName);
        By button = By.xpath(
                "//button[normalize-space()=" + buttonText + " or .//*[normalize-space()=" + buttonText + "]]"
        );

        WebElement element = waitForClickable(button);
        scrollIntoView(element);
        clickElement(element);
    }

    public void clickTopbarMenuItem(String itemName) {

        By item = By.xpath(
                "//button[text()='Switch Accounts']"
        );

        WebElement element = waitForClickable(item);
        scrollIntoView(element);
        clickElement(element);
    }

    public void clickDownArrowButtonThenSelect(String itemName) {
        By profileButton = By.xpath(
                "//span[text()='Tenant Admin']"
        );

        WebElement button = waitForClickable(profileButton);
        scrollIntoView(button);
        clickElement(button);

        clickTopbarMenuItem(itemName);
    }

    public void enterGeneratedOutletName(String label) {
        String outletName = generatedOutletName();
        type(firstVisible(
                By.cssSelector("input[name='details.name']"),
                By.cssSelector("input[id='details.name']"),
                By.xpath("//label[contains(.,'" + label + "')]//following::input[1]")
        ), outletName);
    }

    public void selectAnyLocationFromDropdown() {
        By locationInputLocator = By.cssSelector("#details\\.location input[role='combobox']");
        WebElement locationInput = waitForClickable(locationInputLocator);
        scrollIntoView(locationInput);

        try {
            locationInput.click();
        } catch (WebDriverException exception) {
            clickElement(locationInput);
        }

        locationInput.sendKeys("Gravity");
        locationInput.sendKeys(org.openqa.selenium.Keys.ARROW_DOWN);
        locationInput.sendKeys(org.openqa.selenium.Keys.ENTER);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicitWait", 15)));
        wait.until(ExpectedConditions.or(
                ExpectedConditions.attributeContains(locationInputLocator, "value", "Gravity"),
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@id,'react-select') and contains(.,'Gravity')]")),
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(normalize-space(),'Gravity')]"))
        ));
    }

    public void enterGeneratedFssaiLicenseNumber(String label) {
        String licenseNumber = generatedNumericString(14);
        type(firstVisible(
                By.cssSelector("input[name='details.fssaiLicenseNumber']"),
                By.cssSelector("input[id='details.fssaiLicenseNumber']"),
                By.xpath("//label[contains(.,'" + label + "')]//following::input[@type='number'][1]")
        ), licenseNumber);
    }

    public void selectAllDayCheckbox() {
        By checkbox = By.xpath("//label[.//*[normalize-space()='All Day'] or normalize-space()='All Day']//input[@type='checkbox' or @type='radio'] | //div[@role='none'][contains(.,'All Day')]");
        WebElement element = waitForClickable(checkbox);
        scrollIntoView(element);
        clickElement(element);
    }

    public void enterGeneratedPhoneAndEmail(String phoneFieldLabel, String emailFieldLabel) {
        String phoneNumber = generatedPhoneNumber();
        String emailAddress = generatedEmailAddress();

        type(firstVisible(
                By.cssSelector("input[placeholder='Enter Phone Number']"),
                By.cssSelector("input[type='tel']"),
                By.xpath("//label[contains(.,'" + phoneFieldLabel + "')]//following::input[@type='tel'][1]")
        ), phoneNumber);

        type(firstVisible(
                By.cssSelector("input[name='contactDetails.email']"),
                By.cssSelector("input[id='contactDetails.email']"),
                By.xpath("//label[contains(.,'" + emailFieldLabel + "')]//following::input[1]")
        ), emailAddress);
    }

    public void clickNextButton() {
        By nextButton = firstClickable(
                By.xpath("//button[normalize-space()='Next']"),
                By.xpath("//button[contains(normalize-space(),'Next')]"),
                By.cssSelector("button._nextButton_11if6_33"),
                By.xpath("//div[contains(@class,'_commonButtons_')]//button")
        );

        WebElement button = waitForClickable(nextButton);
        scrollIntoView(button);
        clickElement(button);
    }

    public void waitForOutletCreationConfigurationPage() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicitWait", 15)));
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(normalize-space(),'Outlet Creation Configuration')]")),
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(normalize-space(),'Configuration')]"))
        ));
    }

    public void selectOutletConfigurationCheckbox(String checkboxName) {
        clickLabeledControl(checkboxName, "checkbox");
    }

    public void selectOrderChannel(String orderChannel) {
        String optionLiteral = xpathLiteral(orderChannel);

        // Try multiple strategies to find the Order Channel button
        By option = By.xpath(
                "(//*[contains(normalize-space(),'Order Channel')]" +
                        "/ancestor::*[self::div or self::fieldset or self::section][1]" +
                        "//button[normalize-space()=" + optionLiteral + " or .//*[normalize-space()=" + optionLiteral + "]])[1]" +
                        " | (//*[contains(normalize-space(),'Order Channel')]" +
                        "/following::button[normalize-space()=" + optionLiteral + " or .//*[normalize-space()=" + optionLiteral + "]][1])[1]" +
                        " | //button[normalize-space()=" + optionLiteral + " or .//*[normalize-space()=" + optionLiteral + "]]"
        );

        WebElement element = waitForClickable(option);
        scrollIntoView(element);
        clickElement(element);
    }

    public void viewDropdown(String dropdownName) {
        String literal = xpathLiteral(dropdownName);
        By dropdown = By.xpath(
                "//*[self::label or self::div or self::span][contains(normalize-space()," + literal + ")]" +
                        "/following::*[@role='combobox' or @role='button' or self::input][1]" +
                        " | //*[@role='combobox' or @role='button'][contains(normalize-space()," + literal + ")" +
                        " or contains(@aria-label," + literal + ")]"
        );

        WebElement element = waitForVisible(dropdown);
        scrollIntoView(element);
    }

    public void selectPosOption(String posOption) {
        String posLiteral = xpathLiteral(posOption);

        // Find and click the POS dropdown
        By posLabel = By.xpath(
                "//*[self::label or self::div or self::span][contains(normalize-space(),'POS')]" +
                        "/following::*[@role='combobox' or @role='button' or self::input][1]"
        );

        WebElement dropdownElement = waitForClickable(posLabel);
        scrollIntoView(dropdownElement);
        clickElement(dropdownElement);

        // Wait for the dropdown options to appear
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicitWait", 15)));
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@role='option']")),
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@class,'option')]")),
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[normalize-space()=" + posLiteral + "]"))
        ));

        // Add a small delay to ensure options are fully rendered
        sleepBriefly();

        // Find and click the specific option
        By option = By.xpath(
                "//*[@role='option' or contains(@class,'option')][normalize-space()=" + posLiteral + "]" +
                        " | //*[contains(text()," + posLiteral + ")][@role='option' or parent::*[@role='listbox']]" +
                        " | //*[contains(normalize-space()," + posLiteral + ")][@role='option']"
        );

        List<WebElement> options = driver.findElements(option);
        if (!options.isEmpty()) {
            WebElement selectedOption = options.get(0);
            scrollIntoView(selectedOption);
            sleepBriefly();
            clickElement(selectedOption);

            // Wait for dropdown to close and value to be selected
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[@role='listbox']")),
                    ExpectedConditions.stalenessOf(selectedOption)
            ));
        } else {
            // Fallback: try keyboard navigation
            dropdownElement.sendKeys(org.openqa.selenium.Keys.ARROW_DOWN);
            sleepBriefly();
            dropdownElement.sendKeys(org.openqa.selenium.Keys.ENTER);
        }
    }

    public void viewSection(String sectionName) {
        WebElement element = waitForVisible(By.xpath(
                "//*[contains(normalize-space()," + xpathLiteral(sectionName) + ")]"
        ));
        scrollIntoView(element);
    }

    public void selectRadioOption(String option) {
        String optionLiteral = xpathLiteral(option);
        By radio = By.xpath(
                "//button[.//*[normalize-space()=" + optionLiteral + "] or normalize-space()=" + optionLiteral + "]" +
                        " | //label[.//*[normalize-space()=" + optionLiteral + "] or normalize-space()=" + optionLiteral + "]" +
                        " | //*[(@role='radio' or @type='radio') and (@value=" + optionLiteral + " or @aria-label=" + optionLiteral + ")]" +
                        " | //*[self::div or self::span][normalize-space()=" + optionLiteral + "]"
        );

        WebElement element = waitForClickable(radio);
        scrollIntoView(element);
        clickElement(element);
    }

    public void selectMenuType(String menuType)  {
        selectOptionByLabelOrText("Menu Type", menuType);
    }

    public void selectRadioOptionForField(String fieldLabel, String option) {
        String fieldLiteral = xpathLiteral(fieldLabel);
        String optionLiteral = xpathLiteral(option);

        // Find the field container and then find the radio option within it
        By radio = By.xpath(
                "//*[self::label or self::div or self::span][contains(normalize-space()," + fieldLiteral + ")]" +
                        "/ancestor::*[self::div or self::fieldset or self::section][1]" +
                        "//input[@type='radio' or @role='radio'][(@value=" + optionLiteral + " or @aria-label=" + optionLiteral + ")]" +
                        " | //*[self::label or self::div or self::span][contains(normalize-space()," + fieldLiteral + ")]" +
                        "/following::*[self::label or self::button or self::div or self::span][1]" +
                        "[normalize-space()=" + optionLiteral + " or .//*[normalize-space()=" + optionLiteral + "]]"
        );

        WebElement element = waitForClickable(radio);
        scrollIntoView(element);
        clickElement(element);
    }

    public void selectAnyOptionFromDropdown(String dropdownName) {
        String dropdownLiteral = xpathLiteral(dropdownName);

        By dropdownControl = By.xpath(
                "//*[self::label or self::div or self::span][contains(normalize-space()," + dropdownLiteral + ")]" +
                        "/following::*[@role='combobox' or @role='button' or self::input][1]"
        );

        WebElement element = waitForClickable(dropdownControl);
        scrollIntoView(element);
        clickElement(element);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicitWait", 15)));
        By openOptions = By.xpath(
                "//*[@role='listbox']//*[(@role='option' or self::li or self::div or self::span) and not(contains(@aria-disabled,'true'))]"
        );
        wait.until(ExpectedConditions.visibilityOfElementLocated(openOptions));

        List<WebElement> options = wait.until(driver1 -> driver1.findElements(openOptions));
        for (WebElement option : options) {
            if (option.isDisplayed() && option.isEnabled()) {
                scrollIntoView(option);
                clickElement(option);
                return;
            }
        }

        element.sendKeys(org.openqa.selenium.Keys.ARROW_DOWN);
        element.sendKeys(org.openqa.selenium.Keys.ENTER);
    }

    // Public wrapper to select a specific option value from a dropdown by label/name
    public void selectOptionValueFromDropdown(String dropdownName, String optionValue) {
        // reuse existing selector logic; private helper handles waits and clicking
        selectOptionByLabelOrText(dropdownName, optionValue);
    }
    public void selectOptionValueFromDropdown1(String dropdownName, String optionValue) {
        // reuse existing selector logic; private helper handles waits and clicking
        selectOptionByLabelOrText1(dropdownName, optionValue);
    }

    public void selectAnyCuisineFromDropdown() {
        By cuisineControl = By.xpath(
                "//*[self::label or self::div or self::span][contains(normalize-space(),'Cuisine')]" +
                        "/following::*[@role='combobox' or @role='button' or self::input][1]"
        );
        WebElement element = waitForClickable(cuisineControl);
        scrollIntoView(element);
        clickElement(element);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicitWait", 15)));
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@role,'option') or contains(@class,'option')][not(contains(@aria-disabled,'true'))]")),
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(normalize-space(),'Cuisine')]"))
        ));

        List<WebElement> options = driver.findElements(By.xpath(
                "//*[contains(@role,'option') or contains(@class,'option')][not(contains(@aria-disabled,'true'))]"
        ));
        if (!options.isEmpty()) {
            WebElement option = options.get(0);
            scrollIntoView(option);
            clickElement(option);
        } else {
            element.sendKeys(org.openqa.selenium.Keys.ARROW_DOWN);
            element.sendKeys(org.openqa.selenium.Keys.ENTER);
        }
    }

    public void selectFirstCuisineFromDropdown() {
        selectFirstOptionFromDropdown("Cuisine");
    }

    public void selectAnyTwoCuisinesFromDropdown() {
        selectMultipleOptionsFromDropdown("Cuisine", 2);
    }

    public void selectCuisineFromDropdown(String cuisineName) {
        String cuisineLiteral = xpathLiteral(cuisineName);
        By cuisineControl = By.xpath(
                "//*[self::label or self::div or self::span][contains(normalize-space(),'Cuisine')]" +
                        "/following::*[@role='combobox' or @role='button' or self::input][1]"
        );

        WebElement element = waitForClickable(cuisineControl);
        scrollIntoView(element);
        clickElement(element);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicitWait", 15)));
        By option = By.xpath(
                "//*[(@role='option' or self::div or self::span or self::label or self::li) and " +
                        "(normalize-space()=" + cuisineLiteral + " or contains(normalize-space()," + cuisineLiteral + "))]"
        );

        WebElement optionElement = wait.until(ExpectedConditions.elementToBeClickable(option));
        scrollIntoView(optionElement);
        clickElement(optionElement);
    }

    public void selectFirstOptionFromDropdown(String dropdownName) {
        String dropdownLiteral = xpathLiteral(dropdownName);

        By dropdownControl = By.xpath(
                "//*[self::label or self::div or self::span][contains(normalize-space()," + dropdownLiteral + ")]" +
                        "/following::*[@role='combobox' or @role='button' or self::input][1]"
        );

        WebElement element = waitForClickable(dropdownControl);
        scrollIntoView(element);
        clickElement(element);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicitWait", 15)));
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@role='option' or contains(@class,'option')]")),
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(normalize-space()," + dropdownLiteral + ")]"))
        ));

        sleepBriefly();

        // Select the first available option
        List<WebElement> options = driver.findElements(By.xpath(
                "//*[@role='option' or contains(@class,'option')][not(contains(@aria-disabled,'true'))]"
        ));

        if (!options.isEmpty()) {
            WebElement firstOption = options.get(0);
            scrollIntoView(firstOption);
            sleepBriefly();
            clickElement(firstOption);

            // Wait for dropdown to close
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[@role='listbox']")),
                    ExpectedConditions.stalenessOf(firstOption)
            ));
        } else {
            // Fallback: try keyboard navigation
            element.sendKeys(org.openqa.selenium.Keys.ARROW_DOWN);
            sleepBriefly();
            element.sendKeys(org.openqa.selenium.Keys.ENTER);
        }
    }

    private void selectMultipleOptionsFromDropdown(String dropdownName, int optionCount) {
        String dropdownLiteral = xpathLiteral(dropdownName);
        By dropdownControl = By.xpath(
                "//*[self::label or self::div or self::span][contains(normalize-space()," + dropdownLiteral + ")]" +
                        "/following::*[@role='combobox' or @role='button' or self::input][1]"
        );

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicitWait", 15)));

        for (int i = 0; i < optionCount; i++) {
            WebElement element = waitForClickable(dropdownControl);
            scrollIntoView(element);
            clickElement(element);

            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[(@role='option' or contains(@class,'option')) and not(contains(@aria-disabled,'true'))]")),
                    ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(normalize-space()," + dropdownLiteral + ")]"))
            ));

            List<WebElement> options = driver.findElements(By.xpath(
                    "//*[(@role='option' or contains(@class,'option') or self::li or self::div or self::span) and " +
                            "not(contains(@aria-disabled,'true')) and " +
                            "not(contains(@aria-selected,'true'))]"
            ));

            if (options.isEmpty()) {
                options = driver.findElements(By.xpath(
                        "//*[(@role='option' or contains(@class,'option') or self::li or self::div or self::span) and " +
                                "not(contains(@aria-disabled,'true'))]"
                ));
            }

            if (options.isEmpty()) {
                element.sendKeys(org.openqa.selenium.Keys.ARROW_DOWN);
                element.sendKeys(org.openqa.selenium.Keys.ENTER);
            } else {
                WebElement option = options.get(0);
                scrollIntoView(option);
                clickElement(option);
            }

            sleepBriefly();
        }
    }

    public void enterCostForTwo(String costForTwo) {
        By costField = By.xpath(
                "//*[self::label or self::div or self::span][contains(normalize-space(),'Cost for Two')]" +
                        "/following::input[1]"
        );
        type(firstVisible(
                costField,
                By.cssSelector("input[name*='cost']"),
                By.cssSelector("input[id*='cost']")
        ), costForTwo);
    }

    public boolean isOverviewPageDisplayed() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicitWait", 15)));
        return wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/overview"),
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[normalize-space()='Overview']")),
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(normalize-space(),'Overview')]"))
        ));
    }

    private By firstVisible(By... locators) {
        RuntimeException lastException = null;
        for (By locator : locators) {
            try {
                waitForVisible(locator);
                return locator;
            } catch (RuntimeException exception) {
                lastException = exception;
            }
        }
        throw new IllegalStateException("No visible element found for supplied locators.", lastException);
    }

    private By firstClickable(By... locators) {
        RuntimeException lastException = null;
        for (By locator : locators) {
            try {
                waitForClickable(locator);
                return locator;
            } catch (RuntimeException exception) {
                lastException = exception;
            }
        }
        throw new IllegalStateException("No clickable element found for supplied locators.", lastException);
    }

    private String waitForOtpFromPage() {
        long deadline = System.nanoTime() + Duration.ofSeconds(ConfigReader.getInt("otpWait", 45)).toNanos();
        while (System.nanoTime() < deadline) {
            String otp = extractOtpFromOpenedEmail();
            if (otp != null) {
                return otp;
            }
            sleepBriefly();
        }
        throw new IllegalStateException("Unable to find OTP in the current mail page.");
    }

    private String generatedOutletName() {
        return FAKER.company().name() + " " + FAKER.number().digits(2);
    }

    private String generatedPhoneNumber() {
        return "+91" + FAKER.numerify("##########");
    }

    private String generatedEmailAddress() {
        return FAKER.internet().emailAddress();
    }

    private String generatedNumericString(int length) {
        StringBuilder value = new StringBuilder(length);
        while (value.length() < length) {
            value.append(FAKER.numerify("#########"));
        }
        return value.substring(0, length);
    }

    private void waitForInboxPage() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("mailPageWait", 45)));
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[normalize-space()='Messages']")),
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[normalize-space()='INBOX']")),
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(normalize-space(),'SMTP server listening')]"))
        ));
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(normalize-space(),'Otp Verification') or contains(normalize-space(),'OTP Verification')]")
        ));
    }

    private void clickLatestOtpEmail() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("mailPageWait", 45)));
        WebElement message = wait.until(driver -> findLatestOtpMessageRow());
        scrollIntoView(message);
        clickElement(message);
    }

    private void waitForOpenedEmail() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("mailPageWait", 45)));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(normalize-space(),'Subject: Otp Verification') or contains(normalize-space(),'Login OTP') or contains(normalize-space(),'Otp Verification')]")));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("iframe.htmlview")));
        wait.until(driver -> extractOtpFromOpenedEmail() != null);
    }

    private String extractOtpFromOpenedEmail() {
        String otp = extractOtpFromPreviewIframe();
        if (otp != null) {
            return otp;
        }

        String emailText = getOpenedEmailText();
        otp = extractOtp(emailText);
        if (otp != null) {
            return otp;
        }

        otp = scrollOpenedEmailAndExtractOtp();
        if (otp != null) {
            return otp;
        }

        for (WebElement frame : driver.findElements(By.tagName("iframe"))) {
            try {
                driver.switchTo().frame(frame);
                otp = extractOtp(driver.findElement(By.tagName("body")).getText());
                if (otp != null) {
                    return otp;
                }
            } catch (WebDriverException ignored) {
                // Try the next frame if this one is not the email preview.
            } finally {
                driver.switchTo().defaultContent();
            }
        }

        return null;
    }

    private String extractOtpFromPreviewIframe() {
        List<WebElement> previewFrames = driver.findElements(By.cssSelector("iframe.htmlview"));
        if (previewFrames.isEmpty()) {
            previewFrames = driver.findElements(By.tagName("iframe"));
        }

        for (WebElement frame : previewFrames) {
            try {
                driver.switchTo().frame(frame);

                String otp = extractOtpFromDigitBoxes();
                if (otp != null) {
                    return otp;
                }

                otp = extractOtp(driver.findElement(By.tagName("body")).getText());
                if (otp != null) {
                    return otp;
                }
            } catch (WebDriverException ignored) {
                // Try the next iframe if this one is not the email preview.
            } finally {
                driver.switchTo().defaultContent();
            }
        }

        return null;
    }

    private String extractOtpFromDigitBoxes() {
        List<WebElement> digitBoxes = driver.findElements(By.cssSelector(".btn"));
        if (digitBoxes.size() < 4) {
            return null;
        }

        StringBuilder otp = new StringBuilder();
        for (WebElement digitBox : digitBoxes) {
            String digit = digitBox.getText().trim();
            if (digit.matches("\\d")) {
                otp.append(digit);
            }
        }

        return otp.length() >= 4 && otp.length() <= 8 ? otp.toString() : null;
    }

    private WebElement findLatestOtpMessageRow() {
        By[] messageRowLocators = {
                By.xpath("(//tr[contains(.,'Otp Verification') or contains(.,'OTP Verification')])[1]"),
                By.xpath("(//*[@role='row' and (contains(.,'Otp Verification') or contains(.,'OTP Verification'))])[1]"),
                By.xpath("((//*[contains(normalize-space(),'Otp Verification') or contains(normalize-space(),'OTP Verification')]" +
                        "[not(contains(normalize-space(),'Subject:'))])/ancestor::*[self::tr or @role='row' or contains(@class,'message') or contains(@class,'list')][1])[1]"),
                By.xpath("(//*[contains(normalize-space(),'Otp Verification') or contains(normalize-space(),'OTP Verification')]" +
                        "[not(contains(normalize-space(),'Subject:'))])[1]")
        };

        for (By locator : messageRowLocators) {
            List<WebElement> messages = driver.findElements(locator);
            for (WebElement message : messages) {
                if (message.isDisplayed()) {
                    return message;
                }
            }
        }

        return null;
    }

    private String getOpenedEmailText() {
        By[] openedEmailLocators = {
                By.xpath("//*[contains(normalize-space(),'Login OTP')]/ancestor::*[self::div or self::section or self::main][1]"),
                By.xpath("//*[contains(normalize-space(),'Subject: Otp Verification')]/following::*[contains(normalize-space(),'Login OTP')][1]/ancestor::*[self::div or self::section or self::main][1]"),
                By.xpath("//*[contains(normalize-space(),'Please use the following verification code')]/ancestor::*[self::div or self::section or self::main][1]"),
                By.tagName("body")
        };

        for (By locator : openedEmailLocators) {
            try {
                WebElement element = driver.findElement(locator);
                scrollIntoView(element);
                String text = element.getText();
                if (text != null && !text.isBlank()) {
                    return text;
                }
            } catch (WebDriverException ignored) {
                // Continue with the next locator.
            }
        }

        return "";
    }

    private String scrollOpenedEmailAndExtractOtp() {
        List<WebElement> scrollableAreas = driver.findElements(By.xpath(
                "//*[contains(normalize-space(),'Login OTP') or contains(normalize-space(),'Please use the following verification code')]" +
                        "/ancestor::*[self::div or self::section or self::main][position() <= 6]"
        ));

        if (scrollableAreas.isEmpty()) {
            scrollableAreas = List.of(driver.findElement(By.tagName("body")));
        }

        for (WebElement area : scrollableAreas) {
            for (int i = 0; i < 8; i++) {
                scrollElement(area, 450);
                String otp = extractOtp(area.getText());
                if (otp != null) {
                    return otp;
                }
                sleepBriefly();
            }
        }

        return null;
    }

    private String extractOtp(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }

        Matcher matcher = OTP_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }

        Matcher spacedDigitMatcher = Pattern.compile("(?s)(?:verification\\s*code|Login OTP).*?((?:\\d\\s*){4,8})").matcher(text);
        if (spacedDigitMatcher.find()) {
            String otp = spacedDigitMatcher.group(1).replaceAll("\\D", "");
            if (otp.length() >= 4 && otp.length() <= 8) {
                return otp;
            }
        }

        Matcher fallbackMatcher = FALLBACK_OTP_PATTERN.matcher(text);
        return fallbackMatcher.find() ? fallbackMatcher.group() : null;
    }

    private void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }

    private void clickElement(WebElement element) {
        try {
            element.click();
        } catch (WebDriverException exception) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    private void clickLabeledControl(String labelText, String controlType) {
        String literal = xpathLiteral(labelText);
        By control = By.xpath(
                "(//label[normalize-space()=" + literal + " or .//*[normalize-space()=" + literal + "]])" +
                        "/ancestor-or-self::*[self::label or self::button or self::div or self::span][1]" +
                        " | //*[@aria-label=" + literal + " or normalize-space()=" + literal + "]" +
                        "[self::label or self::button or self::div or self::span]"
        );

        WebElement element = waitForClickable(control);
        scrollIntoView(element);
        clickElement(element);
    }

    private void selectOptionByLabelOrText(String fieldLabel, String optionText)  {
        String fieldLiteral = xpathLiteral(fieldLabel);
        String optionLiteral = xpathLiteral(optionText);
        By control = By.xpath(
                "//*[self::label or self::div or self::span][contains(normalize-space()," + fieldLiteral + ")]" +
                        "/following::*[self::button or self::label or self::div or self::span][.//*[normalize-space()=" + optionLiteral + "] or normalize-space()=" + optionLiteral + "][1]"
        );


        WebElement element = waitForClickable(control);
        scrollIntoView(element);
        clickElement(element);


    }
    private void selectOptionByLabelOrText1(String fieldLabel, String optionText)  {

        By control = By.xpath( "//div[text()='Select GST Rate']/following-sibling::div"

        );


        WebElement element = waitForClickable(control);
        scrollIntoView(element);
        clickElement(element);
        By control1 = By.xpath( "//*[text()='5%']"

        );


        WebElement element1 = waitForClickable(control1);
        scrollIntoView(element1);
        clickElement(element1);



    }

    private void scrollElement(WebElement element, int pixels) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollTop = arguments[0].scrollTop + arguments[1]; window.scrollBy(0, arguments[1]);",
                element,
                pixels
        );
    }

    private void sleepBriefly() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for OTP.", exception);
        }
    }

    private String xpathLiteral(String text) {
        if (!text.contains("'")) {
            return "'" + text + "'";
        }
        if (!text.contains("\"")) {
            return "\"" + text + "\"";
        }

        String[] parts = text.split("'");
        StringBuilder literal = new StringBuilder("concat(");
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                literal.append(", \"'\", ");
            }
            literal.append("'").append(parts[i]).append("'");
        }
        literal.append(")");
        return literal.toString();
    }

    private String buildAuthenticatedUrl(String url, String username, String password) {
        try {
            URI uri = new URI(url);
            String path = uri.getRawPath() == null || uri.getRawPath().isBlank() ? "/" : uri.getRawPath();
            URI authenticatedUri = new URI(
                    uri.getScheme(),
                    username + ":" + password,
                    uri.getHost(),
                    uri.getPort(),
                    path,
                    uri.getRawQuery(),
                    uri.getRawFragment()
            );
            return authenticatedUri.toASCIIString();
        } catch (URISyntaxException exception) {
            throw new IllegalArgumentException("Unable to create authenticated URL from: " + url, exception);
        }
    }

    private void rememberCurrentTab() {
        String currentHandle = driver.getWindowHandle();
        if (!browserTabs.contains(currentHandle)) {
            browserTabs.add(currentHandle);
        }
    }

    private void switchToNewestTab() {
        Set<String> handles = driver.getWindowHandles();
        for (String handle : handles) {
            if (!browserTabs.contains(handle)) {
                browserTabs.add(handle);
                driver.switchTo().window(handle);
                return;
            }
        }

        String latestHandle = new ArrayList<>(handles).get(handles.size() - 1);
        driver.switchTo().window(latestHandle);
    }
}
