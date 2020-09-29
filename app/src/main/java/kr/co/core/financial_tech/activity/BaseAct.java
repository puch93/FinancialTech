package kr.co.core.financial_tech.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import kr.co.core.financial_tech.util.StatusBarUtil;

public class BaseAct extends AppCompatActivity {
    Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        binding = DataBindingUtil.setContentView(this, R.layout., null);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        StatusBarUtil.setStatusBarColor(this, StatusBarUtil.StatusBarColorType.WHITE_STATUS_BAR);
        act = this;

    }
}