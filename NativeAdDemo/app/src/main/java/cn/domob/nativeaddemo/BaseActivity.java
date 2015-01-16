package cn.domob.nativeaddemo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View.OnClickListener;

public abstract class BaseActivity extends Activity implements OnClickListener {

	public static final String ONLINE_PUBID = "56OJyM1ouN5yLUjg8/";
	public static final String ONLINE_NATIVE_PPID1 = "16TLwebvApZlkNUOtpF-7Las";
	public static final String ONLINE_NATIVE_PPID2 = "16TLwebvApZlkNUOtccDl-oi";
	public static final String ONLINE_NATIVE_PPID3 = "16TLwebvApZlkNUOt3Lmuq3z";
	public static final String ONLINE_NATIVE_PPID4 = "16TLwebvApZlkNUOiT4FaRMz";
	public static final String ONLINE_NATIVE_PPID5 = "16TLwebvApZlkNUOiMN3nhJi";

	public static String publisherID = ONLINE_PUBID;
	public static String nativePPID1 = ONLINE_NATIVE_PPID1;
	public static String nativePPID2 = ONLINE_NATIVE_PPID2;
	public static String nativePPID3 = ONLINE_NATIVE_PPID3;
	public static String nativePPID4 = ONLINE_NATIVE_PPID4;
	public static String nativePPID5 = ONLINE_NATIVE_PPID5;
	protected Context appContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appContext = getApplicationContext();
		setContentView(getLayoutId());
		findViewById();
		setListener();
		initData();
	}

	protected abstract int getLayoutId();

	protected abstract void findViewById();

	protected abstract void setListener();

	protected abstract void initData();
	
}
