Feature: Shopper Tokens

  @shopper_tokens
  Scenario: Shopper token - Create by any contact identifier
    When TOKEN_GENERATION a token should contain the contact identifier it is generated with: [shopperId] as [shi]
    When TOKEN_GENERATION a token should contain the contact identifier it is generated with: [contactId] as [coi]
    When TOKEN_GENERATION a token should contain the contact identifier it is generated with: [contactUserSuppliedId] as [cui]

  @shopper_tokens
  Scenario: Shopper token - Sets validity period
    When TOKEN_GENERATION a token should have the right validity period when generated with params [shopperId, validityInSeconds]

  @shopper_tokens
  Scenario: Shopper token - Sets metadata
    #When TOKEN_GENERATION a token should contain the metadata it is generated with: [shopperId, metadata]
