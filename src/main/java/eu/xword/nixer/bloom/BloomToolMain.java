package eu.xword.nixer.bloom;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.hash.Funnels;
import org.docopt.Docopt;

/**
 * TODO
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
        final BloomFilter<CharSequence> ignored = FileBasedBloomFilter.create(
                Paths.get(name),
                Funnels.stringFunnel(Charsets.UTF_8),
                size,
                fpp
        );
    }

    private void handleBenchmark() throws IOException {
        final long size = extractSize();
        final double fpp = extractFpp();
        final Path directory = Files.createTempDirectory("bloom-benchmark-tmp");
        final Path name = directory.resolve("test.bloom");
        try {
            final Stopwatch watch = Stopwatch.createStarted();

            final BloomFilter<Long> filter = createFilterForBenchmark(size, fpp, name, watch);

            final long seedInsertion = 20180827l;
            final long seedOther = 20180710l;

            watch.reset().start();

            benchmarkInsertion(size, watch, filter, seedInsertion);

            watch.reset().start();

            benchmarkChecking(size, watch, filter, seedInsertion, seedOther);


        } finally {
            Files.deleteIfExists(FileBasedBloomFilter.getDataFilePath(name));
            Files.deleteIfExists(name);
            Files.delete(directory);
        }
    }

    private static void benchmarkInsertion(final long size, final Stopwatch watch, final BloomFilter<Long> filter, final long seedInsertion) {
        message("Inserting", "%d values", size);
        watch.reset().start();
        insertRandomElements(filter, size, seedInsertion);
        reportTime(watch, "Insertion time");
        final double elapsed = ((double) watch.elapsed(TimeUnit.NANOSECONDS)) / TimeUnit.SECONDS.toNanos(1);
        message("Insertion speed", "%6.2f op/sec", (double) size / elapsed);
    }

    private static void benchmarkChecking(final long size, final Stopwatch watch, final BloomFilter<Long> filter, final long seedInsertion, final long seedOther) {
        final Random randomInsertion = new Random(seedInsertion);
        final Random randomOther = new Random(seedOther);

        long falsePositives = 0;

        message("Checking", "%d values (half exist, half should not exist)", 2 * size);

        for (long i = 0; i < size; i++) {
            final long mustExistValue = randomInsertion.nextLong();
            if (!filter.mightContain(mustExistValue)) {
                message("Error - missing expected value: " + mustExistValue);
            }
            final long probablyShouldNotExist = randomOther.nextLong();
            if (filter.mightContain(probablyShouldNotExist * 1001)) {
                ++falsePositives;
            }
        }

        reportTime(watch, "Check time");

        final double elapsed = ((double) watch.elapsed().toNanos()) / TimeUnit.SECONDS.toNanos(1);

        message("Check speed", "%6.2f op/sec", 2.0 * (double) size / elapsed);
        message("False Positives", "%d", falsePositives);
        final double falsePositivesRate = (double) falsePositives / (double) size;
        message("False Positive rate", "%16.14f", falsePositivesRate);
        final double expectedFpp = filter.expectedFpp();
        message("FP rate evaluation", "%s", evaluateFalsePositives(falsePositivesRate, expectedFpp));
    }

    @Nonnull
    private static String evaluateFalsePositives(final double falsePositivesRate, final double expectedFpp) {
        if (expectedFpp <= 0.0) {
            return "invalid expected fpp";
        }

        final String grade = (falsePositivesRate <= expectedFpp * 1.5)
                ? "good"
                : (falsePositivesRate  <= expectedFpp * 2.0 ? "tolerable" : "bad");

        final String comment = (falsePositivesRate == 0)
                ? "no detected"
                : String.format("%.2f%% target", falsePositivesRate * 100.0 / expectedFpp);

        return String.format("%s (%s)", grade, comment);
    }


    @Nonnull
    private static BloomFilter<Long> createFilterForBenchmark(final long size, final double fpp, final Path name, final Stopwatch watch) {
        message("Creating: " + name);
        final BloomFilter<Long> filter = FileBasedBloomFilter.create(
                name,
                Funnels.longFunnel(),
                size,
                fpp
        );

        reportTime(watch, "Creation time");

        final BloomFilterParameters parameters = filter.getParameters();
        message("Parameters", "%s", parameters);

        return FileBasedBloomFilter.open(name, Funnels.longFunnel());
    }

    private static void insertRandomElements(final BloomFilter<Long> filter, final long size, final long seedInsertion) {
        final Random randomInsertion = new Random(seedInsertion);
        for (long i = 0; i < size; i++) {
            final long l = randomInsertion.nextLong();
            filter.put(l);
        }
    }

    private static void reportTime(@Nonnull final Stopwatch watch, @Nonnull final String text) {
        message(text, "%s", watch.elapsed());
    }

    private static void message(final String text, String format, Object value) {
        message(String.format("%-20s: %s", text, String.format(format, value)));
    }

    private static void message(final String m) {
        System.out.println(m);
    }


    private void handleInsert() {
    }

    private void handleCheck() {
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
