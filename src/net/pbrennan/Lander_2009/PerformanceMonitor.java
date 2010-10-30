package net.pbrennan.Lander_2009;
public class PerformanceMonitor
{
    public PerformanceMonitor()
    {
        reset();
    }

    public void reset()
    {
        m_eventCounter = 0;
        m_startTime = System.currentTimeMillis();
    }

    synchronized public void putEvent()
    {
        putEvents(1);
    }

    synchronized public void putEvents(int nEvents)
    {
        m_eventCounter += nEvents;
    }

    synchronized public void putSleepTime(long inSleepTime)
    {
        m_sleepTime = inSleepTime;
    }

    synchronized public long getSleepTime()
    {
        //System.out.println(m_sleepTime);
        return m_sleepTime;
    }

    synchronized public double getEventsPerSecond()
    {
        double elapsedTime = ((double)(System.currentTimeMillis() - m_startTime)) / 1000.0;
        double rv = ((double)(m_eventCounter)) / elapsedTime;

        return rv;
    }

    private long m_eventCounter;
    private long m_startTime;
    private long m_sleepTime;
}