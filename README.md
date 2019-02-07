# Lightrail Client for Java

Lightrail is a modern platform for digital account credits, gift cards, promotions, and points (to learn more, visit [Lightrail](https://www.lightrail.com/)). This is a basic library for developers to easily connect with the Lightrail API using Javascript or Typescript. If you are looking for specific use cases or other languages, check out the complete list of all [Lightrail libraries and integrations](https://github.com/Giftbit/Lightrail-API-Docs/blob/master/README.md#lightrail-integrations).

## Features

The following features are supported in this version:

##### Contacts
Create, Get, List, Update, Delete, List Values, Attach Value
 
##### Values
Create, Get, List, Update
  
##### Programs
Create, Get, List, Update

##### Transactions
Checkout, Debit, Credit, Transfer, Reverse, Capture Pending, Void Pending, Get, List
  
##### Currencies
Create, Get, List, Update, Delete

Note that the Lightrail API supports many other features and we are still working on covering them in this library. For a complete list of Lightrail API features check out the [Lightrail API documentation](https://www.lightrail.com/docs/).

## Installation

This library requires Java 8 or higher.

### Maven

You can add this library as a dependency in your `pom.xml` file:

```xml
<dependency>
  <groupId>com.lightrail</groupId>
  <artifactId>lightrail-client</artifactId>
  <version>4.0.0</version>
</dependency>
```

### Gradle

```text
compile 'com.lightrail:lightrail-client:4.0.0'
```

## Usage

### Configuration

To use this client, you'll need to configure an instance of `LightrailClient` with your API key and shared secret. You can find these in the Lightrail web app. Your shared secret is found immediately below your API keys.

```
LightrailClient lr = new LightrailClient({API_KEY}, {SHARED_SECRET});
```

## Development

### Testing

Testing requires a Lightrail account.  Copy `src/test/resources/.env.example` to `src/test/resources/.env` and set your account's test API key which is available in the Lightrail web app.

### Contributing

Bug reports and pull requests are welcome on GitHub at <https://github.com/Giftbit/lightrail-client-java>.

## License

This library is available as open source under the terms of the [MIT License](http://opensource.org/licenses/MIT).

## Changelog

### 4.0.0

- Rewritten to support Lightrail API v2.

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

### 1.0.1

- Basic API functions: gift code balance check, transacting against a gift code and gift card.

