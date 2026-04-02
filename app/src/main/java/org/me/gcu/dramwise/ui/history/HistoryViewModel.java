package org.me.gcu.dramwise.ui.history;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.me.gcu.dramwise.data.DrinkEntry;
import org.me.gcu.dramwise.data.DrinkRepository;

import java.util.List;

public class HistoryViewModel extends AndroidViewModel {

    private final DrinkRepository repository;
    private final LiveData<List<DrinkEntry>> allEntries;

    public HistoryViewModel(@NonNull Application application) {
        super(application);
        repository = DrinkRepository.getInstance(application);
        allEntries = repository.getAll();
    }

    public LiveData<List<DrinkEntry>> getAllEntries() {
        return allEntries;
    }

    public void clearHistory() {
        repository.clearHistory();
    }
}