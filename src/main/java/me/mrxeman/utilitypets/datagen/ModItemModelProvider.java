package me.mrxeman.utilitypets.datagen;

import me.mrxeman.utilitypets.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import static me.mrxeman.utilitypets.Utilitypets.MOD_ID;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        assert ModItems.LUCAS_SPAWN_EGG.getId() != null;
        withExistingParent(ModItems.LUCAS_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
        assert ModItems.FURNY_SPAWN_EGG.getId() != null;
        withExistingParent(ModItems.FURNY_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));
    }
}
