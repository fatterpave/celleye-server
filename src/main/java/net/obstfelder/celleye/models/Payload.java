package net.obstfelder.celleye.models;

import java.util.List;

/**
 * Created by paven on 16.08.2017.
 */
public class Payload
{
    public int autoUpdate = 0;
    public String message;
    public boolean error;
    public String forUser;
    public CellResponseData cellResponseData;
    public List<String> appUserList;

    public Payload(){}
    public Payload(String message,boolean error)
    {
        this.message = message;
        this.error = error;
    }

    public Payload(String message,CellResponseData cellResponseData)
    {
        this.message = message;
        this.cellResponseData = cellResponseData;
    }

    public Payload(String message)
    {
        this.message = message;
    }

    public Payload(String message,int autoUpdate,String forUser)
    {
        this.message = message;
        this.autoUpdate = autoUpdate;
        this.forUser = forUser;
    }
}
