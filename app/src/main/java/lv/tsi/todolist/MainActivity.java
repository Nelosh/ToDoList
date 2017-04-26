package lv.tsi.todolist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import lv.tsi.todolist.async.SaveItemTask;
import lv.tsi.todolist.db.ToDoItemRepository;
import lv.tsi.todolist.preferences.SettingsActivity;

public class MainActivity extends AppCompatActivity {
    public static final String DETAILED_INFO = "lv.tsi.todolist.TODO_INFO";

    @BindView(R.id.listView) ListView listView;
    @BindView(R.id.newItem) EditText editText;

    ToDoItemRepository repository = new ToDoItemRepository(this);
    ToDoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        adapter = new ToDoAdapter(this, repository.getCursorToAll());
        listView.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.buttonAdd)
    public void addItem() {
        ToDoItem item = new ToDoItem(editText.getText().toString());
        addToDoItem(item);
        editText.setText("");
    }

    @OnItemClick(R.id.listView)
    public void itemClick(ListView parent, int position) {
        Intent intent = new Intent(parent.getContext(), DetailedInfoActivity.class);
        intent.putExtra(DETAILED_INFO, parent.getItemIdAtPosition(position));
        parent.getContext().startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.refresh();
    }

    private void addToDoItem(ToDoItem item) {
        new SaveItemTask(this){
            @Override
            protected void onPostExecute(Void aVoid) {
                adapter.refresh();
            }
        }.execute(item);
    }
}
