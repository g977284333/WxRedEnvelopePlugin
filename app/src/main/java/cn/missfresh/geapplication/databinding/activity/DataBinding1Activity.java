package cn.missfresh.geapplication.databinding.activity;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;

import cn.missfresh.geapplication.R;
import cn.missfresh.geapplication.base.BaseActivity;

/**
 * Created by gchen on 16/2/25.
 */
public class DataBinding1Activity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewDataBinding viewDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_data_binding_one);

    }
}
