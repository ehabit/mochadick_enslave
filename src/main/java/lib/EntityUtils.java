package lib;


import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.ReflectionHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityUtils
{
  public static Entity getPointedEntity(World world, Entity entityplayer, double minrange, double range, float padding)
  {
    return getPointedEntity(world, entityplayer, minrange, range, padding, false);
  }
  
  public static Entity getPointedEntity(World world, Entity entityplayer, double minrange, double range, float padding, boolean nonCollide)
  {
    Entity pointedEntity = null;
    double d = range;
    Vec3 vec3d = Vec3.createVectorHelper(entityplayer.posX, entityplayer.posY + entityplayer.getEyeHeight(), entityplayer.posZ);
    

    Vec3 vec3d1 = entityplayer.getLookVec();
    Vec3 vec3d2 = vec3d.addVector(vec3d1.xCoord * d, vec3d1.yCoord * d, vec3d1.zCoord * d);
    
    float f1 = padding;
    List list = world.getEntitiesWithinAABBExcludingEntity(entityplayer, entityplayer.boundingBox.addCoord(vec3d1.xCoord * d, vec3d1.yCoord * d, vec3d1.zCoord * d).expand(f1, f1, f1));
    



    double d2 = 0.0D;
    for (int i = 0; i < list.size(); i++)
    {
      Entity entity = (Entity)list.get(i);
      if (entity.getDistanceToEntity(entityplayer) >= minrange) {
        if (((entity.canBeCollidedWith()) || (nonCollide)) && (world.func_147447_a(Vec3.createVectorHelper(entityplayer.posX, entityplayer.posY + entityplayer.getEyeHeight(), entityplayer.posZ), Vec3.createVectorHelper(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ), false, true, false) == null))
        {
          float f2 = Math.max(0.8F, entity.getCollisionBorderSize());
          AxisAlignedBB axisalignedbb = entity.boundingBox.expand(f2, f2, f2);
          MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3d, vec3d2);
          if (axisalignedbb.isVecInside(vec3d))
          {
            if ((0.0D < d2) || (d2 == 0.0D))
            {
              pointedEntity = entity;
              d2 = 0.0D;
            }
          }
          else if (movingobjectposition != null)
          {
            double d3 = vec3d.distanceTo(movingobjectposition.hitVec);
            if ((d3 < d2) || (d2 == 0.0D))
            {
              pointedEntity = entity;
              d2 = d3;
            }
          }
        }
      }
    }
    return pointedEntity;
  }
  
  public static Entity getPointedEntity(World world, EntityPlayer entityplayer, double range, Class<?> clazz)
  {
    Entity pointedEntity = null;
    double d = range;
    Vec3 vec3d = Vec3.createVectorHelper(entityplayer.posX, entityplayer.posY + entityplayer.getEyeHeight(), entityplayer.posZ);
    

    Vec3 vec3d1 = entityplayer.getLookVec();
    Vec3 vec3d2 = vec3d.addVector(vec3d1.xCoord * d, vec3d1.yCoord * d, vec3d1.zCoord * d);
    
    float f1 = 1.1F;
    List list = world.getEntitiesWithinAABBExcludingEntity(entityplayer, entityplayer.boundingBox.addCoord(vec3d1.xCoord * d, vec3d1.yCoord * d, vec3d1.zCoord * d).expand(f1, f1, f1));
    



    double d2 = 0.0D;
    for (int i = 0; i < list.size(); i++)
    {
      Entity entity = (Entity)list.get(i);
      if ((entity.canBeCollidedWith()) && (world.func_147447_a(Vec3.createVectorHelper(entityplayer.posX, entityplayer.posY + entityplayer.getEyeHeight(), entityplayer.posZ), Vec3.createVectorHelper(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ), false, true, false) == null) && (!clazz.isInstance(entity)))
      {
        float f2 = Math.max(0.8F, entity.getCollisionBorderSize());
        AxisAlignedBB axisalignedbb = entity.boundingBox.expand(f2, f2, f2);
        MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3d, vec3d2);
        if (axisalignedbb.isVecInside(vec3d))
        {
          if ((0.0D < d2) || (d2 == 0.0D))
          {
            pointedEntity = entity;
            d2 = 0.0D;
          }
        }
        else if (movingobjectposition != null)
        {
          double d3 = vec3d.distanceTo(movingobjectposition.hitVec);
          if ((d3 < d2) || (d2 == 0.0D))
          {
            pointedEntity = entity;
            d2 = d3;
          }
        }
      }
    }
    return pointedEntity;
  }
  
  public static boolean canEntityBeSeen(Entity entity, TileEntity te)
  {
    return te.getWorldObj().rayTraceBlocks(Vec3.createVectorHelper(te.xCoord + 0.5D, te.yCoord + 1.25D, te.zCoord + 0.5D), Vec3.createVectorHelper(entity.posX, entity.posY, entity.posZ), false) == null;
  }
  
  public static boolean canEntityBeSeen(Entity entity, double x, double y, double z)
  {
    return entity.worldObj.rayTraceBlocks(Vec3.createVectorHelper(x, y, z), Vec3.createVectorHelper(entity.posX, entity.posY, entity.posZ), false) == null;
  }
  
  public static boolean canEntityBeSeen(Entity entity, Entity entity2)
  {
    return entity.worldObj.rayTraceBlocks(Vec3.createVectorHelper(entity.posX, entity.posY, entity.posZ), Vec3.createVectorHelper(entity2.posX, entity2.posY, entity2.posZ), false) == null;
  }
  
  public static void setRecentlyHit(EntityLivingBase ent, int hit)
  {
    try
    {
      ObfuscationReflectionHelper.setPrivateValue(EntityLivingBase.class, ent, Integer.valueOf(hit), new String[] { "recentlyHit", "field_70718_bc" });
    }
    catch (Exception e) {}
  }
  
  public static int getRecentlyHit(EntityLivingBase ent)
  {
    try
    {
      return ((Integer)ReflectionHelper.getPrivateValue(EntityLivingBase.class, ent, new String[] { "recentlyHit", "field_70718_bc" })).intValue();
    }
    catch (Exception e) {}
    return 0;
  }
  
  public static MovingObjectPosition getMovingObjectPositionFromPlayer(World par1World, EntityPlayer par2EntityPlayer, boolean par3)
  {
    float f = 1.0F;
    float f1 = par2EntityPlayer.prevRotationPitch + (par2EntityPlayer.rotationPitch - par2EntityPlayer.prevRotationPitch) * f;
    

    float f2 = par2EntityPlayer.prevRotationYaw + (par2EntityPlayer.rotationYaw - par2EntityPlayer.prevRotationYaw) * f;
    

    double d0 = par2EntityPlayer.prevPosX + (par2EntityPlayer.posX - par2EntityPlayer.prevPosX) * f;
    

    double d1 = par2EntityPlayer.prevPosY + (par2EntityPlayer.posY - par2EntityPlayer.prevPosY) * f + (par1World.isRemote ? par2EntityPlayer.getEyeHeight() - par2EntityPlayer.getDefaultEyeHeight() : par2EntityPlayer.getEyeHeight());
    













    double d2 = par2EntityPlayer.prevPosZ + (par2EntityPlayer.posZ - par2EntityPlayer.prevPosZ) * f;
    

    Vec3 vec3 = Vec3.createVectorHelper(d0, d1, d2);
    float f3 = MathHelper.cos(-f2 * 0.01745329F - 3.141593F);
    float f4 = MathHelper.sin(-f2 * 0.01745329F - 3.141593F);
    float f5 = -MathHelper.cos(-f1 * 0.01745329F);
    float f6 = MathHelper.sin(-f1 * 0.01745329F);
    float f7 = f4 * f5;
    float f8 = f3 * f5;
    double d3 = 5.0D;
    if ((par2EntityPlayer instanceof EntityPlayerMP)) {
      d3 = ((EntityPlayerMP)par2EntityPlayer).theItemInWorldManager.getBlockReachDistance();
    }
    Vec3 vec31 = vec3.addVector(f7 * d3, f6 * d3, f8 * d3);
    
    return par1World.func_147447_a(vec3, vec31, par3, !par3, false);
  }
  
  public static ArrayList<Entity> getEntitiesInRange(World world, double x, double y, double z, Entity entity, Class clazz, double range)
  {
    ArrayList<Entity> out = new ArrayList();
    List list = world.getEntitiesWithinAABB(clazz, AxisAlignedBB.getBoundingBox(x, y, z, x, y, z).expand(range, range, range));
    if (list.size() > 0) {
      for (Object e : list)
      {
        Entity ent = (Entity)e;
        if ((entity == null) || (entity.getEntityId() != ent.getEntityId())) {
          out.add(ent);
        }
      }
    }
    return out;
  }
  
  public static boolean isVisibleTo(float fov, Entity ent, Entity ent2, float range)
  {
    double[] x = { ent2.posX, ent2.boundingBox.minY + ent2.height / 2.0F, ent2.posZ };
    double[] t = { ent.posX, ent.boundingBox.minY + ent.getEyeHeight(), ent.posZ };
    Vec3 q = ent.getLookVec();
    q.xCoord *= range;
    q.yCoord *= range;
    q.zCoord *= range;
    Vec3 l = q.addVector(ent.posX, ent.boundingBox.minY + ent.getEyeHeight(), ent.posZ);
    
    double[] b = { l.xCoord, l.yCoord, l.zCoord };
    
    return Utils.isLyingInCone(x, t, b, fov);
  }
  
  public static boolean isVisibleTo(float fov, Entity ent, double xx, double yy, double zz, float range)
  {
    double[] x = { xx, yy, zz };
    double[] t = { ent.posX, ent.boundingBox.minY + ent.getEyeHeight(), ent.posZ };
    Vec3 q = ent.getLookVec();
    q.xCoord *= range;
    q.yCoord *= range;
    q.zCoord *= range;
    Vec3 l = q.addVector(ent.posX, ent.boundingBox.minY + ent.getEyeHeight(), ent.posZ);
    
    double[] b = { l.xCoord, l.yCoord, l.zCoord };
    
    return Utils.isLyingInCone(x, t, b, fov);
  }
  
  
 
}
