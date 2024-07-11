package com.demo.chatapp.handlers;

import io.vertx.core.Handler;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketFrame;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketHandler implements Handler<ServerWebSocket> {

  private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketHandler.class);
  private final static String PATH = "/ws/chatapp/connect";

  @Override
  public void handle(ServerWebSocket ws) {
    final String path = ws.path();

    if (!path.equalsIgnoreCase(PATH)) {
      closeClient(ws);
      return;
    }

    ws.accept();
    ws.frameHandler(getWebSocketFrameHandler(ws));
    ws.endHandler(onClose -> BroadcastHandler.disconnectUser(ws));
    ws.exceptionHandler(error -> LOGGER.error("failed ", error));

  }

  private Handler<WebSocketFrame> getWebSocketFrameHandler(ServerWebSocket ws) {
    return received -> {
      final String path = ws.path();
      LOGGER.debug("is text : {} , is binary {} ", received.isText() , received.isBinary());
      if (received.isText()){
        final String receivedMessage = received.textData();
        LOGGER.debug("Received Message : {} " , receivedMessage);
        final JsonObject jsonMessage =  new JsonObject(receivedMessage);
        BroadcastHandler.handleMessage(ws , jsonMessage);
      }
    };
  }

  private void closeClient(ServerWebSocket ws) {
    LOGGER.info("Rejected wrong path: {}" , ws.path());
    ws.writeFinalTextFrame("Rejected Path!");
    ws.close( (short) 1000, "Normal closure");
  }
}
