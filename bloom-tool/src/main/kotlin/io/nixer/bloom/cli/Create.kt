package io.nixer.bloom.cli

import com.github.ajalt.clikt.core.CliktCommand

class Create : CliktCommand(help = """
        Creates a new Bloom filter represented by two files. 
        The first file contains filter parameters, the second one is data file sized to fit the provided number of expected insertions.
    """) {

    private val basicFilterOptions by io.nixer.bloom.cli.BasicFilterOptions().required()
    private val detailedFilterOptions by io.nixer.bloom.cli.DetailedFilterOptions().required()

    override fun run() {

        io.nixer.bloom.cli.createFilter(
                basicFilterOptions.name,
                detailedFilterOptions.size,
                detailedFilterOptions.fpp
        )
    }
}
