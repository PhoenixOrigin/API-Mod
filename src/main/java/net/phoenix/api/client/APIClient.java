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
    public static String username = null;
    public static String uuid = null;
    public static MinecraftClient client = null;

    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(new GetCommand().build());
            dispatcher.register(new PostCommand().build());
        });

        client = MinecraftClient.getInstance();
        username = client.player.getName().getString();
        uuid =  client.player.getUuidAsString();

        config = SimpleConfig.of("settings").request();
        try {
            URI uri = new URI(config.get("ws-url"));
            websocket = new WebsocketHandler(uri);
            websocket.connect();
            websocket.setMessageHandler(message -> {
                MinecraftClient client = MinecraftClient.getInstance();
                client.execute(() -> client.player.sendMessage(Text.literal(message)));
            });
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        ClientSendMessageEvents.CHAT.register((message) -> {
            String token = config.get("token");

            websocket.sendMessage(String.format("""
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

            websocket.sendMessage(String.format("""
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
