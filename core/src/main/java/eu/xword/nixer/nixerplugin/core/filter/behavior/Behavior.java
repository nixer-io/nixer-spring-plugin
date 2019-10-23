package eu.xword.nixer.nixerplugin.core.filter.behavior;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Behavior {

    void act(HttpServletRequest request, HttpServletResponse response) throws IOException;

    boolean isCommitting();

    String name();

}
