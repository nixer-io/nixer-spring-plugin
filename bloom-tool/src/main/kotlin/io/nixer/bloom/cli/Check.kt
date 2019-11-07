package io.nixer.bloom.cli

class Check : io.nixer.bloom.cli.InputStreamingCommand(name = "check",
        help = """
        Checks if values provided in the given input appear in the Bloom filter,
        printing matches to the standard output, and skipping not matched values.
        Each line is a separate value.
    """) {

    private val basicFilterOptions by io.nixer.bloom.cli.BasicFilterOptions().required()


    override fun run() {

        val bloomFilter = io.nixer.bloom.cli.openFilterForCheck(basicFilterOptions.name, hashInput)

        io.nixer.bloom.cli.tryExecuting {
            io.nixer.bloom.cli.checkAgainstFilter(bloomFilter, entryParser(), inputStream())
        }
    }
}
