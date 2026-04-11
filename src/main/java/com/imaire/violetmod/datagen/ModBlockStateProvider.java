package com.imaire.violetmod.datagen;

import com.imaire.violetmod.VioletMod;
import com.imaire.violetmod.common.block.LogicalComputerBlock;
import com.imaire.violetmod.common.block.VioletExtractorBlock;
import com.imaire.violetmod.common.block.state.LogicalComputerVisualState;
import com.imaire.violetmod.common.block.state.VioletExtractorVisualState;
import com.imaire.violetmod.registry.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, VioletMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        registerLogicalComputer();
        registerVioletExtractor();
    }

    private void registerLogicalComputer() {
        ResourceLocation texture = modLoc("block/logical_computer");

        ModelFile powerless = models().cubeAll("logical_computer_powerless", texture);
        ModelFile idle      = models().cubeAll("logical_computer_idle", texture);
        ModelFile running   = models().cubeAll("logical_computer_running", texture);

        VariantBlockStateBuilder builder = getVariantBuilder(ModBlocks.LOGICAL_COMPUTER.get());

        for (Direction dir : HorizontalDirectionalBlock.FACING.getPossibleValues()) {
            int yRot = dirToYRot(dir);
            for (LogicalComputerVisualState vs : LogicalComputerVisualState.values()) {
                ModelFile model = switch (vs) {
                    case POWERLESS -> powerless;
                    case IDLE      -> idle;
                    case RUNNING   -> running;
                };
                builder.partialState()
                        .with(HorizontalDirectionalBlock.FACING, dir)
                        .with(LogicalComputerBlock.VISUAL_STATE, vs)
                        .modelForState()
                        .modelFile(model)
                        .rotationY(yRot)
                        .addModel();
            }
        }

        simpleBlockItem(ModBlocks.LOGICAL_COMPUTER.get(), idle);
    }

    private void registerVioletExtractor() {
        // Base model holds the geometry; thin override models set the texture per state.
        ModelFile base = models().getExistingFile(modLoc("block/violet_extractor_base"));

        VariantBlockStateBuilder builder = getVariantBuilder(ModBlocks.VIOLET_EXTRACTOR.get());

        for (Direction dir : HorizontalDirectionalBlock.FACING.getPossibleValues()) {
            int yRot = dirToYRot(dir);
            for (VioletExtractorVisualState vs : VioletExtractorVisualState.values()) {
                String textureName = "violetmod:block/violet_extractor_" + vs.getSerializedName();
                String modelName   = "violet_extractor_" + vs.getSerializedName();

                ModelFile model = models().withExistingParent(modelName, base.getLocation())
                        .texture("texture", textureName);

                builder.partialState()
                        .with(VioletExtractorBlock.VISUAL_STATE, vs)
                        .with(HorizontalDirectionalBlock.FACING, dir)
                        .modelForState()
                        .modelFile(model)
                        .rotationY(yRot)
                        .addModel();
            }
        }
    }

    private static int dirToYRot(Direction dir) {
        return switch (dir) {
            case EAST  -> 90;
            case SOUTH -> 180;
            case WEST  -> 270;
            default    -> 0;
        };
    }
}
