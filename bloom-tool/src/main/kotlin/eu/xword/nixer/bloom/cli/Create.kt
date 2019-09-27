package eu.xword.nixer.bloom.cli

class Create : BloomFilterCreatingCommand(name = "create",
        help = """
        Creates a new bloom filter represented by two files. 
        The first file contains filter parameters, the second one is data file sized to fit the provided number of expected insertions.
    """) {

    override fun run() {
        createFilter(name, size, fpp, hex)
    }
}
