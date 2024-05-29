package mino;

import java.awt.*;

public class Block extends Rectangle
{
    public int x, y;
    public static final int SIZE = 30; /// 30x30 block
    public Color c;

    public Block(Color c)
    {
        this.c = c;
    }

    public void draw(Graphics2D g_2D)
    {
        int margin = 2;
        g_2D.setColor(c);
        g_2D.fillRect(x+margin, y+margin, SIZE-(margin*2), SIZE-(margin*2));
    }
}
