

# Lightrail Client Library

Lightrail is a modern platform for digital account credits, gift cards, promotions, and points (to learn more, visit [Lightrail](https://www.lightrail.com/)). Lightrail Client Library is a basic library for developers to easily connect with the Lightrail API using Java. If you are looking for specific use cases or other languages, check out [related projects](#related-projects). 

## Features ##

The following features are supported in this version:

- Gift code balance check. 
- Charge a gift code.
- Fund a gift card.

Note that the Lightrail API supports many other features and we are working on covering them in this library. For a complete list of Lightrail API features check out the [Lightrail API documentation](https://www.lightrail.com/docs/).

## Usage ##

### Gift Code Balance Check ###
For checking the balance of a gift code, simply call `GiftCode.retrieve()`. You can get the 
current balance of the gift code from the returned object as well as some other information,
such as its currency:
```Java
Lightrail.apiKey = "<your lightrail API key>";
GiftValue giftValue = GiftValue.retrieve("<GIFT CODE>");
int value = giftValue.getCurrentValue();
String currency = giftValue.getCurrency();
```
You can also pass on an expected currency to the `retrieve()` call to ensure the gift code has the right currency. This call will end in a `CurrencyMismatchException` if the expected currency does not match the gift code currency.

```Java
Lightrail.apiKey = "<your lightrail API key>";
Map<String, Object> giftValueParams = new HashMap<>();
   giftValueParams.put("code", "<GIFT CODE>");
   giftValueParams.put("currency", "USD");
GiftValue giftValue = GiftValue.retrieve(giftValueParams);
int value = giftValue.getCurrentValue();
```

### Charging a Gift Code

In order to charge a gift code, call `GiftCharge.create()`. The minimum required parameters are:

- the gift code,
- the amount to charge in the smallest currency unit, e.g. cents, and
- the currency.

```Java
Lightrail.apiKey = "<your lightrail API key>";
Map<String, Object> giftChargeParams = new HashMap<>();
   giftChargeParams.put("code", "<GIFT CODE>");
   giftChargeParams.put("amount", 735);
   giftChargeParams.put("currency", "USD");

GiftCharge giftCharge = GiftCharge.create(giftChargeParams);
```

Or more concisely:

```Java
Lightrail.apiKey = "<your lightrail API key>";
GiftCharge giftCharge = GiftCharge.create("<GIFT CODE>", 735, "USD");
String transactionId = giftCharge.getTransactionId();
```

Note that Lightrail does not support currency exchange and the currency for the transaction must match the currency of the gift code.

### Auth-Capture on a Gift Code

By passing the additional parameter `capture=false` when charging a gift code, you can direct Lightrail to treat it as a pre-authorized pending transaction. You can later call `capture()` or `cancel()` on the resulting `GiftCharge` object to either collect the pending charge or void it.

```Java
Lightrail.apiKey = "<your lightrail API key>";
Map<String, Object> giftChargeParams = new HashMap<>();
   giftChargeParams.put("code", "<GIFT CODE>");
   giftChargeParams.put("amount", 735);
   giftChargeParams.put("currency", "USD");
   giftChargeParams.put("capture", false);

GiftCharge giftCharge = GiftCharge.create(giftChargeParams);
//later on
giftCharge.capture();
//or
giftCharge.cancel();
```

Or more concisely:

```Java
Lightrail.apiKey = "<your lightrail API key>";
GiftCharge giftCharge = GiftCharge.create("<GIFT CODE>", 735, "USD");
//later on
giftCharge.capture();
//or
giftCharge.cancel();
```


### Funding a Gift Card

In order to fund a gift card you need pass the gift card ID as a parameter and create a `GiftFund` object. Note that the Lightrail API does not permit funding a gift code directly and you can only fund the corresponding gift card (see the [API documentation](https://www.lightrail.com/docs/) for an explanation of cards and codes).

```Java
Lightrail.apiKey = "<your lightrail API key>";
Map<String, Object> giftFundParams = new HashMap<String, Object>();
        giftFundParams.put("cardId", "<CARD ID>");
        giftFundParams.put("currency", "USD");
        giftFundParams.put("amount", 735);
GiftFund giftFund = GiftFund.create(giftFundParams);
```

Or more concisely:

```Java
Lightrail.apiKey = "<your lightrail API key>";
GiftFund giftFund = GiftFund.create("<CARD ID>", 735, "USD");
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

