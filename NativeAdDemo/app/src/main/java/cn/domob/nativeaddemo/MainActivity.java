package cn.domob.nativeaddemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends BaseActivity {

	private View iv_list1;
	private View iv_list2;
	private View iv_list3;
	private View iv_list4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_main;
	}

	@Override
	protected void findViewById() {
		iv_list1 = findViewById(R.id.iv_list1);
		iv_list2 = findViewById(R.id.iv_list2);
		iv_list3 = findViewById(R.id.iv_list3);
		iv_list4 = findViewById(R.id.iv_list4);
	}

	@Override
	protected void setListener() {
		iv_list1.setOnClickListener(this);
		iv_list2.setOnClickListener(this);
		iv_list3.setOnClickListener(this);
		iv_list4.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.iv_list1:
			intent = new Intent(appContext, ImageTextActivity.class);
			break;
		case R.id.iv_list2:
			intent = new Intent(appContext, NewsStreamActivity.class);
			break;
		case R.id.iv_list3:
			intent = new Intent(appContext, WaterfallActivity.class);
			break;
		case R.id.iv_list4:
			intent = new Intent(appContext, WaterfallActivity2.class);
			break;
		}
		startActivity(intent);
	}
}