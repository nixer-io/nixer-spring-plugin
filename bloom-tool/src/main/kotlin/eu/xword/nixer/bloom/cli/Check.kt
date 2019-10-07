package eu.xword.nixer.bloom.cli

import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option

class Check : InputStreamingCommand(name = "check",
        help = """
        Checks if values provided in the given input appear in the Bloom filter,
        printing matches to the standard output, and skipping not matched values.
        Each line is a separate value.
    """) {

    private val hashed: Boolean by option(help = """
            Flag indicating whether the input values are already hashed with SHA-1.
            """).flag()

    private val basicFilterOptions by BasicFilterOptions().required()

    override fun run() {

        val inputStream = inputStream()

        val bloomFilter = openFilterForCheck(basicFilterOptions.name, hashed)

        checkAgainstFilter(bloomFilter, inputStream)
    }
}
