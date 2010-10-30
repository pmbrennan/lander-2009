package net.pbrennan.Lander_2009;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;

class EventDisplay implements ILMEventListener
{
    /* width of the event display window in pixels. */
    private int m_width;
    private int m_height;
    private int m_cornerRadius = 5;
    private int m_displayWidthPixels;
    private Color m_borderColor = Color.green;
    private Font m_font;
    private ArrayList<String> m_buffer;

    EventDisplay(int width, int height, Font font)
    {
        m_width = width;
        m_height = height;
        m_font = font;
        m_displayWidthPixels = m_width - 2 * m_cornerRadius;
        if (m_displayWidthPixels < 0)
            m_displayWidthPixels = 0;
        m_buffer = new ArrayList<String>();

        LMEventSource.getInstance().addListener(this);
    }

    @Override
    public void listen(LMEvent e)
    {
        String[] s;

        if ((LMLandingEvent)e != null)
        {
            s = ((LMLandingEvent)e).toString().split("\n");

            for (int i=0; i<s.length; ++i)
                m_buffer.add(s[i]);
        }
    }

    /***
     * formatTextLines
     *
     * @param g
     * @return a boolean indicating whether the buffer
     * was changed.
     */
    public boolean formatTextLines(Graphics2D g)
    {
        FontMetrics fm = g.getFontMetrics();
        String s;

        // TODO: Only do this for that part of the buffer
        // that hasn't already been processed.
        for (int i=0; i<m_buffer.size(); ++i)
        {
            s = m_buffer.get(i);
            if (fm.stringWidth(s) > m_displayWidthPixels)
            {
                // The string's too long.  Let's break it apart.
                // Perform a binary search to find the appropriate
                // place to split the string.
                m_buffer.remove(i);
                int lowIdx = 0;
                int highIdx = s.length() - 1;
                int splitIdx = s.length() / 2;

                while ((highIdx - lowIdx) >  1)
                {
                    if (fm.stringWidth(s.substring(0, splitIdx)) < m_displayWidthPixels)
                    {
                        lowIdx = splitIdx;
                        splitIdx = (highIdx + lowIdx) / 2;
                    }
                    else
                    {
                        highIdx = splitIdx;
                        splitIdx = (highIdx + lowIdx) / 2;
                    }
                }

                // Off by one.
                lowIdx -= 1;
                highIdx -= 1;

                // Grab whole words if possible.
                while ((splitIdx >= 0)&&(s.charAt(splitIdx) != ' '))
                {
                    --splitIdx;
                }

                if (splitIdx > 0)
                {
                    lowIdx = splitIdx;
                    highIdx = splitIdx + 1;
                }

                m_buffer.add(i,s.substring(0, lowIdx+1));
                m_buffer.add(i+1,s.substring(highIdx,s.length()));
            }
        }

        return false;
    }

    public void draw(Graphics2D g, int x, int y)
    {
        g.setFont(m_font);
        g.setColor(m_borderColor);

        formatTextLines(g);

        FontMetrics fm = g.getFontMetrics();
        int lineHeight = fm.getHeight() + 5;
        int nLinesToDisplay = (m_height - 2 * m_cornerRadius) / lineHeight;

        int drawX = x + m_cornerRadius;
        int drawY = y + m_cornerRadius + fm.getAscent() + fm.getDescent();

        boolean drawLoopComplete = false;
        while (!drawLoopComplete)
        {
            int startIdx = m_buffer.size() - nLinesToDisplay;
            if (startIdx < 0)
                startIdx = 0;

            drawLoopComplete = true;  // We hope!
            for (int linenumber = startIdx ; linenumber < m_buffer.size(); ++linenumber)
            {
                String s = m_buffer.get(linenumber);
                g.drawString(s, drawX, drawY);
                drawY += lineHeight;
            }
        }
        g.drawRoundRect(x, y, m_width, m_height, m_cornerRadius, m_cornerRadius);
    }

    public void clear()
    {
        m_buffer.clear();
    }

    public void put(String s)
    {
        m_buffer.add(s);
        m_buffer.add("\n");
    }
}

