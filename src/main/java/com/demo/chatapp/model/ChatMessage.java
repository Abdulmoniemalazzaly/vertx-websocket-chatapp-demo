package com.demo.chatapp.model;

import com.demo.chatapp.model.enums.MessageType;
import io.vertx.core.json.JsonObject;
import lombok.*;

@Builder
@Data
public class ChatMessage {
  private MessageType messageType;
  private String Sender;
  private String content;
}
