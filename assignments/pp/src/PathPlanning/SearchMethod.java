
package PathPlanning;

import java.util.ArrayList;

/**
 * Base class for a search algorithm.
 * @author Pablo Muñoz
 */
public abstract class SearchMethod {

    /** DEM and traversal costs grid. */
    protected Map map;
    /** Heuristics used. */
    protected short heuristic;
    /** Specify if Z values are used during search. */
    protected boolean withz;
    /** Specify if transversal costs values are used during search. */
    protected boolean withc;
    /** Specify if the algorithm is any-angle. */
    protected boolean anyangle;
    /** Number of expanded nodes. */
    protected int expnodes;
    /** Number of heading changes. */
    private int hchanges;
    /** Average degree heading change. */
    private double beta;
    /** Total . */
    private double degrees;
    /** Path length. */
    private double pathlength;
    /** CPU time spent in search (nanosec). */
    private double cputime;
    /** Start node. */
    protected Node start;
    /** Destination node. */
    protected Node goal;
    /** Last path found by the algorithm. null if there is no path. */
    private ArrayList<Node> path;
    /** Name of the search algorithm implemented. */
    private String name;
    /** Information about JVM to get CPU time of the thread.
    @deprecated */
    private java.lang.management.ThreadMXBean thread;
    /** Euclidean distance from start node to goal node. */
    protected float dsg;
    /** Indicates if the class was previously instantiated. */
    private boolean instantiated = false;
    
    /**
     * Full constructor for search method.
     * @param nam name of the algorithm.
     * @param dem terrain info.
     * @param init start node.
     * @param end goal node.
     * @param h heuristic used for search.
     * @param z true for use Z values.
     * @param c true for use transversal costs.
     * @param a true for any-angle algorithm. False otherwise.
     */
    public SearchMethod(String nam, Map dem, Node init, Node end, short h, boolean z, boolean c, boolean a)
    {
        name = nam;
	anyangle = a;
        map = dem;
        heuristic = h;
        withz = z;
        withc = c;
	restart_search(init, end);
    }
    
    /** 
     * Create the search process with new start and goal nodes.
     * @note DEM information does not change.
     */
    public final void restart_search(Node init, Node end)
    {
        expnodes = 0;
        hchanges = 0;
        beta = 0;
        degrees = Double.MAX_VALUE;
        pathlength = Double.MAX_VALUE;
	path = null;
	start = null;
	goal = null;
        if(!set_start(init))
            System.out.println("ERROR: start node invalid: "+init.getXYZ());
        if(!set_goal(end))
            System.out.println("ERROR: goal node invalid: "+end.getXYZ());
        start.setG(0);
	if(start != null && goal != null)
	    dsg = Heuristics.Heuclidean(start, goal);
        if(instantiated)
        {
	   map.restart_map();
           clear_internal_data();
        }
        else
	   instantiated = true;
    }
    
    /**
     * Allows to change the name of the search algorithm.
     * @param newname new algorithm name.
     */
    protected void change_name(String newname)
    {
        name = newname;
    }

    /**
     * Allows to change any-angle mode of the algorithm.
     * @param newmode true for any-angle algorithm. False otherwise.
     */
    protected void change_mode(boolean newmode)
    {
	anyangle = newmode;
    }
    
    /**
     * Clear internal data of the search algorithm (open list, parent's nodes...) 
     * in order to restart the search algorithm. Called after restart_search. 
     */
    protected abstract void clear_internal_data();
    
    /**
     * Search a path using the map information and start and goal points.
     * @return True if a path is found or false if no path found.
     */
    public abstract boolean search();
    
    /**
     * Check if all information needed to search is valid.
     * @return true if the search can be executed.
     */
    protected boolean check_valid_data()
    {
        return (map.valid_dem() && start != null && goal != null && map.valid_cost_map());
    }
    
    /**
     * Set a new start node.
     * @param init new start position.
     * @return true if the position is valid, false otherwise.
     */
    public final boolean set_start(Node init)
    {
        if(init != null && map.valid_pos(init.getX(), init.getY()) && (map.get_tcost(init) <= Map.MAX_COST || map.get_isDmode()) && !init.isObstacle() && map.get_succesors_without_obstacles(init).size() > 0)
        {
            start = new Node(init.getX(), init.getY(), map.get_node(init.getX(),init.getY()).getZ());
            return true;
        }
        else
            return false;
    }
    
    /**
     * Set a new goal node.
     * @param end new destination position.
     * @return true if the position is valid, false otherwise.
     */
    public final boolean set_goal(Node end)
    {
        if(end != null && map.valid_pos(end.getX(), end.getY()) && (map.get_tcost(end) <= Map.MAX_COST || map.get_isDmode()) && !end.isObstacle() && map.get_succesors_without_obstacles(end).size() > 0)
        {
            goal = new Node(end.getX(), end.getY(), map.get_node(end.getX(),end.getY()).getZ());
            return true;
        }
        else
	    return false;
    }
   
    /**
     * Calculates a particular heuristics for two nodes.
     * @param pos start node.
     * @param dest destination node.
     * @return the heuristic value.
     */
    protected float get_h(Node pos, Node succ, Node dest)
    {
        return Heuristics.CalculateH(heuristic, succ, dest);
    }

    /**
     * Calculates the cost to go from pos to dest node. If there is an obstacle
     * between pos and dest nodes, it returns -1.
     * @param pos start node.
     * @param dest destination node.
     * @param accumulate true for cumulative cost <pre>g(s,s')+g(s)</pre>
     * @return calculated cost value. -1 if there is an obstacle between nodes.
     */
    protected float get_g(Node pos, Node dest, boolean accumulate)
    {
	float costp = 0;
        if(accumulate)
            costp = pos.getG();
        int x0 = pos.getX();
        int y0 = pos.getY();
        int x1 = dest.getX();
        int y1 = dest.getY();
        int dx = Math.abs(x1-x0);
        int dy = Math.abs(y1-y0);
	if(anyangle && (dx > 1 || dy > 1)) // Avoid heavy calculation for adyacent nodes
        {
	      if(withc || withz)
	      {
              // COMPLEX LINE OF SIGHT 
		      float seg = -1;
		      if(seg > 0)
		         return seg + costp;
              else // No line of sight
                 return -1;
	      }
	      else
              // THETA* LINE OF SIGHT
              // Add code for line of sight for any-angle algorithms
              if(true)
                 return Heuristics.LongHyp(dx, dy) + costp;
              else // No line of sight
                 return -1;
        }
        else // Adyacent nodes
        {
	    if(map.get_tcost(x0, y0, x1, y1) > Map.MAX_COST)
	        return -1;
            float cost = 1;
            if(withc)
                cost = map.get_tcost(x0, y0, x1, y1);
            if(withz)
                return Heuristics.LongHypZ(dx, dy, dest.getZ() - pos.getZ()) * cost + costp;
            else
                return Heuristics.LongHyp(dx, dy) * cost + costp;
        }
    }
    
    /**
     * Access to last path. For algorithms like Dijkstra you can change the goal node
     * (@see set_goal) and use this function to get the path without a new search.
     * The ArrayList is a Node list.
     * @return The last path found or null if there is no path.
     */
    public ArrayList get_path()
    {
        if(start == null)
            return null;
        Node aux = map.get_node(goal.getX(), goal.getY()); 
        path = new ArrayList();
        path.add(aux);
        while(aux.getParent() != null && !aux.equals(start))
        {
            System.out.println(aux.toString());
            aux = aux.getParent();
            path.add(aux);
        }
        // Check last added node
        if(aux.equals(start) && path.size() > 1)
            return path;
        else
            return null;
    }
   
    /**
     * Get the cost of the last path.
     * @return The cost of the path or -1 if the path is not recovered yet
     * (call get_path) or there is no path.
     */
    public double get_path_cost()
    {
        if(path != null)
            return path.get(0).getG();
        else
            return -1;
    }

    /**
     * @return Length of the path. It not considers the traversal costs, only path length.
     */
    public double get_path_length()
    {
	if(path == null)
	    return -1;
        pathlength = 0;
        Node ant = path.get(0);
        for(int i=1; i < path.size(); i++)
        {
            // NOT VALID FOR ANY ANGLE
            if(!withz)
                pathlength += Heuristics.LongHyp(path.get(i).getX()-ant.getX(), path.get(i).getY()-ant.getY());
            else
                pathlength += Heuristics.LongHypZ(path.get(i).getX()-ant.getX(), path.get(i).getY()-ant.getY(), path.get(i).getZ()-ant.getZ());
            ant = path.get(i);
        }    
        return pathlength;
    }
    
    /**
     * @return Value of total degrees of the path.
     */
    public double get_path_degrees()    
    {
	if(degrees == Double.MAX_VALUE)
	    get_heading_changes();
	return degrees;
    }
    
    /**
     * @return Number of heading changes of the path.
     * -1 if the path is not recovered yet (call get_path) or there is no path.
     */
    public int get_heading_changes()
    {
        if(path == null)
            return -1;
        float dirnew, dirant;
        beta = 0;
        hchanges = 0;
        degrees = 0;
        float dx, dy;
        Node ant = path.get(path.size()-1);
        // Set new initial orientation
        try{
         dx = path.get(path.size()-2).getX() - ant.getX();
         dy = path.get(path.size()-2).getY() - ant.getY();
        }catch(ArrayIndexOutOfBoundsException e)
         {return 0;}
        // REMOVED
        dirnew = 1;
        dirant = dirnew;
        ant = path.get(path.size()-2);
        for(int i = path.size()-3; i >= 0; i--)
        {
            dx = path.get(i).getX() - ant.getX();
            dy = path.get(i).getY() - ant.getY();
            // REMOVED
            dirnew = 2;
            if(dirnew > 180)
                dirnew = 360 - dirnew;
            if(dirnew != dirant)
            {
                hchanges++;
                beta += Math.abs(dirnew - dirant);
                dirant = dirnew;
            }
            ant = path.get(i);
        }
        degrees = beta;
        if(hchanges > 0)
            beta = beta / hchanges;
        return hchanges;
    }
    
    /**
     * @return The beta parameter (average angle of heading changes).
     */
    public double get_beta()
    {
        return beta;
    }
    
    /**
     * @return Number of expanded vertex.
     */
    public int get_expanded_vertex()
    {
        return expnodes;
    }
    
    /**
     * Get the initial time of CPU at start of the search algorithm.
     */
    protected void start_cpu_counter()
    {
        //cputime = thread.getCurrentThreadCpuTime();
        cputime = System.currentTimeMillis();
    }
    
    /**
     * Set the total CPU time at the end of the search algorithm.
     */
    protected void end_cpu_counter()
    {
        //cputime = (thread.getCurrentThreadCpuTime() - cputime) / 1000000;
        cputime = System.currentTimeMillis() - cputime;
    }
    
    /**
     * Add specified time to the CPU time counter.
     * @param time time in milisec to add to the counter.
     */
    protected void add_cpu_time(double time)
    {
        cputime += time;
    }
    
    /**
     * @return Time spent to find a solution in milisec.
     */
    public double get_cpu_time()
    {
        return cputime;
    }

    /**
     * @return True if the algorithm is any-angle. False otherwise.
     */
    public boolean is_any_angle()
    {
	    return anyangle;
    }
    
    /**
     * @return The information of the algorithm and the used map.
     */
    @Override
    public String toString()
    {
        String temp;
        temp = "Search algorithm: " + name +  "\n" + map.toString() + "\n";
        if(start != null)
            temp += "Initial point: " + start.getXYZ();
        else
            temp += "No initial point set";
        if(goal != null)
            temp += " Goal point: " + goal.getXYZ();
        else
            temp += " No goal point set";
        return temp;
    }
    
    /**
     * @return A string with basic information of the search algorithm.
     */
    public String search_info()
    {
	    return "\nPath-planning algorithm: "+name + "\n" + map.toString() + "\n";
    }
    
    /**
     * @return A string with the number of steps and cost of the path.
     */
    public String path_info()
    {
        if(path != null && path.size() > 1)
            return "#p-cost  : " + get_path_cost() + "\n" +
                   "#h-change: " + get_heading_changes() + "\n" +
                   "#v-expans: " + expnodes + "\n" +
                   "#cpu-time: " + cputime + "\n" +
                   "#beta-avg: " + beta + "\n" +
                   "#degrees : " + degrees + "\n" +
		           "#nº-steps: " + path.size()+ "\n" +
                   (withc?"#p-length: " + get_path_length() + "\n":"") +
                   (withz? "\n#With Z":"" ) +
                   (withc? "\n#Whit C":"") +
		   (anyangle? "\n#Any-angle":"") +
		   "\n#"+name;
        else
            return "No path found yet.";
    }
    
    /** 
     * Print XYZ path nodes.
     */
    public void print_path()
    {
	if(path != null)
        for(int i = path.size()-1; i >= 0; i--)
            System.out.println(((Node)path.get(i)).getXYZ());
    }
    
}
