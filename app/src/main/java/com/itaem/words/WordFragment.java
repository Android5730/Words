package com.itaem.words;

import android.app.Activity;
import android.app.AlertDialog;
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
            // ???????????????
            @Override
            // viewHolder:?????????????????????item
            public void onAnimationFinished(@NonNull RecyclerView.ViewHolder viewHolder) {
                super.onAnimationFinished(viewHolder);
                // ??????LinearLayoutManager
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (linearLayoutManager!=null){
                    // ?????????????????????????????????????????????item
                    int firstPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    int lastPosition = linearLayoutManager.findLastVisibleItemPosition();
                    for (int i = firstPosition;i<=lastPosition;i++){
                        // ??????viewHolder
                        WordAdapter.ViewHolder holder = (WordAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                        // ??????????????????firstPosition???lastPosition?????????-1??????????????????????????????????????????
                        if (firstPosition!=-1&&lastPosition!=-1){
                            assert holder != null;
                            holder.number.setText(String.valueOf(i));
                        }
                    }
                }
            }
        });
        // ????????????item??????
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(VIEW_TYPE, Context.MODE_PRIVATE);
        boolean viewType = sharedPreferences.getBoolean(IS_USING_CARD_VIEW,false);
        // ?????????
        dividerItemDecoration = new DividerItemDecoration(requireActivity(),DividerItemDecoration.VERTICAL);
        if(viewType){
            recyclerView.setAdapter(adapter2);
        }else {
            recyclerView.setAdapter(adapter1);
            // ?????????RV
            recyclerView.addItemDecoration(dividerItemDecoration);
        }
        findWords = viewModel.getAllWordsLive();
        // ???????????????????????????????????????onChanged????????????
        findWords.observe(getViewLifecycleOwner(), words -> {
            int temp = adapter1.getItemCount();
            // notifyDataSetChanged???????????????????????????????????????
            // ???????????????????????????notiXX??????????????????
            if (temp!=words.size()){
                // ????????????????????????
                if (temp<words.size()&& !undoAction){ // ??????????????????????????????????????????????????????
                    recyclerView.smoothScrollBy(0,-200);
                }
                undoAction = false;
                // ????????????????????????(?????????????????????????????????)
                adapter1.submitList(words);
                adapter2.submitList(words);
/*
                adapter1.notifyDataSetChanged();
                adapter2.notifyDataSetChanged();*/
            }
        });
        // rv????????????
        // SimpleCallback(int dragDirs, int swipeDirs)
        // ????????????????????????????????????????????????
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.START|ItemTouchHelper.END) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
/*                // ????????????
                Word wordFrom = findWords.getValue().get(viewHolder.getAbsoluteAdapterPosition());
                // ???????????????????????????????????????
                *//*viewHolder.itemView.setBackgroundColor((Color.parseColor("#000000")));*//*
                // ????????????
                Word wordTo = findWords.getValue().get(target.getAbsoluteAdapterPosition());
                // ???????????????-??????id
                int idTemp = wordFrom.getId();
                wordFrom.setId(wordTo.getId());
                wordTo.setId(idTemp);
                viewModel.updateWords(wordFrom,wordTo);
                // ????????????
                adapter1.notifyItemMoved(viewHolder.getAbsoluteAdapterPosition(),target.getAbsoluteAdapterPosition());
                adapter2.notifyItemMoved(viewHolder.getAbsoluteAdapterPosition(),target.getAbsoluteAdapterPosition());*/
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // ?????????
                // ???????????????????????????
                // ????????????????????????????????????????????????????????????
                if (findWords!=null){
                    Word wordDelete = findWords.getValue().get(viewHolder.getAbsoluteAdapterPosition());
                    viewModel.deleteWords(wordDelete);
                    Snackbar.make(view.findViewById(R.id.wordFragmentLayout),"?????????????????????",Snackbar.LENGTH_SHORT)
                            .setAction("??????", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    undoAction = true;
                                    viewModel.insertWords(wordDelete);
                                }
                            })
                            .show();
                }
            }
            final Drawable icon = ContextCompat.getDrawable(requireActivity(),R.drawable.ic_baseline_delete_forever_24);
            final Drawable background = new ColorDrawable(Color.LTGRAY);
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
            // ???????????????rv???
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

    // ????????????
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main,menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setMaxWidth((int)(requireActivity().getWindow().getDecorView().getWidth()*0.6));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                // ???????????????
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                // ????????????
                String patten = s.trim();
                // ????????????????????????????????????????????????
                findWords.removeObservers(getViewLifecycleOwner());
                findWords = viewModel.findWordWithPatten(patten);
                findWords.observe(getViewLifecycleOwner(), new Observer<List<Word>>() {
                    @Override
                    public void onChanged(List<Word> words) {
                        int temp = adapter1.getItemCount();
                        // notifyDataSetChanged???????????????????????????????????????
                        // ???????????????????????????notiXX??????????????????
                        if (temp!=words.size()){
                            adapter1.submitList(words);
                            adapter2.submitList(words);
                        }
                    }
                });
                Log.d("TAG", "onQueryTextChange: "+s);
                return true; // ????????????
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.cleanData:
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setTitle("????????????")
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                viewModel.deleteAllWords();
                            }
                        })
                        .setNegativeButton("??????",null)
                        .create().show();
                break;
            case R.id.switchViewType:
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(VIEW_TYPE, Context.MODE_PRIVATE);
                boolean viewType = sharedPreferences.getBoolean(IS_USING_CARD_VIEW,false);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                // ???????????????-??????
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

        // adapter?????????????????????????????????????????????????????????Fragment??????????????????
/*        adapter1 = new WordAdapter(false,viewModel);
        adapter2 = new WordAdapter(true,viewModel);*/
    }
}