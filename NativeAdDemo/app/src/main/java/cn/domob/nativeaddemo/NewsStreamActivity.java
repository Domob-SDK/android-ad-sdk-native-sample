package cn.domob.nativeaddemo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.nostra13.universalimageloader.core.ImageLoader;
import cn.domob.android.nativeads.NativeAd;
import cn.domob.android.nativeads.NativeAdData;
import cn.domob.android.nativeads.ViewBinder;
import cn.domob.android.nativeads.NativeAd.NativeAdDataLoadListener;
import cn.domob.nativeaddemo.R;
import cn.domob.nativeaddemo.domain.NewsInfo;
import cn.domob.nativeaddemo.domain.NewsInfo.NewsItem;
import cn.domob.nativeaddemo.service.ItemsControl;
import cn.domob.nativeaddemo.ui.XListView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
/**
 * DomobNativeAdDemo 
 * ListView接入推广信息,使用Domob SDK自带的ViewBinder 和 ViewBinder.NativeViewHolder,在请求到NativeAd广告后再插入显示。
 */
public class NewsStreamActivity extends BaseActivity implements XListView.IXListViewListener {
	public static final int MESSAGE_REFRESH_NEWS = 101;
	public static final int MESSAGE_ADD_NEWS = 102;
	public static final int MESSAGE_REFRESH_AD = 103;

	private List<Integer> positions = new ArrayList<Integer>();
	private ProgressBar progressBar;
	private ItemsControl itemsControl;
	private MyListAdapter adapter;
	private XListView listNews;
	private TextView tv_title;
	private View ll_goback;
	private View ll_title;

	private Handler handler = new Handler(Looper.getMainLooper()) {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MESSAGE_REFRESH_NEWS://首次进入 或者下拉刷新
				NewsInfo itemsInfo = (NewsInfo) msg.obj;
				if (adapter == null) {
					adapter = new MyListAdapter(appContext, itemsInfo);
					setTitle(itemsInfo.getTitle());
					listNews.setAdapter(adapter);
					listNews.setVisibility(View.VISIBLE);
					progressBar.setVisibility(View.GONE);
				} else {
					positions.clear();
					adapter.setRefershData(itemsInfo);
					adapter.notifyDataSetChanged();
					listNews.setRefreshTime(getCurrentDate());
					listNews.stopRefresh();
				}
				break;
			case MESSAGE_ADD_NEWS://上拉加载更多
				NewsInfo addInfo = (NewsInfo) msg.obj;
				adapter.addAllData(addInfo);
				adapter.notifyDataSetChanged();
				listNews.stopLoadMore();
				break;
			case MESSAGE_REFRESH_AD://请求原生广告成功
				NewsItem newsItem = (NewsItem) msg.obj;
				//往adapter对象加请求到的NativeAd
				adapter.addItem(newsItem);
				//刷新界面
				adapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_news_stream;
	}

	@Override
	protected void findViewById() {
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		listNews = (XListView) findViewById(R.id.listNews);
		tv_title = (TextView) findViewById(R.id.tv_title);
		ll_goback = findViewById(R.id.ll_goback);
		ll_title = findViewById(R.id.ll_title);
	}

	@Override
	protected void setListener() {
		ll_goback.setOnClickListener(this);
		listNews.setXListViewListener(this);
		listNews.setPullLoadEnable(true);
		listNews.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				NewsItem itemsInfo = (NewsItem) parent.getItemAtPosition(position);
				if (itemsInfo != null) {
					if (itemsInfo.getType() == ItemsControl.NEWS_TYPE) {
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(itemsInfo.getLink()));
						NewsStreamActivity.this.startActivity(intent);
					}
				}
			}
		});
	}

	@Override
	protected void initData() {
		tv_title.setText(R.string.title_list2);
		ll_title.setBackgroundResource(R.color.title_background2);
		ll_goback.setBackgroundResource(R.drawable.select_back_background2);
		// 请求RSS
		itemsControl = new ItemsControl();
		requestRss(MESSAGE_REFRESH_NEWS);
	}

	
	/**
	 * 加载NativeAd
	 * @param position NativeAd需要插入ListView的位置
	 */
	private void loadNativeAd(int position) {
		if (!positions.contains(position)) {
			positions.add(position);
			new NativeAd(this, MainActivity.publisherID, MainActivity.nativePPID2, new MyNativeAdDataLoadListener(
					position)).loadNativeAd();
		}
	}

	public class MyNativeAdDataLoadListener implements NativeAdDataLoadListener {

		private int position;

		public MyNativeAdDataLoadListener(int position) {
			this.position = position;
		}

		@Override
		public void onLoadSuccess(NativeAd nativeAd) {
			// 保存NativeAd
			NewsItem newsItem = new NewsItem();
			newsItem.setType(ItemsControl.AD_TYPE);
			newsItem.setPosition(position);
			newsItem.setNativeAd(nativeAd);
			handler.obtainMessage(MESSAGE_REFRESH_AD, newsItem).sendToTarget();
		}

		@Override
		public void onLoadFailer(String errorMessage) {

		}
	}

	public class MyListAdapter extends BaseAdapter {

		private Context context;
		private NewsInfo itemsInfo;

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
		}

		public MyListAdapter(Context context, NewsInfo itemsInfo) {
			this.context = context;
			this.itemsInfo = itemsInfo;
		}

		public void addItem(NewsItem newsItem) {
			itemsInfo.addItem(newsItem);
		}

		public void addAllData(NewsInfo items) {
			if (items != null) {
				itemsInfo.addAllItems(items);
			}
		}

		public void setRefershData(NewsInfo items) {
			this.itemsInfo = items;
		}

		@Override
		public int getCount() {
			return itemsInfo.getItems().size();
		}

		@Override
		public Object getItem(int position) {
			if (itemsInfo != null) {
				return itemsInfo.getItems().get(position);
			} else {
				return null;
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getItemViewType(int position) {
			return itemsInfo.getItems().get(position).getType();
		}

		@Override
		public int getViewTypeCount() {
			return itemsInfo.getTypeCount();
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			//间隔10个 触发请求NativeAd
			if (position % 10 == 1) {
				loadNativeAd(position);
			}
			MyViewHolder holder = null;
			ViewBinder binder = null;
			//获取要显示的信息
			final NewsItem newsItem = itemsInfo.getItems().get(position);
			if (convertView == null || convertView.getTag() == null) {
				//判断类型
				if (newsItem.getType() == ItemsControl.NEWS_TYPE) {
					convertView = View.inflate(context, R.layout.item_list3, null);
					holder = new MyViewHolder();
					holder.desc = (TextView) convertView.findViewById(R.id.tv_desc);
					holder.title = (TextView) convertView.findViewById(R.id.tv_title);
					holder.putData = (TextView) convertView.findViewById(R.id.tv_putdata);
					convertView.setTag(holder);
				} else if (newsItem.getType() == ItemsControl.AD_TYPE) {
					convertView = View.inflate(context, R.layout.item_list2, null);
					binder = new ViewBinder.Builder((ViewGroup) convertView).titleId(R.id.tv_title)
							.iconImageId(R.id.iv_icon).mainImageId(R.id.iv_banner).briefId(R.id.tv_brief).build();
					convertView.setTag(binder);
				}
			} else {
				if (newsItem.getType() == ItemsControl.NEWS_TYPE) {
					holder = (MyViewHolder) convertView.getTag();
				} else if (newsItem.getType() == ItemsControl.AD_TYPE) {
					binder = (ViewBinder) convertView.getTag();
				}
			}
			if (holder != null) {
				holder.desc.setText(newsItem.getDesc());
				holder.title.setText(newsItem.getTitle());
				holder.putData.setText(newsItem.getPutData());
			} else {
				final NativeAd nativeAd = newsItem.getNativeAd();
				if (nativeAd != null && binder != null) {
					NativeAdData nativeAdData = nativeAd.getNativeAdData();
					if (nativeAdData != null) {
						binder.nativeViewHolder.tv_title.setText(nativeAdData.title);
						binder.nativeViewHolder.tv_brief.setText(nativeAdData.brief);
						ImageLoader.getInstance().displayImage(nativeAdData.iconInfo.imageUrl,
								binder.nativeViewHolder.iv_icon);
						ImageLoader.getInstance().displayImage(nativeAdData.mainInfo.imageUrl,
								binder.nativeViewHolder.iv_main);
						convertView.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								nativeAd.handleAction();
							}
						});
						nativeAd.trackImpression();
					}
				}
			}
			return convertView;
		}
	}

	private static class MyViewHolder {
		TextView desc;
		TextView title;
		TextView putData;
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

	@SuppressLint("SimpleDateFormat")
	public String getCurrentDate() {
		java.text.DateFormat format = new java.text.SimpleDateFormat("MM 年 dd 月 hh:mm");
		return format.format(new Date());
	}

	private void requestRss(final int what) {
		new Thread() {
			public void run() {
				itemsControl.getItems(handler, what);
			}
		}.start();
	}

	@Override
	public void onRefresh() {
		requestRss(MESSAGE_REFRESH_NEWS);
	}

	@Override
	public void onLoadMore() {
		requestRss(MESSAGE_ADD_NEWS);
	}
}
