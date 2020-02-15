package com.nayaka.loganalyser;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.andor.core.util.SpringApplicationContext;
import com.andor.misc.thumbnails.LogAnalyser;
import com.nayaka.loganalyser.enums.UTILITY;

@SpringBootApplication
@Configuration
@ComponentScan(basePackages = {"com.andor.core","com.andor.misc"})
@EnableAutoConfiguration(exclude={BatchAutoConfiguration.class})
@PropertySource(value = { "file:/opt/andor/properties/application.properties" })
public class AndorMiscApplication {
	
	private static final Logger logger = LoggerFactory.getLogger(AndorMiscApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(AndorMiscApplication.class, args);
		if(args.length==0){
			logger.debug("AndorMiscApplication.main()==============>"+args.length);
			logger.debug("No utility name provided in the given command. Usage: java - jar andor-misc-1.0-exec.jar <UTILITY NAME> [PARAMS]");
			logger.debug("System exiting...");
			System.exit(0);
		}else{
			logger.debug("PROVIDED JOB NAME="+args[0]);
			
			if(UTILITY.ANALYSE_LOGS.toString().equalsIgnoreCase(args[0])) {
				if(args[1]==null) {
					logger.debug("Apache log file path missing.");
					logger.debug("No utility name provided in the given command. Usage: java - jar andor-misc-1.0-exec.jar ANALYSE_LOGS <apache log file path>");
				}
				try {
					SpringApplicationContext.getBean(LogAnalyser.class).analyseLogs(args[1]);
				} catch (IOException e) {
					logger.error("Error occured while reading log file.", e);
				}
			}
			
		}
		
		System.exit(0);
	}
}
