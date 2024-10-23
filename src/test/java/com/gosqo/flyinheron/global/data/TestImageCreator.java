package com.gosqo.flyinheron.global.data;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TestImageCreator {
    public static File createTestImage(int width, int height, String text) throws IOException {
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();

        g2d.setColor(Color.ORANGE);
        g2d.fillRect(width / 2, height / 2, width, height);
        g2d.fillRect(0, 0, width / 2, height / 2);

        g2d.setColor(new Color(255, 255, 255));
        g2d.drawString(text, width / 100 * 22, height / 100 * 53);

        g2d.dispose();

        File tempFile = File.createTempFile(text + "_", ".png");
        ImageIO.write(bufferedImage, "png", tempFile);

        return tempFile;
    }
}