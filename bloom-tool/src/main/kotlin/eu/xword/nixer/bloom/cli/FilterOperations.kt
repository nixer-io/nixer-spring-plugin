package eu.xword.nixer.bloom.cli

import com.google.common.base.Charsets
import com.google.common.hash.Funnel
import com.google.common.hash.Funnels
import eu.xword.nixer.bloom.BloomFilter
import eu.xword.nixer.bloom.FileBasedBloomFilter
import eu.xword.nixer.bloom.HexFunnel
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Created on 26/09/2019.
 *
 * @author gcwiak
 */
fun createFilter(name: String, size: Long, fpp: Double, hex: Boolean): BloomFilter<CharSequence> = FileBasedBloomFilter.create(
        Paths.get(name).also { require(Files.notExists(it)) { "Bloom filter metadata file '$it' already exist" } },
        getFunnel(hex),
        size,
        fpp
)

fun openFilter(name: String, hex: Boolean): BloomFilter<CharSequence> = FileBasedBloomFilter.open(
        Paths.get(name).also { require(Files.exists(it)) { "Bloom filter metadata file '$it' does not exist" } },
        getFunnel(hex)
)

private fun getFunnel(hex: Boolean): Funnel<CharSequence> = when {
    hex -> HexFunnel(Funnels.unencodedCharsFunnel())
    else -> Funnels.unencodedCharsFunnel()
}

fun insertIntoFilter(targetFilter: BloomFilter<CharSequence>,
                     entryTransformer: (String) -> String,
                     entriesStream: InputStream) {
    InputStreamReader(entriesStream, Charsets.UTF_8.newDecoder()).buffered().use { reader ->
        reader.lines().forEach {
            targetFilter.put(entryTransformer(it))
        }
    }
}

fun checkAgainstFilter(bloomFilter: BloomFilter<CharSequence>, entriesStream: InputStream) {
    InputStreamReader(entriesStream, Charsets.UTF_8.newDecoder()).buffered().use { reader ->
        reader.lines()
                .filter { bloomFilter.mightContain(it) }
                .forEach { println(it) }
    }
}

fun fieldExtractor(separator: String, fieldNumber: Int): (String) -> String = { line: String -> line.split(separator)[fieldNumber] }
