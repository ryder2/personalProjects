package test.com.myapplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddComment extends Activity implements OnClickListener{

    private EditText message;
    private Spinner spinnerYear, spinnerBrand, spinnerModel, spinnerJob;


    String userVille;
    String year;
    String brand;

    private ArrayList<HashMap<String, String>> mCommentList;

    // Progress Dialog
    private ProgressDialog pDialog;

    private static final String TAG_POSTS = "posts";
    private static final String TAG_VILLE = "ville";

    //Retrieving Saved Username Data:
    SharedPreferences sp;
    String post_username;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    //php login script

    //localhost :
    //testing on your device
    //put your local ip instead,  on windows, run CMD > ipconfig
    //or in mac's terminal type ifconfig and look for the ip under en0 or en1
    // private static final String POST_COMMENT_URL = "http://xxx.xxx.x.x:1234/webservice/addcomment.php";

    //testing on Emulator:
    private static final String POST_COMMENT_URL = "http://yoann.x10.bz/addcomment.php";
    private static final String LOAD_PROFIL_URL = "http://yoann.x10.bz/getusercountry.php";
    private static final String LOAD_YEAR_URL = "http://yoann.x10.bz/getcaryear.php";
    private static final String LOAD_BRAND_URL = "http://yoann.x10.bz/getcarbrand.php";
    private static final String LOAD_MODEL_URL = "http://yoann.x10.bz/getcarmodel.php";
    private static final String LOAD_JOB_URL = "http://yoann.x10.bz/getjob.php";

    //testing from a real server:
    //private static final String POST_COMMENT_URL = "http://www.mybringback.com/webservice/addcomment.php";

    //ids
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_YEAR = "year";
    private static final String TAG_BRAND = "brand";
    private static final String TAG_MODEL = "model";
    private static final String TAG_JOB = "job";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Button  mSubmit;
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_comment);

        sp = PreferenceManager.getDefaultSharedPreferences(AddComment.this);
        post_username = sp.getString("username", "anon");


        new LoadYears().execute();

        message = (EditText)findViewById(R.id.message);

        mSubmit = (Button)findViewById(R.id.submit);
        mSubmit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        new LoadUserCountryProfil().execute();
        new PostComment().execute();
    }


    class PostComment extends AsyncTask<String, String, String> {

        String post_title = spinnerJob.getSelectedItem().toString();
        String post_message = message.getText().toString();
        String carType = spinnerYear.getSelectedItem().toString() + " " +
                spinnerBrand.getSelectedItem().toString() + " " +
                spinnerModel.getSelectedItem().toString();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddComment.this);
            pDialog.setMessage("Posting Comment...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;

            try {

                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", post_username));
                params.add(new BasicNameValuePair("title", post_title));
                params.add(new BasicNameValuePair("message", post_message));
                params.add(new BasicNameValuePair("ville", userVille));
                params.add(new BasicNameValuePair("car", carType));

                Log.d("request!", "starting");

                //Posting user data to script
                JSONObject json = jsonParser.makeHttpRequest(
                        POST_COMMENT_URL, "POST", params);

                // full json response
                Log.d("Post Comment attempt", json.toString());

                // json success element
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("Comment Added!", json.toString());
                    finish();
                    return json.getString(TAG_MESSAGE);
                }else{
                    Log.d("Comment Failure!", json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (file_url != null){
                Toast.makeText(AddComment.this, file_url, Toast.LENGTH_LONG).show();
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
                userVille = c.getString(TAG_VILLE);
            }catch (Exception e){
                e.printStackTrace();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class LoadUserCountryProfil extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... arg0) {
            updateJSONdata();
            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
        }
    }

    public void updateYears() {

        String years = "0";

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("year", years));

        // getting product details by making HTTP request
        // Note that product details url will use GET request
        JSONObject json = jsonParser.makeHttpRequest(
                LOAD_YEAR_URL, "GET", params);

        // Instantiate the arraylist to contain all the JSON data.
        // we are going to use a bunch of key-value pairs, referring
        // to the json element name, and the content, for example,
        // message it the tag, and "I'm awesome" as the content..

        mCommentList = new ArrayList<>();
        JSONArray mComments;

        // Bro, it's time to power up the J parser
        JSONParser jParser = new JSONParser();
        // Feed the beast our comments url, and it spits us
        // back a JSON object. Boo-yeah Jerome.
        JSONObject a = jParser.getJSONFromUrl(LOAD_YEAR_URL);

        // when parsing JSON stuff, we should probably
        // try to catch any exceptions:
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
                String year = c.getString(TAG_YEAR);

                // creating new HashMap
                HashMap<String, String> map = new HashMap<>();

                map.put(TAG_YEAR, year);

                // adding HashList to ArrayList
                mCommentList.add(map);

                // annndddd, our JSON data is up to date same with our array
                // list
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class LoadYears extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddComment.this);
            pDialog.setMessage("Loading Years...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {
            updateYears();
            return null;

        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            pDialog.dismiss();
            updateYearSpinner();
        }
    }

    private void updateYearSpinner() {

        spinnerYear = (Spinner) findViewById(R.id.carYear);
        List<String> list = new ArrayList<>();
        list.add(0, "Année");
        for (int i = 0; i < mCommentList.size(); i++){

            String year = mCommentList.get(i).toString().replaceAll("\\p{P}", "");
            list.add(i + 1, year.replaceAll("year=", ""));
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_textview_align, list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_textview_align);
        spinnerYear.setAdapter(dataAdapter);

        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(position != 0){
                    year = spinnerYear.getSelectedItem().toString();
                    new LoadBrand().execute();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    public void updateBrand() {

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("year", this.year));

        // getting product details by making HTTP request
        // Note that product details url will use GET request
        JSONObject a = jsonParser.makeHttpRequest(
                LOAD_BRAND_URL, "GET", params);


        // Instantiate the arraylist to contain all the JSON data.
        // we are going to use a bunch of key-value pairs, referring
        // to the json element name, and the content, for example,
        // message it the tag, and "I'm awesome" as the content..

        mCommentList = new ArrayList<>();
        JSONArray mComments;


        // when parsing JSON stuff, we should probably
        // try to catch any exceptions:
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
                String brand = c.getString(TAG_BRAND);

                // creating new HashMap
                HashMap<String, String> map = new HashMap<>();

                map.put(TAG_BRAND, brand);

                System.out.println(brand);              // adding HashList to ArrayList
                mCommentList.add(map);

                // annndddd, our JSON data is up to date same with our array
                // list
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public class LoadBrand extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddComment.this);
            pDialog.setMessage("Loading Brand...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {
            updateBrand();
            return null;

        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            pDialog.dismiss();
            updateBrandSpinner();
        }
    }

    private void updateBrandSpinner() {

        spinnerBrand = (Spinner) findViewById(R.id.carBrand);
        List<String> list = new ArrayList<>();
        list.add(0, "Marque");
        for (int i = 0; i < mCommentList.size(); i++){

            String brand = mCommentList.get(i).toString().replace("}", "");
            list.add(i + 1, brand.substring(7));
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_textview_align, list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_textview_align);
        spinnerBrand.setAdapter(dataAdapter);

        spinnerBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(position != 0) {
                    year = spinnerYear.getSelectedItem().toString();
                    brand = spinnerBrand.getSelectedItem().toString();
                    new LoadModel().execute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    public void updateModel() {

        String brand = this.brand;
        String year = this.year;

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("brand", brand));
        params.add(new BasicNameValuePair("year", year));

        // getting product details by making HTTP request
        // Note that product details url will use GET request
        JSONObject a = jsonParser.makeHttpRequest(
                LOAD_MODEL_URL, "GET", params);

        // Instantiate the arraylist to contain all the JSON data.
        // we are going to use a bunch of key-value pairs, referring
        // to the json element name, and the content, for example,
        // message it the tag, and "I'm awesome" as the content..

        mCommentList = new ArrayList<>();
        JSONArray mComments;


        // when parsing JSON stuff, we should probably
        // try to catch any exceptions:
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
                String model = c.getString(TAG_MODEL);

                // creating new HashMap
                HashMap<String, String> map = new HashMap<>();

                map.put(TAG_MODEL, model);

                // adding HashList to ArrayList
                mCommentList.add(map);

                // annndddd, our JSON data is up to date same with our array
                // list
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public class LoadModel extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddComment.this);
            pDialog.setMessage("Loading Model...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {
            updateModel();
            return null;

        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            pDialog.dismiss();
            updateModelSpinner();
            new LoadJob().execute();
        }
    }

    private void updateModelSpinner() {

        spinnerModel = (Spinner) findViewById(R.id.carModel);
        List<String> list = new ArrayList<>();
        list.add(0, "Model");
        for (int i = 0; i < mCommentList.size(); i++){

            String model = mCommentList.get(i).toString().replaceAll("\\p{P}", "");
            list.add(i + 1, model.substring(6));
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_textview_align, list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_textview_align);
        spinnerModel.setAdapter(dataAdapter);
    }




    public void updateJob() {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("year", "A"));


        // Instantiate the arraylist to contain all the JSON data.
        // we are going to use a bunch of key-value pairs, referring
        // to the json element name, and the content, for example,
        // message it the tag, and "I'm awesome" as the content..

        mCommentList = new ArrayList<>();
        JSONArray mComments;

        // Bro, it's time to power up the J parser
        JSONParser jParser = new JSONParser();
        // Feed the beast our comments url, and it spits us
        // back a JSON object. Boo-yeah Jerome.
        JSONObject a = jParser.getJSONFromUrl(LOAD_JOB_URL);

        // when parsing JSON stuff, we should probably
        // try to catch any exceptions:
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
                String job = c.getString(TAG_JOB);

                // creating new HashMap
                HashMap<String, String> map = new HashMap<>();

                map.put(TAG_JOB, job);

                // adding HashList to ArrayList
                mCommentList.add(map);

                // annndddd, our JSON data is up to date same with our array
                // list
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public class LoadJob extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddComment.this);
            pDialog.setMessage("Loading Jobs...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {
            updateJob();
            return null;

        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            pDialog.dismiss();
            updateJobSpinner();
        }
    }

    private void updateJobSpinner() {

        spinnerJob = (Spinner) findViewById(R.id.selectJob);
        List<String> list = new ArrayList<>();
        list.add(0, "Travaux disponibles");
        for (int i = 0; i < mCommentList.size(); i++){

            String job = mCommentList.get(i).toString().replace("}", "");
            list.add(i + 1, job.substring(5));
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_textview_align, list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_textview_align);
        spinnerJob.setAdapter(dataAdapter);
    }

}

