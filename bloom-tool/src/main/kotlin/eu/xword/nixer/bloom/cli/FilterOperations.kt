package eu.xword.nixer.bloom.cli

import com.google.common.hash.Funnel
import com.google.common.hash.Funnels
import eu.xword.nixer.bloom.BloomFilter
import eu.xword.nixer.bloom.FileBasedBloomFilter
import eu.xword.nixer.bloom.HexFunnel
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
