package electrodynamics.tileentity.structure;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import electrodynamics.interfaces.IRedstoneUser;
import electrodynamics.mbs.MBSManager;
import electrodynamics.mbs.MultiBlockStructure;
import electrodynamics.network.packet.PacketUpdateRedstone;
import electrodynamics.tileentity.TileEntityGeneric;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;

public abstract class TileEntityStructure extends TileEntityGeneric {

	// The coordinates of the central TE.
	protected int targetX, targetY, targetZ;

	// Whether if this TE is part of the structure.
	protected boolean isValidStructure = false;

	// The orientation of the MBS as a whole (relative to the MBS's pattern design).
	protected int rotation;

	// stores the ID of the current MBS this TE is a part of
	protected String mbsID = "";

	public void validateStructure(MultiBlockStructure multiBlockStructure, int rotation, int x, int y, int z) {
		this.rotation = rotation;
		this.targetX = x;
		this.targetY = y;
		this.targetZ = z;
		this.isValidStructure = true;
		this.mbsID = multiBlockStructure.getUID();
	}

	public void invalidateStructure() {
		isValidStructure = false;
		mbsID = "";
	}

	public boolean isValidStructure() {
		return isValidStructure;
	}

	public boolean isCentralTileEntity() {
		return isValidStructure() && xCoord == targetX && yCoord == targetY && zCoord == targetZ;
	}

	public TileEntityStructure getCentralTileEntity() {
		if( isValidStructure ) {
			if( this.isCentralTileEntity() ) {
				return this;
			}
			return (TileEntityStructure) worldObj.getBlockTileEntity( targetX, targetY, targetZ );
		}
		return null;
	}

	public MultiBlockStructure getMBS() {
		if( !mbsID.equals( "" ) ) {
			return MBSManager.getMultiBlockStructure( mbsID );
		}
		return null;
	}

	public int getRotation() {
		return rotation;
	}

	public abstract boolean onBlockActivatedBy(EntityPlayer player, int side, float xOff, float yOff, float zOff);

	public void onBlockUpdate() {
		TileEntityStructure centralTE = getCentralTileEntity();
		
		if (centralTE != null && centralTE instanceof IRedstoneUser && this.isValidStructure()) {
			((IRedstoneUser)centralTE).updateSignalStrength(this.worldObj.getStrongestIndirectPower(xCoord, yCoord, zCoord));
			PacketUpdateRedstone packet = new PacketUpdateRedstone();
			packet.x = centralTE.xCoord;
			packet.y = centralTE.yCoord;
			packet.z = centralTE.zCoord;
			packet.strength = this.worldObj.getStrongestIndirectPower(xCoord, yCoord, zCoord);
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT( nbt );
		NBTTagCompound tag = nbt.getCompoundTag( "structure" );
		isValidStructure = tag.getBoolean( "isPart" );
		targetX = tag.getInteger( "targetX" );
		targetY = tag.getInteger( "targetY" );
		targetZ = tag.getInteger( "targetZ" );
		rotation = tag.getInteger( "rotation" );
		mbsID = tag.getString( "mbsID" );
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT( nbt );
		NBTTagCompound tag = new NBTTagCompound();
		tag.setBoolean( "isPart", isValidStructure );
		tag.setInteger( "targetX", targetX );
		tag.setInteger( "targetY", targetY );
		tag.setInteger( "targetZ", targetZ );
		tag.setInteger( "rotation", rotation );
		tag.setString( "mbsID", mbsID );
		nbt.setTag( "structure", tag );
	}

	@SuppressWarnings("static-access")
	@SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
		return this.INFINITE_EXTENT_AABB;
    }
	
	public static TileEntityStructure createNewPlaceHolderTE() {
		return new TileStructurePlaceHolder();
	}

	public static class TileStructurePlaceHolder extends TileEntityStructure {

		@Override
		public boolean onBlockActivatedBy(EntityPlayer player, int side, float xOff, float yOff, float zOff) {
			return false;
		}
	}

}
