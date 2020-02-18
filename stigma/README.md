# Stigma token detection

Stigma token is a mechanism for stamping devices. It combines unique tokens with results of login attempts 
making it useful for detecting suspicious behaviors over time. 

Due to the way of identifying devices the mechanism is completely immune to attacker's bypass techniques 
like manipulations with IP addresses, User-Agents, usage of bots etc.

## Idea

The mechanism is based on marking each device attempting to login with a so-called Stigma which is a randomly generated unique identifier.
Stigma in turn is wrapped with a simple data structure, assuring security and integrity, called Stigma token.

Each incoming login request is checked for the presence of Stigma token carried as a HTTP cookie.

In case the token is found and the login was successful the cookie is left intact, to be used with any subsequent login attempts.
For all other cases, that is missing/invalid Stigma token or unsuccessful login attempt, a brand new token is generated 
and assigned as cookie via the response.

The core measurement is the number of new Stigmas being generated over time. Large number of consecutive failed login attempts, 
caused e.g. by a credential stuffing attack, is going to result in sudden peak on the distribution.

### Stigma token evaluation

The following table shows all cases in detail.

| Stigma token | Login result | Action    |
|--------------|--------------|-----------|
| Good         | Success      | None      |
| Good         | Failed       | New token, revoke the current Stigma |
| Bad          | Success      | New token |
| Bad          | Failed       | New token |

Stigma token is considered "Bad" when:
* is missing, that is the cookie is not present at all or has empty value,
* it cannot be parsed, e.g. invalid format or decryption error (see further sections),
* the carried Stigma is in invalid state, e.g. revoked or expired.

Creating a new token means generating new unique Stigma and wrapping it with Stigma token. 

## How is it better?

One could ask in which way this is better than just analyzing a number of failed login attempts, 
simple fingerprinting or a regular session?

Each of the mentioned mechanisms alone is not enough. 
Correlating login result with a unique Stigma gives the following benefits:
* simplicity - we need to control generation and maintain only a single quantity, i.e. Stigma values
* only the value of a single cookie is tracked, there is no dependency on any other parameter of the client's device
* attacking tools or bots not able to maintain cookies can be easily detected as generating strong new Stigma generation signal
* not susceptible for brute force attacks from different IP addresses, e.g. via proxies, 
as each failed attempt is going to cause a new Stigma generation - strong new Stigma generation signal 
* attacks based on username enumeration are going to generate a strong signal in similar manner
* Stigmas cannot be reused between failed attempts, due to revoking a valid Stigmas in such cases.

## Implementation

Stigma token is implemented as encrypted [JWT](https://en.wikipedia.org/wiki/JSON_Web_Token), 
that is [JWE](https://en.wikipedia.org/wiki/JSON_Web_Encryption), where the value of Stigma is carried as one of claims.

Apart from confidentiality the encryption ensures integrity and protection from tampering.

Since both encryption (as tokens are created) and decryption (while validating incoming tokens) are done by the same
party, [direct encryption with shared symmetric key](https://tools.ietf.org/html/rfc7518#section-4.5) is used. 
The same key is meant for encrypting and decrypting. The key format being used is [JWK](https://tools.ietf.org/html/rfc7517). 

At a time the system maintains a single encryption key and multiple decryption keys. 
The reason is that after changing the key it is still necessary to be able to decrypt incoming Stigma tokens 
that have been encrypted with any previous keys, so the "old" keys need to be kept for decryption.

# Usage
## Installation

Stigma Nixer plugin is distributed through [Maven Central](https://search.maven.org/search?q=io.nixer).
It requires dependency to Core Nixer plugin as well.

```kotlin
dependencies {
    implementation("io.nixer:nixer-plugin-core:0.1.0.0")
    implementation("io.nixer:nixer-plugin-stigma:0.1.0.0")
}
```

After the dependencies are added all beans are created automatically with Spring's autoconfiguration mechanism.

## Configuration

In order to start using the Stigma mechanism you need to provide a JWK key to be used for encryption and decryption of Stigma tokens. 
This key is passed in two files, one for encryption and the other for decryption.
 
### Key files requirements

* the encryption key file must contain a single key,
* the decryption keys file must contain the key from the encryption file and may contain the old keys,
(kept for decrypting older tokens, see previous section),
* both files must follow [JWK set format](https://tools.ietf.org/html/rfc7517#section-5).

Paths to the files are passed with the following properties: 

```properties
nixer.stigma.encryptionKeyFile=classpath:stigma-enc-jwk.json
nixer.stigma.decryptionKeyFile=classpath:stigma-dec-jwk.json
```

### Keys requirements

* must be in [JWK](https://tools.ietf.org/html/rfc7517) format,
* algorithm: direct, i.e. `alg` parameter must be set to `dir`,
* key type: octet sequence, i.e. `kty` parameter must be set to `oct`,
* key id (`kid`) is mandatory and must be unique among all decryption keys,
* the encryption key must be included in the decryption key set with the same key id.

## Storage

Data related to Stigmas is by default stored in the embedded H2 database shipped with Spring Boot,
however it might be replaced with any other JDBC-compatible database. 
Alternatively, by implementing `io.nixer.nixerplugin.stigma.storage.StigmaStorage`, different storage mechanisms can be used.

## Examples

For full usage examples please see the following:

* [Internal example, most up-to-date, local dependency resolution](https://github.com/nixer-io/nixer-spring-plugin/tree/master/samples/example)
* [External example, aligned to the latest release, real dependency resolution](https://github.com/nixer-io/nixer-spring-plugin-integrations/tree/master-with-nixer-plugin/nixer-spring-plugin-demo-app)
