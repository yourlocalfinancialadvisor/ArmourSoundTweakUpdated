package mr_chumbucket.armoursoundtweak;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.file.FileWatcher;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.*;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Environment(EnvType.CLIENT)
public final class ArmourSoundTweak implements ClientModInitializer {
	public static final String MOD_ID = "armoursoundtweak";

	public static final String ARMOR = "sounds.armor";
	public static final String ELYTRA = "sounds.elytra";
	public static final String SKULLS = "sounds.skulls";
	public static final String PUMPKINS = "sounds.pumpkins";
	public static final String ANYTHING = "sounds.anything";

	public static final boolean DEFAULT_ARMOR = true;
	public static final boolean DEFAULT_ELYTRA = true;
	public static final boolean DEFAULT_SKULLS = true;
	public static final boolean DEFAULT_PUMPKINS = true;
	public static final boolean DEFAULT_ANYTHING = true;

	public static final Path CONFIG_FILE =
			FabricLoader.getInstance().getGameDir()
					.relativize(FabricLoader.getInstance().getConfigDir())
					.resolve(MOD_ID + ".toml");

	private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static final FileConfig CONFIG = FileConfig.builder(CONFIG_FILE).build();
	private static final ConfigSpec CONFIG_SPEC = new ConfigSpec();

	static {
		CONFIG_SPEC.define(ARMOR, DEFAULT_ARMOR);
		CONFIG_SPEC.define(ELYTRA, DEFAULT_ELYTRA);
		CONFIG_SPEC.define(SKULLS, DEFAULT_SKULLS);
		CONFIG_SPEC.define(PUMPKINS, DEFAULT_PUMPKINS);
		CONFIG_SPEC.define(ANYTHING, DEFAULT_ANYTHING);

		try {
			FileWatcher.defaultInstance().addWatch(CONFIG_FILE, ArmourSoundTweak::reloadConfig);
		} catch (final IOException e) {
			throw new IllegalStateException("Unable to add file watcher", e);
		}

		loadConfig();
	}

	private List<ItemStack> oldEquipment = Collections.emptyList();

	public static boolean armor() {
		return CONFIG.get(ARMOR);
	}

	public static void armor(final boolean armor) {
		CONFIG.set(ARMOR, armor);
	}

	public static boolean elytra() {
		return CONFIG.get(ELYTRA);
	}

	public static void elytra(final boolean elytra) {
		CONFIG.set(ELYTRA, elytra);
	}

	public static boolean skulls() {
		return CONFIG.get(SKULLS);
	}

	public static void skulls(final boolean skulls) {
		CONFIG.set(SKULLS, skulls);
	}

	public static boolean pumpkins() {
		return CONFIG.get(PUMPKINS);
	}

	public static void pumpkins(final boolean pumpkins) {
		CONFIG.set(PUMPKINS, pumpkins);
	}

	public static boolean anything() {
		return CONFIG.get(ANYTHING);
	}

	public static void anything(final boolean anything) {
		CONFIG.set(ANYTHING, anything);
	}

	public static void saveConfig() {
		LOGGER.debug("Saving config to file {}", CONFIG_FILE);
		CONFIG.save();
	}

	private static void loadConfig0() {
		CONFIG.load();

		if (CONFIG_SPEC.correct(CONFIG, ArmourSoundTweak::logCorrections) > 0) {
			saveConfig();
		}
	}

	private static void loadConfig() {
		LOGGER.debug("Loading config from file {}", CONFIG_FILE);
		loadConfig0();
	}

	private static void reloadConfig() {
		LOGGER.debug("Reloading config from file {}", CONFIG_FILE);
		loadConfig0();
	}

	private static @Nullable SoundEvent getEquipSound(final ItemStack itemStack) {
		final var item = itemStack.getItem();

		if ((armor() && (item instanceof ArmorItem)) || (elytra() && (item.getComponents().contains(DataComponentTypes.GLIDER)))) {
			return item.getComponents().get(DataComponentTypes.EQUIPPABLE).equipSound().value();
		}

		if ((pumpkins() && (item == Items .CARVED_PUMPKIN)) || anything() || (skulls() && isSkull(item))) {
			return SoundEvents.ITEM_ARMOR_EQUIP_GENERIC.value();
		}

		return null;
	}

	private static boolean isSkull(final Item item) {
		return (item instanceof BlockItem) && (((BlockItem) item).getBlock() instanceof AbstractSkullBlock);
	}

	private static void logCorrections(
			final ConfigSpec.CorrectionAction action,
			final List<String> path,
			final @org.jetbrains.annotations.Nullable Object incorrectValue,
			final @org.jetbrains.annotations.Nullable Object correctedValue) {
		switch (action) {
			case REPLACE -> LOGGER.debug("Defaulting invalid value '{} = {}' to '{}'",
					String.join(".", path), incorrectValue, correctedValue);
			case REMOVE -> LOGGER.debug("Removing unrecognized option '{} = {}'",
					String.join(".", path), incorrectValue);
		}
	}

	@Override
	public void onInitializeClient() {
		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			final @org.jetbrains.annotations.Nullable ClientPlayerEntity player = client.player;

			if ((player != null) && (player.getWorld() != null) && player.getWorld().isClient) {
				final List<ItemStack> equipment = new ArrayList<>(4);

				for (final var stack : player.getArmorItems()) {
					equipment.add(stack.copy());
				}

				if (client.currentScreen instanceof InventoryScreen || client.currentScreen instanceof CreativeInventoryScreen) {
					final var newEquipment = equipment.iterator();
					final var oldEquipment = this.oldEquipment.iterator();

					while (oldEquipment.hasNext() && newEquipment.hasNext()) {
						final var newItem = newEquipment.next();
						final var oldItem = oldEquipment.next();

						if (!ItemStack.areEqual(newItem, oldItem)) {
							final @Nullable SoundEvent sound = getEquipSound(oldItem);

							if (sound != null) {
								player.playSound(sound, 1.0F, 1.0F);
							}
						}
					}
				}

				this.oldEquipment = equipment;
			}
		});
	}
}