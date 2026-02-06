package com.keinn.grade_analysis_v2.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Slf4j
@Component
public class DateFormatConverter implements Converter<String[], List<Timestamp>> {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");

    @Override
    public List<Timestamp> convert(String[] source) {
        List<Timestamp> timestamps = new ArrayList<>();
        for (String dateStr : source) {
            try {
                timestamps.add(new Timestamp(DATE_FORMAT.parse(dateStr).getTime()));
            } catch (ParseException e) {
                throw new IllegalArgumentException("Invalid date format: " + dateStr, e);
            }
        }
        return timestamps;
    }

    public Date convertStringToDate(String dateStr) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        try {
            return formatter.parse(dateStr);
        } catch (ParseException e) {
            throw new Exception("Invalid date format: " + dateStr);
        }

    }

    public Timestamp convertStringToTimestamp(String dateStr) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(dateStr, formatter);
            return Timestamp.valueOf(localDateTime);
        } catch (Exception e) {
            log.error("日期转换失败: {}", e.getMessage());
        }
        return null;
    }
}
