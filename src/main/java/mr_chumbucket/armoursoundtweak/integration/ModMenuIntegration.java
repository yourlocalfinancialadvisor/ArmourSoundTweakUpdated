package mr_chumbucket.armoursoundtweak.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import mr_chumbucket.armoursoundtweak.ArmourSoundTweak;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Util;

import java.util.function.Function;
import java.util.function.Supplier;

public final class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> buildOrOpen(parent, () -> ClothConfigIntegration::buildConfigScreen);
    }

    private static Screen buildOrOpen(final Screen parent, final Supplier<Function<Screen, Screen>> builder) {
        return FabricLoader.getInstance().isModLoaded("cloth-config")
                ? builder.get().apply(parent)
                : new ConfirmLinkScreen(ok -> {
            if (ok) {
                Util.getOperatingSystem().open(ArmourSoundTweak.CONFIG_FILE.toUri());
            }

            MinecraftClient.getInstance().setScreen(parent);
        }, ArmourSoundTweak.CONFIG_FILE.toString(), true);
    }
}