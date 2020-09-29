package kr.co.core.financial_tech.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import kr.co.core.financial_tech.R;
import kr.co.core.financial_tech.databinding.ActivityFindResultBinding;
import kr.co.core.financial_tech.util.StatusBarUtil;
import kr.co.core.financial_tech.util.StringUtil;

public class FindResultAct extends BaseAct {
    ActivityFindResultBinding binding;
    Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_find_result, null);
        StatusBarUtil.setStatusBarColor(this, StatusBarUtil.StatusBarColorType.WHITE_STATUS_BAR);
        act = this;

        String id = getIntent().getStringExtra("id");
        String pw = getIntent().getStringExtra("pw");

        if(StringUtil.isNull(pw)) {
            binding.areaPw.setVisibility(View.GONE);
            binding.id.setText(id);
        } else {
            binding.title02.setText("회원님의 비밀번호는\n다음과 같습니다.");
            binding.id.setText(id);
            binding.pw.setText(pw);
        }

        binding.btnBack.setOnClickListener(view -> {
            finish();
        });

        binding.btnConfirm.setOnClickListener(view -> {
            finish();
        });
    }
}