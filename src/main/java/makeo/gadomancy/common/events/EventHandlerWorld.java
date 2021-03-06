package makeo.gadomancy.common.events;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import makeo.gadomancy.common.Gadomancy;
import makeo.gadomancy.common.blocks.tiles.TileNodeManipulator;
import makeo.gadomancy.common.blocks.tiles.TileStickyJar;
import makeo.gadomancy.common.registration.RegisteredBlocks;
import makeo.gadomancy.common.registration.RegisteredIntegrations;
import makeo.gadomancy.common.registration.RegisteredItems;
import makeo.gadomancy.common.utils.GolemEnumHelper;
import makeo.gadomancy.common.utils.JarMultiblockHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.INode;
import thaumcraft.common.blocks.BlockMagicalLog;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.tiles.TileJarFillable;
import thaumcraft.common.tiles.TileJarNode;
import thaumcraft.common.tiles.TileOwned;
import thaumcraft.common.tiles.TileWarded;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Gadomancy Mod
 * Gadomancy is Open Source and distributed under the
 * GNU LESSER GENERAL PUBLIC LICENSE
 * for more read the LICENSE file
 * <p/>
 * Created by makeo @ 05.07.2015 13:20
 */
public class EventHandlerWorld {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void on(EntityEvent.EntityConstructing e) {
        if (e.entity instanceof EntityPlayer) {
            //e.entity.registerExtendedProperties(Gadomancy.MODID, new ExtendedPlayerProperties((EntityPlayer) e.entity));
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void on(WorldEvent.Load e) {
        if (!e.world.isRemote && e.world.equals(MinecraftServer.getServer().getEntityWorld())) {
            Gadomancy.loadModData();

            GolemEnumHelper.validateSavedMapping();
            GolemEnumHelper.reorderEnum();
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void on(WorldEvent.Unload e) {
        if (!e.world.isRemote && e.world.equals(MinecraftServer.getServer().getEntityWorld())) {
            Gadomancy.unloadModData();
        }
    }


    private Map<EntityPlayer, Integer> interacts = null;

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void on(BlockEvent.PlaceEvent e) {
        if (e.isCanceled()) {
            if (interacts != null)
                interacts.remove(e.player);
        } else {
            if (!e.world.isRemote && isStickyJar(e.itemInHand)) {
                TileEntity parent = e.world.getTileEntity(e.x, e.y, e.z);
                if (parent instanceof TileJarFillable) {
                    int metadata = e.world.getBlockMetadata(e.x, e.y, e.z);
                    e.world.setBlock(e.x, e.y, e.z, RegisteredBlocks.blockStickyJar, metadata, 2);

                    TileEntity tile = e.world.getTileEntity(e.x, e.y, e.z);
                    if (tile instanceof TileStickyJar) {
                        Integer sideHit = interacts.get(e.player);
                        ((TileStickyJar) tile).init((TileJarFillable) parent, e.placedBlock, metadata,
                                ForgeDirection.getOrientation(sideHit == null ? 1 : sideHit).getOpposite());
                        RegisteredBlocks.blockStickyJar.onBlockPlacedBy(e.world, e.x, e.y, e.z, e.player, e.itemInHand);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onBreak(BlockEvent.BreakEvent event) {
        if(!event.world.isRemote) {
            if(event.block == RegisteredBlocks.blockNodeManipulator) {
                TileEntity te = event.world.getTileEntity(event.x, event.y, event.z);
                if(te != null && te instanceof TileNodeManipulator) {
                    if(((TileNodeManipulator) te).isInMultiblock())
                        ((TileNodeManipulator) te).breakMultiblock();
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void on(PlayerInteractEvent e) {
        if (!e.world.isRemote && e.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK
                && isStickyJar(e.entityPlayer.getHeldItem())) {
            if (interacts == null) {
                interacts = new HashMap<EntityPlayer, Integer>();
            }
            interacts.put(e.entityPlayer, e.face);
        }

        if (e.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            ItemStack i = e.entityPlayer.getHeldItem();
            if (i != null && (i.getItem() instanceof ItemWandCasting)) {
                JarMultiblockHandler.handleWandInteract(e.world, e.x, e.y, e.z, e.entityPlayer, i);
            }
        }
    }

    private boolean isStickyJar(ItemStack stack) {
        return stack != null && RegisteredItems.isStickyableJar(stack)
                && stack.hasTagCompound() && stack.stackTagCompound.getBoolean("isStickyJar");
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void on(ItemTooltipEvent e) {
        if (e.toolTip.size() > 0 && e.itemStack.hasTagCompound()) {
            if (e.itemStack.stackTagCompound.getBoolean("isStickyJar")) {
                e.toolTip.add(1, "\u00a7a" + StatCollector.translateToLocal("gadomancy.lore.stickyjar"));
            }
        }
    }
}
