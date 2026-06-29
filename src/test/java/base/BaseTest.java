package base;

import org.openqa.selenium.WebDriver;

public abstract class BaseTest {
    protected WebDriver driver() {
        return DriverFactory.getDriver();
    }

}
