package enslave.network.handler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import enslave.Enslave;
import enslave.entity.EntityEnslavedVillager;
import lib.Utils;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;


public class MessageSyncEntityToClient implements IMessage 
{
    private int entityId ;
    private NBTTagCompound entitySyncDataCompound;

    public MessageSyncEntityToClient() 
    { 
    	// need this constructor
    }

    public MessageSyncEntityToClient(int parEntityId, NBTTagCompound parTagCompound) 
    {
    	entityId = parEntityId;
        entitySyncDataCompound = parTagCompound;
        // DEBUG
        System.out.println("SyncEntityToClient constructor");
    }

    @Override
    public void fromBytes(ByteBuf buf) 
    {
    	entityId = ByteBufUtils.readVarInt(buf, 4);
    	entitySyncDataCompound = ByteBufUtils.readTag(buf); // this class is very useful in general for writing more complex objects
    	// DEBUG
    	System.out.println("fromBytes");
    }

    @Override
    public void toBytes(ByteBuf buf) 
    {
    	ByteBufUtils.writeVarInt(buf, entityId, 4);
    	ByteBufUtils.writeTag(buf, entitySyncDataCompound);
        // DEBUG
        System.out.println("toBytes encoded");
    }

    public static class Handler implements IMessageHandler<MessageSyncEntityToClient, IMessage> 
    {
        
        @Override
        public IMessage onMessage(MessageSyncEntityToClient message, MessageContext ctx) 
        {
        	EntityPlayer thePlayer = Enslave.proxy.getPlayerEntity(ctx);
        	EntityEnslavedVillager slave = (EntityEnslavedVillager)Utils.getEntityByID(message.entityId, thePlayer.worldObj);
//        	slave.setSyncDataCompound(message.entitySyncDataCompound);
        	// DEBUG
        	System.out.println("MessageSyncEnitityToClient onMessage(), entity ID = "+message.entityId);
            return null; // no response in this case
        }
    }
}