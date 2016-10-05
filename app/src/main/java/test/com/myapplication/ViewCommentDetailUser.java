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
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ViewCommentDetailUser extends ListActivity {

    // Progress Dialog
    private ProgressDialog pDialog;

    String post_username;
    String post_id;

    private TextView usernameMecano;

    List<String> postIdList = new ArrayList<>();

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
    private static final String READ_COMMENTS_URL = "http://yoann.x10.bz/loadcommentdetailuser.php";

    // testing from a real server:
    // private static final String READ_COMMENTS_URL =
    // "http://www.mybringback.com/webservice/comments.php";

    // JSON IDS:
    private static final String TAG_POSTS = "posts";
    private static final String TAG_FOURNI_PIECE = "fournipiece";
    private static final String TAG_DEPLACE = "deplace";
    private static final String TAG_USERNAME = "username";
    private static final String TAG_MONTANT = "montant";
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

        setContentView(R.layout.view_comment_detail_user);

        Bundle b = getIntent().getExtras();

        post_username = b.getString("username");
        post_id = b.getString("id");

    }



    @Override
    protected void onResume() {

        super.onResume();
        // loading the comments via AsyncTask
        new LoadUserCommentOfferDetails().execute();
    }

    /**
     * Retrieves recent post data from the server.
     */
    public void updateJSONdata() {

        // Instantiate the arraylist to contain all the JSON data.
        // we are going to use a bunch of key-value pairs, referring
        // to the json element name, and the content, for example,
        // message it the tag, and "I'm awesome" as the content..

        mCommentList = new ArrayList<>();


        JSONArray mComments;

        try{
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair(TAG_POSTID, post_id));

            // getting product details by making HTTP request
            // Note that product details url will use GET request
            JSONObject json = jsonParser.makeHttpRequest(
                    READ_COMMENTS_URL, "GET", params);

            // Feed the beast our comments url, and it spits us
            // back a JSON object. Boo-yeah Jerome.

            // when parsing JSON stuff, we should probably
            // try to catch any exceptions:
            try {

                // I know I said we would check if "Posts were Avail." (success==1)
                // before we tried to read the individual posts, but I lied...
                // mComments will tell us how many "posts" or comments are
                // available
                mComments = json.getJSONArray(TAG_POSTS);

                // looping through all posts according to the json object returned
                for (int i = 0; i < mComments.length(); i++) {
                    JSONObject c = mComments.getJSONObject(i);

                    // gets the content of each tag
                    String deplace;
                    String fourniPiece;

                    if(c.getString(TAG_DEPLACE).equals("1")){
                        deplace = "Oui";
                    } else {
                        deplace = "Non";
                    }

                    if(c.getString(TAG_FOURNI_PIECE).equals("1")){
                        fourniPiece = "Oui";
                    } else {
                        fourniPiece = "Non";
                    }
                    String username = "Nom du mécanicien : " + c.getString(TAG_USERNAME);
                    String montant =  c.getString(TAG_MONTANT) + "$";



                    // creating new HashMap
                    HashMap<String, String> map = new HashMap<>();

                    map.put(TAG_USERNAME, username);
                    map.put(TAG_FOURNI_PIECE, fourniPiece);
                    map.put(TAG_DEPLACE, deplace);
                    map.put(TAG_MONTANT, montant);

                    // adding HashList to ArrayList
                    mCommentList.add(map);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }catch (Exception e){
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
                R.layout.single_post_user_offer, new String[] { TAG_USERNAME, TAG_FOURNI_PIECE,
                TAG_DEPLACE, TAG_MONTANT }, new int[] { R.id.usernameMecano, R.id.pieceFournie,
                R.id.seDeplace, R.id.montant });

        // I shouldn't have to comment on this one:
        setListAdapter(adapter);

        // Optional: when the user clicks a list item we
        //could do something.  However, we will choose
        //to do nothing...

    }

    public class LoadUserCommentOfferDetails extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ViewCommentDetailUser.this);
            pDialog.setMessage("Loading offers...");
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
    public void myClickHandler(View v)
    {
        //get the row the clicked button is in
        LinearLayout vwParentRow = (LinearLayout)v.getParent();

        TextView child = (TextView)vwParentRow.getChildAt(0);
        Intent j = new Intent(this, UserViewProfilMecano.class);
        Bundle b = new Bundle();
        b.putString("username", child.getText().toString());
        j.putExtras(b);
        startActivity(j);

    }
}