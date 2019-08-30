package eu.xword.nixer.nixerplugin.captcha.recaptcha;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

/**
 * Describes Google Recaptcha verification response
 * @see <a href="https://developers.google.com/recaptcha/docs/verify">Google Recaptcha Docs</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
        "success",
        "challenge_ts",
        "hostname",
        "error-codes"
})
public class RecaptchaVerifyResponse {

    private boolean success;

    private String challengeTs;

    private String hostname;

    private List<ErrorCode> errorCodes;

    @JsonCreator
    public RecaptchaVerifyResponse(@JsonProperty("success") final boolean success,
                                   @JsonProperty("challenge_ts") final String challengeTs,
                                   @JsonProperty("hostname") final String hostname,
                                   @JsonProperty("error-codes") final List<ErrorCode> errorCodes) {
        this.success = success;
        this.challengeTs = challengeTs;
        this.hostname = hostname;
        this.errorCodes = errorCodes != null ? ImmutableList.copyOf(errorCodes) : ImmutableList.of();
    }

    @JsonIgnore
    public boolean hasClientError() {
        for (ErrorCode error : getErrorCodes()) {
            switch (error) {
                case InvalidResponse:
                case MissingResponse:
                    return true;
            }
        }
        return false;
    }

    public enum ErrorCode {
        MissingSecret,
        InvalidSecret,
        MissingResponse,
        InvalidResponse,
        BadRequest,
        Timeout;

        private static Map<String, ErrorCode> errorsMap = new HashMap<>(4);

        static {
            errorsMap.put("missing-input-secret", MissingSecret);
            errorsMap.put("invalid-input-secret", InvalidSecret);
            errorsMap.put("missing-input-response", MissingResponse);
            errorsMap.put("invalid-input-response", InvalidResponse);
            errorsMap.put("bad-request", BadRequest);
            errorsMap.put("timeout-or-duplicate", Timeout);
        }

        @JsonCreator
        public static ErrorCode forValue(String value) {
            return errorsMap.get(value.toLowerCase());
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public String getChallengeTs() {
        return challengeTs;
    }

    public String getHostname() {
        return hostname;
    }

    public List<ErrorCode> getErrorCodes() {
        return errorCodes;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RecaptchaVerifyResponse that = (RecaptchaVerifyResponse) o;
        return success == that.success &&
                Objects.equal(challengeTs, that.challengeTs) &&
                Objects.equal(hostname, that.hostname) &&
                Objects.equal(errorCodes, that.errorCodes);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(success, challengeTs, hostname, errorCodes);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("success", success)
                .add("challengeTs", challengeTs)
                .add("hostname", hostname)
                .add("errorCodes", errorCodes)
                .toString();
    }
}
