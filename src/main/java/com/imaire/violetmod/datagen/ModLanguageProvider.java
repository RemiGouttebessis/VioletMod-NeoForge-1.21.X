package com.imaire.violetmod.datagen;

import com.imaire.violetmod.VioletMod;
import com.imaire.violetmod.registry.ModBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ModLanguageProvider extends LanguageProvider {

    private final String locale;

    public ModLanguageProvider(PackOutput output, String locale) {
        super(output, VioletMod.MOD_ID, locale);
        this.locale = locale;
    }

    @Override
    protected void addTranslations() {
        if (locale.equals("en_us")) {
            addEnglish();
        } else if (locale.equals("fr_fr")) {
            addFrench();
        }
    }

    private void addEnglish() {
        addBlock(ModBlocks.LOGICAL_COMPUTER, "Logical Computer");
        addBlock(ModBlocks.VIOLET_EXTRACTOR, "Violet Dust Extractor");

        add("itemGroup.violetmod", "Violet Mod");
        add("tooltip.violetmod.energy", "Energy");
        add("recipe.violetmod.violet_extracting", "Violet Extracting");

        add("config.violetmod.logical_computer.capacity",       "Logical Computer Capacity");
        add("config.violetmod.logical_computer.max_receive",    "Logical Computer Max Receive");
        add("config.violetmod.logical_computer.energy_per_tick","Logical Computer Energy Per Tick");
        add("config.violetmod.logical_computer.max_progress",   "Logical Computer Max Progress");
        add("config.violetmod.violet_extractor.capacity",       "Violet Extractor Capacity");
        add("config.violetmod.violet_extractor.max_receive",    "Violet Extractor Max Receive");
        add("config.violetmod.violet_extractor.energy_per_tick","Violet Extractor Energy Per Tick");
        add("config.violetmod.violet_extractor.max_progress",   "Violet Extractor Max Progress");
    }

    private void addFrench() {
        addBlock(ModBlocks.LOGICAL_COMPUTER, "Ordinateur logique");
        addBlock(ModBlocks.VIOLET_EXTRACTOR, "Extracteur de poudre violette");

        add("itemGroup.violetmod", "Violet Mod");
        add("tooltip.violetmod.energy", "Énergie");
        add("recipe.violetmod.violet_extracting", "Extraction violette");

        add("config.violetmod.logical_computer.capacity",       "Capacité de l'ordinateur logique");
        add("config.violetmod.logical_computer.max_receive",    "Réception max de l'ordinateur logique");
        add("config.violetmod.logical_computer.energy_per_tick","Énergie consommée par tick");
        add("config.violetmod.logical_computer.max_progress",   "Progression max de l'ordinateur logique");
        add("config.violetmod.violet_extractor.capacity",       "Capacité de l'extracteur violet");
        add("config.violetmod.violet_extractor.max_receive",    "Réception max de l'extracteur violet");
        add("config.violetmod.violet_extractor.energy_per_tick","Énergie consommée par tick de l'extracteur violet");
        add("config.violetmod.violet_extractor.max_progress",   "Progression max de l'extracteur violet");
    }
}
