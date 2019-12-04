package com.zyt.handflashlight;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class AboutActivity extends AppCompatActivity {

    private static final String ALIPAY_PACKAGE_NAME = "com.eg.android.AlipayGphone";
    private TextView tv_hongBao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        tv_hongBao =findViewById(R.id.tv_HongBao);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_hongBao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cm = (ClipboardManager) getSystemService(AboutActivity.this.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("支付宝赏金", "打开支付宝首页搜“8046029”领红包！\n");
                cm.setPrimaryClip(clipData);
                Toast.makeText(getApplicationContext(),"已经复制到剪切板",Toast.LENGTH_SHORT).show();
                try {
                    Uri uri = Uri.parse("alipayqr://platformapi/startapp");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
