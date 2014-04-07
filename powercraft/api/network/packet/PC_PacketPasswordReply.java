package powercraft.api.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.World;
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
		this.x = te.xCoord;
		this.y = te.yCoord;
		this.z = te.zCoord;
		this.password = password;
	}
	
	@Override
	protected PC_Packet doAndReply(NetHandlerPlayServer iNetHandler, World world, EntityPlayerMP player) {
		PC_TileEntity te = PC_Utils.getTileEntity(world, this.x, this.y, this.z, PC_TileEntity.class);
		if(te!=null){
			if(!te.guiOpenPasswordReply(player, this.password)){
				return new PC_PacketWrongPassword(te);
			}
		}
		return null;
	}

	@Override
	protected void fromByteBuffer(ByteBuf buf) {
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.password = readStringFromBuf(buf);
	}

	@Override
	protected void toByteBuffer(ByteBuf buf) {
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		writeStringToBuf(buf, this.password);
	}

}
