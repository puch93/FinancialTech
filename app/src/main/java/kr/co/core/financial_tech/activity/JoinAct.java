package kr.co.core.financial_tech.activity;

import android.app.Activity;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import kr.co.core.financial_tech.R;
import kr.co.core.financial_tech.databinding.ActivityJoinBinding;
import kr.co.core.financial_tech.fragment.BaseFrag;
import kr.co.core.financial_tech.fragment.Join01Frag;

public class JoinAct extends BaseAct {
    ActivityJoinBinding binding;
    public static Activity act;

    public static FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_join, null);
        act = this;

        fragmentManager = getSupportFragmentManager();

        changeProcessState(1);

        replaceFragment(new Join01Frag(), 1);

        binding.btnClose.setOnClickListener(view -> {
            finish();
        });
    }

    public void changeProcessState(int i) {
        binding.areaProcess01.setSelected(false);
        binding.areaProcess02.setSelected(false);
        binding.areaProcess03.setSelected(false);

        switch (i) {
            case 1:
                binding.areaProcess01.setSelected(true);
                break;
            case 2:
                binding.areaProcess02.setSelected(true);
                break;
            case 3:
                binding.areaProcess03.setSelected(true);
                break;
        }
    }

    public void replaceFragment(BaseFrag frag, int i) {
        changeProcessState(i);

        /* replace fragment */
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (!(frag instanceof Join01Frag)) {
            transaction.setCustomAnimations(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left, R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
        }
        transaction.replace(R.id.replace_area, frag);
//        transaction.commit();
        transaction.commitAllowingStateLoss();
    }
}