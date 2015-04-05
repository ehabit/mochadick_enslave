package enslave.entity;


import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntitySpecialItem
  extends EntityItem
{
  public EntitySpecialItem(World par1World, double par2, double par4, double par6, ItemStack par8ItemStack)
  {
    super(par1World);
    setSize(0.25F, 0.25F);
    this.yOffset = (this.height / 2.0F);
    setPosition(par2, par4, par6);
    setEntityItemStack(par8ItemStack);
    this.rotationYaw = ((float)(Math.random() * 360.0D));
    this.motionX = ((float)(Math.random() * 0.2000000029802322D - 0.1000000014901161D));
    this.motionY = 0.2000000029802322D;
    this.motionZ = ((float)(Math.random() * 0.2000000029802322D - 0.1000000014901161D));
  }
  
  public EntitySpecialItem(World par1World)
  {
    super(par1World);
    setSize(0.25F, 0.25F);
    this.yOffset = (this.height / 2.0F);
  }
  
  public void onUpdate()
  {
    if (this.motionY > 0.0D) {
      this.motionY *= 0.8999999761581421D;
    }
    this.motionY += 0.03999999910593033D;
    super.onUpdate();
  }
  
  public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_)
  {
    if (p_70097_1_.isExplosion()) {
      return false;
    }
    return super.attackEntityFrom(p_70097_1_, p_70097_2_);
  }
}
