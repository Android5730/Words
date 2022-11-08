package com.itaem.words;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class AddFragment extends Fragment {
    private EditText englishEdit,chineseEdit;
    private Button button;
    private View view;
    private WordViewModel wordViewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        wordViewModel = new ViewModelProvider(this).get(WordViewModel.class);
        view = inflater.inflate(R.layout.fragment_add, container, false);
        iniView();
        return view;
    }

    private void iniView() {
        englishEdit = view.findViewById(R.id.englishEdit);
        chineseEdit = view.findViewById(R.id.chineseEdit);
        button = view.findViewById(R.id.button);
        button.setEnabled(false);
        englishEdit.setFocusable(true);
        englishEdit.setFocusableInTouchMode(true);
        englishEdit.requestFocus();
/*        // 自动唤醒键盘
        InputMethodManager inputMethodManager = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        // 参数：跟控件挂钩；*/
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(englishEdit, 0);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // trim()切掉前后空格
                String english = englishEdit.getText().toString().trim();
                String chinese = chineseEdit.getText().toString().trim();
                // 两者都不为空，结果反应到button的可点击性上
                button.setEnabled(!english.isEmpty()&&!chinese.isEmpty());
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        };
        englishEdit.addTextChangedListener(textWatcher);
        chineseEdit.addTextChangedListener(textWatcher);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String english = englishEdit.getText().toString().trim();
                String chinese = chineseEdit.getText().toString().trim();
                wordViewModel.insertWords(new Word(english,chinese));
                NavController navController = Navigation.findNavController(view);
                navController.navigateUp();
                InputMethodManager inputMethodManager = (InputMethodManager)requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
            }
        });
    }



}