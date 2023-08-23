package com.plugin.logging.services;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

public class LoggingUtils {

	protected static String appName;
	protected static  String msName;
	protected static  String logLevel;
	protected static  String logRollingPolicy;
	protected static  String logRollingSize;
	protected static  String logRollingPattern;
	protected static  int logRollingMaxHistory;
	protected static  String logPattern;
	private static final String BASE_DIRECTORY = "/var/log/app";
	static RollingFileAppender<ILoggingEvent> logFileAppender = new RollingFileAppender<ILoggingEvent>();
	static ConsoleAppender<ILoggingEvent> consoleFileAppender = new ConsoleAppender<ILoggingEvent>();

	private static final HashMap<String, Logger> LOGGERS = new HashMap<>();
	private static final HashMap<String, Level> LOG_LEVELS = new HashMap<>();
	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(LoggingUtils.class);

	private static final LoggingUtils loggingUtils=new LoggingUtils();

	static {
		LOG_LEVELS.put("DEBUG", Level.DEBUG);
		LOG_LEVELS.put("INFO", Level.INFO);
		LOG_LEVELS.put("ERROR", Level.ERROR);
	}

//	@EventListener
//	public void onRefreshScopeRefreshed(final RefreshScopeRefreshedEvent event) {
//		getClass();
//	}

	public static void initialize() {
		logPattern =(logPattern.equals("def") )? "%d{yyyy-MM-dd HH:mm:ss.SSS}|[%5p] [%15.15t] [%-25.25c{1.}]| %m%n%ex":logPattern;

		//getting host name and IP of service
		String ip = "0.0.0.0";
		String hostName = "";
		InetAddress inetAddress;
		try {
			inetAddress = InetAddress.getLocalHost();
			hostName = inetAddress.getHostName();
			ip = inetAddress.getHostAddress();
		} catch (UnknownHostException ignored) {
			LOGGER.error("Host name cannot be found");
		}

		String logFileName = BASE_DIRECTORY + "/" + msName + "-" + ip;
		LOGGER.info("**** Application log file :" + logFileName + ".log");

		// FileAppender
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		PatternLayoutEncoder ple = new PatternLayoutEncoder();
		ple.setPattern(String.format("%s|%s|%s|%s|application-log|%s|%s|%s", "%d{yyyy-MM-dd HH:mm:ss.SSS}", "[%15.15t]", hostName, "%5p", appName, msName, "%m|%X{traceId:-}|%X{spanId:-}%n%ex"));
		ple.setContext(lc);
		ple.start();
		logFileAppender.setContext(lc);
		logFileAppender.setName("application-log");
		logFileAppender.setEncoder(ple);
		logFileAppender.setAppend(true);
		logFileAppender.setFile(logFileName + ".log");
		logFileAppender.setImmediateFlush(true);

		MDC.put("logAppender", logFileAppender.getName());

		//ConsoleAppender
		LoggerContext consoleLC = (LoggerContext) LoggerFactory.getILoggerFactory();
		PatternLayoutEncoder consolePLE = new PatternLayoutEncoder();
		consolePLE.setPattern(String.format("%s|%s|%s|%s|STDOUT|%s|%s|%s", "%d{yyyy-MM-dd HH:mm:ss.SSS}", "[%15.15t]", hostName, "%5p", appName, msName, "%m|%X{traceId:-}|%X{spanId:-}%n%ex"));
		consolePLE.setContext(consoleLC);
		consolePLE.start();
		consoleFileAppender.setContext(consoleLC);
		consoleFileAppender.setName("STDOUT");
		consoleFileAppender.setEncoder(consolePLE);

		// Setting up rolling policy
		if ("time".equalsIgnoreCase(logRollingPolicy)) {
			@SuppressWarnings("rawtypes")
			TimeBasedRollingPolicy logFilePolicy = new TimeBasedRollingPolicy();
			logFilePolicy.setContext(lc);
			logFilePolicy.setParent(logFileAppender);
			logFilePolicy.setFileNamePattern(logFileName + "-%d{yyyy-MM-dd}.log");
			logFilePolicy.setMaxHistory(logRollingMaxHistory);
			logFilePolicy.start();
			logFileAppender.setRollingPolicy(logFilePolicy);
		}
		else if ("size".equalsIgnoreCase(logRollingPolicy)) {
			SizeBasedTriggeringPolicy<ILoggingEvent> trigPolicy = new SizeBasedTriggeringPolicy<ILoggingEvent>();
			trigPolicy.setMaxFileSize(FileSize.valueOf(logRollingSize) );
			trigPolicy.setContext(lc);
			trigPolicy.start();
			FixedWindowRollingPolicy rollingPolicy = new FixedWindowRollingPolicy();
			rollingPolicy.setContext(lc);
			rollingPolicy.setParent(logFileAppender);
			rollingPolicy.setFileNamePattern(logFileName+ "-%i.log");
			rollingPolicy.setMinIndex(1);
			rollingPolicy.setMaxIndex(logRollingMaxHistory);
			rollingPolicy.start();
			logFileAppender.setTriggeringPolicy(trigPolicy);
			logFileAppender.setRollingPolicy(rollingPolicy);
		}

		//start console and file appender
		logFileAppender.start();
		consoleFileAppender.start();
	}

	public static Logger getLogger(String loggerName) {
		if (LOGGERS.get(loggerName) != null) {
			return LOGGERS.get(loggerName);
		} else {
			Logger logger = (Logger) LoggerFactory.getLogger(loggerName);
			logger.addAppender(logFileAppender);
			logger.addAppender(consoleFileAppender);
			logger.setLevel(LOG_LEVELS.get(logLevel.toUpperCase()));
			logger.setAdditive(false);
			LOGGERS.put(loggerName, logger);
			return logger;
		}
	}
}
