package mr_chumbucket.armoursoundtweak.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import eu.midnightdust.lib.config.MidnightConfig;
import mr_chumbucket.armoursoundtweak.ArmourSoundTweakClient;

public final class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> MidnightConfig.getScreen(parent, ArmourSoundTweakClient.MOD_ID);
    }
}