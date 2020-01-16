package io.nixer.nixerplugin.stigma.token;


import com.nimbusds.jwt.JWT;
import io.nixer.nixerplugin.stigma.domain.Stigma;

/**
 * Created on 2019-05-20.
 *
 * @author gcwiak
 */
public interface StigmaTokenProvider {

    JWT getToken(Stigma stigma);
}
