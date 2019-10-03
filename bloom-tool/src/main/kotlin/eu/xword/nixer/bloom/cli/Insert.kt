package eu.xword.nixer.bloom.cli

import com.github.ajalt.clikt.parameters.groups.cooccurring

class Insert : InputStreamingCommand(name = "insert",
        help = """
        Inserts values to the Bloom filter from the given input.
        Each line is a separate value.
    """) {

    private val basicFilterOptions by BasicFilterOptions().required()
    private val entryParsingOptions by EntryParsingOptions().cooccurring()

    override fun run() {

        val inputStream = inputStream()

        val bloomFilter = with(basicFilterOptions) {
            openFilter(name, hex)
        }

        val entryParser = entryParsingOptions
                ?.run { fieldExtractor(separator, field) }
                ?: { it }

        insertIntoFilter(bloomFilter, entryParser, inputStream)
    }
}
