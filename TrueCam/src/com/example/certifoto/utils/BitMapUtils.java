package com.example.certifoto.utils;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

public class BitMapUtils {
	
    public static Bitmap loadBitmap(String imgpath) {  
        return BitmapFactory.decodeFile(imgpath);  
    }  
  
      
   
    public static Bitmap loadBitmap(String imgpath, boolean adjustOritation) {  
        if (!adjustOritation) {  
            return loadBitmap(imgpath);  
        } else {  
            Bitmap bm = loadBitmap(imgpath);  
            int digree = 0;  
            ExifInterface exif = null;  
            try {  
                exif = new ExifInterface(imgpath);  
            } catch (IOException e) {  
                e.printStackTrace();  
                exif = null;  
            }  
            if (exif != null) {  
               
                int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,  
                        ExifInterface.ORIENTATION_UNDEFINED);  
               
                switch (ori) {  
                case ExifInterface.ORIENTATION_ROTATE_90:  
                    digree = 90;  
                    break;  
                case ExifInterface.ORIENTATION_ROTATE_180:  
                    digree = 180;  
                    break;  
                case ExifInterface.ORIENTATION_ROTATE_270:  
                    digree = 270;  
                    break;  
                default:  
                    digree = 0;  
                    break;  
                }  
            }  
            if (digree != 0) {  
              
                Matrix m = new Matrix();  
                m.postRotate(digree);  
                bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),  
                        bm.getHeight(), m, true);  
            }  
            return bm;  
        }  
    }  
          
}
