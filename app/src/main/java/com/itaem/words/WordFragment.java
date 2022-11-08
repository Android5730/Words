package com.itaem.words;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class WordFragment extends Fragment {
    private WordViewModel viewModel;
    private RecyclerView recyclerView;
    private WordAdapter adapter1,adapter2;
    private View view;
    private LiveData<List<Word>> findWords;
    public final static String VIEW_TYPE = "view_type_spe";
    public final static String IS_USING_CARD_VIEW = "is_using_card_view";
    private boolean undoAction;
    private DividerItemDecoration dividerItemDecoration;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_word, container, false);
        setHasOptionsMenu(true);
        initView();
        return view;
    }

    private void initView() {
        viewModel = new ViewModelProvider(this).get(WordViewModel.class);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        adapter1 = new WordAdapter(false,viewModel);
        adapter2 = new WordAdapter(true,viewModel);
        recyclerView.setItemAnimator(new DefaultItemAnimator(){
            // 动画结束后
            @Override
            // viewHolder:正在动画的单元item
            public void onAnimationFinished(@NonNull RecyclerView.ViewHolder viewHolder) {
                super.onAnimationFinished(viewHolder);
                // 找回LinearLayoutManager
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (linearLayoutManager!=null){
                    // 获取可使界面的第一个和最后一个item
                    int firstPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    int lastPosition = linearLayoutManager.findLastVisibleItemPosition();
                    for (int i = firstPosition;i<=lastPosition;i++){
                        // 取回viewHolder
                        WordAdapter.ViewHolder holder = (WordAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                        // 清空数据时，firstPosition及lastPosition会变成-1，导致下方控件出现空对象异常
                        if (firstPosition!=-1&&lastPosition!=-1){
                            holder.number.setText(String.valueOf(i));
                        }
                    }
                }
            }
        });
        // 读取列表item类型
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(VIEW_TYPE, Context.MODE_PRIVATE);
        boolean viewType = sharedPreferences.getBoolean(IS_USING_CARD_VIEW,false);
        // 实例化
        dividerItemDecoration = new DividerItemDecoration(requireActivity(),DividerItemDecoration.VERTICAL);
        if(viewType){
            recyclerView.setAdapter(adapter2);
        }else {
            recyclerView.setAdapter(adapter1);
            // 设置给RV
            recyclerView.addItemDecoration(dividerItemDecoration);
        }
        findWords = viewModel.getAllWordsLive();
        // 添加观察者，数据变化就执行onChanged（）方法
        findWords.observe(getViewLifecycleOwner(), new Observer<List<Word>>() {
            @Override
            public void onChanged(List<Word> words) {
                int temp = adapter1.getItemCount();
                // notifyDataSetChanged（）数据改变，就去刷新视图
                // 长度不变化，不调用notiXX（）耗时操作
                if (temp!=words.size()){
                    // 滚动产生视觉反馈
                    if (temp<words.size()&& !undoAction){ // 原有参数小于现有的参数，即增加就滑动
                        recyclerView.smoothScrollBy(0,-200);
                    }
                    undoAction = false;
                    // 提交列表给适配器(后台自动比较，取决刷新)
                    adapter1.submitList(words);
                    adapter2.submitList(words);
/*
                    adapter1.notifyDataSetChanged();
                    adapter2.notifyDataSetChanged();*/
                }
            }
        });
        // rv辅助工具
        // SimpleCallback(int dragDirs, int swipeDirs)
        // 参数：允许拖动方向；允许滑动方向
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.START|ItemTouchHelper.END) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
/*                // 移动单位
                Word wordFrom = findWords.getValue().get(viewHolder.getAbsoluteAdapterPosition());
                // 移动时变色，移动后恢复原色
                *//*viewHolder.itemView.setBackgroundColor((Color.parseColor("#000000")));*//*
                // 目标单元
                Word wordTo = findWords.getValue().get(target.getAbsoluteAdapterPosition());
                // 交换两单元-交换id
                int idTemp = wordFrom.getId();
                wordFrom.setId(wordTo.getId());
                wordTo.setId(idTemp);
                viewModel.updateWords(wordFrom,wordTo);
                // 通知视图
                adapter1.notifyItemMoved(viewHolder.getAbsoluteAdapterPosition(),target.getAbsoluteAdapterPosition());
                adapter2.notifyItemMoved(viewHolder.getAbsoluteAdapterPosition(),target.getAbsoluteAdapterPosition());*/
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // 做删除
                // 异步获取，可能为空
                // 也可以写集合变量在控制器观察函数内部接收
                if (findWords!=null){
                    Word wordDelete = findWords.getValue().get(viewHolder.getAbsoluteAdapterPosition());
                    viewModel.deleteWords(wordDelete);
                    Snackbar.make(view.findViewById(R.id.wordFragmentLayout),"删除了一个词汇",Snackbar.LENGTH_SHORT)
                            .setAction("撤销", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    undoAction = true;
                                    viewModel.insertWords(wordDelete);
                                }
                            })
                            .show();
                }
            }
            Drawable icon = ContextCompat.getDrawable(requireActivity(),R.drawable.ic_baseline_delete_forever_24);
            Drawable background = new ColorDrawable(Color.LTGRAY);
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView = viewHolder.itemView;
                int iconMargin = itemView.getHeight() - icon.getIntrinsicHeight()/2;
                int iconLeft,iconRight,iconTop,iconBottom;
                int backTop,backBottom,backLeft,backRight;
                backTop = itemView.getTop();
                backBottom = itemView.getBottom();
                iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight())/2;
                iconBottom = iconTop + icon.getIntrinsicHeight();
                if (dX>0){
                    backLeft = itemView.getLeft();
                    backRight = itemView.getLeft() + (int)dX;
                    background.setBounds(backLeft,backTop,backRight,backBottom);
                    iconLeft = itemView.getLeft() + iconMargin;
                    iconRight = iconLeft + icon.getIntrinsicWidth();
                    icon.setBounds(iconLeft,iconTop,iconRight,iconBottom);
                }else if (dX<0){
                    backRight = itemView.getRight();
                    backLeft = itemView.getRight() + (int) dX;
                    background.setBounds(backLeft,backTop,backRight,backBottom);
                    iconRight = itemView.getRight() - iconMargin;
                    iconLeft = iconRight - icon.getIntrinsicWidth();
                    icon.setBounds(iconLeft,iconTop,iconRight,iconBottom);
                }else {
                    background.setBounds(0,0,0,0);
                    icon.setBounds(0,0,0,0);
                }
                background.draw(c);
                icon.draw(c);
            }
            // 附加到相应rv上
        }).attachToRecyclerView(recyclerView);

        FloatingActionButton button = view.findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_wordFragment_to_addFragment);
            }
        });
    }

    // 创建菜单
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main,menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setMaxWidth((int)(requireActivity().getWindow().getDecorView().getWidth()*0.6));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                // 按了确定键
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                // 内容改变
                String patten = s.trim();
                // 两个观察会产生碰撞，此处去除观察
                findWords.removeObservers(getViewLifecycleOwner());
                findWords = viewModel.findWordWithPatten(patten);
                findWords.observe(getViewLifecycleOwner(), new Observer<List<Word>>() {
                    @Override
                    public void onChanged(List<Word> words) {
                        int temp = adapter1.getItemCount();
                        // notifyDataSetChanged（）数据改变，就去刷新视图
                        // 长度不变化，不调用notiXX（）耗时操作
                        if (temp!=words.size()){
                            adapter1.submitList(words);
                            adapter2.submitList(words);
                        }
                    }
                });
                Log.d("TAG", "onQueryTextChange: "+s);
                return true; // 消耗事件
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.cleanData:
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setTitle("清空数据")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                viewModel.deleteAllWords();
                            }
                        })
                        .setNegativeButton("取消",null)
                        .create().show();
                break;
            case R.id.switchViewType:
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(VIEW_TYPE, Context.MODE_PRIVATE);
                boolean viewType = sharedPreferences.getBoolean(IS_USING_CARD_VIEW,false);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                // 持久化存储-切换
                if (viewType){
                    recyclerView.setAdapter(adapter1);
                    recyclerView.addItemDecoration(dividerItemDecoration);
                    editor.putBoolean(IS_USING_CARD_VIEW,false);
                }else {
                    recyclerView.setAdapter(adapter2);
                    recyclerView.removeItemDecoration(dividerItemDecoration);
                    editor.putBoolean(IS_USING_CARD_VIEW,true);
                }
                editor.apply();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        InputMethodManager inputMethodManager = (InputMethodManager)requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(),0);
        super.onResume();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // adapter实例化放在这里比较合适，否则进入下一个Fragment都会被销毁？
/*        adapter1 = new WordAdapter(false,viewModel);
        adapter2 = new WordAdapter(true,viewModel);*/
    }
}