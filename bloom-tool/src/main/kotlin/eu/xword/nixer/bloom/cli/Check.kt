package eu.xword.nixer.bloom.cli

class Check : InputStreamingCommand(name = "check",
        help = """
        Checks if values provided in the standard input appear in the filter,
        printing matches to the standard output, and skipping not matched values.
        Each line is a separate value.
    """) {

    private val basicFilterOptions by BasicFilterOptions().required()

    override fun run() {

        val inputStream = inputStream()

        val bloomFilter = with(basicFilterOptions) {
            openFilter(name, hex)
        }

        checkAgainstFilter(bloomFilter, inputStream)
    }
}
