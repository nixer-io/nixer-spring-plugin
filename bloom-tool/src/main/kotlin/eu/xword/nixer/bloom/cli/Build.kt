package eu.xword.nixer.bloom.cli

import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.groups.cooccurring
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.int
import com.google.common.base.Charsets
import java.io.InputStreamReader

/**
 * Created on 26/09/2019.
 *
 * @author gcwiak
 */
class Build : BloomFilterCreatingCommand(name = "build",
        help = """
        Combination of 'create' and 'insert' commands.    
        Creates a new bloom filter and inserts values from standard input. 
        Each line is a separate value.
    """) {

    private val preprocess by PreprocessOptions().cooccurring()

    private class PreprocessOptions : OptionGroup() {
        val separator: String by option(help = "separator to be used for extracting values from the input entries").required()

        val field: Int by option(
                help = "number of the field to be taken as value after splitting by separator. Starts from 0."
        ).int().required()
    }

    override fun run() {

        val bloomFilter = createFilter(name, size, fpp, hex)

        val preprocessor: (String) -> String = preprocess
                ?.run { { line: String -> line.split(separator)[field] } }
                ?: { line -> line }

        InputStreamReader(System.`in`, Charsets.UTF_8.newDecoder()).buffered().use { reader ->
            reader.lines().forEach {
                bloomFilter.put(preprocessor(it))
            }
        }
    }
}