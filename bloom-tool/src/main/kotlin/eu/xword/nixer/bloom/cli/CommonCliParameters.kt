package eu.xword.nixer.bloom.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.double
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.long

abstract class BloomFilterAwareCommand(name: String, help: String) : CliktCommand(name = name, help = help) {

    protected val name: String by argument(help = """
            Name of the bloom filter. Corresponds to name of the file with filter parameters and prefix of the data file.
            """)

    protected val hex: Boolean by option(help = """
            Flag indicating whether to interpret input values as hexadecimal string when inserting or checking.
            Values are converted to bytes before inserting, if this conversion fail,
            the string is inserted a normal way. (DEFAULT: HEX)
            """)
            .flag(default = true, secondaryNames = *arrayOf("--no-hex"))
}

private const val DEFAULT_FPP = 1e-6

abstract class BloomFilterCreatingCommand(name: String, help: String) : BloomFilterAwareCommand(name = name, help = help) {

    protected val size: Long by option(help = "Expected number of elements to be inserted").long().required()

    protected val fpp: Double by option(help = "Target maximum probability of false positives. (DEFAULT: $DEFAULT_FPP)")
            .double().default(DEFAULT_FPP)
}

class PreprocessOptions : OptionGroup() {
    val separator: String by option(help = "separator to be used for extracting values from the input entries").required()

    val field: Int by option(
            help = "number of the field to be taken as value after splitting by separator. Starts from 0."
    ).int().required()
}
