package eu.xword.nixer.bloom.cli

import com.google.common.base.Charsets
import java.io.InputStreamReader

class Check : BloomFilterAwareCommand(name = "check",
        help = """
        Checks if values provided in the standard input appear in the filter,
        printing matches to the standard output, and skipping not matched values.
        Each line is a separate value.
    """) {

    override fun run() {
        val bloomFilter = openFilter()

        InputStreamReader(System.`in`, Charsets.UTF_8.newDecoder()).buffered().use { reader ->
            reader.lines()
                    .filter { bloomFilter.mightContain(it) }
                    .forEach { println(it) }
        }
    }
}
