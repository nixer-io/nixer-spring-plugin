package io.nixer.bloom;

import java.io.File;
import java.nio.file.Path;

import com.google.common.hash.Funnels;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for {@link FileBasedBloomFilter}.
 * <br>
 * Created on 24/08/2018.
 *
 * @author cezary
 */
class FileBasedBloomFilterTest {

    @TempDir
    Path temporaryFolder;

    @Test
    void shouldCreate() {
        // given
        final File file = temporaryFolder.resolve("test.bloom").toFile();

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
    void shouldNotFindAnythingBeforeInserting() {
        // given
        final File file = temporaryFolder.resolve("test.bloom").toFile();
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
    void shouldFindAfterInserting() {
        // given
        final File file = temporaryFolder.resolve("test.bloom").toFile();
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
    void shouldFindAfterInsertingReopening() {
        // given
        final File file = temporaryFolder.resolve("test.bloom").toFile();
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
}
