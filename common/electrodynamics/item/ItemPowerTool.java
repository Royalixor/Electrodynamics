package electrodynamics.item;

import java.util.List;

import electrodynamics.core.CreativeTabED;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemPowerTool extends Item {

	// For now hardcoded, and shared across all power items. Will most likely change.
	public static final int MAX_POWER = 1600;
	
	public ItemPowerTool(int id) {
		super(id);
		setMaxDamage(0);
		setMaxStackSize(1);
		setNoRepair();
		setCreativeTab(CreativeTabED.tool);
	}
	
	public static int getCharge(ItemStack stack) {
		if (stack.getTagCompound() == null) {
			stack.setTagCompound(new NBTTagCompound());
		}
		
		return stack.getTagCompound().getInteger("charge");
	}
	
	public static void setCharge(ItemStack stack, int chargeLevel) {
		if (chargeLevel > MAX_POWER) chargeLevel = MAX_POWER;
		if (chargeLevel < 0) chargeLevel = 0;
		
		if (stack.getTagCompound() == null) {
			stack.setTagCompound(new NBTTagCompound());
		}
		
		NBTTagCompound nbt = stack.getTagCompound();
		nbt.setInteger("charge", chargeLevel);
		stack.setTagCompound(nbt);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean show) {
		list.add("Power: " + getCharge(stack) + "/" + MAX_POWER);
	}
	
}
