Feature: Sample application smoke test

  Scenario: Verify application page title
    Given the user opens the URL "https://staging.dashboard.aeria.world"
    When user enter email ID "ak@aeria.world"
    And click on Continue button
    And user open a new URL "http://13.202.22.2/" in a new tab
    And enter username "devteam" and password "Aeria@G9#"
    And user click and read the first mail and get OTP
    And user switch to first tab
    And user enter OTP
  And user click on Continue button
  Then verify user navigated to Select Account page
  When user select the tenant "Gravity by Prestige" from the list

  And user clicks on Continue button
  And user clicks on "Outlets" in side stepper
  And user clicks on "Add Outlet" button
  And user enter an "Outlet Name"
  And user select any location from drop-down
  And user enter "FSSAI License Number"
  And user select "All Day" checkbox
  And user enter "Phone Number" and "Email"
  And user click on "Next" button
    #Given the user is on Outlet Creation Configuration page
    When the user selects "Delivery to Desk" checkbox
    And the user selects "Takeaway" checkbox
    And the user selects "Dine-In" checkbox
    And the user selects "App" for Order Channel
    And the user selects "Aeria(Default)" from POS dropdown
    #And the user views "Is Handover code required from customers" section
    And the user selects "Yes" option
    #And the user views "Deliveries will be handled by outlet?" section
    And the user selects "Self" option
    And the user selects "Exclusive" option for "Prices Inclusive or Exclusive of GST?"
    And the user clicks on Default GST Rate dropdown and selects "5%" option from drop down
    And the user selects "Veg & Non-Veg" for Menu Type
    And the user select the checkbox "Chinese" from Cuisines dropdown
    And the user enters "500" in Cost for Two field
    And the user clicks "Done" button

    #Then the outlet should be created with all selected fulfillment modes
    #And the outlet details should display all three fulfillment modes



  #Then verify that user navigate to Overview Page
#    Then the page title should contain "Example"
