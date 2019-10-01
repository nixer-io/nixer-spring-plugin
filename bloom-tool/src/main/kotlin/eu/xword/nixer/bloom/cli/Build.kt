package eu.xword.nixer.bloom.cli

import com.github.ajalt.clikt.parameters.groups.cooccurring

/**
 * Created on 26/09/2019.
 *
 * @author gcwiak
 */
class Build : InputStreamingCommand(name = "build",
        help = """
        Combination of 'create' and 'insert' commands.    
        Creates a new bloom filter and inserts values from standard input. 
        Each line is a separate value.
    """) {

    private val basicFilterOptions by BasicFilterOptions().required()
    private val detailedFilterOptions by DetailedFilterOptions().required()
    private val preprocessOptions by PreprocessOptions().cooccurring()

    override fun run() {

        val inputStream = inputStream()

        val bloomFilter = createFilter(
                basicFilterOptions.name,
                basicFilterOptions.hex,
                detailedFilterOptions.size,
                detailedFilterOptions.fpp
        )

        val entryTransformer = preprocessOptions
                ?.run { fieldExtractor(separator, field) }
                ?: { it }

        insertIntoFilter(bloomFilter, entryTransformer, inputStream)
    }
}
