
package PathPlanning;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * Set of functions to read and write files with DEM info and to write a path. All in a format readable from gnuplot.
 * @author Pablo Muñoz
 */
public abstract class FileManager {
    
    /**
     * Read the Z values file and store the information into a matrix.
     * @param file Relative path to the file to be read.
     * @return Null if there is any problem or and DEM map otherwise.
    */
    public static Node[][] ReadZfile(String file) {  
        String abspath = new File("").getAbsolutePath();
        
        try{
            FileReader fr = new FileReader(abspath + File.separator + file);
            BufferedReader zfile = new BufferedReader(fr);

            int cols, rows = 1;
            // Read first row to get number of cols
            StringTokenizer st = new StringTokenizer(zfile.readLine());
            cols = st.countTokens();
            while(zfile.readLine() != null)
                rows++;
            // Reset buffer reader and create the array
            fr = new FileReader(abspath + File.separator + file);
            zfile = new BufferedReader(fr);
            Node temp[][] = new Node[cols][rows];

            // Read file
            for(int j = 0; j < rows; j++)
            {
                st = new StringTokenizer(zfile.readLine());
                for(int i = 0; i < cols; i++)
                    if(st.hasMoreTokens())
                    temp[i][j] = new Node(i, j, Float.valueOf(st.nextToken()));
            }
            
            // Close file and return array
            zfile.close();
            return temp;
        
        }catch(FileNotFoundException nf){
            System.out.println("No input DEM file found: " + nf.toString());
        }catch(IOException ioe){
            System.out.println("Input/output error: " + ioe.toString());
        }
        return null; // Fail to charge
    }
    
    /**
     * Read the transversal cost values file and store the information into a matrix.
     * @param file Relative path to the file to be read.
     * @return Null if there is any problem or the matrix cost otherwise.
    */
    public static float[][] ReadCfile(String file) {  
        String abspath = new File("").getAbsolutePath();
        
        try{
            FileReader fr = new FileReader(abspath + File.separator + file);
            BufferedReader zfile = new BufferedReader(fr);

            int cols, rows = 1;
            // Read first row to get number of cols
            StringTokenizer st = new StringTokenizer(zfile.readLine());
            cols = st.countTokens();
            while(zfile.readLine() != null)
                rows++;
            // Reset buffer reader and create the array
            fr = new FileReader(abspath + File.separator + file);
            zfile = new BufferedReader(fr);
            float temp[][] = new float[cols][rows];

            // Read file
            for(int j = 0; j < rows; j++)
            {
                st = new StringTokenizer(zfile.readLine());
                for(int i = 0; i < cols; i++)
                {
                    temp[i][j] = Float.valueOf(st.nextToken());
                    if(temp[i][j] == 0)
                        temp[i][j] = 1; // Prevent 0 cost
                }
            }
            
            // Close file and return array
            zfile.close();
            return temp;
        
        }catch(FileNotFoundException nf){
            System.out.println("No input COST file found: " + nf.toString());
        }catch(IOException ioe){
            System.out.println("Input/output error: " + ioe.toString());
        }
        return null; // Fail to charge
    }
    
    /**
     * Write a new file with random Z values in a matrix format readable with gnuplot.
     * It uses the Hill algorithm.
     * @param file Relative path to the new file.
     * @param cols Number of cols (X). Greater than 0.
     * @param rows Number of rows (Y). Greater than 0.
     * @param iter Maximun value of Z. Greater or equal to 0.
     * @return A bidimensional Node array with the DEM info.
     */
    public static Node[][] GenerateZfile(String file, int cols, int rows, int iter)
    {
        // Check cols, rows and iter greater than 0
        if(cols <= 0 || rows <= 0 || iter < 0)
        {
            System.out.println("Cols, rows and iterations must be greater than 0 [" + cols + "," + rows + "](" + iter + ")");
            return null;
        }
        Node temp[][] = new Node[cols][rows];
        String abspath = new File("").getAbsolutePath();
        
        try{
            FileWriter fw = new FileWriter(abspath + File.separator + file);
            PrintWriter zfile = new PrintWriter(fw);
 
            // Set initial z to 0
            for(int j = 0; j < rows; j++)
                for(int i = 0; i < cols; i++)
                    temp[i][j] = new Node(i, j, 0);
            
            // Iteration for terrain elevation
            int r, cx, cy;
            float max = -1;
            Random rand = new Random();
            for(int i = 0; i < iter; i++)
            {
                r = rand.nextInt(Math.min(cols, rows) / 5)+1;
                cx = rand.nextInt(cols);
                cy = rand.nextInt(rows);
                for(int x = 0; x < cols; x++)
                    for(int y = 0; y < rows; y++)
                    {
                        float nz = (float)(r*r-(Math.pow(x-cx,2)+Math.pow(y-cy,2)));
                        if(nz > 0)
                               temp[x][y].setZ(temp[x][y].getZ()+nz);
                        if(temp[x][y].getZ() > max)
                            max = temp[x][y].getZ();
                    }
            }
            // Normalize [0:1000]
            for(int x = 0; x < cols; x++)
                    for(int y = 0; y < rows; y++)
                        temp[x][y].setZ(temp[x][y].getZ()/max*1000);
            
            // Write file
            for(int j = 0; j < rows; j++)
            {
                for(int i = 0; i < cols; i++)
                    zfile.print(" " + temp[i][j].getZ());
                // New row
		zfile.println(" ");
            }
            // Close file and return array
            zfile.close();        
            return temp;
            
        }catch(IOException ioe){
            System.out.println("Input/output error: " + ioe.toString());
            return null;
        }
    }
    
    /**
     * Write a new file with random C values and maximum obstacles % in a matrix format readable with gnuplot.
     * It is not guaranteed to converge if maxo is greater than 40.
     * @param file Relative path to the new file.
     * @param cols Number of cols (X). Greater than 0.
     * @param rows Number of rows (Y). Greater than 0.
     * @param maxo Maximun number of obstacles (%). Greater or equal to 0.
     * @return A bidimensional float array with the DEM info.
     */
    public static float[][] GenerateCfile(String file, int cols, int rows, int maxo)
    {
        // Check cols, rows and maxZ greater than 0
        if(cols <= 0 || rows <= 0 || maxo < 0)
        {
            System.out.println("Cols, rows and oblstacles % must be greater than 0 [" + cols + "," + rows + "](" + maxo + ")");
            return null;
        }
        float temp[][] = new float[cols][rows];
        String abspath = new File("").getAbsolutePath();
        
        try{
            FileWriter fw = new FileWriter(abspath + File.separator + file);
            PrintWriter cfile = new PrintWriter(fw);
 
            // Set initial cost to 1
            for(int j = 0; j < rows; j++)
                for(int i = 0; i < cols; i++)
                    temp[i][j] = 1;
            
            int ox, oy;
            int DXO = cols / 25; // Obstacle dimensions
            int DYO = rows / 25;
            int nblockcells = (maxo * cols * rows) / 100;
            int nobs;
            if(DXO*DYO < 1)
                nobs = nblockcells;
            else
                nobs = nblockcells / (DXO * DYO);
            Random rand = new Random();
            while(nblockcells > 0)
            {
                ox = rand.nextInt(rows);
                oy = rand.nextInt(cols);
                // Put obstacles
                for(int x = ox; x < ox+DXO; x++)
                    for(int y = oy; y < oy+DYO; y++)
			// Protect first/last col/row
                        if(x >= 1 && x < cols-1 && y >= 1 && y < rows-1)
                            if(nblockcells > 0 && temp[x][y] == 1)
                            {
                                temp[x][y] = Map.MAX_COST+1;
                                nblockcells--;
                            }
                nobs--;
                // Put transversable area around obstacle
                for(int x = ox-1; x <= ox+DXO; x++)
                    for(int y = oy-1; y <= oy+DYO; y++)
                        if(x >= 0 && x < cols && y >= 0 && y < rows)
                        if(temp[x][y] == 1) temp[x][y] = 2;
            }
            
            for(int i = 0; i < cols*rows/10; i++)
            {
            nobs = rand.nextInt((int)Map.MAX_COST)+1;
            ox = rand.nextInt(rows);
            oy = rand.nextInt(cols);
            // Put transversal area cost
            for(int x = ox; x < ox+DXO*3; x++)
               for(int y = oy; y < oy+DYO*3; y++)
	       if(x < cols && y < rows && temp[x][y] < 3)
                   temp[x][y] = nobs;
            }
            
            // Write file
            for(int j = 0; j < rows; j++)
            {
                for(int i = 0; i < cols; i++)
                    cfile.print(" " + (int) temp[i][j]);
                // New row
		    cfile.println(" ");
            }          
            // Close file and return array
            cfile.close();        
            return temp;
            
        }catch(IOException ioe){
            System.out.println("Input/output error: " + ioe.toString());
            return null;
        }
    }
     
   /**
    * Write a path file in a gnuplot format. This function accepts two types of lists:
    * ArrayList<Node> or ArrayList<double[][3]>.
    * @param file Relative path to the new file.
    * @param path A list of nodes that represent a valid path.
    * @param headerdata Data to print at top of the file.
    */
   public static void WritePathFile(String file, ArrayList path, String headerdata)
   {
       String abspath = new File("").getAbsolutePath();
        
       try{
           FileWriter fw = new FileWriter(abspath + File.separator + file);
           PrintWriter pfile = new PrintWriter(fw);
           // Print number of steps and total cost
           pfile.println(headerdata);
           // Read the array and write into file
           for(int i = path.size()-1; i >= 0; i--)
	       if(path.get(i).getClass() == Node.class) // Check type of list
		   pfile.println(((Node)path.get(i)).getXYZ());
	       else
		   pfile.println(((double[])path.get(i))[0]+" "+((double[])path.get(i))[1]+" "+((double[])path.get(i))[2]);
           pfile.close();                   

       }catch(IOException ioe){
           System.out.println("Input/output error: " + ioe.toString());
       }
   }
   
}
