package stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import pages.HomePage;

public class HomeSteps {
    private HomePage homePage;
    private String otp;

    @Given("the user opens the application")
    public void theUserOpensTheApplication() {
        homePage().openHomePage();
    }

    @Given("the user opens the Aeria select account page")
    public void theUserOpensTheAeriaSelectAccountPage() {
        homePage().openAeriaSelectAccountPage();
    }

    @Given("the user opens the URL {string}")
    public void theUserOpensTheUrl(String url) {
        homePage().openUrl(url);
    }

    @When("user enter email ID {string}")
    public void userEnterEmailId(String email) {
        homePage().enterEmail(email);
    }

    @When("click on Continue button")
    public void clickOnContinueButton() {
        homePage().clickContinueButton();
    }

    @When("user click on Continue button")
    public void userClickOnContinueButton() {
        homePage().clickContinueButton();
    }

    @When("user clicks on Continue button")
    public void userClicksOnContinueButton() {
        homePage().clickContinueButton();
    }

    @When("user open a new URL {string} in a new tab")
    public void userOpenANewUrlInANewTab(String url) {
        homePage().openUrlInNewTab(url);
    }

    @When("enter username {string} and password {string}")
    public void enterUsernameAndPassword(String username, String password) {
        homePage().enterBasicAuthCredentials(username, password);
    }

    @When("user read the first mail and get OTP")
    public void userReadTheFirstMailAndGetOtp() {
        otp = homePage().readFirstMailAndGetOtp();
    }

    @When("user click and read the first mail and get OTP")
    public void userClickAndReadTheFirstMailAndGetOtp() {
        otp = homePage().readFirstMailAndGetOtp();
    }

    @When("user switch to first tab")
    public void userSwitchToFirstTab() {
        homePage().switchToFirstTab();
    }

    @When("user enter OTP")
    public void userEnterOtp() {
        Assert.assertNotNull(otp, "OTP was not captured from the email page.");
        homePage().enterOtp(otp);
    }

    @Then("the page title should contain {string}")
    public void thePageTitleShouldContain(String expectedTitleText) {
        Assert.assertTrue(
                homePage().getTitle().contains(expectedTitleText),
                "Expected page title to contain: " + expectedTitleText + " but was: " + homePage().getTitle()
        );
    }

    @Then("the Aeria select account page should be displayed")
    public void theAeriaSelectAccountPageShouldBeDisplayed() {
        Assert.assertTrue(
                homePage().isAeriaSelectAccountPageDisplayed(),
                "Expected to be on Aeria select account page but current URL was: " + homePage().getCurrentUrl()
        );
    }

    @Then("verify user navigated to Select Account page")
    public void verifyUserNavigatedToSelectAccountPage() {
        Assert.assertTrue(
                homePage().isSelectAccountPageDisplayed(),
                "Expected Select Account page but current URL was: " + homePage().getCurrentUrl()
        );
    }

    @When("user select the tenant {string} from the list")
    public void userSelectTheTenantFromTheList(String tenantName) {
        homePage().selectTenant(tenantName);
    }

    @When("user clicks on {string} in side stepper")
    public void userClicksOnInSideStepper(String menuName) {
        homePage().clickSideStepperMenu(menuName);
    }

    @When("user clicks on {string} button")
    public void userClicksOnButton(String buttonName) {
        homePage().clickButton(buttonName);
    }

    @When("user enter an {string}")
    public void userEnterAn(String fieldName) {
        homePage().enterGeneratedOutletName(fieldName);
    }

    @When("user select any location from drop-down")
    public void userSelectAnyLocationFromDropDown() {
        homePage().selectAnyLocationFromDropdown();
    }

    @When("user enter {string}")
    public void userEnter(String fieldName) {
        homePage().enterGeneratedFssaiLicenseNumber(fieldName);
    }

    @When("user select {string} checkbox")
    public void userSelectCheckbox(String checkboxName) {
        if ("All Day".equalsIgnoreCase(checkboxName)) {
            homePage().selectAllDayCheckbox();
            return;
        }

        throw new IllegalArgumentException("Unsupported checkbox: " + checkboxName);
    }

    @When("user enter {string} and {string}")
    public void userEnterPhoneAndEmail(String phoneFieldName, String emailFieldName) {
        homePage().enterGeneratedPhoneAndEmail(phoneFieldName, emailFieldName);
    }

    @When("user click on {string} button")
    public void userClickOnButton(String buttonName) {
        if ("Next".equalsIgnoreCase(buttonName)) {
            homePage().clickNextButton();
            return;
        }

        homePage().clickButton(buttonName);
    }

    @When("the user selects {string} checkbox")
    public void theUserSelectsCheckbox(String checkboxName) {
        homePage().selectOutletConfigurationCheckbox(checkboxName);
    }

    @When("the user selects {string} for Order Channel")
    public void theUserSelectsForOrderChannel(String orderChannel) {
        homePage().selectOrderChannel(orderChannel);
    }

    @When("the user views the {string} dropdown")
    public void theUserViewsTheDropdown(String dropdownName) {
        homePage().viewDropdown(dropdownName);
    }

    @When("the user selects {string} from POS dropdown")
    public void theUserSelectsFromPOSDropdown(String posOption) throws InterruptedException {
        // Ensure the POS dropdown is visible/stable, then select
        homePage().viewDropdown("POS");
        homePage().selectPosOption(posOption);
    }

    @When("the user views {string} section")
    public void theUserViewsSection(String sectionName) {
        homePage().viewSection(sectionName);
    }

    @When("the user selects {string} option")
    public void theUserSelectsOption(String option) {
        homePage().selectRadioOption(option);
    }

    @When("the user selects {string} option for {string}")
    public void theUserSelectsOptionFor(String option, String fieldLabel) {
        // Use the field-scoped radio selector to ensure the option is selected within the correct field
        homePage().selectRadioOption(option);
    }

    @When("the user selects any option from {string} dropdown")
    public void theUserSelectsAnyOptionFromDropdown(String dropdownName) {
        homePage().selectAnyOptionFromDropdown(dropdownName);
    }

    @When("the user selects {string} option value from {string} dropdown")
    public void theUserSelectsOptionValueFromDropdown(String optionValue, String dropdownName) {
        // Open the dropdown to ensure options are loaded, then select the requested value
        homePage().viewDropdown(dropdownName);
        homePage().selectOptionValueFromDropdown(dropdownName, optionValue);
    }

    @When("the user clicks on Default GST Rate dropdown and selects \"5%\" option from drop down")
    public void clickOnDefaultGstRateDropdownAndSelectFivePercent() {

        homePage().selectOptionValueFromDropdown1("Default GST Rate", "5%");
    }






    @When("the user selects {string} for Menu Type")
    public void theUserSelectsForMenuType(String menuType) {
        homePage().selectMenuType(menuType);
    }

    @When("the user selects any cuisines from the drop down")
    public void theUserSelectAnyCuisineFromDropDown() {
        homePage().selectAnyCuisineFromDropdown();
    }

    @When("the user selects first cuisines from the drop down")
    public void theUserSelectsFirstCuisineFromDropDown() throws InterruptedException {
        Thread.sleep(500);
        homePage().selectFirstCuisineFromDropdown();
        Thread.sleep(500);
    }

    @When("the user get all the options from Cuisines dropdown and select any two options")
    public void theUserGetAllTheOptionsFromCuisinesDropdownAndSelectAnyTwoOptions() {
        homePage().selectAnyTwoCuisinesFromDropdown();
    }

    @When("the user select the checkbox {string} from Cuisines dropdown")
    public void theUserSelectTheCheckboxFromCuisinesDropdown(String cuisineName) {
        homePage().selectCuisineFromDropdown(cuisineName);
    }



    @When("the user enters {string} in Cost for Two field")
    public void theUserEntersInCostForTwoField(String costForTwo) {
        homePage().enterCostForTwo(costForTwo);
    }

    @When("the user clicks {string} button")
    public void theUserClicksButton(String buttonName) {
        homePage().clickButton(buttonName);
    }

    @Then("verify that user navigate to Overview Page")
    public void verifyThatUserNavigateToOverviewPage() {
        Assert.assertTrue(
                homePage().isOverviewPageDisplayed(),
                "Expected Overview page but current URL was: " + homePage().getCurrentUrl()
        );
    }

    private HomePage homePage() {
        if (homePage == null) {
            homePage = new HomePage();
        }
        return homePage;
    }
}
