package io.nixer.nixerplugin.core.detection.filter;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

import org.springframework.util.Assert;

/**
 * Provides helper methods for accessing
 */
public class RequestMetadataWrapper {

    private static final String NIXER_METADATA_PREFIX = "nixer";
    private final HttpServletRequest request;

    public RequestMetadataWrapper(final HttpServletRequest request) {
        Assert.notNull(request, "HttpServletRequest must not be null");
        this.request = request;
    }

    public Map<String, Object> getMetadataAttributes() {
        return Collections.list(request.getAttributeNames())
                .stream()
                .filter(this::isMetadataAttribute)
                .collect(Collectors.toMap(name -> name, request::getAttribute));
    }

    private boolean isMetadataAttribute(final String name) {
        return name.startsWith(NIXER_METADATA_PREFIX);
    }
}
