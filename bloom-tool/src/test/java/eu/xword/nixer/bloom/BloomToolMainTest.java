package eu.xword.nixer.bloom;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link BloomToolMain}.
 * <br>
 * Created on 23/08/2018.
 *
 * @author cezary
 */
public class BloomToolMainTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void createFilter() throws IOException {
        // given
        final File file = temporaryFolder.newFile("test.bloom");
        delete(file);

        // when
        BloomToolMain.main(new String[]{"create", "--size=100", "--fpp=1e-2", file.getAbsolutePath()});

        // then
        assertThat(file).exists();
    }

    private static void delete(final File file) throws IOException {
        if (!file.delete()) {
            throw new IOException("Failed to delete: " + file);
        }
    }
}