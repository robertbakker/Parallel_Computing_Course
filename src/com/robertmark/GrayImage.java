package com.robertmark;

import java.awt.image.BufferedImage;

/**
 * Created by mark on 1-12-2016.
 */
public class GrayImage {


    public static BufferedImage makeGray(BufferedImage img)
    {
//        int[] test = img.getRGB(0,0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());
//        System.out.println(img.getWidth() * img.getHeight());
//        System.out.println(test.length);
        for (int x = 0; x < img.getWidth(); ++x)
            for (int y = 0; y < img.getHeight(); ++y)
            {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = (rgb & 0xFF);

                int grayLevel = (r + g + b) / 3;
                int gray = (grayLevel << 16) + (grayLevel << 8) + grayLevel;
                img.setRGB(x, y, gray);
            }
            return img;
    }
}
