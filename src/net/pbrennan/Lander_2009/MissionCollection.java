package net.pbrennan.Lander_2009;
// MissionCollection.java
import java.io.FileReader;
import java.util.ArrayList;

public class MissionCollection
{
    public int nMissions()
    {
        if (m_missions == null)
            return 0;
        else
            return m_missions.length;
    }

    public Mission getMission(int num)
    {
        if ((num < 1)||(num > nMissions())||(m_missions == null))
            return (Mission)null;

        num--;
        return m_missions[num];
    }

    public MissionCollection()
    {
        System.out.println("Loading Missions...");

        FileReader fReader;
        CSVReader csvReader;

        try
        {
            String filename = "data/Missions.csv";
            fReader = new FileReader(filename);
            csvReader = new CSVReader(fReader, ',', '\"', 1);
            ArrayList<Mission> missionList = new ArrayList<Mission>();
            String[] line;
            int i = 1;
            while ((line = csvReader.readNext()) != null)
            {
                Mission newMission = new Mission(line);
                System.out.println("\nMission " + i + "\n" + newMission);
                missionList.add(newMission);
                ++i;
            }
            csvReader.close();
            fReader.close();

            m_missions = new Mission[missionList.size()];
            for (int j=0; j<m_missions.length ; ++j)
                m_missions[j] = missionList.get(j);

        }
        catch (Exception e)
        {
            System.out.println("Error in MissionCollection : " + e);
            m_missions = null;
            return;
        }
    }

    private Mission[] m_missions;
}