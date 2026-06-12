package com.portfolio.mysite.service;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class GuestbookSpamGuardTest {

    private static final Instant NOW = Instant.parse("2026-06-12T00:00:00Z");

    private final GuestbookSpamGuard spamGuard = new GuestbookSpamGuard(Clock.fixed(NOW, ZoneOffset.UTC));

    @Test
    void allowsNormalMessageAfterMinimumFormAge() {
        var result = spamGuard.check("127.0.0.1", "", submittedAt(10), "hyein", "Thanks for the thoughtful portfolio.");

        assertThat(result.allowed()).isTrue();
    }

    @Test
    void blocksHoneypotSubmission() {
        var result = spamGuard.check("127.0.0.1", "spam-site", submittedAt(10), "bot", "hello");

        assertThat(result.allowed()).isFalse();
        assertThat(result.reason()).isEqualTo("hidden-field");
    }

    @Test
    void blocksTooFastSubmission() {
        var result = spamGuard.check("127.0.0.1", "", submittedAt(1), "bot", "hello");

        assertThat(result.allowed()).isFalse();
        assertThat(result.reason()).isEqualTo("too-fast");
    }

    @Test
    void blocksRepeatedSubmissionsFromSameClient() {
        spamGuard.check("127.0.0.1", "", submittedAt(10), "hyein", "First guestbook message.");

        var result = spamGuard.check("127.0.0.1", "", submittedAt(10), "hyein", "Second guestbook message.");

        assertThat(result.allowed()).isFalse();
        assertThat(result.reason()).isEqualTo("rate-limit");
    }

    @Test
    void blocksMessagesWithSeveralLinks() {
        var result = spamGuard.check("127.0.0.1", "", submittedAt(10), "bot", "visit https://a.com and www.b.net");

        assertThat(result.allowed()).isFalse();
        assertThat(result.reason()).isEqualTo("spam-content");
    }

    private String submittedAt(long secondsAgo) {
        return String.valueOf(NOW.minusSeconds(secondsAgo).toEpochMilli());
    }
}
