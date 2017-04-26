package lv.tsi.todolist;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.IOException;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lv.tsi.todolist.async.UpdateItemTask;
import lv.tsi.todolist.db.ToDoItemRepository;

import static lv.tsi.todolist.MainActivity.DETAILED_INFO;

public class DetailedInfoActivity extends AppCompatActivity{

    public int REQUEST_IMAGE = 42;

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

        if (item.getUri() == null || item.getUri().equals("")) {
            imageView.setImageResource(R.drawable.checkmark_black);
        } else {
            setImage(Uri.parse(item.getUri()));
        }
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
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();

//                final int takeFlags = data.getFlags()
//                                      & (Intent.FLAG_GRANT_READ_URI_PERMISSION
//                                         | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                // Check for the freshest data.
                this.grantUriPermission(this.getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION);
                this.getContentResolver().takePersistableUriPermission(uri, takeFlags);

                setImage(uri);

                item.setUri(uri.toString());
            }
        }
    }

    private void setImage(Uri uri) {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_DOCUMENTS);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.MANAGE_DOCUMENTS);;
        }
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
