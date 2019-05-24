package com.kyh.app.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kyh.app.R;

public class FloatView extends View {
    private ImageView mMove, mBack, mMain;
    private View root;
    private  LinearLayout.LayoutParams params;

    //开始触控的坐标，移动时的坐标（相对于屏幕左上角的坐标）
    private int mTouchStartX, mTouchStartY, mTouchCurrentX, mTouchCurrentY;
    //开始时的坐标和结束时的坐标（相对于自身控件的坐标）
    private int mStartX, mStartY, mStopX, mStopY;
    //判断悬浮窗口是否移动，这里做个标记，防止移动后松手触发了点击事件
    private boolean isMove;

    public FloatView(Context context) {
        super(context);
        init(context);
    }


    public FloatView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FloatView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     *
     */
    private void init(Context context) {
        root = LayoutInflater.from(context).inflate(R.layout.laytou_float_view, null);

        mMove = root.findViewById(R.id.id_float);
        mBack = root.findViewById(R.id.id_back);
        mMain = root.findViewById(R.id.id_main);

        params = (LinearLayout.LayoutParams) root.getLayoutParams();

        root.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        isMove = false;
                        mTouchStartX = (int) event.getRawX();
                        mTouchStartY = (int) event.getRawY();
                        mStartX = (int) event.getX();
                        mStartY = (int) event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mTouchCurrentX = (int) event.getRawX();
                        mTouchCurrentY = (int) event.getRawY();
//                        params.x -= mTouchCurrentX - mTouchStartX;
//                        params.y += mTouchCurrentY - mTouchStartY;

                        mTouchStartX = mTouchCurrentX;
                        mTouchStartY = mTouchCurrentY;
                        break;
                    case MotionEvent.ACTION_UP:
                        mStopX = (int) event.getX();
                        mStopY = (int) event.getY();
                        if (Math.abs(mStartX - mStopX) >= 1 || Math.abs(mStartY - mStopY) >= 1) {
                            isMove = true;
                        }
                        break;
                }
                //如果是移动事件不触发OnClick事件，防止移动的时候一放手形成点击事件
                return isMove;
            }
        });
    }
}
