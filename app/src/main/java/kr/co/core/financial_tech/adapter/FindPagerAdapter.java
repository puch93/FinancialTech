package kr.co.core.financial_tech.adapter;


import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import kr.co.core.financial_tech.fragment.BaseFrag;
import kr.co.core.financial_tech.fragment.Find01Frag;
import kr.co.core.financial_tech.fragment.Find02Frag;


public class FindPagerAdapter extends FragmentStatePagerAdapter {
    public FindPagerAdapter(FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @Override
    public Fragment getItem(int i) {
        BaseFrag frag;
        if(i == 0) {
            frag = new Find01Frag();
        } else {
            frag = new Find02Frag();
        }
        return frag;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }
}