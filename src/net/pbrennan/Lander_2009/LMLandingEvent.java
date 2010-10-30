package net.pbrennan.Lander_2009;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class LMLandingEvent extends LMEvent
{
    private static DecimalFormat decFmt;
    
    LMLandingEvent(LandingType inType, double vSpeed, double hSpeed, double inDistance, String inmessage)
    {
        type = inType;
        horizontalSpeed = hSpeed;
        verticalSpeed = vSpeed;
        targetDistance = inDistance;
        message = inmessage;
    }

    public LandingType type;
    public double verticalSpeed;
    public double horizontalSpeed;
    public double targetDistance;
    public String message;
    
    public String toString()
    {
        if (decFmt == null)
        {
            decFmt = (DecimalFormat) NumberFormat.getInstance();
            if (decFmt == null)
                return "";
            
            decFmt.setMaximumFractionDigits(3);
        }
        
        String outmessage;
        
        if (message != "")
            outmessage = message + "\n";
        else
            outmessage = "";
        
        outmessage  += type.toString() +  "\nVert. speed : " + decFmt.format(verticalSpeed)
                       + " m/s\nHorz. speed : " + decFmt.format(horizontalSpeed)
                       + " m/s\nDist. to target: " + decFmt.format(targetDistance) + " m";
        return outmessage;
    }
}