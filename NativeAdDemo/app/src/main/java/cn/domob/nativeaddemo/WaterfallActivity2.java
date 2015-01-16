package cn.domob.nativeaddemo;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.domob.nativeaddemo.R;
/**
 * DomobNativeAdDemo 
 * RecyclerView接入推广信息。
 */
public class WaterfallActivity2 extends BaseActivity {
	
	private ProgressBar progressBar;
	private TextView tv_title;
	private View ll_goback;
	private GridLayoutManager gridLayoutManager;
	private RecyclerView recyclerView;
	private SwipeRefreshLayout swipeLayout;
	private RecycleAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_recycle;
	}

	@Override
	protected void findViewById() {
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		tv_title = (TextView) findViewById(R.id.tv_title);
		ll_goback = findViewById(R.id.ll_goback);
		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.srl_refresh);
		recyclerView = (RecyclerView) findViewById(R.id.rv_content);
	}

	@Override
	protected void setListener() {
		ll_goback.setOnClickListener(this);
		swipeLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						swipeLayout.setRefreshing(false);
						adapter.clear();
						adapter.notifyDataSetChanged();
					}
				}, 2000);
			}
		});
	}

	@Override
	protected void initData() {
		tv_title.setText(R.string.title_grid1);
		gridLayoutManager = new GridLayoutManager(this, 3);
		adapter = new RecycleAdapter(this, Constants.picUrls);
		recyclerView.setLayoutManager(gridLayoutManager);
		recyclerView.setAdapter(adapter);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				progressBar.setVisibility(View.GONE);
				swipeLayout.setVisibility(View.VISIBLE);
			}
		}, 2000);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_goback:
			finish();
			break;

		default:
			break;
		}
	}
}
