package com.example.ytoolbar.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class ImmersionModeUtil {

   /**
    * 获取状态栏高度
    */
   public static int getStatusBarHeight(Context context) {
      int result = 0;
      try {
         int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen",
                 "android");
         if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
         }
      } catch (Resources.NotFoundException e) {
         e.printStackTrace();
      }
      return result;
   }


   /**
    * 进入沉浸模式
    *
    * @param view mView
    */
   @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
   public static void hideSystemUI(View view) {
      view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
              | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
              | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
              | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
              | View.SYSTEM_UI_FLAG_FULLSCREEN
              | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY//会自动隐藏
      );
   }

   /**
    * 退出沉浸模式
    *
    * @param view mView
    */
   @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
   public static void showSystemUI(View view) {
      view.setSystemUiVisibility(
              View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                      | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                      | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
   }

   /**
    * 设置状态栏反色
    */
   public static void setDarkStatusIcon(Activity activity, boolean isDark) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
         View decorView = activity.getWindow().getDecorView();
         if (decorView != null) {
            int vis = decorView.getSystemUiVisibility();
            if (isDark) {
               vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
               vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            decorView.setSystemUiVisibility(vis);
         }
      }
   }

   /**
    * 设置透明状态栏与导航栏
    *
    * @param navi true不设置导航栏|false设置导航栏
    */
   public static void setStatusBar(Activity activity, boolean navi) {
      setStatusBar(activity, navi, true);
   }


   public static void setStatusBar(Activity activity, boolean navi, boolean darkStatusIcon) {
      //api>21,全透明状态栏和导航栏;api>19,半透明状态栏和导航栏
      Window window = activity.getWindow();
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
         window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
         window.setStatusBarColor(Color.TRANSPARENT);
         if (navi) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    //状态栏不会被隐藏但activity布局会扩展到状态栏所在位置
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION//导航栏不会被隐藏但activity布局会扩展到导航栏所在位置
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.setNavigationBarColor(Color.TRANSPARENT);
         } else {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
         }
      } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
         if (navi) {
            //半透明导航栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
         }
         //半透明状态栏
         window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      }
      //这是状态栏文字反色
      setDarkStatusIcon(activity, darkStatusIcon);
   }

   public static void setFullyTransparent(Activity activity) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
         Window window = activity.getWindow();
         View decorView = window.getDecorView();
         int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
         decorView.setSystemUiVisibility(option);
         window.setStatusBarColor(Color.TRANSPARENT); //这是状态栏文字反色
         setDarkStatusIcon(activity, true);
      }
   }

}
