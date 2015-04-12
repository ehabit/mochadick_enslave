package enslave.entity.ai;
// Based heavily on Azanor's golem AI

import java.util.List;
import java.util.Random;

import lib.InventoryUtils;
import enslave.entity.EntityEnslavedVillager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class AIPickupItem extends EntityAIBase {
	
  private EntityEnslavedVillager theSlave;
  private Entity targetEntity;
  int count = 0;

  public AIPickupItem(EntityEnslavedVillager slave) {
    this.theSlave = slave;
    setMutexBits(3);
  }

  public boolean shouldExecute() {
    if (this.theSlave.ticksExisted % this.theSlave.slaveDelay > 0) return false;
    return findItem();
  }

  private boolean findItem() {
    double range = 1.7976931348623157E+308D;
    float dmod = this.theSlave.getRange();
    List<Entity> targets = this.theSlave.worldObj.getEntitiesWithinAABBExcludingEntity(this.theSlave, AxisAlignedBB.getBoundingBox(this.theSlave.getHomePosition().posX, this.theSlave.getHomePosition().posY, this.theSlave.getHomePosition().posZ, this.theSlave.getHomePosition().posX + 1, this.theSlave.getHomePosition().posY + 1, this.theSlave.getHomePosition().posZ + 1).expand(dmod, dmod, dmod));

    if (targets.size() == 0) return false;
    for (Entity e : targets) {
      if (((e instanceof EntityItem)) && (((EntityItem)e).delayBeforeCanPickup < 5)) if (!this.theSlave.inventory.allEmpty()) { if (this.theSlave.inventory.getAmountNeededSmart(((EntityItem)e).getEntityItem(), this.theSlave.getSlaveStrength() > 0) <= 0); } else if ((this.theSlave.getCarried() == null) || ((InventoryUtils.areItemStacksEqualStrict(this.theSlave.getCarried(), ((EntityItem)e).getEntityItem())) && (((EntityItem)e).getEntityItem().stackSize <= this.theSlave.getCarrySpace()))) {
          double distance = e.getDistanceSq(this.theSlave.getHomePosition().posX + 0.5F, this.theSlave.getHomePosition().posY + 0.5F, this.theSlave.getHomePosition().posZ + 0.5F);

          double distance2 = e.getDistanceSq(this.theSlave.posX, this.theSlave.posY, this.theSlave.posZ);

          if ((distance2 < range) && (distance <= dmod * dmod)) {
            range = distance2;
            this.targetEntity = e;
          }
        }
    }

    if (this.targetEntity == null) {
      return false;
    }

    return true;
  }

  public boolean continueExecuting() {
    return (this.count-- > 0) && (!this.theSlave.getNavigator().noPath()) && (this.targetEntity.isEntityAlive());
  }

  public void resetTask() {
    this.count = 0;
    this.targetEntity = null;
    this.theSlave.getNavigator().clearPathEntity();
  }

  public void updateTask() {
    this.theSlave.getLookHelper().setLookPositionWithEntity(this.targetEntity, 30.0F, 30.0F);
    double dist = this.theSlave.getDistanceSqToEntity(this.targetEntity);
    if (dist <= 2.0D)
    {
      pickUp();
    }
  }

  private void pickUp() {
    int amount = 0;
    if ((this.targetEntity instanceof EntityItem)) {
      ItemStack stack = ((EntityItem)this.targetEntity).getEntityItem().copy();

      if (((EntityItem)this.targetEntity).getEntityItem().stackSize < this.theSlave.getCarrySpace())
        amount = ((EntityItem)this.targetEntity).getEntityItem().stackSize;
      else {
        amount = this.theSlave.getCarrySpace();
      }
      stack.stackSize = amount;
      ((EntityItem)this.targetEntity).getEntityItem().stackSize -= amount;
      if (this.theSlave.getCarried() == null)
        this.theSlave.setCarried(stack);
      else {
        this.theSlave.getCarried().stackSize += amount;
      }
    }
    if (amount == 0) {
      return;
    }
    this.targetEntity.worldObj.playSoundAtEntity(this.targetEntity, "random.pop", 0.2F, ((this.targetEntity.worldObj.rand.nextFloat() - this.targetEntity.worldObj.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
  }

  public void startExecuting() {
    this.count = 200;
    this.theSlave.getNavigator().tryMoveToEntityLiving(this.targetEntity, this.theSlave.getAIMoveSpeed());
  }
}