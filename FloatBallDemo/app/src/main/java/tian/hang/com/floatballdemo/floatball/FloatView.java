package tian.hang.com.floatballdemo.floatball;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

import tian.hang.com.floatballdemo.LogUtil;
import tian.hang.com.floatballdemo.R;


/**
 * Created by thm on 2016/8/19.
 */
public class FloatView extends FrameLayout implements OnTouchListener {

    private final int ACTION_CHANGE_ALHPA = 100;//停留3s后变暗
    private final int ACTION_HIDE_FLOATWINDOW = 101;//变暗3s后靠边隐藏
    private final int ACTION_ANIM_LEFT = 102;
    private final int ACTION_ANIM_RIGHT = 103;
    private boolean mCurGriRight = false;
    //悬浮窗停留3s后变暗，再3s后靠边隐藏，中途触碰则中断ACTION_HIDE_FLOATWINDOW
    private boolean mTouchAgain = false;

    private WindowManager.LayoutParams mWmParams;
    private WindowManager mWindowManager;
    private boolean isHide = false;
    private long mLastClickTime = 0;


    private Context mContext;

    private ImageView mIvFloatLogo;
    private FloatItems mLlFloatMenu;

    private boolean mIsRight;//logo是否在右边
    private boolean mCanHide;//是否允许隐藏

    private int mAniType;
    private final int typeLeft = 10011;
    private final int typeRight = 10012;

    private float mTouchStartX;
    private float mTouchStartY;
    private int mScreenWidth;
    private int mScreenHeight;
    private boolean mDraging;

    private Timer mTimer;
    private TimerTask mTimerTask;

    private boolean isRunning = false;

    private static int initX;
    private static int initY;

    final Handler mTimerHandler = new Handler() {
        public void handleMessage(Message msg) {
            LogUtil.d("anim msg = "+msg.what +"x = "+mWmParams.x);
            if ( msg.what == ACTION_CHANGE_ALHPA) {
                mTouchAgain = false;
                if (mCanHide) {
                    LogUtil.d("HMTian_set mCanHide->false");
                    mCanHide = false;
                    setMenuVisiable(false);
                    mWmParams.alpha = 0.7f;
                    mWindowManager.updateViewLayout(FloatView.this, mWmParams);
                    refreshFloatMenu(mIsRight);
                    if(mTimer != null){
                        mTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                mTimerHandler.sendEmptyMessage(ACTION_HIDE_FLOATWINDOW);
                            }
                        }, 3000);
                    }
                }
            } else if ( msg.what == ACTION_HIDE_FLOATWINDOW) {
                LogUtil.d("starAni mTouchAgain = "+mTouchAgain);
                if(mTouchAgain)
                    return;
                isRunning = true;
                int padding = 0 - (int)mIvFloatLogo.getWidth()/2;
                LogUtil.d("mIsRight = "+mIsRight);
                if (mIsRight) {
                    //todo 右边动画
                    mIvFloatLogo.setPadding(0, 0, padding, 0);
                } else {
                    //todo 左边动画
                    mIvFloatLogo.setPadding(padding, 0, 0, 0);
                }
                isHide = true;
                isRunning = false;
            }else if ( msg.what == ACTION_ANIM_LEFT) {
                isRunning = true;
                mWmParams.x -= 18;
                mWindowManager.updateViewLayout(FloatView.this, mWmParams);
                if(mWmParams.x > 0){
                    mTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            mTimerHandler.sendEmptyMessage(ACTION_ANIM_LEFT);
                        }
                    }, 5);
                }else {
                    mWmParams.x = 0;
                    isRunning = false;
                }
            }else if ( msg.what == ACTION_ANIM_RIGHT){
                isRunning = true;
                mWmParams.x += 18;
                mWindowManager.updateViewLayout(FloatView.this, mWmParams);
                if(mWmParams.x < mScreenWidth){
                    mTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            mTimerHandler.sendEmptyMessage(ACTION_ANIM_RIGHT);
                        }
                    }, 5);
                } else {
                    mWmParams.x = mScreenWidth;
                    isRunning  = false;
                }
            }

            super.handleMessage(msg);
        }
    };

    public FloatView(Context context) {
        super(context);
        init(context);
    }

    public FloatView(Context context, AttributeSet attrs) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;

        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        // 更新浮动窗口位置参数 靠边
        DisplayMetrics dm = new DisplayMetrics();
        // 获取屏幕信息
        mWindowManager.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        this.mWmParams = new WindowManager.LayoutParams();
        // 设置window type
        mWmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;
        // 设置图片格式，效果为背景透明
        mWmParams.format = PixelFormat.RGBA_8888;
        // 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        mWmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mWmParams.gravity = Gravity.LEFT | Gravity.TOP;

        mScreenHeight = mWindowManager.getDefaultDisplay().getHeight();

        mIsRight = initX > mScreenWidth/2;
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        mWmParams.x = mIsRight ? mScreenWidth : 0;
        mWmParams.y = initY == 0? mScreenHeight / 2 : initY;


        // 设置悬浮窗口长宽数据
        mWmParams.width = LayoutParams.WRAP_CONTENT;
        mWmParams.height = LayoutParams.WRAP_CONTENT;
        addView(createView(mContext));
        mWindowManager.addView(this, mWmParams);

        mTimer = new Timer();
//        hide();
        timerForHide();
    }


    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // 更新浮动窗口位置参数 靠边
        DisplayMetrics dm = new DisplayMetrics();
        // 获取屏幕信息
        mWindowManager.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        int oldX = mWmParams.x;
        int oldY = mWmParams.y;
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE://横屏
                if ( mIsRight ) {
                    mWmParams.x = mScreenWidth;
                    mWmParams.y = oldY;
                } else {
                    mWmParams.x = oldX;
                    mWmParams.y = oldY;
                }
                break;
            case Configuration.ORIENTATION_PORTRAIT://竖屏
                if ( mIsRight ) {
                    mWmParams.x = mScreenWidth;
                    mWmParams.y = oldY;
                } else {
                    mWmParams.x = oldX;
                    mWmParams.y = oldY;
                }
                break;
        }
        mWindowManager.updateViewLayout(this, mWmParams);
    }

    /**
     * 创建Float view
     * @param context
     * @return
     */
    private View createView(final Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        // 从布局文件获取浮动窗口视图
        View rootFloatView = inflater.inflate(R.layout.widget_float_view, null);
        mIvFloatLogo = (ImageView) rootFloatView.findViewById(R.id.pj_float_view_icon_imageView);
        mLlFloatMenu = (FloatItems) rootFloatView.findViewById(R.id.ll_menu);
        rootFloatView.setOnTouchListener(this);
        rootFloatView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                long curTime = System.currentTimeMillis();
                if(curTime - mLastClickTime < 200) {
                    return;
                }

                mLastClickTime = curTime;

                if ( !mDraging ) {
                    if (isMenuVisiable()) {
                        setMenuVisiable(false);
                    } else {
                        setMenuVisiable(true);
                    }
                }
            }
        });
        rootFloatView.measure(MeasureSpec.makeMeasureSpec(0,
                MeasureSpec.UNSPECIFIED), MeasureSpec
                .makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));


        return rootFloatView;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        LogUtil.d("starAni ontouch isRunning = "+isRunning);
        if(isRunning)
            return false;
        if(isMenuVisiable()){
            return false;
        }
        removeTimerTask();
        // 获取相对屏幕的坐标，即以屏幕左上角为原点
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                LogUtil.d("starAni ACTION_DOWN");
                mTouchAgain = true;
                mTouchStartX = event.getX();
                mTouchStartY = event.getY();
                mIvFloatLogo.setPadding(0,0,0,0);
                mIvFloatLogo.setImageResource(R.drawable.float_logo_normal);
                mWmParams.alpha = 1f;
                if(isHide){
                    mWmParams.x += mWmParams.x > mScreenWidth/2 ? -240:140;
                }
                mWindowManager.updateViewLayout(this, mWmParams);
                isHide = false;
                mDraging = false;
                break;
            case MotionEvent.ACTION_MOVE:
                LogUtil.d("starAni ACTION_MOVE");
                float mMoveStartX = event.getX();
                float mMoveStartY = event.getY();


                // 如果移动量大于3才移动
                float delX = Math.abs(mTouchStartX - mMoveStartX);
                float delY = Math.abs(mTouchStartY - mMoveStartY);
                if (delX > 3
                        && delY > 3 ) {
                    //太靠边的时候就不进行放大处理了，以免边界超出屏幕
                    if( mMoveStartX > 15 && mMoveStartX < mScreenWidth-15){
                        mIvFloatLogo.setImageResource(R.drawable.float_logo_bigger);
                    }

                    mDraging = true;
                    // 更新浮动窗口位置参数
//                    LogUtil.d("before showAni x = "+mFlFloatLogo.getX()
//                            +" y = "+mFlFloatLogo.getY());
                    mWmParams.x = (int) (x - mTouchStartX);
                    mWmParams.y = (int) (y - mTouchStartY);
                    mWindowManager.updateViewLayout(this, mWmParams);

                    return false;
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIvFloatLogo.setImageResource(R.drawable.float_logo_normal);
                int left = mCurGriRight ? mWmParams.x+mLlFloatMenu.getLayoutParams().width:mWmParams.x;

                if (left >= mScreenWidth / 2-mIvFloatLogo.getWidth()/2) {
                    mAniType = typeRight;
                    mIsRight = true;
                } else if (left < mScreenWidth / 2 -mIvFloatLogo.getWidth()/2) {
                    mAniType = typeLeft;
                    mIsRight = false;
                }

                refreshFloatMenu(mIsRight);
                //3s后透明度设置成0.7， 再3s后缩到边上
                timerForHide();
                startAni(mAniType);
                // 初始化
                mTouchStartX = mTouchStartY = 0;
                break;
            default:
                mIvFloatLogo.setImageResource(R.drawable.float_logo_normal);
        }
        return false;
    }

    private void removeTimerTask() {
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    private void removeFloatView() {
        try {
            mWindowManager.removeView(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 显示悬浮窗
     */
    public void show() {
        if (getVisibility() != View.VISIBLE) {
            setVisibility(View.VISIBLE);
                mIvFloatLogo.setImageResource(R.drawable.float_logo_normal);
                mWmParams.alpha = 1f;
                mWindowManager.updateViewLayout(this, mWmParams);
                timerForHide();
            LogUtil.d("THMTYPEAPP getVisibility() != View.VISIBLE");
        } else {
            LogUtil.d("THMTYPEAPP getVisibility() == View.VISIBLE");
        }
    }

    /**
     * 刷新float view menu
     * @param right
     */
    private void refreshFloatMenu(boolean right) {
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, mContext.getResources().getDisplayMetrics());
        LayoutParams paramsMenuAccount = (LayoutParams) mLlFloatMenu.getLayoutParams();
        LayoutParams paramsFloatImage = (LayoutParams) mIvFloatLogo.getLayoutParams();
        if (right) {
            paramsMenuAccount.rightMargin = getResources().getDrawable(R.drawable.float_logo_normal).getIntrinsicWidth()-6;//+6
            paramsMenuAccount.leftMargin = padding;
            paramsMenuAccount.gravity = Gravity.RIGHT;
            paramsFloatImage.setMargins(0, 0, 0, 0);
            paramsFloatImage.gravity = Gravity.RIGHT;

            mCurGriRight = true;
        } else {
            paramsMenuAccount.rightMargin = padding;
            paramsMenuAccount.leftMargin = getResources().getDrawable(R.drawable.float_logo_normal).getIntrinsicWidth()+6;
            paramsMenuAccount.gravity = Gravity.LEFT;
            paramsFloatImage.setMargins(0, 0, 0, 0);
            paramsFloatImage.gravity = Gravity.LEFT;

            mCurGriRight = false;
        }
        mLlFloatMenu.setLayoutParams(paramsMenuAccount);
        mIvFloatLogo.setLayoutParams(paramsFloatImage);
    }

    //松开手指后，悬浮球开始滑向两边(具体边取决于当前位置)
    private void startAni(int type){
        if(isRunning || mTimer==null)
            return;
        if(type == typeLeft){
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mTimerHandler.sendEmptyMessage(ACTION_ANIM_LEFT);
                }
            }, 5);
        } else if(type == typeRight){
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mTimerHandler.sendEmptyMessage(ACTION_ANIM_RIGHT);
                }
            }, 5);
        }

    }


    public FloatItems getMenu(){
        return mLlFloatMenu;
    }
    /**
     * 悬浮球停留3s后变暗
     */
    private void timerForHide() {
        LogUtil.d("HMTian_set mCanHide->true");
        mCanHide = true;

        if (mTimerTask != null) {
            LogUtil.d("HMTian mTimerTask!=null ");
            try {
                mTimerTask.cancel();
                mTimerTask = null;
            } catch (Exception e){}

        }

        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                Message message = mTimerHandler.obtainMessage();
                message.what = ACTION_CHANGE_ALHPA;
                mTimerHandler.sendMessage(message);
            }
        };

        if (mCanHide && mTimer != null) {
            mTimer.schedule(mTimerTask, 6000, 3000);
        }
    }

    /**
     * 去除悬浮球
     */
    public void destroy() {
        initX = mWmParams.x;
        initY = mWmParams.y;
        removeFloatView();
        stopHandler();

    }

    public void stopHandler(){
        removeTimerTask();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        try {
            mTimerHandler.removeMessages(100);
            mTimerHandler.removeMessages(101);
            mTimerHandler.removeMessages(102);
            mTimerHandler.removeMessages(103);
        } catch (Exception e){}
    }

    /**
     * 隐藏悬浮窗
     */
    public void hide() {
        setVisibility(View.GONE);
        Message message = mTimerHandler.obtainMessage();
        message.what = ACTION_CHANGE_ALHPA;
        mTimerHandler.sendMessage(message);
        removeTimerTask();
    }

    /*
     *隐藏菜单(专区、礼包等)
     */
    public void hideMenu(){
        setMenuVisiable(false);
    }

    private void setMenuVisiable(boolean visiable){
        if(mLlFloatMenu == null)
            return;
        if (visiable){
            mLlFloatMenu.setVisibility(View.VISIBLE);
        } else {
            mLlFloatMenu.setVisibility(View.GONE);
        }
    }

    private boolean isMenuVisiable(){
        if(mLlFloatMenu == null)
            return false;
        return mLlFloatMenu.getVisibility() == View.VISIBLE;
    }
}