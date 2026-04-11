package com.imaire.violetmod.registry;

import com.imaire.violetmod.VioletMod;
import com.imaire.violetmod.common.menu.VioletExtractorMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ModMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(Registries.MENU, VioletMod.MOD_ID);

    public static final Supplier<MenuType<VioletExtractorMenu>> VIOLET_EXTRACTOR_MENU =
            MENU_TYPES.register("violet_extractor",
                    () -> IMenuTypeExtension.create(VioletExtractorMenu::new));

    private ModMenuTypes() {}
}
