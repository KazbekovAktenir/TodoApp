package com.example.todoapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TaskDatabaseHelper dbHelper;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new TaskDatabaseHelper(this);
        ListView listView = findViewById(R.id.task_list);

        Cursor cursor = dbHelper.getAllTasks();
        adapter = new SimpleCursorAdapter(this,
                R.layout.task_item,
                cursor,
                new String[]{TaskDatabaseHelper.COLUMN_TITLE, TaskDatabaseHelper.COLUMN_STATUS},
                new int[]{R.id.task_title, R.id.task_status},
                0);
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                dbHelper.deleteTask((int) id);
                refreshList();
                Toast.makeText(MainActivity.this, "Задача удалена", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor taskCursor = (Cursor) adapter.getItem(position);
                int status = taskCursor.getInt(taskCursor.getColumnIndexOrThrow(TaskDatabaseHelper.COLUMN_STATUS));
                dbHelper.updateTaskStatus((int) id, status == 0 ? 1 : 0);
                refreshList();
            }
        });

        findViewById(R.id.add_task_button).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddTaskActivity.class));
        });
    }

    private void refreshList() {
        Cursor cursor = dbHelper.getAllTasks();
        adapter.changeCursor(cursor);
    }
}
