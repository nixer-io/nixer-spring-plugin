# bloom-tool

A simple command line utility to manipulate file-based Bloom filters. 

## Concepts

A [Bloom filter](https://en.wikipedia.org/wiki/Bloom_filter) is a probabilistic data structure designed to tell, rapidly and efficiently, 
whether an element is a member of a set. False positives are possible, but false negatives are not, which means query returns either 
"possibly in set" or "definitely not in set".

Our Bloom filter implementation is backed by two files:
- `filter_name.bloom` is metadata file containing filter parameters (e.g. false positive probability, number of insertions), 
- `filter_name.bloom-data` is data set file sized to fit the provided number of expected insertions.

It can efficiently handle sets that require multi-GB data storage, e.g. with billions of entries, and low false positive rates.

## Building

The tool is built using Gradle [application plugin](https://docs.gradle.org/current/userguide/application_plugin.html).
```
./gradlew
```
builds whole project and places the tool package under `build/distributions` directory.

## Using

Unzip the application package and invoke in order to see all commands and options:

```
bin/bloom-tool --help
```

### Examples:

#### Create
Create a new filter `my.bloom` for 1M insertions and 1 to 1M false positive rate.
```
bloom-tool create --size=1000000 --fpp=1e-6 --name=my.bloom
```
Results in creating `my.bloom` metadata file and empty `my.bloom-data` data file.

#### Insert
Insert lines from `entries.txt` into `my.bloom` filter, populating `my.bloom-data` file.
```
bloom-tool insert --name=my.bloom --input-file=entries.txt
```
or from input stream 
```
cat entries.txt | bloom-tool insert --name=my.bloom --stdin
```
alternatively
```
bloom-tool insert --name=my.bloom --stdin < entries.txt
```

Insert hexadecimal from `hashes.txt` to `my.bloom`. Useful if the filter is intended to be use from Java code generating some hashes.
```
bloom-tool insert --hex --name=my.bloom --input-file=hashes.txt
```

#### Check
Check if string `example` might be inserted in `my.bloom` filter, printing it to standard output if it might be true or skipping otherwise.
```
echo example | bloom-tool check --name=my.bloom --stdin
```

#### Benchmark

Execute performance benchmark and correctness verification of the bloom filter implementation.
This might take a minute, or more for greater sizes.
```
bloom-tool benchmark --size 10000000 --fpp=1e-7
```

##### Sample result:

Performance numbers (date: 2018-08-24, on a MacBook 16GB RAM, the file generated had size 4GB ):
```
$ caffeinate bloom-tool benchmark --size=1000000000 --fpp=1e-7
Creating: /var/folders/f3/pqxz3z7s6g37nhv65114479r0000gn/T/bloom-benchmark-tmp3708262923291670873/test.bloom
Creation time       : PT1.063892385S
Parameters          : BloomFilterParameters{expectedInsertions=1000000000, falsePositivesProbability=1.0E-7, numHashFunctions=23, bitSize=33547704320, strategy=MURMUR128_MITZ_64, byteOrder=LITTLE_ENDIAN}
Inserting           : 1000000000 values
Insertion time      : PT41M42.06361178S
Insertion speed     : 399658.84 op/sec
Checking            : 2000000000 values (half exist, half should not exist)
Check time          : PT45M27.88090649S
Check speed         : 733163.21 op/sec
False Positives     : 123
False Positive rate : 0.00000012300000
FP rate evaluation  : good (122.92% target)
```
