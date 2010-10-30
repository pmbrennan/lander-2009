package net.pbrennan.Lander_2009;
public class ControlBinding
{
    public ControlBinding(int code, ControlAction action)
    {
        m_KeyCode = code;
        m_Action = action;
    }

    public int getKeyCode() { return m_KeyCode; }
    public ControlAction getAction() { return m_Action; }

    protected int              m_KeyCode;
    protected ControlAction    m_Action;
}