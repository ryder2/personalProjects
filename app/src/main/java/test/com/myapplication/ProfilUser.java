package test.com.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ProfilUser extends Activity {

    TextView prenom, nom, email, addresse, ville, province, pays;
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
    private static final String LOAD_PROFIL_URL = "http://yoann.x10.bz/loadprofiluser.php";

    //testing from a real server:
    //private static final String LOGIN_URL = "http://www.yourdomain.com/webservice/register.php";

    //ids
    private static final String TAG_POSTS = "posts";
    private static final String TAG_PRENOM = "prenom";
    private static final String TAG_NOM = "nom";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_ADDRESSE = "rue";
    private static final String TAG_VILLE = "ville";
    private static final String TAG_PROVINCE = "province";
    private static final String TAG_PAYS = "pays";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.profil_user);

        Bundle c = getIntent().getExtras();

        usernameSaved = c.getString("username");

        prenom = (TextView) findViewById(R.id.profilPrenom);
        nom = (TextView) findViewById(R.id.profilNom);
        email = (TextView) findViewById(R.id.profilEmail);
        addresse = (TextView) findViewById(R.id.profilAddresse);
        ville = (TextView) findViewById(R.id.profilVille);
        province = (TextView) findViewById(R.id.profilProfince);
        pays = (TextView) findViewById(R.id.profilPays);

        try{
            new LoadUserProfil().execute();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    @Override
    public void onBackPressed() {
        Intent i = new Intent(ProfilUser.this, PortailUser.class);
        Bundle b = new Bundle();
        b.putString("username", usernameSaved);
        i.putExtras(b);
        finish();
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

    public class LoadUserProfil extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ProfilUser.this);
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
                String prenom1 = c.getString(TAG_PRENOM);
                String nom1 = c.getString(TAG_NOM);
                String email1 = c.getString(TAG_EMAIL);
                String addresse1 = c.getString(TAG_ADDRESSE);
                String ville1 = c.getString(TAG_VILLE);
                String province1 = c.getString(TAG_PROVINCE);
                String pays1 = c.getString(TAG_PAYS);


                prenom.setText(prenom1);
                nom.setText(nom1);
                email.setText(email1);
                addresse.setText(addresse1);
                ville.setText(ville1);
                province.setText(province1);
                pays.setText(pays1);

            } catch (Exception e){
                e.printStackTrace();
            }

            pDialog.dismiss();
        }
    }
}
