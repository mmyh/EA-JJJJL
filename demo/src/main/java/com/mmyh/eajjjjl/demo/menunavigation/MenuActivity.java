package com.mmyh.eajjjjl.demo.menunavigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.mmyh.eajjjjl.demo.R;
import com.mmyh.eajjjjl.widget.toolbarwindow.EAPopWin;
import com.mmyh.eajjjjl.widget.toolbarwindow.EAPopWinItem;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_menu);
        List<EAPopWinItem> list = new ArrayList<>();
        list.add(createItem("啊啊", R.id.Fragment1, Fragment1.class.getCanonicalName(), false));
        list.add(createItem("冲冲冲", R.id.Fragment2, Fragment2.class.getCanonicalName(), true));
        list.add(createItem("顶顶顶顶", R.id.Fragment3, Fragment3.class.getCanonicalName(), false));
        final EAPopWin popWin = new EAPopWin.Builder(this, R.id.nav_host_fragment, list)
                .dividerBack(R.drawable.dividerback)
                .popBack(R.drawable.pop_back)
                .build();
        final Button btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWin.get().showAsDropDown(btnMenu);
            }
        });
    }

    private View createItemView(String text) {
        View view = LayoutInflater.from(this).inflate(R.layout.menu_item, null);
        TextView tv = view.findViewById(R.id.tv);
        tv.setText(text);
        return view;
    }

    private EAPopWinItem createItem(String text, int id, String name, boolean isStart) {
        EAPopWinItem item = new EAPopWinItem();
        item.view = createItemView(text);
        item.navDestinationId = id;
        item.navDestinationClassName = name;
        item.isStartDestination = isStart;
        return item;
    }
}
