package net.pbrennan.Lander_2009;
// A property bag for the exchange of instrument data.

import java.awt.Color;
import java.util.ArrayList;

import net.pbrennan.Lander_2009.LunarSpacecraft2D.RCSRotMode;

public class LMInstrumentData
{
    public LMInstrumentData()
    {
        m_vspeedcolor = Color.green;
        m_hspeedcolor = Color.green;
    }

    public double GetFuel()
    {
        return m_fuel;
    }

    public void SetFuel (double in_Fuel)
    {
        m_fuel = in_Fuel;
    }

    public double GetFuelPercent()
    {
        return m_fuelpercent;
    }

    public void SetFuelPercent (double in_FuelPercent)
    {
        m_fuelpercent = in_FuelPercent;
    }

    public double GetDeltaV()
    {
        return m_deltav;
    }

    public void SetDeltaV (double in_DeltaV)
    {
        m_deltav = in_DeltaV;
    }

    public double GetThrottlePercent()
    {
        return m_throttlepercent;
    }

    public void SetThrottlePercent (double in_ThrottlePercent)
    {
        m_throttlepercent = in_ThrottlePercent;
    }

    public double GetFlowRate()
    {
        return m_flowrate;
    }

    public void SetFlowRate (double in_FlowRate)
    {
        m_flowrate = in_FlowRate;
    }

    public double GetVSpeed()
    {
        return m_vspeed;
    }

    public void SetVSpeed (double in_VSpeed)
    {
        m_vspeed = in_VSpeed;
    }

    public double GetHSpeed()
    {
        return m_hspeed;
    }

    public void SetHSpeed (double in_HSpeed)
    {
        m_hspeed = in_HSpeed;
    }

    public double GetDatumAltitude()
    {
        return m_datum_altitude;
    }

    public void SetDatumAltitude (double in_DatumAltitude)
    {
        m_datum_altitude = in_DatumAltitude;
    }

    public double GetRadarAltitude()
    {
        return m_radar_altitude;
    }

    public void SetRadarAltitude (double in_RadarAltitude)
    {
        m_radar_altitude = in_RadarAltitude;
    }

    public boolean GetRadarOn()
    {
        return m_radar_on;
    }

    public void SetRadarOn (boolean in_RadarOn)
    {
        m_radar_on = in_RadarOn;
    }

    public double GetApolune()
    {
        return m_apolune;
    }

    public void SetApolune (double in_Apolune)
    {
        m_apolune = in_Apolune;
    }

    public boolean GetApoluneOK()
    {
        return m_apolune_ok;
    }

    public void SetApoluneOK (boolean in_ApoluneOK)
    {
        m_apolune_ok = in_ApoluneOK;
    }

    public double GetPerilune()
    {
        return m_perilune;
    }

    public void SetPerilune (double in_Perilune)
    {
        m_perilune = in_Perilune;
    }

    public boolean GetPeriluneOK()
    {
        return m_perilune_ok;
    }

    public void SetPeriluneOK (boolean in_PeriluneOK)
    {
        m_perilune_ok = in_PeriluneOK;
    }

    public int GetTimeFactor()
    {
        return m_timefactor;
    }

    public void SetTimeFactor (int in_TimeFactor)
    {
        m_timefactor = in_TimeFactor;
    }

    public boolean GetAutopilotOn()
    {
        return m_autopilot_on;
    }

    public void SetAutopilotOn (boolean in_AutpilotOn)
    {
        m_autopilot_on = in_AutpilotOn;
    }
    
    public String GetAutopilotModeString()
    {
        return m_autopilot_mode_string;
    }
    
    public  void SetAutopilotModeString(String in_mode_string)
    {
        m_autopilot_mode_string = in_mode_string;
    }

    public Color GetVSpeedColor()
    {
        return m_vspeedcolor;
    }

    public void SetVSpeedColor (Color in_VSpeedColor)
    {
        m_vspeedcolor = in_VSpeedColor;
    }

    public Color GetHSpeedColor()
    {
        return m_hspeedcolor;
    }

    public void SetHSpeedColor (Color in_HSpeedColor)
    {
        m_hspeedcolor = in_HSpeedColor;
    }

    public double GetTime()
    {
        return m_time;
    }

    public void SetTime (double in_Time)
    {
        m_time = in_Time;
    }

    public double GetPitch()
    {
        return m_pitch;
    }

    public void SetPitch (double in_Pitch)
    {
        m_pitch = in_Pitch;
    }

    public double GetLongitude()
    {
        return m_longitude;
    }

    public void SetLongitude (double inLongitude)
    {
        m_longitude = inLongitude;
    }

    public double GetEccentricity()
    {
        return m_eccentricity;
    }

    public void SetEccentricity (double in_Eccentricity)
    {
        m_eccentricity = in_Eccentricity;
    }

    public LunarSpacecraft2D.OrbitType GetOrbitType()
    {
        return m_orbittype;
    }

    public void SetOrbitType (LunarSpacecraft2D.OrbitType in_OrbitType)
    {
        m_orbittype = in_OrbitType;
    }


    public double GetApoluneLongitude()
    {
        return m_apolune_longitude;
    }

    public void SetApoluneLongitude (double in_ApoluneLongitude)
    {
        m_apolune_longitude = in_ApoluneLongitude;
    }


    public double GetPeriluneLongitude()
    {
        return m_perilune_longitude;
    }

    public void SetPeriluneLongitude (double in_PeriluneLongitude)
    {
        m_perilune_longitude = in_PeriluneLongitude;
    }

    public double GetHeading()
    {
        return m_heading;
    }

    public void SetHeading (double in_Heading)
    {
        m_heading = in_Heading;
    }

    public double GetVVM()
    {
        return m_VVM;
    }

    public void SetVVM (double in_VVM)
    {
        m_VVM = in_VVM;
    }

    public void SetHVM(double in_HVM)
    {
        m_HVM = in_HVM;
    }

    public double GetHVM()
    {
        return m_HVM;
    }

    public boolean GetTargetSelected()
    {
        return m_target_set;
    }

    public void SetTargetSelected (boolean in_TargetSelected)
    {
        m_target_set = in_TargetSelected;
    }

    public double GetTargetLongitude()
    {
        return m_target_longitude;
    }

    public void SetTargetLongitude (double in_TargetLongitude)
    {
        m_target_longitude = in_TargetLongitude;
    }

    public double GetTargetRadius()
    {
        return m_target_altitude;
    }

    public void SetTargetAltitude (double in_TargetAltitude)
    {
        m_target_altitude = in_TargetAltitude;
    }

    public double GetDistanceToTarget()
    {
        return m_distance_to_target;
    }

    public void SetDistanceToTarget (double in_DistanceToTarget)
    {
        m_distance_to_target = in_DistanceToTarget;
    }

    public double GetDOILongitude()
    {
    	return m_DOI_longitude;
    }

    public boolean GetDOISet() 
    {
        return m_DOI_set;
    }
    
    public double GetDOIRadius()
    {
        return m_DOI_radius;
    }
    
    public void SetDOISet(boolean in_DOISet)
    {
        m_DOI_set = in_DOISet;
    }
    
    public void SetDOILongitude(double in_DOILongitude)
    {
    	m_DOI_longitude = in_DOILongitude;
    }
    
    public void SetDOIRadius(double in_DOIRadius)
    {
        m_DOI_radius = in_DOIRadius;
    }

    public double GetPDILongitude()
    {
    	return m_PDI_longitude;
    }

    public boolean GetPDISet() 
    {
        return m_PDI_set;
    }
    
    public double GetPDIRadius()
    {
        return m_PDI_radius;
    }

    public void SetPDILongitude(double in_PDILongitude)
    {
    	m_PDI_longitude = in_PDILongitude;
    }
    
    public void SetPDISet(boolean in_PDISet)
    {
        m_PDI_set = in_PDISet;
    }
    
    public void SetPDIRadius(double in_PDIRadius)
    {
        m_PDI_radius = in_PDIRadius;
    }

    public double GetTPS()
    {
        return m_TPS;
    }

    public void SetTPS (double in_TPS)
    {
        m_TPS = in_TPS;
    }

    public long GetSleepTime()
    {
        return m_sleepTime;
    }

    public void SetSleepTime (long in_SleepTime)
    {
        m_sleepTime = in_SleepTime;
    }

    public RCSRotMode GetRotMode() {
        return m_RCS_RotMode;
    }

    public void SetRotMode(RCSRotMode inRotMode) {
        m_RCS_RotMode = inRotMode;
    }

    private double    m_fuel;                     // kg
    private double    m_fuelpercent;              // 0-100
    private double    m_deltav;                   // m/s
    private double    m_throttlepercent;          // 0-100
    private double    m_flowrate;                 // kg/s
    private double    m_vspeed;                   // m/s
    private double    m_hspeed;                   // m/s
    private double    m_datum_altitude;           // m
    private double    m_radar_altitude;           // m
    private boolean   m_radar_on;                 // true/false
    private double    m_apolune;                  // m
    private boolean   m_apolune_ok;               // true/false
    private double    m_perilune;                 // m
    private boolean   m_perilune_ok;              // true/false
    private double    m_eccentricity;             // shape of the orbit
    private int       m_timefactor;
    private boolean   m_autopilot_on;
    private String    m_autopilot_mode_string;
    private Color     m_vspeedcolor;              // VSpeedColor
    private Color     m_hspeedcolor;              // HSpeedColor
    private double    m_time;                     // Time

    private long      m_sleepTime;                // sleep time in LMRunner thread
    private double    m_TPS;                      // Ticks/second performance

    private double    m_heading;                  // Heading, degrees
    private double    m_pitch;                    // Pitch, degrees
    private double    m_longitude;                // Longitude, degrees

    private double    m_apolune_longitude;        // ApoluneLongitude
    private double    m_perilune_longitude;       // PeriluneLongitude
    private double    m_VVM;                      // VVM
    private double    m_HVM;                      // HVM

    private boolean   m_target_set;
    private double    m_target_longitude;
    private double    m_target_altitude;
    private double    m_distance_to_target;
    private boolean   m_DOI_set;
    private double    m_DOI_longitude;
    private double    m_DOI_radius;
    private boolean   m_PDI_set;
    private double	  m_PDI_longitude;
    private double    m_PDI_radius;

    private LunarSpacecraft2D.
    RCSRotMode
                      m_RCS_RotMode;

    private
    LunarSpacecraft2D.
    OrbitType
                        m_orbittype;                // OrbitType

    public void copy(LMInstrumentData other)
    {
        m_fuel = other.m_fuel;
        m_fuelpercent = other.m_fuelpercent;
        m_deltav = other.m_deltav;
        m_throttlepercent = other.m_throttlepercent;
        m_flowrate = other.m_flowrate;
        m_vspeed = other.m_vspeed;
        m_hspeed = other.m_hspeed;
        m_datum_altitude = other.m_datum_altitude;
        m_radar_altitude = other.m_radar_altitude;
        m_radar_on = other.m_radar_on;
        m_apolune = other.m_apolune;
        m_apolune_ok = other.m_apolune_ok;
        m_perilune = other.m_perilune;
        m_perilune_ok = other.m_perilune_ok;
        m_eccentricity = other.m_eccentricity;
        m_timefactor = other.m_timefactor;
        m_autopilot_on = other.m_autopilot_on;
        m_autopilot_mode_string = other.m_autopilot_mode_string;
        m_vspeedcolor = other.m_vspeedcolor;
        m_hspeedcolor = other.m_hspeedcolor;
        m_time = other.m_time;
        m_heading = other.m_heading;
        m_pitch = other.m_pitch;
        m_longitude = other.m_longitude;
        m_apolune_longitude = other.m_apolune_longitude;
        m_perilune_longitude = other.m_perilune_longitude;
        m_orbittype = other.m_orbittype;
        m_VVM = other.m_VVM;
        m_HVM = other.m_HVM;
        m_TPS = other.m_TPS;
        m_sleepTime = other.m_sleepTime;
        m_target_set = other.m_target_set;
        m_target_longitude = other.m_target_longitude;
        m_target_altitude = other.m_target_altitude;
        m_distance_to_target = other.m_distance_to_target;
        m_DOI_longitude = other.m_DOI_longitude;
        m_DOI_set = other.m_DOI_set;
        m_DOI_radius = other.m_DOI_radius;
        m_PDI_set = other.m_PDI_set;
        m_PDI_radius = other.m_PDI_radius;
        m_PDI_longitude = other.m_PDI_longitude;
        m_RCS_RotMode = other.m_RCS_RotMode;
    }

    public void addListener(ILMInstrumentDataListener listener)
    {
        listeners.add(listener);
    }

    public void send()
    {
        int nlisteners = listeners.size();
        for (int index = 0 ; index < nlisteners ; index++)
        {
            ILMInstrumentDataListener listener = listeners.get(index);
            listener.listen(this);
        }
    }

    private ArrayList<ILMInstrumentDataListener> listeners = new ArrayList<ILMInstrumentDataListener>(10);

    







}