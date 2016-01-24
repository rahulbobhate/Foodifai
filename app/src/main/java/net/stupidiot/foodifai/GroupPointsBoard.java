package net.stupidiot.foodifai;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GroupPointsBoard extends AppCompatActivity {

    List<String> groupLeaderBoardList = new ArrayList<String>();
    ListView groupLeaderBoardListView;
    TextView groupNameTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_points_board);
        groupNameTextView = (TextView)findViewById(R.id.groupTitle);
        groupNameTextView.setText(getIntent().getStringExtra("groupName"));
        final ArrayAdapter<String> adapter = new  ArrayAdapter<String>(GroupPointsBoard.this, android.R.layout.simple_expandable_list_item_1, groupLeaderBoardList);
        groupLeaderBoardListView = (ListView)findViewById(R.id.groupLeaderBoardListView);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Groups");
        query.whereEqualTo("groupName", getIntent().getStringExtra("groupName"));
        query.orderByDescending("Points");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {

                Collections.sort(list, new ParseGroupUserComparator());
                int count = 1;
                for (ParseObject parseObject : list) {
                    groupLeaderBoardList.add(count++ +") " +"Name: " + parseObject.getString("memberName") + "\nPoints: " + parseObject.getNumber("Points"));
                }
                adapter.notifyDataSetChanged();
            }
        });
        groupLeaderBoardListView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group_points_board, menu);
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

class ParseGroupUserComparator implements Comparator<ParseObject>
{
    public int compare(ParseObject u1, ParseObject u2)
    {
        if(u1.getNumber("Points").intValue() > u2.getNumber("Points").intValue())
            return 1;
        else return 0;
    }


}