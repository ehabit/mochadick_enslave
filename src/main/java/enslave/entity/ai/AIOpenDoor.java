package enslave.entity.ai;
//Modified AI originally from Azanor's Thaumcraft

import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import enslave.entity.EntityEnslavedVillager;

public class AIOpenDoor extends AIDoorInteract
{
  boolean field_75361_i;
  int field_75360_j;

  public AIOpenDoor(EntityEnslavedVillager par1EntityLiving, boolean par2)
  {
    super(par1EntityLiving);
    this.theEntity = par1EntityLiving;
    this.field_75361_i = par2;
  }

  public boolean continueExecuting()
  {
    return (this.field_75361_i) && (this.field_75360_j > 0) && (super.continueExecuting());
  }

  public void startExecuting()
  {
    this.field_75360_j = 20;
    if (this.targetDoor == Blocks.wooden_door) {
      ((BlockDoor)this.targetDoor).func_150014_a(this.theEntity.worldObj, this.entityPosX, this.entityPosY, this.entityPosZ, true);
    } else {
      int var10 = this.theEntity.worldObj.getBlockMetadata(this.entityPosX, this.entityPosY, this.entityPosZ);
      if (!BlockFenceGate.isFenceGateOpen(var10)) {
        int var11 = (MathHelper.floor_double(this.theEntity.rotationYaw * 4.0F / 360.0F + 0.5D) & 0x3) % 4;
        int var12 = BlockFenceGate.getDirection(var10);

        if (var12 == (var11 + 2) % 4)
        {
          var10 = var11;
        }

        this.theEntity.worldObj.setBlock(this.entityPosX, this.entityPosY, this.entityPosZ, this.targetDoor, var10 | 0x4, 3);
        this.theEntity.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1003, this.entityPosX, this.entityPosY, this.entityPosZ, 0);
      }
    }
  }

  public void resetTask()
  {
    if (this.field_75361_i)
    {
      if (this.targetDoor == Blocks.wooden_door) {
        ((BlockDoor)this.targetDoor).func_150014_a(this.theEntity.worldObj, this.entityPosX, this.entityPosY, this.entityPosZ, false);
      } else {
        int var10 = this.theEntity.worldObj.getBlockMetadata(this.entityPosX, this.entityPosY, this.entityPosZ);
        if (BlockFenceGate.isFenceGateOpen(var10)) {
          this.theEntity.worldObj.setBlock(this.entityPosX, this.entityPosY, this.entityPosZ, this.targetDoor, var10 & 0xFFFFFFFB, 3);
          this.theEntity.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1003, this.entityPosX, this.entityPosY, this.entityPosZ, 0);
        }
      }
    }
  }

  public void updateTask()
  {
    this.field_75360_j -= 1;
    super.updateTask();
  }
}