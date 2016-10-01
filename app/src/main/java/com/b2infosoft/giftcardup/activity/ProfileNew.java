package com.b2infosoft.giftcardup.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.b2infosoft.giftcardup.R;
import com.b2infosoft.giftcardup.app.Config;
import com.b2infosoft.giftcardup.app.Format;
import com.b2infosoft.giftcardup.app.Tags;
import com.b2infosoft.giftcardup.app.Urls;
import com.b2infosoft.giftcardup.credential.Active;
import com.b2infosoft.giftcardup.custom.AlertBox;
import com.b2infosoft.giftcardup.custom.Progress;
import com.b2infosoft.giftcardup.model.Approve;
import com.b2infosoft.giftcardup.model.ContactInformation;
import com.b2infosoft.giftcardup.model.User;
import com.b2infosoft.giftcardup.model.UserBalance;
import com.b2infosoft.giftcardup.urlconnection.MultipartUtility;
import com.b2infosoft.giftcardup.volly.DMRRequest;
import com.b2infosoft.giftcardup.volly.DMRResult;
import com.b2infosoft.giftcardup.volly.LruBitmapCache;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProfileNew extends AppCompatActivity implements View.OnClickListener, DMRResult {
    final private static String TAG = ProfileNew.class.getName();
    private Active active;
    private Tags tags;
    private Urls urls;
    private Config config;
    private Format format;
    AlertBox alertBox;
    private DMRRequest dmrRequest;
    Progress progress;

    CollapsingToolbarLayout toolbarLayout;
    ImageView profile_image, arrow1, arrow2, arrow3, identity, bank, ssn, arrow4, image_approve_mail;
    TextView member_science, total_sold, total_saving, mail, mobile, address, total_available_fund;
    Button resend, cardView;
    private final int PICK_IMAGE_REQUEST = 1;
    private Map<Integer, Integer> approveMap;
    private Uri filePath;
    private Bitmap bitmap;

    private void init() {
        active = Active.getInstance(this);
        tags = Tags.getInstance();
        urls = Urls.getInstance();
        config = Config.getInstance();
        format = Format.getInstance();
        alertBox = new AlertBox(this);
        dmrRequest = DMRRequest.getInstance(this, TAG);
        approveMap = new HashMap<>();
        approveMap.put(0, R.drawable.ic_u_pending);
        approveMap.put(1, R.drawable.ic_u_approved);
        approveMap.put(2, R.drawable.ic_u_rejected);
        approveMap.put(3, R.drawable.ic_u_expire);
        approveMap.put(4, R.drawable.ic_u_suspend);
        approveMap.put(9, R.drawable.ic_u_not_submitted);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setContentView(R.layout.activity_profile_new);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        User user = active.getUser();

        //getSupportActionBar().setSubtitle("Member Since : ".concat(format.getDate(user.getJoinDate())));

        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolbarLayout.setTitle(user.getFirstName() + " " + user.getLastName());

        profile_image = (ImageView) findViewById(R.id.profile_user_image);
        image_approve_mail = (ImageView) findViewById(R.id.image_view);
        identity = (ImageView) findViewById(R.id.user_identity_approve);
        bank = (ImageView) findViewById(R.id.user_bank_approve);
        ssn = (ImageView) findViewById(R.id.user_ssn_approve);
        arrow1 = (ImageView) findViewById(R.id.identification_arrow);
        arrow2 = (ImageView) findViewById(R.id.bank_arrow);
        arrow2.setOnClickListener(this);
        arrow3 = (ImageView) findViewById(R.id.ssn_arrow);
        arrow4 = (ImageView) findViewById(R.id.info_arrow);
        arrow4.setOnClickListener(this);
        total_available_fund = (TextView) findViewById(R.id.total_available_fund);
        total_saving = (TextView) findViewById(R.id.total_saving);
        total_sold = (TextView) findViewById(R.id.total_sold);
        mail = (TextView) findViewById(R.id.profile_short_mail);
        mobile = (TextView) findViewById(R.id.profile_short_phone);
        address = (TextView) findViewById(R.id.profile_short_address);
        resend = (Button) findViewById(R.id.resend_btn);
        resend.setOnClickListener(this);
        cardView = (Button) findViewById(R.id.approve_card);
        // member_science = (TextView)findViewById(R.id.profile_member);
        if (active.isLogin()) {
            mail.setText(user.getEmail());
        }

        LruBitmapCache.loadCacheImageProfile(this, profile_image, config.getUserProfileImageAddress().concat(user.getImage()), TAG);
        // member_science.setText("Member Since : ".concat(format.getDate(user.getJoinDate())));
        getApproveForSelling();
        checkAvailableBalance();
        fetchContactInfo();
        checkVerifiedStatus();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile_new, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                this.onBackPressed();
                return true;
            case R.id.action_edit:
                showFileChooser();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setProfile(ContactInformation information) {
        mobile.setText(information.getPhoneNumber());
        address.setText(information.getAddressFull(this));
    }

    private void getApproveForSelling() {
        Map<String, String> map = new HashMap<>();
        map.put(tags.USER_ACTION, tags.CHECK_APPROVE_FOR_SELLING);
        map.put(tags.USER_ID, active.getUser().getUserId() + "");
        dmrRequest.doPost(urls.getUserInfo(), map, this);
    }

    private void setIcons(Approve approve) {
        identity.setImageResource(approveMap.get(approve.getIdentification()));
        bank.setImageResource(approveMap.get(approve.getBank()));
        ssn.setImageResource(approveMap.get(approve.getSsn()));
        int mail = approve.getEmail();
        if (mail == 1) {
            image_approve_mail.setVisibility(View.VISIBLE);
            cardView.setOnClickListener(this);
        } else {
            resend.setVisibility(View.VISIBLE);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertBox.setTitle("Alert");
                    alertBox.setMessage("Verify Email");
                    alertBox.show();
                }
            });
        }
        int identity = approve.getIdentification();
        if (identity != 0 && identity != 1) {
            arrow1.setVisibility(View.VISIBLE);
            arrow1.setOnClickListener(this);
        } else {
            arrow1.setVisibility(View.GONE);
        }
        int ssn = approve.getSsn();
        if (ssn != 0 && ssn != 1) {
            arrow3.setVisibility(View.VISIBLE);
            arrow3.setOnClickListener(this);
        } else {
            arrow3.setVisibility(View.GONE);
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == this.RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), filePath);
                if (profile_image != null)
                    profile_image.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        new updateImage().execute();
    }

    private class updateImage extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progress.show();
        }

        @Override
        protected void onPostExecute(String s) {
           // progress.dismiss();
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... params) {
            User user = active.getUser();
            String url = urls.getUserInfo();
            try {
                MultipartUtility multipart = new MultipartUtility(url);
                multipart.addHeaderField("User-Agent", "CodeJava");
                multipart.addHeaderField("Test-Header", "Header-Value");
                multipart.addFormField(tags.USER_ACTION, tags.UPDATE_PROFILE_PIC);
                multipart.addFormField(tags.USER_ID, active.getUser().getUserId() + "");
                multipart.addFilePartBitmap(tags.PROFILE_NAME, "bank_void_image.png", bitmap);
                multipart.addFormField(tags.PROFILE_OLD_NAME, user.getImage());
                return multipart.finishString();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage() + "");
            }
            return "";
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.identification_arrow:
                startActivity(new Intent(this, AddIdentity.class));
                break;
            case R.id.bank_arrow:
                startActivity(new Intent(this, ProfileBankDetails.class));
                break;
            case R.id.ssn_arrow:
                startActivity(new Intent(this, AddSSN.class));
                break;
            case R.id.info_arrow:
                startActivity(new Intent(this, ProfileIdentification.class));
                break;
            case R.id.resend_btn:
                Toast.makeText(this, "Coming Soon...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.approve_card:
                startActivity(new Intent(this, ProfileSsnEin.class));
                finish();
                break;
            default:

        }
    }

    private void checkVerifiedStatus() {
        Map<String, String> map = new HashMap<>();
        map.put(tags.USER_ACTION, tags.USER_ALL_APPROVE_INFO);
        map.put(tags.USER_ID, active.getUser().getUserId() + "");
        dmrRequest.doPost(urls.getUserInfo(), map, this);
    }

    @Override
    public void onSuccess(JSONObject jsonObject) {
        try {
            if (jsonObject.has(tags.SUCCESS)) {
                if (jsonObject.getInt(tags.SUCCESS) == tags.PASS) {
                    if (jsonObject.has(tags.CHECK_APPROVE_FOR_SELLING)) {
                        Log.d("approveStat",jsonObject.toString());
                        if (jsonObject.getInt(tags.CHECK_APPROVE_FOR_SELLING) == tags.PASS) {
                            cardView.setVisibility(View.GONE);
                        } else if (jsonObject.getInt(tags.CHECK_APPROVE_FOR_SELLING) == tags.FAIL) {
                            cardView.setVisibility(View.VISIBLE);
                        }
                    }
                    if (jsonObject.has(tags.USER_ALL_APPROVE_INFO)) {
                        setIcons(Approve.fromJSON(jsonObject.getJSONObject(tags.USER_ALL_APPROVE_INFO)));
                    }
                    if (jsonObject.has(tags.USER_CONTACT_INFORMATION)) {
                        ContactInformation information = ContactInformation.fromJSON(jsonObject.getJSONObject(tags.USER_CONTACT_INFORMATION));
                        setProfile(information);
                    }
                    if (jsonObject.has(tags.AVAILABLE_FUND_BALANCE)) {
                        updateFund(UserBalance.fromJSON(jsonObject.getJSONObject(tags.AVAILABLE_FUND_BALANCE)));
                    }
                } else if (jsonObject.getInt(tags.SUCCESS) == tags.FAIL) {

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onError(VolleyError volleyError) {
        volleyError.printStackTrace();
        Log.e(TAG, volleyError.getMessage());
    }

    private void checkAvailableBalance() {
        Map<String, String> map = new HashMap<>();
        map.put(tags.USER_ACTION, tags.AVAILABLE_FUND_BALANCE);
        map.put(tags.USER_ID, active.getUser().getUserId());
        dmrRequest.doPost(urls.getUserInfo(), map, this);
    }

    private void updateFund(UserBalance fund) {
        total_available_fund.setText("$" + fund.getAvailable_fund());
        total_saving.setText("$" + fund.getTotal_saving());
        total_sold.setText("$" + fund.getTotal_sold());
        String str = fund.getTotal_sold();
        Log.d("sold",str + "");
    }

    private void fetchContactInfo() {
        if (active.isLogin()) {
            User user = active.getUser();

            /* LOADING USER DETAILS */
            Map<String, String> map1 = new HashMap<>();
            map1.put(tags.USER_ACTION, tags.USER_CONTACT_INFORMATION);
            map1.put(tags.USER_ID, user.getUserId() + "");
            dmrRequest.doPost(urls.getUserInfo(), map1, this);
        }
    }
}
