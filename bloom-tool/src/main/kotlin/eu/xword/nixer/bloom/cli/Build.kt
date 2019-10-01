package eu.xword.nixer.bloom.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.groups.cooccurring
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import java.io.File
import java.io.InputStream

/**
 * Created on 26/09/2019.
 *
 * @author gcwiak
 */
class Build : CliktCommand(help = """
        Combination of 'create' and 'insert' commands.    
        Creates a new bloom filter and inserts values from standard input. 
        Each line is a separate value.
    """) {

    private val stdin: Boolean by option(help = "Indicates that data for insertion should be read from standard input.")
            .flag(default = false)

    private val inputFile: File? by option(help = "Name of the file with data for insertion.")
            .file(exists = true, folderOkay = false, fileOkay = true)

    private val basicFilterOptions by BasicFilterOptions().required()
    private val detailedFilterOptions by DetailedFilterOptions().required()

    private val preprocessOptions by PreprocessOptions().cooccurring()

    override fun run() {

        val inputStream: InputStream = when {
            stdin && inputFile == null -> System.`in`

            !stdin && inputFile != null -> inputFile!!.inputStream()

            else -> throw IllegalArgumentException(
                    "Either standard input or input file must be chosen, but was: '--stdin=$stdin', '--inputFile=$inputFile'"
            )
        }

        val bloomFilter = createFilter(
                basicFilterOptions.name,
                basicFilterOptions.hex,
                detailedFilterOptions.size,
                detailedFilterOptions.fpp
        )

        val entryTransformer = preprocessOptions
                ?.run { fieldExtractor(separator, field) }
                ?: { it }

        insertIntoFilter(bloomFilter, entryTransformer, inputStream)
    }
}
