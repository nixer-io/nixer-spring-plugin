package io.nixer.bloom;

import java.util.Arrays;

import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Unit tests for {@link HexFunnel}.
 * <br>
 * Created on 24/08/2018.
 *
 * @author cezary
 */
@RunWith(JUnitParamsRunner.class)
public class HexFunnelTest {

    @Mock
    private Funnel<CharSequence> fallback;

    @Mock
    private PrimitiveSink sink;

    private HexFunnel funnel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        funnel = new HexFunnel(fallback);
    }

    @Test
    public void shouldConvertHexString() {
        // when
        funnel.funnel("01AbcD", sink);

        // then
        final ArgumentCaptor<byte[]> argumentCaptor = ArgumentCaptor.forClass(byte[].class);
        verify(sink).putBytes(argumentCaptor.capture(), eq(0), eq(3));

        assertThat(argumentCaptor.getValue()).startsWith(1, 0xab, 0xcd);
        verifyNoMoreInteractions(sink);
    }

    @Test
    public void shouldConvertEmptyString() {
        // when
        funnel.funnel("", sink);

        // then
        final ArgumentCaptor<byte[]> argumentCaptor = ArgumentCaptor.forClass(byte[].class);
        verify(sink).putBytes(argumentCaptor.capture(), eq(0), eq(0));
        verifyNoMoreInteractions(sink);
    }

    @Test
    public void shouldConvertHexStringMultipleTimes() {
        // when
        funnel.funnel("FEDCBA01020304", sink);

        // then
        final ArgumentCaptor<byte[]> argumentCaptor = ArgumentCaptor.forClass(byte[].class);
        verify(sink).putBytes(argumentCaptor.capture(), eq(0), eq(7));
        final byte[] valueOne = Arrays.copyOf(argumentCaptor.getValue(), 7);
        assertThat(valueOne).startsWith(0xfe, 0xdc, 0xba, 1, 2, 3, 4);

        // given
        funnel.funnel("01AbcD", sink);

        // then
        verify(sink).putBytes(argumentCaptor.capture(), eq(0), eq(3));
        final byte[] valueTwo = Arrays.copyOf(argumentCaptor.getValue(), 3);

        assertThat(valueTwo).startsWith(1, 0xab, 0xcd);
        verifyNoMoreInteractions(sink);
    }

    @Test
    public void shouldFallbackOnNonEvenLengthString() {
        // when
        funnel.funnel("01AbcD0", sink);

        // then
        verify(fallback).funnel("01AbcD0", sink);

        verifyNoMoreInteractions(fallback);
    }

    @Test
    @Parameters({"foo", "123G", "-1 ", "11  22"})
    public void shouldFallbackOnNonHexString(final String example) {
        // when
        funnel.funnel(example, sink);

        // then
        verify(fallback).funnel(example, sink);

        verifyNoMoreInteractions(fallback);
    }

    @Test
    @Parameters({"*12*", "11*12", "**"})
    public void shouldFallbackOnStringWithSpaces(final String exampleTemplate) {
        final String example = exampleTemplate.replace('*', ' ');
        // when
        funnel.funnel(example, sink);

        // then
        verify(fallback).funnel(example, sink);

        verifyNoMoreInteractions(fallback);
    }

    @Test
    public void should_not_allow_processing_non_hex_values_when_no_fallback_provided() {
        // given hex-only funnel
        funnel = new HexFunnel();

        // when
        final Throwable throwable = catchThrowable(() -> funnel.funnel("not-a-hex-value", sink));

        // then
        assertThat(throwable).isInstanceOf(NotHexStringException.class);
    }
}
