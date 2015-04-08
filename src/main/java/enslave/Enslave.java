package enslave;

import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import enslave.entity.EntityEnslavedVillager;
import enslave.item.ItemShackles;
import enslave.item.ItemWhip;
import enslave.network.handler.EnslavedVillagerSyncHandler;


@Mod(modid = Enslave.MODID, name = Enslave.NAME, version = Enslave.VERSION)
public class Enslave {
	public static final String MODID = "enslave";
	public static final String NAME = "Enslave";
	public static final String VERSION = "0.4";
	public static final String CLIENTSIDE = "enslave.client.ClientProxy";
    public static final String SERVERSIDE = "enslave.CommonProxy";
    
    public static final Logger log = LogManager.getLogger("MOCHADICK");
	
	@SidedProxy(clientSide=Enslave.CLIENTSIDE, serverSide=Enslave.SERVERSIDE)
	public static CommonProxy proxy;
	
	@Instance(Enslave.MODID)
	public static Enslave instance;
	   
    public static ItemShackles shackles;
    public static ItemWhip whip;
    
    
    @EventHandler
    public void PreLoad(FMLPreInitializationEvent PreEvent) {
    	log.info("Registering items");
    	shackles.mainRegistry();
    	whip.mainRegistry();
    	
    	log.info("Registering entities");
    	EntityEnslavedVillager.mainRegistry();
    	
    	MinecraftForge.EVENT_BUS.register(new EnslavedVillagerSyncHandler());
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
    	log.info("Registering renderers");
    	proxy.registerRenderers();
    }
}
