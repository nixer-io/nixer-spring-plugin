package eu.xword.nixer.nixerplugin.ip;

import javax.servlet.http.HttpServletRequest;

public interface IpResolver {

    String resolve(HttpServletRequest request);
}
