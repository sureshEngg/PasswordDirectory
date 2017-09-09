/*******************************************************************************
 * [y] hybris Platform
 * <p/>
 * Copyright (c) 2000-2013 hybris AG
 * All rights reserved.
 * <p/>
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 ******************************************************************************/
package suresh.syp.saveurpasswa;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashMap;
import java.util.Map;


/**
 * @author philip
 *
 */
public final class SDKSettings {
    private static final Map<String, String> settings = new HashMap<>();

    /**
     * Helper for getting saved strings
     *
     * @return string
     */
    public static String getSharedPreferenceString(Context context, String key) {
        PreferenceManager.getDefaultSharedPreferences(context);
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, "");
    }


    /**
     * Helper for saving strings
     *
     * @param key
     *           The key
     * @param value
     *           The value
     */
    public static void setSharedPreferenceString(Context context, String key, String value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void setSettingValue(String settingName, String value) {
        settings.put(settingName, value);
    }
}
