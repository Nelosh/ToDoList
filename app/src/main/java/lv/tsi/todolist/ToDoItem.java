package lv.tsi.todolist;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ToDoItem implements Serializable {
    private Long id;
    private String title;
    private Date date;
    private boolean checked;
    private String details = "";

    public ToDoItem(String title, Date date, boolean checked) {
        this.title = title;
        this.date = date;
        this.checked = checked;
    }

    public ToDoItem(Long id, String title, String details, String date, boolean checked) {
        this.id = id;
        this.title = title;
        this.details = details;
        setFormattedDate(date);
        this.checked = checked;
    }

    public ToDoItem(String title) {
        this.title = title;
        this.date = new Date();
        this.checked = false;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getFormattedDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        return dateFormat.format(date);
    }

    public void setFormattedDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        try {
            this.date = dateFormat.parse(date);
        } catch (ParseException e) {
            this.date = new Date();
        }
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ToDoItem toDoItem = (ToDoItem) o;

        if (checked != toDoItem.checked) return false;
        if (title != null ? !title.equals(toDoItem.title) : toDoItem.title != null) return false;
        return date != null ? date.equals(toDoItem.date) : toDoItem.date == null;

    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (checked ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ToDoItem{" +
                "title='" + title + '\'' +
                ", date=" + date +
                ", checked=" + checked +
                '}';
    }
}
