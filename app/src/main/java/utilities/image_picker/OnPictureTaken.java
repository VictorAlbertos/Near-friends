package utilities.image_picker;

import android.graphics.Bitmap;

import java.io.File;

public interface OnPictureTaken {
    void pictureTaken(Bitmap bitmap, File filePath);
}