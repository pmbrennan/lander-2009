package net.pbrennan.Lander_2009;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class LMControls implements KeyListener
{
    public LMControls()
    {
        m_bindings.add(new ControlBinding(KeyEvent.VK_LEFT,         ControlAction.TURN_LEFT));
        m_bindings.add(new ControlBinding(KeyEvent.VK_RIGHT,        ControlAction.TURN_RIGHT));
        m_bindings.add(new ControlBinding(KeyEvent.VK_OPEN_BRACKET, ControlAction.RCS_LEFT));
        m_bindings.add(new ControlBinding(KeyEvent.VK_CLOSE_BRACKET, ControlAction.RCS_RIGHT));
        m_bindings.add(new ControlBinding(KeyEvent.VK_BACK_SLASH,   ControlAction.TOGGLE_RCS_ROTATION_MODE));
        m_bindings.add(new ControlBinding(KeyEvent.VK_UP,           ControlAction.THRUST_INCREASE));
        m_bindings.add(new ControlBinding(KeyEvent.VK_DOWN,         ControlAction.THRUST_DECREASE));
        m_bindings.add(new ControlBinding(KeyEvent.VK_SPACE,        ControlAction.THRUST_SMART_TOGGLE));
        m_bindings.add(new ControlBinding(KeyEvent.VK_Q,            ControlAction.THRUST_ZERO));
        m_bindings.add(new ControlBinding(KeyEvent.VK_SHIFT,        ControlAction.TRIM));
        m_bindings.add(new ControlBinding(KeyEvent.VK_R,            ControlAction.TOGGLE_RADAR));
        //m_bindings.add(new ControlBinding(KeyEvent.VK_PAUSE,        ControlAction.PAUSE));
        m_bindings.add(new ControlBinding(KeyEvent.VK_ESCAPE,       ControlAction.PAUSE));
        m_bindings.add(new ControlBinding(KeyEvent.VK_T,            ControlAction.TIMEFACTOR_UPx10));
        m_bindings.add(new ControlBinding(KeyEvent.VK_Y,            ControlAction.TIMEFACTOR_DOWNx10));
        m_bindings.add(new ControlBinding(KeyEvent.VK_1,            ControlAction.ZOOM_LM_MOON));
        m_bindings.add(new ControlBinding(KeyEvent.VK_2,            ControlAction.ZOOM_TARGET));
        m_bindings.add(new ControlBinding(KeyEvent.VK_3,            ControlAction.ZOOM_SURFACE));
        m_bindings.add(new ControlBinding(KeyEvent.VK_4,            ControlAction.ZOOM_FULL));
        m_bindings.add(new ControlBinding(KeyEvent.VK_F12,          ControlAction.RESET));
        m_bindings.add(new ControlBinding(KeyEvent.VK_F1,           ControlAction.HELP));
        m_bindings.add(new ControlBinding(KeyEvent.VK_P,            ControlAction.TOGGLE_AUTOPILOT));
        m_bindings.add(new ControlBinding(KeyEvent.VK_O,            ControlAction.CYCLE_AUTOPILOT_MODE));
	m_bindings.add(new ControlBinding(KeyEvent.VK_Z,            ControlAction.TARGET_DECREMENT_LONG));
	m_bindings.add(new ControlBinding(KeyEvent.VK_X,            ControlAction.TARGET_INCREMENT_LONG));
        m_bindings.add(new ControlBinding(KeyEvent.VK_S,            ControlAction.PRINT_STATUS));
        describeKeyBindings();
    }



    public void keyPressed(KeyEvent e) {
        ControlAction action = findBinding(e.getKeyCode());

        switch (action)
        {
            case TURN_LEFT:
                m_turnleft = true;
            break;

            case TURN_RIGHT:
                m_turnright = true;
            break;

            case RCS_LEFT:
                m_rcsleft = true;
            break;

            case RCS_RIGHT:
                m_rcsright = true;
            break;

            case THRUST_INCREASE:
                m_throttleup = true;
            break;

            case THRUST_DECREASE:
                m_throttledown = true;
            break;

            case THRUST_FULL:
                m_fullthrottle = true;
            break;

            case THRUST_ZERO:
                m_killthrottle = true;
            break;

            case TRIM:
                m_trim = true;
            break;
        }
        computeCommands();
    }

    public void keyReleased(KeyEvent e) {
        ControlAction action = findBinding(e.getKeyCode());
        int modifierMask = e.getModifiers();

        switch (action)
        {
            case TURN_LEFT:
                m_turnleft = false;
            break;

            case TURN_RIGHT:
                m_turnright = false;
            break;

            case RCS_LEFT:
                m_rcsleft = false;
            break;

            case RCS_RIGHT:
                m_rcsright = false;
            break;

            case THRUST_INCREASE:
                m_throttleup = false;
            break;

            case THRUST_DECREASE:
                m_throttledown = false;
            break;

            case THRUST_FULL:
                m_fullthrottle = false;
            break;

            case THRUST_ZERO:
                m_killthrottle = false;
            break;

            case TRIM:
                m_trim = false;
            break;

            case PRINT_STATUS:
                m_print_status = true;
            break;
            
            case EXPLOSION:
                m_explode = true;
            break;

            case PAUSE:
                m_pause = true;
            break;

            case TIMEFACTOR_UPx10:
                m_timeaccelx10 = true;
                //System.out.println("Speed up!");
            break;

            case TIMEFACTOR_DOWNx10:
                m_timeacceldiv10 = true;
                //System.out.println("Slow down!");
            break;

            case THRUST_SMART_TOGGLE:
                m_smarttoggle = true;
            break;

            case CYCLE_INTERFACE_LEVEL:
                m_cycle_interface_level = true;
            break;

            case RESET:
                System.out.println("RESET!");
                m_reset_scenario = true;
            break;

            case TOGGLE_RADAR:
                m_toggle_radar = true;
            break;
            
            case TOGGLE_RCS_ROTATION_MODE:
                m_toggle_rotation_mode_raw = true;
            break;
            
            case CYCLE_AUTOPILOT_MODE:               
                m_cycle_autopilot_mode_raw = true;
            break;
            
            case TOGGLE_AUTOPILOT:
                m_toggle_autopilot_raw = true;
            break;
            
            case ZOOM_TARGET:
                m_zoom_target = true;
                m_zoom_surface = false;
                m_zoom_full = false;
                m_zoom_lm_moon = false;
            break;
            
            case ZOOM_SURFACE:
                m_zoom_target = false;
                m_zoom_surface = true;
                m_zoom_full = false;
                m_zoom_lm_moon = false;
            break;
            
            case ZOOM_FULL:
                m_zoom_target = false;
                m_zoom_surface = false;
                m_zoom_full = true;
                m_zoom_lm_moon = false;
            break;
            
            case ZOOM_LM_MOON:
                m_zoom_target = false;
                m_zoom_surface = false;
                m_zoom_full = false;
                m_zoom_lm_moon = true;
            break;
            
            case HELP:
                m_help = true;
            break;
        }
        computeCommands();
    }

    public void keyTyped(KeyEvent e) {
//         ControlAction action = findBinding(e.getKeyCode());
//
//         switch (action)
//         {
//         }
//         computeCommands();
    }

    public double getCommandedYawRate()
    {
        return m_yaw_rate;
    }

    public double getCommandedRCSRate()
    {
        return m_rcs_rate;
    }

    public double getCommandedThrottleRate()
    {
        return m_throttle_rate;
    }

    public ThrottleCmd getThrottleCommand()
    {
        ThrottleCmd rv = m_throttle_cmd;
        if (rv != ThrottleCmd.USE_RATE)
            m_throttle_cmd = ThrottleCmd.NONE;
        return rv;
    }

    public boolean getPaused()
    {
        return m_pause_game;
    }

    public boolean getHelp()
    {
        return m_show_help;
    }

    public int getTimeAcceleration()
    {
        return m_time_acceleration;
    }

    public boolean getCycleInterfaceState()
    {
        boolean rv = m_do_cycle_interface;
        m_do_cycle_interface = false;
        return rv;
    }

    public boolean getPrintStatus()
    {
        boolean rv = m_print_status;
        m_print_status = false;
        return rv;
    }
    
    public boolean getExplosion()
    {
        boolean rv = m_explode;
        m_explode = false;
        return rv;
    }

    public boolean getResetInterfaceState()
    {
        boolean rv = m_reset_scenario;
        m_reset_scenario = false;
        return rv;
    }

    public boolean GetRadarOn()
    {
        return m_landing_radar_on;
    }

    public void SetRadarOn(boolean val)
    {
        m_landing_radar_on = val;
    }
    
    public boolean GetToggleRCSRotationMode()
    {
        boolean returnValue = m_toggle_rotation_mode;
        m_toggle_rotation_mode = false;
        return returnValue;
    }
    
    public boolean GetCycleAutopilotMode()
    {
        boolean returnValue = m_cycle_autopilot_mode;
        m_cycle_autopilot_mode = false;
        return returnValue;
    }
    
    public boolean GetToggleAutopilot()
    {
        boolean returnValue = m_toggle_autopilot;
        m_toggle_autopilot = false;
        return returnValue;
    }
    
    public ScalingMode GetScalingMode()
    {
        if (m_zoom_surface)
        {
            return ScalingMode.LM_SURFACE;
        }
        else if (m_zoom_target)
        {
            return ScalingMode.LM_TARGET;
        }
        else if (m_zoom_full)
        {
            return ScalingMode.LM_MAX;
        }
        else if (m_zoom_lm_moon)
        {
            return ScalingMode.LM_ALL_MOON;
        }
        return ScalingMode.LM_TARGET;
    }

    private ControlAction findBinding(int keyCode)
    {
        for (int i=0 ; i<m_bindings.size() ; ++i)
        {
            if (m_bindings.get(i) == null)
                continue;

            if (m_bindings.get(i).getKeyCode() == keyCode)
                return m_bindings.get(i).getAction();
        }
        return ControlAction.NULL;
    }

    // Once the key events have been processed,
    // compute the actual commands which the user
    // is issuing.
    private void computeCommands()
    {
        // Do not accept any input while paused, except unpause
        // and quit.
        if (!m_pause_game)
        {
            int turnvalue = 0;
            if (m_turnleft) ++turnvalue;
            if (m_turnright) --turnvalue;

            int translatevalue = 0;
            if (m_rcsright) ++translatevalue;
            if (m_rcsleft) --translatevalue;

            int throttlevalue = 0;
            if (m_throttleup) ++throttlevalue;
            if (m_throttledown) --throttlevalue;

            double gain = 1.0;
            if (m_trim)
                gain = 0.2;

            m_yaw_rate = turnvalue * gain;
            m_rcs_rate = translatevalue * 0.01 * gain;

            if (m_smarttoggle)
            {
                m_throttle_cmd = ThrottleCmd.SMART_TOGGLE;
            }
            else
            {
                m_throttle_rate = throttlevalue * 0.5 * gain;
                if (throttlevalue == 0)
                    m_throttle_cmd = ThrottleCmd.NONE;
                else
                    m_throttle_cmd = ThrottleCmd.USE_RATE;

                if (m_fullthrottle)
                    m_throttle_cmd = ThrottleCmd.FULL;

                if (m_killthrottle)
                    m_throttle_cmd = ThrottleCmd.KILL;
            }
            m_smarttoggle = false;

            if (m_toggle_radar)
            {
                m_toggle_radar = false;
                m_landing_radar_on = !m_landing_radar_on;
            }

            int accelval = m_time_acceleration;
            if (m_timeaccelx10) accelval *= 10;
            if (m_timeacceldiv10) accelval /= 10;

            if (accelval < 1)
                accelval = 1;
            if (accelval > 1000)
                accelval = 1000;

            m_time_acceleration = accelval;

            m_timeaccelx10 = m_timeacceldiv10 = false;

            m_yaw_rate /= m_time_acceleration;
            m_rcs_rate /= m_time_acceleration;
            m_throttle_rate /= m_time_acceleration;
            
            if (m_toggle_rotation_mode_raw)
            {
                m_toggle_rotation_mode_raw = false;
                m_toggle_rotation_mode = true;
            }
            else
            {
                m_toggle_rotation_mode = false;
            }
            
            if (m_cycle_autopilot_mode_raw)
            {
                m_cycle_autopilot_mode_raw = false;
                m_cycle_autopilot_mode = true;
            }
            else
            {
                m_cycle_autopilot_mode = false;
            }
            
            if (m_toggle_autopilot_raw)
            {
                m_toggle_autopilot_raw = false;
                m_toggle_autopilot = true;
            }
            else
            {
                m_toggle_autopilot = false;
            }
        }

        if (m_cycle_interface_level)
        {
            m_do_cycle_interface = true;
            m_cycle_interface_level = false;
        }
        else
        {
            m_do_cycle_interface = false;
        }

        if (m_pause)
        {
            m_pause = false;
            m_pause_game = !m_pause_game;

            if (!m_pause_game)
            {
                m_show_help = false;
            }
        }

        if (m_help)
        {
            m_help = false;
            m_show_help = !m_show_help;

            m_pause_game = m_show_help;
        }
    }

    public String getKeyBindingsDescription()
    {
        StringBuilder sb = new StringBuilder();

        sb.append("\nKey bindings:\n=============\n");
        for (int i=0 ; i<m_bindings.size() ; ++i)
        {
            ControlBinding b = m_bindings.get(i);
            int keyCode = b.getKeyCode();
            String keyDesc = KeyEvent.getKeyText(keyCode);
            String controlName = b.getAction().getName();
            String controlDesc = b.getAction().getDesc();
            sb.append(keyDesc);
            sb.append("\t");
            sb.append(controlName);
            sb.append("\t");
            sb.append(controlDesc);
            sb.append("\n");
        }
        return sb.toString();
    }
    
    private static String spacesbuf;
    private String nSpaces(int n)
    {
        if ((spacesbuf == null)||(spacesbuf.length() < n))
        {
            char[] cbuf = new char[n*2];
            for (int i=0; i<cbuf.length ; ++i)
                cbuf[i] = ' ';
            spacesbuf = new String(cbuf);           
        }
        return spacesbuf.substring(0, n);
    }
    
    public String getKeyBindingsDescription2(boolean longForm)
    {
        StringBuilder sb = new StringBuilder();
        
        int columnWidths[] = {0,0,0};
        int i;
        int len;
        int keyCode;
        String keyDesc;
        String controlName;
        String controlDesc;
        ControlBinding b;
        
        for (i=0; i<m_bindings.size(); ++i)
        {
            b = m_bindings.get(i);
            keyCode = b.getKeyCode();
            keyDesc = KeyEvent.getKeyText(keyCode);
            len = keyDesc.length();
            if (len > columnWidths[0])
                columnWidths[0] = len;
            controlName = b.getAction().getName();
            len = controlName.length();
            if (len > columnWidths[1])
                columnWidths[1] = len;
            controlDesc = b.getAction().getDesc();
            len = controlDesc.length();
            if (len > columnWidths[2])
                columnWidths[2] = len;
        }
        
        for (i=0 ; i<m_bindings.size() ; ++i)
        {
            b = m_bindings.get(i);
            keyCode = b.getKeyCode();
            keyDesc = KeyEvent.getKeyText(keyCode);
            controlName = b.getAction().getName();
            controlDesc = b.getAction().getDesc();
            
            sb.append(keyDesc);            
            len = columnWidths[0] - keyDesc.length() + 1;
            if (len > 0)
                sb.append(nSpaces(len));
            
            sb.append(controlName);            
            len = columnWidths[1] - controlName.length() + 1;
            if (len > 0)
                sb.append(nSpaces(len));

            if (longForm)
            {
                sb.append(controlDesc);           
                len = columnWidths[2] - controlDesc.length() + 1;
                if (len > 0)
                    sb.append(nSpaces(len));
            }
            
            sb.append("\n");
        }
        return sb.toString();
    }

    private void describeKeyBindings()
    {
        System.out.println(getKeyBindingsDescription2(true));
    }

    // Raw switch states:
    protected   boolean     m_turnleft;
    protected   boolean     m_turnright;
    protected   boolean     m_rcsleft;
    protected   boolean     m_rcsright;
    protected   boolean     m_trim;
    protected   boolean     m_throttleup;
    protected   boolean     m_throttledown;
    protected   boolean     m_fullthrottle;
    protected   boolean     m_killthrottle;
    protected   boolean     m_smarttoggle;
    protected   boolean     m_pause;
    protected   boolean     m_help;
    protected   boolean     m_timeaccelx10;
    protected   boolean     m_timeacceldiv10;
    protected   boolean     m_cycle_interface_level;
    protected   boolean     m_toggle_radar;
    protected   boolean     m_toggle_rotation_mode_raw;
    protected   boolean     m_cycle_autopilot_mode_raw;
    protected   boolean     m_toggle_autopilot_raw;

    // final outputs
    protected   double      m_yaw_rate; // degrees/sec, positive = ccw, negative = cw
    protected   double      m_rcs_rate; // m/s/s, positive = right, negative = left
    protected   double      m_throttle_rate; // 0.0-1.0 per second
    protected   ThrottleCmd   m_throttle_cmd = ThrottleCmd.NONE;
    protected   boolean     m_landing_radar_on = true;
    protected   int         m_time_acceleration = 1;
    private     boolean     m_do_cycle_interface = false;
    private     boolean     m_reset_scenario = false;
    protected   boolean     m_pause_game = false;
    private     boolean     m_show_help = false;
    private     boolean     m_print_status = false;
    private     boolean     m_explode = false;
    protected   boolean     m_toggle_rotation_mode = false;
    protected   boolean     m_cycle_autopilot_mode = false;
    protected   boolean     m_toggle_autopilot = false;
    protected   boolean     m_zoom_target = true;
    protected   boolean     m_zoom_surface = false;
    protected   boolean     m_zoom_full = false;
    protected   boolean     m_zoom_all = false;
    protected   boolean     m_zoom_lm_moon = false;

    // bindings from keyboard to actions.
    private ArrayList<ControlBinding> m_bindings = new ArrayList<ControlBinding>(20);

    public void addListener(ILMControlsListener listener)
    {
        listeners.add(listener);
    }

    public void send()
    {
        int nlisteners = listeners.size();
        for (int index = 0 ; index < nlisteners ; index++)
        {
            ILMControlsListener listener = listeners.get(index);
            listener.listen(this);
        }
    }

    private ArrayList<ILMControlsListener> listeners = new ArrayList<ILMControlsListener>(10);
}