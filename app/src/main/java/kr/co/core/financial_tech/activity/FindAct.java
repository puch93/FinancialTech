package kr.co.core.financial_tech.activity;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.tabs.TabLayout;

import kr.co.core.financial_tech.R;
import kr.co.core.financial_tech.adapter.FindPagerAdapter;
import kr.co.core.financial_tech.databinding.ActivityFindBinding;

public class FindAct extends BaseAct {
    ActivityFindBinding binding;
    Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_find, null);
        act = this;

        binding.btnBack.setOnClickListener(view -> {
            finish();
        });
        binding.viewPager.setAdapter(new FindPagerAdapter(getSupportFragmentManager()));

        binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));

        binding.tabLayout.getTabAt(0).select();

        // 선택된 탭 텍스트 BOLD 처리
        LinearLayout tabLayout = (LinearLayout) ((ViewGroup) binding.tabLayout.getChildAt(0)).getChildAt(0);
        TextView tabTextView = (TextView) tabLayout.getChildAt(1);
        tabTextView.setTypeface(null, Typeface.BOLD);

        // 선택된 탭 텍스트 BOLD 처리
        LinearLayout tabLayout2 = (LinearLayout) ((ViewGroup) binding.tabLayout.getChildAt(0)).getChildAt(1);
        TextView tabTextView2 = (TextView) tabLayout2.getChildAt(1);
        tabTextView2.setTypeface(null, Typeface.BOLD);

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager.setCurrentItem(tab.getPosition(), true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}