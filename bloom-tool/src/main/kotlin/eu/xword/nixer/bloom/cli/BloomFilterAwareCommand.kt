package eu.xword.nixer.bloom.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.google.common.hash.Funnel
import com.google.common.hash.Funnels
import eu.xword.nixer.bloom.BloomFilter
import eu.xword.nixer.bloom.FileBasedBloomFilter
import eu.xword.nixer.bloom.HexFunnel
import java.nio.file.Paths

abstract class BloomFilterAwareCommand(name: String, help: String) : CliktCommand(name = name, help = help) {

    protected val name: String by argument(help = """
            Name of the bloom filter. Corresponds to name of the file with filter parameters and prefix of the data file.
            """)

    private val hex: Boolean by option(help = """
            Interprets input values as hexadecimal string when inserting or checking.
            Values are converted to bytes before inserting, if this conversion fail,
            the string is inserted a normal way.
            """)
            .flag()

    protected fun openFilter(): BloomFilter<CharSequence> = FileBasedBloomFilter.open(
            Paths.get(name),
            getFunnel()
    )

    protected fun getFunnel(): Funnel<CharSequence> = when {
        hex -> HexFunnel(Funnels.unencodedCharsFunnel())
        else -> Funnels.unencodedCharsFunnel()
    }
}
