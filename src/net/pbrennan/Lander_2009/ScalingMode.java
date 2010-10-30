package net.pbrennan.Lander_2009;

/**
 * an enumeration of the available scaling modes for the main Lunar Lander display. 
 *
 */
public enum ScalingMode {
    LM_SURFACE,             // the closest surface of the moon is at the bottom of the window
    LM_TARGET,              // the LM and the target are both inside the window
    LM_MAX,                 // Zoomed up to the LM as close as we like.
    LM_ALL_MOON             // Show the LM and the entire disk of the moon.
}
