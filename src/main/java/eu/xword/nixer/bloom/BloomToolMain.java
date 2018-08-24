package eu.xword.nixer.bloom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Map;
import javax.annotation.Nonnull;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.hash.Funnel;
import com.google.common.hash.Funnels;
import org.docopt.Docopt;

/**
 * The entry class for a command utility to manipulate file-based bloom filters. See {@code bloom-docopt.txt} for usage information.
 * <br>
 * Created on 23/08/2018.
 *
 * @author cezary.biernacki@crosswordcybersecurity.com
 */
public class BloomToolMain {

    @Nonnull
    private final Map<String, Object> parsed;

    public BloomToolMain(@Nonnull final Map<String, Object> parsed) {
        Preconditions.checkNotNull(parsed, "parsed");
        this.parsed = parsed;
    }

    public static void main(final String argv[]) throws IOException {

        final InputStream resourceAsStream = BloomToolMain.class.getResourceAsStream("bloom-docopt.txt");
        final Docopt docopt = new Docopt(resourceAsStream);

        final Map<String, Object> parsed = docopt.parse(argv);
        new BloomToolMain(parsed).handle();
    }

    private void handle() throws IOException {
        if (isSet("create")) {
            handleCreate();
            return;
        }

        if (isSet("insert")) {
            handleInsert();
            return;
        }

        if (isSet("check")) {
            handleCheck();
            return;
        }

        if (isSet("benchmark")) {
            handleBenchmark();
            return;
        }

        throw new IllegalArgumentException("Failed to understand arguments: " + parsed);
    }

    private void handleCreate() {
        final String name = (String) extract("NAME");
        final long size = extractSize();
        final double fpp = extractFpp();


        FileBasedBloomFilter.create(
                Paths.get(name),
                getFunnel(),
                size,
                fpp
        );
    }


    private void handleInsert() throws IOException {
        final String name = (String) extract("NAME");
        final BloomFilter<CharSequence> filter = FileBasedBloomFilter.open(
                Paths.get(name),
                getFunnel()
        );

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, Charsets.UTF_8.newDecoder()))) {
            reader.lines().forEach(filter::put);
        }
    }

    private void handleCheck() throws IOException {
        final String name = (String) extract("NAME");
        final BloomFilter<CharSequence> filter = FileBasedBloomFilter.open(
                Paths.get(name),
                getFunnel()
        );

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, Charsets.UTF_8.newDecoder()))) {
            reader.lines().filter(filter::mightContain).forEach(System.out::println);
        }
    }

    private void handleBenchmark() throws IOException {
        final long size = extractSize();
        final double fpp = extractFpp();
        new Benchmark(size, fpp).run();
    }

    @Nonnull
    private Funnel<CharSequence> getFunnel() {
        final Funnel<CharSequence> defaultFunnel = Funnels.unencodedCharsFunnel();
        return isSet("--hex")
                ? new HexFunnel(defaultFunnel)
                : defaultFunnel;
    }

    private long extractSize() {
        final String key = "--size";
        final long result = extractLong(key);
        Preconditions.checkArgument(result > 0, "Parameter '%s' should be bigger then 0", key);
        return result;
    }

    private double extractFpp() {
        final String key = "--fpp";
        final double result = extractDouble(key);
        Preconditions.checkArgument(result > 0 && result < 1, "Parameter '%s' should be in range (0, 1) /exclusive/", key);
        return result;
    }

    private boolean isSet(final String key) {
        return Boolean.TRUE.equals(extract(key));
    }

    private long extractLong(@Nonnull final String key) {
        try {
            return Long.parseLong((String) extract(key));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format("Parameter '%s' should be an integer number", key));
        }
    }

    private Object extract(@Nonnull final String key) {
        return Preconditions.checkNotNull(parsed.get(key), "Missing parameter '%s'", key);
    }

    private double extractDouble(@Nonnull final String key) {
        try {
            return Double.parseDouble((String) extract(key));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format("Parameter '%s' should be a floating point number", key));
        }
    }

}
