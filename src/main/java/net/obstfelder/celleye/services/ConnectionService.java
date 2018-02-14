package net.obstfelder.celleye.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import net.obstfelder.celleye.models.CellResponseData;
import net.obstfelder.celleye.models.Payload;
import net.obstfelder.celleye.models.UserSession;
import net.obstfelder.celleye.models.WebSocketRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.websocket.*;
import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by paven on 16.08.2017.
 */
@Singleton
public class ConnectionService
{
    private static final Logger LOG = LoggerFactory.getLogger(ConnectionService.class);
    private static Map<String,UserSession> userSessionMap = null;

    public ConnectionService()
    {
        userSessionMap = new HashMap<>();
        //checkConnectedAppUsers();
    }

    public static void parseAndSendCellResponse(WebSocketRequest request)
    {
        try
        {
            CellResponseData data = new CellResponseData();
            data.battery = request.getParams().get("battery")!=null?Integer.parseInt(request.getParams().get("battery")):null;
            data.dateTime = Long.parseLong(request.getParams().get("dateTime"));
            data.latitude = Double.valueOf(request.getParams().get("latitude"));
            data.longitude = Double.valueOf(request.getParams().get("longitude"));
            data.username = request.getParams().get("username");
            data.forUser = request.getParams().get("forUser");
            data.network = request.getParams().get("network");
            data.status = request.getParams().get("status");
            sendToWebSocket(userSessionMap.get(data.forUser),new Payload("Response from cell for user "+data.username,data));
        }
        catch(Exception e)
        {
            LOG.error("Failed parsing CellResponseData "+request.toString());

            for(UserSession session : userSessionMap.values())
            {
                sendToWebSocket(session,new Payload("Failed parsing cell response",true));
            }
        }
    }

    public static List<String> getAppUserList()
    {
        List<String> userList = new ArrayList<>();
        for(UserSession userSession : userSessionMap.values())
        {
            if(!userSession.isWebUser()) userList.add(userSession.getUsername());
        }
        return userList;
    }

    public synchronized static void requestCellResponseData(String username,String cellUserName,String autoUpdate)
    {
        UserSession user = userSessionMap.get(username.toUpperCase());
        UserSession cellUser = userSessionMap.get(cellUserName.toUpperCase());
        if(cellUser==null)
        {
            sendToWebSocket(user,new Payload("Cell user "+cellUserName+" not present"));
        }
        else
        {
            int autoUpdateInterval = 0;
            if(autoUpdate!=null) autoUpdateInterval = Integer.parseInt(autoUpdate);
            sendToWebSocket(cellUser,new Payload("User "+username+" requesting info (autoUpdate="+autoUpdateInterval+")",autoUpdateInterval,user.getUsername()));
        }
    }

    private static void checkConnectedAppUsers()
    {
        TimerTask checkConnectionsTask = new TimerTask() {

            @Override
            public synchronized void run()
            {
                List<UserSession> userSessions = getSessions();
                for(UserSession userSession : userSessions)
                {
                    if(!userSession.isWebUser())
                    {
                        long lastKeepAliveforSession = userSession.getLastKeepalive();
                        if (new Date().getTime() - lastKeepAliveforSession > 4000) {
                            try {
                                String usernameDisconnected = removeSession(userSession.getWebSocketSession());
                                userSession.getWebSocketSession().close();
                                LOG.info("Connection closed for user: " + usernameDisconnected + " due to missing pong signal");
                            } catch (Exception e) {
                                LOG.error("Failed to remove websocket session" + e.toString());
                            }
                        }
                    }
                }
            }
        };

        Timer timer = new Timer("Check app connections timer");
        timer.scheduleAtFixedRate(checkConnectionsTask,10000,5000);
    }

    public synchronized static void updateKeepaliveForUserSession(Session session)
    {
        for(UserSession userSession : userSessionMap.values())
        {
            if(session.getId().equals(userSession.getWebSocketSession().getId())) userSession.setLastKeepalive(new Date().getTime());
        }
    }

    public synchronized static void addSession(UserSession userSession)
    {
        userSessionMap.put(userSession.getUsername(),userSession);
        LOG.info(String.format("Websocket connection established - user %s ",userSession.getUsername()));
    }

    public static void checkUserConnected(String username)
    {
        for(UserSession session : getSessions())
        {
            if(session.getUsername().equals(username) && session.getWebSocketSession().isOpen())
            {
                Payload payload = new Payload("Same user is logging in elsewhere - disconnection this session");
                //PAYLOAD TYPE
                sendToWebSocket(session,payload);
                LOG.info("User "+username+" is connected elsewhere, replacing session og sending logout signal to app");
                break;
            }
        }
    }

    public static List<UserSession> getSessions()
    {
        return new ArrayList<>(userSessionMap.values());
    }

    public static UserSession getUserSession(Session session)
    {
        try {
            return userSessionMap.entrySet()
                    .stream()
                    .filter(map -> map.getValue().getWebSocketSession().getId().equals(session.getId()))
                    .map(map -> map.getValue()).findFirst().get();
        }
        catch(NoSuchElementException e)
        {
            return null;
        }
    }

    public static void clearSessions(Session session)
    {
        userSessionMap = new HashMap<>();
        try
        {
            for (Session s : session.getOpenSessions()) if (s.isOpen()) s.close();
        }
        catch(Exception ex)
        {
            LOG.error("Error clearing websocket sessions",ex);
        }
    }

    public synchronized static String removeSession(Session session)
    {
        String username = null;
        try
        {
            for (UserSession sessions : userSessionMap.values())
            {
                if (sessions.getWebSocketSession().getId().equals(session.getId()))
                {
                    userSessionMap.remove(sessions.getUsername());
                    username = sessions.getUsername();
                    LOG.info("Websocket connection disconnected for user " + username);
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return username;
    }

    private static void sendToWebSocket(UserSession session, Payload payload)
    {
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(payload);
            if(session.getWebSocketSession().isOpen())
            {
                LOG.info("Sending update to user "+session.getUsername()+" json: "+json);
                if(payload.error) LOG.info("Error: "+payload.message);
                session.getWebSocketSession().getAsyncRemote().sendText(json);
            }
            else LOG.warn("Websocket is closed");
        }
        catch(Exception e)
        {
            LOG.error("Error sending to websocket: ",e);
        }
    }
}
