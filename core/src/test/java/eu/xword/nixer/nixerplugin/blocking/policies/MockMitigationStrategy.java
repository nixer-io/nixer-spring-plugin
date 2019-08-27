package eu.xword.nixer.nixerplugin.blocking.policies;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MockMitigationStrategy implements MitigationStrategy {

    private List<HttpServletRequest> requests = new ArrayList<>();

    @Override
    public void handle(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        requests.add(request);
    }

    public List<HttpServletRequest> getRequests() {
        return requests;
    }
}
