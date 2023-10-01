package net.phoenix.api.commands;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.phoenix.api.client.APIClient;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class WebsocketCommand {

    private int execute(CommandContext<FabricClientCommandSource> ctx) {
        String token = APIClient.config.get("token");
        MinecraftClient client = MinecraftClient.getInstance();
        String username = client.player.getName().getString();
        String uuid = client.player.getUuidAsString();

        JsonObject obj = new JsonObject();
        obj.addProperty("wsToken", token);
        obj.addProperty("username", username);
        obj.addProperty("uuid", uuid);
        obj.addProperty("message", ctx.getArgument("content", String.class).replace("\n", "\\n"));
        obj.addProperty("type", "chat");

        APIClient.websocket.send(obj.toString());
        return 0;
    }

    private int terminate(CommandContext<FabricClientCommandSource> ctx) {
        if(APIClient.websocket.isClosed()) {
            MinecraftClient.getInstance().player.sendMessage(Text.literal("You are already disconnected the server!"));
            return 0;
        }
        APIClient.websocket.dc = true;
        APIClient.websocket.close();
        return 0;
    }

    private int connect(CommandContext<FabricClientCommandSource> ctx) {
        if(APIClient.websocket.isOpen()) {
            MinecraftClient.getInstance().player.sendMessage(Text.literal("You are already connected to the server!"));
            return 0;
        }
        APIClient.websocket.dc = false;
        APIClient.connectWebsocket();
        return 0;
    }

    private int send(CommandContext<FabricClientCommandSource> ctx) {
        String token = APIClient.config.get("token");
        MinecraftClient client = MinecraftClient.getInstance();
        String username = client.player.getName().getString();
        String uuid = client.player.getUuidAsString();

        JsonObject obj = new JsonObject();
        obj.addProperty("wsToken", token);
        obj.addProperty("username", username);
        obj.addProperty("uuid", uuid);
        obj.addProperty("message", ctx.getArgument("content", String.class).replace("\n", "\\n"));
        obj.addProperty("type", "manualSend");

        APIClient.websocket.send(obj.toString());
        return 0;
    }

    private int command(CommandContext<FabricClientCommandSource> ctx) {
        String token = APIClient.config.get("token");
        MinecraftClient client = MinecraftClient.getInstance();
        String username = client.player.getName().getString();
        String uuid = client.player.getUuidAsString();

        JsonObject obj = new JsonObject();
        obj.addProperty("wsToken", token);
        obj.addProperty("username", username);
        obj.addProperty("uuid", uuid);
        obj.addProperty("message", ctx.getArgument("content", String.class).replace("\n", "\\n"));
        obj.addProperty("type", "command");

        APIClient.websocket.send(obj.toString());
        return 0;
    }


    public LiteralArgumentBuilder<FabricClientCommandSource> build(){
        return literal("websocket")
                .then(literal("terminate").executes(this::terminate))
                .then(literal("send")
                        .then(argument("content", StringArgumentType.greedyString()).executes(this::send)))
                .then(literal("command")
                        .then(argument("content", StringArgumentType.greedyString()).executes(this::send)))
                .then(literal("connect").executes(this::connect))
                .executes(this::execute);
    }



}
