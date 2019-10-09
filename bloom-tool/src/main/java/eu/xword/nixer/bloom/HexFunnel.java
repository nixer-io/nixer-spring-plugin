package eu.xword.nixer.bloom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.google.common.base.Preconditions;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;

/**
 * An implementation of {@link Funnel} for strings that tries to interpret incoming strings as hex characters and convert them to bytes.
 *
 * <p>In case of failure (the input string is not valid hex string), it falls back to provided {@link #fallbackFunnel}.
 * <br>
 * Created on 24/08/2018.
 *
 * @author cezary.biernacki@crosswordcybersecurity.com
 */
@NotThreadSafe
public class HexFunnel implements Funnel<CharSequence> {
    private static final long serialVersionUID = -2514607182570735264L;

    private final Funnel<CharSequence> fallbackFunnel;

    private final byte[] buffer = new byte[512];
    private int size;

    public HexFunnel(@Nonnull final Funnel<CharSequence> fallbackFunnel) {
        Preconditions.checkNotNull(fallbackFunnel, "fallbackFunnel");
        this.fallbackFunnel = fallbackFunnel;
    }

    @Override
    public void funnel(@Nonnull final CharSequence from, @Nonnull final PrimitiveSink into) {
        final byte[] decoded = convertHex(from);

        if (decoded != null) {
            into.putBytes(decoded, 0, size);
        } else {
            fallbackFunnel.funnel(from, into);
        }
    }

    @Nullable
    private byte[] convertHex(@Nonnull final CharSequence s) {
        final int len = s.length();

        if ((len & 1) == 1) {
            // invalid length
            return null;
        }

        // avoids allocating byte arrays if they are not too big.
        size = len / 2;
        byte[] result = (size <= buffer.length)
                ? buffer
                : new byte[size];

        int destinationPosition = 0;
        for (int i = 0; i < len; i += 2, ++destinationPosition) {
            assert destinationPosition == i / 2;

            final int digitOne = Character.digit(s.charAt(i), 16);
            final int digitTwo = Character.digit(s.charAt(i + 1), 16);

            if (digitOne < 0 || digitTwo < 0) {
                // invalid characters
                return null;
            }

            result[destinationPosition] = (byte) ((digitOne << 4) + digitTwo);
        }

        return result;
    }
}
