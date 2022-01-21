package com.muthopay.muthobrowser.browser

import com.muthopay.muthobrowser.preference.IntEnum

/**
 * The available Block JavaScript choices.
 */
enum class JavaScriptChoice(override val value: Int) : IntEnum {
    NONE(0),
    WHITELIST(1),
    BLACKLIST(2)
}
