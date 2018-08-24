package eu.xword.nixer.bloom;

import java.io.File;
import java.io.IOException;

import com.google.common.hash.Funnels;
import junitparams.JUnitParamsRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link FileBasedBloomFilter}.
 * <br>
 * Created on 24/08/2018.
 *
 * @author cezary
 */
@RunWith(JUnitParamsRunner.class)
public class FileBasedBloomFilterTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldCreate() throws IOException {
        // given
        final File file = temporaryFolder.newFile("test.bloom");
        delete(file);

        // when

        final BloomFilter<Integer> filter = FileBasedBloomFilter.create(
                file.toPath(), Funnels.integerFunnel(),
                100,
                0.1
        );

        // then
        assertThat(filter).isNotNull();
        assertThat(file).exists();
    }


    @Test
    public void shouldNotFindAnythingBeforeInserting() throws IOException {
        // given
        final File file = temporaryFolder.newFile("test.bloom");
        delete(file);
        final BloomFilter<Integer> filter = FileBasedBloomFilter.create(
                file.toPath(), Funnels.integerFunnel(),
                100,
                0.1
        );

        // then
        final boolean checkOne = filter.mightContain(1);
        final boolean checkTwo = filter.mightContain(1);

        // then
        assertThat(checkOne).isFalse();
        assertThat(checkTwo).isFalse();
    }

    @Test
    public void shouldFindAfterInserting() throws IOException {
        // given
        final File file = temporaryFolder.newFile("test.bloom");
        delete(file);
        final BloomFilter<Integer> filter = FileBasedBloomFilter.create(
                file.toPath(), Funnels.integerFunnel(),
                100,
                0.1
        );

        // then
        final boolean changed = filter.put(1);
        final boolean found = filter.mightContain(1);

        // then
        assertThat(changed).isTrue();
        assertThat(found).isTrue();
    }

    @Test
    public void shouldFindAfterInsertingReopening() throws IOException {
        // given
        final File file = temporaryFolder.newFile("test.bloom");
        delete(file);
        final BloomFilter<Integer> filter = FileBasedBloomFilter.create(
                file.toPath(), Funnels.integerFunnel(),
                100,
                0.1
        );

        // then
        final boolean changed = filter.put(1);

        final BloomFilter<Integer> filter2 = FileBasedBloomFilter.open(file.toPath(), Funnels.integerFunnel());
        final boolean found = filter2.mightContain(1);

        // then
        assertThat(changed).isTrue();
        assertThat(found).isTrue();
    }

    private static void delete(final File file) throws IOException {
        if (!file.delete()) {
            throw new IOException("Failed to delete: " + file);
        }
    }
}