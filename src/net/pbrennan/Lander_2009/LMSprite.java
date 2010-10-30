package net.pbrennan.Lander_2009;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;

public class LMSprite {

    private Image m_LMImage;
    private int width;
    private int height;
    private int half_width;
    private int half_height;
    
    private int paintCounter = 0; // number of times the LM has been painted.
    private int flashPeriod = 25; // number of times to paint the LM till we flash the running light.
    
    public LMSprite()
    {
        m_LMImage = Toolkit.getDefaultToolkit().createImage("images/LMSprite.png");
        while (m_LMImage.getHeight(null) == -1) {};
        width = m_LMImage.getWidth(null);
        half_width = width / 2;
        height = m_LMImage.getHeight(null);
        half_height = height / 2;        
    }
    
    public void draw(Graphics g,
            int centerX, int centerY,
            double angleDegrees, 
            double scale, Color color, double throttle)
    {
        double nangle = -angleDegrees * 3.14159265359 / 180.0 ;
        double scalefactor = scale * 0.005; // TODO: do better.  Magic numbers!
        
        Boolean paintLight = false;
        
        if (scale < 2.0)
            scale = 2.0;

        if (scale <= 2.0)
        {
            g.setColor(color);
            g.fillRect((int)(centerX-scale), (int)(centerY-scale), (int)(scale*2), (int)(scale*2));
            paintLight = false;
        }
        else {
            Graphics2D g2d = (Graphics2D)g;
            
            AffineTransform t = AffineTransform.getTranslateInstance(centerX, centerY);
            t.concatenate(AffineTransform.getRotateInstance(nangle));
            t.concatenate(AffineTransform.getScaleInstance(scalefactor, scalefactor));
            t.concatenate(AffineTransform.getTranslateInstance(-half_width, -half_height));
            g2d.drawImage(m_LMImage, t, null);
            
            // Draw a reticle around the spacecraft.
            int left = (int)(centerX-scale);
            int right = (int)(centerX+scale);
            int top = (int)(centerY-scale);
            int btm = (int)(centerY+scale);
            int incr = Math.min(10, right-left);
            g.setColor(color);
            g.drawLine(left, top, left+incr, top);
            g.drawLine(left, top, left, top+incr);
            g.drawLine(left, btm, left+incr, btm);
            g.drawLine(left, btm, left, btm-incr);
            g.drawLine(right, top, right-incr, top);
            g.drawLine(right, top, right, top+incr);
            g.drawLine(right, btm, right-incr, btm);
            g.drawLine(right, btm, right, btm-incr);
            
            paintLight = true;            
        }
        
        // Add the LM Tracking light.
        if ((++paintCounter >= flashPeriod) && paintLight)
        {
            paintCounter = 0;
            double L = 0.425 * scale;
            double x = centerX + L * Math.sin(nangle);
            double y = centerY - L * Math.cos(nangle);
            double R = 0.025 * scale;
            g.setColor(Color.WHITE);
            g.fillOval((int)(x-R),(int)(y-R),(int)(R*2),(int)(R*2));
        }
    }
}
