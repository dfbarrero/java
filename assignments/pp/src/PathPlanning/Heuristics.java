
package PathPlanning;

/**
 * Functions to compute heuristics for path planning.
 * @author Pablo muñoz
 */
public abstract class Heuristics {
    
    /** Heuristics definitions. */
    public final static short H_EUCLIDEAN   = 0;
    public final static short H_EUCLIDEAN_Z = 1;
    public final static short H_MANHATTAN   = 2;
    public final static short H_OCTILE      = 3;
    public final static short H_OCTILE_Z    = 4;
    
    /**
     * Calculate a particular heuristics for two nodes.
     * @param hfun heuristics function.
     * @param pos start node.
     * @param dest destination node.
     * @return the heuristic value or 1 if the heuristic identifier is not valid.
     */
    public static float CalculateH(short hfun, Node pos, Node dest)
    {
        switch(hfun)
        {
            case H_EUCLIDEAN:   return Heuclidean(pos, dest);
            case H_EUCLIDEAN_Z: return HeuclideanZ(pos, dest);
            case H_MANHATTAN:   return Hmanhattan(pos, dest);
            case H_OCTILE:      return Hoctile(pos, dest);
            case H_OCTILE_Z:    return HoctileZ(pos, dest);
            default: return 1;
        }
    }
        
    /** 
     * Calculate the euclidean heuristic for the two specified nodes (two dimensions).
     * @param pos start node.
     * @param dest destination node.
     * @return Heuristic value from pos to dest.
     */
    public static float Heuclidean(Node pos, Node dest)
    {
	return LongHyp(dest.getX() - pos.getX(), dest.getY() - pos.getY());
    }

    /**
     * Calculate the euclidean heuristic for the two specified nodes (three dimensions).
     * @param pos start node.
     * @param dest destination node.
     * @return Heuristic value from pos to dest using the Z value.
     */
    public static float HeuclideanZ(Node pos, Node dest)
    {
        return LongHypZ(dest.getX() - pos.getX(), dest.getY() - pos.getY(), dest.getZ() - pos.getZ());
    }

    /**
     * Calculate the Manhattan heuristic for the two specified nodes.
     * @param pos start node.
     * @param dest destination node.
     * @return Heuristic value from pos to dest using the Manhattan distance.
     */
    public static int Hmanhattan(Node pos, Node dest)
    {
        return Math.abs(pos.getX() - dest.getX()) + Math.abs(pos.getY() - dest.getY());
    }
    
    /**
     * Calculate the octile distance for the two specified nodes.
     * @param pos actual position node.
     * @param dest destination node.
     * @return Heuristic value from pos to dest using the octile distance.
     */
    public static float Hoctile(Node pos, Node dest)
    {
        int dx = Math.abs(pos.getX() - dest.getX());
        int dy = Math.abs(pos.getY() - dest.getY());
        int large, small;
        if(dx > dy)
        {
            large = dx;
            small = dy;
        }
        else
        {
            large = dy;
            small = dx;
        }
        return (float)Math.sqrt(2) * small + (large - small);
    }

    /**
     * Calculate the octile distance for the two specified nodes (three dimensions).
     * @param pos actual position node.
     * @param dest destination node.
     * @return Heuristic value from pos to dest using the octile distance and altitude variation.
     */
    public static float HoctileZ(Node pos, Node dest)
    {
        return Hoctile(pos, dest) + Math.abs(pos.getZ() - dest.getZ());
    }

    /**
     * Calculate the hypotenuse of a triangle using the other two sides.
     * @param dx side 1.
     * @param dy side 2.
     * @return The hypotenuse of the triangle.
     */
    public static float LongHyp(float dx, float dy)
    {
        return (float)Math.sqrt(dx*dx + dy*dy);
    }

    /**
     * Calculate the hypotenuse of a triangle using the other two sides and a third dimension.
     * @param dx side 1.
     * @param dy side 2.
     * @param dz third dimension.
     * @return The hypotenuse of the triangle.
     */
    public static float LongHypZ(float dx, float dy, float dz)
    {
	return (float)Math.sqrt(dx*dx + dy*dy + dz*dz);
    }
    
}
