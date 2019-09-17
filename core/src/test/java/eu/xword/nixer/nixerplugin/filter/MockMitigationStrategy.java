package eu.xword.nixer.nixerplugin.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.xword.nixer.nixerplugin.filter.strategy.MitigationStrategy;

public class MockMitigationStrategy implements MitigationStrategy {

    private List<HttpServletRequest> requests = new ArrayList<>();

    @Override
    public void act(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        requests.add(request);
    }

    public List<HttpServletRequest> getRequests() {
        return requests;
    }
}
