package main;

import javax.swing.JPanel;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable
{
    public static final int WIDTH = 1280; //게임 패널의 너비
    public static final int HEIGHT = 720; //게임 패널의 높이
    final int FPS = 60; // 화면을 매 초 60번 갱신하기 위함.
    Thread game_thread = new Thread(); // Game Loop 를 실행하기 위해 쓰레드 사용, 객체 생성
    PlayManager play_manager = new PlayManager(); // 객체 생성

    public GamePanel()
    {
        //설정
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.black); //배경색: 검정
        this.setLayout(null); // 레이아웃 커스텀

        //KeyListener 시행
        this.addKeyListener(new KeyHandler());
        this.setFocusable(true);

        play_manager = new PlayManager();
    }

    //쓰레드
    public void launchGame()
    {
        game_thread = new Thread(this);
        game_thread.start();
    }

    @Override
    public void run()
    {
        // 게임 루프를 구현

        // FPS(Frame Per Second)를 기준으로 그리기 간격을 나노초 단위로 계산
        double drawInterval = 1000000000/FPS;
        double delta = 0;

        // 마지막으로 측정한 시간(나노초 단위)을 저장
        long lastTime = System.nanoTime();
        long currentTime;

        // 게임 루프가 실행 중일 때
        while(game_thread != null)
        {
            // 현재 시간을 나노초 단위로 측정
            currentTime = System.nanoTime();

            // 지난 프레임과 현재 프레임 사이의 시간을 계산하여 delta에 추가
            delta += (currentTime - lastTime) / drawInterval;

            // 현재 시간을 마지막 시간으로 업데이트
            lastTime = currentTime;

            // delta가 1을 초과하면 다음 프레임을 그릴 시간이 되었음을 의미
            if (delta > 1)
            {
                update(); // 게임 상태를 업데이트
                repaint(); // 화면을 다시 그리기
                delta--; // delta를 감소시켜 다음 프레임을 대기
            }
        }
    }


    private void update()
    {
        if (KeyHandler.pausePressed == false && play_manager.gameOver == false)
        {
            play_manager.update();
        }
    }


    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g_2D = (Graphics2D)g;
        play_manager.draw(g_2D);
    }
}
