package net.obstfelder.celleye;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

/**
 * Created by obsjoa on 10.02.2017.
 */
public class Config extends Configuration
{
    @JsonProperty
    private String alarmProbePath;

    public String getAlarmProbePath()
    {
        return alarmProbePath;
    }

}
