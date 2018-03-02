Feature: Account Card

# NOTE These are tags. Scenarios are tagged individually by adding these right above the #Scenario def like this:
  @account_creation @by_shopper_id

  Scenario: Create by shopperId
  # NOTE Each of these lines (steps) can start with any of the keywords (Given, When, Then, And, But or *). They're completely interchangeable.
  # NOTE Parameters passed to the stepdef are enclosed in [] for simplicity; any other delimiter could be used if the stepdef's regex is updated. Params listed correspond to keys in variables.json which is read by the stepdef file to actually access the data.
  # NOTE All of these are formatted to match the same regex in account_stepdefs.rb. Adding/changing steps may require the regex to be updated.
  # NOTE Consider in the future: other tests (contact creation, etc) could easily be formatted the same way. Consider adding a feature-identifying keyword to each step: eg, `When ACCOUNT CREATION a contact exists [...]`
    When a contact exists but has no account: requires minimum parameters [shopperId, currency, userSuppliedId] and makes the following REST requests: [contactsSearchOneResult, accountCardSearchNoResults, accountCardCreate]

    When a contact exists and has an account: requires minimum parameters [shopperId, currency, userSuppliedId] and makes the following REST requests: [contactsSearchOneResult, accountCardSearchOneResult]

    When a contact doesn't exist: requires minimum parameters [shopperId, currency, userSuppliedId] and makes the following REST requests: [contactsSearchNoResults, contactCreate, accountCardSearchNoResults, accountCardCreate]


  @account_creation @by_contact_id

  Scenario: Create by contactId
    When a contact exists but has no account: requires minimum parameters [contactId, currency, userSuppliedId] and makes the following REST requests: [contactGet, accountCardSearchNoResults, accountCardCreate]

    When a contact exists and has an account: requires minimum parameters [contactId, currency, userSuppliedId] and makes the following REST requests: [contactGet, accountCardSearchOneResult]


  @account_creation @by_contact_id

  Scenario: Create by contactId - expecting errors
    When a contact doesn't exist: requires minimum parameters [contactId, currency, userSuppliedId] and makes the following REST requests: [contactsError404] and throws the following error: [CouldNotFindObjectError]


  @account_retrieval

  Scenario: Retrieve by shopperId

  Scenario: Retrieve by contactId


  @account_details

  Scenario: retrieve details by shopperId

  Scenario: retrieve details by contactId


  @account_transactions

  Scenario: Charge by shopperId

  Scenario: Charge by contactId

  Scenario: Pending charge

  Scenario: Capture pending

  Scenario: Void pending

  Scenario: Simulate charge (nsf: false)

  Scenario: Simulate charge (nsf: true)

  Scenario: Fund
