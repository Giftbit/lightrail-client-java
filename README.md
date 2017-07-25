

# Lightrail Client Library

Lightrail is a modern platform for digital account credits, gift cards, promotions, and points (to learn more, visit [Lightrail](https://www.lightrail.com/)). Lightrail Client Library is a basic library for developers to easily connect with the Lightrail API using Java. If you are looking for specific use cases or other languages, check out [related projects](#related-projects). 

## Features ##

The following features are supported in this version:

- Gift Cards: balance check, charge, and fund.
- Customer Account Credits: create, retrieve, delete, charge, and fund.

Note that the Lightrail API supports many other features and we are working on covering them in this library. For a complete list of Lightrail API features check out the [Lightrail API documentation](https://www.lightrail.com/docs/). For a list of other Lightrail libraries and integration products, check out the [Lightrail Integration page](https://github.com/Giftbit/Lightrail-API-Docs/blob/usecases/Integrations.md).

## Usage ##

### Gift Cards

A Lighrail gift card is a virtual device for issuing gift values. Each gift card has a specific currency, a `cardId`, as well as a `code`, which is a unique unguessable alphanumeric character string, usually released to the gift recipient in confidence. The recipient of the gift card can present the `code` to redeem the gift value. For further explanation of cards and codes see the [Lightrail API documentation](https://www.lightrail.com/docs/).

#### Balance Check ####
For checking the balance of a gift code, depending on your use-case, you can call any of the following methods:

- `GiftValue.retrieveByCode()`, 

- `GiftValue.retrieveByCardId()`, or

- `GiftValue.retrieve()`.


You can get the current balance of the gift card from the returned object as well as some other information, such as its currency:

```java
Lightrail.apiKey = "<your lightrail API key>";
GiftValue giftValue = GiftValue.retrieveByCode("<GIFT CODE>");
int value = giftValue.getCurrentValue();
String currency = giftValue.getCurrency();
```
The more generic  `retrieve()` method will allow passing an expected `currency`. This call will end in a `CurrencyMismatchException` if the expected currency does not match the gift card currency which makes it easier to process a gift code redemption when a customer provides the `code` at the checkout.

```java
Lightrail.apiKey = "<your lightrail API key>";
Map<String, Object> giftValueParams = new HashMap<>();
   giftValueParams.put("code", "<GIFT CODE>");
   giftValueParams.put("currency", "USD");
GiftValue giftValue = GiftValue.retrieve(giftValueParams);
int value = giftValue.getCurrentValue();
```

#### Charging a Gift Card

In order to charge a gift code, you can use `createByCode()` or `createByCardId()`:

```java
Lightrail.apiKey = "<your lightrail API key>";
GiftCharge giftCharge = GiftCharge.createByCode("<GIFT CODE>", 735, "USD");
String transactionId = giftCharge.getTransactionId();
```

Note that Lightrail does not support currency exchange and the currency provided to these methods must match the currency of the gift card.

#### Authorize-Capture Flow

Using the `createPendingByCode()` or  `createPendingByCardId()` you can create a  pre-authorized pending transaction and later  `capture()` or `cancel()` it to either collect the pending charge or void it:

```java
Lightrail.apiKey = "<your lightrail API key>";
GiftCharge giftCharge = GiftCharge.createPendingByCode("<GIFT CODE>", 735, "USD");
//later on
giftCharge.capture();
//or
giftCharge.cancel();
```


#### Funding a Gift Card

For funding a gift card, you can call `GiftFund.createByCardId()`. Note that the Lightrail API does not permit funding a gift card by its `code` and the only way to fund a card is by providing its `cardId`:

```java
Lightrail.apiKey = "<your lightrail API key>";
GiftFund giftFund = GiftFund.createByCardId("<CARD ID>", 735, "USD");
```

### Customer Accounts

Customer Accounts are values attached to a customer and are commonly used for customer rewards and account credit programs. For further explanation of this concept check out the [Lightrail API documentation](https://www.lightrail.com/docs/). 

#### Creating, Retrieving, and Deleting a Customer Account

You can create a new customer account by calling `CustomerAccount.create()`:

```java
Lightrail.apiKey = "<your lightrail API key>";
String email = "test@testy.ca";
String firstName = "Test";
String lastName = "McTestFace";
CustomerAccount customerAccount = CustomerAccount.create(email, firstName, lastName);
String customerAccountId = customerAccount.getId();
```

Using the `customerAccountId`, you can later retrieve or delete the account:

```java
String customerAccountId = customerAccount.getId();
//later
CustomerAccount customerAccount = CustomerAccount.retrieve(customerAccountId);
//later
CustomerAccount.delete(customerAccountId);
```

#### Balance Check, Charging, and Funding

After creating a customer account, you need to define currencies it will store. Each currency will be tracked and stored separately. For example, you can specify that a customer account will track USD and CAD values, each with a $5 initial balance:

```java
customerAccount.addCurrency("USD", 500);
customerAccount.addCurrency("CAD", 500);
```

Once the currencies are set up, calling the `balance()` method and providing the currency in question will return a `GiftValue` object which contains the available balance for that currency:

```java
int balance = customerAccount.balance("USD").getCurrentValue();
```

You can charge or fund the account by specifying the currency and the amount:

```java
customerAccount.charge(100, "USD");
customerAccount.fund(200, "CAD");
```

Similar to gift cards, an account can also be charged by following an authorize-capture flow:  

```java
GiftCharge charge = customerAccount.pendingCharge(100, "USD", false);
//later on        
charge.capture();
//or        
charge.cancel();        
```

#### Single-Currency Accounts

For the simpler cases where there is only a single currency for the account credits, you can specify this at the time of creating the account and get a simpler interface for interacting without having to specify the currency for each and every call. For example, if you have a points program (for which the standard currency code is `XXX` ), you can proceed as the following:

```java
String email = "test@test.ca";
String firstName = "Test";
String lastName = "McTest";
String currency = "XXX";
int initialBalance = 500;

CustomerAccount customerAccount = CustomerAccount.create(email, firstName, lastName, currency, initialBalance);

//later on
customerAccount.pendingCharge(300);
//later on
customerAccount.fund(100);
int balance = customerAccount.balance().getCurrentValue();
```



## Related Projects

- [Lightrail-Stripe Integration](https://github.com/Giftbit/lightrail-stripe-java)

## Installation ##

### Maven
You can add this library as a dependency in your `maven` `POM` file as:
```xml
<dependency>
  <groupId>com.lightrail</groupId>
  <artifactId>lightrail-client</artifactId>
  <version>1.0.2</version>
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

### 1.0.2

- Minor improvements to the interface for creating gift charge and gift fund.

### 1.0.1 ###

- Basic API functions: gift code balance check, charge a gift code, and fund a gift card.

