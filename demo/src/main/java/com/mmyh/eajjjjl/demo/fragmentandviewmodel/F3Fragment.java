package com.mmyh.eajjjjl.demo.fragmentandviewmodel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mmyh.eajjjjl.annotation.EAView;


@EAView(viewModels = TestViewModel1.class,
        superClass = F1Fragment.class)
public class F3Fragment extends F3FragmentParent {


}
