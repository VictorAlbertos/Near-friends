package utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.view.View;

import org.androidannotations.annotations.EBean;

@EBean
public class BlurEffect {

    public void applyOverlap(final Bitmap sourceToBlur, final View targetOnRender) {
        targetOnRender.post(new Runnable() {
            @Override
            public void run() {
                blur(sourceToBlur, targetOnRender);
            }
        });
    }

    public void applyOverlap(final View sourceToBlur, final View targetOnRender) {
        targetOnRender.post(new Runnable() {
            @Override
            public void run() {
                sourceToBlur.setDrawingCacheEnabled(true);
                Bitmap bitmap = sourceToBlur.getDrawingCache();
                blur(bitmap, targetOnRender);
                sourceToBlur.setDrawingCacheEnabled(false);
            }
        });
    }

    private void blur(Bitmap bitmap, View targetOnRender) {
        if (targetOnRender.getMeasuredWidth() <= 0 || targetOnRender.getMeasuredHeight() <= 0) return;

        float scaleFactor = 4;
        float radius = 9;

        Bitmap overlay = Bitmap.createBitmap((int) (targetOnRender.getMeasuredWidth() / scaleFactor),
                (int) (targetOnRender.getMeasuredHeight() / scaleFactor), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(overlay);
        canvas.translate(-targetOnRender.getLeft() / scaleFactor, -targetOnRender.getTop() / scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bitmap, 0, 0, paint);

        overlay = computeBlur(targetOnRender.getContext(), overlay, (int) radius);
        targetOnRender.setBackground(new BitmapDrawable(targetOnRender.getResources(), overlay));
    }

    private Bitmap computeBlur(Context context, Bitmap sentBitmap, int radius) {
        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        RenderScript renderScript = RenderScript.create(context);
        Allocation input = Allocation.createFromBitmap(renderScript, sentBitmap,
                Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT);
        Allocation output = Allocation.createTyped(renderScript, input.getType());

        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        script.setRadius(radius);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(bitmap);

        renderScript.destroy();
        input.destroy();
        output.destroy();
        script.destroy();

        return bitmap;
    }
}

