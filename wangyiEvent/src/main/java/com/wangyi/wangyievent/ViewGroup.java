package com.wangyi.wangyievent;

import java.util.ArrayList;
import java.util.List;

public class ViewGroup extends  View {

    public ViewGroup(int left, int top, int right, int bottom) {
        super(left, top, right, bottom);
    }

    List<View> childList = new ArrayList<>();

    /**
     * 用数组进行存储，而不是list，提高效率
     */
    private View[] mChildren=new View[0];

    private TouchTarget mFirstTouchTarget;

    /**
     *添加子元素
     * @param view
     */
    public void addView(View view) {
        if (view == null) {
            return;
        }
        childList.add(view);
        mChildren = (View[]) childList.toArray(new View[childList.size()]);
    }



    /**
     * 事件分发的入口
     * @param event
     * @return
     */
    public boolean dispatchTouchEvent(MotionEvent event) {
//
        System.out.println(name+" dispatchTouchEvent ");
        boolean handled = false;

        //获取是否进行事件拦截判断结果
        boolean intercepted= onInterceptTouchEvent(event);

//TouchTarget  模式 内存缓存   move up
        TouchTarget newTouchTarget = null;
        int actionMasked = event.getActionMasked();

        //假如不拦截并且事件没取消则处理事件
        if (actionMasked != MotionEvent.ACTION_CANCEL && !intercepted) {
//            Down事件
            //处理Down事件
            if (actionMasked == MotionEvent.ACTION_DOWN) {
                final View[] children = mChildren;
//                耗时  概率大一些
                for (int i = children.length-1; i >= 0; i--) {
                    View child = mChildren[i];
//                    View能够接收到事件
                    if (!child.isContainer(event.getX(), event.getY())) {
                        continue;
                    }
    //                    能够接受事件  child   分发给他
                    if (dispatchTransformedTouchEvent(event, child)) {
//                        View[]  采取了 Message 的方式进行  链表结构
                        handled = true;
                        newTouchTarget = addTouchTarget(child);
                        break;
                    }

                }
            }
// 当前的ViewGroup  dispatchTransformedTouchEvent
        if(mFirstTouchTarget==null) {
            handled = dispatchTransformedTouchEvent(event, null);
        }
        }
        return handled;
    }

    private TouchTarget addTouchTarget(View child) {
        final TouchTarget target = TouchTarget.obtain(child);
        target.next = mFirstTouchTarget;
        mFirstTouchTarget = target;
        return target;
    }


//    手写RecyClerView   回收池策略·
    private static final class TouchTarget {

    /**
     * 当前缓存的View
     */
    public View child;

    /**
     * 下一个target
     */
    public TouchTarget next;

    /**
     * 锁，防止并发
     */
    private static final Object sRecycleLock = new Object[0];

    /**
     * 回收池（一个对象）,所以设置成静态成员变量
     */
    private static TouchTarget sRecycleBin;

    /**
     * 回收池数量
     */
    private static int sRecycledCount;
//        up事件


    /**
     * 获取
     * 回收池策略，保存取一个，少一个
     * @param child
     * @return
     */
    public static TouchTarget obtain(View child) {
        TouchTarget target;
        synchronized (sRecycleLock) {
            if (sRecycleBin == null) {
                target = new TouchTarget();
            }else {//当up事件发生的时候，所有事件被回收到回收池里面，并把TouchTarget释放掉
                target = sRecycleBin;//把回收池赋值给需要取出来的target
            }
            sRecycleBin = target.next;
            sRecycledCount--;
            target.next = null;
        }
        target.child = child;
        return target;
    }


    /**
     * 回收
     */
     public void recycle() {
            if (child == null) {
                throw new IllegalStateException("已经被回收过了");
            }
            synchronized (sRecycleLock) {

                if (sRecycledCount < 32) {
                    next = sRecycleBin;
                    sRecycleBin = this;
                    sRecycledCount += 1;
                }
            }
         }
     }


//分发处理 子控件  View

    /**
     * 分发事件
     * @param event
     * @param child
     * @return 是否消费
     */
    private boolean dispatchTransformedTouchEvent(MotionEvent event, View child) {
       boolean handled=false;
//        当前View消费了
        if (child != null) {
            handled =child.dispatchTouchEvent(event);
        }else {
            handled = super.dispatchTouchEvent(event);
        }

        return handled;
    }


    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    private String name;

    @Override
    public String toString() {
        return ""+name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
