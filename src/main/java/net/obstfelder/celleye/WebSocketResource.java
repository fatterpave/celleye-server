package net.obstfelder.celleye;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.obstfelder.celleye.models.CellResponseData;
import net.obstfelder.celleye.models.Payload;
import net.obstfelder.celleye.models.UserSession;
import net.obstfelder.celleye.models.WebSocketRequest;
import net.obstfelder.celleye.services.ConnectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by obsjoa on 10.02.2017.
 */
@ServerEndpoint("/ws")
public class WebSocketResource
{
    private static final Logger LOG = LoggerFactory.getLogger(WebSocketResource.class);
    private static final String COMMAND_SUBSCRIBE = "SUBSCRIBE";
    private static final String COMMAND_REQUEST_DATA = "REQUEST_DATA";
    private static final String COMMAND_RESPONSE_DATA = "RESPONSE_DATA";

    @OnOpen
    public void onOpen(final Session session) throws IOException
    {
        session.setMaxIdleTimeout(0);
        LOG.info("Connection established.");
        //sendMessage(session,new Payload("Connection established with CellEye Server"));
    }

    @OnMessage
    public void onPong(final Session session,PongMessage pong)
    {
        ConnectionService.updateKeepaliveForUserSession(session);
    }

    @OnMessage
    public void onMessage(final Session session, String message)
    {
        WebSocketRequest request = handleRequest(message, session);
        if(request==null) return;

        switch (request.getCommand())
        {
            case COMMAND_SUBSCRIBE:
                if(request.getParams()==null) return;
                String username = request.getParams().get("username");
                String webUser = request.getParams().get("webUser");
                UserSession userSession = new UserSession(username.toUpperCase(),session,new Date());
                Payload payload = new Payload(String.format("User '%s' subscribing to CellEye server",username));
                if(webUser!=null)
                {
                    userSession.setWebUser(true);
                    payload.appUserList = ConnectionService.getAppUserList();
                    //MOCK
                    /*List<String> mockList = new ArrayList<>();
                    mockList.add("Iben");
                    mockList.add("Odin");
                    payload.appUserList = mockList;*/
                }
                ConnectionService.addSession(userSession);
                sendMessage(session,payload);
                break;
            case COMMAND_REQUEST_DATA:
                String uname = request.getParams().get("username");
                String cellUserName = request.getParams().get("cellusername");
                String autoUpdate = request.getParams().get("autoupdate");
                ConnectionService.requestCellResponseData(uname,cellUserName,autoUpdate);
                sendMessage(session,new Payload("Requested info from cell user "+cellUserName));
                /*CellResponseData mockData = new CellResponseData();
                mockData.username = cellUserName;
                mockData.forUser = uname;
                mockData.status = "OK";
                mockData.network = "4G";
                mockData.latitude = 60.3128321;
                mockData.longitude = 5.3435966;
                mockData.battery = 44;
                mockData.dateTime = new Date().getTime();
                sendMessage(session,new Payload("Requested info from cell user "+cellUserName,mockData));*/
                break;
            case COMMAND_RESPONSE_DATA:
                ConnectionService.parseAndSendCellResponse(request);
                break;
            default:
                sendMessage(session,new Payload("Unknown command: "+request.getCommand()));
        }
    }

    @OnClose
    public void onClose(final Session session, CloseReason cr)
    {
        try
        {
            LOG.info("Connection closed for user: " + ConnectionService.getUserSession(session).getUsername() + " close reason: " + cr.getReasonPhrase() + " code: " + cr.getCloseCode().getCode());
            ConnectionService.removeSession(session);
        }
        catch(Exception e)
        {
            LOG.error("ERROR IN ONCLOSE");
            e.printStackTrace();
        }
    }

    @OnError
    public void onError(final Session session,Throwable error)
    {
        LOG.error("Error",error);
    }

    private WebSocketRequest handleRequest(String request, Session session)
    {
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            WebSocketRequest req = new WebSocketRequest();
            req = mapper.readValue(request,WebSocketRequest.class);
            return req;
        }
        catch(Exception e)
        {
            LOG.error("Failed parsing websocketrequest, sending standard error message, request: "+request);

            return null;
        }
    }

    private void sendMessage(Session session,Payload payload)
    {
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            String json = mapper.writeValueAsString(payload);
            LOG.debug("Sending JSON: "+json);
            session.getAsyncRemote().sendText(json);
        }
        catch (Exception e)
        {

        }
    }
}
