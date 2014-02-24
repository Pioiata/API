package powercraft.api.gres;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;

import org.lwjgl.opengl.GL11;

import powercraft.api.PC_RectI;
import powercraft.api.PC_Vec2I;
import powercraft.api.PC_Vec3;
import powercraft.api.block.PC_TileEntity;
import powercraft.api.gres.PC_GresAlign.H;
import powercraft.api.gres.PC_GresAlign.V;
import powercraft.api.gres.events.PC_GresEvent;
import powercraft.api.gres.events.PC_GresMouseButtonEvent;
import powercraft.api.gres.events.PC_GresMouseButtonEvent.Event;
import powercraft.api.gres.events.PC_IGresEventListener;
import powercraft.api.gres.history.PC_GresHistory;
import powercraft.api.gres.layout.PC_GresLayoutVertical;
import powercraft.api.inventory.PC_ISidedInventory;
import powercraft.api.redstone.PC_RedstoneWorkType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PC_GresWindowSideTab extends PC_GresContainer {

	private static PC_GresWindowSideTab openSideTab;
	
	private final PC_Vec2I size = new PC_Vec2I(20, 20);
	
	private final PC_Vec3 color = new PC_Vec3(1, 1, 1);
	
	private PC_GresDisplayObject displayObject;
	
	public PC_GresWindowSideTab(String text){
		super(text);
		frame.x = 2;
		frame.y = 20;
		frame.width = 2;
		frame.height = 2;
	}
	
	public PC_GresWindowSideTab(String text, PC_GresDisplayObject displayObject) {
		this(text);
		setDisplayObject(displayObject);
	}

	public void setColor(PC_Vec3 color){
		this.color.setTo(color);
	}
	
	public void setDisplayObject(PC_GresDisplayObject displayObject){
		this.displayObject = displayObject;
	}
	
	public PC_GresDisplayObject getDisplayObject(){
		return displayObject;
	}
	
	@Override
	protected void setParent(PC_GresContainer parent) {
		if(parent instanceof PC_GresWindow){
			this.parent = parent;
			parentVisible = parent.isRecursiveVisible();
			parentEnabled = parent.isRecursiveEnabled();
		}else if (parent == null) {
			this.parent = null;
			parentVisible = true;
			parentEnabled = true;
		}
	}
	
	@Override
	protected PC_Vec2I calculateMinSize() {
		return new PC_Vec2I(20, 20);
	}

	@Override
	protected PC_Vec2I calculateMaxSize() {
		return new PC_Vec2I(100, 100);
	}

	@Override
	protected PC_Vec2I calculatePrefSize() {
		return new PC_Vec2I(20, 20);
	}
	
	@Override
	protected PC_Vec2I getRealLocation() {
		if (parent == null) {
			return rect.getLocation();
		} 
		return rect.getLocation().add(parent.getRealLocation());
	}

	@Override
	protected void paint(PC_RectI scissor, double scale, int displayHeight, float timeStamp) {
		GL11.glColor3d(color.x, color.y, color.z);
		drawTexture("Frame", -2, 0, rect.width+2, rect.height);
		GL11.glColor3f(1, 1, 1);
		if(displayObject!=null)
			displayObject.draw(2, 1, 16, 16);
		PC_Vec2I loc = getRealLocation();
		PC_RectI s = setDrawRect(scissor, new PC_RectI(loc.x+20, loc.y+2, rect.width-22, 16), scale, displayHeight);
		if(s!=null)
			drawString(text, 20, 2, 100, 16, H.LEFT, V.CENTER, false);
	}
	
	private boolean update=true;
	
	@Override
	protected void notifyChange() {
		if(update){
			updateMinSize();
			updatePrefSize();
			updateMaxSize();
		}
		notifyParentOfChange();
		if(update){
			rect.setSize(getPrefSize().max(new PC_Vec2I(fontRenderer.getStringWidth(text)+24, 20)));
			updateLayout();
		}
		rect.setSize(size);
	}

	@Override
	public PC_RectI getChildRect() {
		if(update){
			rect.setSize(getPrefSize().max(new PC_Vec2I(fontRenderer.getStringWidth(text)+24, 20)));
		}
		PC_RectI r = super.getChildRect();
		rect.setSize(size);
		return r;
	}
	
	@Override
	protected void onTick() {
		super.onTick();
		if(openSideTab==this){
			size.setTo(size.add(2).min(getPrefSize().max(new PC_Vec2I(fontRenderer.getStringWidth(text)+24, 20))));
		}else{
			size.setTo(size.sub(2).max(20));
		}
		update=false;
		setSize(size);
		update=true;
	}

	@Override
	protected boolean handleMouseButtonClick(PC_Vec2I mouse, int buttons, int eventButton, PC_GresHistory history) {
		if(openSideTab == this){
			openSideTab = null;
		}else{
			openSideTab = this;
		}
		return true;
	}
	
	private static Object getTypeDisp(PC_RedstoneWorkType type){
		if(type==null)
			return PC_Gres.getGresTexture("I_OFF");
		switch(type){
		case EVER:
			return PC_Gres.getGresTexture("I_ON");
		case ON_FLANK:
			return PC_Gres.getGresTexture("I_FL");
		case ON_HI_FLANK:
			return PC_Gres.getGresTexture("I_HFL");
		case ON_LOW_FLANK:
			return PC_Gres.getGresTexture("I_LFL");
		case ON_OFF:
			return PC_Gres.getGresTexture("Redstone_Torch_Off");
		case ON_ON:
			return Blocks.redstone_torch;
		default:
			return PC_Gres.getGresTexture("I_OFF");
		}
	}
	
	public static PC_GresWindowSideTab createRedstoneSideTab(PC_TileEntity tileEntity){
		PC_GresWindowSideTab sideTab = new PC_GresWindowSideTab("Redstone", new PC_GresDisplayObject(Items.redstone));
		sideTab.setColor(new PC_Vec3(1.0, 0.2, 0.2));
		sideTab.setLayout(new PC_GresLayoutVertical());
		PC_RedstoneWorkType[] types = tileEntity.getAllowedRedstoneWorkTypes();
		Object[] disps = new Object[types.length];
		for(int i=0; i<types.length; i++){
			disps[i] = getTypeDisp(types[i]);
		}
		PC_GresDisplayObject dO = new PC_GresDisplayObject(disps);
		PC_GresDisplay d = new PC_GresDisplay(dO);
		d.addEventListener(new RedstoneConfigEventListener(tileEntity, types));
		d.setBackground(new PC_GresDisplayObject(PC_Gres.getGresTexture("Slot")));
		d.setFrame(new PC_RectI(1, 1, 1, 1));
		sideTab.add(d);
		sideTab.add(new PC_GresLabel("State: ON"));
		return sideTab;
	}
	
	public static PC_GresWindowSideTab createIOConfigurationSideTab(PC_ISidedInventory inventory){
		PC_GresWindowSideTab sideTab = new PC_GresWindowSideTab("Configuration", new PC_GresDisplayObject(PC_Gres.getGresTexture("IO_CONF")));
		sideTab.setColor(new PC_Vec3(0.2, 1.0, 0.2));
		sideTab.setLayout(new PC_GresLayoutVertical());
		PC_GresFrame frame = new PC_GresFrame();
		frame.setMinSize(new PC_Vec2I(54, 54));
		sideTab.add(frame);
		Object[] obj = new Object[inventory.getGroupCount()+1];
		obj[0] = PC_Gres.getGresTexture("NULL");
		for(int i=1; i<obj.length; i++){
			obj[i] = PC_Gres.getGresTexture("F"+i);
		}
		PC_GresDisplay[] sides = new PC_GresDisplay[6];
		PC_GresDisplayObject dO;
		frame.add(sides[0] = new PC_GresDisplay(dO = new PC_GresDisplayObject(obj)));
		dO.setActiveDisplayObjectIndex(inventory.getSideGroup(0)+1);
		sides[0].setLocation(new PC_Vec2I(18, 1));
		sides[0].setSize(new PC_Vec2I(16, 16));
		frame.add(sides[1] = new PC_GresDisplay(dO = new PC_GresDisplayObject(obj)));
		dO.setActiveDisplayObjectIndex(inventory.getSideGroup(1)+1);
		sides[1].setLocation(new PC_Vec2I(1, 18));
		sides[1].setSize(new PC_Vec2I(16, 16));
		frame.add(sides[2] = new PC_GresDisplay(new PC_GresDisplayObject(inventory.getFrontIcon())));
		sides[2].setLocation(new PC_Vec2I(18, 18));
		sides[2].setSize(new PC_Vec2I(16, 16));
		frame.add(sides[3] = new PC_GresDisplay(dO = new PC_GresDisplayObject(obj)));
		dO.setActiveDisplayObjectIndex(inventory.getSideGroup(3)+1);
		sides[3].setLocation(new PC_Vec2I(35, 18));
		sides[3].setSize(new PC_Vec2I(16, 16));
		frame.add(sides[4] = new PC_GresDisplay(dO = new PC_GresDisplayObject(obj)));
		dO.setActiveDisplayObjectIndex(inventory.getSideGroup(4)+1);
		sides[4].setLocation(new PC_Vec2I(18, 35));
		sides[4].setSize(new PC_Vec2I(16, 16));
		frame.add(sides[5] = new PC_GresDisplay(dO = new PC_GresDisplayObject(obj)));
		dO.setActiveDisplayObjectIndex(inventory.getSideGroup(5)+1);
		sides[5].setLocation(new PC_Vec2I(35, 35));
		sides[5].setSize(new PC_Vec2I(16, 16));
		IOConfigEventListener eventListener = new IOConfigEventListener(inventory, sides);
		for(int i=0; i<sides.length; i++){
			sides[i].addEventListener(eventListener);
		}
		return sideTab;
	}
	
	private static class RedstoneConfigEventListener implements PC_IGresEventListener{
		
		private PC_TileEntity tileEntity;
		PC_RedstoneWorkType types[];
		
		public RedstoneConfigEventListener(PC_TileEntity tileEntity, PC_RedstoneWorkType types[]){
			this.tileEntity = tileEntity;
			this.types = types;
		}

		@Override
		public void onEvent(PC_GresEvent event) {
			if(event instanceof PC_GresMouseButtonEvent){
				PC_GresMouseButtonEvent bEvent = (PC_GresMouseButtonEvent) event;
				if(bEvent.getEvent()==Event.CLICK){
					PC_GresDisplay disp = (PC_GresDisplay) event.getComponent();
					PC_RedstoneWorkType rwt = types[disp.getDisplayObject().getActiveDisplayObjectIndex()];
					tileEntity.setRedstoneWorkType(rwt);
				}
			}
		}
		
	}
	
	private static class IOConfigEventListener implements PC_IGresEventListener{
		
		private PC_ISidedInventory inventory;
		private PC_GresDisplay[] sides;
		
		public IOConfigEventListener(PC_ISidedInventory inventory, PC_GresDisplay[] sides){
			this.inventory = inventory;
			this.sides = sides;
		}

		@Override
		public void onEvent(PC_GresEvent event) {
			if(event instanceof PC_GresMouseButtonEvent){
				PC_GresMouseButtonEvent bEvent = (PC_GresMouseButtonEvent) event;
				if(bEvent.getEvent()==Event.CLICK){
					for(int i=0; i<sides.length; i++){
						if(sides[i] == bEvent.getComponent()){
							inventory.setSideGroup(i, sides[i].getDisplayObject().getActiveDisplayObjectIndex()-1);
							break;
						}
					}
				}
			}
		}
		
	}
	
}
