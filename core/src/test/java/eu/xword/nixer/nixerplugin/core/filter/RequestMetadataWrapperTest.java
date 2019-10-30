package eu.xword.nixer.nixerplugin.core.filter;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static eu.xword.nixer.nixerplugin.core.filter.RequestAugmentation.GLOBAL_CREDENTIAL_STUFFING;
import static org.assertj.core.api.Assertions.assertThat;

class RequestMetadataWrapperTest {

    private final MockHttpServletRequest request = new MockHttpServletRequest("POST", "login");

    @Test
    void shouldReturnOnlyNixerAttributes() {
        request.setAttribute("spring.attribute", "something");
        request.setAttribute(GLOBAL_CREDENTIAL_STUFFING, true);

        final RequestMetadataWrapper wrapper = new RequestMetadataWrapper(request);

        assertThat(wrapper.getMetadataAttributes())
                .containsEntry(GLOBAL_CREDENTIAL_STUFFING, true)
                .hasSize(1);
    }

    @Test
    void shouldReturnEmptyCollectionIfAttributesMissing() {
        final RequestMetadataWrapper wrapper = new RequestMetadataWrapper(request);

        assertThat(wrapper.getMetadataAttributes()).isEmpty();
    }
}