package com.plugin.logging.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;

public class LoginInitializer {
    @Value("${plugin.log.app.name:application}")
    private String appName;
    @Value("${plugin.log.app.msName:microService}")
    private String msName;
    @Value("${plugin.log.level:DEBUG}")
    private String logLevel;
    @Value("${plugin.log.rolling.policy:time}")
    private String logRollingPolicy;
    @Value("${plugin.log.rolling.size:10MB}")
    private String logRollingSize;
    @Value("${plugin.log.rolling.pattern:yyyy-MM-dd}")
    private String logRollingPattern;
    @Value("${plugin.log.rolling.max-history:30}")
    private int logRollingMaxHistory;
    @Value("${plugin.log.pattern:def}")
    public String logPattern;
    private static Logger logger = LoggerFactory.getLogger(LoginInitializer.class);
    @PostConstruct
    public void initialize() {
        logger.info("Setting static attributes :" + this.appName + "," + this.msName + "," + this.logLevel + "," + this.logRollingPolicy + "," + this.logRollingSize + "," + this.logRollingPattern + "," + this.logRollingMaxHistory + "," + this.logPattern);
        LoggingUtils.appName = this.appName;
        LoggingUtils.msName = this.msName;
        LoggingUtils.logLevel = this.logLevel;
        LoggingUtils.logRollingPolicy = this.logRollingPolicy;
        LoggingUtils.logRollingSize = this.logRollingSize;
        LoggingUtils.logRollingPattern = this.logRollingPattern;
        LoggingUtils.logRollingMaxHistory = this.logRollingMaxHistory;
        LoggingUtils.logPattern = this.logPattern;
        logger.info("Initializing LoggingUtils");
        LoggingUtils.initialize();
    }
}
