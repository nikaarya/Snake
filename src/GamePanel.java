import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25; //Size of all the items in the game
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static final int DELAY = 75; //Ju högre nummer, ju långsammare är spelet

    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];

    int bodyParts = 6; //Startar med 6 body parts
    int applesEaten = 0; //Startar med 0 uppätna äpplen
    int appleX; //X positioning of where the apple is located (Randomly)
    int appleY;
    char direction = 'R'; //Starting direction of the snake. R=right, L=left, U=upp, D=down
    boolean running = false;
    Timer timer;
    Random random;


    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new myKeyAdapter());
        startGame();

    }

    public void startGame() {
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        //Skapar linjer vertikalt och horisontellt
        //Man får en bild av hur stora föremålen i spelet kommer att vara
        // Det är det som är syftet med UNIT_SIZE, förstorar du den, förstorar du rutorna (och föremålen) och vice versa

        if (running) {
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }
            g.setColor(Color.RED); //Color of the apple
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE); //Shape/size of the apple

            //Draw the head of the snake
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(new Color(0, 0, 205)); //(Color.BLUE);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(0, 0, 255));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            //Score
            g.setColor(Color.RED);
            g.setFont(new Font("Tahoma", Font.PLAIN, 30));
            FontMetrics metrics = getFontMetrics(g.getFont());
            //Sets the text Game Over in the center of the screen
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)), g.getFont().getSize());

        } else {
            gameOver(g);
        }
    }

    public void newApple() {
        appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;

    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            //Ser till att kroppsdelarna rör sig i takt
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            //Up, down, left, right
            //y[0] & x[0] = the head of the snake
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    public void checkApple() {
        //Grab the apple
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions() {
        //checks if head collides with body:
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }
        //checks if head touches left border:
        if (x[0] < 0) {
            running = false;
        }
        //checks if head touches right border:
        if (x[0] > SCREEN_WIDTH) {
            running = false;
        }
        //checks if head touches top border:
        if (y[0] < 0) {
            running = false;
        }
        //checks if head touches bottom border:
        if (y[0] > SCREEN_HEIGHT) {
            running = false;
        }
        //stops the timer if the game ends
        if (!running) {
            timer.stop();
        }
    }

    public void gameOver(Graphics g) {
        //Score
        g.setColor(Color.RED);
        g.setFont(new Font("Tahoma", Font.PLAIN, 30));
        FontMetrics metrics = getFontMetrics(g.getFont());
        //Sets the text Game Over in the center of the screen
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());

        //GameOver text:
        g.setColor(Color.GRAY);
        g.setFont(new Font("Tahoma", Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        //Sets the text Game Over in the center of the screen
        g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();

    }

    public class myKeyAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            if (keyCode == KeyEvent.VK_LEFT) {
                if (direction != 'R') { //Ser till att man inte ska kunna svänga 180 grader (svänga höger när
                    direction = 'L'; //man är påväg åt vänster och vice versa
                }
            } else if (keyCode == KeyEvent.VK_RIGHT) {
                if (direction != 'L') {
                    direction = 'R';
                }
            } else if (keyCode == KeyEvent.VK_UP) {
                if (direction != 'D') {
                    direction = 'U';
                }
            } else if (keyCode == KeyEvent.VK_DOWN) {
                if (direction != 'U') {
                    direction = 'D';
                }
            } else if (keyCode == KeyEvent.VK_ENTER) {
                if (!running) {
                    applesEaten = 0;
                    direction = 'R';
                    bodyParts = 6;

                    move();
                    startGame();
                   


                }
            }
        }
    }
}
