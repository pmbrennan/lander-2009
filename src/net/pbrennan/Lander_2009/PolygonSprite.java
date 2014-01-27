package net.pbrennan.Lander_2009;
import java.awt.Graphics;
import java.awt.Color;

public class PolygonSprite
{
    // Actual instances or subclasses of PolygonSprite will fill these.
    private double allx[][];
    private double ally[][];
    private int workx[];
    private int worky[];

    public PolygonSprite()
    {
    }

    public PolygonSprite(double x[], double y[])
    {
        addxy(x,y);
    }

    public void addxy(double x[], double y[])
    {
        allx = new double[1][];
        allx[0] = x;
        ally = new double[1][];
        ally[0] = y;
    }

    /***
     * Find the length of the longest array of coordinates in the sprite
     * @return
     * the length of the longest array of coordinates in the sprite.
     */
    private int findMaxLen()
    {
        int maxlen = 0;

        if (allx == null)
            return 0;

        for (int i=0; i<allx.length ; ++i)
        {
            int len = allx[i].length;
            if (len > maxlen)
                maxlen = len;
        }
        return maxlen;
    }

    public void draw(Graphics g,
                     int centerX, int centerY,
                     double angleRadians,
                     double scale,
                     Color color,
                     boolean filled)
    {
        double nangle = -angleRadians ;
        double S = Math.sin(nangle);
        double C = Math.cos(nangle);
        int nlines = allx.length;

        if (workx == null)
        {
            int len = findMaxLen();
            if (len == 0)
                return;
            else
            {
                workx = new int[len];
                worky = new int[len];
            }
        }

        if (scale < 1.0)
            scale = 1.0;

        for (int lineindex = 0 ; lineindex < nlines ; ++lineindex)
        {
            int npoints = allx[lineindex].length;
            for (int pointindex = 0 ; pointindex < npoints ; ++pointindex)
            {
                double x = allx[lineindex][pointindex];
                double y = ally[lineindex][pointindex];

                workx[pointindex] = (int)(Math.round(scale * (x * C - y * S))) + centerX;
                worky[pointindex] = (int)(Math.round(scale * (x * S + y * C))) + centerY;
            }
            g.setColor(color);

            if (filled)
                g.fillPolygon(workx, worky, npoints);
            else
                g.drawPolyline(workx, worky, npoints);
        }
    }

}