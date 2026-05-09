package eu.asangarin.arikeys.mixin;

import eu.asangarin.arikeys.AriKeys;
import eu.asangarin.arikeys.screen.AriKeysButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.controls.ControlsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// TODO(Ravel): can not resolve target class ControlsOptionsScreen
@Mixin(ControlsScreen.class)
public class AKOptionsScreen extends Screen {
	@Unique
	private final AriKeysButton arikeys$ak_button = new AriKeysButton(this);

	protected AKOptionsScreen(Component title) {
		super(title);
	}

	// TODO(Ravel): no target class
    @Inject(method = "addOptions", at = @At("TAIL"))
	protected void initAriKeysButton(CallbackInfo ci) {
		if (minecraft == null || minecraft.isLocalServer()) return;

		addRenderableWidget(arikeys$ak_button);
		arikeys$refresh();

		if(AriKeys.getKeybinds().isEmpty()) {
			arikeys$ak_button.setActive(false);
			arikeys$ak_button.setTooltip(Tooltip.create(Component.translatable("arikeys.disabled_message")));
		}
	}

	@Override
	protected void repositionElements() {
		super.repositionElements();
		if (minecraft != null && !minecraft.isLocalServer())
			arikeys$refresh();
	}

	@Unique
	private void arikeys$refresh() {
		arikeys$ak_button.setPosition(this.width / 2 + 158, 37);
	}
}
