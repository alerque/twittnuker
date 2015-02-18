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

package de.vanita5.twittnuker.util;

import android.location.Location;
import android.support.v4.util.LogWriter;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;

import de.vanita5.twittnuker.model.ParcelableStatus;

public class StatisticUtils {


	public static void writeStatusOpen(ParcelableStatus status, Location location, int signalStrength) throws IOException {
//		final LogWriter writer = new LogWriter("Twittnuker");
//		final CSVPrinter printer = CSVFormat.DEFAULT.print(writer);
//		printer.printRecord(status.account_id, status.id, status.user_id, status.user_screen_name,
//				status.text_html, fromStringLocation(location), signalStrength);
	}

	private static String fromStringLocation(Location location) {
		if (location == null) return "";
		return location.getLatitude() + "," + location.getLongitude();
	}

}