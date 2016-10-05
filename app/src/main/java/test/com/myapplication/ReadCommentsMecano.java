package test.com.myapplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ReadCommentsMecano extends ListActivity {

    // Progress Dialog
    private ProgressDialog pDialog;

    ListView lv;

    List<String> postIdList = new ArrayList<>();

    String mecanoVille, post_username;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    // php read comments script

    // localhost :
    // testing on your device
    // put your local ip instead, on windows, run CMD > ipconfig
    // or in mac's terminal type ifconfig and look for the ip under en0 or en1
    // private static final String READ_COMMENTS_URL =
    // "http://xxx.xxx.x.x:1234/webservice/comments.php";

    // testing on Emulator:
    private static final String READ_COMMENTS_URL = "http://yoann.x10.bz/commentsMecano.php";
    private static final String LOAD_PROFIL_URL = "http://yoann.x10.bz/getmecanocountry.php";

    // testing from a real server:
    // private static final String READ_COMMENTS_URL =
    // "http://www.mybringback.com/webservice/comments.php";

    // JSON IDS:
    private static final String TAG_TITLE = "title";
    private static final String TAG_POSTS = "posts";
    private static final String TAG_USERNAME = "username";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_VILLE = "ville";
    private static final String TAG_POSTID = "id";
    // it's important to note that the message is both in the parent branch of
    // our JSON tree that displays a "Post Available" or a "No Post Available"
    // message,
    // and there is also a message for each individual post, listed under the
    // "posts"
    // category, that displays what the user typed as their message.

    // manages all of our comments in a list.
    private ArrayList<HashMap<String, String>> mCommentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.read_comments);

        Bundle b = getIntent().getExtras();

        post_username = b.getString("username");
        new LoadMecanoCountryProfil().execute();

    }


    @Override
    protected void onResume() {

        super.onResume();
        // loading the comments via AsyncTask
        new LoadComments().execute();
    }

    /**
     * Retrieves recent post data from the server.
     */
    public void updateJSONdata() {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("ville", mecanoVille));

        // getting product details by making HTTP request
        // Note that product details url will use GET request
        JSONObject a = jsonParser.makeHttpRequest(
                READ_COMMENTS_URL, "GET", params);

        // Instantiate the arraylist to contain all the JSON data.
        // we are going to use a bunch of key-value pairs, referring
        // to the json element name, and the content, for example,
        // message it the tag, and "I'm awesome" as the content..

        mCommentList = new ArrayList<>();
        if(!postIdList.isEmpty()){
            postIdList.clear();
        }


        JSONArray mComments;

        try {

            // I know I said we would check if "Posts were Avail." (success==1)
            // before we tried to read the individual posts, but I lied...
            // mComments will tell us how many "posts" or comments are
            // available
            mComments = a.getJSONArray(TAG_POSTS);

            // looping through all posts according to the json object returned
            for (int i = 0; i < mComments.length(); i++) {
                JSONObject c = mComments.getJSONObject(i);

                // gets the content of each tag
                String title = c.getString(TAG_TITLE);
                String content = c.getString(TAG_MESSAGE);
                String username = c.getString(TAG_USERNAME);
                String postId =  c.getString(TAG_POSTID);

                // creating new HashMap
                HashMap<String, String> map = new HashMap<>();

                map.put(TAG_TITLE, title);
                map.put(TAG_MESSAGE, content);
                map.put(TAG_USERNAME, username);

                // adding HashList to ArrayList
                mCommentList.add(map);

                postIdList.add(postId);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inserts the parsed data into the listview.
     */
    private void updateList() {
        // For a ListActivity we need to set the List Adapter, and in order to do
        //that, we need to create a ListAdapter.  This SimpleAdapter,
        //will utilize our updated Hashmapped ArrayList,
        //use our single_post xml template for each item in our list,
        //and place the appropriate info from the list to the
        //correct GUI id.  Order is important here.
        ListAdapter adapter = new SimpleAdapter(this, mCommentList,
                R.layout.single_post_mecano_offer, new String[] { TAG_TITLE, TAG_MESSAGE,
                TAG_USERNAME }, new int[] { R.id.title, R.id.message,
                R.id.username });

        // I shouldn't have to comment on this one:
        setListAdapter(adapter);


        lv = getListView();
        lv.setClickable(true);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                Intent i = new Intent(ReadCommentsMecano.this, ViewCommentDetailMecano.class);
                Bundle b = new Bundle();
                b.putString("id", postIdList.get(position));
                b.putString("username", post_username);
                i.putExtras(b);
                startActivity(i);
            }
        });
    }

    public class LoadComments extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ReadCommentsMecano.this);
            pDialog.setMessage("Loading Comments...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {
            updateJSONdata();
            return null;

        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            pDialog.dismiss();
            updateList();
        }
    }

    public void updateJSONdataOne() {

        // Instantiate the arraylist to contain all the JSON data.
        // we are going to use a bunch of key-value pairs, referring
        // to the json element name, and the content, for example,
        // message it the tag, and "I'm awesome" as the content..

        mCommentList = new ArrayList<>();
        JSONArray mComments;


        // when parsing JSON stuff, we should probably
        // try to catch any exceptions:
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", post_username));

            // getting product details by making HTTP request
            // Note that product details url will use GET request
            JSONObject json = jsonParser.makeHttpRequest(
                    LOAD_PROFIL_URL, "GET", params);

            // I know I said we would check if "Posts were Avail." (success==1)
            // before we tried to read the individual posts, but I lied...
            // mComments will tell us how many "posts" or comments are
            // available
            mComments = json.getJSONArray(TAG_POSTS);


            // looping through all posts according to the json object returned
            JSONObject c = mComments.getJSONObject(0);
            try{
                // gets the content of each tag
                mecanoVille = c.getString(TAG_VILLE);

            }catch (Exception e){
                e.printStackTrace();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class LoadMecanoCountryProfil extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... arg0) {
            updateJSONdataOne();
            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
        }
    }
}