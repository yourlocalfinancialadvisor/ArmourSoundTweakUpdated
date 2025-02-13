package mr_chumbucket.armoursoundtweak.integration;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import mr_chumbucket.armoursoundtweak.ArmourSoundTweak;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public final class ClothConfigIntegration {
    private static final String SOUNDS_CONFIG = ArmourSoundTweak.MOD_ID + "." + "sounds";
    private static final String ARMOR_CONFIG = ArmourSoundTweak.MOD_ID + "." + ArmourSoundTweak.ARMOR;
    private static final String ELYTRA_CONFIG = ArmourSoundTweak.MOD_ID + "." + ArmourSoundTweak.ELYTRA;
    private static final String SKULLS_CONFIG = ArmourSoundTweak.MOD_ID + "." + ArmourSoundTweak.SKULLS;
    private static final String PUMPKINS_CONFIG = ArmourSoundTweak.MOD_ID + "." + ArmourSoundTweak.PUMPKINS;
    private static final String ANYTHING_CONFIG = ArmourSoundTweak.MOD_ID + "." + ArmourSoundTweak.ANYTHING;

    private ClothConfigIntegration() {
    }

    public static Screen buildConfigScreen(final Screen parent) {
        final var screen = ConfigBuilder.create()
                .setTitle(Text.translatable(ArmourSoundTweak.MOD_ID))
                .setSavingRunnable(ArmourSoundTweak::saveConfig)
                .setParentScreen(parent);

        final var entries = screen.entryBuilder();

        screen.getOrCreateCategory(Text.translatable(SOUNDS_CONFIG))
                .addEntry(entries.startBooleanToggle(
                                Text.translatable(ARMOR_CONFIG),
                                ArmourSoundTweak.armor())
                        .setDefaultValue(ArmourSoundTweak.DEFAULT_ARMOR)
                        .setSaveConsumer(ArmourSoundTweak::armor)
                        .build())

                .addEntry(entries.startBooleanToggle(
                                Text.translatable(ELYTRA_CONFIG),
                                ArmourSoundTweak.elytra())
                        .setDefaultValue(ArmourSoundTweak.DEFAULT_ELYTRA)
                        .setSaveConsumer(ArmourSoundTweak::elytra)
                        .build())

                .addEntry(entries.startBooleanToggle(
                                Text.translatable(SKULLS_CONFIG),
                                ArmourSoundTweak.skulls())
                        .setDefaultValue(ArmourSoundTweak.DEFAULT_SKULLS)
                        .setSaveConsumer(ArmourSoundTweak::skulls)
                        .build())

                .addEntry(entries.startBooleanToggle(
                                Text.translatable(PUMPKINS_CONFIG),
                                ArmourSoundTweak.pumpkins())
                        .setDefaultValue(ArmourSoundTweak.DEFAULT_PUMPKINS)
                        .setSaveConsumer(ArmourSoundTweak::pumpkins)
                        .build())

                .addEntry(entries.startBooleanToggle(
                                Text.translatable(ANYTHING_CONFIG),
                                ArmourSoundTweak.anything())
                        .setDefaultValue(ArmourSoundTweak.DEFAULT_ANYTHING)
                        .setSaveConsumer(ArmourSoundTweak::anything)
                        .build());

        return screen.build();
    }
}
