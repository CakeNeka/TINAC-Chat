package chat;


import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Un JPanel con una imagen en mosaico,
 * es el fondo del chat
 */
public class ImagePanel extends JPanel {
    private Image backgroundImage;
    private int imageHeight;
    private int imageWidth;

    public ImagePanel(Image backgroundImage) {
        if (backgroundImage != null)
            setBackground(backgroundImage);
    }

    public void setBackground(Image backgroundImage) {
        this.backgroundImage = backgroundImage;
        imageHeight = backgroundImage.getHeight(this);
        imageWidth = backgroundImage.getWidth(this);
    }

    public BufferedImage getBackgroundImage() {
        return (BufferedImage) backgroundImage;
    }

    // Tiled background =)
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        for (int x = 0; x <= panelWidth; x += imageWidth) {
            for (int y = 0; y <= panelHeight; y += imageHeight) {
                g.drawImage(backgroundImage, x, y, this);
            }
        }
    }
}
