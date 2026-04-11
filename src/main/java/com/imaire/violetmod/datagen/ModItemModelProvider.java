package com.imaire.violetmod.datagen;

import com.imaire.violetmod.VioletMod;
import com.imaire.violetmod.registry.ModBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {

    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, VioletMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        // Block items use their block model as parent
        ModelFile logicalComputerModel = getExistingFile(modLoc("block/logical_computer_idle"));
        withExistingParent(
                ModBlocks.LOGICAL_COMPUTER.get().asItem().toString(),
                logicalComputerModel.getLocation()
        );

        ModelFile violetExtractorModel = getExistingFile(modLoc("block/violet_extractor_idle"));
        withExistingParent(
                ModBlocks.VIOLET_EXTRACTOR.get().asItem().toString(),
                violetExtractorModel.getLocation()
        );
    }
}
