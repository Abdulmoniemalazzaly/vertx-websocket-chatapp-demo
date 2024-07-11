package com.demo.chatapp.model.enums;

public enum MessageType {
  DISCONNECT("DISCONNECT"), MESSAGE("MESSAGE"), ADD_USER("ADD_USER");

  private String value;
  MessageType(final String value) {
    this.value = value;
  }

  public String getValue(){
    return this.value;
  }
}
