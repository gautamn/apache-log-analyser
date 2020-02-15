package com.nayaka.loganalyser.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class LogEntry {
	
	private String ipAddress;
	private String requestUrl;
	private String responseCode;
	private int bytesSend;
	private int responseTime;
}
