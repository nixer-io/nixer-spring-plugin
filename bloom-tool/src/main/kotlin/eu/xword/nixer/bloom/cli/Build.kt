package eu.xword.nixer.bloom.cli

import com.github.ajalt.clikt.parameters.groups.cooccurring

/**
 * Created on 26/09/2019.
 *
 * @author gcwiak
 */
class Build : BloomFilterCreatingCommand(name = "build",
        help = """
        Combination of 'create' and 'insert' commands.    
        Creates a new bloom filter and inserts values from standard input. 
        Each line is a separate value.
    """) {

    private val preprocessOptions by PreprocessOptions().cooccurring()

    override fun run() {
        val bloomFilter = createFilter(name, size, fpp, hex)

        val entryTransformer = preprocessOptions
                ?.run { fieldExtractor(separator, field) }
                ?: { it }

        insertFromStandardInput(bloomFilter, entryTransformer)
    }
}
