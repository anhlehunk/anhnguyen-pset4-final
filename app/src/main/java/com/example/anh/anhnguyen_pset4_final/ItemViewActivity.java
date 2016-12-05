package com.example.anh.anhnguyen_pset4_final;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ItemViewActivity extends AppCompatActivity {

    final String[] from = new String[] {TodoManager.CHECKER, TodoManager.TASK};
    final int[] to = new int[] {R.id.checkItem, R.id.todoItem};
    private Cursor taskCursor;
    private Bundle extrasBundle;
    private String task;
    private Long parent_id;
    private SimpleCursorAdapter cursorAdapter;
    private ListView itemList;
    private TodoManager db;
    int[] checkBoxes = {R.drawable.unchecked,
            R.drawable.checked,};



    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        extrasBundle = getIntent().getExtras();
        parent_id = extrasBundle.getLong("item");
        db = TodoManager.getInstance(this);

        placeItem(parent_id);
    }

    public void addNewItem(View view){
        EditText addedItem = (EditText) findViewById(R.id.itemEdit);
        task = addedItem.getText().toString();
        addedItem.setText("");

        //blank editText shows toast
        if(task.length() == 0){
            Toast blank = Toast.makeText(ItemViewActivity.this,   "Insert task", Toast.LENGTH_SHORT);
            blank.show();
        }

        else {
            db.makeItem(checkBoxes[0], task, parent_id);
            taskCursor.requery();
            cursorAdapter.notifyDataSetChanged();

            Toast added = Toast.makeText(ItemViewActivity.this, task +" is added", Toast.LENGTH_SHORT);
            added.show();
        }
    }

    private void placeItem(long parent_id){
        // get cursor and adapter
        taskCursor = db.getItems(parent_id);
        cursorAdapter = new SimpleCursorAdapter(this, R.layout.item_layout, taskCursor, from, to, 0);

        itemList = (ListView) findViewById(R.id.itemList);
        itemList.setAdapter(cursorAdapter);
        itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor selectedItem = db.getItemRow(l);

                if (selectedItem.moveToFirst()) {
                    int checkBox = selectedItem.getInt(1);
                    String selectedTask = selectedItem.getString(2);

                    // done or not
                    if (checkBox == checkBoxes[0]) {
                        checkBox = checkBoxes[1];
                        Toast done = Toast.makeText(ItemViewActivity.this, "Task done", Toast.LENGTH_SHORT);
                        done.show();
                    }
                    else {
                        checkBox = checkBoxes[0];
                    }
                    // update the checkbox
                    db.update(l, checkBox, selectedTask);
                }
                selectedItem.close();

                taskCursor.requery();
                cursorAdapter.notifyDataSetChanged();
            }
        });

        //longclick to delete tasks
        itemList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                // delete in database
                db.item_delete(l);
                taskCursor.requery();
                cursorAdapter.notifyDataSetChanged();

                // toast
                Toast deleted = Toast.makeText(ItemViewActivity.this, "Task is deleted", Toast.LENGTH_SHORT);
                deleted.show();
                return true;
            }
        });
    }

}
