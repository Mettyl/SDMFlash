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

    public YourWordsViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Word>> getWordsByAlphabet() {
        words = loadWords(1);
        return words;
    }

    public LiveData<List<Word>> getWordsByDifficulty() {
        words = loadWords(2);
        return words;
    }

    public LiveData<List<Word>> getWordsByTest() {
        words = loadWords(3);
        return words;
    }

    public LiveData<List<Word>> getWordsByAdded() {
        words = loadWords(4);
        return words;
    }

    public LiveData<List<Word>> getWordsBySource() {
        words = loadWords(5);
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
                        return AppDatabase.getInstance(app[0].getApplicationContext()).wordDao().getAll();
                    case 2:
                        return AppDatabase.getInstance(app[0].getApplicationContext()).wordDao().getAllByFile();
                    case 3:
                        return AppDatabase.getInstance(app[0].getApplicationContext()).wordDao().getAllByChanged();
                    case 4:
                        return AppDatabase.getInstance(app[0].getApplicationContext()).wordDao().getAllByAdded();
                    case 5:
                        return AppDatabase.getInstance(app[0].getApplicationContext()).wordDao().getAllSource();
                    default:
                        return AppDatabase.getInstance(app[0].getApplicationContext()).wordDao().getAll();
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