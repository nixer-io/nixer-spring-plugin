package io.nixer.nixerplugin.core.detection.filter.behavior;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Abstraction of any behavior that will be executed as result of rule match.
 * That could include behavior that log request, do nothing, reject request etc.
 */
public interface Behavior {

    void act(HttpServletRequest request, HttpServletResponse response) throws IOException;

    boolean isCommitting();

    String name();

}
