package Class;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import Class.animatronics.abstrac_animatronic;
import Class.animatronics.Chica;
import Class.animatronics.Bonnie;
import Class.animatronics.Freddy;
import Class.animatronics.L_animatronics;

public class GamePanel extends JPanel {

    private Image image;
    private Image guardLeft;
    private Image guardRight;
    private Map<String, Image> cameraImages = new HashMap<>();
    private Image flashlightLeft;
    private Image flashlightRight;
    private Map<String, Image> animImages = new HashMap<>();
    private java.util.Map<abstrac_animatronic, String> lastSeenRooms = new java.util.HashMap<>();
    private volatile boolean blackoutActive = false;
    private volatile long blackoutStart = 0L;
    private volatile long blackoutDuration = 0L;

    public void startCameraBlackout(int ms) {
        blackoutDuration = Math.max(0, ms);
        blackoutStart = System.currentTimeMillis();
        blackoutActive = true;
        new Thread(() -> {
            long end = blackoutStart + blackoutDuration;
            while (System.currentTimeMillis() < end) {
                try { Thread.sleep(16); } catch (InterruptedException ignored) {}
                repaint();
            }
            blackoutActive = false;
            repaint();
        }).start();
        repaint();
    }

    private void drawAnimAtGuard(Graphics g, abstrac_animatronic a, int x, int y, int targetW, int targetH, boolean leftSide) {
        if (a == null) return;
        String baseName;
        if (a instanceof Chica) baseName = "Chica";
        else if (a instanceof Bonnie) baseName = "Bonnie";
        else if (a instanceof Freddy) baseName = "Freddy";
        else baseName = null;

        Graphics2D g2 = (Graphics2D) g.create();
        int drawW = targetW * 2 / 3;
        int drawH = targetH * 2 / 3;
        int dx = x - drawW/2 + (leftSide ? -targetW/6 : targetW/6);
        int dy = y - drawH/2;

        boolean drawn = false;
        if (baseName != null) {
            String side = leftSide ? "_Left" : "_Right";
            String key = baseName + side;
            Image animImg = animImages.get(key);
            if (animImg == null) {
                try {
                    java.net.URL url = getClass().getResource("/images/" + key + ".png");
                    if (url != null) animImg = ImageIO.read(url);
                } catch (IOException ignored) {}
                if (animImg == null) {
                    try {
                        java.io.File f = new java.io.File("FNAF1/Pictures/" + key + ".png");
                        if (f.exists()) animImg = ImageIO.read(f);
                    } catch (IOException ignored) {}
                }
                if (animImg != null) animImages.put(key, animImg);
            }
            if (animImg != null) {
                int iw = animImg.getWidth(this);
                int ih = animImg.getHeight(this);
                int drawHH = (iw > 0 && ih > 0) ? (int) ((double) drawW * ih / iw) : drawW;
                g2.drawImage(animImg, dx, dy - (drawHH - drawH)/2, drawW, drawHH, this);
                drawn = true;
            }
        }

        if (!drawn) {
            Color col = Color.WHITE;
            String label = "?";
            if (a instanceof Chica) { col = Color.YELLOW; label = "C"; }
            else if (a instanceof Bonnie) { col = new Color(128, 0, 128); label = "B"; }
            else if (a instanceof Freddy) { col = new Color(139, 69, 19); label = "F"; }

            g2.setColor(new Color(0,0,0,160));
            g2.fillOval(dx - 4, dy - 4, drawW + 8, drawH + 8);
            g2.setColor(col);
            g2.fillOval(dx, dy, drawW, drawH);
            g2.setColor(Color.BLACK);
            Font f = g2.getFont().deriveFont(Font.BOLD, (float)drawW/1.6f);
            g2.setFont(f);
            FontMetrics fm = g2.getFontMetrics(f);
            int tx = dx + (drawW - fm.stringWidth(label)) / 2;
            int ty = dy + (drawW + fm.getAscent()) / 2 - 2;
            g2.drawString(label, tx, ty);
        }

        g2.dispose();
    }

    private void drawAnimCentered(Graphics g, abstrac_animatronic a, boolean leftSide) {
        if (a == null) return;
        try {
            System.out.println("Drawing centered anim: " + a.getClass().getSimpleName() + " room=" + a.get_id_room());
        } catch (Throwable ignored) {}
        String baseName;
        if (a instanceof Chica) baseName = "Chica";
        else if (a instanceof Bonnie) baseName = "Bonnie";
        else if (a instanceof Freddy) baseName = "Freddy";
        else baseName = null;

        int panelW = getWidth();
        int panelH = getHeight();
        int maxW = panelW / 3; 
        int drawW = Math.max(40, maxW);
        int drawH = drawW;

        Graphics2D g2 = (Graphics2D) g.create();
        boolean drawn = false;
        if (baseName != null) {
            String side = leftSide ? "_Left" : "_Right";
            String key = baseName + side;
            Image animImg = animImages.get(key);
            if (animImg == null) {
                try {
                    java.net.URL url = getClass().getResource("/images/" + key + ".png");
                    if (url != null) animImg = ImageIO.read(url);
                } catch (IOException ignored) {}
                if (animImg == null) {
                    try {
                        java.io.File f = new java.io.File("FNAF1/Pictures/" + key + ".png");
                        if (f.exists()) animImg = ImageIO.read(f);
                    } catch (IOException ignored) {}
                }
                if (animImg != null) animImages.put(key, animImg);
            }
            if (animImg != null) {
                int iw = animImg.getWidth(this);
                int ih = animImg.getHeight(this);
                drawH = (iw > 0 && ih > 0) ? (int) ((double) drawW * ih / iw) : drawW;
                int dx = (panelW - drawW) / 2 + (leftSide ? -panelW/8 : panelW/8);
                int dy = (panelH - drawH) / 2;
                g2.drawImage(animImg, dx, dy, drawW, drawH, this);
                drawn = true;
            }
        }

        if (!drawn) {
            Color col = Color.WHITE;
            String label = "?";
            if (a instanceof Chica) { col = Color.YELLOW; label = "C"; }
            else if (a instanceof Bonnie) { col = new Color(128, 0, 128); label = "B"; }
            else if (a instanceof Freddy) { col = new Color(139, 69, 19); label = "F"; }

            int dx = (panelW - drawW) / 2 + (leftSide ? -panelW/8 : panelW/8);
            int dy = (panelH - drawH) / 2;
            g2.setColor(new Color(0,0,0,160));
            g2.fillOval(dx - 6, dy - 6, drawW + 12, drawH + 12);
            g2.setColor(col);
            g2.fillOval(dx, dy, drawW, drawH);
            g2.setColor(Color.BLACK);
            Font f = g2.getFont().deriveFont(Font.BOLD, (float)drawW/1.6f);
            g2.setFont(f);
            FontMetrics fm = g2.getFontMetrics(f);
            int tx = dx + (drawW - fm.stringWidth(label)) / 2;
            int ty = dy + (drawW + fm.getAscent()) / 2 - 2;
            g2.drawString(label, tx, ty);
        }

        g2.dispose();
    }

    public GamePanel() {
        try {
            java.net.URL url = getClass().getResource("/images/background.png");
            if (url != null) {
                image = ImageIO.read(url);
                return;
            }
        } catch (IOException | IllegalArgumentException ignored) {
        }

        try {
            java.io.File f = new java.io.File("FNAF1/Pictures/room_You.png");
            if (f.exists()) {
                image = ImageIO.read(f);
            }
        } catch (IOException e) {
            System.out.println("Failed to load background image: " + e.getMessage());
        }

        try {
            java.net.URL urlL = getClass().getResource("/images/Guard_Left.png");
            if (urlL != null) guardLeft = ImageIO.read(urlL);
        } catch (IOException ignored) {}
        try {
            java.net.URL urlR = getClass().getResource("/images/Guard_Right.png");
            if (urlR != null) guardRight = ImageIO.read(urlR);
        } catch (IOException ignored) {}
        try {
            java.io.File fL = new java.io.File("FNAF1/Pictures/Guard_Left.png");
            if (guardLeft == null && fL.exists()) guardLeft = ImageIO.read(fL);
        } catch (IOException ignored) {}
        try {
            java.io.File fR = new java.io.File("FNAF1/Pictures/Guard_Right.png");
            if (guardRight == null && fR.exists()) guardRight = ImageIO.read(fR);
        } catch (IOException ignored) {}

        try {
            java.net.URL urlFL = getClass().getResource("/images/Flashlight_Left.png");
            if (urlFL != null) flashlightLeft = ImageIO.read(urlFL);
        } catch (IOException ignored) {}
        try {
            java.net.URL urlFR = getClass().getResource("/images/Flashlight_Right.png");
            if (urlFR != null) flashlightRight = ImageIO.read(urlFR);
        } catch (IOException ignored) {}
        try {
            java.io.File ffL = new java.io.File("FNAF1/Pictures/Flashlight_Left.png");
            if (flashlightLeft == null && ffL.exists()) flashlightLeft = ImageIO.read(ffL);
        } catch (IOException ignored) {}
        try {
            java.io.File ffR = new java.io.File("FNAF1/Pictures/Flashlight_Right.png");
            if (flashlightRight == null && ffR.exists()) flashlightRight = ImageIO.read(ffR);
        } catch (IOException ignored) {}
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (image != null) {
            g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
        }
        try {
            int pos = Main.getPosition();
            Image guard = (pos == 0) ? guardLeft : guardRight;
            if (guard != null) {
                int targetW = getWidth() / 10;
                int iw = guard.getWidth(this);
                int ih = guard.getHeight(this);
                int x = (getWidth() - targetW) / 2;
                int y = (getHeight() - targetW) / 2;
                int targetH = targetW;
                if (iw > 0 && ih > 0) {
                    targetH = (int) ((double) targetW * ih / iw);
                    y = (getHeight() - targetH) / 2;
                }
                g.drawImage(guard, x, y, targetW, targetH, this);

                try {
                    L_animatronics la = Main.getAnimatronics();
                    if (la != null) {
                        java.util.List<abstrac_animatronic> list = la.get_L();
                        if (Main.isLightLeft()) {
                            for (abstrac_animatronic a : list) {
                                if (a == null) continue;
                                if ("Door_Left".equals(a.get_id_room())) {
                                    drawAnimAtGuard(g, a, x - targetW/4-200, y+20, (int) (targetW*1.5),(int) (targetH*1.5), true);
                                    break;
                                }
                            }
                        }
                        if (Main.isLightRight()) {
                            for (abstrac_animatronic a : list) {
                                if (a == null) continue;
                                if ("Door_Right".equals(a.get_id_room())) {
                                    drawAnimAtGuard(g, a, x + targetW/4+300, y+20, (int) (targetW*1.5),(int) (targetH*1.5), false);
                                    break;
                                }
                            }
                        }
                    }
                } catch (Throwable ignored) {}
            }
        } catch (Throwable ignored) {
        }
        
        try {
            if (Main.isCam()) {
                String camId = Main.getCurrentCamera();
                if (camId != null) { 
                    Image camImg = cameraImages.get(camId);
                    if (camImg == null) {
                        try {
                            java.net.URL url = getClass().getResource("/images/" + camId + ".png");
                            if (url != null) camImg = ImageIO.read(url);
                        } catch (IOException ignored) {}
                        if (camImg == null) {
                            try {
                                java.io.File f = new java.io.File("FNAF1/Pictures/" + camId + ".png");
                                if (f.exists()) camImg = ImageIO.read(f);
                            } catch (IOException ignored) {}
                        }
                        if (camImg != null) cameraImages.put(camId, camImg);
                    }
                    if (camImg != null) {
                        g.drawImage(camImg, 0, 0, getWidth(), getHeight(), this);
                    } else {
                        g.setColor(new Color(0, 0, 0, 150));
                        g.fillRect(getWidth() / 4, getHeight() / 4, getWidth() / 2, getHeight() / 2);
                        g.setColor(Color.WHITE);
                        g.drawString("Camera " + camId + " (image not found)", getWidth() / 2 - 60, getHeight() / 2);
                    }

                    if (blackoutActive) {
                        long now = System.currentTimeMillis();
                        long elapsed = now - blackoutStart;
                        float alpha = 1.0f;
                        if (blackoutDuration > 0) {
                            alpha = 1.0f - Math.min(1.0f, (float) elapsed / (float) blackoutDuration);
                        } else {
                            alpha = 0f;
                        }

                        if (alpha <= 0f) {
                            blackoutActive = false;
                        } else {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                            g2.setColor(Color.BLACK);
                            g2.fillRect(0, 0, getWidth(), getHeight());
                            g2.dispose();
                            return;
                        }
                    }

                    try {
                        if (!"CAM6".equals(camId)) {
                            L_animatronics la = Main.getAnimatronics();
                            if (la != null) {
                                java.util.List<abstrac_animatronic> list = la.get_L();

                                try {
                                    String currentCam = camId;
                                    for (abstrac_animatronic a : list) {
                                        if (a == null) continue;
                                        String prev = lastSeenRooms.get(a);
                                        String nowRoom = a.get_id_room();
                                        if (prev != null && prev.equals(currentCam) && !currentCam.equals(nowRoom) && Main.isCam()) {
                                            try { Main.blinkCamera(400); } catch (Throwable ignored) {}
                                        }
                                        lastSeenRooms.put(a, nowRoom);
                                    }
                                } catch (Throwable ignored) {}
                                java.util.List<abstrac_animatronic> present = new java.util.ArrayList<>();
                                for (abstrac_animatronic a : list) {
                                    if (a != null && camId.equals(a.get_id_room())) {
                                        if (a instanceof Chica || a instanceof Bonnie || a instanceof Freddy) {
                                            present.add(a);
                                        }
                                    }
                                }
                                if (!present.isEmpty()) {
                                    int n = present.size();
                                    for (int i = 0; i < n; i++) {
                                        abstrac_animatronic a = present.get(i);
                                        String baseName;
                                        if (a instanceof Chica) baseName = "Chica";
                                        else if (a instanceof Bonnie) baseName = "Bonnie";
                                        else if (a instanceof Freddy) baseName = "Freddy";
                                        else baseName = null;

                                        int w = getWidth();
                                        int h = getHeight();
                                        int markerSize = Math.max(20, Math.min(w, h) / 8);
                                        boolean verticalLayout = "CAM2A".equals(camId) || "CAM4A".equals(camId);
                                        int x, y;
                                        double marginFrac = 0.12;
                                        if (verticalLayout) {
                                            marginFrac = -0.12;
                                            x = (int) (w * 0.5 - markerSize / 2.0);
                                            double start = h * marginFrac;
                                            double range = h * (1.0 - 2 * marginFrac);
                                            y = (int) ((i + 1) * (range / (n + 1)) + start - markerSize / 2.0);
                                        } else {
                                            double startX = w * marginFrac;
                                            double rangeX = w * (1.0 - 2 * marginFrac);
                                            x = (int) ((i + 1) * (rangeX / (n + 1)) + startX - markerSize / 2.0);
                                            y = (int) (h * 0.65) - markerSize / 2;
                                        }

                                        boolean drawn = false;
                                        if (baseName != null) {
                                            String side = Main.getPosition() == 0 ? "_Left" : "_Right";
                                            String key = baseName + side;
                                            Image animImg = animImages.get(key);
                                            if (animImg == null) {
                                                try {
                                                    java.net.URL url = getClass().getResource("/images/" + key + ".png");
                                                    if (url != null) animImg = ImageIO.read(url);
                                                } catch (IOException ignored) {}
                                                if (animImg == null) {
                                                    try {
                                                        java.io.File f = new java.io.File("FNAF1/Pictures/" + key + ".png");
                                                        if (f.exists()) animImg = ImageIO.read(f);
                                                    } catch (IOException ignored) {}
                                                }
                                                if (animImg != null) animImages.put(key, animImg);
                                            }

                                            if (animImg != null) {
                                                int iw = animImg.getWidth(this);
                                                int ih = animImg.getHeight(this);
                                                int drawW = markerSize * 2;
                                                int drawH = (iw > 0 && ih > 0) ? (int) ((double) drawW * ih / iw) : drawW;
                                                int dx = x - (drawW - markerSize) / 2;
                                                int dy = y - (drawH - markerSize) / 2;
                                                g.drawImage(animImg, dx, dy, drawW, drawH, this);
                                                drawn = true;
                                            }
                                        }

                                        if (!drawn) {
                                            Color col = Color.WHITE;
                                            String label = "?";
                                            if (a instanceof Chica) { col = Color.YELLOW; label = "C"; }
                                            else if (a instanceof Bonnie) { col = new Color(128, 0, 128); label = "B"; }
                                            else if (a instanceof Freddy) { col = new Color(139, 69, 19); label = "F"; }

                                            Graphics2D g2 = (Graphics2D) g.create();
                                            g2.setColor(new Color(0,0,0,120));
                                            g2.fillOval(x - 4, y - 4, markerSize + 8, markerSize + 8);
                                            g2.setColor(col);
                                            g2.fillOval(x, y, markerSize, markerSize);
                                            g2.setColor(Color.BLACK);
                                            Font f = g2.getFont().deriveFont(Font.BOLD, (float)markerSize/1.2f);
                                            g2.setFont(f);
                                            FontMetrics fm = g2.getFontMetrics(f);
                                            int tx = x + (markerSize - fm.stringWidth(label)) / 2;
                                            int ty = y + (markerSize + fm.getAscent()) / 2 - 2;
                                            g2.drawString(label, tx, ty);
                                            g2.dispose();
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Throwable ignored) {}

                    try {
                        String txt = camId;
                        Font orig = g.getFont();
                        Font hudFont = orig.deriveFont(Font.BOLD, 18f);
                        g.setFont(hudFont);
                        FontMetrics fm = g.getFontMetrics(hudFont);
                        int w = fm.stringWidth(txt);
                        int h = fm.getHeight();
                        int x = (getWidth() - w) / 2;
                        int y = fm.getAscent() + 8;

                        Color prev = g.getColor();
                        g.setColor(new Color(0, 0, 0, 160));
                        g.fillRoundRect(x - 12, 4, w + 24, h + 4, 10, 10);

                        g.setColor(Color.BLACK);
                        g.drawString(txt, x + 1, y + 1);
                        g.setColor(Color.WHITE);
                        g.drawString(txt, x, y);

                        g.setFont(orig);
                        g.setColor(prev);
                    } catch (Throwable ignored) {}
                }
            }
        } catch (Throwable ignored) {}

        try {
            boolean leftClose = Main.left_door_close();
            boolean rightClose = Main.right_door_close();
            int panelW = getWidth();
            int panelH = getHeight();

            if (leftClose && !Main.isCam()) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
                g2.setColor(Color.GRAY);
                g2.fillRect(panelW/12+90, panelH/2 - 100, panelW/30, 200);
                g2.dispose();
            }

            if (rightClose && !Main.isCam()) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
                g2.setColor(Color.GRAY);
                g2.fillRect(panelW - panelW/3+90, panelH/2 - 100, panelW/30, 200);
                g2.dispose();
            }
        } catch (Throwable ignored) {}

        try {
            boolean leftOn = Main.isLightLeft();
            boolean rightOn = Main.isLightRight();
            int panelW = getWidth();
            int panelH = getHeight();

            if (leftOn && !Main.isCam()) {
                if (flashlightLeft != null) {
                    int w = panelW / 3;
                    int iw = flashlightLeft.getWidth(this);
                    int ih = flashlightLeft.getHeight(this);
                    int h = (iw > 0 && ih > 0) ? (int) ((double) w * ih / iw) : w;
                    int x = panelW / 8 - w/2;
                    int y = panelH/2 - h/2;
                    g.drawImage(flashlightLeft, x, y, w, h, this);
                } else {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.06f));
                    g2.setColor(Color.YELLOW);
                    g2.fillOval(panelW/12, panelH/2 - 120, panelW/4, 240);
                    g2.dispose();
                }
            }

            if (rightOn && !Main.isCam()) {
                if (flashlightRight != null) {
                    int w = panelW / 3;
                    int iw = flashlightRight.getWidth(this);
                    int ih = flashlightRight.getHeight(this);
                    int h = (iw > 0 && ih > 0) ? (int) ((double) w * ih / iw) : w;
                    int x = panelW - panelW/8 - w/2;
                    int y = panelH/2 - h/2;
                    g.drawImage(flashlightRight, x, y, w, h, this);
                } else {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.06f));
                    g2.setColor(Color.YELLOW);
                    g2.fillOval(panelW - panelW/3, panelH/2 - 120, panelW/4, 240);
                    g2.dispose();
                }
            }
        } catch (Throwable ignored) {}


    }
}
