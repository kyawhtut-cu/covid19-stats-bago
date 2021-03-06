package gov.mm.covid19statsbago.mmfont.components;

import android.content.Context;
import android.util.AttributeSet;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import gov.mm.covid19statsbago.mmfont.MMFontUtils;


/**
 * Created by mgkaung on 4/1/2020.
 */
public class MMBottomNavigation extends BottomNavigationView {

    public MMBottomNavigation(Context context) {
        super(context);
    }

    public MMBottomNavigation(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MMBottomNavigation(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (!isInEditMode())
        MMFontUtils.applyMMFontToMenu(this.getMenu(),this.getContext());
    }

}
