package GUI;

import PathPlanning.Map;
import PathPlanning.Node;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;


/**
 *
 * @author Pablo Muñoz
 */
public class Lienzo extends Canvas
{
    public Lienzo(Map dem, ArrayList route, int zoom)
    {
        if(zoom > 0)
            scale += zoom;
        cols = dem.get_ncols();
        rows = dem.get_nrows();
        cornernode(dem.get_isDmode());
        map = dem;
        path = route;
        showalt = false;
        showcost = false;
        imagevisualization = true;
    }
    
    @Override
    public void paint(Graphics g)
    {
        this.setSize(fonts*2+(cols+1)*scale, fonts*2+rows*scale);
        super.paint(g);
        this.setBackground(Color.WHITE);
        if(imagevisualization)
            paint_img(g);
        else
            paint_grid(g);
   }
    
   public void paint_img(Graphics g)
   {
       // Show image map
       for(int i=0; i<cols; i++)
            for(int j=0; j<rows; j++)
            {
                g.setColor(Color.getHSBColor((float)0.4, (float)1.0, (float)1.0-(map.get_node(i, j).getZ()-map.min_alt())/(map.max_alt()-map.min_alt())));
                g.fillRect(i*scale, j*scale, scale, scale);
            }

       cornernode(!map.get_isDmode()); // Invert due to modify of obstacles
       g.setColor(Color.GRAY);
       for(int i=0; i<cols; i++)
            for(int j=0; j<rows; j++)
             if(map.get_tcost(i, j) > Map.MAX_COST)
                 g.fillRect(i*scale+desp, j*scale+desp, scale, scale);
       
       // Read and show path
       int sch = scale/2;
       g.setColor(Color.BLUE);
       if(path.get(0).getClass() == Node.class) // Check type of list
           g.fillOval(((Node)path.get(0)).getX()*scale+sch/2,((Node)path.get(0)).getY()*scale+sch/2, sch, sch);
       else
           g.fillOval((int)((double[])path.get(0))[0]*scale+sch/2,(int)((double[])path.get(0))[1]*scale+sch/2, sch, sch);
       g.setColor(Color.RED);
       for(int i = 0; i < path.size()-1; i++)
           if(path.get(i).getClass() == Node.class) // Check type of list
           {
               g.drawLine(((Node)path.get(i)).getX()*scale+sch,((Node)path.get(i)).getY()*scale+sch,((Node)path.get(i+1)).getX()*scale+sch,((Node)path.get(i+1)).getY()*scale+sch);
               g.fillOval(((Node)path.get(i+1)).getX()*scale+sch*3/4,((Node)path.get(i+1)).getY()*scale+sch*3/4, sch/2, sch/2);
           }
           else
           {
               g.drawLine((int)((double[])path.get(i))[0]*scale+sch,(int)((double[])path.get(i))[1]*scale+sch,(int)((double[])path.get(i+1))[0]*scale+sch,(int)((double[])path.get(i+1))[1]*scale+sch);
               g.fillOval((int)((double[])path.get(i+1))[0]*scale+sch*3/4,(int)((double[])path.get(i+1))[1]*scale+sch*3/4, sch/2, sch/2);
           }
       // Show altitude scale
       g.setColor(Color.getHSBColor((float)0.4, (float)1.0, (float)1.0-(map.max_alt()-map.min_alt())/(map.max_alt()-map.min_alt())));
       g.fillRect(cols*scale+2, fonts, scale/4, scale/4);
       g.drawString(Float.toString(map.max_alt()),cols*scale+scale/4+1, fonts+scale/4);
       g.setColor(Color.getHSBColor((float)0.4, (float)1.0, (float)1.0-(map.min_alt()-map.min_alt())/(map.max_alt()-map.min_alt())));
       g.fillRect(cols*scale+2, scale+fonts, scale/4, scale/4);
       g.drawString(Float.toString(map.min_alt()),cols*scale+scale/4+1, fonts*2+scale);
   }
    
   public void paint_grid(Graphics g)
   {
       cornernode(map.get_isDmode());
       g.setColor(Color.BLACK);
       g.setFont(new Font("Serif", Font.CENTER_BASELINE, fonts-3));
       int k = 0;
       if(map.get_isDmode())
       {
           rows--; // Avoid +1 col and row paint
           cols--;
       }
       for(int i=fonts; i<=cols*scale+fonts; i+=scale)
       {
           if(k%5 == 0)
               g.drawString(String.valueOf(k), i-4+desp, fonts-5);
           k++;
           g.drawLine(i,fonts,i,rows*scale+fonts);
       }
       k = 0;
       for(int j=fonts; j<=rows*scale+fonts; j+=scale)
       {
           if(k%5 == 0)
               g.drawString(String.valueOf(k), 1, fonts/2+j-2+desp);
           k++;
           g.drawLine(fonts,j,cols*scale+fonts,j);
       }
       if(map.get_isDmode())
       {
           rows++; // Undo
           cols++;
       }
       
       // Read map and show obstacles, costs and altitude
       for(int i=0; i<cols; i++)
           for(int j=0; j<rows; j++)
           {
               g.setColor(Color.GRAY);
               if(map.get_tcost(i, j) > Map.MAX_COST)
                   g.fillRect(i*scale+fonts, j*scale+fonts, scale, scale);
               else
                   if(showcost && map.valid_cost_pos(i, j))
                       g.drawString(Float.toString(map.get_tcost(i, j)), i*scale+fonts+scale/4, j*scale+fonts*2+scale/4);
               if(showalt)
               {
                   g.setColor(Color.getHSBColor((float)0.4, (float)1.0, (float)1.0-(map.get_node(i, j).getZ()-map.min_alt())/(map.max_alt()-map.min_alt())));
                   g.fillRect(i*scale+fonts-scale/8+desp, j*scale+fonts-scale/8+desp/2, scale/4, scale/4);
                   g.setColor(Color.WHITE);
                   g.drawString(Float.toString(map.get_node(i,j).getZ()),i*scale+fonts-scale/8+1+desp, j*scale+fonts+3+desp/2);
               }
           }
       if(showalt)
       {
           g.setColor(Color.getHSBColor((float)0.4, (float)1.0, (float)1.0-(map.max_alt()-map.min_alt())/(map.max_alt()-map.min_alt())));
           g.fillRect(cols*scale+2, fonts, scale/4, scale/4);
           g.drawString(Float.toString(map.max_alt()),cols*scale+scale/4+1, fonts+scale/4);
           
           g.setColor(Color.getHSBColor((float)0.4, (float)1.0, (float)1.0-((map.max_alt()-map.min_alt())/2+map.min_alt())/(map.max_alt()-map.min_alt())));
           g.fillRect(cols*scale+2, scale/2+fonts, scale/4, scale/4);
           g.drawString(Float.toString((map.max_alt()-map.min_alt())/2+map.min_alt()),cols*scale+scale/4+1, fonts*2+scale/2);
           
           g.setColor(Color.getHSBColor((float)0.4, (float)1.0, (float)1.0-(map.min_alt()-map.min_alt())/(map.max_alt()-map.min_alt())));
           g.fillRect(cols*scale+2, scale+fonts, scale/4, scale/4);
           g.drawString(Float.toString(map.min_alt()),cols*scale+scale/4+1, fonts*2+scale);
       }
       
       // Read and show path
       java.awt.Graphics2D g2d = (java.awt.Graphics2D)this.getGraphics();
       g2d.setStroke(new java.awt.BasicStroke(2.0f));
       g2d.setColor(Color.RED);
       for(int i = 0; i < path.size()-1; i++)
           if(path.get(i).getClass() == Node.class) // Check type of list
           {
               if(map.get_isDmode())
                   g2d.drawLine(((Node)path.get(i)).getX()*scale+fonts,((Node)path.get(i)).getY()*scale+fonts,((Node)path.get(i+1)).getX()*scale+fonts,((Node)path.get(i+1)).getY()*scale+fonts);
               else
                   g2d.drawLine(((Node)path.get(i)).getX()*scale+scale/2+fonts,((Node)path.get(i)).getY()*scale+scale/2+fonts,((Node)path.get(i+1)).getX()*scale+scale/2+fonts,((Node)path.get(i+1)).getY()*scale+scale/2+fonts);
           }
           else
           {
               if(map.get_isDmode())
                   g2d.drawLine((int)((double[])path.get(i))[0]*scale+fonts,(int)((double[])path.get(i))[1]*scale+fonts,(int)((double[])path.get(i+1))[0]*scale+fonts,(int)((double[])path.get(i+1))[1]*scale+fonts);
               else
                   g2d.drawLine((int)((double[])path.get(i))[0]*scale+scale/2+fonts,(int)((double[])path.get(i))[1]*scale+scale/2+fonts,(int)((double[])path.get(i+1))[0]*scale+scale/2+fonts,(int)((double[])path.get(i+1))[1]*scale+scale/2+fonts);    
           }
   } 
   
    @Override
   public void update(Graphics g)
   {
        paint(g);
   }
   
   public boolean newValues(int c, int r, int s)
   {
       if(c<1 || r<1 || s<3)
           return false;
       cols = c;
       rows = r;
       scale = s;
       return true;
   }
   
   public void changeScale(int s)
   {
       if(scale > 0)
           scale = s;
   }
   
   public final void cornernode(boolean corner)
   {
       if(!corner)
           desp = scale / 2;
       else
           desp = 0;
   }
   
   public void changeVisualization(boolean imagevis)
   {
       imagevisualization = imagevis;
   }
   
   public void showAltitude(boolean altyes)
   {
       showalt = altyes;
   }
   
   public void showCost(boolean costyes)
   {
       showcost = costyes;
   }
   
   private int cols = 10;
   private int rows = 10;
   private int scale = 3;
   private int fonts = 15;
   private int desp = 0;
   private boolean showalt;
   private boolean showcost;
   private boolean imagevisualization;
   private Map map;
   private ArrayList path;
}