package com.robertmark;

import java.awt.image.BufferedImage;

/**
 * Created by mark on 1-12-2016.
 */
public class GrayImageParallel extends Thread {

    private BufferedImage img;
    private int startWidth;
    private int endWidth;
    private int hight;

    public GrayImageParallel(BufferedImage img, int startWidth, int endWidth, int hight) {
        this.img = img;
        this.startWidth = startWidth;
        this.endWidth = endWidth;
        this.hight = hight;

    }

    public void run() {
        this.makeGray();
    }

    public BufferedImage getImage() {
        return this.img;
    }

    public void makeGray() {
        for (int x = this.startWidth; x < this.endWidth; ++x)
            for (int y = 0; y < this.hight; ++y) {
                int rgb = this.img.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = (rgb & 0xFF);
                int grayLevel = (r + g + b) / 3;
                int gray = (grayLevel << 16) + (grayLevel << 8) + grayLevel;
                this.img.setRGB(x, y, gray);
            }
    }

}
