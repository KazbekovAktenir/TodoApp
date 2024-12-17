package com.example.todoapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddTaskActivity extends AppCompatActivity {

    private TaskDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        dbHelper = new TaskDatabaseHelper(this);

        findViewById(R.id.save_task_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText titleInput = findViewById(R.id.task_title_input);
                EditText descriptionInput = findViewById(R.id.task_description_input);

                String title = titleInput.getText().toString().trim();
                String description = descriptionInput.getText().toString().trim();

                if (!title.isEmpty()) {
                    dbHelper.addTask(title, description);
                    Toast.makeText(AddTaskActivity.this, "Задача добавлена", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddTaskActivity.this, "Введите заголовок", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
