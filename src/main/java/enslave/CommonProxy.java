package enslave;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import enslave.network.MessageToClient;
import enslave.network.MessageToServer;
import net.minecraft.entity.player.EntityPlayer;

public class CommonProxy {
	public void registerRenderers() {
		
	}
	
	public void fmlLifeCycleEvent(FMLPreInitializationEvent event) { 

	}
	
	
	/**
	 * Returns a side-appropriate EntityPlayer for use during message handling
	 */
	public EntityPlayer getPlayerEntity(MessageContext ctx) {
		return ctx.getServerHandler().playerEntity;
	}
}
