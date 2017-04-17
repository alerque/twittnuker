/*
 * Twittnuker - Twitter client for Android
 *
 * Copyright (C) 2013-2017 vanita5 <mail@vanit.as>
 *
 * This program incorporates a modified version of Twidere.
 * Copyright (C) 2012-2017 Mariotaku Lee <mariotaku.lee@gmail.com>
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

package de.vanita5.twittnuker.model.tab.impl

import android.content.Context
import de.vanita5.twittnuker.R
import de.vanita5.twittnuker.annotation.TabAccountFlags
import de.vanita5.twittnuker.constant.IntentConstants.EXTRA_QUERY
import de.vanita5.twittnuker.fragment.StatusesSearchFragment
import de.vanita5.twittnuker.model.Tab
import de.vanita5.twittnuker.model.tab.DrawableHolder
import de.vanita5.twittnuker.model.tab.StringHolder
import de.vanita5.twittnuker.model.tab.TabConfiguration
import de.vanita5.twittnuker.model.tab.argument.TextQueryArguments
import de.vanita5.twittnuker.model.tab.conf.StringExtraConfiguration


class SearchTabConfiguration : TabConfiguration() {

    override val name = StringHolder.resource(R.string.action_search)

    override val icon = DrawableHolder.Builtin.SEARCH

    override val accountFlags = TabAccountFlags.FLAG_HAS_ACCOUNT or TabAccountFlags.FLAG_ACCOUNT_REQUIRED

    override val fragmentClass = StatusesSearchFragment::class.java

    override fun getExtraConfigurations(context: Context) = arrayOf(
            StringExtraConfiguration(EXTRA_QUERY, R.string.search_statuses, null).maxLines(1).headerTitle(R.string.query)
    )

    override fun applyExtraConfigurationTo(tab: Tab, extraConf: TabConfiguration.ExtraConfiguration): Boolean {
        val arguments = tab.arguments as TextQueryArguments
        when (extraConf.key) {
            EXTRA_QUERY -> {
                val query = (extraConf as StringExtraConfiguration).value ?: return false
                arguments.query = query
            }
        }
        return true
    }

    override fun readExtraConfigurationFrom(tab: Tab, extraConf: TabConfiguration.ExtraConfiguration): Boolean {
        val arguments = tab.arguments as? TextQueryArguments ?: return false
        when (extraConf.key) {
            EXTRA_QUERY -> {
                (extraConf as StringExtraConfiguration).value = arguments.query
            }
        }
        return true
    }
}