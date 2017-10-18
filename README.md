

# Lightrail Client Library

Lightrail is a modern platform for digital account credits, gift cards, promotions, and points —to learn more, visit [Lightrail](https://www.lightrail.com/). The goal of this client library is to facilitate integrating with the Lightrail API in Java. If you are looking for specific use-cases or other languages, check out the *Integrations* section of the [Lightrail API documentation](https://www.lightrail.com/docs/).

## Features ##

The following features are supported in this version:

- Account Credits: create, retrieve, balance-check, and create/simulate transactions.
- Gift Cards: create, retrieve, balance-check, and create/simulate transactions.

Note that the Lightrail API supports many other features and we are still working on covering them in this library. For a complete list of Lightrail API features check out the [Lightrail API documentation](https://www.lightrail.com/docs/). 

## Usage ##

Before making any calls, set up your Lightrail API key. 

```java
Lightrail.apiKey = "<your lightrail API key>";
```

### Account Credits

The `LightrailContact` class provides the functionality to support account credit use-cases. For further discussion of Account Cards check out the [Lightrail API documentation](https://www.lightrail.com/docs/). 

#### Creating Contacts

To create a new customer account, call `LightrailContact.create()`:

```java
String email = "test@test.ca";
String firstName = "Test";
String lastName = "McTest";
LightrailContact contact = LightrailContact.create(email, firstName, lastName);
```

#### Retrieving Customer Accounts

Using the `contactId`, you can later retrieve the Contact. This will fetch the Contact object and all of its associated Account Cards.

```java
String contactId = contact.getContactId();
//later
LightrailContact contact = LightrailContact.retrieve(contactId);
```

After creating a contact, you need to define the currencies it should support. Each currency will be tracked and stored separately by a different Account Card. For example, you can specify that a Contact will have USD and CAD accounts, each with a $5 initial balance:

```java
//...
LightrailContact contact = LightrailContact.create(email, firstName, lastName);
contact.addCurrency("USD", 500)
       .addCurrency("CAD", 500);
```

#### Maximum Value and Balance-Checking

The `retrieveMaximumValue()` methods of a `LightrailContact` object return the maximum value of the account in a given currency. Note that when using conditional promotions, some portions of this value might only be available under certain conditions.

```java
int maximumValue = contact.retrieveMaximumValue("USD");
```

To get the precise value a Contact can contribute to a specific Transaction, use one of the `byContact` or `byShopperId` methods in `Transaction.Simulate`. 

```java
String contactId = contact.getContactId();
int txValue = -20455;
String currency = "USD";
Metadata metadata = new Metadata();
//fill in the metadata
LightrailTransaction simulatedTx = 
  LightrailTransaction.Simulate.byContact(contactId, txValue, currency, metadata);
//or
String shopperId = contact.getShopperId();
LightrailTransaction simulatedTx = 
  LightrailTransaction.Simulate.byShopperId(shopperId, txValue, currency, metadata);
int availableValue = 0 - simulatedTx.getValue();
```

#### Transactions

To transact against a Contact, call one of the `createTransaction` methods in `LightrailContact`. 

```java
LightrailTransaction tx = contact.createTransaction(-200, "USD");
```

Alternatively, you can use one of the `byContact` or `byShopperId` methods in `LightrailTransaction.Create`. The former needs the `contactId` and the latter needs the `userSuppliedId` of the Contact in question, also known as `shopperId`.

```java
String shopperId = contact.getShopperId();
Metadata metadata = new Metadata();
//fill in the metadata
LightrailTransaction tx = LightrailTransaction.Create.byShopperId(shopperId, -200, "USD", metadata);
```

#### Authorize-Capture

To create a pending Transaction, use one of the `pendingByContact` or `pendingByShopperId` methods in `LightrailTransaction.Create`. You need to call `capture()` or `doVoid()` on the resulting `LightrailTransaction` object later.

```java
LightrailTransaction tx = LightrailTransaction.Create.pendingByContact("<contactId>", -100, "USD");
//later        
tx.capture();
//or        
tx.doVoid();
```

Note that `capture()` and `doVoid()` return a new `LightrailTransaction` object.  

#### Refunding

To undo a drawdown Transaction, call one of the the `refund()` methods on the Transaction object. You can retrieve the Transaction object using a suitable `retrieve` method from `LightrailTransaction`. Note that `refund()` returns a new `LightrailTransaction` object.  

```java
String cardId = contact.getCardFor("USD").getCardId();
String txId = "...";
LightrailTransaction tx = LightrailTransaction.Retrieve.byCardIdAndTransactionId(cardId, txId);
//later:
LightrailTransaction refundTx = tx.refund();
```

#### Freezing and Unfreezing Accounts

Freezing an account suspends its value until it is unfrozen. This is a suitable method for investigating potential fraud. To freeze or unfreeze an Account, get the corresponding Account Card and call its `freeze()` or `unfreeze()` method.

```java
AccountCard accCard = contact.getCardFor("USD");
accCard.freeze();
//later:
accCard.unfreeze();
```

Note that freezing and unfreezing a card are special transactions and the corresponding `LightrailTransaction` object will be returned by these methods.

#### Single-Currency Accounts

For simpler cases where only one currency is defined for the Contact, you can use a simpler interface without having to specify the currency for each and every call. For example, if you have a points program (for which the standard currency code is `XXX` ) you can use these simpler methods as the following:

```java
String email = "test@test.ca";
String firstName = "Test";
String lastName = "McTest";
String currency = "XXX";
int initialBalance = 500;

LightrailContact contact = LightrailContact.create(email,
                                                   firstName, 
                                                   lastName, 
                                                   currency, 
                                                   initialBalance);
//later 
LightrailTransaction tx = contact.createPendingTransaction(-300);
//later 
tx.capture();
//or        
tx.doVoid();
```

Note that if the Contact has more than one currency, these calls will throw a `BadParameterException`.

### Gift Cards

A Lightrail Gift Card is a virtual device for issuing gift values. Each Gift Card has a specific `currency`, a `cardId`, as well as a `fullCode`, which is a unique unguessable alphanumeric string, usually released to the gift recipient in confidence. The recipient of the Gift Card can present the `fullCode` to redeem the gift value. For further discussion of Gift Cards check out the [Lightrail API documentation](https://www.lightrail.com/docs/).

#### Creating Gift Cards

Gift Cards are created as part of a Gift Card Program. You can set up a Gift Card Program using the Lightrail [Web App](https://www.lightrail.com). Once you have a Gift Card Program, you need to provide its `programId` for creating Gift Cards in that program. To create a Gift Card, call one of the `create()` methods in the `GiftCard` class. For example:

```java
String programId = "...";
int initialValue = 400;
GiftCard newGiftCard = GiftCard.create(programId, initialValue);
//or
String startDate = "2017-08-02T00:27:02.910Z";
String expiryDate = "2017-10-02T00:27:02.910Z";
Metadata metadata = new Metadata();
        metadata.put("orderId", "x72a3sx5e");
        metadata.put("recipientEmail", "recipient@test.ca");
        metadata.put("purchaserName", "Alice Liddell");
        metadata.put("purchaserEmail", "alice@wonderland.ca");
GiftCard newGiftCard = GiftCard.create(programId, 
                                       initialValue, 
                                       startDate, 
                                       expiryDate, 
                                       metadata);
```

To pass more parameters such as `userSuppliedId`, you can use the generic `create()` method which accepts a `RequestParameters` object, a subtype of `Map<String,Object>`).

#### Retrieving the Gift Code

To retrieve the `fullCode` of a Gift Card, call the `retrieveFullCode()` method on a `GiftCard` object. Usually, you make this call after creating the Gift Card to email the Gift Code to the gift recipient. We advise that you refrain from persisting the `fullcode` in your system and retrieve it from the API whenever you need it.

```java
String programId = "...";
int initialValue = 400;
GiftCard newGiftCard = GiftCard.create(programId, initialValue);
String fullCode = newGiftCard.retrieveFullCode();
//email the fullCode to the recipient of the gift.
```

#### Retrieving Gift Cards

To retrieve an existing Gift Card, use one of the `retrieve()` methods. 

```java
GiftCard existingGiftCard = GiftCard.retrieveByCardId("<cardId>");
//
GiftCard existingGiftCard = GiftCard.retrieveByUserSuppliedId("<userSuppliedId>");
```

#### Card Details

To get details of the available value and promotions on a Gift Card, call the `retrieveCardDetails()`method. The principal value of the Card as well as its attached promotions are returned in the `CardDetails` object.

```java
CardDetails cardDetails = giftCard.retrieveCardDetails();
for (ValueStore valueStore : cardDetails.getValueStores()) {
   if("ACTIVE".equals(valueStore.getState()))
      //print valueStore details. 
}
```

To get the details of a Gift Card based on its `fullcode`, use the following static method in the `GiftCard` class.

```java
String fullcode = "...";
CardDetails cardDetails = GiftCard.retrieveCardDetailsByCode(fullcode);
```

#### Maximum Value and Balance-Checking ####

To get the maximum possible value of a Gift Card, use the `retrieveMaximumValue()` method. Note that not all of this value is available for every transaction as there might be some conditional Promotions attached to the Card which are only unlocked under certain conditions. 

```java
int maximumValue = giftCard.retrieveMaximumValue();
```

To get the exact value of a Gift Card in the context of a transaction you can use one of the `simulate` methods in the `Transaction` class and check the `value` of the returned simulated transaction. Note that this value shows the maximum drawdown transaction value that will go through for this Card in that context, so it is negative.

```java
String fullcode = "...";
int orderValue = -20455;
String currency = "USD";
Metadata metadata = new Metadata();
//fill in the metadata
LightrailTransaction simulatedTx = 
  LightrailTransaction.Simulate.byCode(fullcode, orderValue, currency, metadata);
int availableValue = 0 - simulatedTx.getValue();
```

#### Transactions

To create a Transaction on a Gift Card use a suitable methods in `LightrailTransaction.Create`. For example:

```java
String fullcode = "...";
int orderValue = -20455;
String currency = "USD";
Metadata metadata = new Metadata();
//fill in the metadata
LightrailTransaction tx = 
  LightrailTransaction.Create.byCode(fullcode, orderValue, currency, metadata);
```

Note that Lightrail does not support currency exchange and the currency provided to these methods must match the currency of the Card.

#### Authorize-Capture

To create a `pending` Transaction, use one of the methods in `LightrailTransaction.Create` with `pending` in its name. You should later call one of the available `capture()` or `doVoid()` methods on the returned pending Transaction object to either collect the pending Transaction or cancel it:

```java
String fullcode = "...";
int orderValue = -20455;
String currency = "USD";
Metadata metadata = new Metadata();
//fill in the metadata
LightrailTransaction pendingTx = 
  LightrailTransaction.Create.pendingByCode(fullcode, orderValue, currency, metadata);
//later:
LightrailTransaction capturedTx = pendingTx.capture();
//or
LightrailTransaction voidedTx = pendingTx.cancel();
```

Note that `capture()` and `doVoid()` return a new `LightrailTransaction` object.  

#### Refunding

To undo a drawdown Transaction, call one of the the `refund()` methods on the Transaction object. Note that `refund()` returns a new `LightrailTransaction` object.  

```java
String cardId = "...";
String transactionId = "...";
LightrailTransaction tx = 
  LightrailTransaction.Retrieve.byCardIdAndTransactionId(cardId, transactionId);
//later:
LightrailTransaction refundTx = tx.refund();
```

#### Freezing and Unfreezing Cards

Freezing and unfreezing Gift Cards is similar to Account Cards.

```java
String cardId = "...";
GiftCard existingGiftCard = GiftCard.retrieveByCardId(cardId);
existingGiftCard.freeze();
//later:
existingGiftCard.unfreeze();
```

Note that freezing and unfreezing a card are special transactions and the corresponding `LightrailTransaction` object will be returned by these methods.

## Related Projects

- [Lightrail-Stripe Integration](https://github.com/Giftbit/lightrail-stripe-java)

## Installation ##

### Maven
You can add this library as a dependency in your `maven` `POM` file as:
```xml
<dependency>
  <groupId>com.lightrail</groupId>
  <artifactId>lightrail-client</artifactId>
  <version>2.0.1</version>
</dependency>
```

## Build And Test ##
You can build  this library from source using `maven`. Assuming that `maven` is installed and configured in your 
environment, you can simply download or clone the source and invoke:
```sh
$ mvn clean package -DskipTests
```
Note that this will skip the unit tests. In order to run the tests, you will need to set the 
following parameters in a property file names `_test-config.property`. A template 
is provided in `test-config-template.properties`:
- `lightrail.testApiKey`: the API key for Lightrail. You can find your test API key in your account at 
  [lightrail.com](lightrail.com). 
- `happyPath.code`: a test gift code with at least $5 value.
- `happyPath.code.cardId`: the card ID corresponding to the above gift code.
- `happyPath.code.currency`: the currency for this code, preferably `USD`.

## Requirements ## 
This library requires `Java 1.7` or later.

## Dependencies ##

The only dependencies of this library are `gson` and `jsonwebtoken`. 
```xml
<dependency>
  <groupId>com.google.code.gson</groupId>
  <artifactId>gson</artifactId>
  <version>2.2.4</version>
</dependency>
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt</artifactId>
  <version>0.9.0</version>
</dependency>
```
If your project already depends on a different version, make sure the versions 
are compatible. We will periodically update the dependency to the latest version.

The following dependency is also necessary if you want to run the unit tests.
```xml
<dependency>
  <groupId>junit</groupId>
  <artifactId>junit</artifactId>
  <version>4.12</version>
  <scope>test</scope>
</dependency>
```
## Changelog ## 

### 2.0.1

- Supporting `shopperId` for an easier checkout process for Account Cards. 
- Client Token Factory for issuing JWTs for certain endpoints.

### 2.0.0

- Supporting simulated Transactions and metadata.

### 1.2.0

- Gift cards: create, retrieve, and freeze/unfreeze.

### 1.1.1
- Refund transactions.

### 1.1.0

- Customer account credits: create, charge, fund, and balance-check.

### 1.0.2

- Minor improvements to the interface for creating gift transactions.

### 1.0.1 ###

- Basic API functions: gift code balance check, transacting against a gift code and gift card.

