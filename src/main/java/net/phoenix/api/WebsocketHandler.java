package net.phoenix.api;

import javax.websocket.*;
import java.net.URI;
import java.nio.ByteBuffer;

@ClientEndpoint
public class WebsocketHandler {

    private boolean disconnected = true;
    private final URI endpointURI;
    Session userSession = null;
    private MessageHandler messageHandler;

    public WebsocketHandler(URI endpointURI) {
        this.endpointURI = endpointURI;
    }

    /**
     * Connect to websocket
     */
    public void connect(){
        disconnected = false;
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, this.endpointURI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Callback hook for Connection open events.
     *
     * @param userSession the userSession which is opened.
     */
    @OnOpen
    public void onOpen(Session userSession) {
        this.userSession = userSession;
    }

    /**
     * Callback hook for Connection close events.
     *
     * @param userSession the userSession which is getting closed.
     * @param reason      the reason for connection close
     */
    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        this.userSession = null;
        if (disconnected) return;
        connect();
    }

    /**
     * Callback hook for Message Events. This method will be invoked when a client send a message.
     *
     * @param message The text message
     */
    @OnMessage
    public void onMessage(String message) {
        if (this.messageHandler != null) {
            this.messageHandler.handleMessage(message);
        }
    }

    @OnMessage
    public void onMessage(ByteBuffer bytes) {
        System.out.print(bytes);
    }

    /**
     * register message handler
     *
     * @param msgHandler
     */
    public void setMessageHandler(MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }

    /**
     * Send a message.
     *
     * @param message
     */
    public void sendMessage(String message) {
        this.userSession.getAsyncRemote().sendText(message);
    }

    /**
     * Message handler.
     *
     */
    public static interface MessageHandler {

        public void handleMessage(String message);
    }
}
