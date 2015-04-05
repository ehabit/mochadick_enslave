package enslave.entity.ai;
// Modified Golem AI from Azanor

import com.mojang.authlib.GameProfile;

import enslave.entity.EntityEnslavedVillager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.UUID;

import lib.BlockCoordinates;
import lib.BlockUtils;
import lib.CropUtils;
import lib.EntityUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockLog;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeedFood;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.common.util.ForgeDirection;
//import thaumcraft.api.BlockCoordinates;
//import thaumcraft.common.config.Config;
//import thaumcraft.common.config.ConfigBlocks;
//import thaumcraft.common.config.ConfigItems;
//import thaumcraft.common.entities.golems.EntityGolemBase;
//import thaumcraft.common.lib.utils.BlockUtils;
//import thaumcraft.common.lib.utils.CropUtils;
//import thaumcraft.common.lib.utils.EntityUtils;

public class AIHarvestCrops extends EntityAIBase {
  private EntityEnslavedVillager theSlave;
  private int xx;
  private int yy;
  private int zz;
  private float movementSpeed;
  private float distance;
  private World theWorld;
  private Block block = Blocks.air;
  private int blockMd = 0;
  private int delay = -1;
  private int maxDelay = 1;
  private int mod = 1;
  private int count = 0;
  
  public AIHarvestCrops(EntityEnslavedVillager par1EntityCreature)
  {
    this.theSlave = par1EntityCreature;
    this.theWorld = par1EntityCreature.worldObj;
    setMutexBits(3);
    this.distance = MathHelper.ceiling_float_int(this.theSlave.getRange() / 4.0F);
  }
  
  public boolean shouldExecute()
  {
    if ((this.delay >= 0) || (this.theSlave.ticksExisted % this.theSlave.slaveDelay > 0) || (!this.theSlave.getNavigator().noPath())) {
      return false;
    }
    Vec3 var1 = findGrownCrop();
    if (var1 == null) {
      return false;
    }
    this.xx = ((int)var1.xCoord);
    this.yy = ((int)var1.yCoord);
    this.zz = ((int)var1.zCoord);
    
    this.block = this.theWorld.getBlock(this.xx, this.yy, this.zz);
    this.blockMd = this.theWorld.getBlockMetadata(this.xx, this.yy, this.zz);
    
    return true;
  }
  
  public boolean continueExecuting()
  {
    return (this.theWorld.getBlock(this.xx, this.yy, this.zz) == this.block) && (this.theWorld.getBlockMetadata(this.xx, this.yy, this.zz) == this.blockMd) && (this.count-- > 0) && ((this.delay > 0) || (!this.theSlave.getNavigator().noPath()));
  }
  
  public void updateTask()
  {
    double dist = this.theSlave.getDistanceSq(this.xx + 0.5D, this.yy + 0.5D, this.zz + 0.5D);
    this.theSlave.getLookHelper().setLookPosition(this.xx + 0.5D, this.yy + 0.5D, this.zz + 0.5D, 30.0F, 30.0F);
    if (dist <= 4.0D)
    {
      if (this.delay < 0)
      {
        this.delay = ((int)Math.max(10.0F, (20.0F - this.theSlave.getSlaveStrength() * 2.0F) * this.block.getBlockHardness(this.theWorld, this.xx, this.yy, this.zz)));
        this.maxDelay = this.delay;
        this.mod = (this.delay / Math.round(this.delay / 6.0F));
      }
      if (this.delay > 0)
      {
        if ((--this.delay > 0) && (this.delay % this.mod == 0) && (this.theSlave.getNavigator().noPath()))
        {
          this.theSlave.startActionTimer();
          this.theWorld.playSoundEffect(this.xx + 0.5F, this.yy + 0.5F, this.zz + 0.5F, this.block.stepSound.getBreakSound(), (this.block.stepSound.getVolume() + 0.7F) / 8.0F, this.block.stepSound.getPitch() * 0.5F);
          

          BlockUtils.destroyBlockPartially(this.theWorld, this.theSlave.getEntityId(), this.xx, this.yy, this.zz, (int)(9.0F * (1.0F - this.delay / this.maxDelay)));
        }
        if (this.delay == 0)
        {
          harvest();
          checkAdjacent();
        }
      }
    }
  }
  
  private void checkAdjacent()
  {
    for (int x2 = -2; x2 <= 2; x2++) {
      for (int z2 = -2; z2 <= 2; z2++) {
        for (int y2 = -1; y2 <= 1; y2++)
        {
          int x = this.xx + x2;
          int y = this.yy + y2;
          int z = this.zz + z2;
          if ((Math.abs(this.theSlave.getHomePosition().posX - x) <= this.distance) && (Math.abs(this.theSlave.getHomePosition().posY - y) <= this.distance) && (Math.abs(this.theSlave.getHomePosition().posZ - z) <= this.distance)) {
            if (CropUtils.isGrownCrop(this.theWorld, x, y, z))
            {
              Vec3 var1 = Vec3.createVectorHelper(x, y, z);
              if (var1 != null)
              {
                this.xx = ((int)var1.xCoord);
                this.yy = ((int)var1.yCoord);
                this.zz = ((int)var1.zCoord);
                
                this.block = this.theWorld.getBlock(this.xx, this.yy, this.zz);
                this.blockMd = this.theWorld.getBlockMetadata(this.xx, this.yy, this.zz);
                
                this.delay = -1;
                
                startExecuting();
                return;
              }
            }
          }
        }
      }
    }
  }
  
  public void resetTask()
  {
    BlockUtils.destroyBlockPartially(this.theWorld, this.theSlave.getEntityId(), this.xx, this.yy, this.zz, -1);
    this.delay = -1;
  }
  
  public void startExecuting()
  {
    this.count = 200;
    this.theSlave.getNavigator().tryMoveToXYZ(this.xx + 0.5D, this.yy + 0.5D, this.zz + 0.5D, this.theSlave.getAIMoveSpeed());
  }
  
  ArrayList<BlockCoordinates> checklist = new ArrayList();
  
  private Vec3 findGrownCrop()
  {
    Random rand = this.theSlave.getRNG();
    if (this.checklist.size() == 0)
    {
      for (int a = (int)-this.distance; a <= this.distance; a++) {
        for (int b = (int)-this.distance; b <= this.distance; b++) {
          this.checklist.add(new BlockCoordinates(this.theSlave.getHomePosition().posX + a, 0, this.theSlave.getHomePosition().posZ + b));
        }
      }
      Collections.shuffle(this.checklist, rand);
    }
    int x = ((BlockCoordinates)this.checklist.get(0)).x;
    int z = ((BlockCoordinates)this.checklist.get(0)).z;
    this.checklist.remove(0);
    for (int y = this.theSlave.getHomePosition().posY - 3; y <= this.theSlave.getHomePosition().posY + 3; y++) {
      if (CropUtils.isGrownCrop(this.theWorld, x, y, z)) {
        return Vec3.createVectorHelper(x, y, z);
      }
    }
    return null;
  }
  
  void harvest()
  {
    this.count = 200;
    int md = this.blockMd;
    FakePlayer fp = FakePlayerFactory.get((WorldServer)this.theWorld, new GameProfile((UUID)null, "FakeThaumcraftGolem"));
    fp.setPosition(this.theSlave.posX, this.theSlave.posY, this.theSlave.posZ);
    if (CropUtils.clickableCrops.contains(this.block.getUnlocalizedName() + md))
    {
      this.block.onBlockActivated(this.theWorld, this.xx, this.yy, this.zz, fp, 0, 0.0F, 0.0F, 0.0F);
    }
    else
    {
      this.theWorld.func_147480_a(this.xx, this.yy, this.zz, true);
      if (4 > 0)
      {
        ArrayList<ItemStack> items = new ArrayList();
        ArrayList<Entity> drops = EntityUtils.getEntitiesInRange(this.theWorld, this.theSlave.posX, this.theSlave.posY, this.theSlave.posZ, this.theSlave, EntityItem.class, 6.0D);
        if (drops.size() > 0) {
          for (Entity e : drops) {
            if ((e instanceof EntityItem))
            {
              if (e.ticksExisted < 2)
              {
                Vec3 v = Vec3.createVectorHelper(e.posX - this.theSlave.posX, e.posY - this.theSlave.posY, e.posZ - this.theSlave.posZ);
                v = v.normalize();
                e.motionX = (-v.xCoord / 4.0D);
                e.motionY = 0.075D;
                e.motionZ = (-v.zCoord / 4.0D);
              }
              boolean done = false;
              EntityItem item = (EntityItem)e;
              ItemStack st = item.getEntityItem();
              if ((st.getItem() != null) && (st.getItem() == Items.dye) && (st.getItemDamage() == 3))
              {
                int var5 = BlockDirectional.getDirection(this.blockMd);
                int par2 = this.xx + net.minecraft.util.Direction.offsetX[var5];
                int par4 = this.zz + net.minecraft.util.Direction.offsetZ[var5];
                Block var6 = this.theWorld.getBlock(par2, this.yy, par4);
                if ((var6 == Blocks.log) && (BlockLog.func_150165_c(this.theWorld.getBlockMetadata(par2, this.yy, par4)) == 3))
                {
                  st.stackSize -= 1;
                  this.theWorld.setBlock(this.xx, this.yy, this.zz, Blocks.cocoa, BlockDirectional.getDirection(this.blockMd), 3);
                }
                done = true;
              }
              
              else
              {
                int[] xm = { 0, 0, 1, 1, -1, 0, -1, -1, 1 };
                int[] zm = { 0, 1, 0, 1, 0, -1, -1, 1, -1 };
                int count = 0;
                while ((st != null) && (st.stackSize > 0) && (count < 9))
                {
                  if ((st.getItem() != null) && (((st.getItem() instanceof IPlantable)) || ((st.getItem() instanceof ItemSeedFood)))) {
                    if (st.getItem().onItemUse(st.copy(), fp, this.theWorld, this.xx + xm[count], this.yy - 1, this.zz + zm[count], ForgeDirection.UP.ordinal(), 0.5F, 0.5F, 0.5F)) {
                      st.stackSize -= 1;
                    }
                  }
                  count++;
                }
              }
              if (st.stackSize <= 0) {
                item.setDead();
              } else {
                item.setEntityItemStack(st);
              }
              if (done) {
                break;
              }
            }
          }
        }
      }
    }
    fp.setDead();
    this.theSlave.startActionTimer();
  }
}
