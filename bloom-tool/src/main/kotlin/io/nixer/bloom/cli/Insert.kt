package io.nixer.bloom.cli

class Insert : io.nixer.bloom.cli.InputStreamingCommand(name = "insert",
        help = """
        Inserts values to the Bloom filter from the given input.
        Each line is a separate value.
    """) {

    private val basicFilterOptions by io.nixer.bloom.cli.BasicFilterOptions().required()

    override fun run() {

        val inputStream = inputStream()

        val bloomFilter = io.nixer.bloom.cli.openFilter(basicFilterOptions.name)

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
