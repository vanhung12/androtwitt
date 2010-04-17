/*
 * Copyright (C) 2008 The Android Open Source Project
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

package com.androtwitt;

import java.util.ArrayList;
import java.util.Iterator;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import java.util.List;


/**
 * A widget provider.  We have a string that we pull from a preference in order to show
 * the configuration settings and the current time when the widget was updated.  We also
 * register a BroadcastReceiver for time-changed and timezone-changed broadcasts, and
 * update then too.
 *
 * <p>See also the following files:
 * <ul>
 *   <li>ExampleAppWidgetConfigure.java</li>
 *   <li>ExampleBroadcastReceiver.java</li>
 *   <li>res/layout/appwidget_configure.xml</li>
 *   <li>res/layout/appwidget_provider.xml</li>
 *   <li>res/xml/appwidget_provider.xml</li>
 * </ul>
 */
public class ExampleAppWidgetProvider extends AppWidgetProvider {
    // log tag
    private static final String TAG = "ExampleAppWidgetProvider";

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate");
        // For each widget that needs an update, get the text that we should display:
        //   - Create a RemoteViews object for it
        //   - Set the text in the RemoteViews object
        //   - Tell the AppWidgetManager to show that views object for the widget.
        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
    
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.d(TAG, "onDeleted");
        // When the user deletes the widget, delete the preference associated with it.
        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) {
            ExampleAppWidgetConfigure.deleteTitlePref(context, appWidgetIds[i]);
        }
    }

    public void onEnabled(Context context) {
        Log.d(TAG, "onEnabled");
        // When the first widget is created, register for the TIMEZONE_CHANGED and TIME_CHANGED
        // broadcasts.  We don't want to be listening for these if nobody has our widget active.
        // This setting is sticky across reboots, but that doesn't matter, because this will
        // be called after boot if there is a widget instance for this provider.
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(
                new ComponentName("com.androtwitt", ".ExampleBroadcastReceiver"),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public void onDisabled(Context context) {
        // When the first widget is created, stop listening for the TIMEZONE_CHANGED and
        // TIME_CHANGED broadcasts.
        Log.d(TAG, "onDisabled");
        Class clazz = ExampleBroadcastReceiver.class;
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(
                new ComponentName("com.androtwitt", ".ExampleBroadcastReceiver"),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
            int appWidgetId) {
    	
        Log.d(TAG, "updateAppWidget appWidgetId=" + appWidgetId);
        ArrayList lines = new ArrayList();
        
        Twitter unauthenticatedTwitter = new TwitterFactory().getInstance();
        System.out.println("Showing public timeline.");
        try {
            List<Status> statuses = unauthenticatedTwitter.getPublicTimeline();

            // Other methods require authentication
            String username = ExampleAppWidgetConfigure.loadTitlePref(context, appWidgetId, "username");
            String password = ExampleAppWidgetConfigure.loadTitlePref(context, appWidgetId, "password");
            
            Twitter twitter = new TwitterFactory().getInstance(username, password);
            statuses = twitter.getFriendsTimeline();
            for (Status status : statuses) {
            	lines.add(status.getUser().getScreenName() + ":" +
                        status.getText());
            }
        } catch (TwitterException te) {
            Log.d("AT_Error", "Failed to get timeline: " + te.getMessage());
        }
        
        // Construct the RemoteViews object.  It takes the package name (in our case, it's our
        // package, but it needs this because on the other side it's the widget host inflating
        // the layout from our package).
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_provider);
        ArrayList<Integer> widgetTexts = new ArrayList();
        widgetTexts.add(R.id.appwidget_text0);
        widgetTexts.add(R.id.appwidget_text1);
        widgetTexts.add(R.id.appwidget_text2);
        widgetTexts.add(R.id.appwidget_text3);
        widgetTexts.add(R.id.appwidget_text4);
        widgetTexts.add(R.id.appwidget_text5);
        widgetTexts.add(R.id.appwidget_text6);
        widgetTexts.add(R.id.appwidget_text7);
        widgetTexts.add(R.id.appwidget_text8);
        widgetTexts.add(R.id.appwidget_text9);
        
        Iterator i = widgetTexts.iterator();
        Iterator j = lines.iterator();
        while (i.hasNext()) {
        	Integer value = (Integer) i.next();
        	if (j.hasNext()) {
        		views.setTextViewText(value, (String) j.next());
        	}
        }
        
        

        // Tell the widget manager
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}


