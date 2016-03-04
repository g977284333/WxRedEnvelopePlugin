package cn.missfresh.geapplication.activity;

/**
 * Created by gchen on 16/3/3.
 */

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import cn.missfresh.geapplication.R;
import cn.missfresh.geapplication.base.BaseActivity;
import cn.missfresh.geapplication.inject.ViewInjectUtil;
import cn.missfresh.geapplication.inject.annotate.ContentView;
import cn.missfresh.geapplication.inject.annotate.OnClick;
import cn.missfresh.geapplication.inject.annotate.ViewInject;

@ContentView(R.layout.activity_wx_hb_plugin)
public class WxHongBaoPluginActivity extends BaseActivity {


    @ViewInject(R.id.tv_plugin_service_state)
    private TextView mTvPluginServiceState;

    @ViewInject(R.id.btn_start_wx_plugin_service)
    private Button mBtnStartWxPluginSerivce;

    @OnClick({R.id.btn_start_wx_plugin_service})
    public void clickButtonInvoked(View view) {
        switch (view.getId()) {
            case R.id.btn_start_wx_plugin_service:
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewInjectUtil.inject(this);
    }

    @Override
    protected void onResume() {
        togglePulginState();
        super.onResume();

    }

    private void togglePulginState() {
        boolean isPluginRunning = false;
        // 遍历获取允许的Service的名称
        AccessibilityManager am = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> accessibilityServiceInfos = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo accessibilityServiceInfo : accessibilityServiceInfos) {
            if (accessibilityServiceInfo.getId().equals(this.getPackageName() + "/.WxPluginService")) {
                isPluginRunning = true;
            }
        }

        if (isPluginRunning) {
            mTvPluginServiceState.setText("关闭插件");
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            mTvPluginServiceState.setText("开启插件");
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }
}
