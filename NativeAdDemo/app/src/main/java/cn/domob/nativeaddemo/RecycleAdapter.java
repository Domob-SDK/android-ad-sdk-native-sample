package cn.domob.nativeaddemo;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import cn.domob.android.nativeads.NativeAd;
import cn.domob.android.nativeads.NativeAd.NativeAdDataLoadListener;
import cn.domob.nativeaddemo.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.DemoViewHolder> {

	private Activity context;
	private String[] picUrls;
	private SparseArray<NativeAd> nativeAds = new SparseArray<NativeAd>();

	public RecycleAdapter(Activity context, String[] picUrls) {
		this.context = context;
		this.picUrls = picUrls;
	}

	@Override
	public DemoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		// 加载布局
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycle, parent, false);

		return new DemoViewHolder(view);
	}

	private void loadNativeAd(ImageView imageView, int position) {
		new NativeAd(context, MainActivity.publisherID, MainActivity.nativePPID4, new MyNativeAdDataLoadListener(
				imageView, position)).loadNativeAd();
	}

	public class MyNativeAdDataLoadListener implements NativeAdDataLoadListener {

		ImageView imageView;
		int position;

		public MyNativeAdDataLoadListener(ImageView imageView, int position) {
			this.imageView = imageView;
			this.position = position;
		}

		@Override
		public void onLoadSuccess(final NativeAd nativeAd) {
			nativeAds.put(position, nativeAd);
			// 判断当前imageView复用的类型是否一致
			if ((Boolean) imageView.getTag()) {
				return;
			}
			// 绑定数据
			imageView.setImageResource(R.drawable.domob_icon);
			ImageLoader.getInstance().displayImage(nativeAd.nativeAdData.mainInfo.imageUrl, imageView);
			imageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					nativeAd.handleAction();
				}
			});
			// 发现展现报告
			nativeAd.trackImpression();
		}

		@Override
		public void onLoadFailer(String errorMessage) {
			Log.e("DomobSDKDemo", errorMessage);
		}
	}

	@Override
	public void onBindViewHolder(final DemoViewHolder holder, final int position) {
		if (isNativeAd(position)) {
			holder.imageView.setTag(false);
			if (nativeAds.get(position) == null) {
				loadNativeAd(holder.imageView, position);
			} else {
				final NativeAd nativeAd = nativeAds.get(position);
				// 绑定数据
				holder.imageView.setImageResource(R.drawable.domob_item2);
				ImageLoader.getInstance().displayImage(nativeAd.nativeAdData.mainInfo.imageUrl, holder.imageView);
				holder.imageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						nativeAd.handleAction();
					}
				});
				nativeAd.trackImpression();
			}
		} else {
			holder.imageView.setTag(true);
			holder.imageView.setImageResource(R.drawable.domob_item2);
			holder.imageView.setOnClickListener(null);
			ImageLoader.getInstance().displayImage(picUrls[position], holder.imageView);
		}
	}
	// 设置规则是否显示NativeAd
	private boolean isNativeAd(final int position) {
		return position == 4 || position != 0 && position % 10 == 0;
	}

	@Override
	public int getItemCount() {
		// 返回数据有多少条
		if (null == picUrls) {
			return 0;
		}
		return picUrls.length;
	}

	public static class DemoViewHolder extends RecyclerView.ViewHolder {
		ImageView imageView;

		public DemoViewHolder(View itemView) {
			super(itemView);
			imageView = (ImageView) itemView.findViewById(R.id.imageview);
		}
	}

	public void clear() {
		nativeAds.clear();
	}
}
