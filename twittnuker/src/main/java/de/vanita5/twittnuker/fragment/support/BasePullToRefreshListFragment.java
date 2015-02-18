/*
 * Twittnuker - Twitter client for Android
 *
 * Copyright (C) 2013-2015 vanita5 <mail@vanita5.de>
 *
 * This program incorporates a modified version of Twidere.
 * Copyright (C) 2012-2015 Mariotaku Lee <mariotaku.lee@gmail.com>
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

package de.vanita5.twittnuker.fragment.support;

import android.app.Activity;
import android.graphics.Rect;
import android.support.v4.app.FragmentActivity;

import de.vanita5.twittnuker.activity.iface.IControlBarActivity;
import de.vanita5.twittnuker.activity.iface.IControlBarActivity.ControlBarOffsetListener;
import de.vanita5.twittnuker.fragment.iface.IBasePullToRefreshFragment;

public abstract class BasePullToRefreshListFragment extends BaseSupportListFragment implements
        IBasePullToRefreshFragment, ControlBarOffsetListener {


	@Override
	public boolean isRefreshing() {
        return false;
	}

    @Override
    public void onDetach() {
        final FragmentActivity activity = getActivity();
        if (activity instanceof IControlBarActivity) {
            ((IControlBarActivity) activity).unregisterControlBarOffsetListener(this);
        }
        super.onDetach();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof IControlBarActivity) {
            ((IControlBarActivity) activity).registerControlBarOffsetListener(this);
        }
    }

	@Override
	public void setRefreshing(final boolean refresh) {
	}

	@Override
	public boolean triggerRefresh() {
        onRefresh();
		setRefreshing(true);
		return true;
	}

    @Override
    protected void fitSystemWindows(Rect insets) {
        super.fitSystemWindows(insets);
   }


    @Override
    public void onControlBarOffsetChanged(IControlBarActivity activity, float offset) {
    }
}