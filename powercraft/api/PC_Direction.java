package powercraft.api;

import net.minecraftforge.common.util.ForgeDirection;


public enum PC_Direction {

	/** -Y */
    DOWN(0, -1, 0),

    /** +Y */
    UP(0, 1, 0),

    /** -Z */
    NORTH(0, 0, -1),

    /** +Z */
    SOUTH(0, 0, 1),

    /** -X */
    WEST(-1, 0, 0),

    /** +X */
    EAST(1, 0, 0),

    /**
     * Used only by getOrientation, for invalid inputs
     */
    UNKNOWN(0, 0, 0);

    public final int offsetX;
    public final int offsetY;
    public final int offsetZ;
    public final int flag;
    public static final PC_Direction[] VALID_DIRECTIONS = {DOWN, UP, NORTH, SOUTH, WEST, EAST};
    public static final PC_Direction[] OPPOSITES = {UP, DOWN, SOUTH, NORTH, EAST, WEST, UNKNOWN};
    // Left hand rule rotation matrix for all possible axes of rotation
    public static final PC_Direction[][] ROTATION_MATRIX = {
        {DOWN, UP, WEST, EAST, SOUTH, NORTH, UNKNOWN},
        {DOWN, UP, EAST, WEST, NORTH, SOUTH, UNKNOWN},
    	{EAST, WEST, NORTH, SOUTH, DOWN, UP, UNKNOWN},
    	{WEST, EAST, NORTH, SOUTH, UP, DOWN, UNKNOWN},
    	{NORTH, SOUTH, UP, DOWN, WEST, EAST, UNKNOWN},
    	{SOUTH, NORTH, DOWN, UP, WEST, EAST, UNKNOWN},
    	{DOWN, UP, NORTH, SOUTH, WEST, EAST, UNKNOWN},
    };
    
    public static final PC_Direction[] fromRotationY = {NORTH, EAST, SOUTH, WEST};

    private PC_Direction(int x, int y, int z) {
        this.offsetX = x;
        this.offsetY = y;
        this.offsetZ = z;
        this.flag = 1 << ordinal();
    }

    public static PC_Direction fromSide(int id){
        if (id >= 0 && id < VALID_DIRECTIONS.length)
        {
            return VALID_DIRECTIONS[id];
        }
        return null;
    }
    
    public static PC_Direction fromRotationY(int rotation) {
		return fromRotationY[((rotation%4)+4)%4];
	}
    
    public PC_Direction getOpposite(){
        return OPPOSITES[ordinal()];
    }

    public PC_Direction rotateOnce(PC_Direction axis){
    	return ROTATION_MATRIX[axis.ordinal()][ordinal()];
    }

	public PC_Direction rotate(PC_Direction axis, int times) {
		if(this==axis||this.getOpposite()==axis) return this;
		int ttimes = ((times %4) +4) %4;
		if(ttimes==0)
			return this;
		else if(ttimes==1)
			return rotateOnce(axis);
		else if(ttimes==2)
			return getOpposite();
		else if(ttimes==3)
			return rotateOnce(axis.getOpposite());
		return null;
	}

    public static PC_Direction fromForgeDirection(ForgeDirection side) {
		return fromSide(side.ordinal());
	}
    
	public ForgeDirection toForgeDirection() {
		return ForgeDirection.getOrientation(ordinal());
	}
	
	public int timesToRotate(PC_Direction from, PC_Direction to, PC_Direction axis){
		if(from==to) return 0;
		if(from.rotateOnce(axis)==to) return 1;
		if(from.getOpposite().rotateOnce(axis)==to) return -1;
		if(from.rotate(axis, 2)==to) return 2;
		throw new RuntimeException("You can't rotate from "+from+" to "+to+" via "+axis+"-axis!!");
	}

}
