package net.pbrennan.Lander_2009;

/**
 * An enum which allows action descriptors to be attached to symbolic names.
 */
public enum ControlAction 
{
    NULL ("Null", "No action"),

    TURN_LEFT
        ("Turn left", "Apply counterclockwise torque on the spacecraft from the RCS thrusters"),

    TURN_RIGHT
        ("Turn right", "Apply clockwise torque on the spacecraft from the RCS thrusters"),

    TRIM
        ("Trim",
         "Apply the command but at a lower gain.  (Applies to turn and throttle commands)"),

    THRUST_INCREASE
        ("Increase thrust",
        "Increase main engine thrust"),

    THRUST_DECREASE
        ("Decrease thrust",
        "Decrease main engine thrust"),

    THRUST_FULL
        ("Full thrust",
        "Increase main engine thrust to maximum"),

    THRUST_ZERO
        ("Kill thrust",
        "Reduce main engine thrust to zero"),

    STAGE_SEPARATE
        ("Separate Stages",
        "Separate the Ascent and Descent stages and fire the ascent engine at full thrust.  This can be done to lift off from the moon after a successful landing, or as part of an abort maneuver during a landing attempt."),

    TOGGLE_RADAR
        ("Toggle Landing Radar",
        "Toggle the landing radar. (Will only work if you are within 10km of the surface) This is essential to a successful landing."),

    THRUST_SMART_TOGGLE
        ("Thrust smart toggle",
        "If main engine thrust is not zero, kill thrust.  If main engine thrust is zero, apply full thrust."),

    RCS_LEFT
        ("RCS Translate Left",
         "Apply RCS Translation thrusters left"),

    RCS_RIGHT
        ("RCS Translate Right",
         "Apply RCS Translation thrusters right"),
         
    TOGGLE_RCS_ROTATION_MODE
        ("Cycle RCS Rotation Modes",
         "Cycle between free and damp rotation modes"),
         
    CYCLE_AUTOPILOT_MODE
        ("Cycle Autopilot Mode",
         "Cycle between the available autopilot modes"),
          
    TOGGLE_AUTOPILOT
        ("Toggle Autopilot",
         "Toggle between autopilot on and off"),

    TIMEFACTOR_UPx10
        ("Increase time acceleration",
         "Increase the acceleration of time by a factor of 10, up to a maximum of 1000x."),

    TIMEFACTOR_DOWNx10
        ("Decrease time acceleration",
         "Decrease the acceleration of time by a factor of 10, down to a minimum of 1x."),
    //
    //     TOGGLE_SHOW_PREDICTED_FLIGHT_PATH,
    //

    CYCLE_INTERFACE_LEVEL
        ("Cycle through the interface levels",
         "Cycle through the interface levels, from simple to normal to expert."),
         
    ZOOM_TARGET
        ("Zoom to show the landing target",
         "Adjust the zoom level so that the landing target is on the screen."),
         
    ZOOM_SURFACE
        ("Zoom to show the closest surface",
         "Adjust the zoom level to the highest level that includes both the LM and the lunar surface."),
         
    ZOOM_FULL
        ("Zoom to show the LM",
         "Adjust the zoom level to see the LM close up."),
         
    ZOOM_LM_MOON
        ("Zoom to show entire moon",
         "Adjust the zoom level to see the LM and the entire moon"),

    PRINT_STATUS
        ("Print current status to stdout",
         "Print out the current status of the simulation."),
         
    EXPLOSION
        ("Create an explosion of particles",
         "Create an explosion of particles"),

    PAUSE("Pause","Pause the game in progress"),

    RESET("Reset", "Reset the game to its starting state"),

    HELP("Help", "Display help"),

    QUIT("Quit","Quit");

    private final String m_name;
    private final String m_desc;

    ControlAction(String name, String desc)
    {
        m_name = name;
        m_desc = desc;
    }
    public String getName() { return m_name; }
    public String getDesc() { return m_desc; }
}
