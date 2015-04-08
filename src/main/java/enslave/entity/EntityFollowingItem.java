package enslave.entity;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import enslave.CommonProxy;
import enslave.Enslave;

public class EntityFollowingItem extends EntitySpecialItem
  implements IEntityAdditionalSpawnData
{
  double targetX = 0.0D;
  double targetY = 0.0D;
  double targetZ = 0.0D;
  int type = 3;
  public Entity target = null;
  int field_70292_b = 20;
  public double gravity = 0.03999999910593033D;
  
  public EntityFollowingItem(World par1World, double par2, double par4, double par6, ItemStack par8ItemStack)
  {
    super(par1World);
    setSize(0.25F, 0.25F);
    this.yOffset = (this.height / 2.0F);
    setPosition(par2, par4, par6);
    setEntityItemStack(par8ItemStack);
    this.rotationYaw = ((float)(Math.random() * 360.0D));
  }
  
  public EntityFollowingItem(World par1World, double par2, double par4, double par6, ItemStack par8ItemStack, Entity target, int t)
  {
    this(par1World, par2, par4, par6, par8ItemStack);
    this.target = target;
    this.targetX = target.posX;
    this.targetY = (target.boundingBox.minY + target.height / 2.0F);
    this.targetZ = target.posZ;
    this.type = t;
    this.noClip = true;
  }
  
  public EntityFollowingItem(World par1World, double par2, double par4, double par6, ItemStack par8ItemStack, double tx, double ty, double tz)
  {
    this(par1World, par2, par4, par6, par8ItemStack);
    this.targetX = tx;
    this.targetY = ty;
    this.targetZ = tz;
  }
  
  public EntityFollowingItem(World par1World)
  {
    super(par1World);
    setSize(0.25F, 0.25F);
    this.yOffset = (this.height / 2.0F);
  }
  
  public void onUpdate()
  {
    if (this.target != null)
    {
      this.targetX = this.target.posX;
      this.targetY = (this.target.boundingBox.minY + this.target.height / 2.0F);
      this.targetZ = this.target.posZ;
    }
    if ((this.targetX != 0.0D) || (this.targetY != 0.0D) || (this.targetZ != 0.0D))
    {
      float xd = (float)(this.targetX - this.posX);
      float yd = (float)(this.targetY - this.posY);
      float zd = (float)(this.targetZ - this.posZ);
      if (this.field_70292_b > 1) {
        this.field_70292_b -= 1;
      }
      double distance = MathHelper.sqrt_float(xd * xd + yd * yd + zd * zd);
      if (distance > 0.5D)
      {
        distance *= this.field_70292_b;
        this.motionX = (xd / distance);
        this.motionY = (yd / distance);
        this.motionZ = (zd / distance);
      }
      else
      {
        this.motionX *= 0.1000000014901161D;
        this.motionY *= 0.1000000014901161D;
        this.motionZ *= 0.1000000014901161D;
        this.targetX = 0.0D;
        this.targetY = 0.0D;
        this.targetZ = 0.0D;
        this.target = null;
        this.noClip = false;
      }
    }
    else
    {
      this.motionY -= this.gravity;
    }
    super.onUpdate();
  }
  
  public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
  {
    super.writeEntityToNBT(par1NBTTagCompound);
    par1NBTTagCompound.setShort("type", (short)this.type);
  }
  
  public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
  {
    super.readEntityFromNBT(par1NBTTagCompound);
    this.type = par1NBTTagCompound.getShort("type");
  }
  
  public void writeSpawnData(ByteBuf data)
  {
    if (this.target != null)
    {
      data.writeInt(this.target == null ? -1 : this.target.getEntityId());
      data.writeDouble(this.targetX);
      data.writeDouble(this.targetY);
      data.writeDouble(this.targetZ);
      data.writeByte(this.type);
    }
  }
  
  public void readSpawnData(ByteBuf data)
  {
    try
    {
      int ent = data.readInt();
      if (ent > -1) {
        this.target = this.worldObj.getEntityByID(ent);
      }
      this.targetX = data.readDouble();
      this.targetY = data.readDouble();
      this.targetZ = data.readDouble();
      this.type = data.readByte();
    }
    catch (Exception e) {}
  }
}