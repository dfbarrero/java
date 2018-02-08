
package PathPlanning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implements the Dijkstra algorithm to search a path in a DEM.
 * @author Pablo Muñoz
 */
public class Dijkstra extends SearchMethod {
   
    /** Open nodes list, sorted by F. */
    private List<Node> open;
  
    /**
     * Empty constructor.
     */
    public Dijkstra()
    {
        super("",null,null,null,(short)0,false,false,false);
    }
    
    /**
     * Constructor. Create the open vector with all nodes.
     * @param dem the DEM info.
     * @param init start node.
     * @param end goal node.
     * @param heur not used in Dijkstra algorithm.
     * @param withz true for use Z values.
     * @param withc true for use transversal costs.
     */
    public Dijkstra(Map dem, Node init, Node end, short heur, boolean withz, boolean withc)
    {
        super("Dijkstra", dem, init, end, heur, withz, withc, false);
	open = new ArrayList();
        // Copy all nodes to the open table and set the cost to ~INF.
        for(int j = 0; j < map.get_nrows(); j++)
            for(int i = 0; i < map.get_ncols(); i++)
            {
                map.get_node(i, j).setG(Float.MAX_VALUE);
                map.get_node(i, j).setParent(null);
                open.add(map.get_node(i, j));
            }
    }
    
    @Override
    /**
     * Clear internal data of the algorithm.
     */
    protected void clear_internal_data()
    {
	open.clear(); // References in open list lost in DEM clean (new nodes)
	for(int j = 0; j < map.get_nrows(); j++)
            for(int i = 0; i < map.get_ncols(); i++)
            {
                map.get_node(i, j).setG(Float.MAX_VALUE);
                map.get_node(i, j).setParent(null);
                open.add(map.get_node(i, j));
            }
    }
    
    /**
     * Search a path using the Dijkstra algorithm.
     * @return True if a path is found or false if no path found.
     */
    @Override
    public boolean search()
    {
        if(!check_valid_data())
            return false;
        // Set initial state
        map.get_node(start.getX(), start.getY()).setG(0);
        Node pos;
        // Auxiliary variables
        float gsuc;
        int x=0, y=0;
        ArrayList<Node> succ = new ArrayList();
        
        // Dijkstra expansion
        start_cpu_counter();
        while(!open.isEmpty())
        {
            // Get better node
            pos = open.remove(0);
            expnodes++;
          
            // Generate succesors for actual point
            succ = map.get_succesors_without_obstacles(pos);
            for(int i=0; i < succ.size(); i++)
            {
                x = succ.get(i).getX();
                y = succ.get(i).getY();
                gsuc = get_g(pos, map.get_node(x, y), true);        
                // If new path to node is more expensive, not modify
                if(map.get_node(x, y).getG() > gsuc)
                {
                    // Replace node data
                    map.get_node(x, y).setParent(pos);
                    map.get_node(x, y).setG(gsuc);
                }
            }// End of succesor check
            // Sort open list
            Collections.sort(open);
            print_open();
        }
        // Search finish
        end_cpu_counter();
	if(map.get_node(goal.getX(), goal.getY()).getParent() != null)
	    return true;
	else
	    return false;
    }
    
    /**
     * Get the path from the initial node to another destination node.
     * @param dest new destination node.
     * @return The new path or null if there is no path or dest node is invalid.
     */
    public ArrayList get_path(Node dest)
    {
        if(set_goal(dest))
            return get_path();
        else
            return null;
    }
    
    /**
     * Print the open list.
     */
    private void print_open()
    {      
        System.out.println("Open:");
        for(int i=0; i < open.size(); i++)
            System.out.println("    " + open.get(i).toString());
        java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
        try{ 
            br.readLine();
        }catch(Exception ioe){}
        System.out.println();
    }
    
}
