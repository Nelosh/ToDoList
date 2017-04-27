package lv.tsi.todolist;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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

    private static final int PERMISSIONS_REQUEST_READ_PICTURE = 84;
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
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            requestImage();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_PICTURE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_PICTURE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestImage();
        }
    }

    private void requestImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                setImage(uri);
                item.setUri(uri.toString());
            }
        }

    }

    private void setImage(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
