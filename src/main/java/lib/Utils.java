package lib;


import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.EventBus;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.ReflectionHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import enslave.Enslave;
import enslave.entity.EntityEnslavedVillager;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.Vec3;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.BonemealEvent;

public class Utils
{
  public static boolean isChunkLoaded(World world, int x, int z)
  {
    int xx = x >> 4;
    int zz = z >> 4;
    return world.getChunkProvider().chunkExists(xx, zz);
  }
  
  public static final String[] colorNames = { "White", "Orange", "Magenta", "Light Blue", "Yellow", "Lime", "Pink", "Gray", "Light Gray", "Cyan", "Purple", "Blue", "Brown", "Green", "Red", "Black" };
  public static final int[] colors = { 15790320, 15435844, 12801229, 6719955, 14602026, 4312372, 14188952, 4408131, 10526880, 2651799, 8073150, 2437522, 5320730, 3887386, 11743532, 1973019 };
  
  public static boolean isWoodLog(IBlockAccess world, int x, int y, int z)
  {
    Block bi = world.getBlock(x, y, z);
    int md = world.getBlockMetadata(x, y, z);
    if (bi == Blocks.air) {
      return false;
    }
    if (bi.canSustainLeaves(world, x, y, z)) {
      return true;
    }
    return false;
  }
  
  public static boolean isLyingInCone(double[] x, double[] t, double[] b, float aperture)
  {
    double halfAperture = aperture / 2.0F;
    

    double[] apexToXVect = dif(t, x);
    

    double[] axisVect = dif(t, b);


    boolean isInInfiniteCone = dotProd(apexToXVect, axisVect) / magn(apexToXVect) / magn(axisVect) > Math.cos(halfAperture);
    if (!isInInfiniteCone) {
      return false;
    }
    boolean isUnderRoundCap = dotProd(apexToXVect, axisVect) / magn(axisVect) < magn(axisVect);
    
    return isUnderRoundCap;
  }
  
  public static double dotProd(double[] a, double[] b)
  {
    return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
  }
  
  public static double[] dif(double[] a, double[] b)
  {
    return new double[] { a[0] - b[0], a[1] - b[1], a[2] - b[2] };
  }
  
  public static double magn(double[] a)
  {
    return Math.sqrt(a[0] * a[0] + a[1] * a[1] + a[2] * a[2]);
  }
  
  public static Entity getEntityByID(int entityID, World world) {         
	    for(Object o: world.getLoadedEntityList()) {                        
	        if(((Entity)o).getEntityId() == entityID) {   
	        	// DEBUG
	            // System.out.println("Found the entity");                                
	            return ((Entity)o);                        
	        }                
	    }                
	    return null;        
	}
 
}
