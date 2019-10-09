package eu.xword.nixer.bloom.cli

import com.github.ajalt.clikt.core.CliktCommand
import eu.xword.nixer.bloom.Benchmark

class BenchmarkCmd : CliktCommand(name = "benchmark",
        help = """
        Runs performance benchmark and correctness verification
        by creating a Bloom filter in a temporary directory,
        populating it with random data and checking what can be found.
    """) {

    private val detailedFilterOptions by DetailedFilterOptions().required()

    override fun run() {

        with(detailedFilterOptions) {
            Benchmark(size, fpp).run()
        }

    }
}
