package net.pbrennan.Lander_2009;
/**
  * Provides a static method to calculate the 4th order
  * Runge-Kutta integration calculation with a vector-valued
  * function.
  * See http://en.wikipedia.org/wiki/Runge-Kutta_methods#The_common_fourth-order_Runge.E2.80.93Kutta_method
 **/
public class RungeKutta4thVecN
{
  RungeKutta4thVecN(int size)
  {
      // Allocate working storage, so we're not doing it
      // at every time step
      // (therefore avoiding lots of churn on the heap!)
      k1 = new HVecN(size);
      k2 = new HVecN(size);
      k3 = new HVecN(size);
      k4 = new HVecN(size);

      OneHalfHK1 = new HVecN(size);
      OneHalfHK2 = new HVecN(size);
      HK3 = new HVecN(size);

      YPlusOffset = new HVecN(size);
  }

  // Given y=f(t), compute f(t+h)
  public void step (double t,    // Time
                    double h,    // Time Step
                    HVecN y,     // Value of f(t)
                    IDerivableVec func ) // function
    {
        double halfH = 0.5 * h;
        k1 = func.deriv(t, 0.0, y, k1);

        OneHalfHK1.copy(k1);
        OneHalfHK1.scale(halfH);
        YPlusOffset.copy(y);
        YPlusOffset.sum(OneHalfHK1);
        //k2 = func.deriv(t, halfH, y.add(OneHalfHK1), k2);
        k2 = func.deriv(t, halfH, YPlusOffset, k2);

        OneHalfHK2.copy(k2);
        OneHalfHK2.scale(halfH);
        YPlusOffset.copy(y);
        YPlusOffset.sum(OneHalfHK2);
        //k3 = func.deriv(t, halfH, y.add(OneHalfHK2), k3);
        k3 = func.deriv(t, halfH, YPlusOffset, k3);

        HK3.copy(k3);
        HK3.scale(h);
        YPlusOffset.copy(y);
        YPlusOffset.sum(HK3);
        //k4 = func.deriv(t, h, y.add(HK3), k4);
        k4 = func.deriv(t, h, YPlusOffset, k4);

        // compute (1/6)h * (k1 + 2*k2 + 2*k3 + k4)
        int size = y.size;
        double oneSixthH = h / 6.0;

        double nextY;
        for (int i=0 ; i<size ; ++i)
        {
          nextY = y.vec[i]
            + (k1.vec[i]
            + 2.0 * (k2.vec[i] + k3.vec[i])
            + k4.vec[i])
            * oneSixthH;
          y.vec[i] = nextY;
        }
    }

    // Values of the derivative at four key sampling intervals.
    private HVecN k1;
    private HVecN k2;
    private HVecN k3;
    private HVecN k4;

    // trial offsets of the function value.
    private HVecN OneHalfHK1;
    private HVecN OneHalfHK2;
    private HVecN HK3;

    // trial functionvalue
    private HVecN YPlusOffset;
} // RungeKutta4thVecN