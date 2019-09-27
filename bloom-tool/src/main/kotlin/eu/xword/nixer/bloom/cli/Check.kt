package eu.xword.nixer.bloom.cli

class Check : BloomFilterAwareCommand(name = "check",
        help = """
        Checks if values provided in the standard input appear in the filter,
        printing matches to the standard output, and skipping not matched values.
        Each line is a separate value.
    """) {

    override fun run() {
        val bloomFilter = openFilter(name, hex)

        checkAgainstStandardInput(bloomFilter)
    }
}
