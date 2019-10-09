package eu.xword.nixer.bloom;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;

import com.google.common.base.Stopwatch;
import com.google.common.hash.Funnels;

/**
 * A self-contained benchmark of {@link BloomFilter} performance. It creates a filter in a temporary directory with the given size,
 * inserts random values with number equal to the size, and then checks if inserted values exist and how often non-inserted values
 * are reported as existing (false positives). Results are printed to stdout.
 * <br>
 * Created on 24/08/2018.
 *
 * @author cezary.biernacki@crosswordcybersecurity.com
 */
public class Benchmark {
    private static final long SEED_INSERTION = 20180827L;
    private static final long SEED_NEGATIVE = 20180710L;

    private final long size;
    private final double fpp;
    private final Stopwatch watch = Stopwatch.createUnstarted();

    public Benchmark(final long size, final double fpp) {
        this.size = size;
        this.fpp = fpp;
    }

    public void run() throws IOException {
        final Path directory = Files.createTempDirectory("bloom-benchmark-tmp");
        final Path name = directory.resolve("test.bloom");
        try {
            watch.start();

            final BloomFilter<Long> filter = createFilterForBenchmark(name);

            watch.reset().start();

            benchmarkInsertion(filter);

            watch.reset().start();

            benchmarkChecking(filter);
        } finally {
            Files.deleteIfExists(FileBasedBloomFilter.getDataFilePath(name));
            Files.deleteIfExists(name);
            Files.delete(directory);
        }
    }

    private void benchmarkInsertion(@Nonnull final BloomFilter<Long> filter) {
        message("Inserting", "%d values", size);
        watch.reset().start();

        final Random randomInsertion = new Random(SEED_INSERTION);
        for (long i = 0; i < size; i++) {
            final long l = randomInsertion.nextLong();
            filter.put(l);
        }

        reportTime("Insertion time");
        final double elapsed = ((double) watch.elapsed(TimeUnit.NANOSECONDS)) / TimeUnit.SECONDS.toNanos(1);
        message("Insertion speed", "%6.2f op/sec", (double) size / elapsed);
    }

    private void benchmarkChecking(@Nonnull final BloomFilter<Long> filter) {
        final Random randomInsertion = new Random(SEED_INSERTION);
        final Random randomOther = new Random(SEED_NEGATIVE);

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

        reportTime("Check time");

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
    private BloomFilter<Long> createFilterForBenchmark(@Nonnull final Path name) {
        message("Creating: " + name);
        final BloomFilter<Long> filter = FileBasedBloomFilter.create(
                name,
                Funnels.longFunnel(),
                size,
                fpp
        );

        reportTime("Creation time");

        final BloomFilterParameters parameters = filter.getParameters();
        message("Parameters", "%s", parameters);

        return FileBasedBloomFilter.open(name, Funnels.longFunnel());
    }

    private void reportTime(@Nonnull final String text) {
        message(text, "%s", watch.elapsed());
    }

    private static void message(@Nonnull final String text, @Nonnull final String format, Object value) {
        message(String.format("%-20s: %s", text, String.format(format, value)));
    }

    private static void message(@Nonnull final String m) {
        System.out.println(m);
    }

}
