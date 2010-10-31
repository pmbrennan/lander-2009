package net.pbrennan.Lander_2009;
// LunarSpacecraft2D.java
//
// provide a simple 2d model of flying around the moon.
//
// The state vector for LunarSpacecraft2D is a HVecN of size = 6.
// The elements of the state vector are:
// vec[0] : x  : x position of the spacecraft, m (0 == center of the moon)
// vec[1] : y  : y position of the spacecraft, m (0 == center of the moon)
// vec[2] : a  : heading of the spacecraft, radians (0= along the x axis)
// vec[3] : vx : x speed of the spacecraft, m/s
// vec[4] : vy : y speed of the spacecraft, m/s
// vec[5] : va : rotational speed of the spacecraft, radians/second (positive == ccw)
// The coordinate system is centered on the moon, i.e. the
// moon is at [0,0]
public class LunarSpacecraft2D implements IDerivableVec
{
    // ================================================================
    // Constants
    // ================================================================
    public static final double G = 6.67300E-11; // m3 kg-1 s-2
    public static final double MoonRadius = 1737400; // m
    private static final double MoonMu = 4.91533e12;   // Gravitational parameter: G times the mass of the moon
    public static final double TWOPI = Math.PI * 2;
    public static final double HALFPI = Math.PI * 0.5;

    // any orbit with abs(e) <= this will just be called a parabola.
    private static final double ParabolicThreshold = 0.0001;

    // this is the angular momentum below which we will just call the
    // orbit a degenerate conic. (10 m/s horiz. velocity at
    // altitude = 0)
    private static final double DegenerateConicThreshold = 17374000;

    // the eccentricity below which we will just call the
    // orbit a circle
    private static final double CircularEccentricityThreshold = 0.00001;

    // ================================================================
    // Operating variables
    // ================================================================
    private String m_name;
    private double m_time; // Time, s

    // ================================================================
    // position and velocity of the vehicle
    // step() will update these.
    // ================================================================
    public enum Status
    {
        Landed,
        Launching,
        InFlight,
        Colliding,
        Exploding,
        Dead
    };
    private Status m_status;
    private int m_launch_countdown = 0; // prevent collisions when lifting
    private HVecN m_state; // [ x  y  a  x'  y'  a' ]

    // ================================================================
    // Dry mass and fuel mass of the vehicle
    // ================================================================
    private double m_dryMass;   // Mass of the vehicle when unfueled, kg
    private double m_fuelMass;  // Mass of the vehicle's fuel, kg
    private double m_fuelCapacity; // How much fuel can the vehicle carry in total?  kg
    private double m_mdot;      // Fuel flow rate, kg/s (should be negative when thrusting)
    private double m_mdotdot;   // The amount by which the throttle is incr/decreasing
    private ThrottleCmd m_throttle_cmd; // Mode of operation of the throttle.
    private double m_maxMdot;   // the maximum possible fuel flow rate
    private double m_RCSaccelRot; // the rotational acceleration due to RCS thrust.
    private double m_RCSaccel;  // the transverse acceleration due to RCS thrust.
                                // fuel is not tracked for the RCS thrusters.

	// ================================================================
	// Rotation Control mode
	// ================================================================
	public enum RCSRotMode
	{
		AttitudeFree,	        // No management of attitude.
		AttitudeDamp           // damp any rotations
	};
	private RCSRotMode m_RCSRotMode = RCSRotMode.AttitudeDamp;

	// ================================================================
	// Attitude control constants
	// ================================================================
	private final double AttitudeDampK = -0.8;
    private final double AttitudeDampVaThreshold = 0.001;
    private final double AttitudeMaxRCSAccel = 50.0 ;

    private final double AttitudeHoldPK = -0.15; // proportional constant
    private final double AttitudeHoldPV = -0.60; // velocity constant

    // ================================================================
    // Autopilot mode
    // ================================================================
    public enum AutopilotMode
    {
        HoldPitch             ("HOLD PITCH"),    // Hold commanded pitch
        HoldVerticalAttitude  ("HOLD VERT"),     // Hold pitch = 90 degrees
        HoldPitch0            ("HOLD 0"),        // Hold pitch = 0 degrees
        HoldPitch180          ("HOLD 180"),      // Hold pitch = 180 degrees
        HoldProgradeAttitude  ("HOLD PRO"),      // Hold pitch along velocity vector
        HoldRetrogradeAttitude("HOLD RETRO"), 	 // Hold pitch against velocity vector
        HoldVerticalRate      ("HOLD VSPEED"),   // Hold vertical rate
        TerminalDescent       ("TERM DESC");     // Perform terminal descent

        private final String mShortDesc;
        AutopilotMode(String shortDesc) { mShortDesc = shortDesc; }
        public String shortDesc() { return mShortDesc; }
    };
    private AutopilotMode m_AutopilotMode = AutopilotMode.HoldPitch;
    private boolean m_AutopilotOn = false;
    private double m_HTargetError = 0.0;
    private double m_AutopilotRotSetPoint = 0.0;

    // ================================================================
    // Performance of the vehicle
    // ================================================================
    private double m_Ve;      // Exhaust velocity
    private double m_landingAltitude; // What is the height of the vehicle when it's
                                    // Just touching down?

    // ================================================================
    // Position and velocity of the spacecraft in a polar frame
    // These are only calculated through a call to ComputePolarFrame()
    // ================================================================
    private double m_theta; // longitudinal position, radians 0 = along the x axis)
    private double m_radius; // radial position, m (0==center of the moon)
    // rhat = the line from the spacecraft to the center of the moon
    private double[] m_rhat; // local vertical, expressed in world coordinates
    private double[] m_hhat; // local horizontal, in world coordinates
    private double m_vr; // vertical velocity, m/s (v dot rhat)
    private double m_vh; // horizontal velocity, m/s (v dot hhat)
    private double m_pitch; // pitch angle = the angle between the a vector and the local horizontal
    private double m_nomAltitude; // nominal altitude, i.e. radius - nominal lunar radius
    private double m_localG; // local acceleration of gravity
    private double m_VVM; // the vertical velocity metric
    private double m_HVM; // the horizontal velocity metric

    // ================================================================
    // Interaction with the terrain.
    // ================================================================
    private Terrain m_Terrain; // the terrain object.
    private double m_terrainAltitude; // real altitude over current terrain
    private double m_terrainNormalAngle; // in radians.
    private boolean m_terrainAltitudeOK = false;

    // ================================================================
    // Landing target.
    // ================================================================
    private boolean m_TargetSet = false;
    private double m_TargetLong;        // Target Longitude in radians.
    private double m_TargetRadius;      // Radial distance of the target from the lunar center.
    private double m_DistanceToTarget;  // datum plane distance from current position to target.

    private boolean m_PDISet;           // whether there is a PDI point set.
    private double m_PDIRadius;         // the radius of the PDI point (meters)
	private double m_PDILong;			// PDI point, where to begin Powered Descent. (radians)
	private boolean m_DOISet;           // whether there is a DOI point set.
    private double m_DOIRadius;         // the radius of the DOI point (meters)
	private double m_DOILong;			// DOI point, where to enter Descent Orbit. (radians)

    // ================================================================
    // Predicted orbital parameters
    // These are only updated via calls to PredictOrbitalParameters().
    // ================================================================
    public enum OrbitType { None,            // Not computed yet.
                            DegenerateConic, // Dropping like a stone
                            Circle,          // ecc = 0
                            Ellipse,         // 0 < ecc < 1
                            Parabola,        // Escape orbits, ecc = 1
                            Hyperbola };     // ecc > 1
    private OrbitType       m_OrbitType;
    private double          m_predictedEccentricity; // ecc
    private double          m_predictedApolune; // m
    private double          m_predictedPerilune; // m
    private double          m_energy;
    private double          m_angularMomentum;
    private double          m_longitudePerilune; // in radians
    private double          m_longitudeApolune;  // in radians

    // ================================================================
    // Integrator type.  step() uses this to determine how to update
    // the spacecraft state.
    // ================================================================
    public enum IntegratorType { RungeKutta4,
                                 Euler,
                                 DeadReckoning };
    private IntegratorType m_IntegratorType;

    private RungeKutta4thVecN m_Integrator = new RungeKutta4thVecN(6);

    // ================================================================
    // Default constructor.
    // ================================================================
    public LunarSpacecraft2D()
    {
        Initialize();
    }

    public LunarSpacecraft2D(String name)
    {
        Initialize();
        SetName(name);
    }

    // Set up any private members.
    private void Initialize()
    {
        m_state = new HVecN(6);
        m_rhat = new double[2];
        m_hhat = new double[2];
        m_name = new String();
        m_OrbitType = OrbitType.None;
        m_IntegratorType = IntegratorType.RungeKutta4;

        m_maxMdot = 1.0;
        m_fuelCapacity = 1.0;
        m_dryMass = 1.0;
        m_radius = 1.0;
    }

    // ================================================================
    // Getters and setters.
    // ================================================================
    public String GetName() { return m_name; }
    public void SetName(String name) { m_name = name; }

    public double GetTime() { return m_time; }
    public void SetTime(double t) { m_time = t; }

    // retrieve the lunar gravitational parameter
    public double GetMu() { return MoonMu; }

    // retrieve the status of the spacecraft
    public Status GetStatus() { return m_status; }

    public void SetStatus(Status status) { m_status = status; }

    // retrieve the state of the spacecraft, i.e. the position and velocity
    public HVecN GetState() { return m_state; }

    public void SetPosition(double x, double y, double a)
    {
        m_state.vec[0] = x;
        m_state.vec[1] = y;
        m_state.vec[2] = a;
    }

    public void SetVelocity(double vx, double vy, double va)
    {
        m_state.vec[3] = vx;
        m_state.vec[4] = vy;
        m_state.vec[5] = va;
    }

    public void SetRotationVelocity(double va)
    {
        m_state.vec[5] = va;
    }

    public void SetRotationAccel(double aa)
    {
        m_RCSaccelRot = aa;
    }

    public void SetTranslationAccel(double at)
    {
        m_RCSaccel = at;
    }

    public void SetDryMass(double dryMass) { m_dryMass = dryMass; }
    public void SetFuelMass(double fuelMass) { m_fuelMass = fuelMass; }

    // We can try to set the throttle, but if we have no gas,
    // we have no gas.
    public void SetMDot(double mdot)
    {
        if (m_fuelMass > 0.0)
            m_mdot = mdot;
        else
            m_mdot = 0.0;
    }
    public void SetMaxMDot(double mdot_max) {m_maxMdot = mdot_max; }
    public void SetThrottle(double throttle)
    {
        if (m_fuelMass > 0.0)
            m_mdot = m_maxMdot * throttle;
        else
            m_mdot = 0.0;
    }
    public void SetThrottleRate(double throttleRate)
    {
        m_mdotdot = throttleRate * m_maxMdot;
    }
    public void SetThrottleCommand(ThrottleCmd cmd)
    {
        m_throttle_cmd = cmd;
    }

    public double GetDryMass() { return m_dryMass; }
    public double GetFuelMass() { return m_fuelMass; }
    public double GetMDot() { return m_mdot; }
    public double GetTotalMass() { return (m_dryMass + m_fuelMass); }
    public void SetFuelCapacity(double fuelCapacity) { m_fuelCapacity = fuelCapacity; }
    public double GetFuelCapacity() { return m_fuelCapacity; }
    public void SetTanksToFull() { m_fuelMass = m_fuelCapacity; }

    // return the fuel remaining as a percentage
    public double GetPercentFuel()
    {
        double percent = 0.0;
        if (m_fuelCapacity > 0.0)
        {
            percent = 100.0 * (m_fuelMass / m_fuelCapacity);
        }
        return percent;
    }

    public void SetVe (double Ve) { m_Ve = Ve; }

    public void SetLandingAltitude (double la) { m_landingAltitude = la; }

    public void ToggleRCSRotationMode()
    {
        switch (m_RCSRotMode)
        {
            case AttitudeDamp:
                m_RCSRotMode = RCSRotMode.AttitudeFree;
                break;

            case AttitudeFree:
                m_RCSRotMode = RCSRotMode.AttitudeDamp;
                break;
        }
    }

    public void SetAutopilotMode(AutopilotMode newmode)
    {
        m_AutopilotMode = newmode;
    }

    // As a safety precaution, whenever the autopilot mode is
    // being cycled, the autopilot is turned off.
    public void CycleAutopilotMode()
    {
        m_AutopilotOn = false;

        switch (m_AutopilotMode)
        {
        case HoldPitch:
            m_AutopilotMode = AutopilotMode.HoldVerticalAttitude;
            break;

        case HoldVerticalAttitude:
            m_AutopilotMode = AutopilotMode.HoldPitch0;
            break;

        case HoldPitch0:
            m_AutopilotMode = AutopilotMode.HoldPitch180;
            break;

        case HoldPitch180:
            m_AutopilotMode = AutopilotMode.HoldProgradeAttitude;
            break;

        case HoldProgradeAttitude:
            m_AutopilotMode = AutopilotMode.HoldRetrogradeAttitude;
            break;

        case HoldRetrogradeAttitude:
            m_AutopilotMode = AutopilotMode.HoldVerticalRate;
            break;

        case HoldVerticalRate:
        	m_AutopilotMode = AutopilotMode.TerminalDescent;
        	break;

        case TerminalDescent:
            m_AutopilotMode = AutopilotMode.HoldPitch;
            break;

        }
    }

    public boolean GetAutopilotOn()
    {
        return m_AutopilotOn;
    }

    public void SetAutopilotOn(boolean newval)
    {
        m_AutopilotOn = newval;
        if (m_AutopilotMode == AutopilotMode.HoldPitch)
        {
            m_AutopilotRotSetPoint = m_pitch;
        }
    }

    public String GetAutopilotModeString()
    {
        return m_AutopilotMode.shortDesc();
    }

    // return the remaining delta-v
    public double GetDeltaV()
    {
        if (m_dryMass <= 0.0)
            return -1.0;

        double massRatio = (m_dryMass + m_fuelMass) / m_dryMass;
        double deltaV = m_Ve * Math.log(massRatio);
        return deltaV;
    }

    public double GetThrottlePercent()
    {
        double throttle = ( m_mdot / m_maxMdot ) * 100.0;
        return throttle;
    }

    // Knowing the state of the vehicle in the world frame,
    // compute the state of the vehicle in the polar frame.
    public synchronized void ComputePolarFrame()
    {
        double x = m_state.vec[0];
        double y = m_state.vec[1];
        double a = m_state.vec[2];
        double vx = m_state.vec[3];
        double vy = m_state.vec[4];

        System.out.println("LunarSpacecraft2D: calling normalizeAngle...");
        m_theta = normalizeAngle(Math.atan2(y,x));
        double rsquared = x*x + y*y;
        m_radius = Math.sqrt(rsquared);

        m_rhat[0] = x / m_radius;
        m_rhat[1] = y / m_radius;

        m_hhat[0] = m_rhat[1];
        m_hhat[1] = -m_rhat[0];

        // vertical velocity, v dot rhat
        m_vr = vx * m_rhat[0] + vy * m_rhat[1];
        // horizontal velocity, v dot hhat
        m_vh = vx * m_hhat[0] + vy * m_hhat[1];

        // pitch angle
        m_pitch = normalizeAngle(a - m_theta + HALFPI);

        // nominal Altitude
        m_nomAltitude = m_radius - MoonRadius;

        // local acceleration of gravity
        m_localG = MoonMu / rsquared;

        System.out.println("LunarSpacecraft2D: calling getTerrainAltitude...");
        m_terrainAltitude = m_Terrain.getTerrainAltitude(m_theta, m_state.vec[0], m_state.vec[1]);
        m_terrainAltitudeOK = true;
        
        System.out.println("LunarSpacecraft2D: calling getTerrainNormalAngle...");
        m_terrainNormalAngle = m_Terrain.getTerrainNormalAngle(m_state.vec[0], m_state.vec[1]);
        System.out.println("LunarSpacecraft2D: finished getTerrainNormalAngle...");

        if (m_status != Status.InFlight)
        {
            m_VVM = 0.0;
        }
        else
        {
            //m_VVM = GetTotalMass() * ((m_vr * m_vr) / (2 * this.m_TargetRadius - LanderUtils.MoonRadius - m_landingAltitude) + m_localG)
            //    / (m_maxMdot * m_Ve);
            m_VVM = GetTotalMass() * ((m_vr * m_vr) / (2 * m_terrainAltitude - m_landingAltitude) + m_localG)
                / (m_maxMdot * m_Ve);
        }

        if (m_TargetSet)
        {
            System.out.println("LunarSpacecraft2D: calling absAngleDifference...");
            m_DistanceToTarget = LanderUtils.absAngleDifference(m_TargetLong, m_theta) * MoonRadius;
        }
        else
        {
            m_DistanceToTarget = 1000000000.0;
        }

        if ((Math.abs(m_DistanceToTarget) > 0.1)&&(m_status == Status.InFlight))
        {
            double targetX = x - Math.cos(m_TargetLong) * m_TargetRadius;
            double targetY = y - Math.sin(m_TargetLong) * m_TargetRadius;
            m_HTargetError = targetX * m_hhat[0] + targetY * m_hhat[1];
            m_HVM = GetTotalMass() * ((m_vh * m_vh) / (2 * Math.abs(m_HTargetError))) / (m_maxMdot * m_Ve);

            if (m_vh >= 0.0)
                m_HVM *= -1.0;

            if (m_HVM > 1.0)
                m_HVM = 1.0;
            else if (m_HVM < -1.0)
                m_HVM = -1.0;
        }
        else
        {
            m_HVM = 0.0;
        }
    }

    // These only make sense after calling ComputePolarFrame().
    public double GetTheta() { return m_theta; }
    public double GetRadius() { return m_radius; }
    public double GetVerticalVelocity() { return m_vr; }
    public double GetHorizontalVelocity() { return m_vh; }
    public double GetPitchAngle() { return m_pitch; }
    public double GetNominalAltitude() { return m_nomAltitude; }
    public double GetTerrainAltitude() { return m_terrainAltitude; }
    public double GetTerrainNormalAngle() { return m_terrainNormalAngle; }
    public double GetLocalG() { return m_localG; }
    public double GetVVM() { return m_VVM; }
    public double GetHVM() { return m_HVM; }
    public double GetDistanceToTarget() { return m_DistanceToTarget; } // negative means not good.

    // Target point information
    public void SetTargetLong(double inLong)
    {
        m_TargetLong = inLong;
        System.out.println("Target set at " + Math.toDegrees(inLong) + " degrees.");

        if (m_Terrain != null)
        {
            m_TargetRadius = -1.0 * m_Terrain.getTerrainAltitude(inLong,
                MoonRadius * Math.cos(inLong), MoonRadius * Math.sin(inLong));

            System.out.println("Target altitude = " + m_TargetRadius + " m above the datum.");

            m_TargetRadius = MoonRadius + m_TargetRadius;
        }
        m_TargetSet = true;
    }
    public double GetTargetLong() { return m_TargetLong; }
    public boolean GetTargetSelected() { return m_TargetSet; }
    public double GetTargetRadius() { return m_TargetRadius; }

    /**
     *
     * @param inPDI : longitude of the PDI point in radians
     */
    public void SetPDI(boolean inSet, double inPDILong, double inPDIAltitude)
    {
        m_PDISet = inSet;
        m_PDILong = inPDILong;
        m_PDIRadius = inPDIAltitude + LanderUtils.MoonRadius;
    }

    public double GetPDILong() { return m_PDILong; }
    public boolean GetPDISet() { return m_PDISet; }
    public double GetPDIRadius() { return m_PDIRadius; }

    public void SetDOI(boolean inSet, double inDOILong, double inDOIAltitude)
    {
        m_DOISet = inSet;
        m_DOILong = inDOILong;
        m_DOIRadius = inDOIAltitude + LanderUtils.MoonRadius;
	}

    public double GetDOILong() { return m_DOILong; }
    public boolean GetDOISet() { return m_DOISet; }
    public double GetDOIRadius() { return m_DOIRadius; }

    // Integrator type
    public IntegratorType GetIntegrator() { return m_IntegratorType; }
    public void SetIntegrator (IntegratorType type) { m_IntegratorType = type; }

    // Setter/Getter for terrain
    // NB The value of m_terrainAltitude is set in ComputePolarFrame().
    public void SetTerrain(Terrain inTerrain) { m_Terrain = inTerrain; }
    public Terrain GetTerrain() { return m_Terrain; }

    // ================================================================
    // Compute the time derivative of the state vector.
    // Given [x y a x' y' a'] at time t, return approximated
    // [x' y' a' x'' y'' a''] at time t+h
    // if supplied with a vector to use as the return value, use it;
    // otherwise, allocate it.
    public HVecN deriv(double t, double h, HVecN state, HVecN returnVector)
    {
        HVecN rv;
        if (returnVector == null)
            rv = new HVecN(6);
        else
            rv = returnVector;

        double x = state.vec[0];
        double y = state.vec[1];
        double a = state.vec[2];
        double vx = state.vec[3];
        double vy = state.vec[4];
        double va = state.vec[5];

        // acceleration due to gravity
        // a = GM / r^2
        double rsquared = x*x + y*y;
        double ag = - MoonMu / rsquared;
        double angle = Math.atan2(y,x);

        double ax;
        double ay;

        if (m_status == Status.Landed)
        {
            ax = ay = 0.0;
        }
        else
        {
            ax = ag * Math.cos(angle);
            ay = ag * Math.sin(angle);
        }

        // acceleration due to main engine thrust
        // F = mdot * Ve
        double thrust = m_mdot * m_Ve;
        double mass = m_dryMass + m_fuelMass;
        double aRocket = thrust / mass;
        if (aRocket > 0.0)
        {
            ax += aRocket * Math.cos(a);
            ay += aRocket * Math.sin(a);
        }

        // acceleration due to RCS thrusters
        if (m_RCSaccel != 0.0)
        {
            ax += m_RCSaccel * Math.sin(a);
            ay -= m_RCSaccel * Math.cos(a);
        }

        // Compute angular acceleration.
        // This code will determine whether the user has commanded a torque
        // through the RCS system.  If not, then the control system will
        // counteract the existing rotation to bring the LM to a stop.
        double aa = m_RCSaccelRot * AttitudeMaxRCSAccel;

        if (m_status == Status.Landed)
        {
            va = aa = 0.0;
        }
        else if (m_AutopilotOn)
        {
            double desiredAngle;
            if (m_AutopilotMode == AutopilotMode.HoldPitch)
                desiredAngle = angle - LanderUtils.HALF_PI + m_AutopilotRotSetPoint; // used as a pitch setpoint
            else if (m_AutopilotMode == AutopilotMode.HoldVerticalAttitude)
                desiredAngle = angle;
            else if (m_AutopilotMode == AutopilotMode.HoldPitch0)
                desiredAngle = angle - LanderUtils.HALF_PI;
            else if (m_AutopilotMode == AutopilotMode.HoldPitch180)
                desiredAngle = angle + LanderUtils.HALF_PI;
            else if (m_AutopilotMode == AutopilotMode.HoldProgradeAttitude)
                desiredAngle = Math.atan2(vy, vx);
            else if (m_AutopilotMode == AutopilotMode.HoldRetrogradeAttitude)
                desiredAngle = Math.atan2(-vy, -vx);
            else if (m_AutopilotMode == AutopilotMode.TerminalDescent)
            	desiredAngle = angle - LanderUtils.HALF_PI + m_AutopilotRotSetPoint; // used as a pitch setpoint.
            else if (m_AutopilotMode == AutopilotMode.HoldVerticalRate)
                desiredAngle = angle - LanderUtils.HALF_PI + m_AutopilotRotSetPoint; // used as a pitch setpoint.
            else
                desiredAngle = a;

            // error = current angle - desired angle
            double angularError = LanderUtils.diff180(a, desiredAngle);

            // desired angular velocity - current angular velocity
            //double holdAngularV = (vx * y - vy * x) / rsquared;

            // Apply a torque if the angular set point is off
            aa += (AttitudeHoldPK * angularError);
            aa += (AttitudeHoldPV * va);
        }
        else if (m_RCSRotMode == RCSRotMode.AttitudeDamp)
        {
			if (aa == 0.0)
			{
			    aa = va * AttitudeDampK;
			}
		}

        rv.vec[0] = vx;
        rv.vec[1] = vy;
        rv.vec[2] = va;
        rv.vec[3] = ax;
        rv.vec[4] = ay;
        rv.vec[5] = aa;

        return rv;
    }

    // Advance the state of the throttle
    private void stepThrottle(double h)
    {
        switch (m_throttle_cmd)
        {
            case USE_RATE:
                m_mdot += m_mdotdot * h;
                if (m_mdot > m_maxMdot)
                    m_mdot = m_maxMdot;
                else if (m_mdot < 0.0)
                    m_mdot = 0.0;
                break;
            case FULL:
                m_mdot = m_maxMdot;
                m_throttle_cmd = ThrottleCmd.NONE;
                break;
            case KILL:
                m_mdot = 0.0;
                m_throttle_cmd = ThrottleCmd.NONE;
                break;
            case SMART_TOGGLE:
                if (m_mdot > 0.0)
                    m_mdot = 0.0;
                else
                    m_mdot = m_maxMdot;
                m_throttle_cmd = ThrottleCmd.NONE;
                break;
        }
        SetMDot(m_mdot);

        // Has a launch been commanded?
        if ((m_mdot > 0.0) && (m_status == Status.Landed))
        {
            m_status = Status.Launching;
            m_launch_countdown = 10;
        }
    }

    private void stepAutopilot(double h)
    {
		if (m_AutopilotOn == false)
		{
			return;
		}

        if (m_AutopilotMode == AutopilotMode.TerminalDescent)
        {
			if (m_vr < 0.0)
			{
            	m_AutopilotRotSetPoint = Math.atan2(m_VVM, 1.5 * m_HVM);
			}
			else
			{
				m_AutopilotRotSetPoint = Math.atan2(0.0, 1.5 * m_HVM);
			}
            m_throttle_cmd = ThrottleCmd.USE_RATE;
            double throttleFraction = m_VVM / Math.sin(m_AutopilotRotSetPoint);

            // TODO: No thrust unless pitch is approximately where we want it to be.
            m_mdot = m_maxMdot * throttleFraction;
            m_mdotdot = 0.0;
        }
        else if (m_AutopilotMode == AutopilotMode.HoldVerticalRate)
        {
			// Assume there is some throttle setting (which we will
			// leave alone)
			// Adjust pitch so that the vertical descent rate holds
			// steady
			// i.e. the thrust just balances gravity and centrifugal
			// "force"

			double lmMass = GetTotalMass();
			double bodyForceSum = lmMass * (m_localG - (m_vh * m_vh) / m_radius);
			double throttleForce = m_mdot * m_Ve;

			if (throttleForce <= 0.0)
			{
				m_AutopilotRotSetPoint = m_pitch;
			}
			else if (throttleForce < bodyForceSum)
			{
			    if (Math.cos(m_pitch) > 0.0)
			        m_AutopilotRotSetPoint = 0.0;
			    else
			        m_AutopilotRotSetPoint = Math.PI;
			}
			else
			{
			    if (Math.cos(m_pitch) > 0.0)
			        m_AutopilotRotSetPoint = Math.asin(bodyForceSum / throttleForce);
			    else 
			        m_AutopilotRotSetPoint = Math.PI - Math.asin(bodyForceSum / throttleForce);
			}
		}
    }

    // Advance the status of the spacecraft by performing collision calculations
    // TODO: Improve this!
    private void stepCollision(double h)
    {
        if (m_status == Status.Launching)
        {
            m_launch_countdown--;
            if (m_launch_countdown <= 0)
            {
                m_status = Status.InFlight;
            }
            return;
        }

        // a very naive implementation...
        double x = m_state.vec[0];
        double y = m_state.vec[1];
        //double a = m_state.vec[2];
        double vx = m_state.vec[3];
        double vy = m_state.vec[4];
        //double va = m_state.vec[5];

        // Acceleration due to gravity
        // a = GM / r^2
        //double rsquared = x*x + y*y;
        //double r = Math.sqrt(rsquared);

        if (m_terrainAltitudeOK)
        {
            double altitude = m_terrainAltitude;
            //double ag = - MoonMu / rsquared;
            //double angle = Math.atan2(y,x);
            //double v = Math.sqrt(vx * vx + vy * vy);
            if (altitude <= m_landingAltitude)
            {
                m_mdot = 0.0;
                m_mdotdot = 0.0;
                m_throttle_cmd = ThrottleCmd.KILL;
                m_state.vec[3] = 0.0;
                m_state.vec[4] = 0.0;
                m_state.vec[5] = 0.0;
                m_AutopilotOn = false;

                if (m_status == Status.InFlight)
                {
                    LandingType type = LandingType.getLandingType(m_vr, m_vh, Math.toDegrees(m_pitch));

                    m_status = Status.Landed;

                    LMLandingEvent e = new LMLandingEvent(type, m_vr, m_vh, m_DistanceToTarget, "");
                    System.out.println("LANDING DETECTED!\n" + e.toString());

                    if (e.type == LandingType.CRASH)
                    {
                        m_status = Status.Exploding;
                    }

                    LMEventSource.dispatchEvent(e);
                }
            }
        }
    }

    // Advance the state of the spacecraft by h seconds.
    public void step(double h)
    {
        if ((m_status == Status.Dead)||
            (m_status == Status.Exploding))
        {
            return;
        }

        //System.out.println("LunarSpacecraft2D.step: Stepping the autopilot...");
        stepAutopilot(h);

        //System.out.println("LunarSpacecraft2D.step: Stepping the throttle...");
        stepThrottle(h);

        //System.out.println("LunarSpacecraft2D.step: Stepping the collider...");
        stepCollision(h);

        if (m_status == Status.Landed)
        {
            return;
        }

        // Is there enough fuel?
        double deltaFuel = m_mdot * h;
        if (m_fuelMass < deltaFuel)
            deltaFuel = m_fuelMass;

        switch (m_IntegratorType)
        {
            case RungeKutta4:
                stepRK4(h);
                break;

            case Euler:
                stepEuler(h);
                break;

            case DeadReckoning:
                stepDR(h);
                break;
        }

        if ((m_RCSRotMode == RCSRotMode.AttitudeDamp)
             && (Math.abs(m_state.vec[2]) < AttitudeDampVaThreshold))
        {
            m_state.vec[2] = 0.0;
        }

        m_time += h;
        m_fuelMass -= deltaFuel;
        m_state.vec[2] = normalizeAngle(m_state.vec[2]);
    }

    // perform step using fourth-order Runge-Kutta integration.
    public void stepRK4(double h)
    {
        //System.out.println("Calling step() function...");
        m_Integrator.step(m_time, h, m_state, this);
    }

    public void stepEuler(double h)
    {
        HVecN ds = deriv(m_time, h, m_state, null);
        for (int i=0 ; i<6 ; ++i)
        {
            m_state.vec[i] += ds.vec[i] * h;
        }
    }

    public void stepDR(double h)
    {
        HVecN ds = deriv(m_time, h, m_state, null);
        m_state.vec[0] += (ds.vec[0] + 0.5 * ds.vec[3] * h) * h;
        m_state.vec[1] += (ds.vec[1] + 0.5 * ds.vec[4] * h) * h;
        m_state.vec[2] += (ds.vec[2] + 0.5 * ds.vec[5] * h) * h;
        m_state.vec[3] += (ds.vec[3] * h);
        m_state.vec[4] += (ds.vec[4] * h);
        m_state.vec[5] += (ds.vec[5] * h);
    }

    // Given [x y x' y'], compute the angular momentum
    // This should be "r cross v" but since this is a 2d system it's
    // perfectly OK to return a scalar.
    public double ComputeAngularMomentum(HVecN state)
    {
        double x = state.vec[0];
        double y = state.vec[1];
        double vx = state.vec[3];
        double vy = state.vec[4];

        double h = x * vy - y * vx;
        return h;
    }

    // Given [x y x' y'], compute the specific energy of
    // the system.
    public double ComputeSpecificEnergy(HVecN state)
    {
        double x = state.vec[0];
        double y = state.vec[1];
        double vx = state.vec[3];
        double vy = state.vec[4];

        double r = Math.sqrt(x * x + y * y); // orbital radius
        double v2 = vx * vx + vy * vy; // square of speed

        // Compute specific kinetic energy
        double KE = 0.5 * v2;
        // Compute specific potential energy
        double PE = - MoonMu / r;

        // their sum is the specific energy
        return (KE + PE);
    }

    // Predict the apolune and perilune of the orbit the spacecraft is currently in.
    public synchronized void PredictOrbitalParameters()
    {
        // angular momentum
        double h = ComputeAngularMomentum(m_state);
        m_angularMomentum = h;

        // specific energy
        double e = ComputeSpecificEnergy(m_state);
        m_energy = e;

         // semilatus rectum
        double p;

        // semimajor axis.
        @SuppressWarnings("unused")
        double a;

        // distance between the foci
        // double c;

        // If the angular momentum is nearly zero, the
        // orbit may technically be a conic, but it's
        // a degenerate conic.
        // We will take a very expansive view of what constitutes a
        // degenerate conic.  If the spacecraft is moving with a horizontal
        // velocity of less than 10m/s, we'll call that degenerate and be done
        // with it.
        if (Math.abs(h) < DegenerateConicThreshold)
        {
            m_OrbitType = OrbitType.DegenerateConic;
            return;
        }

        if (Math.abs(e) < ParabolicThreshold)
        {
            m_OrbitType = OrbitType.Parabola;
        }
        else if (e < 0.0)
        {
            m_OrbitType = OrbitType.Ellipse;
        }
        else if (e > 0.0)
        {
            m_OrbitType = OrbitType.Hyperbola;
        }

        // eccentricity
        double sqrtTerm = (1 + (2 * e * (h/MoonMu) * (h/MoonMu)));
        // Because of numerical error, this term can be slightly
        // less than zero, which will render the sqrt NaN.
        // Therefore we guard against this possibility.
        if (sqrtTerm < 0.0)
            sqrtTerm = 0.0;
        double ecc = Math.sqrt(sqrtTerm);
        m_predictedEccentricity = ecc;

        //System.out.println("e = " + e + "   h = " + h + "   ecc = " + ecc);

        if (Math.abs(ecc) < CircularEccentricityThreshold)
        {
            m_OrbitType = OrbitType.Circle;
        }

        // p is the semilatus rectum
        p = h * h / MoonMu;
        //System.out.println("p = " + p);

        // a is the semimajor axis.
        if (m_OrbitType != OrbitType.Parabola)
            a = - MoonMu / (2 * e);
        else
            a = 0.0;

        //System.out.println("a = " + a);

        // c is the distance between the two foci of the orbit
        // c = a * ecc;
        //System.out.println("c = " + c);

        switch (m_OrbitType)
        {
            case DegenerateConic:
                m_predictedApolune = 0.0;
                m_predictedPerilune = 0.0;
                break;

            case Ellipse:
                m_predictedPerilune = p / (1 + ecc);
                m_predictedApolune = p / (1 - ecc);
                break;

            case Circle:
                m_predictedPerilune = p ;
                m_predictedApolune = p;
                break;

            case Hyperbola:
                m_predictedPerilune = p / (1 + ecc);
                m_predictedApolune = 0.0;
                break;

            case Parabola:
                m_predictedPerilune = p / (1 + ecc);
                m_predictedApolune = 0.0;
                break;
        }

        // Compute the longitude of the perilune.
        double x = m_state.vec[0];
        double y = m_state.vec[1];
        HVecN rvec = new HVecN(x,y);
        double r = rvec.magnitude();

        double vx = m_state.vec[3];
        double vy = m_state.vec[4];
        double vsquared = vx * vx + vy * vy;
        HVecN vvec = new HVecN(vx,vy);

        HVecN evecPart1 = rvec.mul(vsquared - (MoonMu / r));
        HVecN evecPart2 = vvec.mul(rvec.dot(vvec)).mul(-1.0);
        HVecN evec = evecPart1.add(evecPart2);

        m_longitudePerilune = normalizeAngle(Math.atan2(evec.vec[1], evec.vec[0]));
        m_longitudeApolune = normalizeAngle(m_longitudePerilune + Math.PI);
    }

    public double GetEccentricity()
    {
        return m_predictedEccentricity;
    }

    public double GetApolune()
    {
        return m_predictedApolune;
    }

    public double GetPerilune()
    {
        return m_predictedPerilune;
    }

    public double GetApoluneLongitude()
    {
        return m_longitudeApolune;
    }

    public double GetPeriluneLongitude()
    {
        return m_longitudePerilune;
    }

    public OrbitType GetOrbitType()
    {
        return m_OrbitType;
    }

    public void Initialize(Mission m)
    {
        SetName(m.shipName);
        SetTime(0.0);
        if (m.status == 0)
            SetStatus(Status.Landed);
        else
            SetStatus(Status.InFlight);
        SetPosition(m.x, m.y, Math.toRadians(m.a));
        SetVelocity(m.vx, m.vy, Math.toRadians(m.va));
        SetDryMass(m.dryMass);
        SetFuelMass(m.fuelMass);
        SetMDot(0.0);
        SetThrottleRate(0.0);
        SetThrottleCommand(ThrottleCmd.NONE);
        SetMaxMDot(m.maxMDot);
        SetFuelCapacity(m.fuelCapacity);
        SetVe(m.Ve);
        SetTargetLong(Math.toRadians(m.targetLongitude));

        SetDOI((m.DOISpecified != 0 ? true : false), Math.toRadians(m.DOILongitude), m.DOIAltitude);
        SetPDI((m.PDISpecified != 0 ? true : false), Math.toRadians(m.PDILongitude), m.PDIAltitude);
        SetLandingAltitude(m.landingAlt);
        SetAutopilotMode(AutopilotMode.HoldPitch);
        SetAutopilotOn(false);
        ComputePolarFrame();
    }

    public String MissionState()
    {
        String rv = new String();

        rv  = "x   : " + m_state.vec[0] + "\n";
        rv += "y   : " + m_state.vec[1] + "\n";
        rv += "a   : " + Math.toDegrees(m_state.vec[2]) + "\n";
        rv += "vx  : " + m_state.vec[3] + "\n";
        rv += "vy  : " + m_state.vec[4] + "\n";
        rv += "va  : " + Math.toDegrees(m_state.vec[5]) + "\n";
        rv += "fuel: " + m_fuelMass + " \n";

        return rv;
    }

    public String StatusString()
    {
        String rv = new String();

        // name, time, x, y, vx, vy, radial v, horizontal v, energy, angMomentum
        rv += m_name + ", " + m_time + ", " +
              m_state.vec[0] + ", " +
              m_state.vec[1] + ", " +
              m_state.vec[3] + ", " +
              m_state.vec[4] + ", " +
              m_vr + ", " +
              m_vh + ", " +
              m_energy + ", " +
              m_angularMomentum
              ;
        return rv;
    }

    public double normalizeAngle(double angle)
    {
        double rv = angle;
        while (rv < 0.0)
            rv += TWOPI;
        while (rv > TWOPI)
            rv -= TWOPI;

        return rv;
    }

    public RCSRotMode GetRCSRotMode()
    {
        return this.m_RCSRotMode;
    }
}
