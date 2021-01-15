package com.mmyh.eajjjjl.router;

import android.content.Intent;
import android.util.SparseArray;

import androidx.fragment.app.Fragment;

public class EAFragment extends Fragment {

    private SparseArray<EACallback> mCallbacks = new SparseArray<>();

    private static int requestCode = 1000;

    public void startActivityForResult(Intent intent, EACallback callback) {
        while (mCallbacks.indexOfKey(requestCode) >= 0) {
            requestCode++;
        }
        mCallbacks.put(requestCode, callback);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EACallback uCallback = mCallbacks.get(requestCode);
        mCallbacks.remove(requestCode);
        if (uCallback != null) {
            uCallback.onActivityResult(resultCode, data);
        }
    }
}
