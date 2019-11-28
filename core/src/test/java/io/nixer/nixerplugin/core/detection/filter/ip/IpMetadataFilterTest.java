package io.nixer.nixerplugin.core.detection.filter.ip;

import io.nixer.nixerplugin.core.detection.filter.RequestMetadata;
import io.nixer.nixerplugin.core.domain.ip.IpLookup;
import io.nixer.nixerplugin.core.domain.ip.net.IpAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IpMetadataFilterTest {

    private static final String IP = "1.2.3.4";
    private static final String INVALID_IP = "example.com";

    @Mock
    private final IpLookup ipLookup = mock(IpLookup.class);

    private IpMetadataFilter filter;
    private MockHttpServletRequest request;

    @BeforeEach
    void setup() {
        filter = new IpMetadataFilter(ipLookup);
        request = new MockHttpServletRequest("GET", "/login");
    }

    @Test
    void shouldSetIpMetadataOnRequest() {
        when(ipLookup.lookup(IP))
                .thenReturn(IpAddress.fromIp(IP));

        request.setRemoteAddr(IP);

        filter.apply(request);

        assertEquals(new IpMetadata(true), request.getAttribute(RequestMetadata.IP_METADATA));
    }

    @Test
    void shouldNotSetIpMetadataOnRequest() {
        when(ipLookup.lookup(IP))
                .thenReturn(null);

        request.setRemoteAddr(IP);

        filter.apply(request);

        assertNull(request.getAttribute(RequestMetadata.IP_METADATA));
    }

    @Test
    void shouldSkipIpLookupForInvalidIp() {
        request.setRemoteAddr(INVALID_IP);

        filter.apply(request);

        verifyZeroInteractions(ipLookup);
    }

}