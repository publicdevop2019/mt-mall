package com.mt;

import com.mt.chaos.ChaosTest;
import com.mt.helper.UserAction;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PreDestroy;

@Slf4j
@SpringBootApplication
@EnableScheduling
public class ChaosTestRunner {

    @Autowired
    UserAction userAction;
    @Autowired
    ChaosTest chaosTest;

    public static void main(String[] args) {
        SpringApplication.run(ChaosTestRunner.class, args);
    }


    @Scheduled(fixedRate = 30 * 1000)
    public void runChaosTest() {
        log.info("Chaos test start");
        userAction.initTestUser();
        chaosTest.testCase1();
        log.info("Chaos test end");
    }

    @PreDestroy
    public void onExit() {
        log.info("Closing application..");
        LogManager.shutdown();
    }
}