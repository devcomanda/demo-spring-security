package com.devcomanda.demospringsecurity.services.schedules;


import com.devcomanda.demospringsecurity.services.UserSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * @author Danil Kuznetsov (kuznetsov.danil.v@gmail.com)
 */
@Service
public class UserSecuritySchedule {

    private final UserSecurityService securityService;

    @Autowired
    public UserSecuritySchedule(final UserSecurityService securityService) {
        this.securityService = securityService;
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        final long beforeDays = 3L;
        final Instant date = Instant.now().minus(beforeDays, ChronoUnit.DAYS);
        this.securityService.removeNotActivatedUsersBefore(date);
    }
}
