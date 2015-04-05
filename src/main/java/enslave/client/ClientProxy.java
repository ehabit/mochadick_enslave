package enslave.client;

import cpw.mods.fml.client.registry.RenderingRegistry;
import enslave.CommonProxy;
import enslave.client.render.RenderEnslavedVillager;
import enslave.entity.EntityEnslavedVillager;

public class ClientProxy extends CommonProxy {
	@Override
	public void registerRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(EntityEnslavedVillager.class, new RenderEnslavedVillager());
	}
}
