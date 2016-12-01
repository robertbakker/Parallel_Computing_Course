package com.robertmark;

import java.awt.image.BufferedImage;

/**
 * Created by mark on 1-12-2016.
 */
public class GrayImage extends Thread{

    private BufferedImage img;
   public GrayImage(BufferedImage img){
       this.img = img;

   }

    public void run( ){
        this.makeGray();
    }

    public BufferedImage getImage(){
        return this.img;
    }

    public  void makeGray() {
        for (int x = 0; x < this.img.getWidth(); ++x)
            for (int y = 0; y < this.img.getHeight(); ++y) {
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
