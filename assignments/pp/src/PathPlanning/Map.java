
package PathPlanning;

import java.util.ArrayList;
import java.util.Random;

/**
 * Contain the data and operations of the terrain model.
 * The map can be represented as shown in the figure below. n represents a node
 * with altitude (z) and cost (c) values. 
 * <pre>
 * !D mode:                  D mode:
 * x→ 0   1   2              x→   0  1  2  3
 * y↓┌--┬--┬--┐              y↓ 0 z─-z─-z─-z
 * 0 │n0│n1│n2│                   │c0│c1│c2│
 *   ├--┼--┼--┤                 1 z─-z─-z─-z
 * 1 │n3│n4│n5│                   │c3│c4│c5│
 *   ├--┼--┼--┤                 2 z─-z─-z─-z
 * 2 │n6│n7│n8│                   │c6│c7│c8│
 *   └--┴--┴--┘                 3 z─-z─-z─-z
 * </pre> 
 * @author Pablo Muñoz
 */
public class Map {
    
    /** Number of columns. */
    private int cols;
    /** Number of rows. */
    private int rows;
    /** DEM information, Nodes of the grid. */
    private Node dem[][];
    /** Traversal cost grid. */
    public float cost[][];
    /** Type of transversal costs. */
    private boolean D;
    /** Maximum cost of a point of the traversal cost grid. Strictly greater than this value implies an obstacle. */
    public static final float MAX_COST = 8;
    /** Minimum altitude value of the map. */
    private float minalt;
    /** Maximum altitude value of the map. */
    private float maxalt;
    
    /**
     * Create the internal data structures.
     * @param ncols number of columns of the DEM.
     * @param nrows number of rows of the DEM.
     * @param dmode mode for transversal cost grid. D mode (dmode=true) is used for D* and similar ones.
     */
    private void initialize(int ncols, int nrows, boolean dmode)
    {
        cols = ncols;
        rows = nrows;
        minalt = Float.MAX_VALUE;
        maxalt = Float.MIN_VALUE;
        dem = new Node[cols][rows];
        D = dmode;
        if(D)
            cost = new float[cols-1][rows-1];
        else
            cost = new float[cols][rows];
    }
    
    /**
     * Copy constructor of Map class.
     * @param map DEM to copy.
     */
    public Map(final Map map)
    {
        initialize(map.get_ncols(), map.get_nrows(), map.get_isDmode());
        for(int i = 0; i < cols; i++)
            for(int j=0; j < rows; j++)
            {
                this.dem[i][j] = new Node(map.get_node(i, j));
                if(!D)
                    this.cost[i][j] = map.get_tcost(i, j);
                else
                    if(i < cols-1 && j < rows-1)
                        this.cost[i][j] = map.get_tcost(i+j*(cols-1));
            }
        minalt = map.min_alt();
        maxalt = map.max_alt();
    }
    
    /**
     * Generate a new map with random values for Z and transversal costs. 
     * @param ncols number of columns of the DEM.
     * @param nrows number of rows of the DEM.
     * @param maxz maximum Z value for nodes. 0 for plain terrain.
     * @param maxc maximum traversal cost. 0 for zero uniform cost terrain.
     * @param dmode mode for transversal cost grid. D mode (dmode=true) is used for D* and similar ones.
     */
    public Map(int ncols, int nrows, int maxz, int maxc, boolean dmode)
    {
        initialize(ncols, nrows, dmode);
        Random rand = new Random();
        // Generate random values and fill DEM matrix
        for(int j = 0; j < rows; j++)
            for(int i = 0; i < cols; i++)
            {
                dem[i][j] = new Node(i, j, rand.nextInt(maxz + 1));
                if(!D || (D && i < cols-1 && j < rows-1))
                    cost[i][j] = rand.nextInt(maxc)+1;
            }
    }
    
    /**
     * Charge a map with Z and cost values from the specified files. Number of columns and rows
     * are automatically detected while read the file. If the cost file is null or invalid, it
     * set to 0 cost for all positions. The dmode is automatically adjust depending on the file read.
     * If the DEM file is invalid, it creates a new DEM of 50x50 with 0 Z values.
     * @param zfile file relative path with the Z values matrix.
     * @param cfile file relative path with the transversal cost matrix.
     */
    public Map(String zfile, String cfile)
    {
        minalt = Float.MAX_VALUE;
        maxalt = Float.MIN_VALUE;
        // Read files and set columns and rows values.
        dem = FileManager.ReadZfile(zfile);
        if(dem != null)
        {
            cols = dem.length;
            rows = dem[0].length;
        }
        cost = FileManager.ReadCfile(cfile);
        if(cost == null && dem != null)
        {
            cost = new float[cols-1][rows-1];
            for(int i=0; i < cols-1; i++)
                for(int j=0; j < rows-1; j++)
                    cost[i][j] = 1;
        }
        if(dem == null)
        {
            cols = cost.length;
            rows = cost[0].length;
            dem = new Node[cols][rows];
            for(int i=0; i < dem.length; i++)
                for(int j=0; j < dem[0].length; j++)
                    dem[i][j] = new Node(i, j, 0);
        }
        // If the dimension of DEM and cost matrix are the same, then not in D mode.
        if(dem.length == cost.length && dem[0].length == cost[0].length)
            D = false;
        else
            D = true;
    }
    
    /**
     * Erase all modified data of nodes in the DEM and set it to default values.
     */
    public void restart_map()
    {
        for(int i=0; i < cols; i++)
            for(int j=0; j < rows; j++)
                dem[i][j] = new Node(i, j, dem[i][j].getZ());
    }
    
    /**
     * Allows to change D mode from !D to D or vice-versa (only when object created with !D mode).
     * @return True if the change is valid, false otherwise.
     */
    public boolean change_dmode()
    {
        // Change from !D to D is allowed, last column and row are ignored.
        // From D mode change is only allowed when cost matrix has same length
        // than DEM matrix (this is, object created in !D mode).
        if(!D || (dem.length == cost.length && dem[0].length == cost[0].length))
        {
            D = !D;
            return true;
        }
        else
            return false;
    }

    /**
     * Get the node of the grid at desired position.
     * @param col column or x position of the node [0,columns).
     * @param row row or y position of the node [0,rows).
     * @return The node or null if the position is not valid.
     */
    public Node get_node(int col, int row)
    {
        if(valid_pos(col, row))
            return dem[col][row];
        else
            return null;
    }
    
    /**
     * Obtain a list with the valid successors of a node. Not considers the cost matrix.
     * @param node node to get its successors.
     * @return A list with the successors of the node. Null if there is not successors.
     */
    public ArrayList get_succesors(Node node)
    {
        ArrayList<Node> temp = new ArrayList();
        // Generate successors for actual point
        for(int j = node.getY()-1; j < node.getY()+2; j++)
            for(int i = node.getX()-1; i < node.getX()+2; i++)
                // Check valid col,row, not same node and not an obstacle
                if(valid_pos(i, j) && !(i == node.getX() && j == node.getY()) && !dem[i][j].isObstacle())
                    temp.add(dem[i][j]);
        return temp;
    }
    
    /**
     * Obtain a list with the valid successors of a node. It considers the cost matrix.
     * @param node node to get its successors.
     * @return A list with the successors of the node. Null if there is not reachable successors.
     */
    public ArrayList get_succesors_without_obstacles(Node node)
    {
        ArrayList<Node> temp = get_succesors(node);
        // Check if any succesor is unreachable due to cost matrix
        int i = 0;
        while(i < temp.size())
        {
            if(!D)
            {
                if(cost[temp.get(i).getX()][temp.get(i).getY()] > MAX_COST) // Cost greater than MAX_COST is an obstacle
                    temp.remove(i);
                else
                    i++;
            }
            else
                if(get_tcost(node.getX(), node.getY(), temp.get(i).getX(), temp.get(i).getY()) > MAX_COST)
                    temp.remove(i);
                else
                    i++;
        }
        return temp;
    }

    /** 
     * Get the associated transversal cost to the (x,y) node.
     * @param x x position of the node.
     * @param y y position of the node.
     * @return The associated transversal cost or MAX_COST+1 if the position is invalid.
     */
    public float get_tcost(int x, int y)
    {
        if(valid_pos(x, y))
        {
            if(!D)
                return cost[x][y];
            else
            {
                if(x < (cols-1) && y < (rows-1))
                    return cost[x][y];
                if(x == (cols-1) && y < (rows-1))
                    return cost[x-1][y];
                if(x < (cols-1) && y == (rows-1))
                    return cost[x][y-1];
                if(x == (cols-1) && y == (rows-1))
                    return cost[x-1][y-1];
                else
                    return MAX_COST+1;
            }
        }
        else
            return MAX_COST+1;
    }
    
    /** 
     * Get the associated transversal cost to the (x,y) node.
     * @param node node to get cost.
     * @return The associated transversal cost or MAX_COST if the position is invalid.
     */
    public float get_tcost(Node node)
    {
        return get_tcost(node.getX(), node.getY());
    }
    
    /** 
     * Access to the cost value of the grid, numbering it from 0 to:
     * <br/><li/> <b>D mode</b> <i>(cols-1)*(rows-1)</i>
     * <br/><li/> <b>!D mode</b> <i>(cols*rows)-1</i>
     * @param cpos number of grid to get cost.
     * @return Grid cost of the desired number or MAX_COST+1 if the number is not valid.
     */
    public float get_tcost(int cpos)
    {
        if(cpos >= 0 && ((D && cpos < (cols-1)*(rows-1)) || (!D && cpos < cols*rows)) )
        {
            if(!D)
                return cost[cpos - cols * (int)(cpos / cols)][(int)(cpos / cols)];        
            else
                return cost[cpos - (cols-1) * (int)(cpos / (cols-1))][(int)(cpos / (cols-1))];
        }
        else
            return MAX_COST+1;
    }
    
    /** 
     * Get the transversal cost value between (x1,y1) and (x2,y2) nodes. If not D mode, the cost is 
     * the sum of half transversal costs of each node. For D mode, the cost is the minimum traversal
     * cost between the nodes. (x1,y1) and (x2,y2) must be adjacent.
     * @param x1 x position of the first node.
     * @param y1 y position of the first node.
     * @param x2 x position of the second node.
     * @param y2 y position of the second node.
     * @return The desired transversal cost or MAX_COST+1 if at least one node position is invalid.
     */
    public float get_tcost(int x1, int y1, int x2, int y2)
    {
        if(valid_pos(x1, y1) && valid_pos(x2, y2))
            if(!D) // For not D mode is half cost per node
            {
		if(cost[x2][y2] > MAX_COST) // Objective is obstacle
		    return MAX_COST+1;
                if(x1 == x2 || y1 == y2)
                    return (cost[x1][y1] + cost[x2][y2]) / 2;
                else
                    return (cost[x1][y1] + cost[x2][y2]) / (float)(Math.sqrt(2)/2);
            }
            else // For D mode is the minimum cost between nodes
            {
                // Select the move using (x'-x)+(y'-y)·10
                switch((x2-x1) + (y2-y1)*10)
                {
                    case 1:   // →
                        return Math.min(get_tcost(x1, y1), get_tcost(x1, y1-1));
                    case -1:  // ←
                        return Math.min(get_tcost(x2, y2), get_tcost(x2, y2-1));
                    case 10:  // ↓
                        return Math.min(get_tcost(x1, y1), get_tcost(x1-1, y1));
                    case -10: // ↑
                        return Math.min(get_tcost(x2, y2), get_tcost(x2-1, y2));
                    case 11:  // ↘
                        return get_tcost(x1, y1);
                    case -11: // ↖
                        return get_tcost(x2, y2);//x(y)2=x(y)1 -1
                    case 9:   // ↙
                        return get_tcost(x2, y1);
                    case -9:  // ↗
                        return get_tcost(x1, y2);
		    case 0:
			return 0;
                    default:
                        return MAX_COST+1;
                }
            }
        else
            return MAX_COST+1;
    }
        
    /** 
     * Get the traversal cost value between start and destination nodes. If not D mode, the cost is 
     * the sum of half transversal costs of each node. For D mode, the cost is the minimum traversal
     * cost between the nodes. Nodes must be adjacent.
     * @param start start node.
     * @param dest destination node.
     * @return The desired transversal cost or MAX_COST+1 if at least one node position is invalid.
     */
    public float get_tcost(Node start, Node dest)
    {
        return get_tcost(start.getX(), start.getY(), dest.getX(), dest.getY());
    }
    
    /**
     * Check if a (x,y) position is contained in the map.
     * @param x x position [0,cols).
     * @param y y position [0,rows).
     * @return True if the position is valid, false otherwise.
     */
    public boolean valid_pos(int x, int y)
    {
        return (x >= 0 && x < cols && y >= 0 && y < rows);
    }

    /**
     * Check if a (x,y) position is contained in the cost map.
     * @param x x position [0,cols) for center-node, [0,cols-1) for corner-node.
     * @param y y position [0,rows) for center-node, [0,rows-1) for corner-node.
     * @return True if the position is valid, false otherwise.
     */
    public boolean valid_cost_pos(int x, int y)
    {
        if(D)
            return (x >= 0 && x < cols-1 && y >= 0 && y < rows-1);
        else    
            return (x >= 0 && x < cols && y >= 0 && y < rows);
    }
    
    /**
     * @return True if the DEM is valid, false if DEM is null or 0 dimension.
     */
    public boolean valid_dem()
    {
        return dem!= null && dem.length > 0 && dem[0].length > 0;
    }
    
    /**
     * @return True if the transversal cost grid is valid for actual D mode.
     */
    public boolean valid_cost_map()
    {
        if(!D)
            return cost!= null && cost.length == cols && cost[0].length == rows;
        else
            return cost!=null && cost.length == cols-1 && cost[0].length == rows-1;
    }
    
    /**
     * @return The number of columns of the DEM.
     */
    public int get_ncols()
    {
        return cols;
    }
    
    /**
     * @return The number of rows of the DEM.
     */
    public int get_nrows()
    {
        return rows;
    }
        
    /** 
     * @return The minimum altitude value of the map.
     */
    public float min_alt()
    {
        if(minalt == Float.MAX_VALUE)
            for(int i=0; i<cols; i++)
                for(int j=0; j<rows; j++)
                    if(dem[i][j].getZ() < minalt)
                        minalt = dem[i][j].getZ();
        return minalt;
    }
    
    /** 
     * @return The maximum altitude value of the map.
     */
    public float max_alt()
    {
        if(maxalt == Float.MIN_VALUE)
            for(int i=0; i<cols; i++)
                for(int j=0; j<rows; j++)
                    if(dem[i][j].getZ() > maxalt)
                        maxalt = dem[i][j].getZ();
        return maxalt;
    }
    
    /**
     * @return The actual D mode of the grid. True if map is D mode.
     */
    public boolean get_isDmode()
    {
        return D;
    }
 
    /**
     * Print the complete DEM grid with transversal costs.
     */
    public void print_full_grid()
    {
        if(!D)
            print_grid_not_dmode();
        else
            print_grid_dmode();
    }

    /**
     * Function to print the grid without D mode.
     */
    private void print_grid_not_dmode()
    {
        for(int j=0; j < rows; j++)
        {
            for(int i=0; i < cols; i++)
                System.out.print(" " + dem[i][j].getZ() + "[" + cost[i][j] + "]");
            System.out.println(" ");
        }
    }
    
    /**
     * Function to print the DEM values.
     */
    private void print_grid_dmode()
    {   
        float c;
        for(int j=0; j < rows; j++)
        {
            for(int i=0; i < cols; i++)
                 System.out.print("   " + dem[i][j].getZ());
            System.out.println("   ");
            System.out.print("   ");
            for(int i=0; i < cols-1; i++)
                if(j < rows-1)
                {
                    c = get_tcost(i+j*(cols-1));
                    System.out.print("   " + ((c > MAX_COST)? " X " : Float.toString(c)));
                }
            System.out.println("");
        }
    }

    /**
     * Generate a string with information about the map.
     * @return A string with the relevant data of the map.
     */
    @Override
    public String toString()
    {
        return "Map of " + cols + "x" + rows + " (cols x rows) using " + (D? "corner-node" : "center-node");
    }
    
    /**
     * Function to print a path.
     * @param path A valid path array.
     */
    public static void printPath(ArrayList<Node> path)
    {
        for(int i=0; i < path.size(); i++)
            System.out.println(path.get(i).getXYZ());
    }

}
