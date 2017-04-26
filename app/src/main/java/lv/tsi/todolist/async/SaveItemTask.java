package lv.tsi.todolist.async;


import android.content.Context;
import android.os.AsyncTask;
import lv.tsi.todolist.ToDoItem;
import lv.tsi.todolist.db.ToDoItemRepository;

public class SaveItemTask extends AsyncTask<ToDoItem, Void, Void> {

    Context context;
    ToDoItemRepository repository;

    public SaveItemTask(Context context) {
        this.context = context;
        repository = new ToDoItemRepository(context);
    }

    @Override
    protected Void doInBackground(ToDoItem... params) {
        for (ToDoItem item : params) {
            repository.insert(item);
        }
        return null;
    }

}
