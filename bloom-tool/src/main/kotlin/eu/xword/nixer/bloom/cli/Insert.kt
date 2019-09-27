package eu.xword.nixer.bloom.cli

import com.google.common.base.Charsets
import java.io.InputStreamReader

class Insert : BloomFilterAwareCommand(name = "insert",
        help = """
        Inserts values to the filter from standard input.
        Each line is a separate value.
    """) {

    override fun run() {
        val bloomFilter = openFilter(name, hex)

        InputStreamReader(System.`in`, Charsets.UTF_8.newDecoder()).buffered().use { reader ->
            reader.lines().forEach { bloomFilter.put(it) }
        }
    }
}
