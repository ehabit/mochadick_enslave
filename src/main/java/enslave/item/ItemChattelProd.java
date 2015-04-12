package enslave.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enslave.Enslave;


public class ItemChattelProd extends Item {
    
	public ItemChattelProd(String id) {
        super();
        this.setMaxDamage(100);
        this.setMaxStackSize(1);
        maxStackSize = 1;
		GameRegistry.registerItem(this, id, Enslave.MODID);
//		GameRegistry.addRecipe(new ItemStack(this, 1), new Object[] { "  X", " XS", "X S", 'X', Items.stick, 'S', Items.lead });
		setUnlocalizedName(id);
		setTextureName(Enslave.MODID + ":ChattelProd");
		setCreativeTab(CreativeTabs.tabMisc);
    }
	
	public static void mainRegistry() {
		Enslave.chattelProd = new ItemChattelProd("ChattelProd");
    }
    
	@SideOnly(Side.CLIENT)
    public boolean isFull3D() {
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    public void whipAtPlayer(EntityPlayer player) {
    	player.swingItem();
    	player.worldObj.playSoundAtEntity(player, "enslave:chattelprod", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 0.8F));
    }

    
    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player) {
        
        player.swingItem();
        world.playSoundAtEntity(player, "enslave:chattelprod", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 0.8F));        
        
        return itemstack;
    }

}