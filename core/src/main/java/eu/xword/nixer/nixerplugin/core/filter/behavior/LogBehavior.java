package eu.xword.nixer.nixerplugin.core.filter.behavior;

import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import eu.xword.nixer.nixerplugin.core.filter.RequestMetadataWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.StringUtils;

import static eu.xword.nixer.nixerplugin.core.filter.behavior.Behaviors.LOG;

/**
 * This behaviors performs logging of request. By default, only the URI of the request is logged.
 * What parts of request will be logged could be configured with setters.
 *  <p>
 *  <p>{@code setIncludeHeaders(boolean)}
 *  <p>{@code setIncludeMetadata(boolean)}
 *  <p>{@code setIncludeQueryString(boolean)}
 *  <p>{@code setIncludeUserInfo(boolean)}
 */
public class LogBehavior implements Behavior {

    private final Log logger = LogFactory.getLog(getClass());

    private boolean includeHeaders = false;
    private boolean includeUserInfo = false;
    private boolean includeQueryString = false;
    private boolean includeMetadata = false;

    //todo consider whether http tracking would be replacement or addition to it

    @Override
    public void act(final HttpServletRequest request, final HttpServletResponse response) {
        if (logger.isInfoEnabled()) {
            logger.info(createMessage(request));
        }
    }

    protected String createMessage(HttpServletRequest request) {
        return new StringBuilder()
                .append("RQ=")
                .append(requestMessage(request))
                .toString();
    }

    private String getMetadata(final HttpServletRequest request) {
        final Map<String, Object> metadataAttributes = new RequestMetadataWrapper(request).getMetadataAttributes();

        return metadataAttributes.keySet().stream()
                .map(name -> name + "=" + metadataAttributes.get(name))
                .collect(Collectors.joining(", ", "[", "]"));
    }

    private StringBuilder requestMessage(final HttpServletRequest request) {
        final StringBuilder rqMsg = new StringBuilder();
        rqMsg.append("uri=").append(request.getRequestURI());

        if (isIncludeQueryString()) {
            String queryString = request.getQueryString();
            if (queryString != null) {
                rqMsg.append('?').append(queryString);
            }
        }

        if (isIncludeUserInfo()) {
            String client = request.getRemoteAddr();
            if (StringUtils.hasLength(client)) {
                rqMsg.append(";client=").append(client);
            }
            HttpSession session = request.getSession(false);
            if (session != null) {
                rqMsg.append(";session=").append(session.getId());
            }
            String user = request.getRemoteUser();
            if (user != null) {
                rqMsg.append(";user=").append(user);
            }
        }

        if (isIncludeHeaders()) {
            rqMsg.append(";headers=").append(new ServletServerHttpRequest(request).getHeaders());
        }

        if (isIncludeMetadata()) {
            rqMsg.append(";attributes=").append(getMetadata(request));
        }
        return rqMsg;
    }

    public boolean isIncludeMetadata() {
        return includeMetadata;
    }

    public void setIncludeMetadata(final boolean includeMetadata) {
        this.includeMetadata = includeMetadata;
    }

    public boolean isIncludeHeaders() {
        return includeHeaders;
    }

    public void setIncludeHeaders(final boolean includeHeaders) {
        this.includeHeaders = includeHeaders;
    }

    public boolean isIncludeUserInfo() {
        return includeUserInfo;
    }

    public void setIncludeUserInfo(final boolean includeUserInfo) {
        this.includeUserInfo = includeUserInfo;
    }

    public boolean isIncludeQueryString() {
        return includeQueryString;
    }

    public void setIncludeQueryString(final boolean includeQueryString) {
        this.includeQueryString = includeQueryString;
    }

    @Override
    public boolean isCommitting() {
        return false;
    }

    @Override
    public String name() {
        return LOG.name();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
