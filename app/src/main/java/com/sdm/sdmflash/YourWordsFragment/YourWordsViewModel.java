package com.sdm.sdmflash.YourWordsFragment;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.sdm.sdmflash.db.structure.AppDatabase;
import com.sdm.sdmflash.db.structure.Word;

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

    public LiveData<List<Word>> getWords() {
        if (words == null) {
            words = loadWords();
        }
        return words;
    }

    private LiveData<List<Word>> loadWords() {
        class AskForData extends AsyncTask<Application, Integer, LiveData<List<Word>>> {
            @Override
            protected LiveData<List<Word>> doInBackground(Application... app) {
                return AppDatabase.getInstance(app[0].getApplicationContext()).wordDao().getAll();
            }
        }
        try {
            return new AskForData().execute(this.getApplication()).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }
}