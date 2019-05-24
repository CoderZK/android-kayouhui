package com.kyh.app.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.BridgeWebViewClient;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.github.lzyzsd.jsbridge.DefaultHandler;
import com.kyh.app.R;
import com.kyh.app.config.SystemConfigSp;
import com.kyh.app.jsbridge.CustomWebViewClient;
import com.kyh.app.utils.ImageUtil;
import com.kyh.app.utils.NotificationsUtils;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private BridgeWebView webView;
    private long firstTime = 0;
    private String content;

    private ImageView mMove, mBack, mMain;
    private LinearLayout mFloatView;

    private int width,heigth,parentWidth,parentHeight;

    private int lastX,lastY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PushAgent.getInstance(this).onAppStart();
        initView();
        checkPemission();
        getMainName();
        dealWithNotify();
        checkNotifyPermission();
    }


    private void dealWithNotify () {
        PushAgent mPushAgent = PushAgent.getInstance(this);
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                String content = msg.custom;
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("url", content);
                startActivity(intent);

            }
        };
        mPushAgent.setNotificationClickHandler(notificationClickHandler);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        webView = findViewById(R.id.webView);
        webView.setDefaultHandler(new MainActivity.SocialHandlerCallBack());
        webView.setWebViewClient(new CustomWebViewClient(this));
        webView.addJavascriptInterface(new JavaScriptinterface(this),
                "android");

        mFloatView = findViewById(R.id.id_float_view);
        mBack = findViewById(R.id.id_back);
        mMain = findViewById(R.id.id_main);
        mMove = findViewById(R.id.id_float);
        mMain.setOnClickListener(this);
        mBack.setOnClickListener(this);

        Display display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        heigth = display.getHeight();

        parentWidth = mFloatView.getMeasuredWidth();
        parentHeight = mFloatView.getMeasuredHeight();



        ViewTreeObserver vto = mFloatView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){
            @Override
            public void onGlobalLayout() {
                parentHeight =mFloatView.getHeight();
                parentWidth =mFloatView.getWidth();
                mFloatView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mFloatView.getLayoutParams();
                params.setMargins(width-parentWidth, heigth-parentHeight-dip2px(70), 0, 0);
                mFloatView.setLayoutParams(params);
            }
        });
        mMove.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                //获取到手指处的横坐标和纵坐标
                Log.d("--------------", "screenWidth: " + width + ", screenHeight:" + heigth + ", parentWidth:" + parentWidth + ", parentHeight:"+parentHeight);
                int x = (int) event.getX();
                int y = (int) event.getY();
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        lastX = x;
                        lastY = y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //计算移动的距离
                        int offX = x - lastX;
                        int offY = y - lastY;
                        RelativeLayout.MarginLayoutParams mlp =
                                (RelativeLayout.MarginLayoutParams) mFloatView.getLayoutParams();
                        int leftMargin = mFloatView.getLeft()+offX;
                        int topMargin = mFloatView.getTop()+offY;


                        if (leftMargin <= 0) {
                            leftMargin = 0;
                        }

                        if (topMargin <= 0) {
                            topMargin = 0;
                        }

                        if (parentWidth + leftMargin >= width) {
                            leftMargin = width - parentWidth;
                        }

                        if (parentHeight + topMargin >= heigth-dip2px(70)) {
                            topMargin = heigth - parentHeight - dip2px(70);
                        }

                        mlp.leftMargin = leftMargin;
                        mlp.topMargin = topMargin;
                        Log.d("move----", "offX: " + offX+ ", offY:" + offY + "，leftMargin："+(mFloatView.getLeft()+offX) + ", topMargin:" +(mFloatView.getTop()+offY));
                        mFloatView.setLayoutParams(mlp);
                        break;
                }
                return true;
            }
        });

        loadUrl(getIntent());
    }



    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("MainActivity", "onNewIntent: ");

        loadUrl(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("MainActivity", "onResume: ");
    }

    private void checkPemission() {
        if (Build.VERSION.SDK_INT >= 23) {
            String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_LOGS,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.SET_DEBUG_APP,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.GET_ACCOUNTS,
                    Manifest.permission.WRITE_APN_SETTINGS};
            ActivityCompat.requestPermissions(this, mPermissionList, 123);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
    }

    /**
     * QQ与新浪不需要添加Activity，但需要在使用QQ分享或者授权的Activity中
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    private void shareUrl(String link, String desc, String icon, String title) {
        UMImage image = new UMImage(MainActivity.this, icon);//资源文件
        UMWeb web = new UMWeb(link);
        web.setTitle(title);//标题
        web.setThumb(image);  //缩略图
        if (!TextUtils.isEmpty(desc)) {
            web.setDescription(desc);//描述
        } else {
            web.setDescription(" ");//描述
        }
        new ShareAction(MainActivity.this)
                .withMedia(web)
                .setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.SINA, SHARE_MEDIA.QQ)
                .setCallback(shareListener).open();
    }

    private void shareImage(String imageUrl, String title) {
        UMImage image = new UMImage(MainActivity.this, imageUrl);//资源文件
        new ShareAction(MainActivity.this)
                .withText(title)
                .withMedia(image)
                .setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.SINA, SHARE_MEDIA.QQ)
                .setCallback(shareListener).open();
    }

    private UMShareListener shareListener = new UMShareListener() {
        /**
         * @descrption 分享开始的回调
         * @param platform 平台类型
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        /**
         * @descrption 分享成功的回调
         * @param platform 平台类型
         */
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(MainActivity.this, "分享成功", Toast.LENGTH_LONG).show();
        }

        /**
         * @descrption 分享失败的回调
         * @param platform 平台类型
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(MainActivity.this, "失败" + t.getMessage(), Toast.LENGTH_LONG).show();
        }

        /**
         * @descrption 分享取消的回调
         * @param platform 平台类型
         */
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(MainActivity.this, "已取消分享", Toast.LENGTH_LONG).show();
        }
    };

    private String getMainName() {
        String packageName = getApplicationContext().getPackageName();
        return packageName;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadUrl(Intent intent) {
        content = intent.getStringExtra("url");

        String ua = webView.getSettings().getUserAgentString();
        webView.getSettings().setUserAgentString(ua + "/kyhandroid/CK 2.0");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        //从网上搜索，这似乎是因为硬件加速canvas渲染不支持Chromium WebView，
        // 如果还是不行的话 我觉得只能修改网页body标签的css样式 让背景为白色了
        if (!TextUtils.isEmpty(content)) {
            webView.loadUrl(content);
        } else {
            webView.loadUrl("http://gzh.dkyapp.com/app/index.php?i=2&c=entry&m=ewei_shopv2&do=mobile&r=diypage&id=5");
        }
    }

    /**
     * 复制文本到粘贴板
     *
     * @param text
     */
    public void onClickCopy(String text) {
        // 从API11开始android推荐使用android.content.ClipboardManager
        // 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText(text);
        Toast.makeText(this, "已复制到粘贴板", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.id_back:
                webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
                webView.goBack();
                break;
            case R.id.id_float:
                break;
            case R.id.id_main:
                webView.loadUrl("http://gzh.dkyapp.com/app/index.php?i=2&c=entry&m=ewei_shopv2&do=mobile&r=diypage&id=5");
                break;
        }
    }

    class SocialWebViewClient extends BridgeWebViewClient {
        public SocialWebViewClient(BridgeWebView webView) {
            super(webView);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(url.startsWith("url:")) {
                url = url.substring(4, url.length());
                Intent intent= new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(url);
                intent.setData(content_url);
                startActivity(intent);
            }
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    class SocialHandlerCallBack extends DefaultHandler {
        @Override
        public void handler(String data, CallBackFunction function) {
            if (function != null) {
                Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class JavaScriptinterface {
        Context context;

        public JavaScriptinterface(Context c) {
            context = c;
        }

        /**
         * 与js交互时用到的方法，在js里直接调用的
         */
        @JavascriptInterface
        public String androiddevicetoken() {
            String deviceToken = SystemConfigSp.instance().getStrConfig(SystemConfigSp.SysCfgDimension.deviceToken);
            return deviceToken;
        }

        @JavascriptInterface
        public void shareAction(String title, String desc, String imgUrl, String link) {
            if (ImageUtil.isImage(link)) {
                shareImage(link, title);
            } else {
                shareUrl(link, desc, imgUrl, title);
            }
        }

        @JavascriptInterface
        public void copyurl(String url) {
            onClickCopy(url);
        }
    }

    @Override
    public void onBackPressed() {
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime > 2000) {
            Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            firstTime = secondTime;
        } else {
            System.exit(0);
        }
    }


    private void checkNotifyPermission () {
        if (!NotificationsUtils.isNotificationEnabled(this)) {
            new AlertDialog.Builder(this)
                    .setMessage("检测到您没有打开通知权限，是否去打开?")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent localIntent = new Intent();
                            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                localIntent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                                localIntent.putExtra(Settings.EXTRA_APP_PACKAGE, MainActivity.this.getPackageName());
                            } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                localIntent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                                localIntent.putExtra("app_package", MainActivity.this.getPackageName());
                                localIntent.putExtra("app_uid",  MainActivity.this.getApplicationInfo().uid);
                            } else if (Build.VERSION.SDK_INT >= 9) {
                                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                localIntent.setData(Uri.fromParts("package", MainActivity.this.getPackageName(), null));
                            } else if (Build.VERSION.SDK_INT <= 8) {
                                localIntent.setAction(Intent.ACTION_VIEW);
                                localIntent.setClassName("com.android.settings",
                                        "com.android.settings.InstalledAppDetails");
                                localIntent.putExtra("com.android.settings.ApplicationPkgName",
                                        MainActivity.this.getPackageName());
                            }
                            startActivity(localIntent);
                        }
                    })
                    .create().show();
        }
    }

    private  int dip2px(int dp)
    {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp*density+0.5);
    }

    /** px转换dip */
    private int px2dip(int px) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }
}
