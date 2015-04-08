package enslave.network.handler;

import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import enslave.entity.EntityEnslavedVillager;
import enslave.entity.ExtendedSlave;

public class EnslavedVillagerSyncHandler {
	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event)
	{
		if (event.entity instanceof EntityEnslavedVillager && ExtendedSlave.get((EntityEnslavedVillager) event.entity) == null) {
			// This is how extended properties are registered using our convenient method from earlier
			ExtendedSlave.register((EntityEnslavedVillager)  event.entity);
		}
		
		if (event.entity instanceof EntityEnslavedVillager && event.entity.getExtendedProperties(ExtendedSlave.EXT_PROP_NAME) == null) {
			event.entity.registerExtendedProperties(ExtendedSlave.EXT_PROP_NAME, new ExtendedSlave((EntityEnslavedVillager) event.entity));
		}
	}
}
