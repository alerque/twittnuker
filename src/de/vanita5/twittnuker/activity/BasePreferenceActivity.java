/*
 * Twittnuker - Twitter client for Android
 *
 * Copyright (C) 2013-2014 vanita5 <mail@vanita5.de>
 *
 * This program incorporates a modified version of Twidere.
 * Copyright (C) 2012-2014 Mariotaku Lee <mariotaku.lee@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.vanita5.twittnuker.activity;

import static de.vanita5.twittnuker.util.Utils.restartActivity;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.app.NavUtils;

import de.vanita5.twittnuker.Constants;
import de.vanita5.twittnuker.util.ThemeUtils;

public abstract class BasePreferenceActivity extends PreferenceActivity implements Constants {

    private int mCurrentThemeResource;

	@Override
	public void finish() {
		super.finish();
		overrideCloseAnimationIfNeeded();
	}

    public int getThemeResourceId() {
        return ThemeUtils.getSettingsThemeResource(this);
    }

	public void navigateUpFromSameTask() {
		NavUtils.navigateUpFromSameTask(this);
		overrideCloseAnimationIfNeeded();
	}

	public void overrideCloseAnimationIfNeeded() {
		if (shouldOverrideActivityAnimation()) {
			ThemeUtils.overrideActivityCloseAnimation(this);
		} else {
			ThemeUtils.overrideNormalActivityCloseAnimation(this);
		}
	}

    protected final boolean isThemeChanged() {
        return getThemeResourceId() != mCurrentThemeResource;
    }

	public boolean shouldOverrideActivityAnimation() {
		return true;
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		if (shouldOverrideActivityAnimation()) {
			ThemeUtils.overrideActivityOpenAnimation(this);
		}
        setTheme(mCurrentThemeResource = getThemeResourceId());
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
//    ThemeUtils.applyActionBarBackground(getActionBar(), this, mCurrentThemeResource);
	}

}