package net.pbrennan.Lander_2009;
import java.util.ArrayList;

public class LMEventSource
{
    // Send out an event.
    public static void dispatchEvent(LMEvent e)
    {
        if (instance == null)
            return;

        int nlisteners = instance.listeners.size();
        for (int index = 0 ; index < nlisteners ; index++)
        {
            ILMEventListener listener = instance.listeners.get(index);
            listener.listen(e);
        }
    }

    public void addListener(ILMEventListener l)
    {
        instance.listeners.add(l);
    }

    private LMEventSource()
    {
    }

    public static LMEventSource getInstance()
    {
        if (instance == null)
            instance = new LMEventSource();

        return instance;
    }

    static private LMEventSource instance = new LMEventSource();

    private ArrayList<ILMEventListener> listeners = new ArrayList<ILMEventListener>(10);
}