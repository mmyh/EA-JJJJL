package com.mmyh.eajjjjl.mvvm;

import android.text.Spanned;
import android.widget.ImageView;
import android.widget.TextView;

public class EARender {

    public static void setText(TextView obj, String value) {
        if (value != null) {
            obj.setText(value);
        }
    }

    public static void setText(TextView obj, int value) {
        setText(obj, String.valueOf(value));
    }

    public static void setText(TextView obj, Integer value) {
        if (value != null) {
            setText(obj, String.valueOf(value));
        }
    }

    public static void setText(TextView obj, double value) {
        setText(obj, String.valueOf(value));
    }

    public static void setText(TextView obj, Double value) {
        if (value != null) {
            setText(obj, String.valueOf(value));
        }
    }

    public static void setText(TextView obj, float value) {
        setText(obj, String.valueOf(value));
    }

    public static void setText(TextView obj, Float value) {
        if (value != null) {
            setText(obj, String.valueOf(value));
        }
    }

    public static void setText(TextView obj, Spanned value) {
        if (value != null) {
            obj.setText(value);
        }
    }

    public static void setTextColor(TextView obj, int value) {
        obj.setTextColor(value);
    }

    public static void setImage(EAIImageRender render, ImageView obj, String value) {
        render.render(obj, value);
    }
}
