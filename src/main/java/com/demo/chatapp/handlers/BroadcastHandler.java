package com.demo.chatapp.handlers;

import com.demo.chatapp.model.ChatMessage;
import com.demo.chatapp.model.UserInfo;
import com.demo.chatapp.model.enums.MessageType;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class BroadcastHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(BroadcastHandler.class);

  public static final Map<String, UserInfo> connectedUsers = new HashMap<>();
  private final static String MESSAGE_TYPE_PROPERTY = "messageType";
  private final static String CONTENT_PROPERTY = "content";
  private static final String SENDER_PROPERTY = "sender";

  public static void sendConnectMessage(final ServerWebSocket ws,final String broadcastMessage){
    Map<String, UserInfo> filteredUsers = new HashMap<>();
    connectedUsers.entrySet().stream()
      .filter(u -> !u.getKey().equals(ws.textHandlerID()))
      .forEach(u -> filteredUsers.put(u.getKey() , u.getValue()));
    sendMessage(filteredUsers , broadcastMessage);
  }

  public static void sendMessage(final Map<String, UserInfo> users ,final String broadcastMessage) {
    users.entrySet().stream().forEach(u -> {
      LOGGER.debug("Sending to ID : {} " , u.getKey());
      u.getValue().getWs().writeTextMessage(broadcastMessage);
    });
  }

  public static void handleMessage(final ServerWebSocket ws, final JsonObject jsonMessage) {
    final String messageType = jsonMessage.getString(MESSAGE_TYPE_PROPERTY);
    final String username = jsonMessage.getString(SENDER_PROPERTY);
    if (messageType.equals(MessageType.ADD_USER.getValue())) {
      jsonMessage.remove(CONTENT_PROPERTY);
      connectedUsers.put(ws.textHandlerID() , UserInfo.builder()
          .username(username)
          .ws(ws)
        .build());
      sendConnectMessage(ws,jsonMessage.toString());
    } else if (messageType.equals(MessageType.MESSAGE.getValue())) {
      sendMessage(connectedUsers,jsonMessage.toString());
    }
  }

  public static void disconnectUser(ServerWebSocket ws) {
    final String username = connectedUsers.get(ws.textHandlerID()).getUsername();
    LOGGER.debug("Disconnect username {} " , username);
    connectedUsers.remove(ws.textHandlerID());
    sendMessage(connectedUsers , JsonObject.mapFrom(ChatMessage.builder()
      .messageType(MessageType.DISCONNECT)
      .Sender(username)
      .build()).toString()
    );
  }
}
