package net.pbrennan.Lander_2009;

// IDerivableVec.java

/**
 * Interface for a vector for which the time derivative can be computed.
 */
public interface IDerivableVec
{
    // Evaluate the function derivative at time t+h
    public HVecN deriv(double t, double h, HVecN f, HVecN rv);
}