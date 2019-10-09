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
import eu.xword.nixer.bloom.NotHexStringException
import java.io.File
import java.io.InputStream

abstract class InputStreamingCommand(name: String, help: String) : CliktCommand(name = name, help = help) {

    private val inputOptions by InputOptions().required()

    protected val hashInput: Boolean by option(help = """
            Flag indicating whether the input values should be hashed with SHA-1.
            
            When not set (or '--no-input-hashing') it is expected the input values are already SHA-1 hashed and passed as hexadecimal strings. 
            """).flag(default = false, secondaryNames = *arrayOf("--no-input-hashing"))

    protected fun inputStream(): InputStream = with(inputOptions) {
        when {
            stdin && inputFile == null -> System.`in`

            !stdin && inputFile != null -> inputFile!!.inputStream()

            else -> throw IllegalArgumentException(
                    "Either standard input or input file must be chosen, but was: '--stdin=$stdin', '--inputFile=$inputFile'"
            )
        }
    }
}

class InputOptions : OptionGroup(name = "Input options", help = "Specify way of reading input entries. Pick one.") {

    val stdin: Boolean by option(help = "Indicates that data should be read from standard input.")
            .flag(default = false)

    val inputFile: File? by option(help = "Name of the file with input data.")
            .file(exists = true, folderOkay = false, fileOkay = true)
}

class BasicFilterOptions : OptionGroup(name = "Basic filter options") {
    val name: String by option(help = """
            Name of the Bloom filter. Corresponds to name of the file with filter parameters and prefix of the data file.
            """)
            .required()
}

private const val DEFAULT_FPP = 1e-6

class DetailedFilterOptions : OptionGroup(name = "Detailed filter options") {
    val size: Long by option(help = "Expected number of elements to be inserted").long().required()

    val fpp: Double by option(help = "Target maximum probability of false positives. (DEFAULT: $DEFAULT_FPP)")
            .double().default(DEFAULT_FPP)
}

class EntryParsingOptions : OptionGroup(name = "Entry parsing options",
        help = """
        Not mandatory. Usable when input entries need to be parsed before inserting values into the filter, 
        i.e. the values to be added have to be extracted from CSV-like structure: 
            ```
            <VALUE_TO_ADD>:<SOMETHING_IRRELEVANT>
            ```
        """) {

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

fun tryExecuting(codeBlock: () -> Unit) {
    try {
        codeBlock()
    } catch (e: NotHexStringException) {
        throw UsageError(
                "Invalid input: ${e.message}. Ensure input values are hashed or use hash-input option. Check help for details."
        )
    }
}
