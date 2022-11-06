package com.itaem.words;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

// singleton
// 四个参数：entities(复数) = {} （带上集合{}符号）；版本号;
@Database(entities = {Word.class},version = 5,exportSchema = false)
// 抽象类
public abstract class WordDetabase extends RoomDatabase {
    private static WordDetabase INSTANCE;
    // synchronized 进一步强化singleton强度，保证有多个线程的客户端，保证不会碰撞和冲突，采用排队机制
    // 将获取实例写在此类中，同时返回实例
    static synchronized WordDetabase getDatabase(Context context){
        if (INSTANCE==null){
            // context.getApplicationContext()返回应用程序根节点的context
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),WordDetabase.class,"Word database")
                    // 数据库版本更新 数据迁移方式
        //            .fallbackToDestructiveMigration() // 破坏性的迁移 即创建新的数据库（旧数据没了）
                    .addMigrations(MIGRATION_5_6)  // 添加字段 可放多个参数
                    .build();
        }
        return INSTANCE;
    }
    // 若有多个Entity，则应该写多个Dao
    public abstract WordDao getWordDao();


    // 常量添加
    static final Migration MIGRATION_4_5 = new Migration(4,5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // 改动word表名 实体类名     bar_data新增的列     NOT NULL DEFAULT非空约束为1(即把之前数据 该列的值设为1)
            database.execSQL("ALTER TABLE word ADD COLUMN foo_data INTEGER NOT NULL DEFAULT 1");
        }
    };
    // 常量删除
    static final Migration MIGRATION_3_4 = new Migration(3,4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // 1.新建表
            database.execSQL("CREATE TABLE word_temp (id INTEGER PRIMARY KEY NOT NULL, english_word TEXT," +
                    "chinese_meaning TEXT)");
            // 2.从旧表取出相应列给新表
            database.execSQL("INSERT INTO word_temp (id,english_word,chinese_meaning)" +
                    "SELECT id,english_word,chinese_meaning FROM word");
            // 3.删除旧表
            database.execSQL("Drop table word");
            // 4.添加新表并改名为旧表名
            database.execSQL("ALTER TABLE word_temp RENAME to word");
        }
    };


    // 常量添加
    static final Migration MIGRATION_5_6 = new Migration(5,6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // 改动word表名 实体类名     bar_data新增的列     NOT NULL DEFAULT非空约束为1(即把之前数据 该列的值设为1)
            database.execSQL("ALTER TABLE word ADD COLUMN chinese_invisible INTEGER NOT NULL DEFAULT 0");
        }
    };
}
