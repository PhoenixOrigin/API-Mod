package net.phoenix.api.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.phoenix.api.WebsocketHandler;
import net.phoenix.api.client.APIClient;

import java.net.URI;
import java.net.URISyntaxException;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class WebsocketCommand {

    private int execute(CommandContext<FabricClientCommandSource> ctx) {
        MinecraftClient.getInstance().player.sendMessage(Text.literal("Incorrect usage of command"));
        return 0;
    }

    private int terminate(CommandContext<FabricClientCommandSource> ctx) {
        APIClient.websocket.dc = true;
        APIClient.websocket.close();
        return 0;
    }

    private int connect(CommandContext<FabricClientCommandSource> ctx) {
        APIClient.websocket.dc = false;
        URI uri = null;
        try {
            uri = new URI(APIClient.config.get("ws-url"));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        APIClient.websocket = new WebsocketHandler(uri);
        APIClient.websocket.connect();
        APIClient.websocket.setMessageHandler(new WebsocketHandler.MessageHandler() {
            @Override
            public void handleMessage(String message) {
                MinecraftClient client = MinecraftClient.getInstance();
                if (message.equals("$SERVERPING")) {
                    APIClient.websocket.send("$CLIENTPONG");
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
        return 0;
    }

    private int send(CommandContext<FabricClientCommandSource> ctx) {
        APIClient.websocket.send(ctx.getArgument("content", String.class));
        return 0;
    }


    public LiteralArgumentBuilder<FabricClientCommandSource> build(){
        return literal("websocket")
                .then(literal("terminate").executes(this::terminate))
                .then(literal("send")
                        .then(RequiredArgumentBuilder.argument("content", StringArgumentType.greedyString()))
                        .executes(this::send))
                .then(literal("connect").executes(this::connect))
                .executes(this::execute);
    }



}
