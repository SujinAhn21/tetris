package main;

import javax.swing.JFrame;

public class Main
{
    public static void main(String[] args)
    {
        JFrame window = new JFrame("Tetris");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false); //window 크기 고정.

        //window에 GamePanel 연결
        GamePanel game_panel = new GamePanel(); //객체 생성
        window.add(game_panel);
        window.pack(); //GamePanel의 크기가 window의 크기가 되도록 설정함.

        window.setLocationRelativeTo(null); // window를 화면의 중앙에 배치.
        window.setVisible(true);

        // 게임 루프 시작
        game_panel.launchGame();
    }
}