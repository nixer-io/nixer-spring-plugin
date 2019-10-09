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

    private lateinit var filterFile: File
    private lateinit var valuesFile: File
    private lateinit var checkFile: File

    @Before
    fun setUp() {
        System.setOut(PrintStream(commandOutput))

        filterFile = givenFile("test.bloom")
        valuesFile = givenFile("values.txt")
        checkFile = givenFile("check.txt")
    }

    @After
    fun tearDown() {
        System.setOut(originalOut)
    }

    @Test
    fun `should create bloom filter`() {
        // when
        executeCommand("create", "--size=100", "--fpp=1e-2", "--name=${filterFile.absolutePath}")

        // then
        assertThat(filterFile).exists()
    }

    @Test
    fun `should insert values into bloom filter and execute successful check`() {
        // given
        executeCommand("create", "--size=3", "--fpp=1e-2", "--name=${filterFile.absolutePath}")

        val hashToLookFor = "CBFDAC6008F9CAB4083784CBD1874F76618D2A97" // password123 SHA-1

        val hexHashes = listOf(
                "BB928CA332F5F4FA2CDAEF238672E0FBCF5E7A0F", // foobar1 SHA-1
                hashToLookFor,
                "42E1D179E9781138DF3471EEF084F6622A0E7091" // IamTheBest SHA-1
        )

        givenValuesToInsert(hexHashes)
        givenValueToCheck(hashToLookFor)

        // when
        executeCommand("insert", "--input-file=${valuesFile.absolutePath}", "--name=${filterFile.absolutePath}")

        executeCommand("check", "--input-file=${checkFile.absolutePath}", "--name=${filterFile.absolutePath}")

        // then
        assertThat(commandOutput.toString()).contains(hashToLookFor)
    }

    @Test
    fun `should build bloom filter from unparsed entries and execute successful check`() {
        // given
        val hashToLookFor = "CBFDAC6008F9CAB4083784CBD1874F76618D2A97" // password123 SHA-1

        val hexHashesWithAdditionalColumn = listOf(
                "BB928CA332F5F4FA2CDAEF238672E0FBCF5E7A0F:54", // foobar1 SHA-1
                "$hashToLookFor:7",
                "42E1D179E9781138DF3471EEF084F6622A0E7091:2" // IamTheBest SHA-1
        )

        givenValuesToInsert(hexHashesWithAdditionalColumn)
        givenValueToCheck(hashToLookFor)

        // when
        executeCommand("build",
                "--size=3", "--fpp=1e-2", "--input-file=${valuesFile.absolutePath}", "--separator=:", "--field=0",
                "--name=${filterFile.absolutePath}")

        executeCommand("check", "--input-file=${checkFile.absolutePath}", "--name=${filterFile.absolutePath}")

        // then
        assertThat(commandOutput.toString()).contains(hashToLookFor)
    }

    @Test
    fun `should build bloom filter from not hashed values and execute successful check using hash`() {
        // given
        val hashToLookFor = "CBFDAC6008F9CAB4083784CBD1874F76618D2A97" // password123 SHA-1

        val notHashedValues = listOf(
                "foobar1",
                "password123",
                "IamTheBest"
        )

        givenValuesToInsert(notHashedValues)
        givenValueToCheck(hashToLookFor)

        // when
        executeCommand("build",
                "--size=3", "--fpp=1e-2", "--input-file=${valuesFile.absolutePath}",
                "--name=${filterFile.absolutePath}",
                "--hash-input"
        )

        executeCommand("check", "--input-file=${checkFile.absolutePath}", "--name=${filterFile.absolutePath}")

        // then
        assertThat(commandOutput.toString()).contains(hashToLookFor)
    }

    @Test
    fun `should build bloom filter from not hashed values and execute successful check using not hashed value`() {
        // given
        val valueToLookFor = "password123"

        val notHashedValues = listOf(
                "foobar1",
                valueToLookFor,
                "IamTheBest"
        )

        givenValuesToInsert(notHashedValues)
        givenValueToCheck(valueToLookFor)

        // when
        executeCommand("build",
                "--size=3", "--fpp=1e-2", "--input-file=${valuesFile.absolutePath}",
                "--name=${filterFile.absolutePath}",
                "--hash-input"
        )

        executeCommand("check", "--input-file=${checkFile.absolutePath}", "--name=${filterFile.absolutePath}", "--hash-input")

        // then
        assertThat(commandOutput.toString()).contains(valueToLookFor)
    }

    private fun givenValueToCheck(valueToCheck: String) {
        checkFile.apply { writeText(valueToCheck) }
    }

    private fun givenValuesToInsert(valuesToInsert: List<String>) {
        valuesFile.apply {
            printWriter().use { valuesToInsert.forEach { value -> it.println(value) } }
        }
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