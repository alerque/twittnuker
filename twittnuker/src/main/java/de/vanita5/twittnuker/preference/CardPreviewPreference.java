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

package de.vanita5.twittnuker.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.vanita5.twittnuker.Constants;
import de.vanita5.twittnuker.R;
import de.vanita5.twittnuker.util.TwidereLinkify;
import de.vanita5.twittnuker.view.holder.StatusViewHolder;
import de.vanita5.twittnuker.view.holder.StatusViewHolder.DummyStatusHolderAdapter;

public class CardPreviewPreference extends Preference implements Constants, OnSharedPreferenceChangeListener {

	private final LayoutInflater mInflater;
	private final SharedPreferences mPreferences;
	private final TwidereLinkify mLinkify;
    private StatusViewHolder mHolder;
	private boolean mCompactModeChanged;
    private DummyStatusHolderAdapter mAdapter;

	public CardPreviewPreference(final Context context) {
		this(context, null);
	}

	public CardPreviewPreference(final Context context, final AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CardPreviewPreference(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		mInflater = LayoutInflater.from(context);
		mLinkify = new TwidereLinkify(null);
		mPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		mPreferences.registerOnSharedPreferenceChangeListener(this);
        mAdapter = new DummyStatusHolderAdapter(context);
	}

	@Override
	public View getView(final View convertView, final ViewGroup parent) {
		if (mCompactModeChanged) return super.getView(null, parent);
		return super.getView(convertView, parent);
	}

	@Override
	public void onSharedPreferenceChanged(final SharedPreferences preferences, final String key) {
		if (mHolder == null) return;
		if (KEY_COMPACT_CARDS.equals(key)) {
			mCompactModeChanged = true;
		}
        mAdapter.updateOptions();
		notifyChanged();
	}

	@Override
    protected void onBindView(@NonNull final View view) {
        if (mHolder == null) return;
		mCompactModeChanged = false;
        mHolder.setupViewOptions();
        mHolder.displaySampleStatus();
		super.onBindView(view);
	}

	@Override
	protected View onCreateView(final ViewGroup parent) {
		if (mPreferences != null && mPreferences.getBoolean(KEY_COMPACT_CARDS, false))
            return mInflater.inflate(R.layout.card_item_status_compact, parent, false);
        final View view = mInflater.inflate(R.layout.card_item_status, parent, false);
        mHolder = new StatusViewHolder(mAdapter, view);
        return view;
	}

}