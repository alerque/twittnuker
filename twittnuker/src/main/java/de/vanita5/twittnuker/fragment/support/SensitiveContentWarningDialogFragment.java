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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;

import de.vanita5.twittnuker.R;
import de.vanita5.twittnuker.model.ParcelableMedia;
import de.vanita5.twittnuker.util.ThemeUtils;
import de.vanita5.twittnuker.util.Utils;

import static de.vanita5.twittnuker.util.Utils.openMediaDirectly;

public class SensitiveContentWarningDialogFragment extends BaseSupportDialogFragment implements DialogInterface.OnClickListener {

	@Override
	public void onClick(final DialogInterface dialog, final int which) {
		switch (which) {
			case DialogInterface.BUTTON_POSITIVE: {
				final Context context = getActivity();
				final Bundle args = getArguments();
				if (args == null || context == null) return;
				final long accountId = args.getLong(EXTRA_ACCOUNT_ID, -1);
                final ParcelableMedia current = args.getParcelable(EXTRA_CURRENT_MEDIA);
                final ParcelableMedia[] media = Utils.newParcelableArray(args.getParcelableArray(EXTRA_MEDIA),
						ParcelableMedia.CREATOR);
                openMediaDirectly(context, accountId, current, media);
				break;
			}
		}

	}

    @NonNull
	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final Context wrapped = ThemeUtils.getDialogThemedContext(getActivity());
        final AlertDialog.Builder builder = new AlertDialog.Builder(wrapped);
		builder.setTitle(android.R.string.dialog_alert_title);
		builder.setMessage(R.string.sensitive_content_warning);
		builder.setPositiveButton(android.R.string.ok, this);
		builder.setNegativeButton(android.R.string.cancel, null);
		return builder.create();
	}

}
