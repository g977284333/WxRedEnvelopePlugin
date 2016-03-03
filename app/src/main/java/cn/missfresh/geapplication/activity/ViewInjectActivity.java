package cn.missfresh.geapplication.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import cn.missfresh.geapplication.R;
import cn.missfresh.geapplication.base.BaseActivity;
import cn.missfresh.geapplication.inject.ViewInjectUtil;
import cn.missfresh.geapplication.inject.annotate.ContentView;
import cn.missfresh.geapplication.inject.annotate.OnClick;
import cn.missfresh.geapplication.inject.annotate.ViewInject;

/**
 * Created by gchen on 16/3/2.
 */
@ContentView(value = R.layout.activity_view_inject)
public class ViewInjectActivity extends BaseActivity {

    @ViewInject(value = R.id.btn_view_inject_one)
    private Button mBtnTwo;
    @ViewInject(value = R.id.btn_view_inject_two)
    private Button mBtnThird;
    @ViewInject(value = R.id.btn_view_inject_third)
    private Button mBtnOne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewInjectUtil.inject(this);
    }

    @OnClick({R.id.btn_view_inject_one, R.id.btn_view_inject_two, R.id.btn_view_inject_third})
    public void clickButtonInvoked(View view) {
        switch (view.getId()) {
            case R.id.btn_view_inject_one:
                showToast("button one clicked");
                break;
            case R.id.btn_view_inject_two:
                showToast("button two clicked");
                break;
            case R.id.btn_view_inject_third:
                showToast("button third clicked");
                break;
            default:
                break;
        }
    }

    public void showToast(String msg) {
        Toast.makeText(ViewInjectActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
}
