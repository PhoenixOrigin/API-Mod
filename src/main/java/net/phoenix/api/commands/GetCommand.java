package net.phoenix.api.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.phoenix.api.API;
import net.phoenix.api.RequestHandler;

import java.io.File;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class GetCommand {

    private int execute(CommandContext<FabricClientCommandSource> ctx) {


        return 0;
    }

    private int pathExecute(CommandContext<FabricClientCommandSource> ctx){
        String player = ctx.getSource().getPlayer().getName().getString();
        String uuid = ctx.getSource().getPlayer().getUuidAsString();
        try {
            String path = ctx.getArgument("path", String.class);
            if (RequestHandler.isUrl(path)) {
                System.out.println("working");
                RequestHandler.get(path, player, uuid, response -> {
                    String res = response.getAsString();
                    MinecraftClient client = MinecraftClient.getInstance();
                    client.execute(() -> {
                        client.player.sendMessage(Text.literal(res));
                    });
                });
            } else {
                String url = API.config.get("url") + path;
                RequestHandler.get(url, player, uuid, response -> {
                    String res = response.getAsString();
                    MinecraftClient client = MinecraftClient.getInstance();
                    client.execute(() -> {
                        client.player.sendMessage(Text.literal(res));
                    });
                });
            }
        } catch (IllegalArgumentException ignored) {
            String url = API.config.get("url");
            RequestHandler.get(url, player, uuid, response -> {
                String res = response.getAsString();
                MinecraftClient client = MinecraftClient.getInstance();
                client.execute(() -> {
                    client.player.sendMessage(Text.literal(res));
                });
            });
        }


        return 0;
    }

    public LiteralArgumentBuilder<FabricClientCommandSource> build(){
        return literal("get")
                .then(argument("path", StringArgumentType.greedyString()).executes(this::pathExecute))
                .executes(this::execute);
    }

}
