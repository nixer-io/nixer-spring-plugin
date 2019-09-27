package eu.xword.nixer.bloom.cli

import com.github.ajalt.clikt.parameters.groups.cooccurring

class Insert : BloomFilterAwareCommand(name = "insert",
        help = """
        Inserts values to the filter from standard input.
        Each line is a separate value.
    """) {

    private val preprocessOptions by PreprocessOptions().cooccurring()

    override fun run() {
        val bloomFilter = openFilter(name, hex)

        val entryTransformer = preprocessOptions
                ?.run { fieldExtractor(separator, field) }
                ?: { it }

        insertFromStandardInput(bloomFilter, entryTransformer)
    }
}
