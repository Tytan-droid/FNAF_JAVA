import javax.swing.*;

import org.w3c.dom.events.MouseEvent;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;

import Class.animatronics.*;
import Class.Rooms.*;

public class main {

    public static final int FPS = 60;
    private static final long FRAME_TIME = 1000 / FPS;
    private static volatile boolean cam =false;
    private static volatile boolean light_left =false;
    private static volatile boolean light_right =false;


    private static volatile boolean running = true;
    private static volatile int position =0; //0 regarde à gauche et 1 regarde à droite

    private static L_animatronics L_a;
    private static Rooms_Graph rg;
    private static GamePanel panel;
    private static volatile int power_usage =1;
    private static volatile int power =1000*60;

    public static void main(String[] args) {

        L_a = new L_animatronics();
        L_a.L_animatronics_Builder_n1();

        rg = new Rooms_Graph();
        rg.Rooms_Graph_Builder();

        JFrame frame = new JFrame("FNAF");
        frame.setSize(800, 500);
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        panel = new GamePanel();
        frame.add(panel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);


        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    stop();
                }else if(e.getKeyCode()==KeyEvent.VK_SPACE){
                    if(cam){
                        remove_cam();
                    }else{
                        put_cam();
                    }
                }else if(e.getKeyCode()==KeyEvent.VK_Q){
                    turn_left();
                    remove_light(1);
                }else if(e.getKeyCode()==KeyEvent.VK_D){
                    turn_right();
                    remove_light(0);
                }
            }
        });

        frame.addMouseListener(new MouseAdapter() {

        public void mousePressed(java.awt.event.MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                remove_light(position);
            } else if (SwingUtilities.isRightMouseButton(e)) {
                
            }
        }
        public void mouseReleased(java.awt.event.MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                put_light();
            }
        }
    });

        new Thread(main::run).start();
    }

    private static void run() {
        while (running) {
            long startTime = System.currentTimeMillis();

            update();
            render();

            long elapsed = System.currentTimeMillis() - startTime;
            long sleepTime = FRAME_TIME - elapsed;

            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException ignored) {}
            }
        }

        System.out.println("Game stopped");
        System.exit(0);
    }

    private static void update() {
        L_a.move_all_animatronics(rg);
        power-=power_usage;
    }

    private static void render() {
        if (panel != null) panel.repaint();
    }

    public static int getPosition(){
        return position;
    }

    private static void stop() {
        running = false;
    }

    private static void put_cam(){
        power_usage++;
        cam = true;
    }
    private static void remove_cam(){
        power_usage--;
        cam = false;
    }

    private static void put_light(){
        if(position==0){
            light_left=true;
        }else{
            light_right=true;
        }
    }
    private static void remove_light(int pos){
        if(pos==0){
            light_left=false;
        }else{
            light_right=false;
        }
    }

    private static void turn_left(){
        position=0;
    }

    private static void turn_right(){
        position=1;
    }
}
