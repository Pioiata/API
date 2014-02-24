package powercraft.api.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import powercraft.api.block.PC_AbstractBlockBase;
import powercraft.api.item.PC_Item;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PC_Renderer implements ISimpleBlockRenderingHandler, IItemRenderer {

	private static PC_FakeBlockForRenderer fakeBlock = new PC_FakeBlockForRenderer();
	
	private static PC_Renderer instance;
	
	private int renderID;
	
	public static PC_Renderer getInstance() {
		if(instance==null){
			instance = new PC_Renderer();
		}
		return instance;
	}
	
	private PC_Renderer(){
		if(instance!=null)
			throw new InstantiationError();
		renderID = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(this);
	}
	
	@Override
	public boolean handleRenderType(ItemStack itemStack, ItemRenderType type) {
		Item item = itemStack.getItem();
		if(item instanceof PC_Item){
			return ((PC_Item)item).handleRenderType(itemStack, type);
		}
		return false;
	}


	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack itemStack, ItemRendererHelper helper) {
		Item item = itemStack.getItem();
		if(item instanceof PC_Item){
			return ((PC_Item)item).shouldUseRenderHelper(itemStack, type, helper);
		}
		return false;
	}


	@Override
	public void renderItem(ItemRenderType type, ItemStack itemStack, Object... data) {
		Item item = itemStack.getItem();
		if(item instanceof PC_Item){
			((PC_Item)item).renderItem(itemStack, type, data);
		}
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
		if(block instanceof PC_AbstractBlockBase){
			((PC_AbstractBlockBase)block).renderInventoryBlock(metadata, modelId, renderer);
		}
	}
	
	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		if(block instanceof PC_AbstractBlockBase){
			return ((PC_AbstractBlockBase)block).renderWorldBlock(world, x, y, z, modelId, renderer);
		}
		return false;
	}
	
	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return renderID;
	}

	public static void renderBlockInInventory(Block block, int metadata, int modelId, RenderBlocks renderer){
		Tessellator tessellator = Tessellator.instance;

		IIcon[] textures = new IIcon[6];
		for (int a = 0; a < 6; a++) {
			textures[a] = renderer.getBlockIconFromSideAndMetadata(block, a, metadata);
		}

		block.setBlockBoundsForItemRender();
		renderer.setRenderBoundsFromBlock(block);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		tessellator.startDrawingQuads();
		if (textures[0] != null) {
			tessellator.setNormal(0.0F, -1F, 0.0F);
			renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, textures[0]);
		}
		if (textures[1] != null) {
			tessellator.setNormal(0.0F, 1.0F, 0.0F);
			renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, textures[1]);
		}
		if (textures[2] != null) {
			tessellator.setNormal(0.0F, 0.0F, -1F);
			renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, textures[2]);
		}
		if (textures[3] != null) {
			tessellator.setNormal(0.0F, 0.0F, 1.0F);
			renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, textures[3]);
		}
		if (textures[4] != null) {
			tessellator.setNormal(-1F, 0.0F, 0.0F);
			renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, textures[4]);
		}
		if (textures[5] != null) {
			tessellator.setNormal(1.0F, 0.0F, 0.0F);
			renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, textures[5]);
		}
		tessellator.draw();
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		renderer.unlockBlockBounds();
	}
	
	public static boolean renderBlockInWorld(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer){
		renderer.renderStandardBlock(block, x, y, z);
		return true;
	}
	
	public static void renderStandardBlockInWorld(IBlockAccess world, int x, int y, int z, IIcon[] icons, int colorMultipier, int lightValue, RenderBlocks renderer){
		fakeBlock.icons = icons;
		fakeBlock.colorMultiplier = colorMultipier;
		fakeBlock.lightValue = lightValue;
		renderer.renderStandardBlock(fakeBlock, x, y, z);
	}
	
	public static void renderStandardBlockInInventory(IIcon[] icons, int colorMultipier, int lightValue, RenderBlocks renderer){
		fakeBlock.icons = icons;
		fakeBlock.colorMultiplier = colorMultipier;
		fakeBlock.lightValue = lightValue;
		renderer.renderBlockAsItem(fakeBlock, 0, 1);
	}
	
}
