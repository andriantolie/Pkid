package com.example.altitudelabs.pkid;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by altitudelabs on 17/4/2016.
 */
public class Utils {
    private final static String TAG = Utils.class.getSimpleName();
    public enum ImageType {
        JPG,
        PNG
    }

    /**
     * Create a File for saving an image permanently
     */
    private static File getPermanentOutputImageFile(ImageType imageType) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Pkid");
        return getOutputImageFile(mediaStorageDir, imageType);
    }

    private static File getOutputImageFile(File mediaStorageDir, ImageType imageType) {
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        String fileExtension;
        if (imageType == ImageType.PNG) {
            fileExtension = ".png";
        } else {
            fileExtension = ".jpg";
        }
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + fileExtension);

        return mediaFile;
    }




    private static File saveBitmapToFile(Context context, Bitmap bitmap, ImageType imageType, boolean saveToDevicePermanently) throws IOException {
        File pictureFile;
        if (saveToDevicePermanently) {
            pictureFile = Utils.getPermanentOutputImageFile(ImageType.PNG);
        } else {
            pictureFile = Utils.getPermanentOutputImageFile(ImageType.PNG);
        }

        if (pictureFile == null) {
            Log.d(TAG, "Error creating media file, check storage permissions");
            return null;
        }

        FileOutputStream output = new FileOutputStream(pictureFile);

        Bitmap.CompressFormat compressFormat;
        if (imageType == ImageType.PNG) {
            compressFormat = Bitmap.CompressFormat.PNG;
        } else {
            compressFormat = Bitmap.CompressFormat.JPEG;
        }
        bitmap.compress(compressFormat, 100, output);

        output.flush();
        output.close();

        // If it saves to device permanently, notify the device's gallery to scan new image
        if (saveToDevicePermanently) {
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(pictureFile)));
        }
        return pictureFile;
    }


    public static File saveDrawingBitmapToFile(Context context, Bitmap bitmap, boolean saveToDevicePermanently) throws IOException {
        return saveBitmapToFile(context, bitmap, ImageType.PNG, saveToDevicePermanently);
    }
    public static Bitmap createBitmapFromView(View view) {
        // Clear any focus from the view first to remove any cursor
        view.clearFocus();
        Bitmap drawingBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        drawingBitmap.eraseColor(Color.TRANSPARENT);
        Canvas canvas = new Canvas(drawingBitmap);
        view.draw(canvas);
        return drawingBitmap;
    }


    /**
     * Apply color filter transformation to the bitmap
     *
     * @param src                  Bitmap source, it will be recycled after this transformation is applied
     * @param mediaEditFilterType: Specifies type of color filter used
     * @return filtered bitmap
     */

}
