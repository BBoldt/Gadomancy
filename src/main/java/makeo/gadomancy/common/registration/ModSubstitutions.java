package makeo.gadomancy.common.registration;

import com.google.common.collect.BiMap;
import cpw.mods.fml.common.registry.FMLControlledNamespacedRegistry;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import makeo.gadomancy.common.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.common.blocks.BlockAiryItem;
import thaumcraft.common.config.ConfigBlocks;

import java.util.List;

/**
 * This class is part of the Gadomancy Mod
 * Gadomancy is Open Source and distributed under the
 * GNU LESSER GENERAL PUBLIC LICENSE
 * for more read the LICENSE file
 *
 * Created by HellFirePvP @ 24.10.2015 16:48
 */
public class ModSubstitutions {

    public static void preInit() {
        try {
            ItemBlock item = (ItemBlock) Item.getItemFromBlock(ConfigBlocks.blockAiry);

            item.field_150939_a = RegisteredBlocks.blockNode;

            //Hacky way
            FMLControlledNamespacedRegistry<Block> registry = GameData.getBlockRegistry();
            registry.underlyingIntegerMap.field_148749_a.put(RegisteredBlocks.blockNode, Block.getIdFromBlock(ConfigBlocks.blockAiry));
            registry.underlyingIntegerMap.field_148748_b.set(Block.getIdFromBlock(ConfigBlocks.blockAiry), RegisteredBlocks.blockNode);
            ((BiMap)registry.field_148758_b).forcePut(RegisteredBlocks.blockNode, registry.field_148758_b.get(ConfigBlocks.blockAiry));

            registry.underlyingIntegerMap.field_148749_a.remove(ConfigBlocks.blockAiry);


            ConfigBlocks.blockAiry = RegisteredBlocks.blockNode;
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    public static void init() {

    }

    public static void postInit() {
        CommonProxy.unregisterWandHandler("Thaumcraft", Blocks.glass, -1);
    }
}
