package eu.xword.nixer.bloom.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.double
import com.github.ajalt.clikt.parameters.types.long

abstract class BloomFilterAwareCommand(name: String, help: String) : CliktCommand(name = name, help = help) {

    protected val name: String by argument(help = """
            Name of the bloom filter. Corresponds to name of the file with filter parameters and prefix of the data file.
            """)

    protected val hex: Boolean by option(help = """
            Interprets input values as hexadecimal string when inserting or checking.
            Values are converted to bytes before inserting, if this conversion fail,
            the string is inserted a normal way.
            """)
            .flag(default = true)
}

abstract class BloomFilterCreatingCommand(name: String, help: String) : BloomFilterAwareCommand(name = name, help = help) {

    protected val size: Long by option(help = "Expected number of elements to be inserted").long().required()

    protected val fpp: Double by option(help = "Target maximum probability of false positives").double().default(1e-6)
}
