package enslave.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class ExtendedSlave implements IExtendedEntityProperties {
	
	public final static String EXT_PROP_NAME = "ExtendedSlave";
	
	private EntityEnslavedVillager slave;
	
	public ExtendedSlave(EntityEnslavedVillager entity) {
		this.slave = entity;
	}
	
	public static final void register(EntityEnslavedVillager slave) {
		slave.registerExtendedProperties(ExtendedSlave.EXT_PROP_NAME, new ExtendedSlave(slave));
	}

	public static final EntityEnslavedVillager get(EntityEnslavedVillager slave) {
		return (EntityEnslavedVillager) slave.getExtendedProperties(EXT_PROP_NAME);
	}
	

	@Override
	public void saveNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = new NBTTagCompound();
		
		properties.setInteger("TextureType", this.slave.getTextureType());
		
		compound.setTag(EXT_PROP_NAME, properties);
	}


	@Override
	public void loadNBTData(NBTTagCompound compound) {
		NBTTagCompound properties = (NBTTagCompound) compound.getTag(EXT_PROP_NAME);
		this.slave.setTextureType(properties.getInteger("TextureType"));
	}


	@Override
	public void init(Entity entity, World world) { }
	
}
