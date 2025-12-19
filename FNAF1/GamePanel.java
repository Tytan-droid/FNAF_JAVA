import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class GamePanel extends JPanel {

    private Image image;
    private Image guardLeft;
    private Image guardRight;
    private Map<String, Image> cameraImages = new HashMap<>();
    private Image flashlightLeft;
    private Image flashlightRight;

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
            int pos = main.getPosition();
            Image guard = (pos == 0) ? guardLeft : guardRight;
            if (guard != null) {
                int targetW = getWidth() / 10;
                int iw = guard.getWidth(this);
                int ih = guard.getHeight(this);
                if (iw <= 0 || ih <= 0) {
                    int x = (getWidth() - targetW) / 2;
                    int y = (getHeight() - targetW) / 2;
                    g.drawImage(guard, x, y, targetW, targetW, this);
                } else {
                    int targetH = (int) ((double) targetW * ih / iw);
                    int x = (getWidth() - targetW) / 2;
                    int y = (getHeight() - targetH) / 2;
                    g.drawImage(guard, x, y, targetW, targetH, this);
                }
            }
        } catch (Throwable ignored) {
        }
        
        try {
            if (main.isCam()) {
                String camId = main.getCurrentCamera();
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
                }
            }
        } catch (Throwable ignored) {
        }
        
        try {
            if (main.isCam()) {
                String camId = main.getCurrentCamera();
                if (camId != null) {
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
                }
            }
        } catch (Throwable ignored) {
        }

        try {
            boolean leftOn = main.isLightLeft();
            boolean rightOn = main.isLightRight();
            int panelW = getWidth();
            int panelH = getHeight();

            if (leftOn) {
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
                    int[] xs = { panelW/8, panelW/4, panelW/8 };
                    int[] ys = { panelH/2 - 80, panelW/2, panelH/2 + 80 };
                    g2.fillOval(panelW/12, panelH/2 - 120, panelW/4, 240);
                    g2.dispose();
                }
            }

            if (rightOn) {
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
