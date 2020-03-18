### Example leaked (pwned) credentials database

This directory represent database of leaked credentials 
as a [file-based Bloom filter](https://nixer-io.github.io/additional-features/#file-based-bloom-filter-for-java)
which is used by the `pwned-check` functionality.

The Bloom filter is backed by two files:
- `example.bloom` - metadata file containing filter parameters
- `example.bloom-data` - data set file keeping hashed credentials.

The filter has been generated from `example-leak.txt` file containing raw credentials using 
[`bloom-tool` CLI utility](https://nixer-io.github.io/additional-features/#bloom-cli-tool).

This file is kept here only for informational purposes as there is no straightforward insight into the Bloom filter's data set.
