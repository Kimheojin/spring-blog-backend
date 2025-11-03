package HeoJin.demoBlog.seo.SyncScheuler;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PostScheduler {

    @Scheduled(cron = "0 0 4 * * ?")// 매일 새벽 4시 동기화
    public void runTaskAt4AM(){

    }
}
