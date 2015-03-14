package cn.fanfan.userinfo;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

import cn.fanfan.attentionuser.AttentionUserActivity;
import cn.fanfan.common.AsyncImageGet;
import cn.fanfan.common.Config;
import cn.fanfan.common.FanfanSharedPreferences;
import cn.fanfan.common.GlobalVariables;
import cn.fanfan.common.NetworkState;
import cn.fanfan.common.TipsToast;
import cn.fanfan.main.MainActivity;
import cn.fanfan.main.R;
import cn.fanfan.topic.TopicFragmentActivity;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

//�߼�ʵ�֣���ȡ����Activity���ݵ�ֵ������ֵ��ʼ�����桷�������ȡ���ݡ��������ݲ����õ���Ӧ��bean����������䵽����
public class UserInfoShowActivity extends Activity implements OnClickListener {
	private int haveFrocus = NO;// 1���ѹ�ע ��0δ��ע
	private static final int YES = 1;
	private static final int NO = 0;
	private static TipsToast tipsToast;
	private ImageView iv_avatar;
	private Button bt_focus;
	private TextView tv_username;
	private TextView tv_topic;
	private TextView tv_ifocus_person;
	private TextView tv_focusi_person;
	private TextView tv_thanks;
	private TextView tv_votes;
	private TextView tv_collect;
	private TextView tv_replys;
	private TextView tv_asks;
	private TextView tvSignature;
	private TextView tv_focusi_person_comment, tv_ifocus_person_comment,
			tv_topic_comment;
	private String uid;
	protected String errno;
	protected String err;
	protected String user_name, signature;
	protected String avatar_file = "null";
	protected String fans_count;
	protected String friend_count;
	protected String question_count;
	protected String answer_count;
	protected String topic_focus_count;
	protected String agree_count;
	protected String thanks_count;
	protected String answer_favorite_count;
	private LinearLayout lv_topics, lv_replys, lv_search_friens, lv_news,
			lv_asks, lv_focusi_person, lv_ifocus_person, lv_articles;
	private ProgressBar pb_change_follow;
	private int status;
	private AsyncHttpClient asyncHttpClient;
	private FanfanSharedPreferences ffGetUid;
	private LinearLayout ll_logout;
	private FanfanSharedPreferences sharedPreferences;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_information_show);
		LinearLayout hidePart = (LinearLayout) findViewById(R.id.llHidePart);
		hidePart.setVisibility(View.GONE);
		// ��ӷ��ذ�ť��ActionBar
		ActionBar actionBar = getActionBar();
		actionBar.setIcon(null);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayUseLogoEnabled(false);
		// actionBar.setDisplayShowHomeEnabled(true);
		actionBar.show();
		// Bundle bundle = intent.getExtras();
		// ��ȡ����activity�Ĵ�������ֵ��
		Intent intent = this.getIntent();
		uid = intent.getStringExtra("uid");
		status = intent
				.getIntExtra("status", GlobalVariables.DISAVAILABLE_EDIT);
		// �ж�UID�ǲ��Ǳ������ѵ�¼�û�������ǿ��Ա༭�����ع�ע��ť���������ر༭��ť��ʾ��ע��ť��
		ffGetUid = new FanfanSharedPreferences(this);
		if (uid.equals(ffGetUid.getUid(""))) {
			status = GlobalVariables.AVAILABLE_EDIT;
		}
		init();// ��ʼ������
		// ��ȡ����״̬����������״̬����
		if (uid != null) {
			NetworkState networkState = new NetworkState();
			if (networkState.isNetworkConnected(UserInfoShowActivity.this)) {
				getUserInfo();
			} else {
				showTips(R.drawable.tips_error, R.string.net_notconnect);
			}

		}
	}

	/**
	 * @param context
	 *            ����Ҫ������activity
	 * @param uid
	 *            ��Ҫ�鿴��Ϣ���û�uid��
	 * @param status
	 *            �Ƿ��Ǳ����ѵ�¼�û�
	 */
	public static void actionStar(Context context, String uid) {
		Intent mIntent = new Intent(context, UserInfoShowActivity.class);
		mIntent.putExtra("uid", uid);
		context.startActivity(mIntent);
	}

	// ��ʼ������
	private void init() {
		// TODO Auto-generated method stub
		iv_avatar = (ImageView) findViewById(R.id.iv_avatar);
		tv_username = (TextView) findViewById(R.id.tv_username);
		lv_topics = (LinearLayout) findViewById(R.id.lv_topics);
		lv_topics.setOnClickListener(this);
		tv_topic = (TextView) findViewById(R.id.tv_topic);
		lv_ifocus_person = (LinearLayout) findViewById(R.id.lv_ifocus_person);
		lv_ifocus_person.setOnClickListener(this);
		tv_ifocus_person = (TextView) findViewById(R.id.tv_ifocus_person);
		lv_focusi_person = (LinearLayout) findViewById(R.id.lv_focusi_person);
		lv_focusi_person.setOnClickListener(this);
		tv_focusi_person = (TextView) findViewById(R.id.tv_focusi_person);

		tv_thanks = (TextView) findViewById(R.id.tv_thanks);
		tv_votes = (TextView) findViewById(R.id.tv_votes);
		tv_collect = (TextView) findViewById(R.id.tv_collect);

		lv_replys = (LinearLayout) findViewById(R.id.lv_replys);
		lv_replys.setOnClickListener(this);
		tv_replys = (TextView) findViewById(R.id.tv_replys);
		lv_asks = (LinearLayout) findViewById(R.id.lv_asks);
		lv_asks.setOnClickListener(this);
		tv_asks = (TextView) findViewById(R.id.tv_asks);
		lv_articles = (LinearLayout) findViewById(R.id.lv_articles);
		lv_articles.setOnClickListener(this);

		lv_news = (LinearLayout) findViewById(R.id.lv_news);
		lv_news.setOnClickListener(this);

		lv_search_friens = (LinearLayout) findViewById(R.id.lv_search_friens);
		lv_search_friens.setOnClickListener(this);

		bt_focus = (Button) findViewById(R.id.bt_focus);
		bt_focus.setOnClickListener(this);
		tv_focusi_person_comment = (TextView) findViewById(R.id.tv_focusi_person_comment);
		tv_ifocus_person_comment = (TextView) findViewById(R.id.tv_ifocus_person_comment);
		tv_topic_comment = (TextView) findViewById(R.id.tv_topic_comment);
		tvSignature = (TextView) findViewById(R.id.tvSignature);
		pb_change_follow = (ProgressBar) findViewById(R.id.pb_change_follow);
		ll_logout = (LinearLayout) findViewById(R.id.ll_logout);
		ll_logout.setOnClickListener(this);
		// �жϱ������ѵ�¼�û�������ǿ��Ա༭�����ع�ע��ť���������ر༭��ť��ʾ��ע��ť��
		if (status == GlobalVariables.AVAILABLE_EDIT) {
			bt_focus.setVisibility(View.INVISIBLE);
			ll_logout.setVisibility(View.VISIBLE);
		} else {
			ll_logout.setVisibility(View.GONE);
			tv_focusi_person_comment.setText("��ע������");
			tv_ifocus_person_comment.setText("����ע����");
			tv_topic_comment.setText("����ע�Ļ���");
		}
		if (haveFrocus == YES) {
			bt_focus.setBackgroundResource(R.drawable.btn_silver_normal);
			bt_focus.setTextColor(android.graphics.Color.BLACK);
			bt_focus.setText("ȡ����ע");
		}
	}

	// ��ȡ�û�����,������
	private void getUserInfo() {
		// TODO Auto-generated method stub
		AsyncHttpClient getUserInfo = new AsyncHttpClient();
		PersistentCookieStore mCookieStore = new PersistentCookieStore(this);
		getUserInfo.setCookieStore(mCookieStore);

		RequestParams params = new RequestParams();
		params.put("uid", uid);
		getUserInfo.get(Config.getValue("UserInfoUrl"), params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						// TODO Auto-generated method stub
						// get����ɹ�����json��
						String responseContent = new String(responseBody);
						JSONTokener jsonParser = new JSONTokener(
								responseContent);
						try {
							JSONObject result = (JSONObject) jsonParser
									.nextValue();
							errno = result.getString("errno");
							err = result.getString("err");
							JSONObject rsm = new JSONObject();
							rsm = result.getJSONObject("rsm");
							JSONTokener jsonParser2 = new JSONTokener(rsm
									.toString());
							JSONObject rsmcontent = (JSONObject) jsonParser2
									.nextValue();
							user_name = rsmcontent.getString("user_name");
							signature = rsmcontent.getString("signature");
							avatar_file = rsmcontent.getString("avatar_file");
							fans_count = rsmcontent.getString("fans_count");
							friend_count = rsmcontent.getString("friend_count");
							question_count = rsmcontent
									.getString("question_count");
							topic_focus_count = rsmcontent
									.getString("topic_focus_count");
							agree_count = rsmcontent.getString("agree_count");
							thanks_count = rsmcontent.getString("thanks_count");
							answer_favorite_count = rsmcontent
									.getString("answer_favorite_count");
							answer_count = rsmcontent.getString("answer_count");
							haveFrocus = rsmcontent.getInt("has_focus");
							if (haveFrocus == YES) {
								bt_focus.setBackgroundResource(R.drawable.btn_silver_normal);
								bt_focus.setTextColor(android.graphics.Color.BLACK);
								bt_focus.setText("ȡ����ע");
							} else {
								bt_focus.setBackgroundResource(R.drawable.btn_green_normal);
								bt_focus.setTextColor(android.graphics.Color.WHITE);
								bt_focus.setText("��ע");
							}
							// ������JSON��updateUIչʾ�û�����
							updateUI(avatar_file);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							// showTips(R.drawable.tips_error,
							// R.string.net_break);
							Toast.makeText(UserInfoShowActivity.this,
									"���粻�ã������ԣ�", Toast.LENGTH_LONG).show();

						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
						// TODO Auto-generated method stub
						showTips(R.drawable.tips_error, R.string.get_user_info);
					}
				});
	}

	// ��ȡ���ݲ���������½���
	protected void updateUI(String avatarurl) {
		// TODO Auto-generated method stub
		tv_username.setText(user_name);
		tv_focusi_person.setText(fans_count);
		tv_ifocus_person.setText(friend_count);
		tv_topic.setText(topic_focus_count);
		tv_votes.setText(agree_count);
		tv_thanks.setText(thanks_count);
		tv_collect.setText(answer_favorite_count);
		tv_replys.setText(answer_count);
		tv_asks.setText(question_count);
		if (!signature.equals("false")) {
			tvSignature.setText(signature);
		}

		// �����û�ͷ��
		if ((avatarurl != null) && (!avatarurl.equals(""))) {
			AsyncImageGet getAvatar = new AsyncImageGet(
					Config.getValue("AvatarPrefixUrl") + avatarurl, iv_avatar);
			getAvatar.execute();
		} else {
			iv_avatar.setImageResource(R.drawable.ic_avatar_default);
		}
	}

	// ����������Ԫ�صļ����¼��Ĵ���
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ll_logout:
			// �˳���¼
			sharedPreferences = new FanfanSharedPreferences(
					UserInfoShowActivity.this);
			sharedPreferences.clear();
			PersistentCookieStore cookieStore = new PersistentCookieStore(
					UserInfoShowActivity.this);
			cookieStore.clear();
			Intent mainIntent = new Intent(this, MainActivity.class);
			mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(mainIntent);
			break;
		case R.id.lv_topics:
			Intent intent = new Intent(UserInfoShowActivity.this,
					TopicFragmentActivity.class);
			intent.putExtra("uid", uid);
			startActivity(intent);
			break;
		case R.id.lv_focusi_person:
			Intent intent2 = new Intent(UserInfoShowActivity.this,
					AttentionUserActivity.class);
			intent2.putExtra("userorme", GlobalVariables.ATTENEION_ME);
			intent2.putExtra("uid", uid);
			startActivity(intent2);
			break;
		case R.id.lv_ifocus_person:
			Intent intent1 = new Intent(UserInfoShowActivity.this,
					AttentionUserActivity.class);
			intent1.putExtra("uid", uid);
			intent1.putExtra("userorme", GlobalVariables.ATTENTION_USER);
			startActivity(intent1);
			break;
		case R.id.lv_articles:
			Intent intent3 = new Intent(UserInfoShowActivity.this,
					ArticleActivity.class);
			intent3.putExtra("isArticle", GlobalVariables.ARTICLE);
			intent3.putExtra("uid", uid);
			startActivity(intent3);
			break;
		case R.id.lv_asks:
			Intent intent4 = new Intent(UserInfoShowActivity.this,
					ArticleActivity.class);
			intent4.putExtra("isArticle", GlobalVariables.QUESTION);
			intent4.putExtra("uid", uid);
			startActivity(intent4);
			break;
		case R.id.lv_news:
			Toast.makeText(UserInfoShowActivity.this, "lv_news",
					Toast.LENGTH_SHORT).show();
			break;
		case R.id.lv_search_friens:
			Toast.makeText(UserInfoShowActivity.this, "lv_search_friens",
					Toast.LENGTH_SHORT).show();
			break;
		case R.id.lv_replys:
			Intent intent5 = new Intent(UserInfoShowActivity.this,
					MyAnswerActivity.class);
			intent5.putExtra("uid", uid);
			startActivity(intent5);
			break;
		case R.id.bt_focus:
			if (haveFrocus == YES) {
				haveFrocus = NO;
				bt_focus.setBackgroundResource(R.drawable.btn_green_normal);
				bt_focus.setTextColor(android.graphics.Color.WHITE);
				bt_focus.setText("��ע");
			} else {
				haveFrocus = YES;
				bt_focus.setBackgroundResource(R.drawable.btn_silver_normal);
				bt_focus.setTextColor(android.graphics.Color.BLACK);
				bt_focus.setText("ȡ����ע");
			}
			pb_change_follow.setVisibility(View.VISIBLE);
			changeFrocusStatus();
			bt_focus.setClickable(false);
			break;
		default:
			break;
		}
	}

	private void changeFrocusStatus() {
		// TODO Auto-generated method stub
		asyncHttpClient = new AsyncHttpClient();
		PersistentCookieStore mCookieStore = new PersistentCookieStore(this);
		asyncHttpClient.setCookieStore(mCookieStore);
		RequestParams followStatus = new RequestParams();
		// ���͹�ע״̬�����ʧ�������û���������frocus��ť���״̬
		followStatus.put("uid", uid);// ��Ҫȡ����ע��uid
		asyncHttpClient.get(Config.getValue("ChangeFollowStatus"),
				followStatus, new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int arg0, Header[] arg1,
							byte[] responseBody) {
						// TODO Auto-generated method stub
						String responseContent = new String(responseBody);
						bt_focus.setClickable(true);
						pb_change_follow.setVisibility(View.GONE);
					}

					@Override
					public void onFailure(int arg0, Header[] arg1,
							byte[] responseBody, Throwable arg3) {
						// TODO Auto-generated method stub
						String responseContent = new String(responseBody);
						Toast.makeText(UserInfoShowActivity.this,
								responseContent + "��עʧ�ܣ������ԣ�",
								Toast.LENGTH_SHORT).show();
						// ���İ�ť״̬
						bt_focus.setClickable(true);
						if (haveFrocus == YES) {
							haveFrocus = NO;
							bt_focus.setBackgroundResource(R.drawable.btn_green_normal);
							bt_focus.setTextColor(android.graphics.Color.WHITE);
							bt_focus.setText("��ע");
						} else {
							haveFrocus = YES;
							bt_focus.setBackgroundResource(R.drawable.btn_silver_normal);
							bt_focus.setTextColor(android.graphics.Color.BLACK);
							bt_focus.setText("ȡ����ע");
						}
					}
				});
	}

	// ��ʾģ��
	private void showTips(int iconResId, int msgResId) {
		if (tipsToast != null) {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				tipsToast.cancel();
			}
		} else {
			tipsToast = TipsToast.makeText(getApplication().getBaseContext(),
					msgResId, TipsToast.LENGTH_SHORT);
		}
		tipsToast.show();
		tipsToast.setIcon(iconResId);
		tipsToast.setText(msgResId);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	// ���·���ʱ�ٴλ�ȡ�û���Ϣ
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		if (uid != null) {
			NetworkState networkState = new NetworkState();
			if (networkState.isNetworkConnected(UserInfoShowActivity.this)) {
				getUserInfo();
			} else {
				showTips(R.drawable.tips_error, R.string.net_break);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (status == GlobalVariables.AVAILABLE_EDIT) {
			getMenuInflater().inflate(R.menu.userinforedit, menu);
		}

		return super.onCreateOptionsMenu(menu);
	}

	// ��ActinBar�༭��ť�Ĵ���
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.edit) {
			Intent intent = new Intent(UserInfoShowActivity.this,
					UserInfoEditActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("uid", uid);
			bundle.putString("avatar_file", avatar_file);
			intent.putExtras(bundle);
			startActivity(intent);
			return true;
		}
		if (id == android.R.id.home) {
			this.finish();
		}
		return super.onOptionsItemSelected(item);
	}
}
