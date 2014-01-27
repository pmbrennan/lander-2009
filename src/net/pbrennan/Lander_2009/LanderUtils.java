package net.pbrennan.Lander_2009;
public class LanderUtils
{
    public static final double MoonRadius = 1737400; // m
    public static final double PI2 = Math.PI * 2;
    public static final double HALF_PI = Math.PI * 0.5;
    
    // TODO: Should this be a property of the spacecraft?
    public static final double MAX_SAFE_VERTICAL_VELOCITY = 5.0;
    public static final double MAX_SAFE_HORIZONTAL_VELOCITY = 2.0;

    // put an angle into the range 0 - 2PI.
    public static double normAngleRadians(double rad)
    {
        while (rad > PI2)
        {
            rad -= PI2;
        }
        while (rad < 0.0)
        {
            rad += PI2;
        }
        return rad;
    }

    // Given two angles, return the absolute difference between
    // them, taking into account the fact that their numerical value
    // may be offset by extra turns, i.e. the difference
    // between 359 degrees and 1 degree is actually 2 degrees.
    public static double absAngleDifference(double angle1Radians,
                                            double angle2Radians)
    {
        double diff = angle1Radians - angle2Radians;
        while (diff > Math.PI)
            diff -= PI2;
        while (diff < -Math.PI)
            diff += PI2;

        return Math.abs(diff);
    }
    
    public static double diff180(double angle1Radians,
            double angle2Radians)
    {
        double diff = angle1Radians - angle2Radians;
        while (diff > Math.PI)
            diff -= PI2;
        while (diff < -Math.PI)
            diff += PI2;
        
        return (diff);
    }

}
