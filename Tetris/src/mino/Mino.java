package mino;

import main.KeyHandler;
import main.PlayManager;

import java.awt.*;

public class Mino {
    public Block b[] = new Block[4];
    public Block tempB[] = new Block[4];
    int autoDropCounter = 0;
    public int direction = 1; // 4가지 방향(1/2/3/4), 로테이션
    boolean leftCollision, rightCollision, bottomCollision;
    public boolean active = true;
    public boolean deactivating;
    int deactivateCounter = 0;

    public void create(Color c) {
        b[0] = new Block(c);
        b[1] = new Block(c);
        b[2] = new Block(c);
        b[3] = new Block(c);

        tempB[0] = new Block(c);
        tempB[1] = new Block(c);
        tempB[2] = new Block(c);
        tempB[3] = new Block(c);
    }

    public void setXY(int x, int y) {
        // 위치 설정 코드
    }

    public void updateXY(int direction) {
        checkRotationCollision();

        if (!leftCollision && !rightCollision && !bottomCollision) {
            this.direction = direction;
            b[0].x = tempB[0].x;
            b[0].y = tempB[0].y;
            b[1].x = tempB[1].x;
            b[1].y = tempB[1].y;
            b[2].x = tempB[2].x;
            b[2].y = tempB[2].y;
            b[3].x = tempB[3].x;
            b[3].y = tempB[3].y;
        }
    }

    // 방향 함수 4개 생성
    public void getDirection1() {
        // 방향1 코드
    }

    public void getDirection2() {
        // 방향2 코드
    }

    public void getDirection3() {
        // 방향3 코드
    }

    public void getDirection4() {
        // 방향4 코드
    }

    // 블록이 움직일 때의 충돌
    public void checkMovementCollision() {
        leftCollision = false;
        rightCollision = false;
        bottomCollision = false;

        // static block 충돌 감지
        checkStaticBlockCollision();

        // frame 충돌 감지
        // 왼쪽 벽
        for (int i = 0; i < b.length; i++) {
            if (b[i].x == PlayManager.left_x) {
                leftCollision = true;
            }
        }

        // 오른쪽 벽
        for (int i = 0; i < b.length; i++) {
            if (b[i].x + Block.SIZE == PlayManager.right_x) {
                rightCollision = true;
            }
        }

        // 바닥
        for (int i = 0; i < b.length; i++) {
            if (b[i].y + Block.SIZE == PlayManager.bottom_y) {
                bottomCollision = true;
            }
        }
    }

    // 블록이 회전할 때의 충돌
    public void checkRotationCollision() {
        leftCollision = false;
        rightCollision = false;
        bottomCollision = false;

        // static block 충돌 감지
        checkStaticBlockCollision();

        // frame 충돌 감지
        // 왼쪽 벽
        for (int i = 0; i < tempB.length; i++) {
            if (tempB[i].x < PlayManager.left_x) {
                leftCollision = true;
                break; // 충돌이 감지되면 반복 종료
            }
        }

        // 오른쪽 벽
        for (int i = 0; i < tempB.length; i++) {
            if (tempB[i].x + Block.SIZE > PlayManager.right_x) {
                rightCollision = true;
                break; // 충돌이 감지되면 반복 종료
            }
        }

        // 바닥
        for (int i = 0; i < tempB.length; i++) {
            if (tempB[i].y + Block.SIZE > PlayManager.bottom_y) {
                bottomCollision = true;
                break; // 충돌이 감지되면 반복 종료
            }
        }
    }

    private void checkStaticBlockCollision()
    {
        for (int i = 0; i < PlayManager.staticBlocks.size(); i++)
        {
            int targetX = PlayManager.staticBlocks.get(i).x;
            int targetY = PlayManager.staticBlocks.get(i).y;

            // check down
            for (int j = 0; j < b.length; j++)
            {
                if (b[j].y + Block.SIZE == targetY && b[j].x == targetX)
                {
                    bottomCollision = true;
                }
            }

            // check left
            for (int j = 0; j < b.length; j++)
            {
                if (b[j].x - Block.SIZE == targetX && b[j].y == targetY)
                {
                    leftCollision = true;
                }
            }

            // check right
            for (int j = 0; j < b.length; j++)
            {
                if (b[j].x + Block.SIZE == targetX && b[j].y == targetY)
                {
                    rightCollision = true;
                }
            }
        }
    }

    public void update() {

        if (deactivating)
        {
            deactivating();
        }

        // 블록 움직이기
        if (KeyHandler.upPressed) {
            switch (direction) {
                case 1:
                    getDirection2();
                    break;
                case 2:
                    getDirection3();
                    break;
                case 3:
                    getDirection4();
                    break;
                case 4:
                    getDirection1();
                    break;
            }

            KeyHandler.upPressed = false;
        }

        checkMovementCollision(); // 움직임 충돌 감지 먼저..

        if (KeyHandler.downPressed) {
            // 블록이 어디 부딪치지 않아야만 가능.
            if (!bottomCollision) {
                b[0].y += Block.SIZE;
                b[1].y += Block.SIZE;
                b[2].y += Block.SIZE;
                b[3].y += Block.SIZE;

                // 아래로 내려왔으면, autoDropCounter 리셋함.
                autoDropCounter = 0;
            }

            KeyHandler.downPressed = false;
        }

        if (KeyHandler.leftPressed) {
            if (!leftCollision) {
                b[0].x -= Block.SIZE;
                b[1].x -= Block.SIZE;
                b[2].x -= Block.SIZE;
                b[3].x -= Block.SIZE;
            }

            KeyHandler.leftPressed = false;
        }

        if (KeyHandler.rightPressed) {
            if (!rightCollision) {
                b[0].x += Block.SIZE;
                b[1].x += Block.SIZE;
                b[2].x += Block.SIZE;
                b[3].x += Block.SIZE;
            }

            KeyHandler.rightPressed = false;
        }

        if (bottomCollision)
        {
            deactivating = true;
        }
        else
        {
            autoDropCounter++; // 게임 루프가 실행될 때마다 카운터가 1씩 증가함.
            if (autoDropCounter == PlayManager.dropInterval) {
                // 블록이 아래로 내려감
                b[0].y += Block.SIZE;
                b[1].y += Block.SIZE;
                b[2].y += Block.SIZE;
                b[3].y += Block.SIZE;
                autoDropCounter = 0;
            }
        }
    }

    private void deactivating()
    {
        deactivateCounter++;

        // 비활성화 될 때까지 45 frame 동안 기다림.
        if (deactivateCounter == 45)
        {
            deactivateCounter = 0; // 0으로 초기화함.
            checkMovementCollision(); // 밑바닥이 계속 부딪치고 있는지 확인

            // 45 frame 이후에도 밑바닥이 계속 부딪치고 있다면, 블록을 비활성화 시킴.
            if (bottomCollision)
            {
                active = false;
            }
        }
    }

    public void draw(Graphics2D g_2D) {
        int margin = 2; // 2픽셀 margin
        g_2D.setColor(b[0].c);
        g_2D.fillRect(b[0].x + margin, b[0].y + margin, Block.SIZE - (margin * 2), Block.SIZE - (margin * 2));
        g_2D.fillRect(b[1].x + margin, b[1].y + margin, Block.SIZE - (margin * 2), Block.SIZE - (margin * 2));
        g_2D.fillRect(b[2].x + margin, b[2].y + margin, Block.SIZE - (margin * 2), Block.SIZE - (margin * 2));
        g_2D.fillRect(b[3].x + margin, b[3].y + margin, Block.SIZE - (margin * 2), Block.SIZE - (margin * 2));
    }
}
