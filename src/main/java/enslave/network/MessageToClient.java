package enslave.network;


import io.netty.buffer.ByteBuf;

import enslave.Enslave;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MessageToClient implements IMessage 
{
    
    private String text;

    public MessageToClient() 
    { 
    	// need this constructor
    }

    public MessageToClient(String parText) 
    {
        text = parText;
        // DEBUG
        System.out.println("MyMessage constructor");
    }

    @Override
    public void fromBytes(ByteBuf buf) 
    {
        text = ByteBufUtils.readUTF8String(buf); // this class is very useful in general for writing more complex objects
    	// DEBUG
    	System.out.println("fromBytes = "+text);
    }

    @Override
    public void toBytes(ByteBuf buf) 
    {
        ByteBufUtils.writeUTF8String(buf, text);
        // DEBUG
        System.out.println("toBytes = "+text);
    }

    public static class Handler implements IMessageHandler<MessageToClient, IMessage> 
    {
        
        @Override
        public IMessage onMessage(MessageToClient message, MessageContext ctx) 
        {
            System.out.println(String.format("Received %s from %s", message.text, Enslave.proxy.getPlayerEntity(ctx).getDisplayName()));
            return null; // no response in this case
        }
    }
}