package com.portfolio.mysite.service;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Component
public class GuestbookSpamGuard {

    private static final Duration MIN_FORM_AGE = Duration.ofSeconds(4);
    private static final Duration SUBMIT_COOLDOWN = Duration.ofSeconds(45);
    private static final Pattern URL_PATTERN = Pattern.compile("(?i)(https?://|www\\.|\\.com\\b|\\.net\\b|\\.org\\b|\\.kr\\b)");
    private static final Pattern REPEATED_CHARACTER_PATTERN = Pattern.compile("(.)\\1{12,}");
    private static final String[] BLOCKED_KEYWORDS = {
        "casino", "\uBC14\uCE74\uB77C", "\uD1A0\uD1A0", "loan", "viagra", "porn", "free money", "crypto bonus"
    };

    private final Clock clock;
    private final Map<String, Instant> recentSubmissions = new ConcurrentHashMap<>();

    public GuestbookSpamGuard() {
        this(Clock.systemUTC());
    }

    GuestbookSpamGuard(Clock clock) {
        this.clock = clock;
    }

    public SpamCheckResult check(String clientKey, String honeypot, String submittedAt, String name, String message) {
        if (hasText(honeypot)) {
            return SpamCheckResult.block("hidden-field");
        }

        Instant formRenderedAt = parseSubmittedAt(submittedAt);
        Instant now = Instant.now(clock);
        if (formRenderedAt == null || Duration.between(formRenderedAt, now).compareTo(MIN_FORM_AGE) < 0) {
            return SpamCheckResult.block("too-fast");
        }

        String content = normalize(name) + " " + normalize(message);
        if (countUrls(content) > 1 || containsBlockedKeyword(content) || REPEATED_CHARACTER_PATTERN.matcher(content).find()) {
            return SpamCheckResult.block("spam-content");
        }

        if (hasText(clientKey)) {
            Instant lastSubmittedAt = recentSubmissions.get(clientKey);
            if (lastSubmittedAt != null && Duration.between(lastSubmittedAt, now).compareTo(SUBMIT_COOLDOWN) < 0) {
                return SpamCheckResult.block("rate-limit");
            }
            recentSubmissions.put(clientKey, now);
        }

        return SpamCheckResult.allow();
    }

    private Instant parseSubmittedAt(String submittedAt) {
        if (!hasText(submittedAt)) {
            return null;
        }

        try {
            return Instant.ofEpochMilli(Long.parseLong(submittedAt));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private int countUrls(String content) {
        int count = 0;
        var matcher = URL_PATTERN.matcher(content);
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    private boolean containsBlockedKeyword(String content) {
        for (String keyword : BLOCKED_KEYWORDS) {
            if (content.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String normalize(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT).trim();
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public record SpamCheckResult(boolean allowed, String reason) {
        static SpamCheckResult allow() {
            return new SpamCheckResult(true, "");
        }

        static SpamCheckResult block(String reason) {
            return new SpamCheckResult(false, reason);
        }
    }
}
