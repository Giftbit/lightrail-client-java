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
  <version>4.3.0</version>
</dependency>
```

### Gradle

```text
compile 'com.lightrail:lightrail-client:4.1.0'
```

## Usage

### Configuration

To use this client, you'll need to configure an instance of `LightrailClient` with your API key and shared secret. You can find these in the Lightrail web app. Your shared secret is found immediately below your API keys.

```
LightrailClient lr = new LightrailClient({API_KEY}, {SHARED_SECRET});
```

## Development

### Development Environment
You need to have java and maven installed to test and release this client.

Use sdkman.
- curl -s "https://get.sdkman.io" | bash
- sdk install java
- sdk install maven

Test your java install by `java --version`
Test your maven install by `mvn -v`

Note, if you're getting the following error:
```
mvn -v
The JAVA_HOME environment variable is not defined correctly
This environment variable is needed to run this program
NB: JAVA_HOME should point to a JDK not a JRE
```

If your JAVA_HOME is set correctly double check that there isn't a JAVA_HOME defined in `~.mavenrc`. `vi ~.mavenrc`. Otherwise, verify your JAVA_HOME settings. Useful commands to not forget:
```
which java
echo $JAVA_HOME
which mvn
/usr/libexec/java_home
```

### Testing

Testing requires a Lightrail account.  Copy `src/test/resources/.env.example` to `src/test/resources/.env` and set your account's test API key which is available in the Lightrail web app.

Test with the command `mvn test`.

### Contributing

Bug reports and pull requests are welcome on GitHub at <https://github.com/Giftbit/lightrail-client-java>.

### Releasing (Lightrail team only)

#### One time setup

##### PGP keys

Install `gpg` using homebrew if you don't have it already. 

Copy PGP private key from the password manager into a new `.asc` file (eg `lightrail.asc`), then run `gpg --import lightrail.asc`.  There's a passphrase associated with the key you will use later.

##### Nexus plugin settings

Copy Maven `settings.xml` from password manager to `~/.m2/settings.xml`.  The result should look something like the following:

```
<settings>
    <servers>
        <server>
            <id>ossrh</id>
            <username>{{username}}</username>
            <password>{{password}}</password>
        </server>
        <server>
            <id>sonatype-nexus-staging</id>
            <username>{{username}}</username>
            <password>{{password}}</password>
        </server>
    </servers>
</settings>
```

#### Versioning

The version should be bumped following semver.  It can be found at the top of `pom.xml` under `<version>` and referenced in the installation section of this readme.

`-SNAPSHOT` can be added to the end of the version number to indicate a pre-release build.  This version can be under heavy development, can be overwritten and need not obey semver.  Remove `-SNAPSHOT` for official releases.

#### Deployment

The first command cleans old builds.  Deploy will upload old builds found there if we don't do this.

`mvn clean`

The next command builds, tests and then uploads the build to https://oss.sonatype.org/. You will be prompted for the signing password that goes with the signing key configured above. 
Certain versions of java may run into problems regarding `maven-javadoc-plugin`. `sdk install java 8.0.242.hs-adpt` worked. 

`mvn deploy`

The next command marks the build as acceptable for wide release and makes it available on the main repo https://search.maven.org/search?q=a%3Alightrail-client .

`mvn nexus-staging:release`

Alternately you could mark the build as unacceptable (because it was a testing SNAPSHOT release for example).

`mvn nexus-staging:drop`

## License

This library is available as open source under the terms of the [MIT License](http://opensource.org/licenses/MIT).
