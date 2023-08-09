package com.example.afor;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BrickBreakerGameView extends View {
    private int screenWidth, screenHeight;
    private int paddleX, ballX, ballY, ballVelX, ballVelY;
    private int paddleWidth, paddleHeight, ballSize;
    private Paint paint;
    private boolean isAnimationRunning = true;
    private TextView scoreTextView;
    private int score = 0;

    private List<Brick> bricks;
    private int numRows = 5;
    private int numCols = 8;

    public BrickBreakerGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(60);
        paddleWidth = 200;
        paddleHeight = 20;
        ballSize = 40;


        bricks = new ArrayList<>();
        // Initialize starting positions and velocities here
        resetGame();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        screenWidth = w;
        screenHeight = h;
        resetGame();
    }

    private void resetGame() {
        isAnimationRunning = false;
        paddleX = (screenWidth - paddleWidth) / 2;
        ballX = screenWidth / 2;
        ballY = screenHeight / 2;

        ballVelX = 15;
        ballVelY = -15;

        // Initialize bricks
        bricks.clear();
        int brickWidth = screenWidth / numCols;
        int brickHeight = screenHeight / 20;
        int brickSpacingX = 5;  // Horizontal spacing between bricks
        int brickSpacingY = 5;  // Vertical spacing between bricks

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                int left = col * brickWidth + col * brickSpacingX;
                int top = row * brickHeight + row * brickSpacingY + 100;
                int right = left + brickWidth;
                int bottom = top + brickHeight;
                bricks.add(new Brick(left, top, right, bottom));
            }
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        Drawable background = getResources().getDrawable(R.drawable.bak); // Replace "your_background_image" with the actual image name
        background.setBounds(0, 0, getWidth(), getHeight());
        background.draw(canvas);
        int[] rowColors = {Color.RED, Color.GREEN, Color.LTGRAY, Color.YELLOW, Color.CYAN}; // Different colors for each row

        for (int row = 0; row < numRows; row++) {
            paint.setColor(rowColors[row]);
            for (int col = 0; col < numCols; col++) {
                Brick brick = bricks.get(row * numCols + col);
                if (!brick.isDestroyed()) {
                    float cornerRadius = 10.0f;  // Adjust the radius for the rounded corners
                    RectF brickRect = new RectF(brick.getLeft(), brick.getTop(), brick.getRight(), brick.getBottom());
                    canvas.drawRoundRect(brickRect, cornerRadius, cornerRadius, paint);
                }
            }
        }
        // Change the color of the paddle (bar) to blue
        paint.setColor(Color.WHITE); // Change to blue

        // Make the paddle (bar) a little taller
        int newPaddleHeight = 30; // Adjust the height as needed
        canvas.drawRoundRect(
                paddleX, screenHeight - newPaddleHeight,
                paddleX + paddleWidth, screenHeight,
                15.0f, 15.0f, // Adjust the corner radii for rounded edges
                paint
        );

        // Draw ball and bricks
        paint.setColor(Color.WHITE); // Color of the ball (red)
        canvas.drawCircle(ballX, ballY, ballSize / 2, paint);



        if (isAnimationRunning) {
            updateGame();
            invalidate(); // Keep redrawing the view while animation is running
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            startAnimation();
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            // Update paddleX based on touch position
            paddleX = (int) event.getX() - paddleWidth / 2;
            invalidate(); // Force redrawing the view
        }
        return true;
    }

    public void startAnimation() {
        isAnimationRunning = true;
        invalidate(); // Force redrawing the view
    }

    public void updateGame() {
        if (!isAnimationRunning) {
            return;
        }

        // Update ball position based on velocity
        ballX += ballVelX;
        ballY += ballVelY;

        // Check for collisions with walls
        if (ballX <= 0 || ballX >= screenWidth) {
            ballVelX *= -1;
        }
        if (ballY <= 0) {
            ballVelY *= -1;
        }

        // Check for collision with paddle
        if (ballY >= screenHeight - paddleHeight - ballSize && ballX >= paddleX && ballX <= paddleX + paddleWidth) {
            ballVelY *= -1;
        }

        // Check for collision with bricks
        for (Brick brick : bricks) {
            if (!brick.isDestroyed() && ballX >= brick.getLeft() && ballX <= brick.getRight() &&
                    ballY >= brick.getTop() && ballY <= brick.getBottom()) {
                brick.setDestroyed(true);
                ballVelY *= -1;
                break; // No need to check other bricks
            }
        }

        // Check for game over (ball out of bounds)
        if (ballY > screenHeight) {
            resetGame();
        }

        boolean allBricksDestroyed = true;
        for (Brick brick : bricks) {
            if (!brick.isDestroyed()) {
                allBricksDestroyed = false;
                break;
            }
        }

        if (allBricksDestroyed) {
            // Handle game win condition
            resetGame();
        }

        invalidate(); // Force redrawing the view
    }

    private static class Brick {
        private int left, top, right, bottom;
        private boolean destroyed;

        public Brick(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
            this.destroyed = false;
        }

        public int getLeft() {
            return left;
        }

        public int getTop() {
            return top;
        }

        public int getRight() {
            return right;
        }

        public int getBottom() {
            return bottom;
        }

        public boolean isDestroyed() {
            return destroyed;
        }

        public void setDestroyed(boolean destroyed) {
            this.destroyed = destroyed;
        }
    }
}
