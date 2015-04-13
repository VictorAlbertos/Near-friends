package utilities.image_picker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class PickerImage {

    private Activity _activity;
    private static OnPictureTaken _onPictureTaken;
    private static boolean useGallery = false;
    private static boolean useCamera = false;

    public PickerImage(Activity activity){
        _activity = activity;
    }

    public PickerImage(Activity activity, OnPictureTaken onPictureTaken){
        _activity = activity;
        _onPictureTaken = onPictureTaken;
    }

    public void sendCameraIntent(){
        useCamera = true;
        useGallery = false;
        _activity.startActivity(new Intent(_activity, PickerImageActivity.class));
    }

    public void sendCameraIntent(OnPictureTaken onPictureTaken){
        _onPictureTaken = onPictureTaken;
        sendCameraIntent();
    }

    public void sendGalleryIntent(){
        useCamera = false;
        useGallery = true;
        _activity.startActivity(new Intent(_activity, PickerImageActivity.class));
    }

    public void sendGalleryIntent(OnPictureTaken onPictureTaken){
        _onPictureTaken = onPictureTaken;
        sendGalleryIntent();
    }

    public static class PickerImageActivity extends Activity {

        private static final int PICKER_REQUEST = 0;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            if(useCamera){
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), PICKER_REQUEST);
            }else if(useGallery){
                startActivityForResult(new Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT)
                        .addCategory(Intent.CATEGORY_OPENABLE), PICKER_REQUEST);
            }
        }

        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if(data!=null && requestCode == PICKER_REQUEST) {
                File originalFile = new File(getRealPathFromURI(this, data.getData()));
                Bitmap bitmap = getBitmapFromFile(originalFile);
                File newFileImage = saveImageOnSD(bitmap, originalFile);
                if(_onPictureTaken != null) _onPictureTaken.pictureTaken(bitmap, newFileImage);
            }
            super.onBackPressed();
        }

        private File saveImageOnSD(Bitmap bitmap, File originalFile) {
            String pathDir = getNameApp() + "_photos";
            File dir = new File(Environment.getExternalStorageDirectory() + "/" + pathDir);
            if (!dir.exists()) dir.mkdir();

            String time = String.valueOf(Calendar.getInstance().getTimeInMillis());
            String fileName = time + "_" + originalFile.getName();

            File fileImage = new File(dir, fileName);
            try {fileImage.createNewFile();}
            catch (IOException exception) {exception.printStackTrace();}

            copyImage(bitmap, fileImage);
            return fileImage;
        }

        private String getNameApp() {
            PackageManager lPackageManager = this.getPackageManager();
            ApplicationInfo lApplicationInfo = null;
            try {
                lApplicationInfo = lPackageManager.getApplicationInfo(this.getApplicationInfo().packageName, 0);
            } catch (final PackageManager.NameNotFoundException e) {
            }
            return (String) (lApplicationInfo != null ? lPackageManager.getApplicationLabel(lApplicationInfo) : "app");
        }

        public void copyImage(Bitmap bitmap, File dst)  {
            try {
                FileOutputStream out = new FileOutputStream(dst);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out);
                out.flush();
                out.close();
            } catch (Exception exception){exception.printStackTrace();}
        }

        private Bitmap getBitmapFromFile(File file) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            return fixOrientation(bitmap, file);
        }

        public Bitmap fixOrientation(Bitmap bitmap, File file) {
            try {
                ExifInterface ei = new ExifInterface(file.getAbsolutePath());
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                if (orientation == ExifInterface.ORIENTATION_ROTATE_90)bitmap = rotateImage(bitmap, 90);
                else if (orientation == ExifInterface.ORIENTATION_ROTATE_180)bitmap = rotateImage(bitmap, 180);
            } catch (IOException e) { e.printStackTrace();}
            return bitmap;
        }

        public Bitmap rotateImage(Bitmap source, float angle){
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        }

        private String getRealPathFromURI(Context context, Uri contentUri) {
            Cursor cursor = null;
            try {
                String[] data = {MediaStore.Images.Media.DATA};
                cursor = context.getContentResolver().query(contentUri, data, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            } finally {if (cursor != null) cursor.close();}
        }

    }

}
