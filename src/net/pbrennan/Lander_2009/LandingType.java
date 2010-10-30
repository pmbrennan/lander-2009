package net.pbrennan.Lander_2009;
public enum LandingType
{    
    NULL(0.0, 0.0, 0.0, "Null", "Null"),

    EXCELLENT_LANDING(1.0, 0.5, 6.0,
        "Great landing!",
        "Neil Armstrong himself couldn't have done better!"),

    GOOD_LANDING(3.0, 1.0, 8.0,
        "Good landing",
        "The President sends his congratulations!"),

    HARD_LANDING(5.0, 2.0, 15.0,
        "Hard landing",
        "The LM sustained some minor damage, but you made it."),

    CRASH_LANDING(10.0, 4.0, 20.0,
        "Crash Landing",
        "You are stranded on the moon.  Consider taking a nice walk outside without your helmet."),

    CRASH(-1.0, -1.0, -1.0,
        "Crash",
        "You just created a new crater on the moon!");

    private final String m_name;  // short name of the landing type
    private final String m_desc;  // longer description of the landing type
    private final double m_maxVV; // max vertical velocity, m/s
    private final double m_maxHV; // max horizontal velocity, m/s
    private final double m_maxDevFromVertical; // max deviation from vertical, degrees

    LandingType(double maxVV, double maxHV, double maxDevFromVertical, String name, String desc)
    {
        m_name = name;
        m_desc = desc;
        m_maxVV = maxVV;
        m_maxHV = maxHV;
        m_maxDevFromVertical = maxDevFromVertical;
    }

    public String getName() { return m_name; }
    public String getDesc() { return m_desc; }
    public double getMaxVV() { return m_maxVV; }
    public double getMaxHV() { return m_maxHV; }
    public double getMaxDevFromVertical() { return m_maxDevFromVertical; }

    public static LandingType getLandingType(double VVelocity, double HVelocity, double PitchDegrees)
    {
        double VV = Math.abs(VVelocity);
        double HV = Math.abs(HVelocity);
        double DV = Math.abs(90.0 - PitchDegrees);

        LandingType types[] = { EXCELLENT_LANDING, GOOD_LANDING, HARD_LANDING, CRASH_LANDING };

        for (int i=0 ; i<types.length ; ++i)
        {
            if (VV > types[i].getMaxVV())
                continue;
            if (HV > types[i].getMaxHV())
                continue;
            if (DV > types[i].getMaxDevFromVertical())
                continue;

            return types[i];
        }
        return CRASH;
    }

    public String toString()
    {
        return getName() + ": " + getDesc() ;
    }
}