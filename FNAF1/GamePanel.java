import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import javax.imageio.ImageIO;

public class GamePanel extends JPanel {

    private Image image;
    private Image guardLeft;
    private Image guardRight;

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
    }
}
