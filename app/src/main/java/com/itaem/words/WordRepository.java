package com.itaem.words;


import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class WordRepository {
    private final LiveData<List<Word>> allWordsLive;
    private final WordDao wordDao;

    public WordRepository(Context context) {
        WordDetabase wordDetabase = WordDetabase.getDatabase(context);
        wordDao = wordDetabase.getWordDao();
        allWordsLive =  wordDao.getAllWords();
    }

    public LiveData<List<Word>> getAllWordsLive() {
        return allWordsLive;
    }
    public LiveData<List<Word>> findWordWithPatten(String patten){
        // SQL模糊匹配需要通配符
        return wordDao.findWordWithPatten("%"+patten+"%");
    }


    // 暴露方法给ViewModel调用
    void insertWords(Word...words){
        new InsertAsyncTask(wordDao).execute(words);
    }
    void updateWords(Word...words){
        new UpdateAsyncTask(wordDao).execute(words);
    }
    void deleteWords(Word...words){
        new DeleteAsyncTask(wordDao).execute(words);
    }
    void deleteAllWords(){
        new DeleteAllAsyncTask(wordDao).execute();
    }
    // 子线程操作
    static class InsertAsyncTask extends AsyncTask<Word,Void,Void> {
        private final WordDao wordDao;

        public InsertAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.insertWords(words);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
        }
    }
    static class UpdateAsyncTask extends AsyncTask<Word,Void,Void>{
        private final WordDao wordDao;

        public UpdateAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.updateWords(words);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
        }
    }
    static class DeleteAsyncTask extends AsyncTask<Word,Void,Void>{
        private final WordDao wordDao;

        public DeleteAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.deleteWords(words);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
        }
    }
    static class DeleteAllAsyncTask extends AsyncTask<Void,Void,Void>{
        private final WordDao wordDao;

        public DeleteAllAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            wordDao.deleteAllWords();
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
        }
    }
}
