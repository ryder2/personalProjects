package test.com.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yoann on 2016-05-29.
 *
 */
public class ApplicationDetailsMecano extends Activity implements View.OnClickListener {

    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    JSONObject c;

    // Progress Dialog
    private ProgressDialog pDialog;

    CheckBox jeMeDeplace, jeFourniePiece;
    Button appliquer;
    EditText montantDemander, detailsApplication;

    String mecanoSeDeplace, mecanoFourniPiece, username, commentsId;

    private static final String MECANO_APPLICATION_URL = "http://yoann.x10.bz/applicationmecano.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.application_mecano);

        Bundle b = getIntent().getExtras();

        commentsId = b.getString("id");
        username = b.getString("username");


        jeMeDeplace = (CheckBox) findViewById(R.id.deplacementMec);
        jeFourniePiece = (CheckBox) findViewById(R.id.pieceFournieMec);
        appliquer = (Button) findViewById(R.id.applicationMecano);
        montantDemander = (EditText) findViewById(R.id.prixDemandeMec);
        detailsApplication = (EditText) findViewById(R.id.informationSuppMecano);

        appliquer.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.applicationMecano:
                if(jeMeDeplace.isChecked()){
                    mecanoSeDeplace = "1";
                }else{
                    mecanoSeDeplace = "0";
                }
                if(jeFourniePiece.isChecked()){
                    mecanoFourniPiece = "1";
                }else{
                    mecanoFourniPiece = "0";
                }
                if(montantDemander.getText().length() == 0){
                    Toast.makeText(ApplicationDetailsMecano.this, "Veuillez entrer un montant", Toast.LENGTH_LONG).show();
                }else{
                    new SendApplication().execute();
                }

                break;
        }

    }

    class SendApplication extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */


        String detailsDeLapplication = detailsApplication.getText().toString();
        String montantDemanderParMec = montantDemander.getText().toString();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ApplicationDetailsMecano.this);
            pDialog.setMessage("Applicationl");
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
                params.add(new BasicNameValuePair("montant", montantDemanderParMec));
                params.add(new BasicNameValuePair("fournipiece", mecanoFourniPiece));
                params.add(new BasicNameValuePair("deplace", mecanoSeDeplace));
                params.add(new BasicNameValuePair("details", detailsDeLapplication));
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("commentsid", commentsId));


                Log.d("request!", "starting");

                //Posting user data to script
                JSONObject json = jsonParser.makeHttpRequest(
                        MECANO_APPLICATION_URL, "POST", params);

                // full json response
                Log.d("Sending information", json.toString());

                // json success element
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("Information sent!", json.toString());
                    finish();
                    return json.getString(TAG_MESSAGE);
                }else{
                    Log.d("Sent Failure!", json.getString(TAG_MESSAGE));
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
                Toast.makeText(ApplicationDetailsMecano.this, file_url, Toast.LENGTH_LONG).show();
            }

        }
    }
}
