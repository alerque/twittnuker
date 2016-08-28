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

package de.vanita5.twittnuker.fragment

import android.content.ContentResolver
import android.content.Context
import android.support.v4.app.DialogFragment
import de.vanita5.twittnuker.Constants
import de.vanita5.twittnuker.app.TwittnukerApplication
import de.vanita5.twittnuker.util.*
import de.vanita5.twittnuker.util.dagger.GeneralComponentHelper
import javax.inject.Inject

open class BaseDialogFragment : DialogFragment(), Constants {

    @Inject
    lateinit var twitterWrapper: AsyncTwitterWrapper
    @Inject
    lateinit var userColorNameManager: UserColorNameManager
    @Inject
    lateinit var preferences: SharedPreferencesWrapper
    @Inject
    lateinit var validator: TwidereValidator
    @Inject
    lateinit var keyboardShortcutsHandler: KeyboardShortcutsHandler


    val application: TwittnukerApplication?
        get() {
            val activity = activity
            if (activity != null) return activity.application as TwittnukerApplication
            return null
        }

    val contentResolver: ContentResolver
        get() = activity.contentResolver

    fun getSystemService(name: String): Any? {
        val activity = activity
        if (activity != null) return activity.getSystemService(name)
        return null
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        GeneralComponentHelper.build(context!!).inject(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        DebugModeUtils.watchReferenceLeak(this)
    }

}