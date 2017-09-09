package suresh.syp.saveurpasswa;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements EditPasswordDetailsFragment.EditPasswordListener, PasswordAdaptor.AdListener{
    private EditText mTitle, mUserName, mPassword, mOthers;
    private SQLiteDBUtil sqLiteDBUtil;
    private TextView edUserPin;
    private TextView tvToalPasswa;
    private ListView lvPassword;
    private PasswordAdaptor passwordAdaptor;
    private LinearLayout dialog;
    private FloatingActionButton myFab;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        startDB();
        setPreference();
        init();
        adMOb();
        checkForAPKUpdates();

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        setTitle("");
    }

    private void startWork(){
        if (isPinCreated()) {
            doStuff();
        } else {
            startCreatePinActivity();
        }
    }

    private void init() {
        dialog = (LinearLayout) findViewById(R.id.pin_layout);

        // set the custom dialog components - text, image and button
        edUserPin = (TextView) dialog.findViewById(R.id.ed_user_pin);
        mTitle = (EditText) dialog.findViewById(R.id.ed_title);
        mUserName = (EditText) dialog.findViewById(R.id.ed_username);
        mPassword = (EditText) dialog.findViewById(R.id.ed_password);
        mOthers = (EditText) dialog.findViewById(R.id.ed_other);
        tvToalPasswa = (TextView) dialog.findViewById(R.id.total_pass);

        ((TextView) findViewById(R.id.tv_view_password)).setText(getString(R.string.create_pin));
        setFloatingActionButton();
    }

    private void startCreatePinActivity() {
        Intent createPinIntent = new Intent(this, CreatePinActivity.class);
        startActivity(createPinIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
            onFocusWork();
    }

    private void onFocusWork(){
        if(!isAdActive) {
            ShowPinPopUp(null);
        }else{
            isAdActive = false;
        }
    }

    private void setFloatingActionButton() {
        myFab = (FloatingActionButton) findViewById(R.id.fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ShowPinPopUp(null);
            }
        });
    }

    private void setPasswordData() {
        //sqLiteDBUtil.OpenDB();
        ArrayList<String[]> data = sqLiteDBUtil.getDataOnDemand(this, "Select *from " + Constant.PASSWORD_TBL_NAME);
        //sqLiteDBUtil.CloseDB();
        passwordAdaptor = new PasswordAdaptor(this, data);
        if(lvPassword==null){
            lvPassword = (ListView)findViewById(R.id.password_list);
        }
        lvPassword.setAdapter(passwordAdaptor);
    }

    private void startDB() {
        sqLiteDBUtil = new SQLiteDBUtil(getApplicationContext());
        sqLiteDBUtil.CreateDB();
    }

    private void ShowPinPopUp(final String[] data) {
        if (data != null) {
            mTitle.setText(data[0]);
            mUserName.setText(data[1]);
            mPassword.setText(data[2]);
            mOthers.setText(data[3]);
            mTitle.setEnabled(false);
            mTitle.setClickable(true);
        }

        edUserPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startKeyBoard();
            }
        });

        /*edUserPin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/
        dialog.setVisibility(View.VISIBLE);
        myFab.setVisibility(View.GONE);

        dialog.findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPassordDataOk())
                    addNewPassword(data);
            }
        });

        mPassword.setTag(1);
        mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        dialog.findViewById(R.id.iv_show_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((int)mPassword.getTag() == 1){
                    mPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mPassword.setTag(0);
                }else{
                    mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mPassword.setTag(1);
                }
            }
        });

       /* dialog.findViewById(R.id.tv_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showResetDialog();
            }
        });*/
        //checkIfPinExist(dialog);
        updateTotal();
    }

    private void startKeyBoard(){
        final KeyboardView keyboardView = new KeyboardView(this);
        final AlertDialog.Builder keyboadDialog = new AlertDialog.Builder(this);
        keyboadDialog.setView(keyboardView);
        final AlertDialog ad = keyboadDialog.show();
        keyboardView.mPasswordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 4) {
                    if (!isCorrect(charSequence.toString())) {
                        keyboardView.mPasswordField.setError(HomeActivity.this.getResources().getString(R.string.error_wrong_pin));
                        return;
                    }
                    dialog.setVisibility(View.GONE);
                    myFab.setVisibility(View.VISIBLE);
                    ad.dismiss();
                    gotoPassList();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void gotoPassList() {
        setPasswordData();
    }

   /* private void showResetDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        new ResetAsyncTask().execute();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        builder.setMessage("This action will erase all your passwords and pld pin. You've to set new pin to use this application again.").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }*/

   /* private void checkIfPinExist(Dialog dialog) {
        //sqLiteDBUtil.OpenDB();
        ArrayList<String[]> data = sqLiteDBUtil.getDataOnDemand(this, "Select *from " + Constant.USER_TBL_NAME);
        //sqLiteDBUtil.CloseDB();
        if (data == null || data.size() == 0) {
            ((TextView) dialog.findViewById(R.id.tv_view_password)).setText(getString(R.string.create_pin));
            edUserPin.setHint("4 digit");
        } else {
            ((TextView) dialog.findViewById(R.id.tv_view_password)).setText(getString(R.string.view_your_passwords));
            edUserPin.setHint("Enter Pin");
        }
    }
*/
    private void addNewPassword(String[] data) {
        //sqLiteDBUtil.OpenDB();
        ArrayList<Object> passData = new ArrayList<>();
        passData.add(mTitle.getText().toString());
        passData.add(mUserName.getText().toString());
        passData.add(mPassword.getText().toString());
        passData.add(mOthers.getText().toString());
        if (data != null) {
            passData.add(data[4]);
        } else
            passData.add(mTitle.getText().toString() + "-" + mUserName.getText() + "-" + mPassword.getText());
        sqLiteDBUtil.insertIntoPasswordTbl(passData);
        //sqLiteDBUtil.CloseDB();
        Toast.makeText(this, "Added Successfully. Create Next.", Toast.LENGTH_LONG).show();

        updateTotal();
        emptyThePassFields();
    }

    private void updateTotal() {
        //sqLiteDBUtil.OpenDB();
        ArrayList<String[]> data = sqLiteDBUtil.getDataOnDemand(this, "Select * from " + Constant.PASSWORD_TBL_NAME);
        if (data != null && data.size() > 0) {
            tvToalPasswa.setVisibility(View.VISIBLE);
            tvToalPasswa.setText("Total: " + String.valueOf(data.size()));
        } else {
            tvToalPasswa.setVisibility(View.GONE);
        }
        //sqLiteDBUtil.CloseDB();

    }

    private void emptyThePassFields() {
        mTitle.setText("");
        mUserName.setText("");
        mPassword.setText("");
        mOthers.setText("");
    }

    private boolean isCorrect(String s) {
        //sqLiteDBUtil.OpenDB();
        ArrayList<String[]> data = sqLiteDBUtil.getDataOnDemand(this, "Select *from " + Constant.USER_TBL_NAME);
        //sqLiteDBUtil.CloseDB();
        if (data == null || data.size() == 0) {
            ArrayList<Object> pinData = new ArrayList<>();
            pinData.add(s.toString());
            //sqLiteDBUtil.OpenDB();
            sqLiteDBUtil.insertIntoPinTbl(pinData);
            //sqLiteDBUtil.CloseDB();
            Toast.makeText(this, "Pin created successfully : " + s.toString(), Toast.LENGTH_LONG).show();
            return true;
        }

        if (data.size() > 0) {
            String userPin = data.get(0)[0];
            if (s.toString().equals(userPin)) {
                return true;
            } else {
                edUserPin.setError("Wrong PIN!!");
                return false;
            }
        }
        return true;
    }

    public boolean isPassordDataOk() {
        if (mTitle.getText().toString().trim().length() == 0) {
            mTitle.setError("Enter a Title!");
            return false;
        }
        if (mUserName.getText().toString().trim().length() == 0) {
            mUserName.setError("Enter username!");
            return false;
        }
        if (mPassword.getText().toString().trim().length() == 0) {
            mPassword.setError("Enter password!");
            return false;
        }
        return true;
    }

    @Override
    public void onClickEdit(String[] data) {
        ShowPinPopUp(data);
    }

    @Override
    public void onDelete(String id) {
        //sqLiteDBUtil.OpenDB();
        sqLiteDBUtil.getDataOnDemand(this, "delete from "+Constant.PASSWORD_TBL_NAME+" where id='"+id+"'");
        //sqLiteDBUtil.CloseDB();
        setPasswordData();
    }

    public boolean isPinCreated() {
        ArrayList<String[]> data = sqLiteDBUtil.getDataOnDemand(this, "Select *from " + Constant.USER_TBL_NAME);
        return (data.size() != 0);
    }

    /*private class ResetAsyncTask extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog = null;
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(HomeActivity.this);
            progressDialog.setMessage("Resetting...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            HomeActivity.this.resetApplication();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if(progressDialog!=null)
                progressDialog.dismiss();

            setPasswordData();
            Toast.makeText(HomeActivity.this, "Data reset successfully", Toast.LENGTH_LONG).show();
            ShowPinPopUp(null);
            super.onPostExecute(s);
        }
    }

    private void resetApplication() {
        //sqLiteDBUtil.OpenDB();
        sqLiteDBUtil.getDataOnDemand(this, "delete from "+Constant.PASSWORD_TBL_NAME);
        //sqLiteDBUtil.CloseDB();

        //sqLiteDBUtil.OpenDB();
        sqLiteDBUtil.getDataOnDemand(this, "delete from "+Constant.USER_TBL_NAME);
        //sqLiteDBUtil.CloseDB();
    }*/


    // Checking for the update on play store availabel
    private void setPreference() {
        PreferenceManager.setDefaultValues(this, R.xml.mypreference, false);
    }

    private void checkForAPKUpdates() {
        if (NetworkUtility.isNetworkAvailable(this)) {
            new GetLatestVersion().execute();
        }else{
            startWork();
        }
    }

    //To force update the user
    private String latestVersion;
    private String getCurrentVersion() {
        String currentVersion;
        PackageManager pm = this.getPackageManager();
        PackageInfo pInfo = null;

        try {
            pInfo = pm.getPackageInfo(this.getPackageName(), 0);

        } catch (PackageManager.NameNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        currentVersion = pInfo.versionName;
        return currentVersion;
    }

    private class GetLatestVersion extends AsyncTask<String, String, Boolean> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(HomeActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                //It retrieves the latest version by scraping the content of current version from play store at runtime
                String urlOfAppFromPlayStore = "https://play.google.com/store/apps/details?id=suresh.syp.saveurpasswa";
                Document doc = Jsoup.connect(urlOfAppFromPlayStore).get();
                if (doc != null) {
                    latestVersion = doc.getElementsByAttributeValue
                            ("itemprop", "softwareVersion").first().text();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean jsonObject) {
            super.onPostExecute(jsonObject);
            if(!jsonObject){
                progressDialog.dismiss();
                return;
            }

            if (progressDialog != null)
                progressDialog.dismiss();

            if (jsonObject) {
                String currentVersion = getCurrentVersion();
                Toast.makeText(HomeActivity.this, currentVersion + ", " + latestVersion, Toast.LENGTH_LONG).show();
                if (latestVersion != null && currentVersion != null) {
                    if (!currentVersion.equalsIgnoreCase(latestVersion)) {
                        SDKSettings.setSharedPreferenceString(HomeActivity.this, AppConstants.KEY_PREF_VERSION, latestVersion);
                        showUpdateDialog();
                    }
                }
            }
            startWork();
        }
    }

    private void doStuff() {
        lvPassword = (ListView) findViewById(R.id.password_list);
        setPasswordData();
    }

    private void showUpdateDialog() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("A New Update is Available");

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }

                dialog.dismiss();
            }
        });

        builder.setCancelable(false);
        builder.show();
    }

    boolean isAdActive = false;
    //Code from here is to show the add, temporarly off because of account suspension

    private InterstitialAd mInterstitialAd;
    private void adMOb() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.ad_unit_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.i("Ads", "onAdLoaded");
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
                Log.i("Ads", "onAdOpened");
                isAdActive = true;
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Log.i("Ads", "onAdLeftApplication");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                // openViewActivity();
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the interstitial ad is closed.
            }
        });
    }

    private void invokeAd() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
    }

    @Override
    public void invoke() {
        invokeAd();
    }

    @Override
    public void onBackPressed() {
        if(isAdActive)
            return;
        super.onBackPressed();
    }
}

