package com.sdm.sdmflash.fragmentYourWords;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.sdm.sdmflash.databases.structure.appDatabase.AppDatabase;
import com.sdm.sdmflash.databases.structure.appDatabase.Word;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by mety on 3.2.18.
 */

public class YourWordsViewModel extends AndroidViewModel {

    private LiveData<List<Word>> words;
    private int wordsType = 1;

    public YourWordsViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Word>> getWords() {
        return words;
    }

    public LiveData<List<Word>> getWordsByAlphabet() {
        int type = 1;
        if (words == null || wordsType != type) {
            words = loadWords(type);
            wordsType = type;
            return words;
        }
        return words;
    }

    public LiveData<List<Word>> getWordsByDifficulty() {
        int type = 2;
        if (words == null || wordsType != type) {
            words = loadWords(type);
            wordsType = type;
            return words;
        }
        return words;
    }

    public LiveData<List<Word>> getWordsByTest() {
        int type = 3;
        if (words == null || wordsType != type) {
            words = loadWords(type);
            wordsType = type;
            return words;
        }
        return words;
    }

    public LiveData<List<Word>> getWordsByAdded() {
        int type = 4;
        if (words == null || wordsType != type) {
            words = loadWords(type);
            wordsType = type;
            return words;
        }
        return words;
    }

    public LiveData<List<Word>> getWordsBySource() {
        int type = 5;
        if (words == null || wordsType != type) {
            words = loadWords(type);
            wordsType = type;
            return words;
        }
        return words;
    }

    private LiveData<List<Word>> loadWords(int type) {

        class AskForData extends AsyncTask<Application, Integer, LiveData<List<Word>>> {

            private int type;

            public AskForData(int type) {
                this.type = type;
            }

            @Override
            protected LiveData<List<Word>> doInBackground(Application... app) {
                switch (type) {
                    case 1:
                        return AppDatabase.getInstance(app[0].getApplicationContext()).wordDao().getAllLive();
                    case 2:
                        return AppDatabase.getInstance(app[0].getApplicationContext()).wordDao().getAllByFile();
                    case 3:
                        return AppDatabase.getInstance(app[0].getApplicationContext()).wordDao().getAllByChanged();
                    case 4:
                        return AppDatabase.getInstance(app[0].getApplicationContext()).wordDao().getAllByAdded();
                    case 5:
                        return AppDatabase.getInstance(app[0].getApplicationContext()).wordDao().getAllSource();
                    default:
                        return AppDatabase.getInstance(app[0].getApplicationContext()).wordDao().getAllLive();
                }
            }
        }
        try {
            return new AskForData(type).execute(this.getApplication()).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }
}