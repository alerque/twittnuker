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

package de.vanita5.twittnuker.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import de.vanita5.twittnuker.Constants;
import de.vanita5.twittnuker.R;
import de.vanita5.twittnuker.model.Draft;
import de.vanita5.twittnuker.model.DraftCursorIndices;
import de.vanita5.twittnuker.model.ParcelableMedia;
import de.vanita5.twittnuker.model.ParcelableMediaUpdate;
import de.vanita5.twittnuker.model.util.ParcelableMediaUtils;
import de.vanita5.twittnuker.util.DataStoreUtils;
import de.vanita5.twittnuker.util.JsonSerializer;
import de.vanita5.twittnuker.util.MediaLoaderWrapper;
import de.vanita5.twittnuker.util.MediaLoadingHandler;
import de.vanita5.twittnuker.util.SharedPreferencesWrapper;
import de.vanita5.twittnuker.util.TwidereArrayUtils;
import de.vanita5.twittnuker.util.Utils;
import de.vanita5.twittnuker.util.dagger.GeneralComponentHelper;
import de.vanita5.twittnuker.view.holder.DraftViewHolder;

import javax.inject.Inject;

public class DraftsAdapter extends SimpleCursorAdapter implements Constants {

    @Inject
    MediaLoaderWrapper mImageLoader;
    @Inject
    SharedPreferencesWrapper mPreferences;
    private final MediaLoadingHandler mMediaLoadingHandler;
    private final int mMediaPreviewStyle;

    private float mTextSize;
    private DraftCursorIndices mIndices;

    public DraftsAdapter(final Context context) {
        super(context, R.layout.list_item_draft, null, new String[0], new int[0], 0);
        GeneralComponentHelper.build(context).inject(this);
        mMediaLoadingHandler = new MediaLoadingHandler(R.id.media_preview_progress);
        mMediaPreviewStyle = Utils.getMediaPreviewStyle(mPreferences.getString(KEY_MEDIA_PREVIEW_STYLE, null));
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        final DraftViewHolder holder = (DraftViewHolder) view.getTag();
        final long[] accountIds = TwidereArrayUtils.parseLongArray(cursor.getString(mIndices.account_ids), ',');
        final String text = cursor.getString(mIndices.text);
        final ParcelableMediaUpdate[] mediaUpdates = JsonSerializer.parseArray(cursor.getString(mIndices.media),
                ParcelableMediaUpdate.class);
        final long timestamp = cursor.getLong(mIndices.timestamp);
        String actionType = cursor.getString(mIndices.action_type);
        final String actionName = getActionName(context, actionType);
        holder.media_preview_container.setStyle(mMediaPreviewStyle);
        if (actionType == null) {
            actionType = Draft.Action.UPDATE_STATUS;
        }
        switch (actionType) {
            case Draft.Action.UPDATE_STATUS:
            case Draft.Action.UPDATE_STATUS_COMPAT_1:
            case Draft.Action.UPDATE_STATUS_COMPAT_2:
            case Draft.Action.REPLY:
            case Draft.Action.QUOTE: {
                final ParcelableMedia[] media = ParcelableMediaUtils.fromMediaUpdates(mediaUpdates);
                holder.media_preview_container.setVisibility(View.VISIBLE);
                holder.media_preview_container.displayMedia(media, mImageLoader, null, -1, null,
                        mMediaLoadingHandler);
                break;
            }
            default: {
                holder.media_preview_container.setVisibility(View.GONE);
                break;
            }
        }
        holder.content.drawEnd(DataStoreUtils.getAccountColors(context, accountIds));
        holder.setTextSize(mTextSize);
        final boolean emptyContent = TextUtils.isEmpty(text);
        if (emptyContent) {
            holder.text.setText(R.string.empty_content);
        } else {
            holder.text.setText(text);
        }
        holder.text.setTypeface(holder.text.getTypeface(), emptyContent ? Typeface.ITALIC : Typeface.NORMAL);

        if (timestamp > 0) {
            final String timeString = Utils.formatSameDayTime(context, timestamp);
            holder.time.setText(context.getString(R.string.action_name_saved_at_time, actionName, timeString));
        } else {
            holder.time.setText(actionName);
        }
    }

    @Override
    public View newView(final Context context, final Cursor cursor, final ViewGroup parent) {
        final View view = super.newView(context, cursor, parent);
        final Object tag = view.getTag();
        if (!(tag instanceof DraftViewHolder)) {
            view.setTag(new DraftViewHolder(view));
        }
        return view;
    }

    public void setTextSize(final float text_size) {
        mTextSize = text_size;
    }

    @Override
    public Cursor swapCursor(final Cursor c) {
        final Cursor old = super.swapCursor(c);
        if (c != null) {
            mIndices = new DraftCursorIndices(c);
        }
        return old;
    }

    private static String getActionName(final Context context, final String actionType) {
        if (TextUtils.isEmpty(actionType)) return context.getString(R.string.update_status);
        switch (actionType) {
            case Draft.Action.UPDATE_STATUS:
            case Draft.Action.UPDATE_STATUS_COMPAT_1:
            case Draft.Action.UPDATE_STATUS_COMPAT_2:
                return context.getString(R.string.update_status);
            case Draft.Action.REPLY:
                return context.getString(R.string.reply);
            case Draft.Action.QUOTE:
                return context.getString(R.string.quote);
            case Draft.Action.SEND_DIRECT_MESSAGE:
            case Draft.Action.SEND_DIRECT_MESSAGE_COMPAT:
                return context.getString(R.string.send_direct_message);
        }
        return null;
    }
}