package de.vanita5.twittnuker.fragment.support;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;

import de.vanita5.twittnuker.R;
import de.vanita5.twittnuker.model.ParcelableUser;
import de.vanita5.twittnuker.model.ParcelableUserList;
import de.vanita5.twittnuker.util.AsyncTwitterWrapper;
import de.vanita5.twittnuker.util.ThemeUtils;
import de.vanita5.twittnuker.util.Utils;

public class DeleteUserListMembersDialogFragment extends BaseSupportDialogFragment implements
		DialogInterface.OnClickListener {

	public static final String FRAGMENT_TAG = "destroy_user_list_member";

	@Override
	public void onClick(final DialogInterface dialog, final int which) {
		switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				final ParcelableUser[] users = getUsers();
				final ParcelableUserList userList = getUserList();
				final AsyncTwitterWrapper twitter = getTwitterWrapper();
				if (users == null || userList == null || twitter == null) return;
				twitter.deleteUserListMembersAsync(userList.account_id, userList.id, users);
				break;
			default:
				break;
		}
	}

	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final Context wrapped = ThemeUtils.getDialogThemedContext(getActivity());
        final AlertDialog.Builder builder = new AlertDialog.Builder(wrapped);
		final ParcelableUser[] users = getUsers();
		final ParcelableUserList userList = getUserList();
		if (users == null || userList == null) throw new NullPointerException();
		if (users.length == 1) {
			final ParcelableUser user = users[0];
            final String displayName = Utils.getDisplayName(wrapped, user.id, user.name, user.screen_name);
			builder.setTitle(getString(R.string.delete_user, displayName));
			builder.setMessage(getString(R.string.delete_user_from_list_confirm, displayName, userList.name));
		} else {
			builder.setTitle(R.string.delete_users);
			final Resources res = getResources();
			final String message = res.getQuantityString(R.plurals.delete_N_users_from_list_confirm, users.length,
					users.length, userList.name);
			builder.setMessage(message);
		}
		builder.setPositiveButton(android.R.string.ok, this);
		builder.setNegativeButton(android.R.string.cancel, null);
		return builder.create();
	}

	private ParcelableUserList getUserList() {
		final Bundle args = getArguments();
		if (!args.containsKey(EXTRA_USER_LIST)) return null;
		return args.getParcelable(EXTRA_USER_LIST);
	}

	private ParcelableUser[] getUsers() {
		final Bundle args = getArguments();
		if (!args.containsKey(EXTRA_USERS)) return null;
		final Parcelable[] array = args.getParcelableArray(EXTRA_USERS);
		if (array == null) return null;
		final ParcelableUser[] users = new ParcelableUser[array.length];
		for (int i = 0, j = users.length; i < j; i++) {
			users[i] = (ParcelableUser) array[i];
		}
		return users;
	}

	public static DeleteUserListMembersDialogFragment show(final FragmentManager fm, final ParcelableUserList userList,
			final ParcelableUser... users) {
		final Bundle args = new Bundle();
		args.putParcelable(EXTRA_USER_LIST, userList);
		args.putParcelableArray(EXTRA_USERS, users);
		final DeleteUserListMembersDialogFragment f = new DeleteUserListMembersDialogFragment();
		f.setArguments(args);
		f.show(fm, FRAGMENT_TAG);
		return f;
	}
}
