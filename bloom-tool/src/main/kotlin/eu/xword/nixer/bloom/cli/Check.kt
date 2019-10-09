package eu.xword.nixer.bloom.cli

class Check : InputStreamingCommand(name = "check",
        help = """
        Checks if values provided in the given input appear in the Bloom filter,
        printing matches to the standard output, and skipping not matched values.
        Each line is a separate value.
    """) {

    private val basicFilterOptions by BasicFilterOptions().required()

    override fun run() {

        val inputStream = inputStream()

        val bloomFilter = openFilterForCheck(basicFilterOptions.name, hashInput)

        tryExecuting {
            checkAgainstFilter(bloomFilter, inputStream)
        }
    }
}
