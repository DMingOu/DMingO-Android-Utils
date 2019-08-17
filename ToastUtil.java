package com.example.taxidata.util;

import android.view.Gravity;
import android.widget.Toast;

import com.example.taxidata.application.TaxiApp;

public class ToastUtil {


    //触发多少次Toast调用，都只会持续一次Toast显示的时长
    private static Toast toast;

    /**
     * 短时间显示Toast【居下】
     *
     * @param msg 显示的内容-字符串
     */
    public static void showShortToastBottom(String msg) {
        if (TaxiApp.getContext() != null) {
            if (toast == null) {
                toast = Toast.makeText(TaxiApp.getContext(), msg, Toast.LENGTH_SHORT);
            } else {
                toast.setText(msg);
            }
            toast.show();
        }
    }

    /**
     * 短时间显示Toast【居中】
     *
     * @param msg 显示的内容-字符串
     */
    public static void showShortToastCenter(String msg) {
        if (TaxiApp.getContext() != null) {
            if (toast == null) {
                toast = Toast.makeText(TaxiApp.getContext(), msg, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
            } else {
                toast.setText(msg);
            }
            toast.show();
        }
    }

    /**
     * 短时间显示Toast【居上】
     *
     * @param msg 显示的内容-字符串
     */
    public static void showShortToastTop(String msg) {
        if (TaxiApp.getContext() != null) {
            if (toast == null) {
                toast = Toast.makeText(TaxiApp.getContext(), msg, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 0, 0);
            } else {
                toast.setText(msg);
            }
            toast.show();
        }
    }

    /**
     * 长时间显示Toast【居下】
     *
     * @param msg 显示的内容-字符串
     */
    public static void showLongToastBottom(String msg) {
        if (TaxiApp.getContext() != null) {
            if (toast == null) {
                toast = Toast.makeText(TaxiApp.getContext(), msg, Toast.LENGTH_LONG);
            } else {
                toast.setText(msg);
            }
            toast.show();
        }
    }
}


