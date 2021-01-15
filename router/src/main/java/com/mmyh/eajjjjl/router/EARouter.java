package com.mmyh.eajjjjl.router;

import android.content.Intent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.HashMap;
import java.util.Map;

public class EARouter {

    private final Map<Class, EAService> serviceMap = new HashMap<>();

    private final Map<Class, Class> registerServiceMap = new HashMap<>();

    private static class SingletonHolder {
        private static EARouter instance = new EARouter();
    }

    private EARouter() {

    }

    public static <T extends EAService> T getService(Class<T> t) {
        return SingletonHolder.instance.findService(t);
    }

    public static <T extends EAService, M extends T> void registerService(Class<T> t, Class<M> impl) {
        SingletonHolder.instance.register(t, impl);
    }

    public static void startActivity(FragmentActivity activity, Intent intent, EACallback callback) {
        SingletonHolder.instance.startAct(activity, intent, callback);
    }

    public static void startActivity(Fragment fragment, Intent intent, EACallback callback) {
        SingletonHolder.instance.startAct(fragment, intent, callback);
    }

    private void startAct(FragmentActivity activity, Intent intent, EACallback callback) {
        EAFragment fragment = getMYFragment(activity);
        fragment.startActivityForResult(intent, callback);
    }

    private void startAct(Fragment f, Intent intent, EACallback callback) {
        EAFragment fragment = getMYFragment(f);
        fragment.startActivityForResult(intent, callback);
    }

    private <T extends EAService, M extends T> void register(Class<T> t, Class<M> impl) {
        if (registerServiceMap.get(t) == null) {
            registerServiceMap.put(t, impl);
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends EAService> T findService(Class<T> t) {
        EAService service = serviceMap.get(t);
        if (service == null) {
            try {
                service = (EAService) registerServiceMap.get(t).newInstance();
                serviceMap.put(t, service);
                return (T) service;

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        return (T) service;
    }

    private EAFragment getMYFragment(FragmentActivity activity) {
        Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag(EAFragment.class.getCanonicalName());
        if (fragment == null) {
            fragment = new EAFragment();
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .add(fragment, EAFragment.class.getCanonicalName())
                    .commitAllowingStateLoss();
            activity.getSupportFragmentManager().executePendingTransactions();
        }
        return (EAFragment) fragment;
    }

    private EAFragment getMYFragment(Fragment f) {
        Fragment fragment = f.getChildFragmentManager().findFragmentByTag(EAFragment.class.getCanonicalName());
        if (fragment == null) {
            fragment = new EAFragment();
            f.getChildFragmentManager()
                    .beginTransaction()
                    .add(fragment, EAFragment.class.getCanonicalName())
                    .commitAllowingStateLoss();
            f.getChildFragmentManager().executePendingTransactions();
        }
        return (EAFragment) fragment;
    }

}
