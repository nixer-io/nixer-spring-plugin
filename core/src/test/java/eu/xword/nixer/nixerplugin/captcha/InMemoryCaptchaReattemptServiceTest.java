package eu.xword.nixer.nixerplugin.captcha;

import java.time.Duration;
import java.util.ArrayList;

import eu.xword.nixer.nixerplugin.captcha.reattempt.InMemoryCaptchaReattemptService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InMemoryCaptchaReattemptServiceTest {

    InMemoryCaptchaReattemptService underTest;

    @BeforeEach
    public void setup() {
        new ArrayList<String>() {

        };
        underTest = new InMemoryCaptchaReattemptService(4, Duration.ofHours(1), () -> "");
    }

    @Test
    public void should_block_when_number_of_attempts_exceeded() {
        underTest.onFailure();
        underTest.onFailure();
        underTest.onFailure();
        underTest.onFailure();
        underTest.onFailure();

        Assertions.assertTrue(underTest.shouldBlock());
    }

    @Test
    public void should_reset_after_success() {
        underTest.onFailure();
        underTest.onFailure();
        underTest.onFailure();
        underTest.onFailure();
        underTest.onSuccess();

        Assertions.assertFalse(underTest.shouldBlock());
    }

    @Test
    public void should_block_for_duration() throws InterruptedException {
        underTest = new InMemoryCaptchaReattemptService(1, Duration.ofMillis(10), () -> "");

        underTest.onFailure();
        underTest.onFailure();

        Assertions.assertTrue(underTest.shouldBlock());
        Thread.sleep(20);

        Assertions.assertFalse(underTest.shouldBlock());
    }
}