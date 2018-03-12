Feature: Account Card

  @account_creation @by_shopper_id

  Scenario: Create by shopperId
    When ACCOUNT_CREATION a contact exists but has no account: requires minimum parameters [shopperId, currency, userSuppliedId] and makes the following REST requests: [contactsSearchOneResult, accountCardSearchNoResults, accountCardCreate]

    When ACCOUNT_CREATION a contact exists and has an account: requires minimum parameters [shopperId, currency, userSuppliedId] and makes the following REST requests: [contactsSearchOneResult, accountCardSearchOneResult]

    When ACCOUNT_CREATION a contact doesn't exist: requires minimum parameters [shopperId, currency, userSuppliedId] and makes the following REST requests: [contactsSearchNoResults, contactCreate, accountCardSearchNoResults, accountCardCreate]


  @account_creation @by_contact_id

  Scenario: Create by contactId
    When ACCOUNT_CREATION a contact exists but has no account: requires minimum parameters [contactId, currency, userSuppliedId] and makes the following REST requests: [contactGet, accountCardSearchNoResults, accountCardCreate]

    When ACCOUNT_CREATION a contact exists and has an account: requires minimum parameters [contactId, currency, userSuppliedId] and makes the following REST requests: [contactGet, accountCardSearchOneResult]


  @account_creation @by_contact_id

  Scenario: Create by contactId - expecting errors
    When ACCOUNT_CREATION a contact doesn't exist: requires minimum parameters [contactId, currency, userSuppliedId] and makes the following REST requests: [contactsError404] and throws the following error: [LightrailException]

    # This scenario doesn't need a 'byShopperId' equivalent: attempting to create an account for a contact that doesn't exist should create the contact if the shopperId is provided


  @account_retrieval

  Scenario: Retrieve by shopperId
    When ACCOUNT_RETRIEVAL a contact exists: requires minimum parameters [shopperId, currency] and makes the following REST requests: [contactsSearchOneResult]
    When ACCOUNT_RETRIEVAL a contact doesn't exist: requires minimum parameters [shopperId, currency] and makes the following REST requests: [contactsSearchNoResults]


  @account_retrieval

  Scenario: Retrieve by contactId
    When ACCOUNT_RETRIEVAL a contact exists: requires minimum parameters [contactId, currency] and makes the following REST requests: [accountCardSearchOneResult]
    When ACCOUNT_RETRIEVAL a contact doesn't exist: requires minimum parameters [contactId, currency] and makes the following REST requests: [accountCardSearchNoResults]


  @account_details

  Scenario: retrieve details by shopperId

  Scenario: retrieve details by contactId


  @account_transactions

  Scenario: Charge by shopperId
    When ACCOUNT_TRANSACTION a contact exists and has an account: requires minimum parameters [shopperId, currency, value, userSuppliedId] and makes the following REST requests: [contactsSearchOneResult, accountCardSearchOneResult, accountCardPostTransaction]
    When ACCOUNT_TRANSACTION a contact exists and but has no account: requires minimum parameters [shopperId, currency, value, userSuppliedId] and makes the following REST requests: [contactsSearchOneResult, accountCardSearchNoResults] and throws the following error: [LightrailException]
    When ACCOUNT_TRANSACTION a contact doesn't exist: requires minimum parameters [shopperId, currency, value, userSuppliedId] and makes the following REST requests: [contactsSearchNoResults] and throws the following error: [LightrailException]

  Scenario: Charge by contactId

  Scenario: Pending charge

  Scenario: Capture pending

  Scenario: Void pending

  Scenario: Simulate charge (nsf: false)

  Scenario: Simulate charge (nsf: true)

  Scenario: Fund
