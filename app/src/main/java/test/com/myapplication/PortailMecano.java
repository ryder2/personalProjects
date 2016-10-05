package test.com.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import android.view.View.OnClickListener;
import java.util.ArrayList;
import java.util.List;


public class PortailMecano extends Activity implements OnClickListener {

    Button profilMecano, voirOffres;
    String usernameSaved;

    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    //php login script location:

    //localhost :
    //testing on your device
    //put your local ip instead,  on windows, run CMD > ipconfig
    //or in mac's terminal type ifconfig and look for the ip under en0 or en1
    // private static final String LOGIN_URL = "http://xxx.xxx.x.x:1234/webservice/login.php";

    //testing on Emulator:
    private static final String VALID_MEC_URL = "http://yoann.x10.bz/isValideMecano.php";

    //testing from a real server:
    //private static final String LOGIN_URL = "http://www.yourdomain.com/webservice/login.php";

    //JSON element ids from repsonse of php script:
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.portail_mecano);

        Bundle b = getIntent().getExtras();

        usernameSaved = b.getString("username");

        profilMecano = (Button)findViewById(R.id.profil);
        voirOffres = (Button)findViewById(R.id.voirOffres);

        profilMecano.setOnClickListener(this);
        voirOffres.setOnClickListener(this);
    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.voirOffres:
                new CheckAutorisation().execute();
                break;
            case R.id.profil:
                Intent j = new Intent(this, ProfilMecano.class);
                Bundle b = new Bundle();
                b.putString("username", usernameSaved);
                j.putExtras(b);
                startActivity(j);
                break;

            default:
                break;
        }
    }

    class CheckAutorisation extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */

        String username = usernameSaved;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(PortailMecano.this);
            pDialog.setMessage("Tentative de récupération des offres");
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
                params.add(new BasicNameValuePair("username", username));

                Log.d("request!", "starting");
                // getting product details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(
                        VALID_MEC_URL, "POST", params);

                // check your log for json response
                Log.d("Login attempt", json.toString());

                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {

                    Intent i = new Intent(PortailMecano.this, ReadCommentsMecano.class);
                    Bundle b = new Bundle();
                    b.putString("username", username);
                    i.putExtras(b);
                    finish();
                    startActivity(i);

                    return json.getString(TAG_MESSAGE);
                } else {
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
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (file_url != null) {
                Toast.makeText(PortailMecano.this, file_url, Toast.LENGTH_LONG).show();
            }
        }
    }


}