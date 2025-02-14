package mr_chumbucket.armoursoundtweak;

import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Environment(EnvType.CLIENT)
public final class ArmourSoundTweakClient implements ClientModInitializer {
	public static final String MOD_ID = "armoursoundtweak";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private List<ItemStack> oldEquipment = Collections.emptyList();

	private static @Nullable SoundEvent getEquipSound(final ItemStack itemStack) {
		final var item = itemStack.getItem();

		if ((ArmourSoundTweakConfig.armour && (item instanceof ArmorItem)) || (ArmourSoundTweakConfig.elytra && (item instanceof ElytraItem))) {
			return ((Equipment) item).getEquipSound().value();
		}

		if ((ArmourSoundTweakConfig.pumpkins && (item == Items .CARVED_PUMPKIN)) || (ArmourSoundTweakConfig.skulls && isSkull(item))) {
			return SoundEvents.ITEM_ARMOR_EQUIP_GENERIC.value();
		}

		return null;
	}

	private static boolean isSkull(final Item item) {
		return (item instanceof BlockItem) && (((BlockItem) item).getBlock() instanceof AbstractSkullBlock);
	}

	@Override
	public void onInitializeClient() {
		LOGGER.info("Starting Armour Sound Tweak Updated!");
		MidnightConfig.init(MOD_ID, ArmourSoundTweakConfig.class);

		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			final @Nullable ClientPlayerEntity player = client.player;

			if ((player != null) && (player.getWorld() != null) && player.getWorld().isClient) {
				final List<ItemStack> equipment = new ArrayList<>(4);

				for (final var stack : player.getArmorItems()) {
					equipment.add(stack.copy());
				}

				if (client.currentScreen instanceof AbstractInventoryScreen<?>) {
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