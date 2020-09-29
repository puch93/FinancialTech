package kr.co.core.financial_tech.adapter;


import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import kr.co.core.financial_tech.fragment.BaseFrag;
import kr.co.core.financial_tech.fragment.BoardFrag;
import kr.co.core.financial_tech.fragment.Find01Frag;
import kr.co.core.financial_tech.fragment.Find02Frag;


public class BoardPagerAdapter extends FragmentStatePagerAdapter {
    public BoardPagerAdapter(FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @Override
    public Fragment getItem(int i) {
        BoardFrag frag = new BoardFrag();

        switch (i) {
            case 0:
                frag.setType("news", "2");
                break;
            case 1:
                frag.setType("information", "3");
                break;
            case 2:
                frag.setType("case", "4");
                break;
            case 3:
                frag.setType("wise", "5");
                break;
            case 4:
                frag.setType("humor", "6");
                break;
            case 5:
                frag.setType("notice", "1");
                break;
        }

        return (BaseFrag) frag;
    }

    @Override
    public int getCount() {
        return 6;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }
}