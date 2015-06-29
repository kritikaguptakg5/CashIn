package com.mantralabsglobal.cashin.utils;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.mantralabsglobal.cashin.businessobjects.AndroidImage;

/**
 * Created by pk on 6/28/2015.
 */
public class CameraUtils {

    public static void createBlackAndWhite(final String filePath, final CameraUtils.Listener listener) {


        AsyncTask<String, Void, Bitmap> asyncTask = new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... params) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                Bitmap src = BitmapFactory.decodeFile(filePath, options);

                Log.d("CameraUtils", "File decoded");
                int width = src.getWidth();
                int height = src.getHeight();
                // create output bitmap
                Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
                // color information
                int A, R, G, B;
                int pixel;

                // scan through all pixels
                for (int x = 0; x < width; ++x) {
                    for (int y = 0; y < height; ++y) {
                        // get pixel color
                        pixel = src.getPixel(x, y);
                        A = Color.alpha(pixel);
                        R = Color.red(pixel);
                        G = Color.green(pixel);
                        B = Color.blue(pixel);
                        int gray = (int) (0.2989 * R + 0.5870 * G + 0.1140 * B);

                        // use 128 as threshold, above -> white, below -> black
                        if (gray > 120)
                            gray = 255;
                        else
                            gray = 0;
                        // set new pixel color to output bitmap
                        bmOut.setPixel(x, y, Color.argb(A, gray, gray, gray));
                    }
                }
                Log.d("CameraUtils", "File converted to binary");
                return bmOut;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                listener.onComplete(result);
            }
        };

        asyncTask.execute(filePath);
    }

    public static AndroidImage applyThreshold(AndroidImage imageIn, int threshold) {

        // The Resulting image
        AndroidImage imageOut;

        // Initiate the Output image
        imageOut = new AndroidImage(imageIn.getImage());

        // Do Threshold process
        for(int y=0; y<imageIn.getHeight(); y++){
            for(int x=0; x<imageIn.getWidth(); x++){

                if(imageOut.getRComponent(x,y) < threshold){
                    imageOut.setPixelColor(x, y, 0,0,0);
                }
                else{
                    imageOut.setPixelColor(x, y, 255,255,255);
                }
            }
        }

        // Return final image
        return imageOut;
    }

    // The luminance method
    private static Bitmap toGray(Bitmap original) {

        int alpha, red, green, blue;
        int newPixel;

        Bitmap lum = Bitmap.createBitmap(original.getWidth(),original.getHeight(),Bitmap.Config.ALPHA_8);

        for(int i=0; i<original.getWidth(); i++) {
            for(int j=0; j<original.getHeight(); j++) {
                int pixel = original.getPixel(i,j);
                // Get pixels by R, G, B
                alpha = Color.alpha(pixel);
                red =  Color.red(pixel);
                green = Color.green(pixel);
                blue = Color.blue(pixel);

                red = (int) (0.21 * red + 0.71 * green + 0.07 * blue);
                // Return back to original format
                //newPixel = colorToRGB(alpha, red, red, red);

                // Write pixels into image
                lum.setPixel(i, j, Color.argb(alpha, red, red, red));

            }
        }

        return lum;

    }

    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,
                Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    // Return histogram of grayscale image
    public static int[] imageHistogram(Bitmap input) {

        int[] histogram = new int[256];

        for(int i=0; i<histogram.length; i++) histogram[i] = 0;

        for(int i=0; i<input.getWidth(); i++) {
            for(int j=0; j<input.getHeight(); j++) {
                int pixel = input.getPixel(i,j);
                int red = Color.red(pixel);
                histogram[red]++;
            }
        }

        return histogram;

    }
    // Get binary treshold using Otsu's method
    private static int otsuTreshold(Bitmap original) {

        int[] histogram = imageHistogram(original);
        int total = original.getHeight() * original.getWidth();

        float sum = 0;
        for(int i=0; i<256; i++) sum += i * histogram[i];

        float sumB = 0;
        int wB = 0;
        int wF = 0;

        float varMax = 0;
        int threshold = 0;

        for(int i=0 ; i<256 ; i++) {
            wB += histogram[i];
            if(wB == 0) continue;
            wF = total - wB;

            if(wF == 0) break;

            sumB += (float) (i * histogram[i]);
            float mB = sumB / wB;
            float mF = (sum - sumB) / wF;

            float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);

            if(varBetween > varMax) {
                varMax = varBetween;
                threshold = i;
            }
        }

        return threshold;

    }

    public interface Listener {
        public void onComplete(Bitmap bmp);
    }
}
