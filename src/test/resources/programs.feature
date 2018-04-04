Feature: Account Card

  @programs @program_creation
  Scenario: Create program
    When PROGRAM_CREATION a program is created with minimum parameters [userSuppliedId, name, currency, valueStoreType] the following REST requests are made: [programCreate]

  @programs @program_retrieval
  Scenario: Retrieve program
    When PROGRAM_RETRIEVAL a program is retrieved by [programId] the following REST requests are made: [programGet]

