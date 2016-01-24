package net.stupidiot.foodifai;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LeaderBoardActivity extends AppCompatActivity {

    List<String> leaderBoardList = new ArrayList<String>();
    ListView leaderBoardListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        final ArrayAdapter<String> adapter = new  ArrayAdapter<String>(LeaderBoardActivity.this, android.R.layout.simple_expandable_list_item_1, leaderBoardList);
        leaderBoardListView = (ListView)findViewById(R.id.leaderBoardListView);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByDescending("Points");
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {

                Collections.sort(list, new ParseUserComparator());
                int count =1;
                for (ParseUser parseUser : list) {

                    leaderBoardList.add(count++ +") " +"Name: " + parseUser.getString("Name") + "\nPoints: " + parseUser.getNumber("Points"));
                }
                adapter.notifyDataSetChanged();
            }
        });
        leaderBoardListView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_leader_board, menu);
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

class ParseUserComparator implements Comparator<ParseUser>
{
    public int compare(ParseUser u1, ParseUser u2)
    {
        if(u1.getNumber("Points").intValue() > u2.getNumber("Points").intValue())
            return 1;
        else return 0;
    }
 }