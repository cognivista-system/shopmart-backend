package com.shopmart.common.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/** Default SMS channel: logs instead of sending. Active unless app.sms.enabled=true. */
@Slf4j
@Component
@ConditionalOnProperty(name = "app.sms.enabled", havingValue = "false", matchIfMissing = true)
public class LoggingSmsSender implements SmsSender {
    @Override
    public void send(String toPhone, String message) {
        log.info("[SMS:log] to={} message={}", toPhone, message);
    }
}
