package test.com.myapplication;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends FragmentActivity implements OnClickListener {

    private EditText user, pass, prenom1, nom1, rue1, codepostal1, ville1, province1, pays1, email1, retypepass1;

    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    //php login script

    //localhost :
    //testing on your device
    //put your local ip instead,  on windows, run CMD > ipconfig
    //or in mac's terminal type ifconfig and look for the ip under en0 or en1
    // private static final String LOGIN_URL = "http://xxx.xxx.x.x:1234/webservice/register.php";

    //testing on Emulator:
    private static final String LOGIN_URL = "http://yoann.x10.bz/register.php";

    //testing from a real server:
    //private static final String LOGIN_URL = "http://www.yourdomain.com/webservice/register.php";

    //ids
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Button  mRegister, rechercheVille;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        user = (EditText)findViewById(R.id.username);
        pass = (EditText)findViewById(R.id.password);
        retypepass1 = (EditText)findViewById(R.id.passwordconfirmation);
        prenom1 = (EditText)findViewById(R.id.prenom);
        nom1 = (EditText)findViewById(R.id.nom);
        rue1 = (EditText)findViewById(R.id.rue);
        codepostal1 = (EditText)findViewById(R.id.codepostal);
        ville1 = (EditText)findViewById(R.id.ville);
        province1 = (EditText)findViewById(R.id.province);
        pays1 = (EditText)findViewById(R.id.pays);
        email1 = (EditText)findViewById(R.id.email);

        mRegister = (Button)findViewById(R.id.register);
        rechercheVille = (Button)findViewById(R.id.rechercheville);

        mRegister.setOnClickListener(this);
        rechercheVille.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.rechercheville:
                if (ContextCompat.checkSelfPermission(Register.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(Register.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_FINE_LOCATION);
                }else{
                    GPSTrack();
                }

                break;

            case R.id.register:
                if(pass.getText().toString().equals(retypepass1.getText().toString()) &&
                        !prenom1.getText().toString().isEmpty() &&
                        !nom1.getText().toString().isEmpty() &&
                        !rue1.getText().toString().isEmpty() &&
                        !codepostal1.getText().toString().isEmpty() &&
                        !ville1.getText().toString().isEmpty() &&
                        !province1.getText().toString().isEmpty() &&
                        !pays1.getText().toString().isEmpty() &&
                        !email1.getText().toString().isEmpty() &&
                        isValidEmail(email1.getText().toString()) &&
                        pass.getText().length() > 4 &&
                        !pass.getText().toString().isEmpty() &&
                        !user.getText().toString().isEmpty()){

                    new CreateUser().execute();
                }else if(!pass.getText().toString().equals(retypepass1.getText().toString()) || pass.getText().length() < 5) {
                    Toast.makeText(Register.this, "Mot de passe non valide", Toast.LENGTH_SHORT).show();
                }else if(!isValidEmail(email1.getText().toString())) {
                    Toast.makeText(Register.this, "Entrez une adresse e-mail valide", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Register.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    GPSTrack();
                } else {
                    Toast.makeText(Register.this, "Vous devez autoriser l'utilisation du GPS", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void GPSTrack(){
        GPSTracker gpsTracker = new GPSTracker(Register.this);
        if (gpsTracker.canGetLocation()) {
            rue1.setText(gpsTracker.getAddressLine(Register.this));
            codepostal1.setText(gpsTracker.getPostalCode(Register.this));
            ville1.setText(gpsTracker.getLocality(Register.this));
            province1.setText(gpsTracker.getProvince(Register.this));
            pays1.setText(gpsTracker.getCountryName(Register.this));
        }

    }


    public static boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }



    class CreateUser extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */


        String salt = "Random$SaltValue#WithSpecialCharacters12@$@4&#%^$*";

        String username = user.getText().toString();
        String password = Md5.md5(pass.getText().toString() + salt);
        String prenom = prenom1.getText().toString();
        String nom = nom1.getText().toString();
        String rue = rue1.getText().toString();
        String codepostal = codepostal1.getText().toString();
        String ville = ville1.getText().toString();
        String province = province1.getText().toString();
        String pays = pays1.getText().toString();
        String email = email1.getText().toString();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Register.this);
            pDialog.setMessage("Création de l'utilisateur");
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
                params.add(new BasicNameValuePair("password", password));
                params.add(new BasicNameValuePair("prenom", prenom));
                params.add(new BasicNameValuePair("nom", nom));
                params.add(new BasicNameValuePair("rue", rue));
                params.add(new BasicNameValuePair("codepostal", codepostal));
                params.add(new BasicNameValuePair("ville", ville));
                params.add(new BasicNameValuePair("province", province));
                params.add(new BasicNameValuePair("pays", pays));
                params.add(new BasicNameValuePair("email", email));

                Log.d("request!", "starting");

                //Posting user data to script
                JSONObject json = jsonParser.makeHttpRequest(
                        LOGIN_URL, "POST", params);

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
                Toast.makeText(Register.this, file_url, Toast.LENGTH_LONG).show();
            }

        }

    }

}