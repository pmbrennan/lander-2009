package net.pbrennan.Lander_2009;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
//import java.awt.Rectangle;
import java.lang.StringBuilder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class LMInstruments extends Canvas implements ILMInstrumentDataListener
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -2630108500478742147L;
	private static final int WIDTH = 600;
    private static final int HEIGHT = 800;
    private static final Color GREEN = new Color(0x00ff00);
    private static final Color YELLOW = new Color(0xffff00);
    private static final Color RED = new Color(0xff0000);

    // Interface levels: don't overwhelm beginners but
    // give lots of information to experts.
    private static final int SIMPLE = 0;
    private static final int NORMAL = 1;
    private static final int EXPERT = 2;

    LMInstruments()
    {
        setBackground(Color.black);
        setSize(WIDTH,HEIGHT);

        m_symbols = new DecimalFormatSymbols();
        m_symbols.setDecimalSeparator('.');
        m_symbols.setGroupingSeparator(' ');
        m_symbols.setMinusSign('-');
        m_symbols.setPercent('%');

        // Some fonts I tried.
        // TODO: If the Digital Dream font is available, use it; otherwise, default to monospaced.
        String fontNames[] = { "Digital-7",
                               "DS-Digital",
                               "Digital Dream Narrow",
                               "Lucida Console",
                               "Data Control",
                               "Digital Dream Skew Narrow", // Use this one if I can!
                               "Monospaced"
                              };
        m_font = new Font(fontNames[5], Font.PLAIN, 18);
        //m_font = new Font(fontNames[6], Font.BOLD, 18);

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
    }

    public void listen(LMInstrumentData newdata)
    {
        m_data.copy(newdata);
    }

    public void SetData(LMInstrumentData newdata)
    {
        m_data.copy(newdata);
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

    private PolygonSprite m_velocityArrow;
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

        if (m_velocityArrow == null)
        {
            double xs[] = {5,-5,-5};
            double ys[] = {0,-5,5};
            m_velocityArrow = new PolygonSprite(xs, ys);
        }
        m_velocityArrow.draw(g, px, py,
                             Math.atan2(VV, HV),
                             1.0, color, true);
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

    // Draw the Vertical Velocity Monitor (VVM)
    private void drawVVM(Graphics g, int centerx, int centery, int halfwidth, int halfheight)
    {
        int x1 = centerx - halfwidth;
        int x2 = centerx + halfwidth;
        int y1 = centery - halfheight;
        int y2 = centery + halfheight;
        int y3 = centery - halfheight / 2;
        int y4 = centery + halfheight / 2;

        int x[] = { x1, x2, x2, x1 };
        int y[] = { y1, y1, y2, y2 };

        // The danger region
        g.setColor(RED);
        g.fillRect(x1, y1, halfwidth * 2, halfheight / 2);

        // The good region
        g.setColor(GREEN);
        g.fillRect(x1, y3, halfwidth * 2, halfheight / 2);

        g.setColor(GREEN);
        g.drawLine(x1, y4, x2, y4);
        g.drawPolygon(x, y, 4);

        int arrow1x[] = {7, -7, -7};
        int arrow2x[] = {-7, 7, 7};
        int arrow1y[] = {0, -7, 7};
        int arrow2y[] = {0, -7, 7};

        // Get the current vertical thrust measure
        double throttle = 0.01 * m_data.GetThrottlePercent();
        double pitch = Math.toRadians(m_data.GetPitch());
        double vertThrust = throttle * Math.sin(pitch);
        if (vertThrust < 0.0)
            vertThrust = 0.0;
        else if (vertThrust > 1.0)
            vertThrust = 1.0;
        int ypos = y2 - (int)Math.round(vertThrust * halfheight * 2);
        for (int idx = 0; idx < 3 ; ++idx)
        {
            arrow1x[idx] += x1;
            arrow1y[idx] += ypos;
        }
        g.setColor(YELLOW);
        g.fillPolygon(arrow1x, arrow1y, 3);

        double VVM = m_data.GetVVM();
        if (VVM < 0.0)
            VVM = 0.0;
        else if (VVM > 1.0)
            VVM = 1.0;
        ypos = y2 - (int)Math.round(VVM * halfheight * 2);
        for (int idx = 0; idx < 3 ; ++idx)
        {
            arrow2x[idx] += x2;
            arrow2y[idx] += ypos;
        }
        g.setColor(YELLOW);
        g.fillPolygon(arrow2x, arrow2y, 3);

    }

    public void update(Graphics g)
    {
        if (m_img == null)
            m_img = createImage(WIDTH,HEIGHT);
        Graphics ig = m_img.getGraphics();
        paint(ig);
        g.drawImage(m_img,0,0,this);
    }

    public void paint(Graphics g)
    {
        g.setColor(Color.black);
        g.fillRect(0,0,WIDTH,HEIGHT);

        g.setFont(m_font);
        int x;
        int y;
        int lineHeight;

        FontMetrics fm = g.getFontMetrics();
        //lineHeight = fm.getHeight();
        lineHeight = fm.getHeight() + 5; // for Digital Dream
        //String str;

        m_sb.setLength(0);
        x = 0;
        y = fm.getAscent() + lineHeight;

        if (m_ilevel > SIMPLE)
        {
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

            y += lineHeight * 2;
        }

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
        y += lineHeight;

        if (m_ilevel == EXPERT)
        {
            m_sb.setLength(0);
            m_sb.append(" DELTA V: ");
            m_sb.append(m_format4dot1.format(m_data.GetDeltaV()));
            m_sb.append(" M/S");
            g.setColor(GREEN);
            g.drawString(m_sb.toString(), x, y);
            y += lineHeight;
        }

        if (m_ilevel > SIMPLE)
        {
            m_sb.setLength(0);
            m_sb.append(" THROTTL:  ");
            m_sb.append(m_format3percent.format(m_data.GetThrottlePercent()));
            m_sb.append("   FLOW: ");
            m_sb.append(m_format2dot2.format(m_data.GetFlowRate()));
            m_sb.append(" KG/S");
            g.setColor(GREEN);
            g.drawString(m_sb.toString(), x, y);
            y += lineHeight;
        }

        y += lineHeight;
        m_sb.setLength(0);
        m_sb.append(" V SPEED: ");
        appendTrimLeading0(m_sb, m_format1_3dot2.format(m_data.GetVSpeed()), 7);
        m_sb.append(" M/S");
        g.setColor(m_data.GetVSpeedColor());
        g.drawString(m_sb.toString(), x, y);

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
            formatAltitude(m_data.GetRadarAltitude(), m_sb);
            g.setColor(GREEN);
        }
        else
        {
            m_sb.append(" RADAR OFF");
            g.setColor(Color.red);
        }
        g.drawString(m_sb.toString(), x, y);

        y += lineHeight * 2;
        m_sb.setLength(0);
        m_sb.append(" H SPEED: ");
        appendTrimLeading0(m_sb, m_format1_3dot2.format(m_data.GetHSpeed()), 7);
        m_sb.append(" M/S");
        g.setColor(m_data.GetHSpeedColor());
        g.drawString(m_sb.toString(), x, y);

        if (m_ilevel > SIMPLE)
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
        }

        if (m_ilevel > NORMAL)
        {
            y += lineHeight * 2;
            m_sb.setLength(0);
            m_sb.append(" APOLUNE: ");
            LunarSpacecraft2D.OrbitType orbitType = m_data.GetOrbitType();
            if (m_data.GetApoluneOK())
            {
                formatAltitude(m_data.GetApolune(), m_sb);
                if (orbitType != LunarSpacecraft2D.OrbitType.Circle)
                {
                    m_sb.append(m_format3dot2.format(m_data.GetApoluneLongitude()));
                    m_sb.append(" DEG");
                }
                g.setColor(GREEN);
            }
            else if ((orbitType == LunarSpacecraft2D.OrbitType.Parabola)||
                     (orbitType == LunarSpacecraft2D.OrbitType.Hyperbola))
            {
                m_sb.append(" ESCAPE ORBIT");
                g.setColor(GREEN);
            }
            else
            {
                m_sb.append("     --- ---.-- N/A");
                g.setColor(Color.red);
            }
            g.drawString(m_sb.toString(), x, y);

            y += lineHeight;
            m_sb.setLength(0);
            m_sb.append(" PERILUN: ");
            if (m_data.GetPeriluneOK())
            {
                formatAltitude(m_data.GetPerilune(), m_sb);
                if (orbitType != LunarSpacecraft2D.OrbitType.Circle)
                {
                    m_sb.append(m_format3dot2.format(m_data.GetPeriluneLongitude()));
                    m_sb.append(" DEG");
                }
                g.setColor(GREEN);
            }
            else
            {
                m_sb.append("     --- ---.-- N/A");
                g.setColor(Color.red);
            }
            g.drawString(m_sb.toString(), x, y);
        }

        y += lineHeight * 2;
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

        if (m_ilevel > SIMPLE)
        {
            y += lineHeight;
            m_sb.setLength(0);
            m_sb.append(" A.PILOT: ");
            if (m_data.GetAutopilotOn()) {
                m_sb.append("ON");
            } else {
                m_sb.append("OFF");
            }
            g.setColor(GREEN);
            g.drawString(m_sb.toString(), x, y);

            y += lineHeight;
            m_sb.setLength(0);
            m_sb.append(" PITCH  : ");
            m_sb.append(m_format3dot2.format(m_data.GetPitch()));
            m_sb.append(" DEG   LONG: ");
            m_sb.append(m_format3dot2.format(m_data.GetLongitude()));
            m_sb.append(" DEG");
            g.setColor(GREEN);
            g.drawString(m_sb.toString(), x, y);
        }

        ////////////////////////////////////
        // Draw the attitude/velocity widget
        int scale = 100; // scale to draw the LM at in this panel
        int rad = 120; // radius of the instrument circle
        int rad2 = rad + 10; // radius of a second circle enclosing it
        int offset = rad + 20; // offset of the center of the circle from the last UI element.
        x += offset;
        //y += offset;
        y = 660;
        m_LMpoly.draw(g, x, y, m_data.GetPitch() - 90.0, scale, GREEN, m_data.GetThrottlePercent() * 0.01);

        g.setColor(Color.gray);
        g.drawLine(x-rad, y, x+rad, y);
        g.drawLine(x, y-rad, x, y+rad);
        g.drawOval(x-rad, y-rad, rad*2, rad*2);
        g.drawOval(x-rad2, y-rad2, rad2*2, rad2*2);

        drawVelocityVector(g, x, y, rad2, Color.cyan);

        ////////////////////////////////////
        // Draw the VVM
         int halfwid = 10; // half the width of the VVM
         x += offset + halfwid * 2;
         drawVVM(g, x, y, halfwid, rad2);

        ////////////////////////////////////
        // Draw the longitude widget
        x += offset + halfwid * 2;
        g.setColor(Color.gray);
        g.drawLine(x-rad, y, x+rad, y);
        g.drawLine(x, y-rad, x, y+rad);
        g.drawOval(x-rad, y-rad, rad*2, rad*2);
        g.drawOval(x-rad2, y-rad2, rad2*2, rad2*2);

        drawLongitudeVector(g, x, y, rad2, Color.green);

    }

    public synchronized void CycleInterfaceLevel()
    {
        if (m_ilevel == SIMPLE)
            m_ilevel = NORMAL;
        else if (m_ilevel == NORMAL)
            m_ilevel = EXPERT;
        else if (m_ilevel == EXPERT)
            m_ilevel = SIMPLE;
    }

    protected DecimalFormatSymbols m_symbols;
    protected DecimalFormat m_format2;
    protected DecimalFormat m_format4dot1;
    protected DecimalFormat m_format3percent;
    protected DecimalFormat m_format3dot1percent;
    protected DecimalFormat m_format2dot2;
    protected DecimalFormat m_format3dot2;
    protected DecimalFormat m_format1_3dot2;
    protected DecimalFormat m_format3_3dot2;

    protected Font m_font;

    protected StringBuilder m_sb;

    protected Image m_img;

    protected LMInstrumentData m_data;

    protected LMPolygon m_LMpoly = new LMPolygon();

    protected int m_ilevel = EXPERT; // SIMPLE, NORMAL, or EXPERT
}
