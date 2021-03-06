package makeo.gadomancy.common.blocks;

import makeo.gadomancy.common.blocks.tiles.TileManipulationFocus;
import makeo.gadomancy.common.blocks.tiles.TileManipulatorPillar;
import makeo.gadomancy.common.registration.RegisteredBlocks;
import makeo.gadomancy.common.registration.RegisteredItems;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thaumcraft.common.config.ConfigBlocks;

import java.util.List;
import java.util.Random;

/**
 * This class is part of the Gadomancy Mod
 * Gadomancy is Open Source and distributed under the
 * GNU LESSER GENERAL PUBLIC LICENSE
 * for more read the LICENSE file
 * <p/>
 * Created by makeo @ 27.10.2015 13:16
 */
public class BlockStoneMachine extends Block {
    public BlockStoneMachine() {
        super(Material.rock);
        setHardness(3.0F);
        setResistance(25.0F);
        setStepSound(Block.soundTypeStone);
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        setCreativeTab(RegisteredItems.creativeTab);
    }

    private IIcon pillarIcon;
    private IIcon focusIcon;

    @Override
    public void registerBlockIcons(IIconRegister ir) {
        pillarIcon = ir.registerIcon("thaumcraft:es_1");
        focusIcon = ir.registerIcon("thaumcraft:thaumiumblock");
    }

    @Override
    public IIcon getIcon(int side, int metadata) {
        if(metadata == 11 || metadata == 15) {
            return pillarIcon;
        } else if(metadata == 0) {
            return focusIcon;
        }

        return super.getIcon(side, metadata);
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return metadata == 15 || metadata == 0;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 15));
        list.add(new ItemStack(item, 1, 11));
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
        int metadata = world.getBlockMetadata(x, y, z);
        if(metadata == 11 || metadata == 15) {
            return new ItemStack(getItemDropped(metadata, null, 0), 1, damageDropped(metadata));
        }

        return super.getPickBlock(target, world, x, y, z, player);
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        if(metadata == 15) {
            return new TileManipulatorPillar();
        } else if(metadata == 0) {
            return new TileManipulationFocus();
        }
        return null;
    }

    @Override
    public int damageDropped(int metadata) {
        return metadata;
    }

    @Override
    public Item getItemDropped(int metadata, Random random, int fortune) {
        if(metadata == 11 || metadata == 15) {
            return Item.getItemFromBlock(ConfigBlocks.blockCosmeticSolid);
        }

        return super.getItemDropped(metadata, random, fortune);
    }

    @Override
    public boolean canReplace(World world, int x, int y, int z, int side, ItemStack stack) {
        if(stack.getItemDamage() == 0 && (y < 1 || world.getBlock(x, y - 1, z) != RegisteredBlocks.blockNodeManipulator)) {
            return false;
        }
        return super.canReplace(world, x, y, z, side, stack);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        int metadata = world.getBlockMetadata(x, y, z);
        if(metadata == 15) {
            if(world.getBlock(x, y + 1, z) != this || world.getBlockMetadata(x, y + 1, z) != 11) {
                dropBlockAsItem(world, x, y, z, metadata, 0);
                world.setBlockToAir(x, y, z);
            }
        } else if(metadata == 11) {
            if(world.getBlock(x, y - 1, z) != this || world.getBlockMetadata(x, y - 1, z) != 15) {
                dropBlockAsItem(world, x, y, z, metadata, 0);
                world.setBlockToAir(x, y, z);
            }
        } else if(metadata == 0) {
            if(world.getBlock(x, y - 1, z) != RegisteredBlocks.blockNodeManipulator || world.getBlockMetadata(x, y - 1, z) != 5) {
                dropBlockAsItem(world, x, y, z, metadata, 0);
                world.setBlockToAir(x, y, z);
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer p_149727_5_, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
        if(world.getBlockMetadata(x, y, z) == 0 && world.getBlock(x, y - 1, z).equals(RegisteredBlocks.blockNodeManipulator) && world.getBlockMetadata(x, y - 1, z) == 5) {
            world.getBlock(x, y - 1, z).onBlockActivated(world, x, y - 1, z, p_149727_5_, p_149727_6_, p_149727_7_, p_149727_8_, p_149727_9_);
        }
        return super.onBlockActivated(world, x, y, z, p_149727_5_, p_149727_6_, p_149727_7_, p_149727_8_, p_149727_9_);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        int metadata = world.getBlockMetadata(x, y, z);
        if(metadata == 15) {
            setBlockBounds(0, 0, 0, 1, 0.5f, 1);
        } else if(metadata == 11) {
            setBlockBounds(0.0F, -1.0F, 0.0F, 1.0F, -0.5F, 1.0F);
        } else if(metadata == 0) {
            setBlockBounds(3/16f, 0, 3/16f, 1-(3/16f), 6/16f, 1-(3/16f));
        }
        super.setBlockBoundsBasedOnState(world, x, y, z);
    }

    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB axisalignedbb, List list, Entity entity) {
        int metadata = world.getBlockMetadata(x, y, z);
        if(metadata == 11 || metadata == 15) {
            setBlockBounds(0, 0, 0, 1, 1, 1);
        } else if(metadata == 0) {
            setBlockBoundsBasedOnState(world, x, y, z);
        }

        super.addCollisionBoxesToList(world, x, y, z, axisalignedbb, list, entity);
    }

    public boolean isOpaqueCube()
/*  97:    */   {
/*  98: 96 */     return false;
/*  99:    */   }

    public boolean renderAsNormalBlock()
/* 102:    */   {
/* 103:102 */     return false;
/* 104:    */   }

    @Override
    public int getRenderType() {
        return -1;
    }
}
