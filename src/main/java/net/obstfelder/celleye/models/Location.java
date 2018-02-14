package net.obstfelder.celleye.models;

import java.util.Date;

/**
 * Created by obsjoa on 13.02.2017.
 */
public class Location
{
    private String geo;
    private Date time;
    private String type;
    private int battery;
    private Date deviceTime;

    public Location()
    {
    }

    public String getGeo()
    {
        return geo;
    }

    public void setGeo(String geo)
    {
        this.geo = geo;
    }

    public Date getTime()
    {
        return time;
    }

    public void setTime(Date time)
    {
        this.time = time;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public int getBattery()
    {
        return battery;
    }

    public void setBattery(int battery)
    {
        this.battery = battery;
    }

    public Date getDeviceTime()
    {
        return deviceTime;
    }

    public void setDeviceTime(Date deviceTime)
    {
        this.deviceTime = deviceTime;
    }
}
