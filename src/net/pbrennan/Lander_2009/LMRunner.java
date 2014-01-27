package net.pbrennan.Lander_2009;

import java.awt.Color;
// 20090413 PMB : removed the code which implements LMRunner as Runnable,
// 				  adapted it as a single-threaded app.

public class LMRunner
{
    private static final int UPDATE_RATE = TestLMRunner2.TARGET_FRAME_RATE; // updates/second
    private static final int MS_PER_UPDATE=1000 / UPDATE_RATE; // milliseconds per update

    // for 1x, 10x, 100x, and 1000x
    private double secondsPerIntegration[] =
    {
        (double)(MS_PER_UPDATE) /  1000.0,
        (double)(MS_PER_UPDATE) /   100.0,
        (double)(MS_PER_UPDATE) /   100.0,
        (double)(MS_PER_UPDATE) /   100.0,
    };
    private int nIntegrations[] =
    {
         1,
         1,
        10,
       100,
    };

    public LMRunner()
    {
        m_perfmon = new PerformanceMonitor();
    }

    public synchronized void setLM(LunarSpacecraft2D lm)
    {
        m_lm = lm;
    }

    public synchronized void setTimeFactor(int timefac)
    {
        if (timefac >= 1000)
            timefac = 1000;
        else if (timefac >= 100)
            timefac = 100;
        else if (timefac >= 10)
            timefac = 10;
        else
            timefac = 1;
        m_timefactor = timefac;
    }

    public synchronized void setPaused(boolean paused)
    {
        if (m_paused == true)
        {
            if (paused == false)
            {
                m_paused = false;
            }
        }
        m_paused = paused;
    }

    // fill an instrument data object with information.
    public synchronized LMInstrumentData getInstrumentData(LMInstrumentData data)
    {
        data.SetTime(m_lm.GetTime());

        double fuel = m_lm.GetFuelMass();
        data.SetFuel(fuel);

        double fuelpercent = m_lm.GetPercentFuel();
        data.SetFuelPercent(fuelpercent);

        double deltav = m_lm.GetDeltaV();
        data.SetDeltaV(deltav);

        double throttlepercent = m_lm.GetThrottlePercent();
        data.SetThrottlePercent(throttlepercent);

        double flowrate = m_lm.GetMDot();
        data.SetFlowRate(flowrate);

        double vspeed = m_lm.GetVerticalVelocity();
        data.SetVSpeed(vspeed);
        if (Math.abs(vspeed) > LanderUtils.MAX_SAFE_VERTICAL_VELOCITY)
        {
            data.SetVSpeedColor(Color.YELLOW);
        }
        else
        {
            data.SetVSpeedColor(Color.GREEN);
        }

        double hspeed = m_lm.GetHorizontalVelocity();
        data.SetHSpeed(hspeed);
        if (Math.abs(hspeed) > LanderUtils.MAX_SAFE_HORIZONTAL_VELOCITY)
        {
            data.SetHSpeedColor(Color.YELLOW);
        }
        else
        {
            data.SetHSpeedColor(Color.GREEN);
        }

        double dtmalt = m_lm.GetNominalAltitude();
        data.SetDatumAltitude(dtmalt);

        data.SetTimeFactor(m_timefactor);

        data.SetPitch(m_lm.GetPitchAngle() * 180.0 / 3.14159265);

        double apolune = Math.max(0.0, m_lm.GetApolune() - LanderUtils.MoonRadius);
        data.SetApolune(apolune);

        double perilune = Math.max(0.0, m_lm.GetPerilune() - LanderUtils.MoonRadius);
        data.SetPerilune(perilune);

        LunarSpacecraft2D.OrbitType orbitType = m_lm.GetOrbitType();
        data.SetOrbitType(orbitType);
        switch (orbitType)
        {
            case None:
            case DegenerateConic:
                data.SetApoluneOK(false);
                data.SetPeriluneOK(false);
                break;

            case Circle:
            case Ellipse:
                data.SetApoluneOK(true);
                data.SetPeriluneOK(perilune > 0.0);
                break;

            case Parabola:
            case Hyperbola:
                data.SetApoluneOK(false);
                data.SetPeriluneOK(perilune > 0.0);
                break;
        }
        data.SetEccentricity(m_lm.GetEccentricity());

        HVecN stateVec = m_lm.GetState();
        data.SetHeading(Math.toDegrees(stateVec.vec[2]));

        double longitude = m_lm.GetTheta();
        data.SetLongitude(Math.toDegrees(longitude));

        double perilune_long = m_lm.GetPeriluneLongitude();
        data.SetPeriluneLongitude(Math.toDegrees(perilune_long));

        double apolune_long = m_lm.GetApoluneLongitude();
        data.SetApoluneLongitude(Math.toDegrees(apolune_long));

        data.SetVVM(m_lm.GetVVM());
        data.SetHVM(m_lm.GetHVM());

        data.SetTPS(m_perfmon.getEventsPerSecond());
        data.SetSleepTime(m_perfmon.getSleepTime());

        data.SetRadarAltitude(m_lm.GetTerrainAltitude());

        data.SetAutopilotOn(m_lm.GetAutopilotOn());
        data.SetAutopilotModeString(m_lm.GetAutopilotModeString());

        data.SetTargetSelected(m_lm.GetTargetSelected());
        data.SetTargetLongitude(m_lm.GetTargetLong());        
        data.SetTargetAltitude(m_lm.GetTargetRadius());
        data.SetDistanceToTarget(m_lm.GetDistanceToTarget());
        data.SetDOISet(m_lm.GetDOISet());
        data.SetDOILongitude(m_lm.GetDOILong());
        data.SetDOIRadius(m_lm.GetDOIRadius());
        data.SetPDISet(m_lm.GetPDISet());
        data.SetPDILongitude(m_lm.GetPDILong());
        data.SetPDIRadius(m_lm.GetPDIRadius());
        
        data.SetRotMode(m_lm.GetRCSRotMode());
        
        // TODO: Add whatever other information we want to go into the 
        // data packet.

        return data;
    }

    public synchronized void updateModel()
    {
        // Get seconds per integration : see secondsPerIntegration
        double h;

        if (m_timefactor == 1)
            h = secondsPerIntegration[0];
        else if (m_timefactor == 10)
            h = secondsPerIntegration[1];
        else if (m_timefactor == 100)
            h = secondsPerIntegration[2];
        else
            h = secondsPerIntegration[3];

        // Get number of integrations.
        int n;

        if (m_timefactor == 1)
            n = nIntegrations[0];
        else if (m_timefactor == 10)
            n = nIntegrations[1];
        else if (m_timefactor == 100)
            n = nIntegrations[2];
        else
            n = nIntegrations[3];

        //System.out.println("updateModel: Performing integrations...");
        
        // do integration(s)
        for (int i=0 ; i<n ; ++i)
        {
            m_lm.step(h);
        }

        // predict orbit
        //System.out.println("LMRunner.updateModel: Predicting orbital parameters...");
        m_lm.PredictOrbitalParameters();

        // transform into polar frame
        //System.out.println("LMRunner.updateModel: Computing polar frame...");
        m_lm.ComputePolarFrame();
    }

    public synchronized void tick(double seconds)
    {
	if (!m_paused)
	{
	    updateModel();
	}

	//System.out.println("LMRunner.tick: done with updateModel(), putting event...");
	m_perfmon.putEvent();
    }

    protected LunarSpacecraft2D     m_lm;
    protected int                   m_timefactor;
    protected boolean               m_paused;
    protected PerformanceMonitor    m_perfmon;


}