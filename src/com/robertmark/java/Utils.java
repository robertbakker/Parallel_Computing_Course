package com.robertmark.java;

/**
 * Created by mark on 1-12-2016.
 */
public class Utils {

    public static int[] makeGray(int[] rgbs) {
        for (int i = 0; i < rgbs.length; i++) {
            int rgb = rgbs[i];
            int r = (rgb >> 16) & 0xFF;
            int g = (rgb >> 8) & 0xFF;
            int b = (rgb & 0xFF);

            int grayLevel = (r + g + b) / 3;
            int gray = (grayLevel << 16) + (grayLevel << 8) + grayLevel;
            rgbs[i] = gray;
        }
        return rgbs;
    }

    public static int[] partitionNumber(int whole, int parts) {
        int[] arr = new int[parts];
        int part = (int)Math.ceil((double)whole / parts);
        for (int i = 0; i < arr.length; i++) {
            if(i + 1 == arr.length) {
                arr[i] = whole - (i*part);
            } else {
                arr[i] = part;
            }
        }
        return arr;
    }
}
