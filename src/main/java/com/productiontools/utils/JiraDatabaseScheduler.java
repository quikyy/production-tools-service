package com.productiontools.utils;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.productiontools.services.JiraService;
import org.apache.tomcat.util.json.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class JiraDatabaseScheduler {

    @Autowired
    JiraService jiraService;

    private static final String midnightCronExpression = "0 0 0 * * *";

    @Scheduled(cron = midnightCronExpression)
    public void updateJiraDatabase() throws UnirestException, FileNotFoundException, ParseException {
        jiraService.updateJiraDatabase();
    }
}
