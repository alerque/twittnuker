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

package de.vanita5.twittnuker.activity.support;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.BackStackEntryTrojan;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentManagerTrojan;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import de.vanita5.twittnuker.R;
import de.vanita5.twittnuker.model.Panes;
import de.vanita5.twittnuker.util.ThemeUtils;
import de.vanita5.twittnuker.view.SlidingPaneView;

@SuppressLint("Registered")
public class DualPaneActivity extends BaseSupportActivity implements OnBackStackChangedListener {

	private SharedPreferences mPreferences;

	private SlidingPaneView mSlidingPane;

	private Fragment mDetailsFragment;

	private boolean mDualPaneInPortrait, mDualPaneInLandscape;

	public Fragment getDetailsFragment() {
		return mDetailsFragment;
	}

	public final Fragment getLeftPaneFragment() {
		final FragmentManager fm = getSupportFragmentManager();
		final Fragment leftPaneFragment = fm.findFragmentById(PANE_LEFT);
		return leftPaneFragment;
	}

	public final Fragment getRightPaneFragment() {
		final FragmentManager fm = getSupportFragmentManager();
		final Fragment rightPaneFragment = fm.findFragmentById(PANE_RIGHT);
		return rightPaneFragment;
	}

	public SlidingPaneView getSlidingPane() {
		return mSlidingPane;
	}

	public final boolean isDualPaneMode() {
		return findViewById(PANE_LEFT) instanceof ViewGroup && findViewById(PANE_RIGHT) instanceof ViewGroup;
	}

	public final boolean isLeftPaneUsed() {
		final FragmentManager fm = getSupportFragmentManager();
		final Fragment f = fm.findFragmentById(PANE_LEFT);
		return f != null && f.isAdded();
	}

	public final boolean isRightPaneOpened() {
		return mSlidingPane != null && mSlidingPane.isRightPaneOpened();
	}

	public final boolean isRightPaneUsed() {
		final FragmentManager fm = getSupportFragmentManager();
		final Fragment f = fm.findFragmentById(PANE_RIGHT);
		return f != null && f.isAdded();
	}

	@Override
	public void onBackStackChanged() {
		if (!isDualPaneMode()) return;
		final FragmentManager fm = getSupportFragmentManager();
		final int count = fm.getBackStackEntryCount();
		final Fragment leftPaneFragment = fm.findFragmentById(PANE_LEFT);
		final Fragment rightPaneFragment = fm.findFragmentById(PANE_RIGHT);
		final boolean leftPaneUsed = leftPaneFragment != null && leftPaneFragment.isAdded();
		final boolean rightPaneUsed = rightPaneFragment != null && rightPaneFragment.isAdded();
		if (count > 0) {
			final BackStackEntry entry = fm.getBackStackEntryAt(count - 1);
			if (entry == null) return;
			final Fragment fragment = BackStackEntryTrojan.getFragmentInBackStackRecord(entry);
			if (fragment instanceof Panes.Right) {
				showRightPane();
			} else if (fragment instanceof Panes.Left) {
				showLeftPane();
			}
		} else {
			if (fm.findFragmentById(getMainViewId()) != null || leftPaneUsed) {
				showLeftPane();
			} else if (rightPaneUsed) {
				showRightPane();
			}
		}
		updateMainViewVisibility();
	}

	@Override
	public void onContentChanged() {
		super.onContentChanged();
		mSlidingPane = (SlidingPaneView) findViewById(R.id.sliding_pane);
	}

	public final void showAtPane(final int pane, final Fragment fragment, final boolean addToBackStack) {
		if (isStateSaved()) return;
		final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
		switch (pane) {
			case PANE_LEFT: {
				showLeftPane();
				ft.replace(PANE_LEFT, fragment);
				break;
			}
			case PANE_RIGHT: {
				showRightPane();
				ft.replace(PANE_RIGHT, fragment);
				break;
			}
		}
		if (addToBackStack) {
			ft.addToBackStack(null);
		}
		ft.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.commit();
		mDetailsFragment = fragment;
	}

	public final void showFragment(final Fragment fragment, final boolean add_to_backstack) {
		if (fragment instanceof Panes.Right) {
			showAtPane(PANE_RIGHT, fragment, add_to_backstack);
		} else {
			showAtPane(PANE_LEFT, fragment, add_to_backstack);
		}
	}

	public final void showLeftPane() {
		if (mSlidingPane != null) {
			mSlidingPane.hideRightPane();
		}
	}

	public final void showRightPane() {
		if (mSlidingPane != null) {
			mSlidingPane.showRightPane();
		}
	}

	protected int getDualPaneLayoutRes() {
		return R.layout.base_dual_pane;
	}

	protected int getMainViewId() {
		return R.id.main;
	}

	protected int getNormalLayoutRes() {
		return R.layout.base;
	}

    protected Drawable getPaneBackground() {
        return ThemeUtils.getWindowBackground(this, getCurrentThemeResourceId());
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		mPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		super.onCreate(savedInstanceState);
		final Resources res = getResources();
		final int orientation = res.getConfiguration().orientation;
		final int layout;
		final boolean is_large_screen = res.getBoolean(R.bool.is_large_screen);
		mDualPaneInPortrait = mPreferences.getBoolean(KEY_DUAL_PANE_IN_PORTRAIT, is_large_screen);
		mDualPaneInLandscape = mPreferences.getBoolean(KEY_DUAL_PANE_IN_LANDSCAPE, is_large_screen);
		switch (orientation) {
			case Configuration.ORIENTATION_LANDSCAPE:
				layout = mDualPaneInLandscape || shouldForceEnableDualPaneMode() ? getDualPaneLayoutRes()
						: getNormalLayoutRes();
				break;
			case Configuration.ORIENTATION_PORTRAIT:
				layout = mDualPaneInPortrait || shouldForceEnableDualPaneMode() ? getDualPaneLayoutRes()
						: getNormalLayoutRes();
				break;
			default:
				layout = getNormalLayoutRes();
				break;
		}
		setContentView(layout);
		if (mSlidingPane != null) {
			mSlidingPane.setRightPaneBackground(getPaneBackground());
		}
		final FragmentManager fm = getSupportFragmentManager();
		fm.addOnBackStackChangedListener(this);
		if (savedInstanceState != null) {
			updateMainViewVisibility();
		}
	}

	@Override
	protected void onStart() {
		final FragmentManager fm = getSupportFragmentManager();
		if (!isDualPaneMode() && !FragmentManagerTrojan.isStateSaved(fm)) {
			// for (int i = 0, count = fm.getBackStackEntryCount(); i < count;
			// i++) {
			// fm.popBackStackImmediate();
			// }
			fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		}
		super.onStart();
		final Resources res = getResources();
		final boolean is_large_screen = res.getBoolean(R.bool.is_large_screen);
		final boolean dual_pane_in_portrait = mPreferences.getBoolean(KEY_DUAL_PANE_IN_PORTRAIT,
				is_large_screen);
		final boolean dual_pane_in_landscape = mPreferences.getBoolean(KEY_DUAL_PANE_IN_LANDSCAPE,
				is_large_screen);
		final int orientation = res.getConfiguration().orientation;
		switch (orientation) {
			case Configuration.ORIENTATION_LANDSCAPE:
				if (mDualPaneInLandscape != dual_pane_in_landscape) {
					restart();
				}
				break;
			case Configuration.ORIENTATION_PORTRAIT:
				if (mDualPaneInPortrait != dual_pane_in_portrait) {
					restart();
				}
				break;
		}
	}

	protected boolean shouldForceEnableDualPaneMode() {
		return false;
	}

	private void updateMainViewVisibility() {
		if (!isDualPaneMode()) return;
		final FragmentManager fm = getSupportFragmentManager();
		final Fragment leftPaneFragment = fm.findFragmentById(PANE_LEFT);
		final boolean leftPaneUsed = leftPaneFragment != null && leftPaneFragment.isAdded();
		final View mainView = findViewById(getMainViewId());
		if (mainView != null) {
			final int visibility = leftPaneUsed ? View.GONE : View.VISIBLE;
			// Visibility changed, so start animation.
			if (mainView.getVisibility() != visibility) {
				final Animation anim = AnimationUtils.loadAnimation(this, leftPaneUsed ? android.R.anim.fade_out
						: android.R.anim.fade_in);
				mainView.startAnimation(anim);
			}
			mainView.setVisibility(visibility);
		}
	}
}
