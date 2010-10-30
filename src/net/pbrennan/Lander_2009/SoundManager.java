package net.pbrennan.Lander_2009;
import javax.sound.sampled.*;
import java.io.File;

class SoundManager implements ILMInstrumentDataListener, ILMEventListener
{
    public enum SoundIndex
    {
        ROCKET("RocketSound.wav"),
        EAGLE("a11landing.wav"),
        PROBLEM("problem2.wav"),

        N_SOUNDS("");

        SoundIndex(String fn)
        {
            filename = fn;
        }

        public String filename;
    }

    Clip [] m_Clips = new Clip[SoundIndex.N_SOUNDS.ordinal()];
    int [] m_LengthsMs = new int[SoundIndex.N_SOUNDS.ordinal()];
    FloatControl [] m_Vol = new FloatControl[SoundIndex.N_SOUNDS.ordinal()];
    BooleanControl [] m_Mute = new BooleanControl[SoundIndex.N_SOUNDS.ordinal()];
    boolean m_loadedOK;
    
    boolean m_MuteAll = false;

    public void loop(SoundIndex soundIndex)
    {
        m_Clips[soundIndex.ordinal()].loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void play(SoundIndex soundIndex)
    {
        Clip clip = m_Clips[soundIndex.ordinal()];
        clip.setMicrosecondPosition(0);
        clip.loop(0);
    }

    /**
     * Set the sound volume
     * 
     * @param soundIndex
     * @param value : a value between 0.0 and 1.0
     */
    public void setVolume(SoundIndex soundIndex, float value)
    {
        //System.out.println("Setting volume " + soundIndex.ordinal() + " to " + value);
        FloatControl v = m_Vol[soundIndex.ordinal()];
        BooleanControl m = m_Mute[soundIndex.ordinal()];
        
        // Transform the linear volume value into decibels
        // which can be plugged into the sound controls.
        final double epsilon = 0.0001; // Ensure that we never ask for log(0)
        final double logFactor = 5.00; // Scale the value before taking the log.
        final double scale = 20.0;
        
        double DB = scale * Math.log(value * logFactor + epsilon);
        if (DB < -80.0)
            DB = -80.0;
        else if (DB > 13.9794)
            DB = 13.9794;

        if ((value == 0.0)||(m_MuteAll))
        {
            m.setValue(true);
            v.setValue(-80.0f);
        }
        else
        {
            m.setValue(false);
            v.setValue((float)DB);
        }
    }

    private boolean Init()
    {
        Mixer myMixer = null;
        Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
        for (int i=0 ; i<mixerInfo.length ; ++i)
        {
            //System.out.println("Mixer " + i + " Info = " + mixerInfo[i]);
            if (mixerInfo[i].getName() == "Java Sound Audio Engine")
            {
                //System.out.println("Found my mixer!");
                myMixer = AudioSystem.getMixer(mixerInfo[i]);
            }
        }

        //for (int i=0 ; i<SoundIndex.N_SOUNDS.ordinal(); ++i)
        for (SoundIndex si : SoundIndex.values())
        {
            if (si == SoundIndex.N_SOUNDS)
                break;

            int i = si.ordinal();
            Clip clip = null;
            AudioInputStream ain = null;
            DataLine.Info info = null;
            FloatControl volumeControl = null;
            BooleanControl muteControl = null;
            int audioLength = 0;

            // Getting a Clip object for a file of sampled audio data is kind
            // of cumbersome.  The following lines do what we need.
            File f = new File("sounds/" + si.filename);
            try
            {
                ain = AudioSystem.getAudioInputStream(f);
                //System.out.println("ain = " + ain);

                info = new DataLine.Info(Clip.class,ain.getFormat( ));
                //System.out.println("clip info = " + info);

                // This line works, but I can't control the gain.
                //clip = (Clip) AudioSystem.getLine(info);

                clip = (Clip)myMixer.getLine(info);
                //System.out.println("clip = " + clip);

                Line.Info [] targetLineInfos = myMixer.getTargetLineInfo();
                for (int j=0 ; j<targetLineInfos.length ; ++j)
                {
                    //System.out.println("target Line " + j + " info = " +
                    //        targetLineInfos[j]);
                }

                // Question: is this master gain only applied to THIS
                // clip?  I don't know.
                volumeControl = (FloatControl)clip.getControl(
                    FloatControl.Type.MASTER_GAIN);
                //System.out.println("My volume control = " + volumeControl);

                muteControl = (BooleanControl)clip.getControl(
                    BooleanControl.Type.MUTE);
                //System.out.println("My mute control = " + muteControl);

                Control[] controls = clip.getControls();
                //System.out.println("Got " + controls.length + " controls");
                for (int j=0 ; j< controls.length ; ++j)
                {
                    //System.out.println(controls[j]);
                    if (controls[j].getType() == FloatControl.Type.MASTER_GAIN)
                        volumeControl = (FloatControl)controls[j];
                }

                clip.open(ain);

                // We're done with the input stream.
                ain.close( );

                // Get the clip length in microseconds and convert to milliseconds
                audioLength = (int)(clip.getMicrosecondLength( )/1000);

                m_Clips[i]      = clip;
                m_LengthsMs[i]  = audioLength;
                m_Vol[i]        = volumeControl;
                m_Mute[i]       = muteControl;

                //setVolume(si, 0.0f);
                //play(si);
            }
            catch (java.lang.NullPointerException e)
            {
                return false;
            }
            catch (java.io.IOException e)
            {
                return false;
            }
            catch (javax.sound.sampled.UnsupportedAudioFileException e)
            {
                return false;
            }
            catch (javax.sound.sampled.LineUnavailableException e)
            {
                return false;
            }
        }

        // loop the engine sound continuously
        loop(SoundManager.SoundIndex.ROCKET);
        setVolume(SoundManager.SoundIndex.ROCKET, 0.0f);

        return true;
    }

    SoundManager()
    {
        m_loadedOK = Init();
    }

    public void listen(LMInstrumentData data)
    {
        if (!m_loadedOK)
            return;

        double throttle = (data.GetThrottlePercent() / 100.0);

        setVolume(SoundIndex.ROCKET, (float)(throttle));
    }    

    public void listen(LMEvent e)
    {
        //System.out.println("Got an event");
        LMLandingEvent le = (LMLandingEvent)e;

        if (le != null)
        {
            //System.out.println("Got a landing event, type = " + le.type);
            switch (le.type)
            {
                case EXCELLENT_LANDING:
                case GOOD_LANDING:
                case HARD_LANDING:
                    setVolume(SoundIndex.EAGLE, 0.25f);
                    play(SoundIndex.EAGLE);
                    break;
                case CRASH_LANDING:
                case CRASH:
                    setVolume(SoundIndex.PROBLEM, 0.25f);
                    play(SoundIndex.PROBLEM);
                    break;
            }
        }
    }
    
    public void setMute(boolean bMute)
    {
        m_MuteAll = bMute;
    }
}
