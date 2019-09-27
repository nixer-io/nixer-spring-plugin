package eu.xword.nixer.bloom.cli

import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.PrintStream


/**
 * Tests for [BloomToolMain] CLI.
 *
 * Created on 23/08/2018.
 *
 * @author cezary
 */
class BloomToolMainTest {

    @Rule
    @JvmField
    val temporaryFolder = TemporaryFolder()

    private val commandOutput = ByteArrayOutputStream()
    private val originalOut = System.out

    @Before
    fun setUp() {
        System.setOut(PrintStream(commandOutput))
    }

    @After
    fun tearDown() {
        System.setOut(originalOut)
    }

    @Test
    fun `should create bloom filter`() {
        // given
        val filterFile = givenFile("test.bloom")

        // when
        executeCommand("create", "--size=100", "--fpp=1e-2", filterFile.absolutePath)

        // then
        assertThat(filterFile).exists()
    }

    @Test
    fun `should insert values into bloom filter and execute successful check`() {
        // given
        val filterFile = givenFile("test.bloom")
        executeCommand("create", "--size=3", "--fpp=1e-2", filterFile.absolutePath)
        assertThat(filterFile).exists()

        val hashToLookFor = "FFFFFFF8A0382AA9C8D9536EFBA77F261815334D"

        val hexHashes = listOf(
                "FFFFFFF1A63ACC70BEA924C5DBABEE4B9B18C82D",
                hashToLookFor,
                "FFFFFFFEE791CBAC0F6305CAF0CEE06BBE131160"
        )

        val valuesFile = givenFile("values.txt").apply {
            printWriter().use { hexHashes.forEach { hash -> it.println(hash) } }
        }

        val checkFile = givenFile("check.txt").apply { writeText(hashToLookFor) }

        // when
        executeCommand("insert", "--input-file=${valuesFile.absolutePath}", filterFile.absolutePath)

        executeCommand("check", "--input-file=${checkFile.absolutePath}", filterFile.absolutePath)

        // then
        assertThat(commandOutput.toString()).contains(hashToLookFor)
    }

    @Test
    fun `should build bloom filter with values and execute successful check`() {
        // given
        val filterFile = givenFile("test.bloom")

        val hashToLookFor = "FFFFFFF8A0382AA9C8D9536EFBA77F261815334D"

        val hexHashesWithAdditionalColumn = listOf(
                "FFFFFFF1A63ACC70BEA924C5DBABEE4B9B18C82D:54",
                "$hashToLookFor:7",
                "FFFFFFFEE791CBAC0F6305CAF0CEE06BBE131160:2"
        )

        val valuesFile = givenFile("values.txt").apply {
            printWriter().use { hexHashesWithAdditionalColumn.forEach { hash -> it.println(hash) } }
        }

        val checkFile = givenFile("check.txt").apply { writeText(hashToLookFor) }

        // when
        executeCommand("build",
                "--size=3", "--fpp=1e-2", "--input-file=${valuesFile.absolutePath}", "--separator=:", "--field=0",
                filterFile.absolutePath)

        executeCommand("check", "--input-file=${checkFile.absolutePath}", filterFile.absolutePath)

        // then
        assertThat(commandOutput.toString()).contains(hashToLookFor)
    }

    private fun executeCommand(vararg command: String) {
        main(arrayOf(*command))
    }

    private fun givenFile(name: String): File {
        val file = temporaryFolder.newFile(name)
        delete(file)
        return file
    }

    private fun delete(file: File) {
        if (!file.delete()) {
            throw IOException("Failed to delete: $file")
        }
    }
}