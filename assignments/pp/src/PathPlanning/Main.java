
package PathPlanning;

import java.util.ArrayList;

/**
 * Main class. Read arguments and executes path planning algorithm.
 * @author Pablo Muñoz
 */
public class Main {

    /**
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
    try{
        
        if(args.length < 5)
	    {
            System.out.print("Usage:");
            System.out.println("\n>For file generation:");
            System.out.println("  -g{z,c} n-cols n-rows value outfile\n");
            System.out.println("     z for DEM generation with 'value' as number of iteration for hill algorithm.");
            System.out.println("     c for cost file, 'value' is the percentage of obstacles ");
            System.out.println("       (less 40% to guarantee convergence of the algorithm.");
            System.out.println("\n>To search a path:");
            System.out.println("  z-file c-file Xi Yi Xg Yg -alg [-z] [-c] pathfile [-w[s]]\n");
            System.out.println("     -z specifies to use altitude in search. Uses lineal interpolation for");
            System.out.println("        any-angle algorithms. Using -zq changes to quadratic interpolation.");
            System.out.println("     -c specifies to use transversal costs in search.");
            System.out.println("     -alg can be: -d -> Dijkstra");
            System.out.println("     -w[s] show a window with map and path representation, s for scale zoom [3,100].");
            System.exit(-1);
	    }
        // Main data
	    int nextarg = -1;
        Map map;
        
        /******************************
         *  GENERATE Z_FILE OR C_FILE
         ******************************/
        String aux = args[++nextarg];
        if(aux.equals("-gz") || aux.equals("-gc"))
	    {
            int cols, rows;
            cols = Integer.valueOf(args[++nextarg]);
            rows = Integer.valueOf(args[++nextarg]);
            int maxz = Integer.valueOf(args[++nextarg]);
            if(cols < 1 || rows < 1 || maxz < 0) // Check files/rows value
            {
                System.out.println("Invalid col/row/z value (must be > 0, z >=0) [" + cols + "," + rows + "](" + maxz + ")");
                System.exit(-1);
            }
            switch(aux.charAt(2))
            {
                case 'z':
                    FileManager.GenerateZfile(args[++nextarg], cols, rows, maxz);
                    break;
                case 'c':
                    FileManager.GenerateCfile(args[++nextarg], cols, rows, maxz);
                    break;
                default:
                    System.out.println("Invalid generation method.");
                    System.exit(-1);
            }
            System.exit(0);
        }
        
        /****************
         *  SEARCH PATH
         ****************/
        if(args.length < 7)
        {
            System.out.println("Too few parameters to search a path.");
            System.exit(-1);
        }
        // Open the Z-file or create a 10 random altitude DEM parsing the arguments
        try{
            // Less than 3 arguments valid with z-file
            if(args.length < 3)
                throw new java.lang.NumberFormatException();
            map = new Map(Integer.parseInt(args[nextarg]), Integer.parseInt(args[++nextarg]), 10, 10, false);
        }catch(NumberFormatException notNumber){
            // DEM file specified
            String cfile = "";
            if(args.length >= 8) // 8 args or more implies cost file
            {
                cfile = args[1];
                nextarg++;
            }
            map = new Map(args[0], cfile);
        }
        // Check if DEM file is valid
        if(!map.valid_dem())
        {
            System.out.println("DEM is invalid.");
            System.exit(-1);
        }
        // Get initial and goal points
        int xs = 0, ys = 0, xg = 0, yg = 0;
        try{
            xs = Integer.valueOf(args[++nextarg]);
            ys = Integer.valueOf(args[++nextarg]);
            xg = Integer.valueOf(args[++nextarg]);
            yg = Integer.valueOf(args[++nextarg]);
        }catch(Exception e)
        {
            System.out.println("Check arguments for initial and goal position. " + e.toString());
            System.exit(-1);
        }
        if(!map.valid_pos(xs, ys) || !map.valid_pos(xg, yg))
        {
            System.out.println("Invalid initial or goal point; X and Y must be greater or equal to 0 and less than cols or rows.");
            System.exit(-1);
        }
        // Read search algorithm
        if(args[nextarg+1].length() < 2)
        {
            System.out.println("Invalid search algorithm.");
            System.exit(-1);
        }
        char alg = args[++nextarg].charAt(1);
        // Check if want to use the Z and/or C values
        boolean withz = false;
        boolean withc = false;
        short heur = Heuristics.H_EUCLIDEAN;
        try{
	     if(args[nextarg+1].equals("-c") || args[nextarg+2].equals("-c"))
             withc = true;
         if(args[nextarg+1].startsWith("-z") || args[nextarg+2].startsWith("-z"))
         {
             withz = true;
             heur = Heuristics.H_EUCLIDEAN_Z;
         }
        }catch(ArrayIndexOutOfBoundsException a){}
        if(withz) nextarg++;
        if(withc) nextarg++;
        // Check if want gui visualization
        boolean gui = false;
        int scale = 0;
        if(args[args.length-1].startsWith("-w"))
             gui = true;
        if(gui && args[args.length-1].length()>2)
            scale = Integer.valueOf(args[args.length-1].substring(2));        
        
        // <<<<< BEGIN SEARCH >>>>>
        SearchMethod algorithm;
        switch(alg)
        {
            case 'd': // Dijkstra algorithm
                algorithm = new Dijkstra(map, map.get_node(xs, ys), map.get_node(xg, yg), heur, withz, withc);
                break;
    	    default:
                algorithm = new Dijkstra();
                System.out.println("Invalid search algorithm: " + alg);
                System.exit(-1);
        }

        // Perform search and print relevant information
        algorithm.search();        
        ArrayList path = algorithm.get_path();
        aux = algorithm.path_info();
        System.out.println("-------------------------------------------------");
        System.out.println(algorithm.toString());
        System.out.println(aux);
        
        // Output file with path
        if(path != null) // If there is a path, write the output file
        {
            FileManager.WritePathFile(args[++nextarg], path, aux);
            System.out.println("Output file with found path is " + args[nextarg]);
        }
        else
        {
            System.out.println("No path found");
            System.exit(0);
        }
        System.out.println("-------------------------------------------------");
        
        // Check GUI visualization
        if(gui)
        {
            GUI.Panel guimap = new GUI.Panel(map, path, scale, algorithm.toString()+"\n"+algorithm.path_info());
            guimap.setVisible(true);
        }

        if(!gui)
           System.exit(0);
        //----------------------------------/
        }catch(ArrayIndexOutOfBoundsException a){
           System.out.println("Invalid argument index "+a.toString());
           System.exit(-1); 
        }catch(Exception e){
           System.out.println(e.toString());
           System.exit(-1);
        }
    }
}

