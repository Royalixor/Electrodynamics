package electrodynamics.module;

import java.io.File;
import java.util.EnumSet;

import net.minecraftforge.common.MinecraftForge;

import electrodynamics.Electrodynamics;
import electrodynamics.block.BlockHandler;
import electrodynamics.client.render.RenderThermalOverlay;
import electrodynamics.configuration.ConfigurationHandler;
import electrodynamics.core.helper.HeatHelper;
import electrodynamics.item.ItemHandler;
import electrodynamics.lib.ModInfo;
import electrodynamics.module.ModuleManager.Module;

public class EDModuleCore extends EDModule {

	//TODO Neatening up
	@Override
	public void preInit() {
		ConfigurationHandler.handleConfig(new File(Electrodynamics.instance.configFolder, ModInfo.CORE_CONFIG));
		
		Electrodynamics.proxy.registerKeyBindings();
		
		ItemHandler.initializeItems();
		BlockHandler.createBlockHoloPad();
	}

	@Override
	public void init() {
		HeatHelper.initializeMapping();
		MinecraftForge.EVENT_BUS.register(new RenderThermalOverlay());
		Electrodynamics.proxy.registerTileEntities();
	}

	@Override
	public void postInit() {
		
	}

	@Override
	public EnumSet<Module> dependencies() {
		return null;
	}

	@Override
	public boolean canLoad() {
		return true;
	}

	@Override
	public String failLoadReason() {
		return "If this fails to load, you've got a problem.";
	}

}
