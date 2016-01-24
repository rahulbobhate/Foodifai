package net.stupidiot.foodifai;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ViewGroupActivity extends AppCompatActivity {

    ParseUser currentUser;
    List<String> groupNamesList = new ArrayList<>();
    ListView groupNameListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_group);

        groupNameListView = (ListView)findViewById(R.id.groupNameListView);
        currentUser = ParseUser.getCurrentUser();
        final ArrayAdapter<String> adapter = new  ArrayAdapter<String>(ViewGroupActivity.this, android.R.layout.simple_expandable_list_item_1, groupNamesList);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Groups");
        query.whereEqualTo("userName", currentUser.getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject object : objects
                            ) {
//                        Toast.makeText(getApplicationContext(),
//                                object.getString("userName"),
//                                Toast.LENGTH_LONG).show();
                        if (!groupNamesList.contains(object.getString("groupName"))) {
                            groupNamesList.add(object.getString("groupName"));
                        }
                    }
                    adapter.notifyDataSetChanged();


                } else {
                    Toast.makeText(getApplicationContext(),
                            e + "", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
        groupNameListView.setAdapter(adapter);

        groupNameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ViewGroupActivity.this,
                        GroupPointsBoard.class);
                intent.putExtra("groupName", (groupNameListView.getItemAtPosition(position)).toString());
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
