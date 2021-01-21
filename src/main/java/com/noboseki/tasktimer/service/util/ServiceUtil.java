package com.noboseki.tasktimer.service.util;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;

@Component
@NoArgsConstructor
public class ServiceUtil {

    public String mapTimeToString(int hours, int minutes) {
        String time = "";

        if (hours >= 10) {
            time += String.valueOf(hours);
        } else {
            time += "0" + hours;
        }

        if (minutes >= 10) {
            time += ":" + minutes;
        } else {
            time += ":0" + minutes;
        }

        return time;
    }

    public boolean isValidFormat(String format, String value) {
        java.util.Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            date = sdf.parse(value);
            if (!value.equals(sdf.format(date))) {
                date = null;
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return date != null;
    }
}
