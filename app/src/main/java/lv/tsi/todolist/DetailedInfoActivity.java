package lv.tsi.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lv.tsi.todolist.async.UpdateItemTask;
import lv.tsi.todolist.db.ToDoItemRepository;

import static lv.tsi.todolist.MainActivity.DETAILED_INFO;

public class DetailedInfoActivity extends AppCompatActivity{

    ToDoItem item;
    ToDoItemRepository repository;

    @BindView(R.id.info) EditText editText;
    @BindView(R.id.img) ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_info);

        ButterKnife.bind(this);

        repository = new ToDoItemRepository(this);

        final Intent intent = getIntent();
        final Long id = intent.getLongExtra(DETAILED_INFO, -1);
        if (id == -1) {
            finish();
        }

        item = repository.getById(id);
        if (item == null) {
            finish();
        }

        editText.setText(item.getDetails());
    }

    @OnClick(R.id.backToList)
    public void goBack() {
        item.setDetails(editText.getText().toString());
        new UpdateItemTask(DetailedInfoActivity.this){
            @Override
            protected void onPostExecute(Void aVoid) {
                finish();
            }
        }.execute(item);
    }

    @OnClick(R.id.share)
    public void share() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, item.getDetails());
        try {
            intent.setPackage("com.twitter.android");
            startActivity(intent);
        } catch(Exception e) {
            e.printStackTrace();
            startActivity(Intent.createChooser(intent, "Title of the dialog the system will open"));
        }
    }

    @OnClick(R.id.img)
    public void changeImg() {

    }


}
