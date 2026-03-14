package net.liukrast.repackaged;

import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import com.simibubi.create.foundation.utility.CreateLang;
import net.liukrast.deployer.lib.helper.ClientRegisterHelpers;
import net.liukrast.deployer.lib.helper.GuiRenderingHelpers;
import net.liukrast.deployer.lib.logistics.board.GaugeSlot;
import net.liukrast.repackaged.content.fluid.*;
import net.liukrast.repackaged.registry.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;

import java.util.List;

@Mod(value = RepackagedConstants.MOD_ID, dist = Dist.CLIENT)
public class RepackagedClient {
    public RepackagedClient(IEventBus eventBus, ModContainer container) {
        eventBus.addListener(RepackagedBlockEntityTypes::registerRenderers);
        eventBus.addListener(RepackagedBlockEntityTypes::fmlClientSetup);
        container.registerConfig(ModConfig.Type.CLIENT, RepackagedConfig.Client.SPEC);
        RepackagedPartialModels.init();
        eventBus.register(this);
    }

    @SubscribeEvent
    private void registerClientExtensions(RegisterClientExtensionsEvent event) {
        RepackagedItems.bottleStream()
                .forEach(pack ->
                        event.registerItem(SimpleCustomRenderer.create(pack.get(), new BottleRenderer()), pack)
                );
    }

    @SubscribeEvent
    private void fMLClientSetup(FMLClientSetupEvent event) {
        ClientRegisterHelpers.registerPackageVisual4ChainConveyor(BottleChainVisual::new);
        ClientRegisterHelpers.registerPackageVisual4Entity(BottleVisual::new,
                box -> {
                    if(RepackagedItems.STANDARD_BOTTLES.stream().noneMatch(def -> box.box.is(def.get()))) return false;
                    return box.box.getOrDefault(RepackagedDataComponents.BOTTLE_CONTENTS, SimpleFluidContent.EMPTY).copy().isEmpty();
                });
        ClientRegisterHelpers.registerStockKeeperTab(FluidTabScreen::new);
        ClientRegisterHelpers.registerRedstoneRequesterTab(FluidRequesterTab::new);
        ClientRegisterHelpers.registerGaugeSlot(RepackagedPanels.FLUID.get(), new GaugeSlot<FluidStack, FluidPanelBehaviour>() {
            @Override
            public FluidStack collectData(FluidPanelBehaviour fluidPanelBehaviour) {
                return fluidPanelBehaviour.getStack().copy();
            }

            @Override
            public boolean isEmpty(FluidStack fluidStack) {
                return fluidStack.isEmpty();
            }

            @Override
            public void renderInputSlot(GuiGraphics graphics, FluidStack stack, int mouseX, int mouseY, int x, int y, boolean restocker, Font font) {
                if(!stack.isEmpty()) GuiRenderingHelpers.renderFluid(graphics, stack.copyWithAmount(1000), x+1, y+1, 14,14);
                if(stack.getAmount() > 999) {
                    var ms = graphics.pose();
                    ms.pushPose();
                    String s = String.valueOf(stack.getAmount()/1000);
                    ms.translate(0.0F, 0.0F, 200.0F);
                    graphics.drawString(font, s, x + 19 - 2 - font.width(s), y + 6 + 3, 16777215, true);
                    ms.popPose();
                }
                if(mouseX < x - 2 || mouseX >= x - 2 + 20 || mouseY < y -2 || mouseY >= y -2 + 20)
                    return;

                if(stack.isEmpty()) {
                    graphics.renderComponentTooltip(font, List.of(CreateLang.translate("gui.factory_panel.empty_panel")
                                            .color(ScrollInput.HEADER_RGB)
                                            .component(),
                                    CreateLang.translate("gui.factory_panel.left_click_disconnect")
                                            .style(ChatFormatting.DARK_GRAY)
                                            .style(ChatFormatting.ITALIC)
                                            .component()),
                            mouseX, mouseY);
                    return;
                }

                if(restocker) {
                    graphics.renderComponentTooltip(font,
                            List.of(CreateLang.translate("gui.factory_panel.sending_item", CreateLang.builder().add(stack.getHoverName().copy())
                                                    .string())
                                            .color(ScrollInput.HEADER_RGB)
                                            .component(),
                                    CreateLang.translate("gui.factory_panel.sending_item_tip")
                                            .style(ChatFormatting.GRAY)
                                            .component(),
                                    CreateLang.translate("gui.factory_panel.sending_item_tip_1")
                                            .style(ChatFormatting.GRAY)
                                            .component()),
                            mouseX, mouseY);
                    return;
                }

                graphics.renderComponentTooltip(font,
                        List.of(CreateLang.translate("gui.factory_panel.sending_item", CreateLang.builder().add(stack.getHoverName().copy())
                                                .add(CreateLang.text(" x" + stack.getAmount() + "Mb"))
                                                .string())
                                        .color(ScrollInput.HEADER_RGB)
                                        .component(),
                                CreateLang.translate("gui.factory_panel.scroll_to_change_amount")
                                        .style(ChatFormatting.DARK_GRAY)
                                        .style(ChatFormatting.ITALIC)
                                        .component(),
                                CreateLang.translate("gui.factory_panel.left_click_disconnect")
                                        .style(ChatFormatting.DARK_GRAY)
                                        .style(ChatFormatting.ITALIC)
                                        .component()),
                        mouseX, mouseY);
            }

            @Override
            public void renderOutputSlot(GuiGraphics graphics, FluidStack stack, int mouseX, int mouseY, int x, int y, Font font) {
                if(!stack.isEmpty()) GuiRenderingHelpers.renderFluid(graphics, stack.copyWithAmount(1000), x+1, y+1, 14,14);
                if(stack.getAmount() > 999) {
                    var ms = graphics.pose();
                    ms.pushPose();
                    String s = String.valueOf(stack.getAmount()/1000);
                    ms.translate(0.0F, 0.0F, 200.0F);
                    graphics.drawString(font, s, x + 19 - 2 - font.width(s), y + 6 + 3, 16777215, true);
                    ms.popPose();
                }
                if (mouseX >= x - 1 && mouseX < x - 1 + 18 && mouseY >= y - 1
                        && mouseY < y - 1 + 18) {
                    MutableComponent c1 = CreateLang
                            .translate("gui.factory_panel.expected_output", CreateLang.builder().add(stack.getHoverName().copy())
                                    .add(CreateLang.text(" x" + stack.getAmount() + "Mb"))
                                    .string())
                            .color(ScrollInput.HEADER_RGB)
                            .component();
                    MutableComponent c2 = CreateLang.translate("gui.factory_panel.expected_output_tip")
                            .style(ChatFormatting.GRAY)
                            .component();
                    MutableComponent c3 = CreateLang.translate("gui.factory_panel.expected_output_tip_1")
                            .style(ChatFormatting.GRAY)
                            .component();
                    MutableComponent c4 = CreateLang.translate("gui.factory_panel.expected_output_tip_2")
                            .style(ChatFormatting.DARK_GRAY)
                            .style(ChatFormatting.ITALIC)
                            .component();
                    graphics.renderComponentTooltip(font, List.of(c1, c2, c3, c4),
                            mouseX, mouseY);
                }
            }
        });
    }
}
