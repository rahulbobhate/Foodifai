package net.stupidiot.foodifai;

import android.app.Application;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
/**
 * Created by parikshitt23 on 23-01-2016.
 */
public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        // Add your initialization code here
        Parse.initialize(this, "ZwirlqrKB584YC8CycxSFV4uosP26EueSjqRmU6f", "FeVdkAXYkiuQJotDc02UnhFooYNvuugxV7LKfJwv");
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);

    }
}
