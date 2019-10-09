package eu.xword.nixer.bloom.cli

class Insert : InputStreamingCommand(name = "insert",
        help = """
        Inserts values to the Bloom filter from the given input.
        Each line is a separate value.
    """) {

    private val basicFilterOptions by BasicFilterOptions().required()

    override fun run() {

        val inputStream = inputStream()

        val bloomFilter = openFilter(basicFilterOptions.name)

        val entryParser = entryParser()

        val entryHasher: (String) -> String = when {
            hashInput -> ::sha1
            else -> { it -> it }
        }

        val entryTransformer: (String) -> String = { entryHasher(entryParser(it)) }

        tryExecuting {
            insertIntoFilter(bloomFilter, entryTransformer, inputStream)
        }
    }
}
