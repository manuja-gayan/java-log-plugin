package com.plugin.logging.config;


import com.plugin.logging.services.LoggingUtils;
import com.plugin.logging.services.LoginInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingBeenConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoggingBeenConfiguration.class);

	@Bean("loggingUtils")
    public LoginInitializer createLoggingUtil() {
		LoginInitializer initializer= new LoginInitializer();
    	LOGGER.info("**** Generating logging utility been..");
    	return initializer;
    }

}