package com.kyh.app.utils;

public class ImageUtil {

    public static boolean isImage(String imageUrl) {
        if(imageUrl.endsWith(".jpg") || imageUrl.endsWith(".jpeg") || imageUrl.endsWith(".gif")
                ||imageUrl.endsWith(".png")||imageUrl.endsWith(".bmp")) {
            return true;
        }
        return false;
    }
}
