package eu.xword.nixer.bloom.cli

import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.double
import com.github.ajalt.clikt.parameters.types.long
import eu.xword.nixer.bloom.FileBasedBloomFilter
import java.nio.file.Paths

class Create : BloomFilterAwareCommand(name = "create", help = "Creates a new bloom filter.") {

    private val size: Long by option(help = "Expected number of elements to be inserted").long().required()

    private val fpp: Double by option(help = "Target maximum probability of false positives").double().default(1e-6)

    override fun run() {
        FileBasedBloomFilter.create(
                Paths.get(name),
                getFunnel(),
                size,
                fpp
        )
    }
}
