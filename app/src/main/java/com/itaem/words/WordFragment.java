package com.itaem.words;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Date;
import java.util.List;

public class WordFragment extends Fragment {
    private WordViewModel viewModel;
    private RecyclerView recyclerView;
    private WordAdapter adapter1,adapter2;
    private View view;
    private LiveData<List<Word>> findWords;
    public final static String VIEW_TYPE = "view_type_spe";
    public final static String IS_USING_CARD_VIEW = "is_using_card_view";

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
        // 读取列表item类型
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(VIEW_TYPE, Context.MODE_PRIVATE);
        boolean viewType = sharedPreferences.getBoolean(IS_USING_CARD_VIEW,false);
        if(viewType){
            recyclerView.setAdapter(adapter2);
        }else {
            recyclerView.setAdapter(adapter1);
        }
        findWords = viewModel.getAllWordsLive();
        // 添加观察者，数据变化就执行onChanged（）方法
        findWords.observe(requireActivity(), new Observer<List<Word>>() {
            @Override
            public void onChanged(List<Word> words) {
                int temp = adapter1.getItemCount();
                adapter1.setAllWords(words);
                adapter2.setAllWords(words);
                // notifyDataSetChanged（）数据改变，就去刷新视图
                // 长度不变化，不调用notiXX（）耗时操作
                if (temp!=words.size()){
                    adapter1.notifyDataSetChanged();
                    adapter2.notifyDataSetChanged();
                }
            }
        });
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
                findWords.removeObservers(requireActivity());
                findWords = viewModel.findWordWithPatten(patten);
                findWords.observe(requireActivity(), new Observer<List<Word>>() {
                    @Override
                    public void onChanged(List<Word> words) {
                        int temp = adapter1.getItemCount();
                        adapter1.setAllWords(words);
                        adapter2.setAllWords(words);
                        // notifyDataSetChanged（）数据改变，就去刷新视图
                        // 长度不变化，不调用notiXX（）耗时操作
                        if (temp!=words.size()){
                            adapter1.notifyDataSetChanged();
                            adapter2.notifyDataSetChanged();
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
                        .setPositiveButton("取消",null)
                        .create().show();
                break;
            case R.id.switchViewType:
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(VIEW_TYPE, Context.MODE_PRIVATE);
                boolean viewType = sharedPreferences.getBoolean(IS_USING_CARD_VIEW,false);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                // 持久化存储-切换
                if (viewType){
                    recyclerView.setAdapter(adapter1);
                    editor.putBoolean(IS_USING_CARD_VIEW,false);
                }else {
                    recyclerView.setAdapter(adapter2);
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