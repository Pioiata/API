package powercraft.api.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import powercraft.api.PC_Utils;
import powercraft.api.block.PC_TileEntity;
import powercraft.api.network.PC_Packet;
import powercraft.api.network.PC_PacketClientToServer;

public class PC_PacketPasswordReply extends PC_PacketClientToServer {

	private int x;
	private int y;
	private int z;
	private String password;
	
	public PC_PacketPasswordReply(){
		
	}
	
	public PC_PacketPasswordReply(PC_TileEntity te, String password){
		x = te.xCoord;
		y = te.yCoord;
		z = te.zCoord;
		this.password = password;
	}
	
	@Override
	protected PC_Packet doAndReply(INetHandler iNetHandler) {
		EntityPlayer player = ((NetHandlerPlayServer)iNetHandler).playerEntity;
		PC_TileEntity te = PC_Utils.getTileEntity(player.worldObj, x, y, z, PC_TileEntity.class);
		if(te!=null){
			if(!te.guiOpenPasswordReply(player, password)){
				return new PC_PacketWrongPassword(te);
			}
		}
		return null;
	}

	@Override
	protected void fromByteBuffer(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		password = readStringFromBuf(buf);
	}

	@Override
	protected void toByteBuffer(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		writeStringToBuf(buf, password);
	}

}
