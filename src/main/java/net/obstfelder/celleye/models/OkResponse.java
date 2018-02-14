package net.obstfelder.celleye.models;

/**
 * Created by obsjoa on 22.02.2017.
 */
public class OkResponse
{
    public int status = 200;
    public boolean ok = true;
    public String message;

    public OkResponse()
    {
    }

    public static OkResponse OK()
    {
        return new OkResponse(200,true,"");
    }

    public OkResponse(int status, boolean ok, String message)
    {
        this.status = status;
        this.ok = ok;
        this.message = message;
    }
}
