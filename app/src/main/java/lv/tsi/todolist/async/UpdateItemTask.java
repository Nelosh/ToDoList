package lv.tsi.todolist.async;


import android.content.Context;
import android.os.AsyncTask;

import lv.tsi.todolist.ToDoItem;
import lv.tsi.todolist.db.ToDoItemRepository;

public class UpdateItemTask extends AsyncTask<ToDoItem, Void, Void> {
    Context context;
    ToDoItemRepository repository;

    public UpdateItemTask(Context context) {
        this.context = context;
        repository = new ToDoItemRepository(context);
    }

    @Override
    protected Void doInBackground(ToDoItem... params) {
        for(ToDoItem param: params) {
            repository.update(param);
        }
        return null;
    }

}
