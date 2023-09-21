package net.phoenix.api.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.phoenix.api.WebsocketHandler;
import net.phoenix.api.commands.GetCommand;
import net.phoenix.api.commands.PostCommand;
import net.phoenix.api.utils.SimpleConfig;
import net.fabricmc.fabric.api.client.message.v1.*;

import java.net.URI;
import java.net.URISyntaxException;

public class APIClient implements ClientModInitializer {

    public static SimpleConfig config = null;
    public static WebsocketHandler websocket = null;

    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(new GetCommand().build());
            dispatcher.register(new PostCommand().build());
        });

        config = SimpleConfig.of("httpconfig/settings").request();
        try {
            URI uri = new URI(config.get("ws-url"));
            websocket = new WebsocketHandler(uri);
            websocket.connect();
            websocket.setMessageHandler(new WebsocketHandler.MessageHandler() {
                @Override
                public void handleMessage(String message) {
                    MinecraftClient client = MinecraftClient.getInstance();
                    client.execute(() -> {
                        try {
                            client.player.sendMessage(Text.literal(message));
                        } catch (NullPointerException ignored){}
                    });
                }
            });
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        ClientSendMessageEvents.CHAT.register((message) -> {
            String token = config.get("token");
            MinecraftClient client = MinecraftClient.getInstance();
            String username = client.player.getName().getString();
            String uuid =  client.player.getUuidAsString();

            websocket.send(String.format("""
                    {
                      "wsToken": %s,
                      "username": %s,
                      "uuid": %s,
                      "message": %s (the chat message)
                    }
                    """, token, username, uuid, message));
        });
        ClientReceiveMessageEvents.CHAT.register(((message, signedMessage, sender, params, receptionTimestamp) -> {
            String token = config.get("token");
            MinecraftClient client = MinecraftClient.getInstance();
            String username = client.player.getName().getString();
            String uuid =  client.player.getUuidAsString();

            websocket.send(String.format("""
                    {
                      "wsToken": %s,
                      "username": %s,
                      "uuid": %s,
                      "message": %s (the chat message)
                    }
                    """, token, username, uuid, message));
        }));
    }
}
