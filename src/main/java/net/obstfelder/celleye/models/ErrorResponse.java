package net.obstfelder.celleye.models;

/**
 * Created by obsjoa on 13.02.2017.
 */
public class ErrorResponse
{
    public boolean ok;
    public String message;
    public Exception exception;
    public int statusCode;

    public ErrorResponse(boolean ok,String message,int statusCode)
    {
        this.ok = ok;
        this.message = message;
        this.statusCode = statusCode;
    }

    public ErrorResponse(boolean ok,String message,int statusCode,Exception exception)
    {
        this.ok = ok;
        this.message = message;
        this.statusCode = statusCode;
        this.exception = exception;
    }
}
