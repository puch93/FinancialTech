package kr.co.core.financial_tech.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.core.financial_tech.R;
import kr.co.core.financial_tech.activity.BoardWriteAct;
import kr.co.core.financial_tech.adapter.BoardListAdapter;
import kr.co.core.financial_tech.data.BoardData;
import kr.co.core.financial_tech.databinding.FragmentBoardCategoryBinding;
import kr.co.core.financial_tech.server.ReqBasic;
import kr.co.core.financial_tech.server.netUtil.HttpResult;
import kr.co.core.financial_tech.server.netUtil.NetUrls;
import kr.co.core.financial_tech.util.Common;
import kr.co.core.financial_tech.util.StringUtil;

public class BoardFrag extends BaseFrag implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    FragmentBoardCategoryBinding binding;
    Activity act;
    String type;
    String type_code;

    BoardListAdapter adapter;
    LinearLayoutManager manager;
    ArrayList<BoardData> list = new ArrayList<>();
    private boolean isScroll = false;
    private int page = 1;
    private String search_text = "";

    public void setType(String type, String type_code) {
        this.type = type;
        this.type_code = type_code;
    }

    private void setKeyboardVisibilityListener() {
        final View parentView = binding.getRoot();
        parentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            private boolean alreadyOpen;
            private final int defaultKeyboardHeightDP = 100;
            private final int EstimatedKeyboardDP = defaultKeyboardHeightDP + (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 48 : 0);
            private final Rect rect = new Rect();

            @Override
            public void onGlobalLayout() {
                int estimatedKeyboardHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, EstimatedKeyboardDP, parentView.getResources().getDisplayMetrics());
                parentView.getWindowVisibleDisplayFrame(rect);
                int heightDiff = parentView.getRootView().getHeight() - (rect.bottom - rect.top);
                boolean isShown = heightDiff >= estimatedKeyboardHeight;

                if (isShown == alreadyOpen) {
                    Log.i("Keyboard state", "Ignoring global layout change...");
                    return;
                }
                alreadyOpen = isShown;

                boolean keyboardOpenState = isShown;
                if (keyboardOpenState) {
                } else {
                    binding.search.clearFocus();
                }
            }
        });
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_board_category, container, false);
        act = getActivity();

        setLayout();

        setKeyboardVisibilityListener();

        binding.search.setText("");
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        page = 1;
        list = new ArrayList<>();
        adapter.setSearch_result(false);
        binding.search.setText("");
        /* get data */
        getBoardList();
    }

    private void setLayout() {
        /* set click listener */
        binding.refreshLayout.setOnRefreshListener(this);
        binding.btnSearch.setOnClickListener(this);
        binding.btnWrite.setOnClickListener(this);

        /* set recycler view */
        manager = new LinearLayoutManager(act);
        binding.recyclerView.setLayoutManager(manager);
        adapter = new BoardListAdapter(act, list);
        adapter.setSearch_result(false);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setItemViewCacheSize(20);

        /* set scroll listener */
        binding.nestedScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                View view = (View) binding.nestedScrollView.getChildAt(binding.nestedScrollView.getChildCount() - 1);

                int diff = (view.getBottom() - (binding.nestedScrollView.getHeight() + binding.nestedScrollView
                        .getScrollY()));

                if (diff == 0) {
                    if (!isScroll) {
                        Log.e(StringUtil.TAG, "onScrollChange");
                        ++page;
                        getBoardList();
                    }
                }
            }
        });

        if (getSelection()) {
            binding.btnWrite.setVisibility(View.VISIBLE);
        } else {
            binding.btnWrite.setVisibility(View.GONE);
        }
    }

    private void setSearch() {
        page = 1;
        list = new ArrayList<>();
        search_text = binding.search.getText().toString();

        if (!StringUtil.isNull(binding.search.getText().toString())) {
            adapter.setSearch_result(true);
        } else {
            adapter.setSearch_result(false);
        }

        getBoardList();
    }

    private void getBoardList() {
        isScroll = true;
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            JSONArray ja = jo.getJSONArray("data");
                            for (int i = 0; i < ja.length(); i++) {
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

                                list.add(new BoardData(type_code, b_idx, b_name, b_title, bf_file, b_regdate, b_hits, b_comment_cnt));

                                act.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.setList(list);
                                    }
                                });

                                isScroll = false;

                            }

                        } else {
                            if (page == 1) {
                                act.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.setList(list);
                                    }
                                });

                                isScroll = true;
                            }
                        }

                    } catch (JSONException e) {
                        isScroll = true;
                        e.printStackTrace();
                        Common.showToastNetwork(act);
                    }
                } else {
                    isScroll = true;
                    Common.showToastNetwork(act);
                }

                if(binding.refreshLayout.isRefreshing()) {
                    binding.refreshLayout.setRefreshing(false);
                }
            }
        };

        server.setTag("Board List " + type);
        server.addParams("dbControl", NetUrls.BOARD_LIST);
        server.addParams("BCODE", type_code);
        server.addParams("pagenum", String.valueOf(page));
        if (adapter.isSearch_result()) {
            server.addParams("word", binding.search.getText().toString());
        } else {
            server.addParams("word", "");
        }
        server.execute(true, false);
    }

    private boolean getSelection() {
        switch (type) {
            case "case":
            case "wise":
            case "humor":
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_search:
                setSearch();
                break;

            case R.id.btn_write:
                if (getSelection()) {
                    startActivity(new Intent(act, BoardWriteAct.class).putExtra("cate", type));
                } else {
                    Common.showToast(act, "작성안됨");
                }
                break;
        }
    }

    @Override
    public void onRefresh() {
        list = new ArrayList<>();
        page = 1;
        getBoardList();
    }
}
