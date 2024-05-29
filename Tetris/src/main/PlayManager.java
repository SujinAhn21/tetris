package main;

import mino.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class PlayManager
{
    //게임을 실행할 영역을 그림 -> 화면에 테트리스 블록들이 놓일 영역 표시.
    //다양한 모양의 테트리스 블록들을 생성, 이동, 회전하는 작업 처리.
    //완성된 줄 삭제, 점수 추가 등의 기능 구현

    //Main Play Area
    final int WIDTH = 360;
    final int HEIGHT = 600;
    public static int left_x;
    public static int right_x;
    public static int top_y;
    public static int bottom_y;

    // Mino
    Mino currentMino;
    final int MINO_START_X;
    final int MINO_START_Y;
    Mino nextMino;
    final int NEXTMINO_X;
    final int NEXTMINO_Y;
    public static ArrayList<Block> staticBlocks = new ArrayList<>();

    // Others
    public static int dropInterval = 60; // 속도 조절 - mino(블록)가 매 60프레임마다 한 칸씩 떨어짐.
    boolean gameOver;

    // Effects
    boolean effectCounterOn;
    int effectCounter;
    ArrayList<Integer> effectY = new ArrayList<>();

    // Score
    int level = 1;
    int lines;
    int score;

    public  PlayManager()
    {
        //Main Play Area Frame
        left_x = (GamePanel.WIDTH/2) - (WIDTH/2); //1280/2 - 360/2 = 460
        right_x = left_x + WIDTH;
        top_y = 50;
        bottom_y = top_y + HEIGHT;

        MINO_START_X = left_x + (WIDTH/2) - Block.SIZE;
        MINO_START_Y = top_y + Block.SIZE;

        NEXTMINO_X = right_x + 175;
        NEXTMINO_Y = top_y + 500;

        // starting Mino 설정
        currentMino = pickMino(); // 만든 블록들 중 랜덤으로 골라져서 나타나도록
        currentMino.setXY(MINO_START_X, MINO_START_Y);
        nextMino = pickMino();
        nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);

    }

    private Mino pickMino()
    {
        // 랜덤으로 내려올 블록을 고름
        Mino mino = null;
        int i = new Random().nextInt(7);

        switch(i)
        {
            case 0:
                mino = new Mino_L1();
                break;
            case 1:
                mino = new Mino_L2();
                break;
            case 2:
                mino = new Mino_Square();
                break;
            case 3:
                mino = new Mino_Bar();
                break;
            case 4:
                mino = new Mino_T();
                break;
            case 5:
                mino = new Mino_Z1();
                break;
            case 6:
                mino = new Mino_Z2();
                break;
        }
        return mino;
    }

    public void update()
    {
        // currentMino가 활성화 되어있는지 확인
        if (currentMino.active == false)
        {
            // 블록이 활성화되어있지 않으면 staticBlocks에 넣음.
            staticBlocks.add(currentMino.b[0]);
            staticBlocks.add(currentMino.b[1]);
            staticBlocks.add(currentMino.b[2]);
            staticBlocks.add(currentMino.b[3]);

            // 게임이 끝났는지 확인함.
            if (currentMino.b[0].x == MINO_START_X && currentMino.b[0].y == MINO_START_Y)
            {
                // currentMino가 블록과 충돌해서 아예 움직일 수 없음을 나타냄.(못 움직임 -> 게임 오버)
                // 그래서 currentMino의 x와 y가 nextMino의 것과 같아짐.
                gameOver = true;
            }

            currentMino.deactivating = false;

            //currentMino를 nextMino로 바꿈.
            currentMino = nextMino;
            currentMino.setXY(MINO_START_X, MINO_START_Y);
            nextMino = pickMino();
            nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);

            //블록이 비활성화되면, 줄(들)이 삭제 될 수 있는지 확인함.
            checkDelete();
        }
        else
        {
            currentMino.update();
        }
    }

    private void checkDelete()
    {
        int x = left_x;
        int y = top_y;
        int blockCount = 0;
        int lineCount = 0;

        while (x < right_x && y < bottom_y)
        {
            for (int i = 0; i < staticBlocks.size(); i++)
            {
                if (staticBlocks.get(i).x == x && staticBlocks.get(i).y == y)
                {
                    //고정된 블록이 있으면 count를 증가시킴.
                    blockCount++;
                }
            }

            x += Block.SIZE;

            if (x == right_x)
            {
                // 만약 blockCount가 12가 되면, 현재 y line이 블록들로 가득 채워져서 삭제할 수 있다는 의미.
                if (blockCount == 12)
                {
                    effectCounterOn = true;
                    effectY.add(y);

                    for (int i = staticBlocks.size()-1; i > -1; i--)
                    {
                        // 현재 y line에 있는 모든 블록들을 제거함.
                        if (staticBlocks.get(i).y == y)
                        {
                            staticBlocks.remove(i);
                        }
                    }

                    lineCount++;
                    lines++;
                    // Drop Speed - 레벨이 오름에 따라 떨어지는 속도 증가시킴.
                    // 만약 line score가 특정 숫자에 다다르면, drop speed(떨어지는 속도)를 증가시킴.
                    // 1이 가장 빠름.
                    if (lines % 10 == 0 && dropInterval > 1)
                    {
                        level++;
                        if (dropInterval > 10)
                        {
                            dropInterval -= 10;
                        }
                        else
                        {
                            dropInterval -= 1;
                        }
                    }

                    // 줄이 삭제됐기 때문에 바로 위에 있던 블록들을 밑으로 내려줘야 함.
                    for (int i = 0; i < staticBlocks.size(); i++)
                    {
                        // 만약 블록이 current y의 위에 있다면, block size에 의해 아래로 내려와야 함.
                        if (staticBlocks.get(i).y < y)
                        {
                            staticBlocks.get(i).y += Block.SIZE;
                        }
                    }
                }

                blockCount = 0;
                x = left_x;
                y += Block.SIZE;
            }
        }

        // Score를 더함.
        if (lineCount > 0)
        {
            int singleLineScore = 10 * level; //현재 레벨 곱하기 10
            score += singleLineScore * lineCount;
        }
    }

    public void draw(Graphics2D g_2D)
    {
        // Draw Play Area Frame
        g_2D.setColor(Color.white);
        g_2D.setStroke(new BasicStroke(4f)); //두께: 4픽셀
        g_2D.drawRect(left_x-4, top_y-4, WIDTH+8, HEIGHT+8); //사각형 그리기

        // Draw Next Mino Frame
        int x = right_x + 100;
        int y = bottom_y - 200;
        g_2D.drawRect(x, y, 200, 200);
        g_2D.setFont(new Font("Arial", Font.PLAIN, 30));
        g_2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g_2D.drawString("NEXT", x+60, y+60);

        // Draw Score Frame
        g_2D.drawRect(x, top_y, 250, 300);
        x += 40;
        y = top_y + 90;
        g_2D.drawString("LEVEL: " + level, x, y); y += 70;
        g_2D.drawString("LINES: " + lines, x, y); y += 70;
        g_2D.drawString("SCORE: " + score, x, y);

        //Draw the currentMino
        if (currentMino != null)
        {
            currentMino.draw(g_2D);
        }

        //Draw the nextMino (다음꺼 뭔지 나타내기)
        nextMino.draw(g_2D);

        //Draw Static Blocks
        for (int i = 0; i < staticBlocks.size(); i++)
        {
            staticBlocks.get(i).draw(g_2D);
        }

        //Draw Effect - 삭제될때 효과
        if (effectCounterOn)
        {
            effectCounter++;

            g_2D.setColor(Color.black); // 검은색
            for (int i = 0; i < effectY.size(); i++)
            {
                g_2D.fillRect(left_x, effectY.get(i), WIDTH, Block.SIZE);
            }

            if (effectCounter == 10) //10 frames로 설정
            {
                effectCounterOn = false;
                effectCounter = 0;
                effectY.clear();
            }
        }

        //Draw Pause 또는 Draw Game Over
        g_2D.setColor(Color.pink); //핑크색
        g_2D.setFont(g_2D.getFont().deriveFont(50f));

        if (gameOver)
        {
            x = left_x + 25;
            y = top_y + 320;
            g_2D.drawString("GAME OVER", x, y);
        }

        else if (KeyHandler.pausePressed)
        {
            x = left_x + 70;
            y = top_y + 320;
            //스페이스 바 누르면 정지되고 'PAUSED' 메시지가 뜸.
            g_2D.drawString("PAUSED", x, y);
        }

        // Draw the Game Title
        x = 35;
        y = top_y + 320;
        g_2D.setColor(Color.white);
        g_2D.setFont(new Font("Times New Roman", Font.ITALIC, 60));
        g_2D.drawString("Simple Tetris", x+20, y);
    }
}
