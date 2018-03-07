Feature: Account Card

  @account_creation @by_shopper_id @only_this_one

  Scenario: Create by shopperId
    When a contact exists but has no account: requires minimum parameters [shopperId, currency, userSuppliedId] and makes the following REST requests: [contactsSearchOneResult, accountCardSearchNoResults, accountCardCreate]

    When a contact exists and has an account: requires minimum parameters [shopperId, currency, userSuppliedId] and makes the following REST requests: [contactsSearchOneResult, accountCardSearchOneResult]

    When a contact doesn't exist: requires minimum parameters [shopperId, currency, userSuppliedId] and makes the following REST requests: [contactsSearchNoResults, contactCreate, accountCardSearchNoResults, accountCardCreate]


  @account_creation @by_contact_id @only_this_one

  Scenario: Create by contactId
    When a contact exists but has no account: requires minimum parameters [contactId, currency, userSuppliedId] and makes the following REST requests: [contactGet, accountCardSearchNoResults, accountCardCreate]

    When a contact exists and has an account: requires minimum parameters [contactId, currency, userSuppliedId] and makes the following REST requests: [contactGet, accountCardSearchOneResult]


  @account_creation @by_contact_id  @only_this_one

  Scenario: Create by contactId - expecting errors
    When a contact doesn't exist: requires minimum parameters [contactId, currency, userSuppliedId] and makes the following REST requests: [contactsError404] and throws the following error: [LightrailException]


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
