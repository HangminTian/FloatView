package tian.hang.com.floatballdemo;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tian.hang.com.floatballdemo.floatball.FloatView;

/**
 * Created by thm on 2016/10/8.
 */
public class FloatUtil {
    private static HashMap<String, Boolean> showRedPoint = new HashMap<>();
    private static FloatView floatView;
    private static FloatUtil instance;
    public static List<Activity> mContexts;

    private FloatUtil(){}

    public static FloatUtil getInstance(){
        if(instance == null){
            instance = new FloatUtil();
        }
        if(mContexts == null){
            mContexts = new ArrayList<>();
        }
        return instance;
    }

    public synchronized void addActivity(Activity context){
        if(mContexts.contains(context)){
            return;
        }
        mContexts.add(context);
    }

    public synchronized void removeActivity(Activity context){
        if(!mContexts.contains(context)){
            return;
        }
        mContexts.remove(context);
    }

    public synchronized void showFloatView(Context context) {
        if(floatView != null){
            floatView.destroy();
            floatView = null;
        }
        initFloatView(context);
    }

    public synchronized void hide(){
        if(floatView != null){
            floatView.setVisibility(View.VISIBLE);
        }
    }

    private void initFloatView(Context context){
        floatView = new FloatView(context);
        floatView.show();
    }

    public synchronized void stopHandler(Context context) {
        if(floatView == null){
            return;
        }
        floatView.stopHandler();
    }

    public synchronized void hideFloatView(Context context) {
        if(floatView == null){
            return;
        }
        floatView.setVisibility(View.GONE);
    }

    public synchronized Context getCurContext(){
        if(CollectionsUtil.isEmpty(mContexts)){
            return null;
        }
        return mContexts.get(mContexts.size()-1);
    }


}
