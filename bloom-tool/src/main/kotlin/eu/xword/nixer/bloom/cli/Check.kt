package eu.xword.nixer.bloom.cli

import com.github.ajalt.clikt.parameters.groups.cooccurring

class Check : InputStreamingCommand(name = "check",
        help = """
        Checks if values provided in the given input appear in the Bloom filter,
        printing matches to the standard output, and skipping not matched values.
        Each line is a separate value.
    """) {

    private val basicFilterOptions by BasicFilterOptions().required()
    private val entryParsingOptions by EntryParsingOptions().cooccurring()

    override fun run() {

        val inputStream = inputStream()

        val bloomFilter = openFilterForCheck(basicFilterOptions.name, hashInput)

        val entryParser = entryParsingOptions
                ?.run { fieldExtractor(separator, field) }
                ?: { it }

        tryExecuting {
            checkAgainstFilter(bloomFilter, entryParser, inputStream)
        }
    }
}
