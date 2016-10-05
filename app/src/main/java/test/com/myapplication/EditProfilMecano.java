package test.com.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class EditProfilMecano extends Activity implements View.OnClickListener {

    EditText aProposDeMoi, experience;
    Button valider;
    String usernameSaved;

    private ArrayList<HashMap<String, String>> mCommentList;

    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    JSONObject c;

    //php login script

    //localhost :
    //testing on your device
    //put your local ip instead,  on windows, run CMD > ipconfig
    //or in mac's terminal type ifconfig and look for the ip under en0 or en1
    // private static final String LOGIN_URL = "http://xxx.xxx.x.x:1234/webservice/register.php";

    //testing on Emulator:
    private static final String LOAD_PROFIL_URL = "http://yoann.x10.bz/loadprofilmecano.php";
    private static final String MODIFY_PROFIL_URL = "http://yoann.x10.bz/modifyprofilmecano.php";

    //testing from a real server:
    //private static final String LOGIN_URL = "http://www.yourdomain.com/webservice/register.php";

    //ids
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_POSTS = "posts";
    private static final String TAG_APROPOS = "apropos";
    private static final String TAG_EXPERIENCE = "experience";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit_profil_mecano);

        Bundle b = getIntent().getExtras();

        usernameSaved = b.getString("username");

        aProposDeMoi = (EditText)findViewById(R.id.aProposDeMoi);
        experience = (EditText)findViewById(R.id.monExperience);

        try{
            new LoadMecanoProfil().execute();
        }catch (Exception e){
            e.printStackTrace();
        }


        valider = (Button)findViewById(R.id.valider1);

        valider.setOnClickListener(this);

    }

    public void onClick(View v){
        switch (v.getId()) {
            case R.id.valider1:
                new UpdateProfil().execute();
                break;
        }
    }

    class UpdateProfil extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */


        String aproposdemoi = aProposDeMoi.getText().toString();
        String experience1 = experience.getText().toString();
        String username = usernameSaved;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditProfilMecano.this);
            pDialog.setMessage("Édition du Profil");
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
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("apropos", aproposdemoi));
                params.add(new BasicNameValuePair("experience", experience1));
                params.add(new BasicNameValuePair("username", username));


                Log.d("request!", "starting");

                //Posting user data to script
                JSONObject json = jsonParser.makeHttpRequest(
                        MODIFY_PROFIL_URL, "POST", params);

                // full json response
                Log.d("Login attempt", json.toString());

                // json success element
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("User Created!", json.toString());
                    finish();
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
                Toast.makeText(EditProfilMecano.this, file_url, Toast.LENGTH_LONG).show();
            }

        }
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
            params.add(new BasicNameValuePair("username", usernameSaved));

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
            c = mComments.getJSONObject(0);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class LoadMecanoProfil extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditProfilMecano.this);
            pDialog.setMessage("Téléchargement du profil");
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
                String apropos = c.getString(TAG_APROPOS);
                String experience2 = c.getString(TAG_EXPERIENCE);



                aProposDeMoi.setText(apropos);
                experience.setText(experience2);
            }catch (Exception e){
                e.printStackTrace();
            }

            pDialog.dismiss();
        }
    }
}
