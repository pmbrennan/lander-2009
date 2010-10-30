package net.pbrennan.Lander_2009;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.lang.StringBuilder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

import javax.swing.JPanel;

import net.pbrennan.Lander_2009.LunarSpacecraft2D.OrbitType;

import java.awt.geom.AffineTransform;

public class SideView4 extends JPanel implements ILMInstrumentDataListener, ILMEventListener
{
    /**
	 *
	 */
	private static final long serialVersionUID = 6118089846830900268L;
	private static final boolean DISPLAY_PERFORMANCE_DATA = false;
    private static final boolean PAINT_TERRAIN = true;
    private static final boolean USE_DIGITAL_FONT = true;
    private static final boolean ADVANCED_DISPLAY = true;

    ///////////////////////////////////////////////////////////////////
    //
    // Dimensions of the display window.
    //
    public static final int MIN_WIDTH = 700;
    public static final int MAX_WIDTH = 1400;
    public static final int MIN_HEIGHT = 700;
    public static final int MAX_HEIGHT = 1000;
    
    
    
    private static final int DEFAULT_WIDTH = 1400;
    private static final int DEFAULT_HEIGHT = 1000;
	private int mWidth;
	private int mHalfWidth;
	private int mHeight;
	private int mHalfHeight;
	
	private int mEventDisplayWidth;  
	private int mEventDisplayHeight; 
	private int mEventDisplayX; 
	private int mEventDisplayY; 

	private void setDimensions(int width, int height)
	{
	    System.out.println("setDimensions(" + width + "," + height + ")");
	    
	    Toolkit tk = java.awt.Toolkit.getDefaultToolkit();
	    Dimension dim = tk.getScreenSize();
	    
	    if (width == -1)
	        width = dim.width - 50;
	    if (height == -1)
	        height = dim.height - 50;
	    
	    if (width < MIN_WIDTH)
	        width = MIN_WIDTH;
	    if (width > MAX_WIDTH)
	        width = MAX_WIDTH;
	    if (height < MIN_HEIGHT)
	        height = MIN_HEIGHT;
	    if (height > MAX_HEIGHT)
	        height = MAX_HEIGHT;
	    
		mWidth = width;
		mHeight = height;
		mHalfWidth = width / 2;
		mHalfHeight = height / 2;
		
		mEventDisplayWidth = 375; // Good for width = 1400
		mEventDisplayHeight = mHeight - 520; // 400 is Good for height = 1000
		mEventDisplayX = mWidth - mEventDisplayWidth - 20;
		mEventDisplayY = mHeight - mEventDisplayHeight - 20;
		
		System.out.println("event display width = " + mEventDisplayWidth);
		System.out.println("event display height = " + mEventDisplayHeight);
		System.out.println("event display x = " + mEventDisplayX);
        System.out.println("event display y = " + mEventDisplayY);
	}
    
    private static final Color GREEN = new Color(0x00ff00);
    private static final Color YELLOW = new Color(0xffff00);
    private static final Color GOLD = new Color(0xFFD700);
    private static final Color RED = new Color(0xff0000);
    //private static final Color DARKGREY = new Color(0x4c4c4c);
    private static final Color VERYDARKGREY = new Color(0x383838);
    private static final Color AVERAGEGREY = new Color(61,61,61);
    private static final Color DARKBLUE = new Color(0x000080);
    private static final Color HELPEDGE = new Color(0xCD7F32);

    private EventDisplay m_EventDisplay;

    SideView4()
    {
        Init(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
    
    SideView4(int width, int height)
    {
        Init (width, height);
    }
    
    void Init(int width, int height)
    {   
        setDimensions(width, height);        
        
        setBackground(Color.black);
        setPreferredSize(new Dimension(mWidth, mHeight));
        setSize(mWidth, mHeight);

        m_renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                                              RenderingHints.VALUE_ANTIALIAS_ON);

        if (USE_DIGITAL_FONT)
        {
            //m_font = new Font("Digital Dream Skew Narrow", Font.PLAIN, 14);
            m_font = new Font("Digital Dream Narrow", Font.PLAIN, 14);
            m_helpFont = new Font("Digital Dream Fat", Font.BOLD, 16);
            m_OPIFont = new Font("Arial", Font.PLAIN, 12);
            m_EventFont = new Font("Digital Dream Narrow", Font.PLAIN, 12);
        }
        else
        {
            m_font = new Font("Monospaced", Font.PLAIN, 14);
            m_helpFont = new Font("Monospaced", Font.BOLD, 16);
            m_OPIFont = new Font("Arial", Font.PLAIN, 12);
            m_EventFont = new Font("Monospaced", Font.PLAIN, 12);
        }

        m_EventDisplay = new EventDisplay(mEventDisplayWidth, mEventDisplayHeight, m_EventFont);
        m_EventDisplay.put("Welcome to Lunar Lander!\n");

        m_symbols = new DecimalFormatSymbols();
        m_symbols.setDecimalSeparator('.');
        m_symbols.setGroupingSeparator(' ');
        m_symbols.setMinusSign('-');
        m_symbols.setPercent('%');

        m_format2 = new DecimalFormat("00", m_symbols);

        m_format4dot1 = new DecimalFormat(
            "+0,000.0;-0,000.0",
            m_symbols);

        m_format3percent = new DecimalFormat(
            "000'%'", m_symbols);

        m_format3dot1percent = new DecimalFormat(
            "000.0'%'", m_symbols);

        m_format2dot2 = new DecimalFormat(
            "00.00", m_symbols);

        m_format3dot2 = new DecimalFormat(
            "000.00", m_symbols);

        m_format1_3dot2 = new DecimalFormat(
            "+000,000,000.00;-000,000,000.00",
            m_symbols);

        m_format3_3dot2 = new DecimalFormat(
            "+000,000,000.00;-000,000,000.00",
            m_symbols);

        m_sb = new StringBuilder(1000);
        m_data = new LMInstrumentData();

        m_targetIndicator = new PolygonSprite();
        double xs[] = {0.0,18.0,18.0};
        double ys[] = {0.0,8.0,-8.0};
        m_targetIndicator.addxy(xs, ys);

        m_helpBg = Toolkit.getDefaultToolkit().createImage("images/LanderHelpBg.png");
        m_moonImg = Toolkit.getDefaultToolkit().createImage("images/MoonNPoleProjection2.png");

        // Wait while the files load.
        while ((m_moonImg.getHeight(null) == -1)||(m_helpBg.getHeight(null) == -1)) {};
    }
    
    public void resetParticles()
    {
        m_particles.die();
    }

    public void listen(LMInstrumentData newdata)
    {
        m_data.copy(newdata);
    }

    public void listen(LMEvent event)
    {
        // TODO: When receiving a landing, handle crashes appropriately.
    }

    public void SetData(LMInstrumentData newdata)
    {
        m_data.copy(newdata);
    }

    public void SetTerrain(Terrain terrain)
    {
        m_terrain = terrain;
    }

    /***
     * A private method to determine the transforms which are necessary to
     * yield drawing coordinates from world coordinates and vice-versa.
     * The altitude can be supplied from an outer layer of logic.
     *
     * The routine will exit with the members set:
     * m_margin1
     * m_Scale_ppm
     * m_Scale_mpp
     * m_uhat
     * m_vhat
     * m_center
     */

    private void ComputeTransforms()
    {
        double lm_dtmaltitude = m_data.GetDatumAltitude();
        double lm_radaraltitude = m_data.GetRadarAltitude();
        double lm_altitude = lm_dtmaltitude;
        boolean useRadar = m_data.GetRadarOn() && (lm_radaraltitude < 10000.0);
        if (lm_dtmaltitude < 0.0)
            lm_altitude = 0.0;
        else if ((useRadar)&&(lm_radaraltitude > 0.0))
            lm_altitude = lm_radaraltitude;

        // determine margin1, where to place the LM.
        double transition_high_point = 500.0;
        double transition_high_value = 0.125;
        double transition_low_point = 100.0;
        double transition_low_value = 0.80;
        if (lm_altitude > transition_high_point)
        {
            m_margin1 = transition_high_value;
        }
        else if (lm_altitude < transition_low_point)
        {
            m_margin1 = transition_low_value;
        }
        else
        {
            m_margin1 = transition_low_value + (lm_altitude - transition_low_point) * (transition_high_value - transition_low_value) / (transition_high_point - transition_low_point);
        }

        // Get the direction of the lander
        double longitude = Math.toRadians(m_data.GetLongitude());

        // Basis vectors
        double S = Math.sin(longitude);
        double C = Math.cos(longitude);
        m_uhat.vec[0] = S;
        m_uhat.vec[1] = -C;
        m_vhat.vec[0] = C;
        m_vhat.vec[1] = S;

        // Scale: meters per pixel and pixels per meter
        m_Scale_mpp = lm_altitude / (mHeight * (1.0 - m_margin1 - m_margin2));
        m_Scale_ppm = 1.0 / m_Scale_mpp;

        // Centerpoint of the view.
        m_center.vec[0] = LanderUtils.MoonRadius * C;
        m_center.vec[1] = LanderUtils.MoonRadius * S;

        if (useRadar)
        {
            double diff = lm_dtmaltitude - lm_radaraltitude ;
            m_center.vec[0] += diff * C;
            m_center.vec[1] += diff * S;
        }

        m_center.sum(m_vhat.mul(mHeight * (0.5 - m_margin2) * m_Scale_mpp));

        if (m_scalingMode == ScalingMode.LM_TARGET)
        {
            double targetLong = m_data.GetTargetLongitude();
            double targetRadius = m_data.GetTargetRadius();
            // Global position of the target point.
            HVecN targetW = new HVecN(targetRadius * Math.cos(targetLong),
                    targetRadius * Math.sin(targetLong));
            // Global position of the LM.
            HVecN lmposW = (new HVecN(C,S)).scale(lm_dtmaltitude + LanderUtils.MoonRadius);
            HVecN diff = targetW.sub(lmposW);
            // Differences in position in X and Y.
            double xdiff = diff.dot(m_uhat);
            double ydiff = diff.dot(m_vhat);

            double xscale_mpp = Math.abs(xdiff / (mWidth * (0.5 - m_margin3)));
            double yscale_mpp = Math.abs(ydiff / (mHeight * (1.0 - m_margin1 - m_margin3)));
            double lm_target_scaling = Math.max(xscale_mpp, yscale_mpp);

            if (lm_target_scaling > m_Scale_mpp)
            {
                m_Scale_mpp = lm_target_scaling;
                m_Scale_ppm = 1.0 / m_Scale_mpp;

                // Re-center the view.
                m_center.copy(lmposW);
                m_center.sum(m_vhat.mul(-1.0 * mHeight * (0.5 - m_margin1) * m_Scale_mpp));
            }
        }
        else if (m_scalingMode == ScalingMode.LM_MAX)
        {
            double lm_max_scaling = 0.035;

            if (lm_max_scaling < m_Scale_mpp)
            {
                m_Scale_mpp = lm_max_scaling;
                m_Scale_ppm = 1.0 / m_Scale_mpp;
                HVecN lmposW = (new HVecN(C,S)).scale(lm_dtmaltitude + LanderUtils.MoonRadius);

                // Re-center the view.
                m_margin1 = 0.50;
                m_center.copy(lmposW);
            }
        }
        else if (m_scalingMode == ScalingMode.LM_ALL_MOON)
        {
            m_margin1 = 0.125;

            m_Scale_mpp = (lm_dtmaltitude + LanderUtils.MoonRadius * 2) / (mHeight * (1.0 - m_margin1 * 2));
            m_Scale_ppm = 1.0 / m_Scale_mpp;

            // Centerpoint of the view.
            m_center.vec[0] = 0.0;
            m_center.vec[1] = 0.0;
            m_center.sum(m_vhat.mul(lm_dtmaltitude * 0.5));
        }
    }

    // Take a vector, expressed in world coordinates, and return
    // it transformed into screen coordinates.
    private HVecN workvector = new HVecN(2);
    public HVecN TransformWorldToScreen(HVecN Pworld)
    {
        workvector.copy(Pworld);
        workvector.negsum(m_center);
        HVecN Pscreen = Pworld; //new HVecN(2);
        Pscreen.vec[0] = m_uhat.dot(workvector) * m_Scale_ppm + mHalfWidth;
        Pscreen.vec[1] = mHalfHeight - m_vhat.dot(workvector) * m_Scale_ppm;

        return Pscreen;
    }


    // A private method for trimming leading zeros out of a string representation
    // of a value and appending the resulting string to an existing StringBuilder.
    private void appendTrimLeading0(StringBuilder sb, String str, int nzeros)
    {
        int idx;
        int len = str.length();
        char c;

        for (idx=0; ((idx<nzeros)&&(idx<len)) ; ++idx)
        {
            c = str.charAt(idx);
            if (c == '0')
            {
                sb.append(' ');
            }
            else if ((c > '1')&&(c <= '9'))
            {
                sb.append(c);
                ++idx;
                break;
            }
            else
            {
                sb.append(c);
            }
        }

        for ( ; idx < len ; ++idx)
        {
            c = str.charAt(idx);
            sb.append(c);
        }
    }

    private PolygonSprite m_arrowSprite;

    // draw the ADI target vector
    private void drawTargetVector(Graphics g, int x, int y, int radius, double angle, Color color)
    {
        if (targetIndicatorOn)
        {
            int px = x + (int)(radius * Math.cos(angle));
            int py = y - (int)(radius * Math.sin(angle));

            g.setColor(color);
            //g.drawLine(x, y, px, py);

            if (m_arrowSprite == null)
            {
                double xs[] = {5,-5,-5};
                double ys[] = {0,-5,5};
                m_arrowSprite = new PolygonSprite(xs, ys);
            }
            m_arrowSprite.draw(g, px, py,
                                 angle,
                                 2.0, color, true);
        }
    }

    private void drawVelocityVector(Graphics g, int x, int y, int radius, Color color)
    {
        double epsilonV = 0.001;
        double HV = m_data.GetHSpeed();
        double VV = m_data.GetVSpeed();

        if ((Math.abs(HV) < epsilonV)&&(Math.abs(VV) < epsilonV))
            return;

        double norm = Math.sqrt(HV*HV + VV*VV);
        int px = x + (int)(radius * HV / norm);
        int py = y - (int)(radius * VV / norm);

        g.setColor(color);
        g.drawLine(x, y, px, py);

        //int orad = 5;
        //g.fillOval(px - orad, py - orad, orad * 2, orad * 2);

        if (m_arrowSprite == null)
        {
            double xs[] = {5,-5,-5};
            double ys[] = {0,-5,5};
            m_arrowSprite = new PolygonSprite(xs, ys);
        }
        m_arrowSprite.draw(g, px, py,
                             Math.atan2(VV, HV),
                             1.0, color, true);
    }

    // Draw the stars
    private BufferedImage m_Starfield_buffered;
    private RenderingHints m_Starfield_renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_OFF);
    private static final int NSTARS = 3000;
    private void drawStars(Graphics2D g, double rotationRadians)
    {
        if (m_Starfield_buffered == null)
        {
            int starFieldSize = (int)Math.ceil(2.0 * Math.sqrt((double)(mHeight * mHeight + mWidth * mWidth)));
            //m_Starfield_buffered = new BufferedImage(starFieldSize, starFieldSize, BufferedImage.TYPE_INT_ARGB);
            m_Starfield_buffered = new BufferedImage(starFieldSize, starFieldSize, BufferedImage.TYPE_INT_RGB);

            Graphics2D g2d = (Graphics2D)m_Starfield_buffered.getGraphics();
            g2d.setBackground(Color.BLACK);
            g2d.setColor(Color.WHITE);

            for (int i=0 ; i<NSTARS; ++i)
            {
                int x = (int)(Math.random() * starFieldSize);
                int y = (int)(Math.random() * starFieldSize);

                g2d.drawOval(x, y, 1, 1);
            }
        }
        int tx = (int)(-0.5 * m_Starfield_buffered.getWidth());
        int ty = (int)(-0.5 * m_Starfield_buffered.getHeight());
        AffineTransform T = g.getTransform();
        g.setTransform(new AffineTransform());
        g.translate(mHalfWidth, mHalfHeight);
        g.rotate(rotationRadians);
        g.translate(tx, ty);
        g.setRenderingHints(m_Starfield_renderingHints);
        g.drawImage(m_Starfield_buffered, null, 0, 0);
        g.setTransform(T);
        g.setRenderingHints(m_renderingHints);
    }

    // Draw the attitude data indicator
    private BufferedImage m_ADI_buffered;
    private void drawADI(Graphics g, int x, int y, int radius,
                         boolean drawTargetOnADI, double targetSightAngle)
    {
        // TODO: scale with panel size?
        int scale = 100; // scale to draw the LM at in this panel 
        int rad = radius; // radius of the instrument circle
        int rad2 = rad + 10; // radius of a second circle enclosing it

        m_LMpoly.draw(g, x, y, m_data.GetPitch() - 90.0, scale, GREEN, m_data.GetThrottlePercent() * 0.01);

        // We cache the ADI circle so we aren't constantly redrawing it.
        if (m_ADI_buffered == null)
        {
            m_ADI_buffered = new BufferedImage(rad2 * 2 + 5, rad2 * 2 + 5,
                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = (Graphics2D)m_ADI_buffered.getGraphics();

            g2d.setColor(Color.gray);
            g2d.addRenderingHints(m_renderingHints);
            g2d.drawLine(rad2-rad, rad2, rad2 + rad, rad2);
            g2d.drawLine(rad2, rad2-rad, rad2, rad2 + rad);
            g2d.drawOval(rad2-rad, rad2-rad, rad*2, rad*2);
            g2d.drawOval(0, 0, rad2*2, rad2*2);
        }
        g.drawImage(m_ADI_buffered, x-rad2, y-rad2, rad2 * 2 + 5, rad2 * 2 + 5, null);

        if (drawTargetOnADI)
            drawTargetVector(g, x, y, rad2, targetSightAngle, RED);

        drawVelocityVector(g, x, y, rad2, Color.cyan);
    }

    private void drawLongitudeVector(Graphics g, int x, int y, int radius, Color color)
    {
        //double epsilonV = 0.001;
        double lon = m_data.GetLongitude();
        double lonRad = lon * Math.PI / 180.0;

        int px = x + (int)(radius * Math.cos(lonRad));
        int py = y - (int)(radius * Math.sin(lonRad));

        g.setColor(color);
        g.drawLine(x, y, px, py);

        m_LMpoly.draw(g, px, py, m_data.GetHeading() - 90.0, 12, GREEN, m_data.GetThrottlePercent() * 0.01);
    }

    private void drawApolunePeriluneCueOnOPI(Graphics g, int x, int y, int radius, Color color)
    {
        OrbitType otype = m_data.GetOrbitType();

        if ((otype == OrbitType.Circle)||
            (otype == OrbitType.None)||
            (otype == OrbitType.DegenerateConic))
        {
            return;
        }

        g.setColor(color);
        g.setFont(m_OPIFont);
        FontMetrics fm = g.getFontMetrics();

        // If we've gotten this far, we know that
        // the orbit is either an ellipse, a parabola, or a hyperbola.
        boolean periOK = m_data.GetPeriluneOK();
        if (periOK)
        {
            double periLong = Math.toRadians(m_data.GetPeriluneLongitude());
            String p = new String("P");
            int halfwidth = fm.stringWidth(p) / 2;
            int halfheight = fm.getAscent() / 2;
            int px = x + (int)(radius * Math.cos(periLong)) - halfwidth;
            int py = y - (int)(radius * Math.sin(periLong)) + halfheight;
            g.drawString(p, px, py);
        }

        if (otype != OrbitType.Ellipse)
        {
            return;
        }

        boolean apoOK = m_data.GetApoluneOK();
        if (apoOK)
        {
            double apoLong = Math.toRadians(m_data.GetApoluneLongitude());
            String a = new String("A");
            int halfwidth = fm.stringWidth(a) / 2;
            int halfheight = fm.getAscent() / 2;
            int px = x + (int)(radius * Math.cos(apoLong)) - halfwidth;
            int py = y - (int)(radius * Math.sin(apoLong)) + halfheight;
            g.drawString(a, px, py);
        }
    }

    private void drawDOIandPDICueOnOPI(Graphics g, int x, int y, int radius, Color color)
    {
        g.setColor(color);
        g.setFont(m_OPIFont);
        FontMetrics fm = g.getFontMetrics();

        double DOILong = m_data.GetDOILongitude();

        String p = new String("DOI");
        int halfwidth = fm.stringWidth(p) / 2;
        int halfheight = fm.getAscent() / 2;
        int px = x + (int)(radius * Math.cos(DOILong)) - halfwidth;
        int py = y - (int)(radius * Math.sin(DOILong)) + halfheight;
        g.drawString(p, px, py);
    }

    // blink the target indicator
    private boolean targetIndicatorOn;
    private int targetIndicatorCountdown;
    private final int targetIndicatorCountdownResetValue = 13;
    private void drawTargetIndicator(Graphics g, int x, int y, int radius, Color color)
    {
        if (--targetIndicatorCountdown <= 0)
        {
            targetIndicatorOn = !targetIndicatorOn;
            targetIndicatorCountdown = targetIndicatorCountdownResetValue;
        }

        if (m_data.GetTargetSelected() && targetIndicatorOn)
        {
            double lon = m_data.GetTargetLongitude();
            m_targetIndicator.draw(g, (int)(x + radius * Math.cos(lon)),
                                   (int)(y - radius * Math.sin(lon)),
                                   lon,
                                   1.0,
                                   color,
                                   true);
        }
    }

    /**
     *  Draw the orbital position indicator
     *
     *  @param g: Graphics context to draw into
     *  @param x: location of the centerpoint of the OPI.
     *  @param y: location of the centerpoint of the OPI
     *  @param rad: radius of the OPI circle.
     */
    private BufferedImage m_OPI_buffered;
    private void drawOPI(Graphics g, int x, int y, int rad)
    {
        int rad2 = rad + 10;

        if (m_OPI_buffered == null)
        {
            m_OPI_buffered = new BufferedImage(rad2 * 2 + 5, rad2 * 2 + 5,
                BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = (Graphics2D)m_OPI_buffered.getGraphics();
            g2d.setColor(Color.gray);
            g2d.addRenderingHints(m_renderingHints);
            g2d.drawLine(rad2-rad, rad2, rad2 + rad, rad2);
            g2d.drawLine(rad2, rad2-rad, rad2, rad2 + rad);
            g2d.drawOval(rad2-rad, rad2-rad, rad*2, rad*2);
            g2d.drawOval(0, 0, rad2*2, rad2*2);

        }
        g.drawImage(m_OPI_buffered, x-rad2, y-rad2, rad2 * 2 + 5, rad2 * 2 + 5, null);

        drawTargetIndicator(g, x, y, rad, RED);     // Draw an indicator of where on the moon is our target.
        drawLongitudeVector(g, x, y, rad2, GREEN);  // Draw an indicator of where the LM is in relation to the moon.
        drawApolunePeriluneCueOnOPI(g, x, y, rad2, Color.WHITE);
        //drawDOIandPDICueOnOPI(g,x,y,rad2, GOLD);
    }

    /**
     * Draw the Vertical Velocity Monitor
     * @param g
     * @param centerx
     * @param centery
     * @param halfwidth
     * @param halfheight
     */
    // Handle this with a cached graphic
    private BufferedImage m_VVM_buffered;
    private void drawVVM(Graphics g, int centerx, int centery, int halfwidth, int halfheight)
    {
        if (m_VVM_buffered == null)
        {
            int width = halfwidth * 2;
            int height = halfheight * 2;
            m_VVM_buffered = new BufferedImage(width + 5, height + 5,
                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = (Graphics2D)m_VVM_buffered.getGraphics();
            g2d.addRenderingHints(m_renderingHints);

            int x1 = 0;
            int x2 = width;
            int y1 = 0;
            int y2 = height;
            int y3 = halfheight;
            int y4 = (height * 3) / 4;

            int x[] = { x1, x2, x2, x1 };
            int y[] = { y1, y1, y2, y2 };

            // The danger region: 3/4 throttle to full throttle
            g2d.setColor(RED);
            g2d.fillRect(x1, y1, width, halfheight / 2);

            // The good region: 1/2 to 3/4 throttle
            g2d.setColor(GREEN);
            g2d.fillRect(x1, halfheight / 2, halfwidth * 2, halfheight / 2);

            // The non-critical region: 0 to 1/2 throttle
            g2d.setColor(GREEN);
            g2d.drawLine(x1, y4, x2, y4);
            g2d.drawPolygon(x, y, 4);
        }
        g.drawImage(m_VVM_buffered, centerx-halfwidth, centery-halfheight, null);

        // TODO: This is very inefficient, can I please clean it up?
        int arrow1x[] = {7, -7, -7}; // This arrow points in
        int arrow2x[] = {-7, 7, 7}; // This arrow points out
        int arrow1y[] = {0, -7, 7};
        int arrow2y[] = {0, -7, 7};

        double VVM = m_data.GetVVM();
        if (VVM < 0.0)
            VVM = 0.0;
        else if (VVM > 1.0)
            VVM = 1.0;
        int ypos = (centery + halfheight) - (int)Math.round(VVM * halfheight * 2);
        for (int idx = 0; idx < 3 ; ++idx)
        {
            arrow1x[idx] += centerx - halfwidth;
            arrow1y[idx] += ypos;
        }
        g.setColor(YELLOW);
        g.fillPolygon(arrow1x, arrow1y, 3);

        // Get the current vertical thrust measure
        double throttle = 0.01 * m_data.GetThrottlePercent();
        double pitch = Math.toRadians(m_data.GetPitch());
        double vertThrust = throttle * Math.sin(pitch);
        if (vertThrust < 0.0)
            vertThrust = 0.0;
        else if (vertThrust > 1.0)
            vertThrust = 1.0;
        ypos = (centery + halfheight) - (int)Math.round(vertThrust * halfheight * 2);
        for (int idx = 0; idx < 3 ; ++idx)
        {
            arrow2x[idx] += centerx + halfwidth;
            arrow2y[idx] += ypos;
        }
        g.setColor(YELLOW);
        g.fillPolygon(arrow2x, arrow2y, 3);

    }

    private BufferedImage m_HVM_buffered;
    private void drawHVM(Graphics g, int centerx, int centery, int halfwidth, int halfheight)
    {
        int width = halfwidth * 2;
        int height = halfheight * 2;
        if (m_HVM_buffered == null)
        {
            m_HVM_buffered = new BufferedImage(width + 5, height + 5,
                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = (Graphics2D)m_HVM_buffered.getGraphics();
            g2d.addRenderingHints(m_renderingHints);

            int x1 = 0;
            int x2 = width / 8;
            //int x3 = width / 4;
            int x4 = width / 2;
            int x5 = (width * 3) / 4;
            int x6 = (width * 7) / 8;
            //int x7 = width;

            int y1 = 0;
            int y2 = height;

            g2d.setColor(RED);
            g2d.fillRect(x1, y1, width / 8, height);
            g2d.fillRect(x6, y1, width / 8, height);

            g2d.setColor(GREEN);
            g2d.fillRect(x2, y1, width / 8, height);
            g2d.fillRect(x5, y1, width / 8, height);

            g2d.drawRect(x1, y1, width, height);
            g2d.drawLine(x4, y1, x4, y2);
        }
        g.drawImage(m_HVM_buffered, centerx-halfwidth, centery-halfheight, null);

        // TODO: This is very inefficient, can I please clean it up?
        int arrow1x[] = {0, -7, 7};
        int arrow2x[] = {0, -7, 7};
        int arrow1y[] = {7, -7, -7};
        int arrow2y[] = {-7, 7, 7};

        // Get the current horizontal thrust measure
        double throttle = 0.01 * m_data.GetThrottlePercent();
        double pitch = Math.toRadians(m_data.GetPitch());
        double horizThrust = throttle * Math.cos(pitch);
        if (horizThrust < -1.0)
            horizThrust = -1.0;
        else if (horizThrust > 1.0)
            horizThrust = 1.0;
        int xpos = (centerx) + (int)Math.round(horizThrust * halfwidth);
        for (int idx = 0; idx < 3 ; ++idx)
        {
            arrow1x[idx] += xpos;
            arrow1y[idx] += centery - halfheight;
        }
        g.setColor(YELLOW);
        g.fillPolygon(arrow1x, arrow1y, 3);

        double HVM = m_data.GetHVM();

        if (HVM < -1.0)
            HVM = -1.0;
        else if (HVM > 1.0)
            HVM = 1.0;
        xpos = (centerx) + (int)Math.round(HVM * halfwidth);
        for (int idx = 0; idx < 3 ; ++idx)
        {
            arrow2x[idx] += xpos;
            arrow2y[idx] += centery + halfheight;
        }
        g.setColor(YELLOW);
        g.fillPolygon(arrow2x, arrow2y, 3);
    }

    private void formatAltitude(double altitude, StringBuilder sb)
    {
        if (altitude < 1000000.0)
        {
            appendTrimLeading0(sb, m_format3_3dot2.format(altitude), 4);
            sb.append(" M  ");
        }
        else
        {
            appendTrimLeading0(sb, m_format3_3dot2.format(altitude/1000.0), 4);
            sb.append(" KM ");
        }
    }

    // Draw the digital displays
    private void drawDigitalDisplays(Graphics g)
    {
        g.setFont(m_font);
        int x;
        int y;
        int lineHeight;

        FontMetrics fm = g.getFontMetrics();
        lineHeight = fm.getHeight();
        if (USE_DIGITAL_FONT)
            lineHeight += 5;

        g.setColor(GREEN);

        ///////////////////////////////////////////////////////////////
        // Where to place the text over the ADI
        int OverADIx = 50;
        int OverADIy = lineHeight;

        // Place the Pitch over the ADI.
        x = OverADIx;
        y = OverADIy;
        m_sb.setLength(0);
        m_sb.append(" PITCH: ");
        m_sb.append(m_format3dot2.format(m_data.GetPitch()));
        m_sb.append(" DEG");
        g.drawString(m_sb.toString(), x, y);

        ///////////////////////////////////////////////////////////////
        // Where to place the text over the OPI
        int OverOPIx = mWidth - 215;
        int OverOPIy = lineHeight;

        x = OverOPIx;
        y = OverOPIy;
        m_sb.setLength(0);
        m_sb.append("LONG: ");
        m_sb.append(m_format3dot2.format(m_data.GetLongitude()));
        m_sb.append(" DEG");
        g.drawString(m_sb.toString(), x, y);

        ///////////////////////////////////////////////////////////////
        // Where to place the left hand digital display block
        int LeftHandBlockx = 10;
        int LeftHandBlocky = 340;

        x = LeftHandBlockx;
        y = LeftHandBlocky;
        m_sb.setLength(0);
        m_sb.append(" V SPEED: ");
        appendTrimLeading0(m_sb, m_format1_3dot2.format(m_data.GetVSpeed()), 7);
        m_sb.append(" M/S");
        g.setColor(m_data.GetVSpeedColor());
        g.drawString(m_sb.toString(), x, y);

        if (ADVANCED_DISPLAY)
        {
            y += lineHeight;
            m_sb.setLength(0);
            m_sb.append(" DTM ALT: ");
            formatAltitude(m_data.GetDatumAltitude(), m_sb);
            g.setColor(GREEN);
            g.drawString(m_sb.toString(), x, y);
    
            y += lineHeight;
            m_sb.setLength(0);
            m_sb.append(" RDR ALT: ");
            if (m_data.GetRadarOn())
            {
                double altitude = m_data.GetRadarAltitude();
                if (altitude <= 10000.0)
                {
                    formatAltitude(altitude, m_sb);
                    g.setColor(GREEN);
                }
                else
                {
                    m_sb.append(" -- NO SIGNAL --");
                    g.setColor(RED);
                }
            }
            else
            {
                m_sb.append(" RADAR OFF");
                g.setColor(RED);
            }
            g.drawString(m_sb.toString(), x, y);
            y += lineHeight;
        }

        y += lineHeight;
        m_sb.setLength(0);
        m_sb.append(" H SPEED: ");
        appendTrimLeading0(m_sb, m_format1_3dot2.format(m_data.GetHSpeed()), 7);
        m_sb.append(" M/S");
        g.setColor(m_data.GetHSpeedColor());
        g.drawString(m_sb.toString(), x, y);

        if (ADVANCED_DISPLAY)
        {
            y += lineHeight;
            m_sb.setLength(0);
            m_sb.append(" TGT DIS: ");
            if (m_data.GetTargetSelected())
            {
                g.setColor(GREEN);
                m_sb.append(m_format1_3dot2.format(m_data.GetDistanceToTarget()));
                m_sb.append(" M");
            }
            else
            {
                g.setColor(Color.yellow);
                m_sb.append(" NO TARGET");
            }
            g.drawString(m_sb.toString(), x, y);
            y += lineHeight;
        }
        
        y += lineHeight;
        m_sb.setLength(0);
        m_sb.append(" FUEL   : ");
        m_sb.append(m_format4dot1.format(m_data.GetFuel()));
        m_sb.append(" KG (");
        double percent = m_data.GetFuelPercent();
        m_sb.append(m_format3dot1percent.format(percent));
        m_sb.append(")");

        if (percent > 5.0)
            g.setColor(GREEN);
        else if (percent > 0.0)
            g.setColor(Color.yellow);
        else
            g.setColor(Color.red);
        g.drawString(m_sb.toString(), x, y);
        
        if (ADVANCED_DISPLAY)
        {
            y += lineHeight;
            m_sb.setLength(0);
            m_sb.append(" THROTTL:  ");
            m_sb.append(m_format3percent.format(m_data.GetThrottlePercent()));
    		
            m_sb.append(" ( ");
            m_sb.append(m_format3dot2.format(m_data.GetFlowRate()));
            m_sb.append(" kg/s)");
    
            g.setColor(GREEN);
            g.drawString(m_sb.toString(), x, y);
            y += lineHeight;
    
            m_sb.setLength(0);
            m_sb.append(" DELTA V:  ");
            m_sb.append(this.m_format3dot2.format(m_data.GetDeltaV()));
            
            m_sb.append(" m/s");
    
            g.setColor(GREEN);
            g.drawString(m_sb.toString(), x, y);
        }
        y += lineHeight;

        if (DISPLAY_PERFORMANCE_DATA)
        {
            m_sb.setLength(0);
            m_sb.append(" perf  = ");
            m_sb.append(m_format2dot2.format(m_data.GetTPS()));
            y += lineHeight;
            m_sb.append(" ticks/s");
            g.drawString(m_sb.toString(), x, y);
        }
        
        

        ///////////////////////////////////////////////////////////////
        // Where to place the right hand digital display block
        int RightHandBlockx = mWidth - 280;
        int RightHandBlocky = LeftHandBlocky;

        x = RightHandBlockx;
        y = RightHandBlocky;

        if (ADVANCED_DISPLAY)
        {
            m_sb.setLength(0);
            m_sb.append(" APOLUNE: ");
            LunarSpacecraft2D.OrbitType orbitType = m_data.GetOrbitType();
            if (m_data.GetApoluneOK())
            {
                formatAltitude(m_data.GetApolune(), m_sb);
                g.setColor(GREEN);
                g.drawString(m_sb.toString(), x, y);
    
                m_sb.setLength(0);
                y += lineHeight;
                if (orbitType != LunarSpacecraft2D.OrbitType.Circle)
                {
                    m_sb.append("                   ");
                    m_sb.append(m_format3dot2.format(m_data.GetApoluneLongitude()));
                    m_sb.append(" DEG");
                }
                g.drawString(m_sb.toString(), x, y);
                y += lineHeight;
            }
            else if ((orbitType == LunarSpacecraft2D.OrbitType.Parabola)||
                     (orbitType == LunarSpacecraft2D.OrbitType.Hyperbola))
            {
                m_sb.append(" ESCAPE ORBIT");
                g.setColor(GREEN);
                g.drawString(m_sb.toString(), x, y);
                y += 2 * lineHeight;
            }
            else
            {
                m_sb.append("     --- ---.--");
                g.setColor(Color.red);
                g.drawString(m_sb.toString(), x, y);
                y += 2 * lineHeight;
            }
    
            m_sb.setLength(0);
            m_sb.append(" PERILUN: ");
            if (m_data.GetPeriluneOK())
            {
                formatAltitude(m_data.GetPerilune(), m_sb);
                g.setColor(GREEN);
                g.drawString(m_sb.toString(), x, y);
                m_sb.setLength(0);
                y += lineHeight;
                if (orbitType != LunarSpacecraft2D.OrbitType.Circle)
                {
                    m_sb.append("                   ");
                    m_sb.append(m_format3dot2.format(m_data.GetPeriluneLongitude()));
                    m_sb.append(" DEG");
                }
                g.drawString(m_sb.toString(), x, y);
                y += lineHeight;
            }
            else
            {
                m_sb.append("     --- ---.--");
                g.setColor(Color.red);
                g.drawString(m_sb.toString(), x, y);
                y += 2 * lineHeight;
            }
        }
        
        m_sb.setLength(0);
        m_sb.append(" TIME   : ");
        double time_sec = m_data.GetTime();
        double time_hours = Math.floor(time_sec / 3600.0);
        m_sb.append(m_format2.format(time_hours));
        m_sb.append(":");
        double time_minutes = Math.floor((time_sec % 3600.0) / 60.0);
        m_sb.append(m_format2.format(time_minutes));
        m_sb.append(":");
        double time_seconds = Math.floor(time_sec % 60.0);
        m_sb.append(m_format2.format(time_seconds));
        g.setColor(GREEN);
        g.drawString(m_sb.toString(), x, y);

        y += lineHeight;
        m_sb.setLength(0);
        m_sb.append(" TIMEFAC: ");
        int timefactor = m_data.GetTimeFactor();
        m_sb.append(timefactor);
        m_sb.append(" X");
        if (timefactor == 1)
        {
            m_sb.append(" [REAL TIME] ");
        }
        g.setColor(GREEN);
        g.drawString(m_sb.toString(), x, y);
        y += lineHeight;
        m_sb.setLength(0);
        m_sb.append(" A.PILOT: ");
        m_sb.append(m_data.GetAutopilotModeString());
        if (m_data.GetAutopilotOn()) {
            m_sb.append(" [ON]");
        } else {
            m_sb.append(" [OFF]");
        }
        g.setColor(GREEN);
        g.drawString(m_sb.toString(), x, y);

        y += lineHeight;
        m_sb.setLength(0);
        m_sb.append(" ROTMODE: ");
        if (m_data.GetAutopilotOn())
        {
            m_sb.append("AUTO");
        }
        else if (m_data.GetRotMode() == LunarSpacecraft2D.RCSRotMode.AttitudeFree)
        {
            m_sb.append("FREE");
        }
        else if (m_data.GetRotMode() == LunarSpacecraft2D.RCSRotMode.AttitudeDamp)
        {
            m_sb.append("DAMP");
        }
        g.setColor(GREEN);
        g.drawString(m_sb.toString(), x, y);
    }

    public void paintComponent(Graphics g)
    {
        renderElements(g);
    }

    private HVecN m_mooncenter = new HVecN(0.0, 0.0);
    public void renderElements(Graphics g1)
    {
        Graphics2D g = (Graphics2D)g1;

        g.addRenderingHints(m_renderingHints);

        ComputeTransforms();

        // Where's the LM?
        double lmdatumaltitude = m_data.GetDatumAltitude();
        double lmradius =  lmdatumaltitude + LanderUtils.MoonRadius;
        double lmlongitude = Math.toRadians(m_data.GetLongitude());

        // Transform the moon into screen coordinates.
        m_mooncenter.vec[0] = m_mooncenter.vec[1] = 0.0;
        m_mooncenter = TransformWorldToScreen(m_mooncenter);

        double radiusInPixels = LanderUtils.MoonRadius * m_Scale_ppm;

        double r = m_moonImg.getHeight(null) * 0.5;
        double s = radiusInPixels / r;
        double alpha = lmlongitude - LanderUtils.HALF_PI;

        drawStars(g, lmlongitude);

        // Set up transforms to put the moon texture on to the screen.
        AffineTransform t1 = AffineTransform.getTranslateInstance(-r, -r);
        AffineTransform r1 = AffineTransform.getRotateInstance(alpha);
        AffineTransform s1 = AffineTransform.getScaleInstance(s,s);
        AffineTransform t2 = AffineTransform.getTranslateInstance(m_mooncenter.vec[0], m_mooncenter.vec[1]);
        r1.concatenate(t1);
        s1.concatenate(r1);
        t2.concatenate(s1);

        // What are the longitude bounds of the screen?
        double metersSpan = (mHalfWidth * m_Scale_mpp);
        double longitudeSpan;
        boolean closeUpMoon;
        if (metersSpan < LanderUtils.MoonRadius)
        {
            longitudeSpan = Math.asin(metersSpan/LanderUtils.MoonRadius);
            closeUpMoon = true;
        }
        else
        {
            longitudeSpan = LanderUtils.HALF_PI;
            closeUpMoon = false;
        }
        //double minlong = (lmlongitude - longitudeSpan); // minlong is from -0.5 PI to 2 PI
        //double maxlong = (lmlongitude + longitudeSpan); // maxlong is from 0 to 2.5 PI

        // Paint the longitude lines.
        double radius = m_data.GetDatumAltitude() + LanderUtils.MoonRadius + metersSpan * 2; // give it a margin to ensure that we go beyond the screen
        //int moonx = (int)Math.round(mooncenter.vec[0]);
        //int moony = (int)Math.round(mooncenter.vec[1]);
        g.setColor(VERYDARKGREY);

        double lon = 0.0; // longitude, in degrees
        double lonincr = 5.0;
        double lonrad = 0.0; // and in radians
        double radincr = Math.toRadians(lonincr);
        for (lon = 0.0 ; lon < 360.0 ; lon += lonincr, lonrad += radincr)
        {
            double C = Math.cos(lonrad);
            double S = Math.sin(lonrad);
            HVecN linestart = new HVecN(LanderUtils.MoonRadius * C, LanderUtils.MoonRadius * S);
            HVecN lineend = new HVecN(radius * C, radius * S);
            linestart = TransformWorldToScreen(linestart);
            lineend = TransformWorldToScreen(lineend);
            g.drawLine((int)(Math.round(linestart.vec[0])), (int)(Math.round(linestart.vec[1])), (int)(Math.round(lineend.vec[0])), (int)(Math.round(lineend.vec[1])));
        }

        // Paint the moon.
        double moonradInScreenCoords = LanderUtils.MoonRadius * m_Scale_ppm;

        if (closeUpMoon)
        {
            // Just draw the datum circle.
            g.setColor(DARKBLUE);
            g.drawOval((int)Math.round(m_mooncenter.vec[0] - moonradInScreenCoords),
                    (int)Math.round(m_mooncenter.vec[1] - moonradInScreenCoords),
                    (int)Math.round(moonradInScreenCoords) * 2, (int)Math.round(moonradInScreenCoords) * 2);
        }
        else
        {
            // fill the datum circle.
            g.setColor(AVERAGEGREY);
            g.fillOval((int)Math.round(m_mooncenter.vec[0] - moonradInScreenCoords),
                    (int)Math.round(m_mooncenter.vec[1] - moonradInScreenCoords),
                    (int)Math.round(moonradInScreenCoords) * 2, (int)Math.round(moonradInScreenCoords) * 2);
        }

        // Paint the altitude lines
        if (lmdatumaltitude < 1000000)
        {
            g.setColor(VERYDARKGREY);
            double linealt     =  20000.0; // line altitude, m
            double linelatincr =  20000.0; // increment
            double linealt_end = 200000.0;
            for ( ; linealt <= linealt_end; linealt += linelatincr)
            {
                double radiusInScreenCoords = (LanderUtils.MoonRadius + linealt) * m_Scale_ppm;
                double radiusInScreenCoordsX2 = radiusInScreenCoords * 2;
                g.drawOval((int)Math.round(m_mooncenter.vec[0] - radiusInScreenCoords), (int)Math.round(m_mooncenter.vec[1] - radiusInScreenCoords),
                            (int)Math.round(radiusInScreenCoordsX2), (int)Math.round(radiusInScreenCoordsX2));
            }
        }

        // Paint the terrain
        if ((PAINT_TERRAIN)&&(closeUpMoon))
        {
            m_terrain.drawWithWindow((Graphics2D)g,
                                      (int)(Math.round(m_mooncenter.vec[0])),
                                      (int)(Math.round(m_mooncenter.vec[1])),
                                      m_Scale_ppm,
                                      Math.toRadians(90.0 - m_data.GetLongitude()),
                                      lmlongitude,
                                      longitudeSpan,
                                      mWidth, mHeight, AVERAGEGREY);
        }

        // Paint the moon texture
        Composite c = g.getComposite();

        // Linear alpha scaling function.
        // double moonalpha = ((m_Scale_mpp - 100.0) / 4400.0);
        // Log alpha scaling function.
        double moonalpha = ((Math.log10(m_Scale_mpp)) - 2.0) / 1.64 ;
        if (moonalpha < 0.0)
            moonalpha = 0.0;
        else if (moonalpha > 1.0)
            moonalpha = 1.0;
        Composite c2 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)moonalpha);
        g.setComposite(c2);
        g.drawImage(m_moonImg, t2, null);
        g.setComposite(c);

        // Paint the target indicator
        double targetTheta = m_data.GetTargetLongitude();
        double targetRadius = m_data.GetTargetRadius();
        HVecN workingPoint = new HVecN(targetRadius * Math.cos(targetTheta),
                                      targetRadius * Math.sin(targetTheta));
        workingPoint = TransformWorldToScreen(workingPoint);
        // Draw a triangle: let L be the LM's position, O is the center of
        // the moon, and H is the horizon from the LM.  The angle between
        // OH and HL is a right angle.  horizonAngle is the angle between
        // OL and OH.
        double horizonAngle = Math.acos(LanderUtils.MoonRadius / lmradius);
        double angleLM2Target =
            LanderUtils.absAngleDifference(lmlongitude, targetTheta);

        // positions of the lm and the target
        double lmx = lmradius * Math.cos(lmlongitude);
        double lmy = lmradius * Math.sin(lmlongitude);
        double targetx = targetRadius * Math.cos(targetTheta);
        double targety = targetRadius * Math.sin(targetTheta);

        // difference in world coordinates.
        double dx = targetx - lmx;
        double dy = targety - lmy;

        // difference in our local frame
        double ldx = m_uhat.vec[0] * dx + m_uhat.vec[1] * dy;
        double ldy = m_vhat.vec[0] * dx + m_vhat.vec[1] * dy;

        double sightAngle = Math.atan2(ldy, ldx);

        // Draw the target indicator on the terrain
        if ((angleLM2Target < longitudeSpan)||(closeUpMoon == false))
        {
            m_targetIndicator.draw(g,(int)(Math.round(workingPoint.vec[0])),
                                     (int)(Math.round(workingPoint.vec[1])),
                                     1.5708 - lmlongitude + targetTheta,
                                     1.5,
                                     RED,
                                     true);
        }
        boolean drawTargetOnADI = (angleLM2Target < horizonAngle);

        // Draw the DOI cue
        g.setColor(GOLD);
        boolean DOIset = m_data.GetDOISet();
        if (DOIset)
        {
            double DOIlong = m_data.GetDOILongitude();
            double DOIradius = m_data.GetDOIRadius();
            workingPoint.vec[0] = DOIradius * Math.cos(DOIlong);
            workingPoint.vec[1] = DOIradius * Math.sin(DOIlong);
            workingPoint = TransformWorldToScreen(workingPoint);
            g.drawRect((int)Math.round(workingPoint.vec[0] - HALF_PROMPT_BOX_SIZE),
            		   (int)Math.round(workingPoint.vec[1] - HALF_PROMPT_BOX_SIZE),
            		   PROMPT_BOX_SIZE, PROMPT_BOX_SIZE);
            g.drawString("DOI",
            		(int)Math.round(workingPoint.vec[0] - 14),
            		(int)Math.round(workingPoint.vec[1] - HALF_PROMPT_BOX_SIZE - 10));
        }

        // Draw the PDI cue
        boolean PDIset = m_data.GetPDISet();
        if (PDIset)
        {
            double PDIlong = m_data.GetPDILongitude();
            double PDIradius = m_data.GetPDIRadius();
            workingPoint.vec[0] = PDIradius * Math.cos(PDIlong);
            workingPoint.vec[1] = PDIradius * Math.sin(PDIlong);
            workingPoint = TransformWorldToScreen(workingPoint);
            g.drawRect((int)Math.round(workingPoint.vec[0] - HALF_PROMPT_BOX_SIZE),
                       (int)Math.round(workingPoint.vec[1] - HALF_PROMPT_BOX_SIZE),
                       PROMPT_BOX_SIZE, PROMPT_BOX_SIZE);
            g.drawString("PDI",
                    (int)Math.round(workingPoint.vec[0] - 14),
                    (int)Math.round(workingPoint.vec[1] - HALF_PROMPT_BOX_SIZE - 10));
        }

        // Paint the LM
        int LMx = mHalfWidth;
        int LMy = (int)Math.round(m_margin1 * mHeight);

        m_LMsprite.draw(g,
                      // (int)Math.round(lmposition.vec[0]), (int)Math.round(lmposition.vec[1]), m_data.GetPitch() - 90.0,
                      mHalfWidth, (int)Math.round(m_margin1 * mHeight), m_data.GetPitch() - 90.0,
                      Math.max(1, m_Scale_ppm * 5.25), // TODO: FIX THIS MAGIC NUMBER!!!
                      GREEN, m_data.GetThrottlePercent() * 0.01);

        m_particles.draw(g,
                mHalfWidth, (int)Math.round(m_margin1 * mHeight), m_Scale_ppm, 0.05, Color.ORANGE);

        // paint the velocity vector.
        //double lmradaraltitude = m_data.GetRadarAltitude();
        //boolean useRadar = m_data.GetRadarOn();
        //boolean drawVelocityVector = ((!useRadar)&&(lmdatumaltitude < 500))||((useRadar)&&(lmradaraltitude < 500));
        if (true /* drawVelocityVector */)
        {
            int vecx = LMx + (int)Math.round(m_data.GetHSpeed() * m_Scale_ppm);
            int vecy = LMy - (int)Math.round(m_data.GetVSpeed() * m_Scale_ppm);
            g.setColor(Color.cyan);
            g.drawLine(LMx, LMy, vecx, vecy);
        }

        // Paint the instruments
        drawADI(g, 140, 160, 120, drawTargetOnADI, sightAngle);
        drawOPI(g, mWidth - 140, 160, 120);
        //drawVVM(g, 30, 600, 8, 100);
        drawVVM(g, 15, 160, 5, 100);
        //drawHVM(g, 116, 730, 100, 8);
        drawHVM(g, 140, 310, 100, 5);
        drawDigitalDisplays(g);        
        
        m_EventDisplay.draw(g, mEventDisplayX, mEventDisplayY);

        // Paint the help display
        if (m_displayHelp)
        {
            renderHelpDisplay(g);
        }

    }

    public int drawMultilineText(Graphics g, String s, int x, int y, Color c)
    {
        int lineHeight;

        FontMetrics fm = g.getFontMetrics();
        lineHeight = fm.getHeight() + 5;
        g.setColor(c);

        String[] sarray = s.split("\n");
        for (int i=0 ; i<sarray.length ; ++i)
        {
            g.drawString(sarray[i],x,y);
            y += lineHeight;
        }

        return y;
    }

    public void addKeyBindingsString(String s)
    {
        m_keyBindingsString = s;
    }

    public void renderHelpDisplay(Graphics g)
    {
        Graphics2D g2d = (Graphics2D)g;
        g2d.setFont(m_helpFont);

        int helpHeight = m_helpBg.getHeight(this);
        int helpWidth = m_helpBg.getWidth(this);
        int bgx = (mWidth - helpWidth) / 2;
        int bgy = (mHeight - helpHeight) / 2;

        g2d.setColor(Color.BLACK);
        g2d.fillRect(bgx, bgy, helpWidth, helpHeight);

        int nexty = drawMultilineText(g,
                "Lunar Lander 0.4 by Patrick M Brennan\nMISSON: " +
                "Land at the target (Red Triangle).\n\nKey Bindings:\n",
                          bgx + 5, bgy + 15, GREEN);

        nexty = drawMultilineText(g, m_keyBindingsString, bgx + 5, nexty, GREEN);

        drawMultilineText(g, "\nPress ESC or F1 to return to the game.", bgx + 5, nexty, GREEN);

        g2d.setColor(HELPEDGE);
        for (int i=0; i<10 ; ++i)
        {
            g2d.draw3DRect(bgx-i, bgy-i, helpWidth + i * 2, helpHeight + i * 2, true);
        }

        Composite c = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        g2d.drawImage(m_helpBg, bgx, bgy, null);
        g2d.setComposite(c);
    }

    private Font m_font;
    private Font m_helpFont;
    private Font m_OPIFont;
    private Font m_EventFont;

    private StringBuilder m_sb;

    protected Image m_img;

    protected LMInstrumentData m_data;

    protected LMPolygon m_LMpoly = new LMPolygon();
    protected LMSprite m_LMsprite = new LMSprite();
    protected LanderParticles m_particles = new LanderParticles(800);

    protected ScalingMode m_scalingMode = ScalingMode.LM_TARGET;

    protected boolean m_displayHelp = false;

    // the vertical margin between the top of the viewport and the center of
    // the LM, expressed as a fraction of the viewport height.
    protected double m_margin1 = 0.125;  // 0.5;

    // the vertical margin between the bottom of the viewport and the crest
    // of the moon, expressed as a fraction of the viewport height.
    protected double m_margin2 = 0.1;

    // the vertical or horizontal margin used for the landing target
    // in certain modes.
    protected double m_margin3 = 0.1;

    // Scale factor, pixels per meter
    protected double m_Scale_ppm;

    // Scale factor, meters per pixel
    protected double m_Scale_mpp;
    public double m_Scale_mpp_low = -1.0;
    public double m_Scale_mpp_high = -1.0;

    // Basis vectors of the view coordinate system,
    // expressed in the world coordinate system.
    HVecN m_uhat = new HVecN(2);
    HVecN m_vhat = new HVecN(2);

    // Centerpoint of the screen, in world coordinates.
    HVecN m_center = new HVecN(2);

    private Terrain m_terrain;

    // Numeric Formatting for digital displays
    protected DecimalFormatSymbols m_symbols;
    protected DecimalFormat m_format2;
    protected DecimalFormat m_format4dot1;
    protected DecimalFormat m_format3percent;
    protected DecimalFormat m_format3dot1percent;
    protected DecimalFormat m_format2dot2;
    protected DecimalFormat m_format3dot2;
    protected DecimalFormat m_format1_3dot2;
    protected DecimalFormat m_format3_3dot2;

    protected RenderingHints m_renderingHints;

    private PolygonSprite m_targetIndicator;

    private Image m_helpBg;

    private Image m_moonImg;
    private String m_keyBindingsString;
    private static final int PROMPT_BOX_SIZE = 50;
    private static final int HALF_PROMPT_BOX_SIZE = PROMPT_BOX_SIZE / 2;

    public void setScalingMode(ScalingMode mode) {
        m_scalingMode = mode;
    }

    public void setHelpDisplay(boolean showhelp)
    {
        m_displayHelp = showhelp;
    }
}


