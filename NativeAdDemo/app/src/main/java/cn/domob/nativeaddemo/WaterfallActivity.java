package cn.domob.nativeaddemo;

import com.nostra13.universalimageloader.core.ImageLoader;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.domob.android.nativeads.NativeAd;
import cn.domob.android.nativeads.NativeAd.NativeAdDataLoadListener;
import cn.domob.nativeaddemo.R;

/**
 * DomobNativeAdDemo
 * GridView接入推广信息，在请求NativeAd之前已经确定和预留了NativeAd显示的条目位置,请求成功返回后更新条目信息。
 */
public class WaterfallActivity extends BaseActivity {

	private ProgressBar progressBar;
	private GridView gv_content;
	private TextView tv_title;
	private View ll_goback;
	private SwipeRefreshLayout swipeLayout;
	private String[] picUrls = Constants.picUrls;
	private SparseArray<NativeAd> nativeAds = new SparseArray<NativeAd>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_grid;
	}

	@Override
	protected void findViewById() {
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		gv_content = (GridView) findViewById(R.id.gv_content);
		tv_title = (TextView) findViewById(R.id.tv_title);
		ll_goback = findViewById(R.id.ll_goback);
		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.srl_refresh);
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
						// 清除缓存
						nativeAds.clear();
						swipeLayout.setRefreshing(false);
					}
				}, 2000);
			}
		});
	}

	@Override
	protected void initData() {
		tv_title.setText(R.string.title_grid2);
		gv_content.setAdapter(new MyAdapter());
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				progressBar.setVisibility(View.GONE);
				swipeLayout.setVisibility(View.VISIBLE);
			}
		}, 2000);
	}

	private void loadNativeAd(MyViewHolder holder) {
		new NativeAd(this, MainActivity.publisherID, MainActivity.nativePPID5, new MyNativeAdDataLoadListener(holder))
				.loadNativeAd();
	}

	static class MyViewHolder {
		int position;
		ImageView icon;
		TextView title;
		TextView bt;
	}

	public class MyAdapter extends BaseAdapter {
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView = null;
			MyViewHolder holder = null;
			if (convertView == null || convertView.getTag() == null) {
				if (isNativeAd(position)) {
					// Domob Native Ad
					convertView = View.inflate(appContext, R.layout.item_grid2, null);
					holder = new MyViewHolder();
					holder.icon = (ImageView) convertView.findViewById(R.id.iv_icon);
					holder.title = (TextView) convertView.findViewById(R.id.tv_title);
					holder.bt = (TextView) convertView.findViewById(R.id.tv_action);
					convertView.setTag(holder);
				} else {
					convertView = View.inflate(appContext, R.layout.item_grid1, null);
					imageView = (ImageView) convertView.findViewById(R.id.iv_image);
					convertView.setTag(imageView);
				}
			} else {
				if (isNativeAd(position)) {
					holder = (MyViewHolder) convertView.getTag();
				} else {
					imageView = (ImageView) convertView.getTag();
				}
			}

			if (isNativeAd(position)) {
				// 如果当前条目已经请求过广告则复用
				NativeAd nativeAd = nativeAds.get(position);
				if (nativeAd == null) {
					holder.position = position;
					loadNativeAd(holder);
				} else {
					renderNativeAdData(nativeAd, holder);
					nativeAd.trackImpression();
				}
			} else {
				imageView.setImageResource(R.drawable.domob_item1);
				ImageLoader.getInstance().displayImage(picUrls[position], imageView);
			}
			return convertView;
		}

		@Override
		public int getItemViewType(int position) {
			int type;
			if (isNativeAd(position)) {
				type = 1;
			} else {
				type = 0;
			}
			return type;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			return this;
		}

		@Override
		public int getCount() {
			if (null == picUrls) {
				return 0;
			}
			return picUrls.length;
		}
	}

	// 设置规则是否显示NativeAd
	private boolean isNativeAd(int position) {
		return position == 4 || position != 0 && position % 10 == 0;
	}

	public class MyNativeAdDataLoadListener implements NativeAdDataLoadListener {

		MyViewHolder holder;

		public MyNativeAdDataLoadListener(MyViewHolder holder) {
			this.holder = holder;
		}

		@Override
		public void onLoadSuccess(final NativeAd nativeAd) {
			// 保存NativeAd
			nativeAds.put(holder.position, nativeAd);

			renderNativeAdData(nativeAd, holder);
			// 发现展现报告
			nativeAd.trackImpression();
		}

		@Override
		public void onLoadFailer(String errorMessage) {
			Log.e("DomobSDKDemo", errorMessage);
		}
	}

	// 绑定数据
	private void renderNativeAdData(final NativeAd nativeAd, final MyViewHolder holder) {
		// 考虑ViewHolder的复用，防止图片错乱，先设置默认图片
		holder.icon.setImageResource(R.drawable.domob_icon);
		ImageLoader.getInstance().displayImage(nativeAd.nativeAdData.iconInfo.imageUrl, holder.icon);
		holder.bt.setText(nativeAd.nativeAdData.clickActionText);
		holder.title.setText(nativeAd.nativeAdData.title);
		holder.bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				nativeAd.handleAction();
			}
		});
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
