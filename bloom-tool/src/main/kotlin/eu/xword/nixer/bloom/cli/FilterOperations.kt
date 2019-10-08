package eu.xword.nixer.bloom.cli

import com.google.common.base.Charsets
import com.google.common.hash.Funnel
import com.google.common.hash.Funnels
import com.google.common.hash.Hashing
import eu.xword.nixer.bloom.BloomFilter
import eu.xword.nixer.bloom.BloomFilterCheck
import eu.xword.nixer.bloom.FileBasedBloomFilter
import eu.xword.nixer.bloom.HexFunnel
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Paths
import java.util.function.Predicate

/**
 * Created on 26/09/2019.
 *
 * @author gcwiak
 */
fun createFilter(name: String, hex: Boolean, size: Long, fpp: Double): BloomFilter<CharSequence> = FileBasedBloomFilter.create(
        Paths.get(name).also { require(Files.notExists(it)) { "Bloom filter metadata file '$it' already exist" } },
        getFunnel(hex),
        size,
        fpp
)

fun openFilter(name: String, hex: Boolean): BloomFilter<CharSequence> = FileBasedBloomFilter.open(
        Paths.get(name).also { require(Files.exists(it)) { "Bloom filter metadata file '$it' does not exist" } },
        getFunnel(hex)
)

fun openFilterForCheck(name: String, hashInputBeforeCheck: Boolean): Predicate<String> {

    val filterFilePath = Paths.get(name).also { require(Files.exists(it)) { "Bloom filter metadata file '$it' does not exist" } }

    return when {
        hashInputBeforeCheck -> BloomFilterCheck.hashingBeforeCheck(filterFilePath)
        else -> BloomFilterCheck.notHashingBeforeCheck(filterFilePath)
    }
}

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

fun checkAgainstFilter(bloomFilter: Predicate<String>, entriesStream: InputStream) {
    InputStreamReader(entriesStream, Charsets.UTF_8.newDecoder()).buffered().use { reader ->
        reader.lines()
                .filter { bloomFilter.test(it) }
                .forEach { println(it) }
    }
}

fun fieldExtractor(separator: String, fieldNumber: Int): (String) -> String = { line: String -> line.split(separator)[fieldNumber] }

private val sha1HashFunction = Hashing.sha1()

fun sha1(entry: String): String {
    return sha1HashFunction.hashString(entry, kotlin.text.Charsets.UTF_8).toString().toUpperCase()
}
