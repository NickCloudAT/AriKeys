package eu.asangarin.arikeys.screen;

import eu.asangarin.arikeys.AriKey;
import eu.asangarin.arikeys.AriKeys;
import eu.asangarin.arikeys.util.AriKeysIO;
import eu.asangarin.arikeys.util.ModifierKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.HashSet;
import java.util.Optional;

public class AriKeysOptions extends OptionsSubScreen {
	public AriKey focusedMKey;
	private AriKeyControlsListWidget keyBindingListWidget;
	private Button resetButton;

	public AriKeysOptions(Screen parent) {
		super(parent, Minecraft.getInstance().options, Component.translatable("arikeys.controls.title"));
	}

	@Override
	protected void addContents() {  // was: initBody
		this.keyBindingListWidget = this.layout.addToContents(  // was: addBody, client
				new AriKeyControlsListWidget(this, this.minecraft));
	}

	@Override
	protected void addOptions() {}

	@Override
	protected void addFooter() {  // was: initFooter
		this.resetButton = Button.builder(Component.translatable("controls.resetAll"), (button) -> {
			for (AriKey keyBinding : AriKeys.getKeybinds()) {
				keyBinding.setBoundKey(keyBinding.getKeyCode(), false);
				keyBinding.resetBoundModifiers();
			}
			KeyMapping.resetMapping();
		}).build();
		LinearLayout footer = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));  // was: addFooter
		footer.addChild(this.resetButton);  // was: add
		footer.addChild(Button.builder(CommonComponents.GUI_DONE, (button) -> this.onClose()).build());  // was: DONE, close()
	}

	@Override
	protected void repositionElements() {  // was: refreshWidgetPositions
		this.layout.arrangeElements();
		this.keyBindingListWidget.updateSize(this.width, this.layout);  // was: position
	}

	@Override
	public boolean keyPressed(KeyEvent input) {
		if (focusedMKey != null) {
			if (input.isEscape()) {
				focusedMKey.setBoundKey(InputConstants.UNKNOWN, false);
				focusedMKey.setBoundModifiers(new HashSet<>());
			} else if (isModifier(input.key())) {
				return super.keyPressed(input);
			} else {
				focusedMKey.setBoundKey(InputConstants.getKey(input), true);
			}
			AriKeysIO.save();
			focusedMKey = null;
			KeyMapping.resetMapping();
			return true;
		} else {
			return super.keyPressed(input);
		}
	}

	private boolean isModifier(int code) {
		for (ModifierKey modifier : ModifierKey.ALL)
			if (modifier.getCode() == code) return true;
		return false;
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent click, boolean doubleClick) {
		Optional<GuiEventListener> optional = this.getChildAt(click.x(), click.y());
		if (optional.isEmpty()) return false;

		GuiEventListener element = optional.get();
		if (element.mouseClicked(click, doubleClick) && element.shouldTakeFocusAfterInteraction()) {
			this.setFocused(element);
			if (click.button() == 0) this.setDragging(true);
		}
		return true;
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
		super.extractRenderState(context, mouseX, mouseY, delta);

		boolean canReset = false;
		for (AriKey ariKey : AriKeys.getKeybinds()) {
			if (ariKey.hasChanged()) {
				canReset = true;
				break;
			}
		}
		this.resetButton.active = canReset;
	}
}