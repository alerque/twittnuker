package de.vanita5.twittnuker.activity.support;

import static de.vanita5.twittnuker.util.Utils.restartActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;

import com.negusoft.holoaccent.AccentHelper;

import de.vanita5.twittnuker.activity.iface.IThemedActivity;
import de.vanita5.twittnuker.theme.TwidereAccentHelper;
import de.vanita5.twittnuker.util.StrictModeUtils;
import de.vanita5.twittnuker.util.ThemeUtils;
import de.vanita5.twittnuker.util.Utils;


public abstract class BaseSupportThemedActivity extends FragmentActivity implements IThemedActivity {

	private int mCurrentThemeResource, mCurrentThemeColor;
    private AccentHelper mAccentHelper;

	@Override
	public void finish() {
		super.finish();
		overrideCloseAnimationIfNeeded();
	}

	@Override
	public final int getCurrentThemeResourceId() {
		return mCurrentThemeResource;
	}
	
	@Override
	public final Resources getDefaultResources() {
		return super.getResources();
	}

    @Override
    public Resources getResources() {
        return getThemedResources();
    }

    @Override
    public abstract int getThemeColor();

    @Override
    public final Resources getThemedResources() {
        if (mAccentHelper == null) {
            mAccentHelper = new TwidereAccentHelper(ThemeUtils.getUserThemeColor(this));
        }
        return mAccentHelper.getResources(this, super.getResources());
    }

    @Override
    public abstract int getThemeResourceId();

	@Override
	public void navigateUpFromSameTask() {
		NavUtils.navigateUpFromSameTask(this);
		overrideCloseAnimationIfNeeded();
	}

	@Override
	public void overrideCloseAnimationIfNeeded() {
		if (shouldOverrideActivityAnimation()) {
			ThemeUtils.overrideActivityCloseAnimation(this);
		} else {
			ThemeUtils.overrideNormalActivityCloseAnimation(this);
		}
	}

	@Override
	public boolean shouldOverrideActivityAnimation() {
		return true;
	}

	protected final boolean isThemeChanged() {
		return getThemeResourceId() != mCurrentThemeResource || getThemeColor() != mCurrentThemeColor;
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		if (Utils.isDebugBuild()) {
			StrictModeUtils.detectAllVmPolicy();
			StrictModeUtils.detectAllThreadPolicy();
		}

		if (shouldOverrideActivityAnimation()) {
			ThemeUtils.overrideActivityOpenAnimation(this);
		}
		setTheme();
		super.onCreate(savedInstanceState);
		setActionBarBackground();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isThemeChanged()) {
			restart();
		}
	}

	protected final void restart() {
		restartActivity(this);
	}

	private final void setActionBarBackground() {
		ThemeUtils.applyActionBarBackground(getActionBar(), this);
	}

	private final void setTheme() {
		mCurrentThemeResource = getThemeResourceId();
		mCurrentThemeColor = getThemeColor();
		setTheme(mCurrentThemeResource);
	}
}