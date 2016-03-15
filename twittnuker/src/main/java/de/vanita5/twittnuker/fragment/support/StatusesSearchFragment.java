/*
 * Twittnuker - Twitter client for Android
 *
 * Copyright (C) 2013-2016 vanita5 <mail@vanit.as>
 *
 * This program incorporates a modified version of Twidere.
 * Copyright (C) 2012-2016 Mariotaku Lee <mariotaku.lee@gmail.com>
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

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;

import de.vanita5.twittnuker.loader.support.TweetSearchLoader;
import de.vanita5.twittnuker.model.ParcelableStatus;
import de.vanita5.twittnuker.model.UserKey;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class StatusesSearchFragment extends ParcelableStatusesFragment {

    @Override
    protected Loader<List<ParcelableStatus>> onCreateStatusesLoader(final Context context,
                                                                    final Bundle args,
                                                                    final boolean fromUser) {
        setRefreshing(true);
        final UserKey accountKey = args.getParcelable(EXTRA_ACCOUNT_KEY);
        final String maxId = args.getString(EXTRA_MAX_ID);
        final String sinceId = args.getString(EXTRA_SINCE_ID);
        final int page = args.getInt(EXTRA_PAGE, -1);
        final String query = args.getString(EXTRA_QUERY);
        final int tabPosition = args.getInt(EXTRA_TAB_POSITION, -1);
        final boolean makeGap = args.getBoolean(EXTRA_MAKE_GAP, true);
        return new TweetSearchLoader(getActivity(), accountKey, query, sinceId, maxId, page,
                getAdapterData(), getSavedStatusesFileArgs(), tabPosition, fromUser, makeGap);
    }

    @Override
    protected void fitSystemWindows(Rect insets) {
        super.fitSystemWindows(insets);
    }

    @Override
    protected String[] getSavedStatusesFileArgs() {
        final Bundle args = getArguments();
        if (args == null) return null;
        final UserKey accountKey = args.getParcelable(EXTRA_ACCOUNT_KEY);
        final String query = args.getString(EXTRA_QUERY);
        return new String[]{AUTHORITY_SEARCH_TWEETS, "account" + accountKey, "query" + query};
    }


    @Override
    protected String getReadPositionTagWithArguments() {
        final Bundle args = getArguments();
        assert args != null;
        final int tabPosition = args.getInt(EXTRA_TAB_POSITION, -1);
        StringBuilder sb = new StringBuilder("search_");
        if (tabPosition < 0) return null;
        final String query = args.getString(EXTRA_QUERY);
        if (query == null) return null;
        final String encodedQuery;
        try {
            encodedQuery = URLEncoder.encode(query, "UTF-8").replaceAll("[^\\w\\d]", "_");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
        sb.append(encodedQuery);
        return sb.toString();
    }

}