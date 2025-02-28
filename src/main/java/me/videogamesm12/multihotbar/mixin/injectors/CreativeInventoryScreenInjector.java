/*
 * Copyright (c) 2021 Video
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.videogamesm12.multihotbar.mixin.injectors;

import me.videogamesm12.multihotbar.mixin.accessors.CreativeInventoryScreenAccessor;
import me.videogamesm12.multihotbar.mixin.accessors.HandledScreenAccessor;
import me.videogamesm12.multihotbar.util.Util;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * CreativeInventoryScreenInjector - Adds a few buttons to the CreativeInventoryScreen.
 * @author Video
 */
@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryScreenInjector extends AbstractInventoryScreen<CreativeInventoryScreen.CreativeScreenHandler>
{
    @Shadow protected abstract void setSelectedTab(ItemGroup group);

    ButtonWidget button;
    ButtonWidget backupButton;
    ButtonWidget nextButton;

    public CreativeInventoryScreenInjector(CreativeInventoryScreen.CreativeScreenHandler screenHandler, PlayerInventory playerInventory, Text text)
    {
        super(screenHandler, playerInventory, text);
    }

    @Inject(method = "init", at = @At("RETURN"))
    public void injectInit(CallbackInfo ci)
    {
        //int x = ((HandledScreenAccessor) this).getX() + 138;
        int x = ((HandledScreenAccessor) this).getX() + 159;
        int y = ((HandledScreenAccessor) this).getY() + 4;
        //
        button = new ButtonWidget(x - 16, y, 16, 12, new LiteralText("←"), (buttonWidget) ->
        {
            Util.previousPage();
            setSelectedTab(ItemGroup.HOTBAR);
        });
        // One of these would be a good icon, but I'm not sure which - 💾 ⚙ ✍
        backupButton = new ButtonWidget(x, y, 16, 12, new LiteralText("✍"), (buttonWidget) ->
        {
            if (!Util.backupInProgress)
            {
                Util.backupCurrentHotbar();
            }
        });
        nextButton = new ButtonWidget(x + 16, y, 16, 12, new LiteralText("→"), (buttonWidget) ->
        {
            Util.nextPage();
            setSelectedTab(ItemGroup.HOTBAR);
        });
        //
        button.visible = false;
        backupButton.visible = false;
        nextButton.visible = false;
        //
        addButton(button);
        addButton(backupButton);
        addButton(nextButton);
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void injectRenderHead(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci)
    {
        if (((CreativeInventoryScreenAccessor) this).getSelectedTab() == ItemGroup.HOTBAR.getIndex())
        {
            button.active = Util.getPage() > 0;
            backupButton.active = Util.hotbarFileExists() && !Util.backupInProgress;
            nextButton.active = Util.getPage() != 2147483647;
            //
            button.visible = true;
            backupButton.visible = true;
            nextButton.visible = true;
        }
        else
        {
            button.visible = false;
            backupButton.visible = false;
            nextButton.visible = false;
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    public void injectRenderReturn(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci)
    {
        renderButtonToolTips(matrices, mouseX, mouseY);
    }

    // I don't care if this looks like shit. Hell, I don't even care if it's a hackish solution. The fact of the matter
    // is it works, and that's all I care about.
    public void renderButtonToolTips(MatrixStack matrices, int mouseX, int mouseY)
    {
        if (!(((CreativeInventoryScreenAccessor) this).getSelectedTab() == ItemGroup.HOTBAR.getIndex()))
        {
            return;
        }

        if (button.isMouseOver(mouseX, mouseY))
        {
            this.renderTooltip(matrices, new TranslatableText("tooltip.previous_page_button"), mouseX, mouseY);
        }
        else if (backupButton.isMouseOver(mouseX, mouseY))
        {
            if (!Util.hotbarFileExists())
            {
                this.renderTooltip(matrices, new TranslatableText("tooltip.hotbar_is_empty").formatted(Formatting.RED), mouseX, mouseY);
            }
            else if (Util.backupInProgress)
            {
                this.renderTooltip(matrices, new TranslatableText("tooltip.backup_in_progress").formatted(Formatting.RED), mouseX, mouseY);
            }
            else
            {
                this.renderTooltip(matrices, new TranslatableText("tooltip.backup_hotbar_button"), mouseX, mouseY);
            }
        }
        else if (nextButton.isMouseOver(mouseX, mouseY))
        {
            this.renderTooltip(matrices, new TranslatableText("tooltip.next_page_button"), mouseX, mouseY);
        }
    }
}
