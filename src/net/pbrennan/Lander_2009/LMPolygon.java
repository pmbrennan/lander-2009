package net.pbrennan.Lander_2009;
import java.awt.Graphics;
import java.awt.Color;

public class LMPolygon
{
    private double allx[][] = {
        // Ascent Stage I
        { -0.333333333333, -0.420289855072, -0.420289855072, -0.340579710145, -0.289855072464, 0.340579710145, 0.347826086957, 0.427536231884, 0.565217391304, 0.565217391304, 0.45652173913, 0.297101449275, 0.297101449275, 0.260869565217, 0.260869565217, 0.166666666667, -0.333333333333 },
        // Ascent Stage II
        { -0.123188405797, -0.0652173913043, 0.0724637681159, 0.130434782609 },
        // Ascent Stage III
        { -0.0942028985507, 0.0942028985507 },
        // Ascent Stage IV
        { -0.0869565217391, -0.18115942029, -0.0724637681159 },
        // Ascent Stage V
        { 0.0942028985507, 0.18115942029, 0.0797101449275 },
        // Ascent Stage VI
        { -0.115942028986, -0.217391304348, -0.275362318841, -0.275362318841, -0.195652173913, 0.0, 0.195652173913, 0.275362318841, 0.275362318841, 0.217391304348, 0.123188405797 },
        // Left pad
        { -1.0, -0.811594202899 },
        // Right pad
        { 1.0, 0.811594202899 },
        // Left leg I
        { -0.898550724638, -0.652173913043, -0.463768115942 },
        // Left leg II
        { -0.81884057971, -0.463768115942, -0.644927536232 },
        // Right leg I
        { 0.898550724638, 0.652173913043, 0.463768115942 },
        // Right leg II
        { 0.81884057971, 0.463768115942, 0.644927536232 },
        // Frame I
        { -0.463768115942, 0.463768115942, 0.463768115942, -0.463768115942, -0.463768115942 },
        // Frame II
        { -0.384057971014, -0.384057971014, 0.384057971014, 0.384057971014 },
        // Frame III
        { -0.159420289855, -0.159420289855 },
        // Frame IV
        { 0.159420289855, 0.159420289855 },
        // Engine Bell
        { -0.115942028986, -0.144927536232, 0.144927536232, 0.115942028986 },
        // Exhaust Flame
        { -0.144927536232, 0.0, 0.144927536232 }
    };

    private double ally[][] = {
        // Ascent Stage I
        { 0.00724637681159, -0.130434782609, -0.275362318841, -0.369565217391, -0.630434782609, -0.63768115942, -0.31884057971, -0.31884057971, -0.239130434783, -0.0942028985507, -0.0144927536232, -0.0144927536232, 0.00724637681159, 0.00724637681159, -0.0144927536232, 0.00724637681159, 0.00724637681159 },
        // Ascent Stage II
        { 0.00724637681159, -0.528985507246, -0.528985507246, 0.00724637681159 },
        // Ascent Stage III
        { -0.297101449275, -0.297101449275 },
        // Ascent Stage IV
        { -0.355072463768, -0.434782608696, -0.471014492754 },
        // Ascent Stage V
        { -0.355072463768, -0.434782608696, -0.471014492754 },
        // Ascent Stage VI
        { -0.0507246376812, -0.13768115942, -0.239130434783, -0.376811594203, -0.492753623188, -0.572463768116, -0.492753623188, -0.376811594203, -0.239130434783, -0.13768115942, -0.0507246376812 },
        // Left pad
        { 0.63768115942, 0.63768115942 },
        // Right pad
        { 0.63768115942, 0.63768115942 },
        // Left leg I
        { 0.63768115942, 0.101449275362, 0.0289855072464 },
        // Left leg II
        { 0.45652173913, 0.384057971014, 0.108695652174 },
        // Right leg I
        { 0.63768115942, 0.101449275362, 0.0289855072464 },
        // Right leg II
        { 0.45652173913, 0.384057971014, 0.108695652174 },
        // Frame I
        { 0.00724637681159, 0.00724637681159, 0.391304347826, 0.391304347826, 0.00724637681159 },
        // Frame II
        { 0.391304347826, 0.442028985507, 0.442028985507, 0.391304347826 },
        // Frame III
        { 0.00724637681159, 0.391304347826 },
        // Frame IV
        { 0.00724637681159, 0.391304347826 },
        // Engine Bell
        { 0.442028985507, 0.586956521739, 0.586956521739, 0.442028985507 },
        // Exhaust Flame
        { 0.586956521739, 0.586956521739, 0.586956521739 }
    };
    private int workx[] = new int[1000];
    private int worky[] = new int[1000];

    private static final int        FLAME_LINE_NUM          = 17;
    private static final int        FLAME_APEX_POINT_NUM    = 1;
    private static final double     FLAME_MAX_LENGTH        = 0.8;
    private static final double     FLAME_VARIATION         = 0.2;

    public void draw(Graphics g,
                     int centerX, int centerY,
                     double angleDegrees, 
                     double scale, Color color, double throttle)
    {
        double nangle = -angleDegrees * 3.14159265359 / 180.0 ;
        double S = Math.sin(nangle);
        double C = Math.cos(nangle);
        int nlines = allx.length;

        if (throttle < 0.0)
            throttle = 0.0;
        else if (throttle > 1.0)
            throttle = 1.0;

        if (scale < 2.0)
            scale = 2.0;

        if (scale <= 2.0)
        {
            g.setColor(color);
            g.fillRect((int)(centerX-scale), (int)(centerY-scale), (int)(scale*2), (int)(scale*2));
        }
        else {
            for (int lineindex = 0 ; lineindex < nlines ; ++lineindex)
            {
                int npoints = allx[lineindex].length;
                for (int pointindex = 0 ; pointindex < npoints ; ++pointindex)
                {
                    double x = allx[lineindex][pointindex];
                    double y = ally[lineindex][pointindex];

                    if ((lineindex == FLAME_LINE_NUM)&&(pointindex == FLAME_APEX_POINT_NUM))
                    {
                        y += throttle * (FLAME_MAX_LENGTH + FLAME_VARIATION * Math.random());
                    }

                    workx[pointindex] = (int)(Math.round(scale * (x * C - y * S))) + centerX;
                    worky[pointindex] = (int)(Math.round(scale * (x * S + y * C))) + centerY;
                }
                g.setColor(color);
                g.drawPolyline(workx, worky, npoints);
            }
        }
    }

}