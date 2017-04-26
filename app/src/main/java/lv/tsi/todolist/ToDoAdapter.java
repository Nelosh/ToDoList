package lv.tsi.todolist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;

import lv.tsi.todolist.async.DeleteItemTask;
import lv.tsi.todolist.async.UpdateItemTask;
import lv.tsi.todolist.db.ToDoItemRepository;


public class ToDoAdapter extends CursorAdapter {
    ToDoItemRepository repository;
    Context context;

    public ToDoAdapter(Context context, Cursor c) {
        super(context, c, false);
        repository = new ToDoItemRepository(context);
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.todo_element, parent, false);
        ToDoViewHolder viewHolder = new ToDoViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final ToDoViewHolder viewHolder = (ToDoViewHolder) view.getTag();
        final ToDoItem item = repository.toDoListFromCursor(cursor);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        Integer color = sharedPref.getInt("color", 0);

        viewHolder.title.setText(item.getTitle());

        viewHolder.date.setText(item.getFormattedDate());

        viewHolder.checkBox.setOnCheckedChangeListener(null);
        viewHolder.checkBox.setChecked(item.isChecked());
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.setChecked(isChecked);
                new UpdateItemTask(context) {
                    @Override
                    protected void onPostExecute(Void aVoid) {
                        repository.close(swapCursor(repository.getCursorToAll()));
                    }
                }.execute(item);
            }
        });

        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItemAlertDialog(item.getId()).show();
            }
        });

        view.setBackgroundColor(color);

    }

    @Override
    public Object getItem(int position) {
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        return repository.toDoListFromCursor(cursor);
    }

    @Override
    public long getItemId(int position) {
        return ((ToDoItem) getItem(position)).getId();
    }

    private AlertDialog deleteItemAlertDialog(final Long id) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure, you want to delete this item?")
               .setTitle("Delete item");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new DeleteItemTask(context){
                    @Override
                    protected void onPostExecute(Void aVoid) {
                        refresh();
                    }
                }.execute(id);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return builder.create();
    }

    public void refresh() {
        repository.close(swapCursor(repository.getCursorToAll()));
        notifyDataSetChanged();
    }

}

