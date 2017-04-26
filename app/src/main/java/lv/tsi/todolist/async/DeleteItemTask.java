package lv.tsi.todolist.async;


import android.content.Context;
import android.os.AsyncTask;

import lv.tsi.todolist.db.ToDoItemRepository;

public class DeleteItemTask extends AsyncTask<Long, Void, Void> {
    Context context;
    ToDoItemRepository repository;

    public DeleteItemTask(Context context) {
        this.context = context;
        repository = new ToDoItemRepository(context);
    }

    @Override
    protected Void doInBackground(Long... params) {
        for (Long item : params) {
            repository.deleteById(item);
        }
        return null;
    }

}
