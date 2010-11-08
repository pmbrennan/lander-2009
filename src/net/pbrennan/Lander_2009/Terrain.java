package net.pbrennan.Lander_2009;
import java.io.*;
import java.util.ArrayList;
import java.lang.StringBuilder;
import java.awt.Graphics2D;
import java.awt.Color;

public class Terrain
{
    private static final String DefaultTerrainFileName = "data/terrain001.csv";

    public Terrain()
    {
        System.out.println("in Terrain constructor....");
        System.out.println("CWD = " + System.getProperty("user.dir"));
        Init(DefaultTerrainFileName);
    }
    
    public Terrain(String inTerrainFileName)
    {
        System.out.println("in Terrain constructor....");
        System.out.println("CWD = " + System.getProperty("user.dir"));
        Init(inTerrainFileName);
    }
    
    private void Init(String inTerrainFileName)
    { 
        int loadFlag = loadTerrainFile(inTerrainFileName);
        if (loadFlag == 0)
            System.out.println("successful load.");
        else
            System.out.println("failed to load.");
    }

    public int loadTerrainFile(String fileName)
    {
        int errFlag = 0;

        System.out.println("Loading file: " + fileName);

        try
        {
            File inputFile = new File(fileName);
            if (inputFile == null)
                return -1;
            FileReader in = new FileReader(inputFile);

            errFlag = readAllLines(in);
            if (errFlag != 0)
                return errFlag;

            errFlag = castToArrays();
            if (errFlag != 0)
                return errFlag;

            return (0);
        }
        catch (Exception e)
        {
            // TODO : handle exceptions better
            System.out.println("got exception: " + e);
            return -1;
        }
    }

    private int readAllLines(FileReader in)
    {
        int c;
        sb.setLength(0);
        try
        {
            while ((c = in.read()) != -1)
            {
                char ch = (char)c;
                if (ch != '\n')
                {
                    sb.append(ch);
                }
                else
                {
                    if (parseLine() != 0)
                        return -1;
                    sb.setLength(0);
                }
            }
            if (parseLine() != 0)
                return -1;

            in.close();
        }
        catch (Exception e)
        {
            System.out.println("got exception: " + e + "on line number " + lineNumber);
            return -1;
        }

        return 0;
    }

    private int parseLine()
    {
        lineNumber ++;
        String s = sb.toString().trim();

        if (s.length() == 0)
            return 0;

        String delims = "[,]";
        String[] tokens = s.split(delims);
        String sHeader = "theta, r, d, alt, x, y";

        //System.out.println("'" + s + "'");
        //System.out.println("'" + sHeader + "'");
        //System.out.println(s.compareTo(sHeader));

        if (s.compareTo(sHeader) == 0)
        {
            if (!gotFirstLine)
            {
                gotFirstLine = true;
                //System.out.println("got first line");
                return 0;
            }
            else
            {
                //System.out.println("duplicate first line?");
                return -1;
            }
        }

        //System.out.println(tokens[0]);
        double theta =  Double.valueOf(tokens[0].trim()).doubleValue();
        thetaList.add(theta);

        double r =      Double.valueOf(tokens[1].trim()).doubleValue();
        rList.add(r);

        double d =      Double.valueOf(tokens[2].trim()).doubleValue();
        dList.add(d);

        double alt =    Double.valueOf(tokens[3].trim()).doubleValue();
        altList.add(alt);

        double x =      Double.valueOf(tokens[4].trim()).doubleValue();
        xList.add(x);

        double y =      Double.valueOf(tokens[5].trim()).doubleValue();
        yList.add(y);

        return 0;
    }

    private int castToArrays()
    {
        int size = thetaList.size();

        thetaArray = new double[size];
        rArray = new double[size];
        dArray = new double[size];
        altArray = new double[size];
        xArray = new double[size];
        yArray = new double[size];
        nxArray = new double[size];
        nyArray = new double[size];
        normAngleArray = new double[size];

        int i;
        
        for (i=0; i<size ; i++)
        {
            thetaArray[i] = thetaList.get(i);
            rArray[i] = rList.get(i);
            dArray[i] = dList.get(i);
            altArray[i] = altList.get(i);
            xArray[i] = xList.get(i);
            yArray[i] = yList.get(i);
        }
         
        for (i=0; i<size ; i++)
        {
            // Compute the normal.
            double dx;
            double dy;
            double a;
            if (i<(size-1))
            {
                dx = xArray[i+1] - xArray[i];
                dy = yArray[i+1] - yArray[i];
            }
            else
            {
                dx = xArray[0] - xArray[i];
                dy = yArray[0] - yArray[i];
            }
            
            
            a = Math.atan2(dy, dx) - LanderUtils.HALF_PI; 
            if (a < 0.0)
            {
                a += LanderUtils.PI2;
            }
            
            
            //a = thetaArray[i];
            
            nxArray[i] = Math.cos(a);
            nyArray[i] = Math.sin(a);
            normAngleArray[i] = a;
        }

        return 0;
    }
    
    /**
     * Given the position of the LM in the
     * world frame, return the normal angle for the correct terrain 
     * line segment.
     */
    // TODO: this function is hanging sometimes. How?
    public double getTerrainNormalAngle(double lmXWorld, double lmYWorld)
    {
        double thetaRadians = Math.atan2(lmYWorld, lmXWorld);
        if (thetaRadians < 0.0)
        {
            thetaRadians += LanderUtils.PI2;
        }
        int index;
        int nIndices = thetaArray.length;
        for (index = 0; index < nIndices; ++index)
        {
            if (thetaArray[index] > thetaRadians)
            {
                --index;
                break;
            }
        }
        
        // TODO: This logic SUCKS! Improve it.
        if (index < nIndices)
        {
            --index;
        }
        
        if (index < 0)
        {
            index += nIndices;
        }
        
        if (index >= nIndices) 
        {
            index -= nIndices;
        }
        
        //System.out.println("Terrain.getTerrainNormalAngle: index = " + index);
        return normAngleArray[index];
    }

    // --------------------------------------------------------------------
    // Given an angle in radians, and the position of the LM in the
    // world frame, return the terrain altitude of the LM.
    public double getTerrainAltitude(double thetaRadians,
                                     double lmXWorld, double lmYWorld)
    {
        // TODO: Cache this information, next frame it will be easier to keep this up
        // to date.
        int m_interpolation_index_right;
        int m_interpolation_index_left;
        for (m_interpolation_index_right = 0; m_interpolation_index_right < thetaArray.length; ++m_interpolation_index_right)
        {
            if (thetaArray[m_interpolation_index_right] > thetaRadians)
                break;
        }

        m_interpolation_index_left = m_interpolation_index_right - 1;
        if (m_interpolation_index_right == thetaArray.length)
        {
            m_interpolation_index_right = 0;
        }

        //-------------------------------------------------------------
        // There are several ways to perform this interpolation.
        //
        // 1. Interpolate the altitude array, using theta as an
        //    interpolation value.
        //
        // 2. Interpolate x and y arrays, using theta as an
        //    interpolation value, and then compute distance using
        //    pythagoras.
        //
        // 3. Determine the point of interest using the law of sines
        //    and interpolate based on that.
        //
        // 4. Transform coordinate endpoints into our local coordinate
        //    system and perform a linear interpolation that way
        //
        // Method 4 is the most accurate way and jibes most closely
        // with how the terrain is presented in the game.

        double S = Math.sin(thetaRadians);
        double C = Math.cos(thetaRadians);
        double u[] = { S, -C };
        double v[] = { C, S };
        double x1world = lmXWorld - xArray[m_interpolation_index_left];
        double x2world = lmXWorld - xArray[m_interpolation_index_right];
        double y1world = lmYWorld - yArray[m_interpolation_index_left];
        double y2world = lmYWorld - yArray[m_interpolation_index_right];

        // Now transform into the local coordinate system
        double x1LM = x1world * u[0] + y1world * u[1];
        double y1LM = x1world * v[0] + y1world * v[1];
        double x2LM = x2world * u[0] + y2world * u[1];
        double y2LM = x2world * v[0] + y2world * v[1];

        // and perform interpolation.
        double y = y1LM + (0 - x1LM) * (y2LM - y1LM) / (x2LM - x1LM);

        double method4Alt = y;


        return method4Alt;

    }

    // Draw all terrain line segments which fall between :
    // centerRadians + marginRadians and
    // centerRadians - marginRadians
    public void drawWithWindow(Graphics2D g,
			       // Center point of the moon, 
			       // in screen coordinates (pixels)
                   int centerX, int centerY,  
                   double scale_ppm, // scale, pix/meter
			       // rotation of view wrt world in radians
                   double rotation, 
			       // center point of the view in radians
                   double centerRadians,
                   double marginRadians,
                   int WIDTH,
                   int HEIGHT,
                   Color fillColor)
    {
        if (workx == null)
        {
            workx = new int[xArray.length + 2];
            worky = new int[xArray.length + 2];
        }

        // Find the window
        // TODO: Cache this information, next frame it
        // will be easier to keep this up to date.
        int m_center_index;
        for (m_center_index = 0; 
	         m_center_index < thetaArray.length; 
	         ++m_center_index)
        {
            if (thetaArray[m_center_index] > centerRadians)
                break;
        }

        //System.out.println("m_center_index = " + m_center_index);

        // TODO: I suspect I have a hang in one of these two loops.
        int m_left_index = m_center_index - 1;
        boolean m_left_wrap = false;
        while (true)
        {
            if (m_left_index == -1)
            {
                m_left_index = thetaArray.length - 1;
                m_left_wrap = true;
            }

            if (m_left_wrap && (m_left_index == m_center_index))
            {
                break;
            }

            if (LanderUtils.absAngleDifference(centerRadians, 
					       thetaArray[m_left_index])
                > marginRadians)
                break;

            --m_left_index;
        }

        int m_right_index = m_center_index;
        boolean m_right_wrap = false;
        while (true)
        {
            if (m_right_index == thetaArray.length)
            {
                m_right_index = 0;
                m_right_wrap = true;
            }

            if (m_right_wrap && (m_right_index == m_center_index))
            {
                break;
            }

            if (LanderUtils.absAngleDifference(centerRadians, 
					       thetaArray[m_right_index])
                > marginRadians)
                break;

            ++m_right_index;
        }

        // Now stuff the work array.
        int src_index = m_left_index;   // index to take x and y from the source arrays.
        int dest_index = 0;             // index to put x and y into the dest arrays.
        int point_count = 0;            // how many points have we got?
        double S = Math.sin(rotation);
        double C = Math.cos(rotation);
        while (true)
        {
            double x = xArray[src_index];
            double y = yArray[src_index];

            workx[dest_index] = centerX + 
                (int)(Math.round(scale_ppm * (x * C - y * S)));
            worky[dest_index] = centerY - 
                (int)(Math.round(scale_ppm * (x * S + y * C)));

            ++point_count;
            ++dest_index;
            if (src_index == m_right_index)
                break;

            ++src_index;
            if (src_index == xArray.length)
                src_index = 0;
        }

        workx[dest_index] = -10;
        worky[dest_index] = HEIGHT + 10;
        ++dest_index;
        workx[dest_index] = WIDTH + 10;
        worky[dest_index] = HEIGHT + 10;
        point_count += 2;

        g.setColor(fillColor);

        // Draw or fill the polygon.
        //g.drawPolygon(workx, worky, point_count);
        g.fillPolygon(workx, worky, point_count);
    }

    private StringBuilder sb = new StringBuilder();
    private boolean gotFirstLine = false;
    private int lineNumber = 0;

    // The raw data from the terrain file.
    private ArrayList<Double> thetaList    = new ArrayList<Double>();
    private ArrayList<Double> rList        = new ArrayList<Double>();
    private ArrayList<Double> dList        = new ArrayList<Double>();
    private ArrayList<Double> altList      = new ArrayList<Double>();
    private ArrayList<Double> xList        = new ArrayList<Double>();
    private ArrayList<Double> yList        = new ArrayList<Double>();

    // Once those are read in, they are cast into simple arrays 
    // for fast access.
    private double[] thetaArray;    // the theta value of each point.
    private double[] rArray;        // the r value of each point.
    private double[] dArray;        // the d (distance along datum circle)
    private double[] altArray;      // the altitude value of each point.
    private double[] xArray;        // the x value of each point.
    private double[] yArray;        // the y value of each point.
    
    // These arrays are derived from the originals.
    private double[] nxArray;       // the normal vector of each line segment.
    private double[] nyArray;       // the normal vector of each line segment.
    private double[] normAngleArray; // the angle of the normal vector.
    
    // Work arrays, for drawing.
    private int workx[];
    private int worky[];

}