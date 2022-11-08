package com.itaem.words;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Word {
    // 主键 自动生成
    @PrimaryKey(autoGenerate = true)
    private int id;

    // 列的名称
    @ColumnInfo(name = "english_word")
    private final String word;

    @ColumnInfo(name = "chinese_meaning")
    private final String chineseMeaning;
    @ColumnInfo(name = "foo_data")
    private boolean foo;
    @ColumnInfo(name = "chinese_invisible")
    public boolean isChineseInvisible;
/*    @ColumnInfo(name = "bar_data")
    private boolean bar;

    public boolean isBar() {
        return bar;
    }

    public void setBar(boolean bar) {
        this.bar = bar;
    }
*/
    public boolean isFoo() {
        return foo;
    }

    public void setFoo(boolean foo) {
        this.foo = foo;
    }

    public Word(String word, String chineseMeaning) {
        this.word = word;
        this.chineseMeaning = chineseMeaning;
    }

    public String getWord() {
        return word;
    }

    public String getChineseMeaning() {
        return chineseMeaning;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public boolean isChineseInvisible() {
        return isChineseInvisible;
    }

    public void setChineseInvisible(boolean chineseInvisible) {
        isChineseInvisible = chineseInvisible;
    }
}
