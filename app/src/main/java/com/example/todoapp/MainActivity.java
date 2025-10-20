package com.example.todoapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TaskDatabaseHelper dbHelper;
    private SimpleCursorAdapter adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new TaskDatabaseHelper(this);
        listView = findViewById(R.id.task_list);

        loadTasks();

        //удаление по долгому нажатию
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            try {
                dbHelper.deleteTask((int) id);
                refreshList();
                Toast.makeText(MainActivity.this, "Задача удалена", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Ошибка при удалении: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        //изменение статуса задачи
        listView.setOnItemClickListener((parent, view, position, id) -> {
            try {
                Cursor taskCursor = (Cursor) adapter.getItem(position);
                int status = taskCursor.getInt(taskCursor.getColumnIndexOrThrow(TaskDatabaseHelper.COLUMN_STATUS));
                dbHelper.updateTaskStatus((int) id, status == 0 ? 1 : 0);
                refreshList();
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Ошибка при обновлении статуса", Toast.LENGTH_SHORT).show();
            }
        });

        //переход на экран добавления задачи
        findViewById(R.id.add_task_button).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddTaskActivity.class))
        );
    }

    private void loadTasks() {
        try {
            Cursor cursor = dbHelper.getAllTasks();
            adapter = new SimpleCursorAdapter(
                    this,
                    R.layout.task_item,
                    cursor,
                    new String[]{TaskDatabaseHelper.COLUMN_TITLE},
                    new int[]{R.id.task_title},
                    0
            );

            //отображение выполненных задач зачёркнутыми и чекбоксами
            adapter.setViewBinder((view, cursor1, columnIndex) -> {
                int status = cursor1.getInt(cursor1.getColumnIndexOrThrow(TaskDatabaseHelper.COLUMN_STATUS)); // ✅ вынесли сюда

                if (view.getId() == R.id.task_title) {
                    TextView titleView = (TextView) view;
                    titleView.setText(cursor1.getString(columnIndex));

                    if (status == 1) {
                        titleView.setPaintFlags(titleView.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                        titleView.setTextColor(getResources().getColor(android.R.color.darker_gray));
                    } else {
                        titleView.setPaintFlags(titleView.getPaintFlags() & (~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG));
                        titleView.setTextColor(getResources().getColor(android.R.color.black));
                    }
                    return true;
                }

                if (view.getId() == R.id.task_status) {
                    android.widget.CheckBox checkBox = (android.widget.CheckBox) view;
                    checkBox.setChecked(status == 1);
                    return true;
                }

                return false;
            });

            listView.setAdapter(adapter);
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка при загрузке задач", Toast.LENGTH_SHORT).show();
        }
    }


    private void refreshList() {
        Cursor newCursor = dbHelper.getAllTasks();
        adapter.changeCursor(newCursor);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }
}
