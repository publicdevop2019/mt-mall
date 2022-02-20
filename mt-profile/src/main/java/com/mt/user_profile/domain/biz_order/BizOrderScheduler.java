package com.mt.user_profile.domain.biz_order;

import com.mt.user_profile.application.ApplicationServiceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@EnableScheduling
public class BizOrderScheduler {

    @Scheduled(fixedRateString = "${fixedRate.in.milliseconds.release}", initialDelay = 60 * 1000)
    public void releaseExpiredOrder() {
        ApplicationServiceRegistry.getBizOrderApplicationService().releaseExpiredOrder();
    }

    /**
     * resubmit order in paid_reserved
     */
    @Scheduled(fixedRateString = "${fixedRate.in.milliseconds.resubmit}", initialDelay = 60 * 1000)
    public void concludeOrder() {
        ApplicationServiceRegistry.getBizOrderApplicationService().concludeOrder();
    }

    /**
     * resubmit order in paid_recycled
     */
    @Scheduled(fixedRateString = "${fixedRate.in.milliseconds.resubmit}", initialDelay = 60 * 1000)
    public void resubmitOrder() {
        ApplicationServiceRegistry.getBizOrderApplicationService().resubmitOrder();
    }

}
