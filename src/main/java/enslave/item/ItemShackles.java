package enslave.item;

import enslave.Enslave;
import enslave.entity.EntityEnslavedVillager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemShackles extends Item {
	
	public ItemShackles(String id) {
		maxStackSize = 64;
		GameRegistry.registerItem(this, id, Enslave.MODID);
		GameRegistry.addRecipe(new ItemStack(this, 1), new Object[] { "  X", " S ", "X  ", 'X', Blocks.iron_bars, 'S', Items.iron_ingot });
		setUnlocalizedName(id);
		setTextureName(Enslave.MODID + ":Shackles");
		setCreativeTab(CreativeTabs.tabMisc);
	}
	
	public static void mainRegistry() {
		Enslave.shackles = new ItemShackles("Shackles");
    }
	
	@Override
	public int getItemEnchantability() {
		return 0;
	}
	
	@SideOnly(Side.CLIENT)
    public boolean shouldRotateAroundWhenRendering() {
        return true;
    }
	
	
	@Override
	public boolean hitEntity(ItemStack itemstack, EntityLivingBase target, EntityLivingBase player){
	    
		if(!target.worldObj.isRemote){
	        if(target instanceof EntityVillager){
	        	// remove villager
	        	target.isDead = true;
	        	
	        	EntityPlayer p = (EntityPlayer) player;
	        	if (!p.capabilities.isCreativeMode) {
	        		// consume shackles if not in creative mode
	        		p.inventory.consumeInventoryItem(this);
	        	}
	        	
	        	// spawn enslaved villager for player
	        	Object enslavedVillager = new EntityEnslavedVillager(target.worldObj);
	        	((EntityEnslavedVillager)enslavedVillager).setLocationAndAngles(target.posX, target.posY, target.posZ, target.worldObj.rand.nextFloat() * 360.0F, 0.0F);
	        	target.worldObj.spawnEntityInWorld((EntityEnslavedVillager) enslavedVillager);
	        	
	        	
	            return true;
	        } 
	    }
	    return false;
	}	
	
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean isFull3D() {
		return true;
	}
}
