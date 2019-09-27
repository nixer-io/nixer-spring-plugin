package eu.xword.nixer.bloom.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

/**
 * The entry class for a command utility to manipulate file-based bloom filters.
 * <br></br>
 * Created on 23/08/2018.
 *
 * @author cezary.biernacki@crosswordcybersecurity.com
 */
class BloomToolMain : CliktCommand(name = "bloom-tool") {
    override fun run() = Unit // Nothing here, the actual work is done by subcommands.
}

fun main(args: Array<String>) = BloomToolMain()
        .subcommands(
                Create(),
                Insert(),
                Build(),
                Check(),
                BenchmarkCmd()
        )
        .main(args)
