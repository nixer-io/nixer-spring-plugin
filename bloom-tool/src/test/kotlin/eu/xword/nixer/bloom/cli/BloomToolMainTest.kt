package eu.xword.nixer.bloom.cli

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.ExpectedSystemExit
import org.junit.contrib.java.lang.system.SystemErrRule
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.rules.TemporaryFolder
import java.io.File
import java.io.IOException


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

    @Rule
    @JvmField
    val exit: ExpectedSystemExit = ExpectedSystemExit.none()

    @Rule
    @JvmField
    val systemErr: SystemErrRule = SystemErrRule().enableLog()

    @Rule
    @JvmField
    val systemOut: SystemOutRule = SystemOutRule().enableLog()

    private lateinit var filterFile: File
    private lateinit var valuesFile: File
    private lateinit var checkFile: File

    @Before
    fun setUp() {
        filterFile = givenFile("test.bloom")
        valuesFile = givenFile("values.txt")
        checkFile = givenFile("check.txt")
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
        executeCommand("create", "--size=3", "--name=${filterFile.absolutePath}")

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
        assertThat(systemOut.log).contains(hashToLookFor)
    }

    @Test
    fun `should build bloom filter from unparsed entries and execute successful check`() {
        // given
        val hashToLookFor = "CBFDAC6008F9CAB4083784CBD1874F76618D2A97" // password123 SHA-1

        val hexHashesWithAdditionalColumn = listOf(
                "BB928CA332F5F4FA2CDAEF238672E0FBCF5E7A0F:irrelevant_column", // foobar1 SHA-1
                "$hashToLookFor:another_irrelevant",
                "42E1D179E9781138DF3471EEF084F6622A0E7091:and_one_more" // IamTheBest SHA-1
        )

        givenValuesToInsert(hexHashesWithAdditionalColumn)
        givenValueToCheck(hashToLookFor)

        // when
        executeCommand("build",
                "--size=3", "--input-file=${valuesFile.absolutePath}", "--separator=:", "--field=0",
                "--name=${filterFile.absolutePath}")

        executeCommand("check", "--input-file=${checkFile.absolutePath}", "--name=${filterFile.absolutePath}")

        // then
        assertThat(systemOut.log).contains(hashToLookFor)
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
                "--size=3", "--input-file=${valuesFile.absolutePath}",
                "--name=${filterFile.absolutePath}",
                "--hash-input"
        )

        executeCommand("check", "--input-file=${checkFile.absolutePath}", "--name=${filterFile.absolutePath}")

        // then
        assertThat(systemOut.log).contains(hashToLookFor)
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
                "--size=3", "--input-file=${valuesFile.absolutePath}",
                "--name=${filterFile.absolutePath}",
                "--hash-input")

        executeCommand("check", "--input-file=${checkFile.absolutePath}", "--name=${filterFile.absolutePath}", "--hash-input")

        // then
        assertThat(systemOut.log).contains(valueToLookFor)
    }

    @Test
    fun `should fail building bloom filter from not hashed values while expecting hashed ones`() {
        // given
        val notHashedValues = listOf(
                "foobar1",
                "password123",
                "IamTheBest"
        )

        givenValuesToInsert(notHashedValues)

        // system exit assertions must be defined before execution
        assertErrorExit(notHashedValues[0])

        // when
        executeCommand("build",
                "--size=3", "--input-file=${valuesFile.absolutePath}",
                "--name=${filterFile.absolutePath}",
                "--no-input-hashing")
    }

    @Test
    fun `should build bloom filter and fail to execute check using not hashed value while expecting hashed one`() {
        // given
        val valueToLookFor = "password123"

        val notHashedValues = listOf(
                "foobar1",
                valueToLookFor,
                "IamTheBest"
        )

        givenValuesToInsert(notHashedValues)
        givenValueToCheck(valueToLookFor)

        executeCommand("build",
                "--size=3", "--input-file=${valuesFile.absolutePath}",
                "--name=${filterFile.absolutePath}",
                "--hash-input")

        // system exit assertions must be defined before execution
        assertErrorExit(valueToLookFor)

        // when
        executeCommand("check",
                "--input-file=${checkFile.absolutePath}",
                "--name=${filterFile.absolutePath}",
                "--no-input-hashing")
    }

    @Test
    fun `should fail to insert not hashed values into bloom filter while expecting hashed ones`() {
        // given
        executeCommand("create", "--size=3", "--name=${filterFile.absolutePath}")

        val notHashedValues = listOf(
                "foobar1",
                "password123",
                "IamTheBest"
        )

        givenValuesToInsert(notHashedValues)

        // system exit assertions must be defined before execution
        assertErrorExit(notHashedValues[0])

        // when
        executeCommand("insert", "--input-file=${valuesFile.absolutePath}", "--name=${filterFile.absolutePath}")
    }

    private fun assertErrorExit(errMsgValue: String) {
        exit.expectSystemExitWithStatus(1)
        exit.checkAssertionAfterwards {
            assertThat(systemErr.log).contains(
                    "Error: Invalid input:",
                    errMsgValue,
                    "not a hexadecimal string"
            )
        }
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