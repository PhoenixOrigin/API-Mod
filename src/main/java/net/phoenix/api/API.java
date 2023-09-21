package net.phoenix.api;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.phoenix.api.commands.GetCommand;
import net.phoenix.api.commands.PostCommand;
import net.phoenix.api.utils.SimpleConfig;


public class API implements ModInitializer {

    public static SimpleConfig config = null;

    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(new GetCommand().build());
            dispatcher.register(new PostCommand().build());
        });

        config = SimpleConfig.of("settings").request();

    }
}
