# Lightrail Client for Java

Lightrail makes it easy to run promotions, manage customer loyalty points, create gift cards, and collect payment all in one unified checkout solution (to learn more, visit [Lightrail](https://www.lightrail.com/)). This is a basic library for developers to easily connect with the Lightrail API using Java.

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

### Releasing (Lightrail team only)

Instructions coming soon.

### Contributing

Bug reports and pull requests are welcome on GitHub at <https://github.com/Giftbit/lightrail-client-java>.

## License

This library is available as open source under the terms of the [MIT License](http://opensource.org/licenses/MIT).
