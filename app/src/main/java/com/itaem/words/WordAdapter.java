package com.itaem.words;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class WordAdapter extends ListAdapter<Word,WordAdapter.ViewHolder> {

    boolean useCardView;
    private final WordViewModel viewModel;



    public WordAdapter(boolean useCardView, WordViewModel wordViewModel) {
        super(new DiffUtil.ItemCallback<Word>() {
            @Override
            public boolean areItemsTheSame(@NonNull Word oldItem, @NonNull Word newItem) {
                // 比较列表两个元素id是否相同
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Word oldItem, @NonNull Word newItem) {
                // 内容是否相同
                return (oldItem.getWord().equals(newItem.getWord())&&
                        oldItem.getChineseMeaning().equals(newItem.getChineseMeaning())&&
                        oldItem.isChineseInvisible() == newItem.isChineseInvisible());
            }
        });
        this.useCardView = useCardView;
        this.viewModel = wordViewModel;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView number;
        TextView englishText;
        TextView chineseText;
        SwitchMaterial switchMaterial;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            number = itemView.findViewById(R.id.number);
            englishText = itemView.findViewById(R.id.englishText);
            chineseText = itemView.findViewById(R.id.chineseText);
            switchMaterial = itemView.findViewById(R.id.switchView);
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (useCardView){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_normal,parent,false);
        }
        final ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(v -> {
            // 跳转系统浏览器
            Uri uri = Uri.parse("https://www.youdao.com/result?word="+holder.englishText.getText()+"&lang=en");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            holder.itemView.getContext().startActivity(intent);
        });
        // 获取position
        // bind方法存入tag
        // 在Create取出tag
        holder.switchMaterial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Word word = (Word) holder.itemView.getTag(R.id.word_for_view_holder);
                if (b){
                    holder.chineseText.setVisibility(View.GONE);
                    word.setChineseInvisible(true);
                    viewModel.updateWords(word);
                }else {
                    holder.chineseText.setVisibility(View.VISIBLE);
                    word.setChineseInvisible(false);
                    viewModel.updateWords(word);
                }
            }
        });
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Word word = getItem(position);
        holder.itemView.setTag(R.id.word_for_view_holder,word);
        holder.number.setText(String.valueOf(position));
        holder.englishText.setText(word.getWord());
        holder.chineseText.setText(word.getChineseMeaning());
        if (word.isChineseInvisible){
            holder.chineseText.setVisibility(View.GONE);
            holder.switchMaterial.setChecked(true);
        }else {
            holder.chineseText.setVisibility(View.VISIBLE);
            holder.switchMaterial.setChecked(false);
        }

    }

    /**
     * Called when a view created by this adapter has been attached to a window.
     *
     * <p>This can be used as a reasonable signal that the view is about to be seen
     * by the user. If the adapter previously freed any resources in
     * {@link #onViewDetachedFromWindow(RecyclerView.ViewHolder) onViewDetachedFromWindow}
     * those resources should be restored here.</p>
     *
     * @param holder Holder of the view being attached
     */
    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.number.setText(String.valueOf(holder.getBindingAdapterPosition()));
    }
}
