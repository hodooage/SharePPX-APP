package com.example.z1310_000.sharedppx.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.z1310_000.sharedppx.R;
import com.example.z1310_000.sharedppx.entity.User;
import com.example.z1310_000.sharedppx.request.RetrieveUserBalanceRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import cz.msebera.android.httpclient.Header;

public class MyWalletActivity extends AppCompatActivity {

    private TextView balance;

    private User nowUser=DataSupport.findFirst(User.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wallet);

        initView();
        initData();

    }

    private void initView(){
        balance= (TextView) findViewById(R.id.balance);

    }

    private void initData(){
        //检查当前账户余额是否足够
        AsyncHttpClient RetrieveUserBalanceClient=new AsyncHttpClient();
        RetrieveUserBalanceRequest retrieveUserBalanceRequest=new RetrieveUserBalanceRequest();
        int uid=nowUser.getUid();
        RetrieveUserBalanceClient.post(MyWalletActivity.this,retrieveUserBalanceRequest.getUrl(),retrieveUserBalanceRequest.getStringEntity(uid),"application/json",new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    balance.setText(response.getString("balance"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    public static void startAction(Context context){
        Intent intent=new Intent(context,MyWalletActivity.class);
        context.startActivity(intent);
    }

}
