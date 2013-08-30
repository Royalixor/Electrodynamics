package electrodynamics.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import electrodynamics.client.gui.GuiGlassJar;
import electrodynamics.core.CreativeTabED;
import electrodynamics.core.handler.GuiHandler;
import electrodynamics.core.handler.GuiHandler.GuiType;
import electrodynamics.purity.AlloyFactory;

public class ItemGlassJar extends Item {

	public static Map<String, Vec3> playerLook = new HashMap<String, Vec3>();
	
	public static final int SHAKE_PROGRESS_MAX = 20;
	
	public static final String DUST_LIST_KEY = "dusts";
	public static final String DUST_EXIST_BOOL_KEY = "hasDusts";
	public static final String MIXED_KEY = "mixed";
	
	public ItemGlassJar(int id) {
		super(id);
		setCreativeTab(CreativeTabED.tool);
		setMaxDamage(SHAKE_PROGRESS_MAX + 1);
		setMaxStackSize(1);
	}

	public static boolean isMixed(ItemStack jar) {
		if (jar.stackTagCompound == null) {
			jar.setTagCompound(new NBTTagCompound());
		}
		
		NBTTagCompound jarNBT = jar.stackTagCompound;
		
		return jarNBT.hasKey(MIXED_KEY) && jarNBT.getBoolean(MIXED_KEY) == true;
	}
	
	public static void setMixed(ItemStack jar, boolean bool) {
		if (jar.stackTagCompound == null) {
			jar.setTagCompound(new NBTTagCompound());
		}
		
		NBTTagCompound jarNBT = jar.stackTagCompound;
		
		jarNBT.setBoolean(MIXED_KEY, bool);
	}
	
	public static ItemStack[] getStoredDusts(ItemStack jar) {
		if (jar.stackTagCompound == null) {
			jar.setTagCompound(new NBTTagCompound());
		}
		
		NBTTagCompound jarNBT = jar.stackTagCompound;
		
		if (hasDusts(jar)) {
			NBTTagList dustsNBT = jarNBT.getTagList(DUST_LIST_KEY);
			ItemStack[] dusts = new ItemStack[dustsNBT.tagCount()];
			
			for (int i=0; i<dustsNBT.tagCount(); i++) {
				NBTTagCompound dust = (NBTTagCompound) dustsNBT.tagAt(i);
				dusts[i] = ItemStack.loadItemStackFromNBT(dust);
			}
			
			return dusts;
		} else {
			return new ItemStack[0];
		}
	}
	
	public static void addDusts(ItemStack jar, ItemStack[] dusts) {
		if (jar.stackTagCompound == null) {
			jar.setTagCompound(new NBTTagCompound());
		}
		
		NBTTagCompound jarNBT = jar.stackTagCompound;

		NBTTagList dustsNBT = null;
		
		if (!hasDusts(jar)) {
			dustsNBT = new NBTTagList();
		} else {
			dustsNBT = jarNBT.getTagList(DUST_LIST_KEY);
		}
		
		for (ItemStack dustStack : dusts) {
			NBTTagCompound dust = new NBTTagCompound();
			dustStack.writeToNBT(dust);
			dustsNBT.appendTag(dust);
		}
		jarNBT.setTag(DUST_LIST_KEY, dustsNBT);
		jarNBT.setBoolean(DUST_EXIST_BOOL_KEY, true);
		jar.setTagCompound(jarNBT);
	}
	
	public static void dumpDusts(ItemStack jar) {
		if (jar.stackTagCompound == null) {
			jar.setTagCompound(new NBTTagCompound());
		}
		
		NBTTagCompound jarNBT = jar.stackTagCompound;
		
		jarNBT.setTag(DUST_LIST_KEY, new NBTTagList());
		jarNBT.setBoolean(DUST_EXIST_BOOL_KEY, false);
		jar.setTagCompound(jarNBT);
	}
	
	public static boolean hasDusts(ItemStack jar) {
		if (jar.stackTagCompound == null) {
			return false;
		}
		
		NBTTagCompound jarNBT = jar.stackTagCompound;
		
		if (!jarNBT.hasKey(DUST_LIST_KEY)) {
			return false;
		} else {
			if (!jarNBT.hasKey(DUST_EXIST_BOOL_KEY) || (jarNBT.hasKey(DUST_EXIST_BOOL_KEY) && jarNBT.getBoolean(DUST_EXIST_BOOL_KEY) == false)) {
				return false;
			} else {
				return true;
			}
		}
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int int1, boolean bool1) {
		if (!world.isRemote) {
			if (entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer)entity;
				String ID = "look" + player.getEntityName();
				
				if (hasDusts(stack)) {
					if (!isMixed(stack)) {
						if (stack.getItemDamage() < SHAKE_PROGRESS_MAX) {
							Vec3 currLook = player.getLookVec();
							Vec3 lastLook = playerLook.get(ID);
							
							if (lastLook == null) {
								lastLook = currLook;
							}
							
							if (lastLook.distanceTo(currLook) >= 1.2) {
								stack.setItemDamage(stack.getItemDamage() + 1);
							}
							
							playerLook.put(ID, currLook);
						} else {
							setMixed(stack, true);
							stack.setItemDamage(0);
						}
					}
				}
			}
		}
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean show) {
		int max = GuiGlassJar.DUST_MAX;
		
		if (!hasDusts(stack)) {
			list.add("Empty (0/" + max + ")");
		} else {
			int dustCount = getStoredDusts(stack).length;
			
			if (dustCount < max) {
				list.add("Partially Filled (" + dustCount + "/" + max+ ")");
			} else {
				list.add("Full (" + max + "/" + max + ")");
			}
			
			if (this.isMixed(stack)) {
				list.add("Mixed");
			}
		}
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (!world.isRemote) {
			if (!player.isSneaking()) {
				GuiHandler.openGui(player, world, (int)player.posX, (int)player.posY, (int)player.posZ, GuiType.GLASS_JAR);
			} else {
				ItemStack[] dusts = ItemGlassJar.getStoredDusts(stack);
				
				if (dusts != null && dusts.length > 0) {
					if (!isMixed(stack)) {
						for (ItemStack dust : dusts) {
							player.dropPlayerItem(dust);
						}
					} else {
						AlloyFactory factory = AlloyFactory.fromInventory(dusts);
						ItemStack dust = factory.generateItemStack(0);
						dust.stackSize = dusts.length;
						player.dropPlayerItem(dust);
					}
				}
				
				ItemGlassJar.dumpDusts(stack);
				ItemGlassJar.setMixed(stack, false);
			}
		}
		
		return stack;
	}

}
