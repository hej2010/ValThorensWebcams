package se.swecookie.valthorens;

import android.content.Context;
import android.util.AttributeSet;

public class PressedImageView extends android.support.v7.widget.AppCompatImageView {

    public PressedImageView(Context context) {
        super(context);
    }

    public PressedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PressedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        setAlpha(pressed ? 0.8f : 1f);
    }

}
