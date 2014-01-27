package net.pbrennan.Lander_2009;
// Mission.java

public class Mission
{
    public boolean missionOK;

    public String missionName;
    public String missionDescription;

    public double targetLongitude;  // in degrees: where to land
    public int    DOISpecified;     // 0=no DOI specified 1=DOI specified
    public double DOILongitude;     // in degrees: where to begin descent orbit (recommended)
    public double DOIAltitude;      // in meters: where to begin descent orbit (recommended)
    public int    PDISpecified;     // 0=no PDI specified 1=PDI specified
    public double PDILongitude;     // in degrees: where to begin powered descent (recommended)
    public double PDIAltitude;      // in meters: where to begin powered descent (recommended)

    // State of the spacecraft at the start of the mission
    public int    status;         // 0=landed 1=inflight
    public double x;
    public double y;
    public double a;              // angle in degrees
    public double vx;
    public double vy;
    public double va;             // degrees/second

    public String shipName;

    public double dryMass;
    public double fuelMass;
    public double fuelCapacity;
    public double maxMDot;        // maximum fuel flow rate
    public double Ve;             // exhaust velocity
    public double landingAlt;     // landing altitude

    public Mission(String inMissionName,
                   String inMissionDescription,
                   double inTargetLongitude,
                   int    inDOISpecified,
                   double inDOILongitude,
                   double inDOIAltitude,
                   int    inPDISpecified,
                   double inPDILongitude,
                   double inPDIAltitude,
                   int    inStatus,
                   double inX,
                   double inY,
                   double inA,
                   double inVx,
                   double inVy,
                   double inVa,
                   String inShipName,
                   double inDryMass,
                   double inFuelMass,
                   double inFuelCapacity,
                   double inMaxMDot,
                   double inVe,
                   double inLandingAlt)
    {
        missionOK          = true;
        missionName        = inMissionName;
        missionDescription = inMissionDescription;
        targetLongitude    = inTargetLongitude;
        DOISpecified       = inDOISpecified;
        DOILongitude       = inDOILongitude;
        DOIAltitude        = inDOIAltitude;
        PDISpecified       = inPDISpecified;
        PDILongitude       = inPDILongitude;
        PDIAltitude        = inPDIAltitude;
        status             = inStatus;
        x                  = inX;
        y                  = inY;
        a                  = inA;
        vx                 = inVx;
        vy                 = inVy;
        va                 = inVa;
        shipName           = inShipName;
        dryMass            = inDryMass;
        fuelMass           = inFuelMass;
        fuelCapacity       = inFuelCapacity;
        maxMDot            = inMaxMDot;
        Ve                 = inVe;
        landingAlt         = inLandingAlt;
    }

    public Mission(String[] p)
    {
        missionOK = false;
        if (p.length != 23)
            return;

        try
        {
            missionName         = p[0];
            missionDescription  = p[1];
            targetLongitude     = Double.parseDouble(p[2]);
            DOISpecified        = Integer.parseInt(p[3]);
            DOILongitude        = Double.parseDouble(p[4]);
            DOIAltitude         = Double.parseDouble(p[5]);
            PDISpecified        = Integer.parseInt(p[6]);
            PDILongitude        = Double.parseDouble(p[7]);
            PDIAltitude         = Double.parseDouble(p[8]);
            status              = Integer.parseInt(p[9]);
            x                   = Double.parseDouble(p[10]);
            y                   = Double.parseDouble(p[11]);
            a                   = Double.parseDouble(p[12]);
            vx                  = Double.parseDouble(p[13]);
            vy                  = Double.parseDouble(p[14]);
            va                  = Double.parseDouble(p[15]);
            shipName            = p[16];
            dryMass             = Double.parseDouble(p[17]);
            fuelMass            = Double.parseDouble(p[18]);
            fuelCapacity        = Double.parseDouble(p[19]);
            maxMDot             = Double.parseDouble(p[20]);
            Ve                  = Double.parseDouble(p[21]);
            landingAlt          = Double.parseDouble(p[22]);
        }
        catch (Exception e)
        {
            return;
        }

        missionOK = true;
    }

    public String toString()
    {
        String rv =   "Mission Name :        " + missionName +
                    "\nMission Description : \n" + missionDescription +
                    "\nTarget Long, deg    : " + targetLongitude +
                    "\nDOI Longitude, deg  : " + DOILongitude +
                    "\nPDI Longitude, deg  : " + PDILongitude;

        return rv;

    }
}