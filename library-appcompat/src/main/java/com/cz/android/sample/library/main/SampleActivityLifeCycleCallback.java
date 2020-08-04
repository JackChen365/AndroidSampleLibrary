package com.cz.android.sample.library.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;


import com.cz.android.sample.function.FunctionManager;
import com.cz.android.sample.function.SampleFunction;
import com.cz.android.sample.library.appcompat.R;
import com.cz.android.sample.main.MainSampleComponentFactory;

import java.util.List;

/**
 * @author Created by cz
 * @date 2020-01-27 19:47
 * @email bingo110@126.com
 */
public class SampleActivityLifeCycleCallback extends SampleActivityLifeCycleCallbackAdapter {
    private static final String BIND_MAIN_SAMPLE_FRAGMENT_TAG="android_sample_main_fragment";
    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        //initialize all functions
        initializeFunction(activity);
        //check and inject main component
        injectMainComponent(activity);
    }

    private void initializeFunction(@NonNull Activity activity) {
        AndroidSampleImpl androidSample = AndroidSampleImpl.getInstance();
        FunctionManager functionManager = androidSample.getFunctionManager();
        List<SampleFunction> functionList = functionManager.getFunctionList();
        for(SampleFunction function:functionList){
            if(activity instanceof FragmentActivity){
                FragmentActivity fragmentActivity=(FragmentActivity)activity;
                function.initialize(fragmentActivity);
            }
        }
    }

    /**
     * Check this activity if is main activity we will inject our fragment
     * @param activity
     */
    private void injectMainComponent(@NonNull Activity activity) {
        String launchActivityName = getLaunchActivityName(activity);
        if(launchActivityName.equals(activity.getClass().getName())){
            //Here we are the main activity
            if(!(activity instanceof AppCompatActivity)){
                throw new IllegalArgumentException("The main activity should extend from AppCompatActivity! We can't support the Activity!");
            } else {
                AppCompatActivity appCompatActivity = (AppCompatActivity) activity;
                // Hide ActionBar
//                Resources.Theme theme = appCompatActivity.getTheme();
//                theme.applyStyle(R.style.SampleAppCompat,true);
                //Add the main fragment if needed;
                FragmentManager supportFragmentManager = appCompatActivity.getSupportFragmentManager();
                if (supportFragmentManager.findFragmentByTag(BIND_MAIN_SAMPLE_FRAGMENT_TAG) == null) {
                    AndroidSampleImpl androidSample = AndroidSampleImpl.getInstance();
                    MainSampleComponentFactory<Fragment> componentContainer = androidSample.getMainComponentContainer();
                    Fragment fragment = componentContainer.getFragmentComponent();
                    supportFragmentManager.beginTransaction().add(android.R.id.content,fragment, BIND_MAIN_SAMPLE_FRAGMENT_TAG).commit();
                }
            }
        }
    }

    /**
     * Get android.intent.action.MAIN activity class name
     * @param context
     * @return
     */
    private String getLaunchActivityName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        return intent.getComponent().getClassName();
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        checkMainComponent(activity);
    }

    /**
     * check main activity if user has its own layout we should remove it
     * @param activity
     */
    private void checkMainComponent(@NonNull Activity activity) {
        String launchActivityName = getLaunchActivityName(activity);
        if(launchActivityName.equals(activity.getClass().getName())) {
            //Here we are the main activity
            if (!(activity instanceof AppCompatActivity)) {
                throw new IllegalArgumentException("The main activity should extend from AppCompatActivity! We can't support the Activity!");
            } else {
                ViewGroup contentLayout=activity.findViewById(android.R.id.content);
                ViewGroup sampleFragmentContainer=contentLayout.findViewById(R.id.sampleFragmentContainer);
                if(null==sampleFragmentContainer){
                    contentLayout.removeAllViews();
                } else {
                    //remove all the children from content view except my boy
                    int keepSize = 0;
                    while(keepSize<contentLayout.getChildCount()){
                        View childView = contentLayout.getChildAt(keepSize);
                        if(childView==sampleFragmentContainer){
                            keepSize++;
                        } else {
                            contentLayout.removeView(childView);
                        }
                    }
                }
            }
        }
    }
}
