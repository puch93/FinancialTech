package kr.co.core.financial_tech.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.core.financial_tech.R;
import kr.co.core.financial_tech.data.BoardData;
import kr.co.core.financial_tech.databinding.ActivityMainBinding;
import kr.co.core.financial_tech.server.ReqBasic;
import kr.co.core.financial_tech.server.netUtil.HttpResult;
import kr.co.core.financial_tech.server.netUtil.NetUrls;
import kr.co.core.financial_tech.util.AppPreference;
import kr.co.core.financial_tech.util.BackPressCloseHandler;
import kr.co.core.financial_tech.util.Common;
import kr.co.core.financial_tech.util.StatusBarUtil;
import kr.co.core.financial_tech.util.StringUtil;

public class MainAct extends BaseAct implements View.OnClickListener {
    ActivityMainBinding binding;
    Activity act;

    private BackPressCloseHandler backPressCloseHandler;

    ArrayList<LinearLayout> linearLayouts = new ArrayList<>();
    ArrayList<TextView> textViews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main, null);
        act = this;

        linearLayouts.add(binding.areaInfo01);
        linearLayouts.add(binding.areaInfo02);
        linearLayouts.add(binding.areaInfo03);
        linearLayouts.add(binding.areaInfo04);

        textViews.add(binding.titleInfo01);
        textViews.add(binding.titleInfo02);
        textViews.add(binding.titleInfo03);
        textViews.add(binding.titleInfo04);

        backPressCloseHandler = new BackPressCloseHandler(this);

        StatusBarUtil.setStatusBarColor(this, StatusBarUtil.StatusBarColorType.DEFAULT_STATUS_BAR);

        setLayout();
    }

    private void setLayout() {
        /* set click listener */
        binding.btnConsult.setOnClickListener(this);
        binding.btnDrawer.setOnClickListener(this);
        binding.btnMenuNews.setOnClickListener(this);
        binding.btnMenuInfo.setOnClickListener(this);
        binding.btnMenuCase.setOnClickListener(this);
        binding.btnMenuWise.setOnClickListener(this);
        binding.btnMenuHumor.setOnClickListener(this);
        binding.btnMenuNotice.setOnClickListener(this);
        binding.btnInfoMore.setOnClickListener(this);

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
        binding.btnMenuNews.setTag(0);
        binding.btnMenuInfo.setTag(1);
        binding.btnMenuCase.setTag(2);
        binding.btnMenuWise.setTag(3);
        binding.btnMenuHumor.setTag(4);
        binding.btnMenuNotice.setTag(5);
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

        getBoardList();
    }

    private void getBoardList() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.titleInfo01.setText("");
                binding.titleInfo02.setText("");
                binding.titleInfo03.setText("");
                binding.titleInfo04.setText("");

                binding.ivN01.setVisibility(View.GONE);
                binding.ivN02.setVisibility(View.GONE);
            }
        });

        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            JSONArray ja = jo.getJSONArray("data");
                            for (int i = 0; i < ja.length(); i++) {
                                if (i == 4)
                                    break;

                                int finalI = i;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (finalI == 0) {
                                            binding.ivN01.setVisibility(View.VISIBLE);
                                        }

                                        if (finalI == 1) {
                                            binding.ivN02.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });

                                JSONObject job = ja.getJSONObject(i);

                                String bf_file = null;
                                if (job.has("file")) {
                                    JSONObject file = job.getJSONObject("file");
                                    bf_file = NetUrls.DOMAIN_ORIGIN + StringUtil.getStr(file, "bf_dir") + StringUtil.getStr(file, "bf_file");
                                    Log.i(StringUtil.TAG, "bf_file: " + bf_file);
                                }

                                String b_idx = StringUtil.getStr(job, "b_idx");
                                String b_name = StringUtil.getStr(job, "m_nick");
                                String b_title = StringUtil.getStr(job, "b_title");
                                String b_regdate = StringUtil.convertTime(StringUtil.getStr(job, "b_regdate"));
                                String b_hits = StringUtil.getStr(job, "b_hits");
                                String b_comment_cnt = StringUtil.getStr(job, "b_comment_cnt");
                                if (Integer.parseInt(b_comment_cnt) > 99) {
                                    b_comment_cnt = "99";
                                }

                                setBoardLayout(
                                        linearLayouts.get(i),
                                        textViews.get(i),
                                        new BoardData("3", b_idx, b_name, b_title, bf_file, b_regdate, b_hits, b_comment_cnt));

                            }

                        } else {
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Common.showToastNetwork(act);
                    }
                } else {
                    Common.showToastNetwork(act);
                }
            }
        };

        server.setTag("Board List " + "info");
        server.addParams("dbControl", NetUrls.BOARD_LIST);
        server.addParams("BCODE", "3");
        server.addParams("pagenum", "1");
        server.addParams("word", "");
        server.execute(true, false);
    }

    private void setBoardLayout(LinearLayout linearLayout, TextView textView, BoardData data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(data.getTitle());

                linearLayout.setOnClickListener(view -> {
                    if (!StringUtil.isNull(textView.getText().toString())) {
                        act.startActivity(new Intent(act, BoardDetailAct.class)
                                .putExtra("title", StringUtil.getBoardName(data.getB_type()))
                                .putExtra("b_type", data.getB_type())
                                .putExtra("b_idx", data.getB_idx()));
                    }
                });
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (!binding.drawerLayout.isDrawerOpen(Gravity.LEFT))
            backPressCloseHandler.onBackPressed();
        else
            binding.drawerLayout.closeDrawer(Gravity.LEFT);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.btn_menu_news:
            case R.id.btn_menu_info:
            case R.id.btn_menu_case:
            case R.id.btn_menu_wise:
            case R.id.btn_menu_humor:
            case R.id.btn_menu_notice:
            case R.id.btn_drawer_menu_news:
            case R.id.btn_drawer_menu_info:
            case R.id.btn_drawer_menu_case:
            case R.id.btn_drawer_menu_wise:
            case R.id.btn_drawer_menu_humor:
            case R.id.btn_drawer_menu_notice:
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

            case R.id.btn_drawer:
                binding.drawerLayout.openDrawer(Gravity.LEFT);
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
                        onResume();
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