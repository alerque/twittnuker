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
import de.vanita5.twittnuker.constant.IntentConstants.EXTRA_PLACE
import de.vanita5.twittnuker.fragment.TrendsSuggestionsFragment
import de.vanita5.twittnuker.model.Tab
import de.vanita5.twittnuker.model.tab.DrawableHolder
import de.vanita5.twittnuker.model.tab.StringHolder
import de.vanita5.twittnuker.model.tab.TabConfiguration
import de.vanita5.twittnuker.model.tab.conf.TrendsLocationExtraConfiguration
import de.vanita5.twittnuker.model.tab.extra.TrendsTabExtras

class TrendsTabConfiguration : TabConfiguration() {

    override val name = StringHolder.resource(R.string.trends)

    override val icon = DrawableHolder.Builtin.HASHTAG

    override val accountFlags = TabAccountFlags.FLAG_HAS_ACCOUNT or
            TabAccountFlags.FLAG_ACCOUNT_REQUIRED or TabAccountFlags.FLAG_ACCOUNT_MUTABLE

    override val fragmentClass = TrendsSuggestionsFragment::class.java

    override fun getExtraConfigurations(context: Context) = arrayOf(
            TrendsLocationExtraConfiguration(EXTRA_PLACE, R.string.trends_location).mutable(true)
    )

    override fun applyExtraConfigurationTo(tab: Tab, extraConf: TabConfiguration.ExtraConfiguration): Boolean {
        val extras = tab.extras as TrendsTabExtras
        when (extraConf.key) {
            EXTRA_PLACE -> {
                val conf = extraConf as TrendsLocationExtraConfiguration
                val place = conf.value
                if (place != null) {
                    extras.woeId = place.woeId
                    extras.placeName = place.name
                } else {
                    extras.woeId = 0
                    extras.placeName = null
                }
            }
        }
        return true
    }

    override fun readExtraConfigurationFrom(tab: Tab, extraConf: TabConfiguration.ExtraConfiguration): Boolean {
        val extras = tab.extras as? TrendsTabExtras ?: return false
        when (extraConf.key) {
            EXTRA_PLACE -> {
                val woeId = extras.woeId
                val name = extras.placeName
                if (name != null) {
                    val place = TrendsLocationExtraConfiguration.Place(woeId, name)
                    (extraConf as TrendsLocationExtraConfiguration).value = place
                } else {
                    (extraConf as TrendsLocationExtraConfiguration).value = null
                }
            }
        }
        return true
    }
}