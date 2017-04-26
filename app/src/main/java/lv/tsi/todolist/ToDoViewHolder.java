package lv.tsi.todolist;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ToDoViewHolder {
    @BindView(R.id.todoitem) TextView title;
    @BindView(R.id.date) TextView date;
    @BindView(R.id.checkbox) CheckBox checkBox;
    @BindView(R.id.deleteButton) Button deleteButton;

    public ToDoViewHolder(View view) {
        ButterKnife.bind(this, view);
    }

}
