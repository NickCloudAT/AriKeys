package eu.asangarin.arikeys.screen;

import com.google.common.collect.ImmutableList;
import eu.asangarin.arikeys.AriKey;
import eu.asangarin.arikeys.AriKeys;
import eu.asangarin.arikeys.util.AriKeysIO;
import eu.asangarin.arikeys.util.ModifierKey;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.CommonColors;


import java.util.Collections;
import java.util.List;

public class AriKeyControlsListWidget extends ContainerObjectSelectionList<AriKeyControlsListWidget.Entry> {
	final AriKeysOptions parent;
	int maxKeyNameLength;

	public AriKeyControlsListWidget(AriKeysOptions parent, Minecraft client) {
		super(client, parent.width + 45, parent.height - 52, 20, 20);
		this.parent = parent;
		String category = null;

		for (AriKey ariKey : AriKeys.getCategorySortedKeybinds()) {
			String keyCat = ariKey.getCategory();
			if (!keyCat.equals(category)) {
				category = keyCat;
				this.addEntry(new CategoryEntry(Component.literal(keyCat)));
			}

			Component text = Component.literal(ariKey.getName());
			int i = client.font.width(text);
			if (i > this.maxKeyNameLength) {
				this.maxKeyNameLength = i;
			}

			this.addEntry(new KeyBindingEntry(ariKey, text));
		}

	}

	public int getRowWidth() {
		return super.getRowWidth() + 32;
	}

	public class CategoryEntry extends AriKeyControlsListWidget.Entry {
		final Component text;
		private final int textWidth;

		public CategoryEntry(Component text) {
			this.text = text;
			this.textWidth = AriKeyControlsListWidget.this.minecraft.font.width(this.text);
		}

        public void extractContent(GuiGraphicsExtractor context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            assert minecraft.screen != null;

            // Entry bounds are now accessible via this.getX(), this.getY(), this.getWidth(), this.getHeight()
            int x = this.getX();
            int y = this.getY();
            int entryHeight = this.getHeight();

            int width = (minecraft.screen.width / 2 - this.textWidth / 2);
            int height = y + entryHeight;

            context.text(minecraft.font, this.text, width, height - 9 - 1, CommonColors.WHITE, false);
        }


		public List<? extends GuiEventListener> children() {
			return Collections.emptyList();
		}

		public List<? extends NarratableEntry> narratables() {
			return ImmutableList.of(new NarratableEntry() {
				public NarratableEntry.NarrationPriority narrationPriority() {
					return NarratableEntry.NarrationPriority.HOVERED;
				}

				public void updateNarration(NarrationElementOutput builder) {
					builder.add(NarratedElementType.TITLE, CategoryEntry.this.text);
				}
			});
		}
    }

    public class KeyBindingEntry extends AriKeyControlsListWidget.Entry {
        private final AriKey ariKey;
        private final Component bindingName;
        private final Button editButton;
        private final Button resetButton;

        KeyBindingEntry(AriKey ariKey, Component bindingName) {
            this.ariKey = ariKey;
            this.bindingName = bindingName;

            this.editButton = Button.builder(bindingName, (button) -> AriKeyControlsListWidget.this.parent.focusedMKey = ariKey)
                    .bounds(0, 0, 135, 20)
                    .createNarration(supplier -> ariKey.isUnbound() ? Component.translatable("narrator.controls.unbound", bindingName)
                            : Component.translatable("narrator.controls.bound", bindingName, supplier.get()))
                    .build();

            this.resetButton = Button.builder(Component.translatable("controls.reset"), (button) -> {
                        ariKey.setBoundKey(ariKey.getKeyCode(), false);
                        ariKey.resetBoundModifiers();
                        AriKeysIO.save();
                        KeyMapping.resetMapping();
                    })
                    .bounds(0, 0, 50, 20)
                    .createNarration(supplier -> Component.translatable("narrator.controls.reset", bindingName))
                    .build();
        }

        @Override
        public void extractContent(GuiGraphicsExtractor context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            boolean focused = parent.focusedMKey == this.ariKey;

            int x = this.getX();
            int y = this.getY();
            int entryHeight = this.getHeight();

            int width = x + 20 - maxKeyNameLength;
            int height = y + entryHeight / 2;

            context.text(minecraft.font, this.bindingName, width, height - 9 / 2, CommonColors.WHITE, false);

            this.resetButton.setX(x + 210);
            this.resetButton.setY(y);
            this.resetButton.active = this.ariKey.hasChanged();
            this.resetButton.extractRenderState(context, mouseX, mouseY, deltaTicks);

            this.editButton.setX(x + 65);
            this.editButton.setY(y);

            MutableComponent editMessage = Component.empty();
            for (ModifierKey modifier : this.ariKey.getBoundModifiers()) {
                editMessage.append(Component.translatable(modifier.getTranslationKey()));
                editMessage.append(Component.literal(" + "));
            }
            editMessage.append(this.ariKey.getBoundKeyCode().getDisplayName().plainCopy());
            editMessage = editMessage.copy();

            boolean conflict = false;
            if (!this.ariKey.isUnbound()) {
                for (KeyMapping keyBinding : minecraft.options.keyMappings) {
                    if (keyBinding.saveString().equals(ariKey.getBoundKeyCode().getName())
                            && ariKey.getBoundModifiers().isEmpty()) {
                        conflict = true;
                        break;
                    }
                }
                for (AriKey key : AriKeys.getKeybinds()) {
                    if (!key.equals(ariKey) && key.getBoundKeyCode().equals(ariKey.getBoundKeyCode())) {
                        if (key.testModifiers(ariKey.getBoundModifiers())) {
                            conflict = true;
                            break;
                        }
                    }
                }
            }

            if (focused) {
                this.editButton.setMessage(Component.literal("> ").append(editMessage.withStyle(ChatFormatting.YELLOW)).append(" <").withStyle(ChatFormatting.YELLOW));
            } else if (conflict) {
                this.editButton.setMessage(editMessage.withStyle(ChatFormatting.RED));
            } else {
                this.editButton.setMessage(editMessage);
            }

            this.editButton.extractRenderState(context, mouseX, mouseY, deltaTicks);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of(this.editButton, this.resetButton);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(this.editButton, this.resetButton);
        }

        // ❌ Remove manual mouseClicked / mouseReleased overrides
    }


	public abstract static class Entry extends ContainerObjectSelectionList.Entry<AriKeyControlsListWidget.Entry> {
	}
}
