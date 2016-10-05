package test.com.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yoann on 2016-05-27.
 *
 */
public class ViewCommentDetailMecano extends Activity implements View.OnClickListener {

    TextView vehicule,reparationDemande, username, details, ville;
    String postId, postUsername;
    Button valider;

    private ArrayList<HashMap<String, String>> mCommentList;

    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    JSONObject c;

    //testing on Emulator:
    private static final String LOAD_COMMENT_DETAILS_URL = "http://yoann.x10.bz/loadcommentdetailmecano.php";
    private static final String CHECK_MECANO_ALREADY_APPLY_URL = "http://yoann.x10.bz/checkmecanoalreadyapply.php";


    //testing from a real server:
    //private static final String LOGIN_URL = "http://www.yourdomain.com/webservice/register.php";

    //ids
    private static final String TAG_POSTS = "posts";
    private static final String TAG_USERNAME = "username";
    private static final String TAG_VILLE = "ville";
    private static final String TAG_VEHICULE = "vehicule";
    private static final String TAG_TYPEREP = "reparation";
    private static final String TAG_DETAIL = "detail";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_comment_detail_mecano);

        Bundle b = getIntent().getExtras();

        postId = b.getString("id");
        postUsername = b.getString("username");

        username = (TextView) findViewById(R.id.detailDemandeur);
        ville = (TextView) findViewById(R.id.detailVille);
        vehicule = (TextView) findViewById(R.id.detailVehicule);
        reparationDemande = (TextView) findViewById(R.id.detailReparationDemande);
        details = (TextView) findViewById(R.id.detailDetails);

        valider = (Button)findViewById(R.id.appliquer);
        valider.setOnClickListener(this);

        try{
            new LoadCommentDetails().execute();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void onClick(View v){
        switch (v.getId()) {
            case R.id.appliquer:
                new AlreadyApply().execute();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(ViewCommentDetailMecano.this, ReadCommentsMecano.class);
        Bundle b = new Bundle();
        b.putString("username", postUsername);
        i.putExtras(b);
        startActivity(i);
    }

    public void updateJSONdata() {

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
            params.add(new BasicNameValuePair("id", postId));

            // getting product details by making HTTP request
            // Note that product details url will use GET request
            JSONObject json = jsonParser.makeHttpRequest(
                    LOAD_COMMENT_DETAILS_URL, "GET", params);

            // I know I said we would check if "Posts were Avail." (success==1)
            // before we tried to read the individual posts, but I lied...
            // mComments will tell us how many "posts" or comments are
            // available
            mComments = json.getJSONArray(TAG_POSTS);


            // looping through all posts according to the json object returned
            c = mComments.getJSONObject(0);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public class LoadCommentDetails extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ViewCommentDetailMecano.this);
            pDialog.setMessage("Téléchargement des détails");
            pDialog.setIndeterminate(true);
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

            try{
                // gets the content of each tag
                String username1 = c.getString(TAG_USERNAME);
                String vehicule1 = c.getString(TAG_VEHICULE);
                String details1 = c.getString(TAG_DETAIL);
                String reparationDemande1 = c.getString(TAG_TYPEREP);
                String ville1 = c.getString(TAG_VILLE);

                username.setText(username1);
                vehicule.setText(vehicule1);
                details.setText(details1);
                reparationDemande.setText(reparationDemande1);
                ville.setText(ville1);

            }catch (Exception e){
                e.printStackTrace();
            }

            pDialog.dismiss();
        }
    }


    class AlreadyApply extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        String postUsername = ViewCommentDetailMecano.this.postUsername;
        String test = ViewCommentDetailMecano.this.postId;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ViewCommentDetailMecano.this);
            pDialog.setMessage("Application");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }



        @Override
        protected String doInBackground(String... args) {

            // Check for success tag
            int success;

            try {

                // Building Parameters
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("username", postUsername));
                params.add(new BasicNameValuePair("id", test));

                Log.d("request!", "starting");

                //Posting user data to script
                JSONObject json = jsonParser.makeHttpRequest(
                        CHECK_MECANO_ALREADY_APPLY_URL, "POST", params);

                // full json response
                Log.d("Login attempt", json.toString());

                // json success element
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("User Created!", json.toString());
                    Intent i = new Intent(ViewCommentDetailMecano.this, ApplicationDetailsMecano.class);
                    Bundle b = new Bundle();
                    b.putString("id", postId);
                    b.putString("username", postUsername);
                    i.putExtras(b);
                    startActivity(i);
                    return json.getString(TAG_MESSAGE);
                }else{
                    Log.d("Login Failure!", json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (file_url != null){
                Toast.makeText(ViewCommentDetailMecano.this, file_url, Toast.LENGTH_LONG).show();
            }

        }

    }
}
