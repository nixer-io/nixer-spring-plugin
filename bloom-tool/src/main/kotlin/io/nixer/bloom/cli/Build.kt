package io.nixer.bloom.cli

/**
 * Created on 26/09/2019.
 *
 * @author gcwiak
 */
class Build : io.nixer.bloom.cli.InputStreamingCommand(name = "build",
        help = """
        Creates a new Bloom filter and inserts values from the given input. 
        Each line is a separate value.
        
        Works as combination of 'create' and 'insert' commands.  
        
        The created filter is represented by two files, the first one contains filter parameters, 
        the second one is data file sized to fit the provided number of expected insertions.
    """) {

    private val basicFilterOptions by io.nixer.bloom.cli.BasicFilterOptions().required()
    private val detailedFilterOptions by io.nixer.bloom.cli.DetailedFilterOptions().required()

    override fun run() {

        val inputStream = inputStream()

        val bloomFilter = io.nixer.bloom.cli.createFilter(
                basicFilterOptions.name,
                detailedFilterOptions.size,
                detailedFilterOptions.fpp
        )

        val entryParser = entryParser()

        val entryHasher: (String) -> String = when {
            hashInput -> ::sha1
            else -> { it -> it }
        }

        val entryTransformer: (String) -> String = { entryHasher(entryParser(it)) }

        io.nixer.bloom.cli.tryExecuting {
            io.nixer.bloom.cli.insertIntoFilter(bloomFilter, entryTransformer, inputStream)
        }
    }
}
