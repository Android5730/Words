package com.itaem.words;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatToggleButton;
import androidx.core.app.TaskStackBuilder;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CompoundButton;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    // 只能在onStart（）函数添加，此时xml已经画好
    @Override
    protected void onStart() {
        super.onStart();
        // 控制器； 存在与活动中，给碎片切换用的控件名
        NavigationUI.setupActionBarWithNavController(this,
                Navigation.findNavController(this,R.id.fragmentContainerView));
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController controller = Navigation.findNavController(this,R.id.fragmentContainerView);
/*        InputMethodManager inputMethodManager = (InputMethodManager)this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        // 存储Fragment的容器
        inputMethodManager.hideSoftInputFromWindow(findViewById(R.id.fragmentContainerView),0);*/
        return controller.navigateUp();
    }
}
