package pages;

import base.DriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ConfigReader;

import java.time.Duration;

public abstract class BasePage {
    protected final WebDriver driver;
    private final WebDriverWait wait;

    protected BasePage() {
        this.driver = DriverFactory.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("explicitWait", 15)));
        PageFactory.initElements(driver, this);
    }

    protected void open(String url) {
        driver.get(url);
    }

    protected WebElement waitForVisible(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void click(WebElement element) {
        waitForVisible(element).click();
    }

    protected void click(By locator) {
        waitForClickable(locator).click();
    }

    protected void type(WebElement element, String text) {
        WebElement visibleElement = waitForVisible(element);
        visibleElement.clear();
        visibleElement.sendKeys(text);
    }

    protected void type(By locator, String text) {
        WebElement visibleElement = waitForVisible(locator);
        visibleElement.clear();
        visibleElement.sendKeys(text);
    }

    protected String title() {
        return driver.getTitle();
    }

    protected String currentUrl() {
        return driver.getCurrentUrl();
    }

    protected boolean urlContains(String expectedUrlPart) {
        return wait.until(ExpectedConditions.urlContains(expectedUrlPart));
    }

    protected boolean titleContains(String expectedTitlePart) {
        return wait.until(ExpectedConditions.titleContains(expectedTitlePart));
    }
}
