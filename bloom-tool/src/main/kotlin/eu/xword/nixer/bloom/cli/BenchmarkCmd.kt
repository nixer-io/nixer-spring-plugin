package eu.xword.nixer.bloom.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.double
import com.github.ajalt.clikt.parameters.types.long
import eu.xword.nixer.bloom.Benchmark

class BenchmarkCmd : CliktCommand(name = "benchmark",
        help = """
        Runs performance benchmark and correctness verification
        by creating a filter in a temporary directory,
        populating it with random data and checking what can be found.
    """) {

    private val size: Long by option(help = "Expected number of elements to be inserted").long().required()

    private val fpp: Double by option(help = "Target maximum probability of false positives").double().default(1e-6)

    override fun run() {
        Benchmark(size, fpp).run()
    }
}
