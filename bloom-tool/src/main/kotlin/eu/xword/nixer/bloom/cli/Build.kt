package eu.xword.nixer.bloom.cli

import com.github.ajalt.clikt.parameters.groups.cooccurring

/**
 * Created on 26/09/2019.
 *
 * @author gcwiak
 */
class Build : InputStreamingCommand(name = "build",
        help = """
        Creates a new Bloom filter and inserts values from the given input. 
        Each line is a separate value.
        
        Works as combination of 'create' and 'insert' commands.  
        
        The created filter is represented by two files, the first one contains filter parameters, 
        the second one is data file sized to fit the provided number of expected insertions.
    """) {

    private val basicFilterOptions by BasicFilterOptions().required()
    private val detailedFilterOptions by DetailedFilterOptions().required()
    private val entryParsingOptions by EntryParsingOptions().cooccurring()

    override fun run() {

        val inputStream = inputStream()

        val bloomFilter = createFilter(
                basicFilterOptions.name,
                basicFilterOptions.hex,
                detailedFilterOptions.size,
                detailedFilterOptions.fpp
        )

        val entryParser = entryParsingOptions
                ?.run { fieldExtractor(separator, field) }
                ?: { it }

        val entryHasher: (String) -> String = hashingFunction()

        val entryTransformer: (String) -> String = { entryHasher(entryParser(it)) }

        insertIntoFilter(bloomFilter, entryTransformer, inputStream)
    }
}
