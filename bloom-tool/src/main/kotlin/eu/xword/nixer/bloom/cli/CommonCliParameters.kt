package eu.xword.nixer.bloom.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.UsageError
import com.github.ajalt.clikt.parameters.groups.CoOccurringOptionGroup
import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.double
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.long
import java.io.File
import java.io.InputStream

abstract class InputStreamingCommand(name: String, help: String) : CliktCommand(name = name, help = help) {

    private val stdin: Boolean by option(help = "Indicates that data for insertion should be read from standard input.")
            .flag(default = false)

    private val inputFile: File? by option(help = "Name of the file with data for insertion.")
            .file(exists = true, folderOkay = false, fileOkay = true)

    protected fun inputStream(): InputStream = when {
        stdin && inputFile == null -> System.`in`

        !stdin && inputFile != null -> inputFile!!.inputStream()

        else -> throw IllegalArgumentException(
                "Either standard input or input file must be chosen, but was: '--stdin=$stdin', '--inputFile=$inputFile'"
        )
    }
}

class BasicFilterOptions : OptionGroup(name = "Basic filter options") {
    val name: String by option(help = """
            Name of the bloom filter. Corresponds to name of the file with filter parameters and prefix of the data file.
            """)
            .required()

    val hex: Boolean by option(help = """
            Flag indicating whether to interpret input values as hexadecimal string when inserting or checking.
            Values are converted to bytes before inserting, if this conversion fail,
            the string is inserted a normal way. (DEFAULT: HEX)
            """)
            .flag(default = true, secondaryNames = *arrayOf("--no-hex"))
}

private const val DEFAULT_FPP = 1e-6

class DetailedFilterOptions : OptionGroup(name = "Detailed filter options") {
    val size: Long by option(help = "Expected number of elements to be inserted").long().required()

    val fpp: Double by option(help = "Target maximum probability of false positives. (DEFAULT: $DEFAULT_FPP)")
            .double().default(DEFAULT_FPP)
}

class PreprocessOptions : OptionGroup() {

    val separator: String by option(help = "separator to be used for extracting values from the input entries").required()
    val field: Int by option(
            help = "number of the field to be taken as value after splitting by separator. Starts from 0."
    ).int().required()

}

fun <T : OptionGroup> T.required(): CoOccurringOptionGroup<T, T> {
    return CoOccurringOptionGroup(this) { occurred, g, _ ->
        if (occurred == true) g
        else {
            val groupName = when {
                this.groupName != null -> this.groupName.toString()
                else -> this.javaClass.simpleName
            }
            throw UsageError("Missing options: $groupName. Check help for details.")
        }
    }
}
