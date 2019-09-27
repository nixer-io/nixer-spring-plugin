package eu.xword.nixer.bloom.cli

import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.io.IOException

/**
 * Created on 23/08/2018.
 *
 * @author cezary
 */
class BloomToolMainTest {

    @Rule
    @JvmField
    val temporaryFolder = TemporaryFolder()

    @Test
    fun `should create filter`() {
        // given
        val file = temporaryFolder.newFile("test.bloom")
        delete(file)

        // when
        main(arrayOf("create", "--size=100", "--fpp=1e-2", file.absolutePath))

        // then
        assertThat(file).exists()
    }

    private fun delete(file: File) {
        if (!file.delete()) {
            throw IOException("Failed to delete: $file")
        }
    }
}