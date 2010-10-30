package net.pbrennan.Lander_2009;
// --------------------------------------------------------------------
//
// TestLMRunner2
//
// invoked as:
// java [-classpath <classpath>] TestLMRunner2 [-mission=n] [-level=m]
//
// --------------------------------------------------------------------

import javax.swing.JFrame;

import net.pbrennan.Lander_2009.LunarSpacecraft2D.Status;

import java.awt.AWTEvent;
import java.awt.event.WindowEvent;
import java.awt.Color;
import java.awt.FlowLayout;

public class TestLMRunner2 extends JFrame implements Runnable
{
    /**
	 *
	 */
	private static final long serialVersionUID = 2959138309093055826L;
	public static void main(String[] arg)
    {
        TestLMRunner2 instance = new TestLMRunner2(arg);

        instance.start();
    }

    private boolean printHelpAndExit = false;
    public int parseIntegerArgument(String s)
    {
        int rv = -1;
        String[] sarray = s.split("=");
        if (sarray.length != 2)
        {
            //System.out.println("Couldn't split " + s);
            printHelpAndExit = true;
            return -1;
        }
        try
        {
            //System.out.println("Trying to parse " + sarray[1] + " into an integer");
            rv = Integer.parseInt(sarray[1]);
        }
        catch (NumberFormatException e)
        {
            //System.out.println("Got a Number Format exception: " + e);
            printHelpAndExit = true;
            return -1;
        }
        return rv;
    }
    
    private int userWidth = -1;
    private int userHeight = -1;
    private boolean parseGeometryArgument(String s)
    {
        String[] sarray1 = s.split("=");
        if (sarray1.length != 2)
        {
            return false;
        }
        String[] sarray2 = sarray1[1].split("x");
        if (sarray2.length != 2)
        {
            return false;
        }
        try
        {
            userWidth = Integer.parseInt(sarray2[0]);
            
            if (userWidth < SideView4.MIN_WIDTH)
                userWidth = SideView4.MIN_WIDTH;
            else if (userWidth > SideView4.MAX_WIDTH)
                userWidth = SideView4.MAX_WIDTH;            
            
            userHeight = Integer.parseInt(sarray2[1]);
            
            if (userHeight < SideView4.MIN_HEIGHT)
                userHeight = SideView4.MIN_HEIGHT;
            else if (userHeight > SideView4.MAX_HEIGHT)
                userHeight = SideView4.MAX_HEIGHT;
            
            return true;
        }
        catch (NumberFormatException e)
        {
            //System.out.println("Got a Number Format exception: " + e);
            printHelpAndExit = true;
            return false;
        }
    }

    public void parseArguments(String[] args)
    {
        // Set defaults here
        m_missionNum = 1;
        m_level = 0;

        for (int i=0 ; i<args.length ; ++i)
        {
            if (args[i].startsWith("-mission="))
            {
                m_missionNum = parseIntegerArgument(args[i]);
                System.out.println("Mission Number set to " + m_missionNum);
            }
            else if (args[i].startsWith("-level="))
            {
                m_level = parseIntegerArgument(args[i]);
                System.out.println("Level set to " + m_level);
            }
            else if (args[i].startsWith("-geometry="))
            {
                if (!parseGeometryArgument(args[i]))
                    printHelpAndExit = true;
            }
            else
            {
                printHelpAndExit = true;
            }
        }
        if (printHelpAndExit)
        {
            System.out.println("Usage:\njava TestLMRunner2 [-mission=n] [-level=m] [-geometry=<width>x<height>]");
            System.out.println("      width must be >= " + SideView4.MIN_WIDTH + " and <= " + SideView4.MAX_WIDTH);
            System.out.println("      height must be >= " + SideView4.MIN_HEIGHT + " and <= " + SideView4.MAX_HEIGHT);
            System.exit(0);
        }

    }

    public void resetScenario()
    {
        m_lm.Initialize(m_missions.getMission(m_missionNum));
        m_sideview.resetParticles();
    }

    public TestLMRunner2(String [] arg)
    {
        parseArguments(arg);
        
        setLayout(new FlowLayout(FlowLayout.LEFT,3,3));
        setBackground(Color.darkGray);

        m_thread = new Thread(this);
        m_thread.setPriority(Thread.MAX_PRIORITY);

        m_data = new LMInstrumentData();

        m_sideview = new SideView4(userWidth,userHeight);
        m_data.addListener(m_sideview);
        LMEventSource.getInstance().addListener(m_sideview);

        System.out.println("creating a LMRunner...");
        
        m_runner = new LMRunner();
        m_runner.setTimeFactor(1);

        System.out.println("creating a LunarSpacecraft2D...");

        m_lm = new LunarSpacecraft2D();
        m_runner.setLM(m_lm);

        System.out.println("creating a Terrain...");

        m_lm.SetTerrain(new Terrain());
        m_sideview.SetTerrain(m_lm.GetTerrain());

        m_missions = new MissionCollection();

        // TODO: Allow the user to select a target interactively.
        // TODO: The setting of the target also drives setting the
        // DOI cue and the PDI cue.
        //m_lm.SetTargetLong(Math.toRadians(252.0));

        m_controls = new LMControls();
        addKeyListener(m_controls);
        m_sideview.addKeyListener(m_controls);
        m_sideview.addKeyBindingsString(m_controls.getKeyBindingsDescription2(false));

        m_soundMgr = new SoundManager();
        m_data.addListener(m_soundMgr);
        LMEventSource.getInstance().addListener(m_soundMgr);

        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        setTitle("LM Runner Test");
        setResizable(false);
    }

    public void processWindowEvent(WindowEvent event)
    {
        if (event.getID() == WindowEvent.WINDOW_CLOSING)
        {
            System.exit(0);
        }
    }

    public void start()
    {
        resetScenario();
        add(m_sideview);
        pack();
        setVisible(true);

        if (m_thread != null)
            m_thread.start();
    }

    public void run()
    {
        try
        {
            boolean help_lastFrame = false;
            boolean help_thisFrame = false;
            boolean paused_lastFrame = false;
            boolean paused_thisFrame = false;
            while (true)
            {
                m_lm.SetRotationAccel(Math.toRadians(m_controls.getCommandedYawRate()));
                m_lm.SetTranslationAccel(m_controls.getCommandedRCSRate());
                m_lm.SetThrottleCommand(m_controls.getThrottleCommand());
                m_lm.SetThrottleRate(m_controls.getCommandedThrottleRate());
                if (m_controls.GetToggleRCSRotationMode())
                {
                    m_lm.ToggleRCSRotationMode();
                }
                if (m_controls.GetCycleAutopilotMode())
                {
                    m_lm.CycleAutopilotMode();
                }
                if (m_controls.GetToggleAutopilot())
                {
                    m_lm.SetAutopilotOn(!(m_lm.GetAutopilotOn()));
                }

                if (m_controls.getPrintStatus())
                {
                    System.out.println(m_lm.MissionState());
                }
                
                m_soundMgr.setMute(m_controls.getPaused());

                paused_lastFrame = paused_thisFrame;
                paused_thisFrame = m_controls.getPaused();
                help_lastFrame = help_thisFrame;
                help_thisFrame = m_controls.getHelp();
                
                m_runner.setPaused(paused_thisFrame);
                m_runner.setTimeFactor(m_controls.getTimeAcceleration());

                if (m_controls.getResetInterfaceState())
                {
                    resetScenario();
                }

                m_runner.tick(0.05);

                if (!paused_lastFrame)
                {
                    m_data = m_runner.getInstrumentData(m_data);
                    m_data.SetRadarOn(m_controls.GetRadarOn());
                    m_data.send();
                    m_sideview.setScalingMode(m_controls.GetScalingMode());
                    
                    if (m_lm.GetStatus() == Status.Exploding)
                    {
                        /*m_sideview.m_particles.spawnVerticalBias(
                                0.0, 0.0, 1.0, 
                                0.0, 0.0, 
                                300.0, 30.0);*/
                        
                        double terrainNormalAngle = m_lm.GetTerrainNormalAngle();
                        double currentTheta = m_lm.GetTheta();
                        double offset = Math.PI * 0.5;
                        
                        System.out.println("Terrain Normal = " + terrainNormalAngle);
                        System.out.println("Current Theta = " + currentTheta);
                        System.out.println("offset = " + offset);
                        
                        m_sideview.m_particles.spawnWCentralAngle(
                                terrainNormalAngle - currentTheta  + offset , 
                                0.0, 0.0, 1.0, 
                                0.0, 0.0, 
                                300.0, 30.0);
                        
                        m_lm.SetStatus(Status.Dead);
                    }
                    
                    m_sideview.setHelpDisplay(help_thisFrame);
                    m_sideview.repaint();
                }
                
                Thread.sleep(50);
            }
        }
        catch (Exception e)
        {
            return;
        }
    }

    // Game options
    private int                 m_missionNum;   // The scenario to load
    private int                 m_level;        // The difficulty level.

    // Game objects to manage.
    private Thread              m_thread;       // The main thread.

    private SideView4           m_sideview;     // The side view of the action, the "main window"
    private SoundManager        m_soundMgr;

    private MissionCollection   m_missions;     // The missions.

    private LMInstrumentData    m_data;         // The data which feeds the instruments
    private LMRunner            m_runner;       // The object which runs the physics loop in its own thread.
    private LMControls          m_controls;     // The control state, gets input and puts it into a form to be used.
    private LunarSpacecraft2D   m_lm;           // The player's ship.
}