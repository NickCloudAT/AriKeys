package eu.asangarin.arikeys.screen;

import eu.asangarin.arikeys.AriKeys;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.Identifier;

@Getter
@Setter
public class AriKeysButton extends ImageButton {
	private boolean active = true;

	private static final WidgetSprites BUTTON_TEXTURES = new WidgetSprites(
		Identifier.fromNamespaceAndPath(AriKeys.MOD_ID, "arikeys/ak_button_enabled"),
		Identifier.fromNamespaceAndPath(AriKeys.MOD_ID, "arikeys/ak_button_disabled"),
		Identifier.fromNamespaceAndPath(AriKeys.MOD_ID, "arikeys/ak_button_focused")
	);

	public AriKeysButton(Screen parent) {
		super(20, 20, BUTTON_TEXTURES, action -> Minecraft.getInstance().setScreenAndShow(new AriKeysOptions(parent)), CommonComponents.EMPTY);
	}

	/*@Override
	public void onPress() {
		if(this.isActive())
			onPress.onPress(this);
	}*/

	@Override
	public void extractContents(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
		Identifier identifier = this.sprites.get(this.isActive(), this.isHoveredOrFocused());
		context.blitSprite(RenderPipelines.GUI_TEXTURED, identifier, this.getX(), this.getY(), this.width, this.height);
	}
}
