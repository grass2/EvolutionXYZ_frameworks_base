/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.server.wm.flicker.helpers

import android.app.Instrumentation
import android.tools.common.traces.component.ComponentNameMatcher
import android.tools.device.apphelpers.StandardAppHelper
import android.tools.device.helpers.FIND_TIMEOUT
import android.tools.device.traces.parsers.WindowManagerStateHelper
import android.tools.device.traces.parsers.toFlickerComponent
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import com.android.server.wm.flicker.testapp.ActivityOptions

open class ImeAppHelper
@JvmOverloads
constructor(
    instr: Instrumentation,
    launcherName: String = ActivityOptions.Ime.Default.LABEL,
    component: ComponentNameMatcher = ActivityOptions.Ime.Default.COMPONENT.toFlickerComponent()
) : StandardAppHelper(instr, launcherName, component) {
    /**
     * Opens the IME and wait for it to be displayed
     *
     * @param wmHelper Helper used to wait for WindowManager states
     */
    open fun openIME(wmHelper: WindowManagerStateHelper) {
        val editText =
            uiDevice.wait(Until.findObject(By.res(packageName, "plain_text_input")), FIND_TIMEOUT)

        requireNotNull(editText) {
            "Text field not found, this usually happens when the device " +
                "was left in an unknown state (e.g. in split screen)"
        }
        editText.click()
        waitIMEShown(wmHelper)
    }

    fun waitIMEShown(wmHelper: WindowManagerStateHelper) {
        wmHelper.StateSyncBuilder().withImeShown().waitForAndVerify()
    }

    /**
     * Opens the IME and wait for it to be gone
     *
     * @param wmHelper Helper used to wait for WindowManager states
     */
    open fun closeIME(wmHelper: WindowManagerStateHelper) {
        uiDevice.pressBack()
        wmHelper.StateSyncBuilder().withImeGone().waitForAndVerify()
    }
}
