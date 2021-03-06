package enslave.entity.ai;
// Modified AI originally from Azanor's Thaumcraft

import com.mojang.authlib.GameProfile;

import java.util.Random;
import java.util.UUID;

import lib.BlockUtils;
import lib.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemAxe;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import enslave.Enslave;
import enslave.entity.EntityEnslavedVillager;


public class AIHarvestLogs extends EntityAIBase {
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
  FakePlayer player;
  private int count = 0;
  
  public AIHarvestLogs(EntityEnslavedVillager slave)
  {
    this.theSlave = slave;
    this.theWorld = slave.worldObj;
    setMutexBits(3);
    this.distance = MathHelper.ceiling_float_int(this.theSlave.getRange() / 3.0F);
    if ((this.theWorld instanceof WorldServer)) {
      this.player = FakePlayerFactory.get((WorldServer)this.theWorld, new GameProfile((UUID)null, "FakeEnslavedVillager"));
    }
  }
  
  public boolean shouldExecute()
  {
    if ((this.delay >= 0) || (this.theSlave.ticksExisted % this.theSlave.slaveDelay > 0) || (!this.theSlave.getNavigator().noPath())) {
      return false;
    }
    // if not holding an axe then return false, should not look for trees
    if (this.theSlave.getHeldItem() == null) {
    	return false;
    } else {
    	if (!(this.theSlave.getHeldItem().getItem() instanceof ItemAxe)) {
    		return false;
    	}
    }
    
    Vec3 var1 = findLog();
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
    return (this.theWorld.getBlock(this.xx, this.yy, this.zz) == this.block) && (this.theWorld.getBlockMetadata(this.xx, this.yy, this.zz) == this.blockMd) && (this.count-- > 0) && ((this.delay > 0) || (Utils.isWoodLog(this.theWorld, this.xx, this.yy, this.zz)) || (!this.theSlave.getNavigator().noPath()));
  }
  
  public void updateTask() {
    double dist = this.theSlave.getDistanceSq(this.xx + 0.5D, this.yy + 0.5D, this.zz + 0.5D);
    this.theSlave.getLookHelper().setLookPosition(this.xx + 0.5D, this.yy + 0.5D, this.zz + 0.5D, 30.0F, 30.0F);
    if (dist <= 4.0D) {
      if (this.delay < 0) {
        this.delay = ((int)Math.max(5.0F, (20.0F - this.theSlave.getSlaveStrength() * 3.0F) * this.block.getBlockHardness(this.theWorld, this.xx, this.yy, this.zz)));
        this.maxDelay = this.delay;
        this.mod = (this.delay / Math.round(this.delay / 6.0F));
      }
      if (this.delay > 0) {
        if ((--this.delay > 0) && (this.delay % this.mod == 0) && (this.theSlave.getNavigator().noPath())) {
          this.theSlave.startActionTimer();
          this.theWorld.playSoundEffect(this.xx + 0.5F, this.yy + 0.5F, this.zz + 0.5F, this.block.stepSound.getBreakSound(), (this.block.stepSound.getVolume() + 0.7F) / 8.0F, this.block.stepSound.getPitch() * 0.5F);
          
          BlockUtils.destroyBlockPartially(this.theWorld, this.theSlave.getEntityId(), this.xx, this.yy, this.zz, (int)(9.0F * (1.0F - this.delay / this.maxDelay)));
        }
        if (this.delay == 0) {
          harvest();
          if (Utils.isWoodLog(this.theWorld, this.xx, this.yy, this.zz)) {
            this.delay = -1;
            this.block = this.theWorld.getBlock(this.xx, this.yy, this.zz);
            this.blockMd = this.theWorld.getBlockMetadata(this.xx, this.yy, this.zz);
            startExecuting();
          } else {
            checkAdjacent();
          }
        }
      }
    }
  }
  
  private void checkAdjacent() {
    for (int x2 = -1; x2 <= 1; x2++) {
      for (int z2 = -1; z2 <= 1; z2++) {
        for (int y2 = -1; y2 <= 1; y2++) {
          int x = this.xx + x2;
          int y = this.yy + y2;
          int z = this.zz + z2;
          if ((Math.abs(this.theSlave.getHomePosition().posX - x) <= this.distance) && (Math.abs(this.theSlave.getHomePosition().posY - y) <= this.distance) && (Math.abs(this.theSlave.getHomePosition().posZ - z) <= this.distance)) {
            if (Utils.isWoodLog(this.theWorld, x, y, z)) {
              Vec3 var1 = Vec3.createVectorHelper(x, y, z);
              if (var1 != null) {
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
  
  void harvest()
  {
    this.count = 200;
    this.theWorld.playAuxSFX(2001, this.xx, this.yy, this.zz, Block.getIdFromBlock(this.block) + (this.blockMd << 12));
    BlockUtils.breakFurthestBlock(this.theWorld, this.xx, this.yy, this.zz, this.block, this.player);
    this.theSlave.startActionTimer();
  }
  
  private Vec3 findLog()
  {
    Random rand = this.theSlave.getRNG();
    for (int var2 = 0; var2 < this.distance * 4.0F; var2++)
    {
      int x = (int)(this.theSlave.getHomePosition().posX + rand.nextInt((int)(1.0F + this.distance * 2.0F)) - this.distance);
      int y = (int)(this.theSlave.getHomePosition().posY + rand.nextInt((int)(1.0F + this.distance)) - this.distance / 2.0F);
      int z = (int)(this.theSlave.getHomePosition().posZ + rand.nextInt((int)(1.0F + this.distance * 2.0F)) - this.distance);
      if (Utils.isWoodLog(this.theWorld, x, y, z))
      {
        Vec3 v = Vec3.createVectorHelper(x, y, z);
        double dist = this.theSlave.getDistanceSq(x + 0.5D, y + 0.5D, z + 0.5D);
        int yy = 1;
        while ((Utils.isWoodLog(this.theWorld, x, y - yy, z)) && (this.theSlave.getDistanceSq(x + 0.5D, y - yy + 0.5D, z + 0.5D) < dist))
        {
          v = Vec3.createVectorHelper(x, y - yy, z);
          dist = this.theSlave.getDistanceSq(x + 0.5D, y - yy + 0.5D, z + 0.5D);
          yy++;
        }
        return v;
      }
    }
    return null;
  }
}
