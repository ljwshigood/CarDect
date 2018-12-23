package com.zzteck.cardect.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class WJViewPaper extends ViewPager {
	
	 private boolean scrollble = true;  
	  
     public WJViewPaper(Context context) {  
        super(context);  
     }  
  
     public WJViewPaper(Context context, AttributeSet attrs) {  
        super(context, attrs);  
     }  
  
     @Override  
     public boolean onTouchEvent(MotionEvent ev) {  
        if (!scrollble) {  
            return true;  
        }  
        return super.onTouchEvent(ev);  
     }  
  
     @Override
     public boolean onInterceptTouchEvent(MotionEvent arg0) {
         if (!scrollble){
             return false;
         }else{
             return super.onInterceptTouchEvent(arg0);
         }
     }
  
     public boolean isScrollble() {  
         return scrollble;  
     }  
  
     public void setScrollble(boolean scrollble) {  
        this.scrollble = scrollble;  
     }
	    
}
