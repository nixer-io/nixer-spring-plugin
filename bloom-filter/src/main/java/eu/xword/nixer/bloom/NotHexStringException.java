package eu.xword.nixer.bloom;

/**
 * Created on 09/10/2019.
 *
 * @author gcwiak
 */
public class NotHexStringException extends RuntimeException {

    public NotHexStringException(final CharSequence value) {
        super(String.format("(%s) is not a hexadecimal string", value));
    }
}
