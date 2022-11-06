package com.itaem.words;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao  // Database access object
public interface WordDao {
    @Insert
    void insertWords(Word... words);

    @Update
    void updateWords(Word...words);

    @Delete
    void deleteWords(Word...words);

    // 清空表
    @Query("DELETE FROM WORD")
    void deleteAllWords();
    // 查询 返回所以内容  DESC降序
    @Query("SELECT * FROM WORD ORDER BY ID DESC")
   // List<Word> getAllWords();
    LiveData<List<Word>> getAllWords();


    // 根据patten模糊查询
    // LIKE 模糊查询
    @Query("SELECT * FROM WORD WHERE english_word LIKE :patten ORDER BY ID DESC")
    LiveData<List<Word>> findWordWithPatten(String patten);

}
