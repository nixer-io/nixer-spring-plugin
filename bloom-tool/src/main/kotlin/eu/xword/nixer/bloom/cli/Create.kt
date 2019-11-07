package eu.xword.nixer.bloom.cli

import com.github.ajalt.clikt.core.CliktCommand

class Create : CliktCommand(help = """
        Creates a new Bloom filter represented by two files. 
        The first file contains filter parameters, the second one is data file sized to fit the provided number of expected insertions.
    """) {

    private val basicFilterOptions by BasicFilterOptions().required()
    private val detailedFilterOptions by DetailedFilterOptions().required()

    override fun run() {

        createFilter(
                basicFilterOptions.name,
                detailedFilterOptions.size,
                detailedFilterOptions.fpp
        )
    }
}
