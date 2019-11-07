package io.nixer.nixerplugin.core.detection.filter;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static io.nixer.nixerplugin.core.detection.filter.RequestMetadata.GLOBAL_CREDENTIAL_STUFFING;
import static org.assertj.core.api.Assertions.assertThat;

class RequestMetadataWrapperTest {

    private final MockHttpServletRequest request = new MockHttpServletRequest("POST", "login");

    @Test
    void shouldReturnOnlyNixerAttributes() {
        request.setAttribute("spring.attribute", "something");
        request.setAttribute(RequestMetadata.GLOBAL_CREDENTIAL_STUFFING, true);

        final RequestMetadataWrapper wrapper = new RequestMetadataWrapper(request);

        assertThat(wrapper.getMetadataAttributes())
                .containsEntry(RequestMetadata.GLOBAL_CREDENTIAL_STUFFING, true)
                .hasSize(1);
    }

    @Test
    void shouldReturnEmptyCollectionIfAttributesMissing() {
        final RequestMetadataWrapper wrapper = new RequestMetadataWrapper(request);

        assertThat(wrapper.getMetadataAttributes()).isEmpty();
    }
}
