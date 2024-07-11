package com.demo.chatapp;

import com.demo.chatapp.handlers.WebSocketHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle {


  private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Router router = Router.router(vertx);
    router.route("/static/*").handler(StaticHandler.create("static").setCachingEnabled(false));
    router.route("/chatroom").handler(routingContext -> {
      HttpServerResponse response = routingContext.response();
      response.sendFile("static/index.html");
    });

    vertx.createHttpServer(new HttpServerOptions()
        .setRegisterWebSocketWriteHandlers(true))
        .requestHandler(router)
        .webSocketHandler(new WebSocketHandler())
      .listen(8888, http -> {
        if (http.succeeded()) {
          startPromise.complete();
          LOGGER.info("Server started on port {}", 8888);
        } else {
          startPromise.fail(http.cause());
        }});
  }
}
