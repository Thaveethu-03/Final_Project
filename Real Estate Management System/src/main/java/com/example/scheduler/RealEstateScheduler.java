package com.example.scheduler;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.dto.LeaseResponseDTO;
import com.example.service.LeaseService;
import com.example.service.RentPaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Background jobs. Cron format: second minute hour day-of-month month day-of-week.
 * Every cron can be changed from application.properties (see application-additions.properties).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RealEstateScheduler 
{

    private static final int REMINDER_DAYS = 30;

    private final LeaseService leaseService;
    private final RentPaymentService rentPaymentService;

    /** Every day 01:00 - ACTIVE leases whose end date is over become EXPIRED, property becomes AVAILABLE. */
    @Scheduled(cron = "${app.scheduler.expire-leases-cron:0 0 1 * * *}", zone = "Asia/Kolkata")
    public void expireOverdueLeases() 
    {
        try {
            int count = leaseService.expireOverdueLeases();
            log.info("[Scheduler] Lease expiry job finished. Expired leases: {}", count);
        } 
        
        catch (Exception ex) 
        {
            log.error("[Scheduler] Lease expiry job failed", ex);
        }
    }

    /** Every day 01:30 - PENDING rent payments whose due date is over become OVERDUE. */
    @Scheduled(cron = "${app.scheduler.mark-overdue-cron:0 30 1 * * *}", zone = "Asia/Kolkata")
    public void markOverduePayments() 
    {
        try 
        {
            int count = rentPaymentService.markOverduePayments();
            log.info("[Scheduler] Overdue payment job finished. Payments marked OVERDUE: {}", count);
        } 
        
        catch (Exception ex) 
        {
            log.error("[Scheduler] Overdue payment job failed", ex);
        }
    }

    /** Every day 09:00 - logs the leases that will end within the next 30 days (reminder). */
    @Scheduled(cron = "${app.scheduler.expiry-reminder-cron:0 0 9 * * *}", zone = "Asia/Kolkata")
    public void logLeasesExpiringSoon() 
    {
        try 
        {
            List<LeaseResponseDTO> leases = leaseService.getExpiringLeases(REMINDER_DAYS);
            if (leases.isEmpty()) 
            
            {
                log.info("[Scheduler] No lease is ending in the next {} days", REMINDER_DAYS);
                return;
            }
            for (LeaseResponseDTO lease : leases) 
            {
                log.warn("[Scheduler] REMINDER: lease {} of tenant '{}' for property '{}' ends on {}",
                        lease.getId(), lease.getTenantName(), lease.getPropertyTitle(), lease.getEndDate());
            }
        } catch (Exception ex) 
        {
            log.error("[Scheduler] Lease reminder job failed", ex);
        }
    }
}
