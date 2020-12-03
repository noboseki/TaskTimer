package com.noboseki.tasktimer.service.util;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

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
}
