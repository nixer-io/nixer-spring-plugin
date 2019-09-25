package eu.xword.nixer.bloom

import com.google.common.base.Charsets
import com.google.common.base.Preconditions
import com.google.common.hash.Funnel
import com.google.common.hash.Funnels
import org.docopt.Docopt
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.file.Paths

/**
 * The entry class for a command utility to manipulate file-based bloom filters. See `bloom-docopt.txt` for usage information.
 * <br></br>
 * Created on 23/08/2018.
 *
 * @author cezary.biernacki@crosswordcybersecurity.com
 */
class BloomToolMain(private val parsed: Map<String, Any>) {

    private val funnel: Funnel<CharSequence>
        get() {
            val defaultFunnel = Funnels.unencodedCharsFunnel()
            return if (isSet("--hex"))
                HexFunnel(defaultFunnel)
            else
                defaultFunnel
        }

    init {
        Preconditions.checkNotNull(parsed, "parsed")
    }

    @Throws(IOException::class)
    private fun handle() {
        if (isSet("create")) {
            handleCreate()
            return
        }

        if (isSet("insert")) {
            handleInsert()
            return
        }

        if (isSet("check")) {
            handleCheck()
            return
        }

        if (isSet("benchmark")) {
            handleBenchmark()
            return
        }

        throw IllegalArgumentException("Failed to understand arguments: $parsed")
    }

    private fun handleCreate() {
        val name = extract("NAME") as String
        val size = extractSize()
        val fpp = extractFpp()


        FileBasedBloomFilter.create(
                Paths.get(name),
                funnel,
                size,
                fpp
        )
    }


    @Throws(IOException::class)
    private fun handleInsert() {
        val name = extract("NAME") as String
        val filter = FileBasedBloomFilter.open(
                Paths.get(name),
                funnel
        )

        BufferedReader(InputStreamReader(System.`in`, Charsets.UTF_8.newDecoder())).use { reader -> reader.lines().forEach { filter.put(it) } }
    }

    @Throws(IOException::class)
    private fun handleCheck() {
        val name = extract("NAME") as String
        val filter = FileBasedBloomFilter.open(
                Paths.get(name),
                funnel
        )

        BufferedReader(InputStreamReader(System.`in`, Charsets.UTF_8.newDecoder())).use { reader -> reader.lines().filter { filter.mightContain(it) }.forEach { println(it) } }
    }

    @Throws(IOException::class)
    private fun handleBenchmark() {
        val size = extractSize()
        val fpp = extractFpp()
        Benchmark(size, fpp).run()
    }

    private fun extractSize(): Long {
        val key = "--size"
        val result = extractLong(key)
        Preconditions.checkArgument(result > 0, "Parameter '%s' should be bigger then 0", key)
        return result
    }

    private fun extractFpp(): Double {
        val key = "--fpp"
        val result = extractDouble(key)
        Preconditions.checkArgument(result > 0 && result < 1, "Parameter '%s' should be in range (0, 1) /exclusive/", key)
        return result
    }

    private fun isSet(key: String): Boolean {
        return java.lang.Boolean.TRUE == extract(key)
    }

    private fun extractLong(key: String): Long {
        try {
            return java.lang.Long.parseLong(extract(key) as String)
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException(String.format("Parameter '%s' should be an integer number", key))
        }

    }

    private fun extract(key: String): Any {
        return Preconditions.checkNotNull<Any>(parsed[key], "Missing parameter '%s'", key)
    }

    private fun extractDouble(key: String): Double {
        try {
            return java.lang.Double.parseDouble(extract(key) as String)
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException(String.format("Parameter '%s' should be a floating point number", key))
        }

    }

    companion object {

        @Throws(IOException::class)
        @JvmStatic
        fun main(argv: Array<String>) {

            val resourceAsStream = BloomToolMain::class.java.getResourceAsStream("bloom-docopt.txt")
            val docopt = Docopt(resourceAsStream)

            val parsed = docopt.parse(*argv)
            BloomToolMain(parsed).handle()
        }
    }

}
