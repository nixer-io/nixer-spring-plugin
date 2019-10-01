package eu.xword.nixer.bloom.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import java.io.File
import java.io.InputStream

class Check : CliktCommand(help = """
        Checks if values provided in the standard input appear in the filter,
        printing matches to the standard output, and skipping not matched values.
        Each line is a separate value.
    """) {

    private val basicFilterOptions by BasicFilterOptions().required()

    private val stdin: Boolean by option(help = "Indicates that data for insertion should be read from standard input.")
            .flag(default = false)

    private val inputFile: File? by option(help = "Name of the file with data for insertion.")
            .file(exists = true, folderOkay = false, fileOkay = true)

    override fun run() {

        val inputStream: InputStream = when {
            stdin && inputFile == null -> System.`in`

            !stdin && inputFile != null -> inputFile!!.inputStream()

            else -> throw IllegalArgumentException(
                    "Either standard input or input file must be chosen, but was: '--stdin=$stdin', '--inputFile=$inputFile'"
            )
        }

        val bloomFilter = with(basicFilterOptions) {
            openFilter(name, hex)
        }

        checkAgainstFilter(bloomFilter, inputStream)
    }
}
