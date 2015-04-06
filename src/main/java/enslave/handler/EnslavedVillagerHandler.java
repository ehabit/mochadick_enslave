package enslave.handler;

import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import enslave.entity.EntityEnslavedVillager;

public class EnslavedVillagerHandler {
	
	@SubscribeEvent
	public void onRenderEnslavedVillager(RenderLivingEvent.Post event) {
	    if (event.entity instanceof EntityEnslavedVillager) {
	    	
	    }
	}
}
