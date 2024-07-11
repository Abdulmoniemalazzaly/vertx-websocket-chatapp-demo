package com.demo.chatapp.model;

import io.vertx.core.http.ServerWebSocket;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserInfo {

  private String username;
  private ServerWebSocket ws;
}
