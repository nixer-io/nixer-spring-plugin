package eu.xword.nixer.nixerplugin.filter.strategy;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface MitigationStrategy {
    void handle(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
