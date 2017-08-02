

# Lightrail Client Library

Lightrail is a modern platform for digital account credits, gift cards, promotions, and points (to learn more, visit [Lightrail](https://www.lightrail.com/)). Lightrail Client Library is a basic library for developers to easily connect with the Lightrail API using Java. If you are looking for specific use cases or other languages, check out [related projects](#related-projects) and the complete list of all [Lightrail libraries and integrations](https://github.com/Giftbit/Lightrail-API-Docs/blob/master/README.md#lightrail-integrations).

## Features ##

The following features are supported in this version:

- Gift Cards: create, retrieve, balance-check, charge, and fund.
- Account Credits: create, retrieve, balance-check, charge, and fund.

Note that the Lightrail API supports many other features and we are working on covering them in this library. For a complete picture of Lightrail API features check out the [Lightrail API documentation](https://www.lightrail.com/docs/). 

## Usage ##

Before using any parts of the library, you need to set up your Lightrail API key: 

```java
Lightrail.apiKey = "<your lightrail API key>";
```

### Gift Cards

A Lightrail gift card is a virtual device for issuing gift values. Each gift card has a specific `currency`, a `cardId`, as well as a `fullCode`, which is a unique unguessable alphanumeric string, usually released to the gift recipient in confidence. The recipient of the gift card can present the `fullCode` to redeem the gift value. For further explanation of cards and codes see the [Lightrail API documentation](https://www.lightrail.com/docs/).

#### Balance Check ####
For checking the balance of a gift code, depending on your use-case, you can call the`retrieveByCode()` or `retrieveByCardId()` from the `LightrailValue` class. The current balance of the gift card as well as some other information such as its `currency` and `state` are included in the returned object:

```java
Lightrail.apiKey = "<your lightrail API key>";
LightrailValue giftValue = LightrailValue.retrieveByCode("<GIFT CODE>");
int value = giftValue.getCurrentValue();
String currency = giftValue.getCurrency();
boolean isCardActive = ("ACTIVE".equals(giftValue.getState()));
```
The more generic  `retrieve()` method allows passing an expected `currency`. This call will end in a `CurrencyMismatchException` if the expected currency does not match the gift card currency which can make error-handling easier when processing gift redemptions.

```java
Lightrail.apiKey = "<your lightrail API key>";
Map<String, Object> giftValueParams = new HashMap<>();
   giftValueParams.put("code", "<GIFT CODE>");
   giftValueParams.put("currency", "USD");
LightrailValue giftValue = LightrailValue.retrieve(giftValueParams);
int value = giftValue.getCurrentValue();
```

#### Charging a Gift Card

In order to charge a gift code, you can use `createByCode()` or `createByCardId()`:

```java
Lightrail.apiKey = "<your lightrail API key>";
LightrailCharge giftCharge = LightrailCharge.createByCode("<GIFT CODE>", 735, "USD");
String chargeTxId = giftCharge.getTransactionId();
```

Note that Lightrail does not support currency exchange and the currency provided to these methods must match the currency of the gift card.

#### Authorize-Capture Flow

Using the `createPendingByCode()` or  `createPendingByCardId()` you can create a  pre-authorized pending transaction and later  `capture()` or `cancel()` it to either collect the pending charge or void it:

```java
Lightrail.apiKey = "<your lightrail API key>";
LightrailCharge giftCharge = LightrailCharge.createPendingByCode("<GIFT CODE>", 735, "USD");
//later on
giftCharge.capture();
//or
giftCharge.cancel();
```

Note that `capture()` and `cancel()` will each end in a new transaction with its own `transactionId`. If you need to record the transaction ID you can get it from the transaction object returned by these methods. 

#### Refunding a Charge

You can undo a charge by calling `refund()`. This will create a new `refund` transaction which will return the charged amount back to the card. If you need the transaction ID of the refund transaction, you can find it the returned transaction object. 

```java
Lightrail.apiKey = "<your lightrail API key>";
LightrailCharge giftCharge = LightrailCharge.createByCode("<GIFT CODE>", 735, "USD");
//later on
giftCharge.refund();
```

Note that this does not necessarily mean that the refunded amount is available for a re-charge. In the edge case where the fund for the original charge came from a promotion which has now expired, refunding will return those funds back to the now-expired value store and therefore the value will not be available for re-charge.

#### Funding a Gift Card

For funding a gift card, you can call `GiftFund.createByCardId()`. Note that the Lightrail API does not permit funding a gift card by its `code` and the only way to fund a card is by providing its `cardId`:

```java
Lightrail.apiKey = "<your lightrail API key>";
LightrailFund giftFund = LightrailFund.createByCardId("<CARD ID>", 735, "USD");
```

#### Creating Gift Cards

Gift cards are created as part of a Gift Card Program. You can set up a Gift Card Program by logging into the Lightrail [web application](https://www.lightrail.com). Once you have a Gift Card Program, you can copy and use the `programId` for creating Gift Cards in that program.

You can create a Gift Card by calling one of the `create()` methods in the `GiftCard` class. You need to provide the Program ID. Optionally, you can also provide an initial value, a start date, and an expiry date. For example:

```java
Lightrail.apiKey = "<your lightrail API key>";
GiftCard newGiftCard = GiftCard.create("<PROGRAM ID>", 400);
//or
String startDate = "2017-08-02T00:27:02.910Z";
String expiryDate = "2017-10-02T00:27:02.910Z";
GiftCard newGiftCard = GiftCard.create("<PROGRAM ID>", 400, startDate, expiryDate);
```

To pass more parameters, you can use the generic `create()` method which accepts a `Map<String,Object>`.

#### Retrieving Gift Cards

You can retrieve an exiting gift card by providing its `cardId`, using the `retrieve()` method. 

```java
Lightrail.apiKey = "<your lightrail API key>";
GiftCard existingGiftCard = GiftCard.retrieve("<CARD ID>");
```

#### Retrieving the Full Code

Gift codes are an unguessable alphanumeric string associate to a gift card which can be used to redeem the value of a card. This value is usually shared with the recipient of the gift card in confidence. To improve the confidentiality, Lightrail API endpoints which return a `card` object only return the last 4 digits of the code. For retrieving the `fullCode` you can call the `retrieveFullCode()` method on a `GiftCard` object. Usually you will email this value directly to the recipient of the Gift Card after creating it; we highly suggest that you refrain from persisting it.

```java
Lightrail.apiKey = "<your lightrail API key>";
GiftCard newGiftCard = GiftCard.create("<PROGRAM ID>", 400);
String fullCode = newGiftCard.retrieveFullCode();
//email the fullCode to the recipient of the gift.
```

Note that the `GiftCard` class does not cache the value of the code and every call to retrieve the full code, leads to an API call to Lightrail servers.

#### Freezing and Unfreezing Cards

Freezing a card will suspend its value until it is unfrozen. This is a useful method when investigating potential fraud. 

```java
Lightrail.apiKey = "<your lightrail API key>";
GiftCard existingGiftCard = GiftCard.retrieve("<CARD ID>");
existingGiftCard.freeze();
//later
existingGiftCard.unfreeze();
```

Note that freezing and unfreezing a card are special transactions and the corresponding transaction object will be returned by the these methods.

#### Gift Card Attributes

There are two sets of methods for reading a gift card attributes: 

- The getter methods prefixed with `get` will read a local copy of the attribute if available. 
- The `retrieve` methods make a call to the API and ensure that a fresh value for the attribute is fetched from the server. 

For example `getState()` will return the `state` of the card (i.e. whether it is active, frozen, etc.) according to the local copy, while `retrieveState()` will make a call to the API and ensure an up-to-date value is returned. These methods will enable you to budget your API calls. 

### Customer Accounts

Customer Accounts are values attached to a customer and are commonly used for customer rewards and account credit programs. For further explanation of this concept check out the [Lightrail API documentation](https://www.lightrail.com/docs/). 

#### Creating and Retrieving a Customer Account

You can create a new customer account by calling `CustomerAccount.create()`:

```java
Lightrail.apiKey = "<your lightrail API key>";
String email = "test@test.ca";
String firstName = "Test";
String lastName = "McTest";
CustomerAccount customerAccount = CustomerAccount.create(email, firstName, lastName);
String customerAccountId = customerAccount.getId();
```

Using the `customerAccountId`, you can later retrieve the account:

```java
String customerAccountId = customerAccount.getId();
//later
CustomerAccount customerAccount = CustomerAccount.retrieve(customerAccountId);
```

After creating a customer account, you need to define the currencies it can store. Each currency will be tracked and stored separately. For example, you can specify that a customer account will track USD and CAD values, each with a $5 initial balance:

```java
customerAccount.addCurrency("USD", 500)
               .addCurrency("CAD", 500);
```

#### Balance-Check

The `balance()` method on a `CustomerAccount` object returns a `LightrailValue` object which contains the available balance:

```java
int balance = customerAccount.balance("USD").getCurrentValue();
```

Alternatively, you can get the balance from the `LightrailValue`class by providing the customer account ID and the currency in question:

```java
int balance = LightrailValue.retrieveByCustomer("<CUSTOMER_ID", "USD").getCurrentValue(); 
```

#### Charging and Funding

You can charge or fund the account by specifying the currency and the amount. These calls will return a `LightrailCharge` or `LightrailFund` object containing the transaction details. 

```java
LightrailCharge charge = customerAccount.charge(100, "USD");
String chargeTxId = charge.getTransactionId();

LightrailFund fund = customerAccount.fund(200, "CAD");
String fundTxId = fund.getTransactionId();
```

Alternatively, you can use the `LightrailCharge` or `LightrailFund` class to charge or fund a customer in a similar way to that of gift cards:

```java
Lightrail.apiKey = "<your lightrail API key>";
LightrailFund fund = LightrailFund.createByCustomer("<CUSTOMER_ID", 200, "USD");
String fundTxId = fund.getTransactionId();
```

Similar to gift cards, an account can also be charged by following an authorize-capture flow:  

```java
LightrailCharge charge = customerAccount.pendingCharge(100, "USD");
//later on        
charge.capture();
//or        
charge.cancel();        
```

Or alternatively:

```java
Lightrail.apiKey = "<your lightrail API key>";
LightrailCharge charge = LightrailCharge.createPendingByCustomer("<CUSTOMER_ID", 100, "USD");
//later on        
charge.capture();
//or        
charge.cancel();
```

#### Single-Currency Accounts
For simpler cases where only one currency is defined for the customer account, you can use a simpler interface for interacting with the account without having to specify the currency for each and every call. For example, if you have a points program (for which the standard currency code is `XXX` ) you can proceed as the following:

```java
Lightrail.apiKey = "<your lightrail API key>";
String email = "test@test.ca";
String firstName = "Test";
String lastName = "McTest";
String currency = "XXX";
int initialBalance = 500;

CustomerAccount customerAccount = CustomerAccount.create(email,
                                                         firstName, 
                                                         lastName, 
                                                         currency, 
                                                         initialBalance);
//later on
LightrailCharge charge = customerAccount.pendingCharge(300);
//later on
charge.capture();
//later on
customerAccount.fund(100);
int balance = customerAccount.balance().getCurrentValue();
```

Note that if there is more than one currency defined for the account these calls will throw a `BadParameterException`.

## Related Projects

- [Lightrail-Stripe Integration](https://github.com/Giftbit/lightrail-stripe-java)

## Installation ##

### Maven
You can add this library as a dependency in your `maven` `POM` file as:
```xml
<dependency>
  <groupId>com.lightrail</groupId>
  <artifactId>lightrail-client</artifactId>
  <version>1.1.1</version>
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

The only dependency of this library is `gson`. 
```xml
<dependency>
  <groupId>com.google.code.gson</groupId>
  <artifactId>gson</artifactId>
  <version>2.2.4</version>
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

### 1.2.0
- Gift cards: create, retrieve, and freeze/unfreeze.

### 1.1.1
- Refund transactions.

### 1.1.0

- Customer account credits: create, charge, fund, and balance-check.
- Simpler shortcut methods creating `GiftCharge`, `GiftFund`, and retrieving `GiftValue`.

### 1.0.2

- Minor improvements to the interface for creating gift charge and gift fund.

### 1.0.1 ###

- Basic API functions: gift code balance check, charge a gift code, and fund a gift card.

