package eu.xword.nixer.bloom.cli

import com.github.ajalt.clikt.parameters.groups.cooccurring

class Insert : InputStreamingCommand(name = "insert",
        help = """
        Inserts values to the filter from standard input.
        Each line is a separate value.
    """) {

    private val basicFilterOptions by BasicFilterOptions().required()
    private val preprocessOptions by PreprocessOptions().cooccurring()

    override fun run() {

        val inputStream = inputStream()

        val bloomFilter = with(basicFilterOptions) {
            openFilter(name, hex)
        }

        val entryTransformer = preprocessOptions
                ?.run { fieldExtractor(separator, field) }
                ?: { it }

        insertIntoFilter(bloomFilter, entryTransformer, inputStream)
    }
}
