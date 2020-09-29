package kr.co.core.financial_tech.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;

import com.google.android.material.tabs.TabLayout;

import kr.co.core.financial_tech.R;
import kr.co.core.financial_tech.adapter.BoardPagerAdapter;
import kr.co.core.financial_tech.databinding.ActivityBoardBinding;
import kr.co.core.financial_tech.util.AppPreference;
import kr.co.core.financial_tech.util.Common;
import kr.co.core.financial_tech.util.StatusBarUtil;
import kr.co.core.financial_tech.util.StringUtil;

public class BoardAct extends BaseAct implements View.OnClickListener {
    ActivityBoardBinding binding;
    public static Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_board, null);
        StatusBarUtil.setStatusBarColor(this, StatusBarUtil.StatusBarColorType.WHITE_STATUS_BAR);
        act = this;

        if (AppPreference.getPrefBoolean(act, AppPreference.LOGIN_STATE)) {
            int position = getIntent().getIntExtra("type", -1);
            if (position == -1) {
                Common.showToast(act, "일시적인 오류입니다.");
                finish();
            } else {
                Log.i(StringUtil.TAG, "tab position: " + position);
            }


            binding.btnDrawer.setOnClickListener(this);
            binding.btnHome.setOnClickListener(this);


            binding.viewPager.setAdapter(new BoardPagerAdapter(getSupportFragmentManager()));
            binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));
            binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    binding.viewPager.setCurrentItem(tab.getPosition(), true);
                    binding.title.setText(getTitle(tab.getPosition()));
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

            binding.viewPager.setCurrentItem(position);

            for (int i = 0; i < 6; i++) {
                setTabLayoutText(i);
            }

            setDrawerLayout();
        } else {
            Common.showToast(act, "로그인 후 이용 가능합니다.");
            Intent intent = new Intent(act, LoginAct.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    private String getTitle(int position) {
        switch (position) {
            case 0:
                return "재테크 뉴스";
            case 1:
                return "재테크 정보";
            case 2:
                return "성공 사례";
            case 3:
                return "성공 명언";
            case 4:
                return "힐링 유머";
            case 5:
                return "공지사항";
            default:
                return "재테크 뉴스";
        }
    }


    private void setDrawerLayout() {
        /* set click listener drawer */
        binding.layoutDrawer.drawerAllArea.setOnClickListener(this);
        binding.layoutDrawer.btnDrawerMenuNews.setOnClickListener(this);
        binding.layoutDrawer.btnDrawerMenuInfo.setOnClickListener(this);
        binding.layoutDrawer.btnDrawerMenuCase.setOnClickListener(this);
        binding.layoutDrawer.btnDrawerMenuWise.setOnClickListener(this);
        binding.layoutDrawer.btnDrawerMenuHumor.setOnClickListener(this);
        binding.layoutDrawer.btnDrawerMenuNotice.setOnClickListener(this);

        binding.layoutDrawer.btnDrawerMenuLogin.setOnClickListener(this);
        binding.layoutDrawer.btnDrawerMenuJoin.setOnClickListener(this);
        binding.layoutDrawer.btnDrawerMenuConsult01.setOnClickListener(this);

        binding.layoutDrawer.btnDrawerMenuLogout.setOnClickListener(this);
        binding.layoutDrawer.btnDrawerMenuMyInfo.setOnClickListener(this);
        binding.layoutDrawer.btnDrawerMenuConsult02.setOnClickListener(this);

        /* set tag */
        binding.layoutDrawer.btnDrawerMenuNews.setTag(0);
        binding.layoutDrawer.btnDrawerMenuInfo.setTag(1);
        binding.layoutDrawer.btnDrawerMenuCase.setTag(2);
        binding.layoutDrawer.btnDrawerMenuWise.setTag(3);
        binding.layoutDrawer.btnDrawerMenuHumor.setTag(4);
        binding.layoutDrawer.btnDrawerMenuNotice.setTag(5);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /* set login state drawer */
        if (AppPreference.getPrefBoolean(act, AppPreference.LOGIN_STATE)) {
            binding.layoutDrawer.areaLoginN01.setVisibility(View.GONE);
            binding.layoutDrawer.areaLoginN02.setVisibility(View.GONE);
            binding.layoutDrawer.areaLoginY01.setVisibility(View.VISIBLE);
            binding.layoutDrawer.areaLoginY02.setVisibility(View.VISIBLE);

            binding.layoutDrawer.topNick.setText(AppPreference.getPrefString(act, AppPreference.PREF_NICK));
            binding.layoutDrawer.topId.setText(AppPreference.getPrefString(act, AppPreference.PREF_ID));
        } else {
            binding.layoutDrawer.areaLoginN01.setVisibility(View.VISIBLE);
            binding.layoutDrawer.areaLoginN02.setVisibility(View.VISIBLE);
            binding.layoutDrawer.areaLoginY01.setVisibility(View.GONE);
            binding.layoutDrawer.areaLoginY02.setVisibility(View.GONE);
        }
    }

    private void setTabLayoutText(int position) {
        // 선택된 탭 텍스트 BOLD 처리
        LinearLayout tabLayout = (LinearLayout) ((ViewGroup) binding.tabLayout.getChildAt(0)).getChildAt(position);
        TextView tabTextView = (TextView) tabLayout.getChildAt(1);
        tabTextView.setTypeface(null, Typeface.BOLD);
    }

    @Override
    public void onBackPressed() {
        if (!binding.drawerLayout.isDrawerOpen(Gravity.LEFT))
            super.onBackPressed();
        else
            binding.drawerLayout.closeDrawer(Gravity.LEFT);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.btn_drawer:
                binding.drawerLayout.openDrawer(Gravity.LEFT);
                break;

            case R.id.btn_home:
                finish();
                break;

            case R.id.btn_drawer_menu_news:
            case R.id.btn_drawer_menu_info:
            case R.id.btn_drawer_menu_case:
            case R.id.btn_drawer_menu_wise:
            case R.id.btn_drawer_menu_humor:
                intent = new Intent(act, BoardAct.class);
                intent.putExtra("type", (int) view.getTag());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.btn_info_more:
                intent = new Intent(act, BoardAct.class);
                intent.putExtra("type", 1);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.drawer_all_area:
                break;


            case R.id.btn_drawer_menu_login:
                startActivity(new Intent(act, LoginAct.class));
                break;
            case R.id.btn_drawer_menu_join:
                startActivity(new Intent(act, JoinAct.class));
                break;


            case R.id.btn_drawer_menu_my_info:
                startActivity(new Intent(act, EditAct.class));
                break;
            case R.id.btn_drawer_menu_logout:
                Common.showAlert(act, "로그아웃", "로그아웃 하시겠습니까?", new Common.OnAlertAfter() {
                    @Override
                    public void onAfterOk() {
                        AppPreference.setPrefBoolean(act, AppPreference.AUTO_LOGIN, false);
                        AppPreference.setPrefBoolean(act, AppPreference.LOGIN_STATE, false);
                        finish();
                    }

                    @Override
                    public void onAfterCancel() {

                    }
                });
                break;

            case R.id.btn_consult:
            case R.id.btn_drawer_menu_consult_01:
            case R.id.btn_drawer_menu_consult_02:
//                Common.showToastDevelop(act);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://click.gl/VfUT9Q")));
                break;
        }
    }
}