package net.pbrennan.Lander_2009;

import java.awt.Color;
import java.awt.Graphics;

public class LanderParticles 
{
    
    private static final double G = 1.628; // m/s^2, lunar gravity.
    private static final double halfG = G * 0.5;
    
    // The positions of the particles.
    private double[] x;
    private double[] y;     // positive means down, negative means up
    
    // The velocities of the particles.
    private double[] vx;
    private double[] vy;
    
    // The lifetimes of the particles.
    // this is a countdown clock.
    private double[] life;
    
    LanderParticles(int Nparticles)
    {
        x = new double [Nparticles];
        y = new double [Nparticles];
        vx = new double [Nparticles];
        vy = new double [Nparticles];
        life = new double [Nparticles];
        
        for (int i=0; i<Nparticles; ++i)
            life[i] = 0;
    }
    
    public void spawn(
                      double x0, double y0,
                      double maxRadius,
                      double vx0, double vy0,
                      double maxRelativeV,
                      double maxLifetime
                      )
    {
        int Nparticles = x.length;
        for (int i=0; i<Nparticles; ++i)
        {
            double angle = Math.random() * LanderUtils.PI2;
            double r = Math.random() * maxRadius;
            x[i] = x0 + r * Math.cos(angle);
            y[i] = y0 + r * Math.sin(angle);
            
            double v = Math.random() * maxRelativeV;
            vx[i] = vx0 + v * Math.cos(angle);
            vy[i] = vy0 + v * Math.sin(angle);
            
            life[i] = Math.random() * maxLifetime;
        }
        
        // Sort the lifetime list in ascending order.
        java.util.Arrays.sort(life);
    }
    
    /***
     * Just like the spawn() method, except this one will create particles
     * only in the upper half of the circle, with velocities guaranteed to be
     * in the up direction.
     * 
     * @param x0
     * @param y0
     * @param maxRadius
     * @param vx0
     * @param vy0
     * @param maxRelativeV
     * @param maxLifetime
     */
    public void spawnVerticalBias(
            double x0, double y0,
            double maxRadius,
            double vx0, double vy0,
            double maxRelativeV,
            double maxLifetime
            )
    {
        int Nparticles = x.length;
        
        for (int i=0; i<Nparticles; ++i)
        {
            double angle = Math.random() * Math.PI;
            double r = Math.random() * maxRadius;
            x[i] = x0 + r * Math.cos(angle);
            y[i] = y0 - r * Math.sin(angle);
            
            double v = Math.random() * maxRelativeV;
            vx[i] = vx0 + v * Math.cos(angle);
            vy[i] = vy0 - v * Math.sin(angle);
            
            life[i] = Math.random() * maxLifetime;
        }
        
        // Sort the lifetime list in ascending order.
        java.util.Arrays.sort(life);
    }
    
    /***
     * Just like the spawnVerticalBias() method, except this one will allow
     * the caller to specify a central angle.
     * 
     * @param centralAngle
     * @param x0
     * @param y0
     * @param maxRadius
     * @param vx0
     * @param vy0
     * @param maxRelativeV
     * @param maxLifetime
     */
    public void spawnWCentralAngle(
            double centralAngle,
            double x0, double y0,
            double maxRadius,
            double vx0, double vy0,
            double maxRelativeV,
            double maxLifetime
            )
    {
        System.out.println("central angle = " + centralAngle + "(" + centralAngle / Math.PI * 180.0 + " deg)");
        
        int Nparticles = x.length;
        
        for (int i=0; i<Nparticles; ++i)
        {
            double angle = Math.random() * Math.PI + centralAngle - Math.PI * 0.5;
            double r = Math.random() * maxRadius;
            x[i] = x0 + r * Math.cos(angle);
            y[i] = y0 - r * Math.sin(angle);
            
            double v = Math.random() * maxRelativeV;
            vx[i] = vx0 + v * Math.cos(angle);
            vy[i] = vy0 - v * Math.sin(angle);
            
            life[i] = Math.random() * maxLifetime;
        }
        
        // Sort the lifetime list in ascending order.
        java.util.Arrays.sort(life);
    }
    
    public void die()
    {
        life[life.length - 1] = 0.0;
    }

    // Advance the life of the particles by t
    // seconds.
    public void step(double t)
    {
        if (x == null)
            return;
        
        int Nparticles = x.length;
        if (life[Nparticles-1] <= 0.0)
            return;
        
        // precompute the amounts by which
        // to add velocity and distance in y due to gravity.
        double yIncrement = halfG * t;
        double vyIncrement = G * t;        
        
        for (int i=Nparticles-1 ; i>=0 ; --i)
        {
            if (life[i] <= 0.0)
                break;
            
            life[i] -= t;
            
            x[i] += vx[i] * t;
            y[i] += (vy[i] + yIncrement) * t;
            vy[i] += vyIncrement;
        }        
    }
    
    public void draw(Graphics g,
            int centerX, int centerY,
            double scale, 
            double stepTime,
            Color color)
    {
        if (life[life.length-1] <= 0.0)
            return;
        
        step(stepTime);
        g.setColor(color);
        
        for (int i=x.length-1; i>=0; --i)
        {
            if (life[i] <=0)
                break;
            int dx = centerX + (int)(x[i] * scale);
            int dy = centerY + (int)(y[i] * scale);
            g.drawOval(dx, dy, 1, 1);
        }            
    }
}
