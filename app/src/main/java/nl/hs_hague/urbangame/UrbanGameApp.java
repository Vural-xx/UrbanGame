package nl.hs_hague.urbangame;

import android.app.Application; //Application App to has resources accesible in all the project.... Made by Iram
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class UrbanGameApp extends Application
{
    @Override
    public void onCreate()
    {
           super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }
}
