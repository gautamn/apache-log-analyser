package com.nayaka.loganalyser.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.nayaka.loganalyser.bean.LogEntry;

@Service
public class LogAnalyser {
	
	private static final Logger logger = LoggerFactory.getLogger(LogAnalyser.class);
	
	public void analyseLogs(String logFilePath) throws FileNotFoundException, IOException {
		File file = new File(logFilePath);
		List<LogEntry> logEntries = new ArrayList<>();
		try(BufferedReader br = new BufferedReader(new FileReader(file))) {
		    for(String line; (line = br.readLine()) != null; ) {
		        // process the line.
		    	LogEntry logEntry = processLogEntry(line);
		    	if(logEntry!=null) logEntries.add(logEntry);
		    }
		}
		
		/*Find most Time Consuming APIs*/
		Map<String, Integer> apiFreqMap = mostTimeConsumingAPIs(logEntries);
		
		/*most frequent apis*/
		findMostFreqentAPIs(apiFreqMap);
		
		/*api response status codes*/
		findApisResponseStatus(logEntries);
	}
	
	
	private void findApisResponseStatus(List<LogEntry> logEntries) {
		Map<String, Integer> responseStatusCountMap = new HashMap<>();
		for (LogEntry logEntry : logEntries) {
			int statusCount = 1;
			if(responseStatusCountMap.containsKey(logEntry.getResponseCode())) {
				statusCount = statusCount + responseStatusCountMap.get(logEntry.getResponseCode());
			}
			responseStatusCountMap.put(logEntry.getResponseCode(), statusCount);
		}
		
		Map<String, Integer> sortedResponseStatusCountMap = sortByValue(responseStatusCountMap);
		logger.info("response_code, count");
		for (Map.Entry<String, Integer> entry : sortedResponseStatusCountMap.entrySet()) {
			logger.debug(entry.getKey() + "," + entry.getValue());
		}
	}

	private void findMostFreqentAPIs(Map<String, Integer> apiFreqMap) {
		/*sort map*/
		Map<String, Integer> sortedApiFreqMap = sortByValue(apiFreqMap);
		/*print map order by frequency*/
		logger.info("api, count");
		for (Map.Entry<String, Integer> entry : sortedApiFreqMap.entrySet()) {
			logger.debug(entry.getKey() + "," + entry.getValue());
		}
	}

	private Map<String, Integer> mostTimeConsumingAPIs(List<LogEntry> logEntries) {
		Map<String, Integer> responseTimeMap = new HashMap<>();
		Map<String, Integer> apiCountMap = new HashMap<>();
		for (LogEntry logEntry : logEntries) {
			int responseTime = logEntry.getResponseTime();
			int apiCount = 1;
			if(responseTimeMap.containsKey(logEntry.getRequestUrl())) {
				responseTime = responseTime + responseTimeMap.get(logEntry.getRequestUrl());
			}
			responseTimeMap.put(logEntry.getRequestUrl(), responseTime);
			if(apiCountMap.containsKey(logEntry.getRequestUrl())) {
				apiCount = apiCount + apiCountMap.get(logEntry.getRequestUrl());
			}
			apiCountMap.put(logEntry.getRequestUrl(), apiCount);
		}
		
		/*Find avg response time*/
		for (Map.Entry<String, Integer> entry : responseTimeMap.entrySet()) {
			logger.debug("url : " + entry.getKey() + " aggr response time : " + entry.getValue());
			int avgResponseTime = entry.getValue()/apiCountMap.get(entry.getKey());
			responseTimeMap.put(entry.getKey(), avgResponseTime);
		}
		
		/*sort map*/
		Map<String, Integer> sortedResponseTimeMap = sortByValue(responseTimeMap);
		
		/*print map order by response time*/
		logger.info("URL,Avg Response Time (µsec)");
		for (Map.Entry<String, Integer> entry : sortedResponseTimeMap.entrySet()) {
			logger.debug(entry.getKey() + "," + entry.getValue());
		}
		
		return apiCountMap;
	}


	private LogEntry processLogEntry(String logEntryLine) {
		
		LogEntry logEntry = new LogEntry();
		String logEntryPattern = "^(\\S+) ([\\d.]+) (\\S+) (\\S+) \\[([\\w:/]+\\s[+\\-]\\d{4})\\] \"(.+?)\" (\\d{3}) (\\d+) \"([^\"]+)\" \"([^\"]+)\" (\\S+) ([a-z0-9-\\.]+) (\\d+)(\\/)(\\d+)";
		logger.debug("Using RE Pattern:");
		logger.debug(logEntryPattern);
		logger.debug("Input line is:");
		logger.debug(logEntryLine);
		Pattern p = Pattern.compile(logEntryPattern);
		Matcher matcher = p.matcher(logEntryLine);
		if (!matcher.matches()) {
			logger.error("Bad log entry (or problem with RE?):");
			logger.error(logEntryLine);
			return null;
		}else logger.debug("Regular expression matched!!!");
		logger.debug("IP Address: " + matcher.group(2));
		logger.debug("Date&Time: " + matcher.group(5));
		logger.debug("Request: " + matcher.group(6));
		logger.debug("Response Code: " + matcher.group(7));
		logger.debug("Bytes Sent: " + matcher.group(8));
		if (!matcher.group(8).equals("-"))
			logger.debug("Referer: " + matcher.group(9));
		logger.debug("Browser: " + matcher.group(10));
		logger.debug("Server: " + matcher.group(12));
		logger.debug("Response time ms: " + matcher.group(15));
		
		logEntry.setIpAddress(matcher.group(2));
		String requestUrl = matcher.group(6);
		if(requestUrl!=null) {
			if(requestUrl.contains("?")) requestUrl = requestUrl.substring(0, requestUrl.indexOf("?"));
			logEntry.setRequestUrl(requestUrl);
		}
		String responseCode = matcher.group(7);
		if(responseCode!=null)
			logEntry.setResponseCode(responseCode);
		String bytesSend = matcher.group(7);
		if(bytesSend!=null)
			logEntry.setBytesSend(Integer.valueOf(bytesSend));
		String responseTime = matcher.group(15);
		if(responseTime!=null)
			logEntry.setResponseTime(Integer.valueOf(responseTime));
		
		return logEntry;
	}
	
	// function to sort hashmap by values 
    private static Map<String, Integer> sortByValue(Map<String, Integer> map){ 
        // Create a list from elements of HashMap 
        List<Map.Entry<String, Integer> > list = 
               new LinkedList<Map.Entry<String, Integer> >(map.entrySet()); 
  
        // Sort the list 
        Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() { 
            public int compare(Map.Entry<String, Integer> o1,  
                               Map.Entry<String, Integer> o2) 
            { 
                return (o2.getValue()).compareTo(o1.getValue()); 
            } 
        }); 
          
        // put data from sorted list to hashmap  
        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>(); 
        for (Map.Entry<String, Integer> aa : list) { 
            temp.put(aa.getKey(), aa.getValue()); 
        } 
        return temp; 
    } 

}
