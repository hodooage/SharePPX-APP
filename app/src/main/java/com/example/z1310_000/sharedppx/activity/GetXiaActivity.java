package com.example.z1310_000.sharedppx.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.z1310_000.sharedppx.R;
import com.example.z1310_000.sharedppx.databinding.ActivityGetXiaBinding;
import com.example.z1310_000.sharedppx.entity.UseRecord;
import com.example.z1310_000.sharedppx.entity.User;
import com.example.z1310_000.sharedppx.entity.Xia;
import com.example.z1310_000.sharedppx.service.UseRecordService;
import com.example.z1310_000.sharedppx.service.XiaService;
import com.example.z1310_000.sharedppx.entity.ResponseResult;
import com.example.z1310_000.sharedppx.utils.Tips;

import org.litepal.crud.DataSupport;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetXiaActivity extends BaseActivity {

    private static final String TAG = "GetXiaActivity";
    private  ActivityGetXiaBinding mBinding;

    private User nowUser= DataSupport.findFirst(User.class);

    private Xia nowXia;

    private UseRecordService useRecordService=UseRecordService.util.getUseRecordService();

    private XiaService xiaService=XiaService.util.getXiaService();

    UseRecord useRecord;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding= DataBindingUtil.setContentView(this,R.layout.activity_get_xia);
        initToolbar("虾辆信息");

        nowXia=(Xia) getIntent().getSerializableExtra("xia");
//        initView();
//
        initData();
//
        initListener();
    }

    //初始化页面
    private void initView() {

    }


    private void initData(){
        mBinding.num.setText(String.valueOf(nowXia.getId()));
        Typeface colorttf=Typeface.createFromAsset(getAssets(),"fonts/color.ttf");
        switch (nowXia.getType()){
            case 0:
                mBinding.type.setText("普通皮皮虾");
                break;
            case 1:
                mBinding.type.setText("黄金皮皮虾");
                break;
            case 2:
                mBinding.type.setTypeface(colorttf);
                mBinding.type.setText("七彩至尊皮皮虾");
                break;
        }

        mBinding.price.setText(String.valueOf(nowXia.getPrice()));

    }

    private void initListener(){
        mBinding.ppxgo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "onResponse: 点击了ppxgo");
                int userId = nowUser.getUid();
                int xiaId = nowXia.getId();

                //更改虾状态为正在使用
                Call<ResponseResult<String>> changeStateCall = xiaService.startXiaById(nowXia.getId());
                changeStateCall.enqueue(new Callback<ResponseResult<String>>() {
                    @Override
                    public void onResponse(Call<ResponseResult<String>> call, Response<ResponseResult<String>> response) {
                        ResponseResult<String> result = response.body();
                        Log.e(TAG, "onResponse: "+ result);
                    }

                    @Override
                    public void onFailure(Call<ResponseResult<String>> call, Throwable t) {
                        Toast.makeText(GetXiaActivity.this, Tips.INTERNET_ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
                    }
                });

                //生成新的使用记录
                Call<ResponseResult<UseRecord>> call = useRecordService.insertNewUseRecord(userId, xiaId);
                call.enqueue(new Callback<ResponseResult<UseRecord>>() {
                    @Override
                    public void onResponse(Call<ResponseResult<UseRecord>> call, Response<ResponseResult<UseRecord>> response) {
                        ResponseResult<UseRecord> result=response.body();
                        useRecord=result.getData();
                        Log.e(TAG, "onResponse: "+result);
                        //启动计时页面
                        CountTimeActivity.startAction(GetXiaActivity.this, useRecord);
                    }

                    @Override
                    public void onFailure(Call<ResponseResult<UseRecord>> call, Throwable t) {
                        Toast.makeText(GetXiaActivity.this, Tips.INTERNET_ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public static void startAction(Context context,Xia xia){
        Intent intent=new Intent(context,GetXiaActivity.class);
        intent.putExtra("xia",xia);
        context.startActivity(intent);
    }
}
