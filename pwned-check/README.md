# Pwned credentials check

Module that provides functionality of checking if the credentials being used for login attempt have been compromised in a data breach.

---
**The check is done completely locally, no data is sent outside your application.**

---

It is performed on hashes of credentials, not original values, so the risk of a password leak is not increased by using this feature.

# How does it work

The functionality operates on hashes of credentials and uses [Bloom filter](https://en.wikipedia.org/wiki/Bloom_filter) for doing the check. 

The Bloom filter implementation being used offers great performance and can be applied on hot paths, 
[read more here](https://github.com/nixer-io/nixer-spring-plugin/tree/master/bloom-filter).

# Usage
## Data source

Before using the filter it must be populated with leaked credentials data to be checked against. 
For this purpose we provide convenient command line utility, 
[bloom-tool](https://github.com/nixer-io/nixer-spring-plugin/tree/master/bloom-tool), which allows generating, manipulating 
and testing Bloom filters.

As a data source you can use pwned passwords lists available at [haveibeenpwned.com](https://haveibeenpwned.com/Passwords) 
or similar websites, or can be obtained from any credentials breach data you have access to. 
 
For information about transforming the data into Bloom filter please refer to 
[bloom-tool documentation](https://github.com/nixer-io/nixer-spring-plugin/tree/master/bloom-tool).

Bloom filter is represented by two files:
- `filter_name.bloom` - metadata file containing filter parameters, 
- `filter_name.bloom-data` - data set file.

## Installation

Pwned Check Nixer plugin is distributed through [Maven Central](https://search.maven.org/search?q=io.nixer).
It requires dependency to Core Nixer plugin as well.

```kotlin
dependencies {
    implementation("io.nixer:nixer-plugin-core:0.1.0.0")
    implementation("io.nixer:nixer-plugin-pwned-check:0.1.0.0")
}
```

After the dependencies are added all beans are created automatically with Spring's autoconfiguration mechanism.

## Configuration

In order to enable the functionality the following properties are to be set:

```properties
nixer.pwned.check.enabled=true
nixer.pwned.check.pwnedFilePath=classpath:PWNED_DATABASE_DIRECTORY/filter_name.bloom
```

where `PWNED_DATABASE_DIRECTORY` is expected to contain both Bloom filter files, `filter_name.bloom` and `filter_name.bloom-data`.

## Results

Pwned check results are written into 
[Spring Boot application metrics](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-features.html#production-ready-metrics), 
under `pwned_check` metric name, from where they can be utilized for any mitigation actions.

## Examples

For full usage examples please see the following:

* [Internal example, most up-to-date, local dependency resolution](https://github.com/nixer-io/nixer-spring-plugin/tree/master/samples/example)
* [External example, aligned to the latest release, real dependency resolution](https://github.com/nixer-io/nixer-spring-plugin-integrations/tree/master-with-nixer-plugin/nixer-spring-plugin-demo-app)
