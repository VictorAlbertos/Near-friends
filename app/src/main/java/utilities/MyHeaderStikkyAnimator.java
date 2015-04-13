package utilities;

import android.graphics.Point;
import android.view.View;

import com.hacer_app.near_friends.R;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.res.DimensionPixelOffsetRes;

import it.carlom.stikkyheader.core.animator.AnimatorBuilder;
import it.carlom.stikkyheader.core.animator.HeaderStikkyAnimator;

@EBean
public class MyHeaderStikkyAnimator extends HeaderStikkyAnimator {

    @DimensionPixelOffsetRes(R.dimen._10dp)int marginLeft;
    @DimensionPixelOffsetRes(R.dimen._5dp)int marginBottom;

    @Override public AnimatorBuilder getAnimatorBuilder() {
        View rl_dynamic_header = getHeader().findViewById(R.id.rl_dynamic_header);
        View rl_avatar = getHeader().findViewById(R.id.rl_avatar);
        View v_map = getHeader().findViewById(R.id.v_map);
        View v_target_blur = getHeader().findViewById(R.id.v_target_blur);

        float velocityParallax = 0.5f;
        float scaleX = 0.5f;
        float scaleY = 0.5f;
        float startFade = 1f;
        float endFade = 0.8f;

        AnimatorBuilder animatorBuilder = AnimatorBuilder.create()
                .applyVerticalParallax(rl_dynamic_header, velocityParallax)
                .applyScale(rl_avatar, scaleX, scaleY)
                .applyFade(v_map, startFade, endFade)
                .applyFade(v_target_blur, startFade, endFade)
                .applyTranslation(rl_avatar, new Point(marginLeft, marginBottom));
        return animatorBuilder;
    }
}
