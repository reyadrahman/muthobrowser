package com.muthopay.muthobrowser.database.adblock

import android.content.SharedPreferences
import com.muthopay.muthobrowser.R
import com.muthopay.muthobrowser.di.AdBlockPrefs
import com.muthopay.muthobrowser.preference.delegates.nullableStringPreference
import javax.inject.Inject

/**
 * Information about the contents of the hosts repository.
 */
class HostsRepositoryInfo @Inject constructor(@AdBlockPrefs preferences: SharedPreferences) {

    /**
     * The identity of the contents of the hosts repository as a [String] or `null`.
     */
    var identity: String? by preferences.nullableStringPreference(R.string.pref_key_identity)

}

