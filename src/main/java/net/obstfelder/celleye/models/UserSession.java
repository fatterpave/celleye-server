package net.obstfelder.celleye.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by obsjoa on 16.02.2017.
 */
public class UserSession
{
    private String username;
    private Session webSocketSession;
    private Date startTime;
    private long lastKeepalive;
    private boolean webUser;

    public UserSession(String username,Session websocketSession,Date startTime)
    {
        this.webSocketSession = websocketSession;
        this.username = username;
        this.startTime = startTime;
        lastKeepalive = startTime.getTime();
    }

    public boolean isWebUser() {
        return webUser;
    }

    public void setWebUser(boolean webUser) {
        this.webUser = webUser;
    }

    public Date getStartTime(){return startTime;}

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    @JsonIgnore
    public Session getWebSocketSession()
    {
        return webSocketSession;
    }

    public void setWebSocketSession(Session webSocketSession)
    {
        this.webSocketSession = webSocketSession;
    }

    @Override
    public boolean equals(Object obj)
    {
        UserSession obj2 = (UserSession)obj;
        return this.username.equals(obj2.getUsername());
    }

    public long getLastKeepalive()
    {
        return lastKeepalive;
    }

    public void setLastKeepalive(long lastKeepalive)
    {
        this.lastKeepalive = lastKeepalive;
    }

    @Override
    public String toString()
    {
        return "Username: "+getUsername();
    }
}
