package powercraft.api;

import java.io.File;
import java.util.Arrays;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.ModMetadata;

public abstract class PC_Module {

	public static final String POWERCRAFT = "PowerCraft";
	public static final String POWERCRAFT_URL = "http://powercrafting.net";
	public static final String POWERCRAFT_LOGOFILE = "/powercraft/PowerCraft.png";
	public static final String[] POWERCRAFT_AUTHORS = {"XOR", "Rapus", "Buggi"};
	public static final String POWERCRAFT_CREDITS = "MightyPork, RxD, LOLerul2";
	
	private final PC_CreativeTab creativeTab;
	private final ModContainer mod;
	private final Configuration config;
	
	public PC_Module(){
		creativeTab = new PC_CreativeTab(Loader.instance().activeModContainer().getName(), this);
		mod = PC_Utils.getActiveMod();
		ModMetadata metadata = getMetadata();
		metadata.autogenerated = false;
		metadata.url = POWERCRAFT_URL;
		metadata.logoFile = POWERCRAFT_LOGOFILE;
		metadata.description = "Mod PowerCraft";
		metadata.authorList = Arrays.asList(POWERCRAFT_AUTHORS);
		metadata.credits = POWERCRAFT_CREDITS;
		if (PC_Api.INSTANCE != null) PC_Api.INSTANCE.getMetadata().childMods.add(PC_Utils.getActiveMod());
		config = new Configuration(new File(Loader.instance().getConfigDir(), mod.getName()+".cfg"));
		config.load();
		moduleBootstrap();
	}
	
	protected void moduleBootstrap(){
		
	}
	
	public void saveConfig(){
		config.save();
	}
	
	public Configuration getConfig(){
		return config;
	}
	
	/**
	 * get the {@link ModContainer} for this module
	 * @return the modContainer or null if none
	 */
	public ModContainer getContainer() {
		return mod;
	}

	/**
	 * get the mod metadata
	 * @return the metadata
	 */
	public ModMetadata getMetadata() {
		return mod.getMetadata();
	}

	public String getName() {
		return getContainer().getName();
	}
	
	public String getModId() {
		return getContainer().getModId();
	}

	public abstract ItemStack getCreativeTabItemStack();

	public PC_CreativeTab getCreativeTab(){
		return creativeTab;
	}
	
}
