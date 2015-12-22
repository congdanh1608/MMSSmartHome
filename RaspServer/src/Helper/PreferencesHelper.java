package Helper;

import java.util.prefs.Preferences;

public class PreferencesHelper {
	public static Class<Preferences> mPreferences = java.util.prefs.Preferences.class;
	public static Preferences prefs;

	public static void setPreferenceString(String string, String pref) {
		prefs = Preferences.userRoot().node(mPreferences.getClass().getName());
		// prefs = Preferences.userNodeForPackage(this.getClass());
		prefs.put(pref, string);
	}

	public static String getPreferencesString(String pref){
		prefs = Preferences.userRoot().node(mPreferences.getClass().getName());
		return prefs.get(pref, null);
	}
}
