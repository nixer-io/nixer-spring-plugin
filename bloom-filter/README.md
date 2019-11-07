# bloom-filter

A library for a file-based Bloom filters. The algorithms are copied from Google Guava, but the used bit array is backed
by memory mapped files, instead of Java arrays. The goal was too be able to handle very large bloom filters. 

### Benchmark
Performance numbers (date: 2018-08-24, on a MacBook Pro 16GB RAM, the file generated had size 4GB ) using `bloom-tool benchmark`:
```
$ caffeinate build/dist/bloom-tool benchmark --size=1000000000 --fpp=1e-7
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

