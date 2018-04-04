# Lightrail Client for Java

Lightrail is a modern platform for digital account credits, gift cards, promotions, and points (to learn more, visit Lightrail). This is a basic library for developers to easily connect with the Lightrail API using Java. If you are looking for specific use cases or other languages, check out the complete list of all Lightrail [libraries and integrations](https://www.lightrail.com/docs/#client-libraries/client-libraries).


## Features

The following features are supported in this version:

- Account Credits: create, retrieve, balance-check, and create/simulate transactions.
- Gift Cards: create, retrieve, balance-check, and create/simulate transactions.

Note that the Lightrail API supports many other features and we are still working on covering them in this library. For a complete list of Lightrail API features check out the [Lightrail API documentation](https://www.lightrail.com/docs/). 


## Installation

### Maven

You can add this library as a dependency in your `maven` `POM` file as:
```xml
<dependency>
  <groupId>com.lightrail</groupId>
  <artifactId>lightrail-client</artifactId>
  <version>3.0.0</version>
</dependency>
```


## Usage

### Configuration

To use this client, you'll need to configure an instance of `LightrailClient` with your API key and shared secret. You can find these in the Lightrail web app -- go to your [account settings](https://www.lightrail.com/app/#/account/profile), then click 'Integrations' and 'Generate Key' in the 'API Keys' section (for security reasons, each generated key is only displayed once). Your shared secret is found immediately below your API keys.  

```
LightrailClient lr = new LightrailClient({API_KEY}, {SHARED_SECRET});
```


## Use Case: Account Credits Powered by Lightrail

The remainder of this document is a quick demonstration of implementing account credits powered by Lightrail using this library. It will assume familiarity with the API and some core concepts, which are discussed in detail in the [Accounts section](https://www.lightrail.com/docs/#accounts/accounts) of the Lightrail docs. 

### Handling Contacts

#### Creating a New Contact

To create a new Contact, you need to provide a client-side unique identifier known as the `shopperId` or `userSuppliedId`. This is a per-endpoint unique identifier used to ensure idempotence (meaning that if the same request is issued more than once, it will not result in repeated actions). Typically this is a customer ID generated by your CRM. Note that the terms `shopperId` and `userSuppliedId` for Contacts are interchangeable (the Lightrail server will store the value as a `userSuppliedId` but you can refer to it as a `shopperId`).

Optionally, you can also provide an `email`, `firstName`, and `lastName`. Here is a sample call:

```
LightrailClient lr = new LightrailClient({API_KEY}, {SHARED_SECRET});

// create by shopperId
lr.contacts.create("cust-a95a09");

// create with all params
CreateContactParams contactParams = new CreateContactParams();
contactParams.shopperId = "cust-a95a09";
contactParams.email = "test@test.com";
contactParams.firstName = "Test";
contactParams.lastName = "Contact";

lr.contacts.create(contactParams);
```

The return value will be a `Contact` object, which will include both the `shopperId` you provided (as `userSuppliedId`) and a server-generated `contactId`. You can choose to save either value to retrieve the contact later:

```
{Contact@2278}
    contactId = "contact-0s459jy6h56"
    userSuppliedId = "cust-a95a09"
    email = "test@test.com"
    firstName = "Test"
    lastName = "Contact"

// output simplified for readability
```

#### Retrieving a Contact

You can retrieve a Contact by its `shopperId` or its `contactId`. The response to this call will be a `Contact` object similar to the one shown above.

```
LightrailClient lr = new LightrailClient({API_KEY}, {SHARED_SECRET});

// retrieve by contactId
lr.contacts.retrieve("contact-0s459jy6h56");

// retrieve by shopperId
lr.contacts.retrieveByShopperId("cust-a95a09");
```

### Handling Accounts

#### Creating Accounts

You can create an account for a contact based on their `shopperId` (identifier generated by your e-commerce system) or based on their `contactId` (generated by Lightrail). You must also specify the currency that the account will be in, and provide a `userSuppliedId`, a unique identifier from your own system. Since each Contact can have only up to one Account Card per currency, you can add the currency as a suffix to the `shopperId`/`userSuppliedId` you provided for the Contact.

You may optionally include an `initialValue` for the account. If provided, this must be a positive integer in the smallest currency unit (for example, `500` is 5 USD).

```
LightrailClient lr = new LightrailClient({API_KEY}, {SHARED_SECRET});

CreateAccountCardByShopperIdParams accountParams = new CreateAccountCardByShopperIdParams();
accountParams.shopperId = "cust-a95a09";
accountParams.currency = "USD";
accountParams.userSuppliedId = "cust-a95a09-USD-account";
accountParams.initialValue = 500;

lr.accounts.create(accountParams);

// To create an account using a contact's Lightrail-generated contactId, 
// use the CreateAccountCardByContactIdParams class - the only difference 
// will be replacing 'shopperId' with 'contactId'
```

The return value will be a `Card` object, which will include both the account card's `userSuppliedId` that you provided and a Lightrail-generated `cardId` which you can persist and use to retrieve the account card later, as well as several other details:

```
{Card@2278}
    cardId = "card-4358huf98r"
    userSuppliedId = "cust-a95a09-USD-account"
    currency = "USD"
    initialValue = 500

// output simplified for readability
```

#### Funding and Charging

To fund or charge an account, you can once again use either the `contactId` (generated by Lightrail) or the `shopperId` (generated by your e-commerce system) to identify the customer account that you wish to transact against.

You must additionally pass in the following:

- The `value` of the transaction: a positive `value` will add funds to the account, while a negative `value` will post a charge to the account. This amount must be in the smallest currency unit (e.g., `500` for 5 USD)
- The `currency` that the transaction is in (note that Lightrail does not handle currency conversion and the contact must have an account in the corresponding currency)
- A `userSuppliedId`, which is a unique transaction identifier to ensure idempotence (for example, the order ID from your e-commerce system)

```
LightrailClient lr = new LightrailClient({API_KEY}, {SHARED_SECRET});

CreateAccountTransactionByShopperIdParams chargeParams = new CreateAccountTransactionByShopperIdParams();
chargeParams.shopperId = "cust-a95a09";
chargeParams.currency = "USD";
chargeParams.value = -350;
chargeParams.userSuppliedId = "order-a90h09a509gaj00-a4";

lr.accounts.createTransaction(chargeParams);
```

The return value is a `Transaction` which includes the full details of the transaction, including both the `userSuppliedId` you provided and a server-generated `transactionId` you can later use to retrieve the transaction again:

```
{Transaction@2278}
    transactionId = "transactionId"
    userSuppliedId = "order-a90h09a509gaj00-a4"
    transactionType = "DRAWDOWN"
    value = -350
    currency = "USD"

// output simplified for readability
```

#### Transaction Simulation and Balance Checking

Before attempting to post a transaction, you may wish to do a transaction simulation to find out whether or not the account has enough funds. In the case of insufficient funds, this can also tell you the maximum value for which the transaction _would be_ successful. For example, if you simulate a $35 drawdown Transaction, the method can tell you that it _would be_ successful if it were only for $20.

The parameters for this method call are almost identical to those for posting a transaction. To get the maximum value, add `nsf: false` to your transaction parameters:

```
LightrailClient lr = new LightrailClient({API_KEY}, {SHARED_SECRET});

CreateAccountTransactionByShopperIdParams chargeParams = new CreateAccountTransactionByShopperIdParams();
chargeParams.shopperId = "cust-a95a09";
chargeParams.currency = "USD";
chargeParams.value = -6500;
chargeParams.userSuppliedId = "order-a90h09a509gaj00-a4";
chargeParams.nsf = "false";

lr.accounts.createTransaction(chargeParams);
```

The response will be similar to the following. Note that this is just a simulation and NOT an actual transaction; for instance, it does not have a `transactionId`. The response indicates that for this transaction, the maximum value this account can provide is $55.

```
{Transaction@2278}
    value = -5500,
    userSuppliedId = "order-a90h09a509gaj00-a4"
    transactionType = "DRAWDOWN"
    currency = "USD"
    transactionBreakdown = [
        {
            value = -500
            valueAvailableAfterTransaction = 0
            valueStoreId = "value-497d783b68fc4b4caa4f12be19112fbd"
        }
        {
            value = -5000
            valueAvailableAfterTransaction = 0
            valueStoreId = "value-4eee14b03d454a6aa514c87e1268639d"
        }
    ]
    transactionId = null

// output simplified for readability

```

### Shopper Tokens

If you are using our [Drop-in Gift Card](https://www.lightrail.com/docs/#drop-in-gift-cards/quickstart) solution, you can use this library to generate shopper tokens for transacting against a customer's account. 

A shopper token is generated from a unique customer identifier from your system: this is the same `shopperId` or contact `userSuppliedId` you would have used when creating the Contact, or you can also use the Lightrail-generated `contactId` that comes back in the response when a Contact is created. 

You can also pass in two optional parameters: 
- `(int) validityInSeconds`: defaults to 43200
- `(Map<String, Object>) metadata`: arbitrary metadata - if creating a split tender charge with Stripe, this is where the contact's Stripe customer ID should be stored

```
LightrailClient lr = new LightrailClient({API_KEY}, {SHARED_SECRET});

Map<String, Object> metadata = new HashMap();
metadata.put("extraId", 112358);

CreateShopperTokenParams tokenParams = new CreateShopperTokenParams();
tokenParams.shopperId = "cust-a95a09";   // must set one of 'shopperId', 'contactId', or 'contactUserSuppliedId'
tokenParams.validityInSeconds = 50000;
tokenParams.metadata = metadata;

lr.generateShopperToken(tokenParams);
```

Note that if you haven't yet created a Contact record, some functions that use the generated shopper token will create one for you automatically based solely on the `shopperId` you provide - ie Account creation. If you want extra information to be associated with the Contact, like their name or email address, you should [create the contact](#handling-contacts) first.


## Testing and Development

You can build  this library from source using `maven`. Assuming that `maven` is installed and configured in your 
environment, you can simply download or clone the source and invoke:

```bash
$ mvn clean package -DskipTests
```

Note that this will skip the unit tests. To run them, omit `-DskipTests` from the previous command or run:

```bash
mvn -Dcucumber test
```


## Requirements

This library requires `Java 1.7` or later.

## Dependencies

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
If your project already depends on a different version, make sure the versions are compatible. We will periodically update the dependency to the latest version.

The following dependencies are also necessary if you want to run the unit tests.

```xml
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.12</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-java</artifactId>
    <version>2.3.1</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-junit</artifactId>
    <version>2.3.1</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>2.13.0</version>
    <scope>test</scope>
</dependency>
```


## Contributing

Bug reports and pull requests are welcome on GitHub at <https://github.com/Giftbit/lightrail-client-java>.


## Changelog

### 3.0.0

- Complete overhaul: see readme for details.

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

