package eu.xword.nixer.bloom

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.double
import com.github.ajalt.clikt.parameters.types.long
import com.google.common.base.Charsets
import com.google.common.hash.Funnel
import com.google.common.hash.Funnels
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Paths

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
                Check(),
                BenchmarkCmd()
        )
        .main(args)

class Create : BloomFilterAwareCommand(name = "create", help = "Creates a new bloom filter.") {

    private val size: Long by option(help = "Expected number of elements to be inserted").long().required()

    private val fpp: Double by option(help = "Target maximum probability of false positives").double().default(1e-6)

    override fun run() {
        FileBasedBloomFilter.create(
                Paths.get(name),
                getFunnel(),
                size,
                fpp
        )
    }
}

class Insert : BloomFilterAwareCommand(name = "insert",
        help = """
        Inserts values to the filter from standard input.
        Each line is a separate value.
        
        Example:
        ```
        bloom-tool insert my.bloom < entries.txt
        cat entries.txt | bloom-tool insert my.bloom
        # both variants insert lines from entries.txt to my.bloom
        ```
    """) { // TODO do not hardcode command names in help text

    override fun run() {
        val filter = openFilter()

        BufferedReader(
                InputStreamReader(System.`in`, Charsets.UTF_8.newDecoder())
        ).use { reader ->
            reader.lines().forEach { filter.put(it) }
        }
    }
}

class Check : BloomFilterAwareCommand(name = "check",
        help = """
        Checks if values provided in the standard input appear in the filter,
        printing matches to the standard output, and skipping not matched values.
        Each line is a separate value.
        
        Example:
        ```
        echo "example" | bloom-tool check my.bloom
        # checks if string "example" might be inserted in my.bloom,
        # printing it standard output if it might be true, and skipping it otherwise
        ```
    """) { // TODO do not hardcode command names in help text

    override fun run() {
        val filter = openFilter()

        BufferedReader(
                InputStreamReader(System.`in`, Charsets.UTF_8.newDecoder())
        ).use { reader ->
            reader.lines().filter { filter.mightContain(it) }.forEach { println(it) }
        }
    }
}

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

class BenchmarkCmd : CliktCommand(name = "benchmark",
        help = """
        Runs performance benchmark and correctness verification
        by creating a filter in a temporary directory,
        populating it with random data and checking what can be found.
    """) {

    private val size: Long by option(help = "Expected number of elements to be inserted").long().required()

    private val fpp: Double by option(help = "Target maximum probability of false positives").double().default(1e-6)

    override fun run() {
        Benchmark(size, fpp).run()
    }
}
