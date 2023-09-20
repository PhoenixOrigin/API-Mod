package net.phoenix.api.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.phoenix.api.RequestHandler;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class GetCommand {

    private int execute(CommandContext<FabricClientCommandSource> ctx) {


        return 0;
    }

    private int pathExecute(CommandContext<FabricClientCommandSource> ctx) {
        String path = ctx.getArgument("path", String.class);
        String player = ctx.getSource().getPlayer().getName().getString();
        String uuid = ctx.getSource().getPlayer().getUuidAsString();
        if(RequestHandler.isUrl(path)) {
            RequestHandler.get(path, player, uuid, response -> {
                MinecraftClient.getInstance().player.sendMessage(Text.literal(response.getAsString()));
            });
        } else {
            
        }


        return 0;
    }

    public LiteralArgumentBuilder<FabricClientCommandSource> build(){
        return literal("get")
                .then(argument("path", StringArgumentType.string()).executes(this::pathExecute))
                .executes(this::execute);
    }

}
