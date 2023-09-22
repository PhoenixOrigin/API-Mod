package net.phoenix.api.client;

import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.text.Text;
import net.phoenix.api.WebsocketHandler;
import net.phoenix.api.commands.GetCommand;
import net.phoenix.api.commands.PostCommand;
import net.phoenix.api.commands.WebsocketCommand;
import net.phoenix.api.utils.SimpleConfig;

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
            LiteralCommandNode<FabricClientCommandSource> node = dispatcher.register(new WebsocketCommand().build());
            dispatcher.register(ClientCommandManager.literal("ws").redirect(node));
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
                    if (message.equals("$SERVERPING")) {
                        websocket.send("$CLIENTPONG");
                        return;
                    }
                    client.execute(() -> {
                        try {
                            client.player.sendMessage(Text.literal(message));
                        } catch (NullPointerException ignored) {
                        }
                    });
                }
            });
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        ClientSendMessageEvents.CHAT.register((message) -> handleText(Text.literal(message)));
        ClientReceiveMessageEvents.CHAT.register(((message, signedMessage, sender, params, receptionTimestamp) -> handleText(message)));
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (overlay) return;
            handleText(message);
        });
    }

    private void handleText(Text message) {
        if (websocket.isClosed()) return;

        String token = config.get("token");
        MinecraftClient client = MinecraftClient.getInstance();
        String username = client.player.getName().getString();
        String uuid = client.player.getUuidAsString();

        websocket.send(String.format("""
                {
                  "wsToken": "%s",
                  "username": "%s",
                  "uuid": "%s",
                  "message": "%s"
                }
                """, token, username, uuid, message.copy().toString()));
    }
}
