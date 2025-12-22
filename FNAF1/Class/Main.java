package Class;

import javax.swing.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.util.HashMap;
import java.util.Map;

import Class.animatronics.*;
import Class.rooms.*;
import Class.SoundManager;

public class Main {

    public static final int FPS = 60;
    private static final long FRAME_TIME = 1000 / FPS;

    private static volatile boolean cam = false;
    private static volatile boolean light_left = false;
    private static volatile boolean light_right = false;
    private static volatile int cam_id = 0;
    private static volatile boolean left_door_close = false;
    private static volatile boolean right_door_close = false;
    private static volatile boolean running = true;
    private static volatile int position = 0; // 0 gauche, 1 droite
    private static volatile boolean can_play = true;

    private static L_animatronics L_a;
    private static Rooms_Graph rg;
    private static GamePanel panel;
    private static JFrame gameFrame;
    private static JFrame menuFrame;
    private static JFrame gameOverFrame;
    private static JFrame winFrame;

    private static long nightStartTime;
    private static final int SECONDS_PER_HOUR = 30;
    private static int currentNight = 1;

    private static volatile int power_usage = 1;
    private static volatile int power = 1000 * 60;

    private static final String[] CAMERA_IDS = {
            "CAM1A","CAM1B","CAM1C","CAM2A","CAM2B","CAM3",
            "CAM4A","CAM4B","CAM5","CAM6","CAM7"
    };
    static String[] hints = {
        "ESC pendant la nuit : retour menu",
        "Ferme les portes seulement si nécessaire",
        "Surveille ta consommation d'énergie",
        "Les caméras consomment de l'électricité",
        "Tous les animatroniques ont un comportement différent",
        "Écoute les bruits de pas...",
        "Ne cligne pas trop longtemps des yeux",
        "Ils te regardent déjà",
        "Mais où touvent-ils toute cette énergie ?",
        "Ur ur ur ur...",
        "Was that the bite of 67 ?",
        "Pizza !"
    };


    private static final Map<String, Long> cooldowns = new HashMap<>();

    private static boolean canUse(String action, long cooldownMs) {
        long now = System.currentTimeMillis();
        long last = cooldowns.getOrDefault(action, 0L);
        if (now - last >= cooldownMs) {
            cooldowns.put(action, now);
            return can_play;
        }
        return false;
    }

    public static void main(String[] args) {
        SoundManager.loadAll("FNAF1/Sounds");
        currentNight = SaveManager.loadNight();
        initialise_menu();
    }

    public static void night(int num) {
        running = true;
        nightStartTime = System.currentTimeMillis();
        cam = false;
        light_left = false;
        light_right = false;
        left_door_close = false;
        right_door_close = false;
        cam_id = 0;
        position = 0;
        power_usage = 1;
        power = 1000 * 60;
        can_play=true;
        SoundManager.loop("Eerie ambience largesca");
        L_a = new L_animatronics();
        if (num == 1) L_a.L_animatronics_Builder_n1();
        if (num == 2) L_a.L_animatronics_Builder_n2();
        if (num == 3) L_a.L_animatronics_Builder_n3();
        if (num == 4) L_a.L_animatronics_Builder_n4();
        if (num == 5) L_a.L_animatronics_Builder_n5();
        if (num == 6) L_a.L_animatronics_Builder_n6();

        rg = new Rooms_Graph();
        rg.Rooms_Graph_Builder();
        gameFrame = new JFrame("FNAF");
        gameFrame.setSize(800, 500);
        gameFrame.setUndecorated(true);
        gameFrame.setResizable(false);
        gameFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        gameFrame.setLocationRelativeTo(null);
        panel = new GamePanel();
        gameFrame.add(panel);
        gameFrame.setVisible(true);
        gameFrame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    if (canUse("ESC", 500)) {
                        returnToMenu();
                    }
                }
                else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (!canUse("CAM", 500)) return;
                    if (cam) remove_cam();
                    else {
                        put_cam();
                        if(light_left||light_right){
                            power_usage--;
                        }
                        remove_light(0);
                        remove_light(1);
                    }
                }
                else if (e.getKeyCode() == KeyEvent.VK_Q) {
                    if (!canUse("LEFT", 300)) return;
                    if (cam) switch_cam_left();
                    else {
                        turn_left();
                        if (light_right) {
                            remove_light(1);
                            put_light();
                        }
                    }
                }
                else if (e.getKeyCode() == KeyEvent.VK_D) {
                    if (!canUse("RIGHT", 300)) return;
                    if (cam) switch_cam_right();
                    else {
                        turn_right();
                        if (light_left) {
                            remove_light(0);
                            put_light();
                        }
                    }
                }
            }
        });
        gameFrame.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && !cam) {
                    if (canUse("LIGHT", 150)) {put_light(); power_usage++;}
                }
                else if (SwingUtilities.isRightMouseButton(e) && !cam) {
                    if (canUse("DOOR", 200)) door(rg);
                }
            }
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)&&(light_left||light_right)) {
                    remove_light(position); power_usage--;
                }
            }
        });
        new Thread(Main::run).start();
    }

    private static void run() {
        while (running) {
            long start = System.currentTimeMillis();
            update();
            render();
            long sleep = FRAME_TIME - (System.currentTimeMillis() - start);
            if (sleep > 0) {
                try { Thread.sleep(sleep); }
                catch (InterruptedException ignored) {}
            }
        }
    }

    private static void update() {
        L_a.move_all_animatronics(rg);
        power -= power_usage*(2.5);
        if(power<60){
            gameOver();
        }else if(getHour()>=6){
            nightWin();
        }
    }

    private static void render() {
        if (panel != null) panel.repaint();
    }

    public static int getPosition() { return position; }
    public static L_animatronics getAnimatronics() { return L_a; }
    public static boolean isLightLeft() { return light_left; }
    public static boolean isLightRight() { return light_right; }
    public static boolean isCam() { return cam; }

    public static String getCurrentCamera() {
        if (cam_id >= 0 && cam_id < CAMERA_IDS.length) return CAMERA_IDS[cam_id];
        return CAMERA_IDS[0];
    }

    private static void put_cam() {
        SoundManager.play("fnaf-open-camera-sound");
        power_usage++;
        cam = true;
    }

    public static void remove_cam() {
        power_usage--;
        cam = false;
    }

    private static void put_light() {
        SoundManager.play("fnaf-light-sound");
        if (position == 0) light_left = true;
        else light_right = true;
    }

    private static void remove_light(int pos) {
        if (pos == 0) light_left = false;
        else light_right = false;
    }

    private static void turn_left() { position = 0; }
    private static void turn_right() { position = 1; }

    private static void switch_cam_left() {
        SoundManager.play("fnaf2-camera");
        cam_id = (cam_id > 0) ? cam_id - 1 : 10;
    }

    private static void switch_cam_right() {
        SoundManager.play("fnaf2-camera");
        cam_id = (cam_id < 10) ? cam_id + 1 : 0;
    }

    private static volatile long lastBlink = 0;

    public static void blinkCamera(int delayMs) {
        long now = System.currentTimeMillis();
        if (!cam) return;
        if (now - lastBlink < 300) return;
        lastBlink = now;
        if (panel != null) panel.startCameraBlackout(300);
    }

    public static void close_left_door(Rooms_Graph rg) {
        SoundManager.play("door_slamming_fnaf_1_sound_effects");
        power_usage++;
        rg.removeEdge(rg.getRoom("You"), rg.getRoom("Door_Left"));
        left_door_close = true;
    }

    public static void close_right_door(Rooms_Graph rg) {
        SoundManager.play("door_slamming_fnaf_1_sound_effects");
        power_usage++;
        rg.removeEdge(rg.getRoom("You"), rg.getRoom("Door_Right"));
        right_door_close = true;
    }

    public static void open_right_door(Rooms_Graph rg) {
        power_usage--;
        rg.addEdge(rg.getRoom("You"), rg.getRoom("Door_Right"));
        right_door_close = false;
    }

    public static void open_left_door(Rooms_Graph rg) {
        power_usage--;
        rg.addEdge(rg.getRoom("You"), rg.getRoom("Door_Left"));
        left_door_close = false;
    }

    public static void door(Rooms_Graph rg) {
        if (position == 0) {
            if (left_door_close) open_left_door(rg);
            else close_left_door(rg);
        } else {
            if (right_door_close) open_right_door(rg);
            else close_right_door(rg);
        }
    }

    public static void set_running_false(){
        running=false;
    }

    public static void initialise_menu() {
        int nightNumber = currentNight;

        if (menuFrame != null) return;

        SoundManager.loop("fnaf-music-box-109");

        menuFrame = new JFrame("FNAF");
        menuFrame.setSize(800, 500);
        menuFrame.setUndecorated(true);
        menuFrame.setResizable(false);
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(java.awt.Color.BLACK);

        JLabel title = new JLabel("Night "+ nightNumber);
        title.setForeground(java.awt.Color.WHITE);
        title.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 42));
        title.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Appuyez sur ENTRÉE pour commencer");
        subtitle.setForeground(java.awt.Color.WHITE);
        subtitle.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 18));
        subtitle.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        String randomHint = hints[(int) (Math.random() * hints.length)];
        JLabel hint = new JLabel(randomHint);
        hint.setForeground(java.awt.Color.GRAY);
        hint.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
        hint.setAlignmentX(JLabel.CENTER_ALIGNMENT);


        panel.add(Box.createVerticalGlue());
        panel.add(title);
        panel.add(Box.createVerticalStrut(30));
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(10));
        panel.add(hint);
        panel.add(Box.createVerticalGlue());

        menuFrame.add(panel);
        menuFrame.setVisible(true);

        menuFrame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {

                if (e.getKeyCode() == KeyEvent.VK_ENTER ||
                    e.getKeyCode() == KeyEvent.VK_SPACE) {

                    SoundManager.stop("fnaf-music-box-109");
                    menuFrame.dispose();
                    menuFrame = null;
                    night(nightNumber);
                }else if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
                    System.exit(0);
                }
            }
        });
    }


    public static boolean left_door_close() { return left_door_close; }
    public static boolean right_door_close() { return right_door_close; }

        public static void returnToMenu() {

        running = false;
        SoundManager.stopAll();
        SoundManager.loop("fnaf-music-box-109");

        if (gameFrame != null) {
            gameFrame.dispose();
            gameFrame = null;
        }

        initialise_menu();
    }

    public static void gameOver() {
        running = false;
        SoundManager.stopAll();
        SoundManager.play("bite_of_87_fnaf");
        if (gameFrame != null) {
            gameFrame.dispose();
            gameFrame = null;
        }
        if (gameOverFrame != null) return;
        gameOverFrame = new JFrame("GAME OVER");
        gameOverFrame.setSize(800, 500);
        gameOverFrame.setUndecorated(true);
        gameOverFrame.setResizable(false);
        gameOverFrame.setLocationRelativeTo(null);
        gameOverFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(java.awt.Color.BLACK);
        JLabel title = new JLabel("GAME OVER");
        title.setForeground(java.awt.Color.RED);
        title.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 60));
        title.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        JLabel info = new JLabel("Appuyez sur ENTRÉE pour retourner au menu");
        info.setForeground(java.awt.Color.WHITE);
        info.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 18));
        info.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalGlue());
        panel.add(title);
        panel.add(Box.createVerticalStrut(30));
        panel.add(info);
        panel.add(Box.createVerticalGlue());
        gameOverFrame.add(panel);
        gameOverFrame.setVisible(true);
        gameOverFrame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    gameOverFrame.dispose();
                    gameOverFrame = null;
                    initialise_menu();
                }
            }
        });
    }
    public static int getPower() {
        return power;
    }
    public static int getPowerUsage(){
        return power_usage;
    }
    public static int getHour() {
        long elapsedSeconds = (System.currentTimeMillis() - nightStartTime) / 1000;
        int hour = (int) (elapsedSeconds / SECONDS_PER_HOUR);

        if (hour > 6) hour = 6;
        return hour;
    }
    public static String getHourString() {
        int hour = getHour();
        if (hour == 0) return "12 AM";
        return hour + " AM";
    }

    public static void nightWin() {

        if (!running) return;
        running = false;
        if (currentNight < 6) {
            currentNight++;
            SaveManager.saveNight(currentNight);
        }

        SoundManager.stopAll();
        SoundManager.play("fnaf-chimes");

        if (gameFrame != null) {
            gameFrame.dispose();
            gameFrame = null;
        }

        if (winFrame != null) return;

        winFrame = new JFrame("6 AM");
        winFrame.setSize(800, 500);
        winFrame.setUndecorated(true);
        winFrame.setResizable(false);
        winFrame.setLocationRelativeTo(null);
        winFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.BLACK);

        JLabel title = new JLabel("6 AM");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 72));
        title.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        JLabel win = new JLabel("VOUS AVEZ SURVÉCU");
        win.setForeground(Color.GREEN);
        win.setFont(new Font("Arial", Font.PLAIN, 22));
        win.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        JLabel hint = new JLabel("Appuyez sur ENTRÉE pour retourner au menu");
        hint.setForeground(Color.GRAY);
        hint.setFont(new Font("Arial", Font.PLAIN, 14));
        hint.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalGlue());
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));
        panel.add(win);
        panel.add(Box.createVerticalStrut(30));
        panel.add(hint);
        panel.add(Box.createVerticalGlue());

        winFrame.add(panel);
        winFrame.setVisible(true);

        winFrame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    winFrame.dispose();
                    winFrame = null;
                    SoundManager.stopAll();
                    initialise_menu();
                }
            }
        });
    }

    public static void cant_play(){
        can_play=false;
    }
    public static GamePanel getGamePanel() {
        return panel;
    }
    public static void startJumpscare(abstrac_animatronic a) {
        running = false;
        panel.startSlideJumpscare(a);
    }

}

