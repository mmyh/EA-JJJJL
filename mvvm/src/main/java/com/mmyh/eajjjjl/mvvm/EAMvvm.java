package com.mmyh.eajjjjl.mvvm;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;


import com.mmyh.eajjjjl.annotation.EAParent;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EAMvvm {

    private static BaseImageLoader imageLoader;

    public static void configImageLoader(BaseImageLoader el) {
        imageLoader = el;
    }

    public static BaseImageLoader getImageLoader() {
        if (imageLoader == null) {
            imageLoader = new BaseImageLoader();
        }
        return imageLoader;
    }

    public static void work(FragmentActivity bindObject) {
        internalWork(bindObject, null);
    }

    public static void work(Fragment bindObject, ViewGroup parent) {
        internalWork(bindObject, parent);
    }

    public static void work(View bindObject, ViewGroup parent, FragmentActivity uiType) {
        internalWork(bindObject, parent, uiType);
    }

    public static void work(View bindObject, ViewGroup parent, Fragment uiType) {
        internalWork(bindObject, parent, uiType);
    }

    private static void internalWork(Object bindObject, ViewGroup vg) {
        Class<?> tmp = bindObject.getClass();
        while (tmp != null
                && tmp != AppCompatActivity.class
                && tmp != FragmentActivity.class
                && tmp != Activity.class
                && tmp != Fragment.class) {
            try {
                EAParent parent = tmp.getAnnotation(EAParent.class);
                if (parent != null) {
                    String parentName = tmp.getCanonicalName().replace(".", "_");
                    Method initMethod = tmp.getMethod("init_" + parentName, ViewGroup.class);
                    initMethod.invoke(bindObject, vg);
                    Method renderWidget = tmp.getMethod("renderWidget_" + parentName);
                    renderWidget.invoke(bindObject);
                }
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            tmp = tmp.getSuperclass();
        }
    }

    private static void internalWork(Object bindObject, ViewGroup vg, Object uiType) {
        Class<?> tmp = bindObject.getClass();
        while (tmp != null
                && tmp != ViewGroup.class
                && tmp != View.class) {
            try {
                EAParent parent = tmp.getAnnotation(EAParent.class);
                if (parent != null) {
                    String parentName = tmp.getCanonicalName().replace(".", "_");
                    Field uiTypeField = null;
                    if (uiType instanceof FragmentActivity) {
                        uiTypeField = tmp.getDeclaredField("viewActivity");
                    } else if (uiType instanceof Fragment) {
                        uiTypeField = tmp.getDeclaredField("viewFragment");
                    }
                    if (uiTypeField != null) {
                        uiTypeField.setAccessible(true);
                        uiTypeField.set(bindObject, uiType);
                        Field field = tmp.getDeclaredField("lifecycleOwner");
                        field.setAccessible(true);
                        field.set(bindObject, uiType);
                    }
                    Method initMethod = tmp.getMethod("init_" + parentName, ViewGroup.class);
                    initMethod.invoke(bindObject, vg);
                    Method renderWidget = tmp.getMethod("renderWidget_" + parentName);
                    renderWidget.invoke(bindObject);
                }
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            tmp = tmp.getSuperclass();
        }
    }
}
