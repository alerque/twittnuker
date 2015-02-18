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

package de.vanita5.twittnuker.loader.support;

import android.content.Context;

import de.vanita5.twittnuker.model.ParcelableStatus;

import java.util.List;

public class UserMentionsLoader extends TweetSearchLoader {

    public UserMentionsLoader(final Context context, final long accountId, final String screenName,
                              final long maxId, final long sinceId, final List<ParcelableStatus> data,
                              final String[] savedStatusesArgs, final int tabPosition, boolean fromUser) {
        super(context, accountId, screenName, sinceId, maxId, data, savedStatusesArgs, tabPosition, fromUser);
	}

	@Override
	protected String processQuery(final String query) {
		if (query == null) return null;
		final String screenName = query.startsWith("@") ? query : String.format("@%s", query);
		return String.format("%s exclude:retweets", screenName);
	}

}
