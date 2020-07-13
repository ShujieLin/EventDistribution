package com.wangyi.wangyievent;

import com.wangyi.wangyievent.litener.OnClickListener;
import com.wangyi.wangyievent.litener.OnTouchListener;

public class View {
    private int left;
    private int top;
    private int right;
    private int bottom;

    private OnTouchListener mOnTouchListener;
    private OnClickListener onClickListener;
    public void setOnTouchListener(OnTouchListener mOnTouchListener) {
        this.mOnTouchListener = mOnTouchListener;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public View() {
    }

    public View(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }
    public boolean isContainer(int x, int y) {

        if (x >= left && x < right && y >= top && y < bottom) {
            return true;
        }

        return false;
    }
//接受分发的代码

    /**
     * 接收事件分发
     * @param event
     * @return 是否接收事件分发
     */
    public boolean dispatchTouchEvent(MotionEvent event) {
        System.out.println(" view  dispatchTouchEvent ");
//        消费
        //是否消费事件
        boolean result = false;

        //假如设置了OnTouchListener接口，并且实现了onTouch方法
        if (mOnTouchListener != null&& mOnTouchListener.onTouch(this, event)) {
            result = true;
        }
//        mOnTouchListener   onClickListener.onClick
        //假如上面result = false，并且设置了onTouchEvent
        //设置一个点击事件，代表消费了事件
        if(!result && onTouchEvent(event)){
            result = true;
        }

        return result;
    }

    /**
     * 点击事件
     * @param event
     * @return
     */
    private boolean onTouchEvent(MotionEvent event) {
        if (onClickListener != null) {
            onClickListener.onClick(this);
            return true;
        }
        return false;
    }
    }
