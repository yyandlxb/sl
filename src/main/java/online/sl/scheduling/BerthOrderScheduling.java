package online.sl.scheduling;

import lombok.extern.slf4j.Slf4j;
import online.sl.service.BerthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@ConditionalOnProperty("online.sl.berth.enabled")
@Slf4j
public class BerthOrderScheduling {


    private BerthService berthService;

    @Autowired
    public void setBerthService(BerthService berthService) {
        this.berthService = berthService;
    }

    @Scheduled(cron = "*/15 * * * * ?")
    public void predict(){
        berthService.berthOrderUsing();
        berthService.berthOrderAudit();
        // 订单通知
        berthService.overOrderNotice();
    }
}
