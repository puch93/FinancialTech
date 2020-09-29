package kr.co.core.financial_tech.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import kr.co.core.financial_tech.R;
import kr.co.core.financial_tech.adapter.CommentAdapter;
import kr.co.core.financial_tech.data.CommentData;
import kr.co.core.financial_tech.data.CommentDoubleData;
import kr.co.core.financial_tech.databinding.ActivityBoardDetailBinding;
import kr.co.core.financial_tech.server.ReqBasic;
import kr.co.core.financial_tech.server.netUtil.HttpResult;
import kr.co.core.financial_tech.server.netUtil.NetUrls;
import kr.co.core.financial_tech.util.AppPreference;
import kr.co.core.financial_tech.util.Common;
import kr.co.core.financial_tech.util.StatusBarUtil;
import kr.co.core.financial_tech.util.StringUtil;

public class BoardDetailAct extends BaseAct implements View.OnClickListener {
    ActivityBoardDetailBinding binding;
    Activity act;

    String b_idx;
    String b_type;

    ArrayList<CommentData> list_comment = new ArrayList<>();
    CommentAdapter adapter_comment;

    CommentData commentData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_board_detail, null);
        StatusBarUtil.setStatusBarColor(this, StatusBarUtil.StatusBarColorType.WHITE_STATUS_BAR);
        act = this;

        if (AppPreference.getPrefBoolean(act, AppPreference.LOGIN_STATE)) {

            binding.btnWrite.setOnClickListener(this);
            binding.btnBack.setOnClickListener(this);
            binding.btnWriteDouble.setOnClickListener(this);
            binding.btnClose.setOnClickListener(this);

            binding.title.setText(getIntent().getStringExtra("title"));

            b_idx = getIntent().getStringExtra("b_idx");
            b_type = getIntent().getStringExtra("b_type");

            adapter_comment = new CommentAdapter(act, list_comment, new CommentAdapter.CommentClickListener() {
                @Override
                public void sendCommentData(CommentData data) {
                    commentData = data;
                    setCommentDoubleLayout();
                }
            });
            binding.recyclerView.setAdapter(adapter_comment);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(act));
            binding.recyclerView.setHasFixedSize(true);
            binding.recyclerView.setItemViewCacheSize(20);

            getBoardDetail();

            binding.btnBack.setOnClickListener(view -> {
                finish();
            });

            setCommentLayout();
        } else {
            Common.showToast(act, "로그인 후 이용 가능합니다.");
            Intent intent = new Intent(act, LoginAct.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            if(BoardAct.act != null) {
                BoardAct.act.finish();
            }
            finish();
        }
    }

    private void setCommentDoubleLayout() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.comment.setText("");
                binding.commentDouble.setText("");
                binding.areaComment.setVisibility(View.GONE);
                binding.areaCommentDouble.setVisibility(View.VISIBLE);
                binding.commentPreNick.setText(commentData.getU_nick());
            }
        });
    }

    private void setCommentLayout() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.comment.setText("");
                binding.commentDouble.setText("");
                binding.areaComment.setVisibility(View.VISIBLE);
                binding.areaCommentDouble.setVisibility(View.GONE);
                binding.commentPreNick.setText("");
            }
        });
    }

    private void setWebView(String contents) {
        /* web view */
        binding.webview.setBackgroundColor(0);
        binding.webview.setVerticalScrollBarEnabled(true);
        binding.webview.getSettings().setDomStorageEnabled(true);
        binding.webview.getSettings().setJavaScriptEnabled(true);
        binding.webview.getSettings().setAllowFileAccess(true);
        binding.webview.getSettings().setAllowContentAccess(true);
        binding.webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        binding.webview.getSettings().setLoadWithOverviewMode(true);
        binding.webview.getSettings().setSupportMultipleWindows(true);
        binding.webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        String result = contents;
//        if (contents.contains("/front_new/modules/img_view.jsp")) {
//            Document doc = Jsoup.parse(contents);
//            Elements img = doc.getElementsByTag("img");
//
//
//            for (int i = 0; i < img.size(); i++) {
//                String first, convert;
//
//                first = img.get(i).attr("src");
//                convert = NetUrls.DOMAIN_ORIGIN + img.get(i).attr("src");
//                result = result.replace(first, convert);
//            }
//        }

        Document doc = Jsoup.parse(contents);
        Elements img = doc.getElementsByTag("img");


        for (int i = 0; i < img.size(); i++) {
            String first, convert;

            first = img.get(i).attr("src");
            if(!first.contains("http://ftech.adamstore.co.kr")) {
                convert = NetUrls.DOMAIN_ORIGIN + img.get(i).attr("src");
                result = result.replace(first, convert);
            } else {
                result = first;
            }
        }

        binding.webview.loadData(result, "text/html;charset=UTF-8", "UTF-8");
        binding.webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url != null && url.startsWith("intent://")) {
                    try {
                        Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        Intent existPackage = getPackageManager().getLaunchIntentForPackage(intent.getPackage());

                        if (existPackage != null) {
                            startActivity(intent);
                        } else {
                            Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                            marketIntent.setData(Uri.parse("market://details?id=" + intent.getPackage()));
                            startActivity(marketIntent);
                        }
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else if (url != null && url.startsWith("market://")) {
                    try {
                        Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        if (intent != null) {
                            startActivity(intent);
                        }
                        return true;
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else {

                }

                return true;
            }
        });

        binding.webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(final WebView view, boolean dialog, boolean userGesture, Message resultMsg) {
                WebView newWebView = new WebView(act);
                WebView.WebViewTransport transport
                        = (WebView.WebViewTransport) resultMsg.obj;

                transport.setWebView(newWebView);
                resultMsg.sendToTarget();

                newWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                        browserIntent.setData(Uri.parse(url));
                        startActivity(browserIntent);
                    }
                });


                return true;

            }
        });
    }

    private void getBoardDetail() {
        list_comment = new ArrayList<>();

        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            JSONArray data = jo.getJSONArray("data");
                            JSONObject job_data = data.getJSONObject(0);

                            String b_title = StringUtil.getStr(job_data, "b_title");
                            String b_contents = StringUtil.getStr(job_data, "b_contents");
                            String b_comment_cnt = StringUtil.getStr(job_data, "b_comment_cnt");
                            String b_hits = StringUtil.getStr(job_data, "b_hits");
                            String b_regdate = StringUtil.convertTime(StringUtil.getStr(job_data, "b_regdate"));
                            String b_name = StringUtil.getStr(job_data, "m_nick");

                            b_title = b_title.replace("\\", "");
                            b_contents = b_contents.replace("\\", "");

                            // 게시글 데이터 세팅
                            if (b_name.equalsIgnoreCase("재테크야놀자 관리자") || b_name.equalsIgnoreCase("관리자")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        binding.webviewArea.setVisibility(View.VISIBLE);
                                        binding.appArea.setVisibility(View.GONE);
                                    }
                                });

                                setWebView(b_contents);

                                if (jo.has("file") && !StringUtil.getStr(jo, "file").equalsIgnoreCase("null") && !StringUtil.isNull(StringUtil.getStr(jo, "file"))) {
                                    JSONArray file = jo.getJSONArray("file");
                                    for (int i = 0; i < file.length(); i++) {
                                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                        layoutParams.topMargin = 20;
                                        JSONObject job = file.getJSONObject(i);
                                        ImageView imageView = new ImageView(act);
                                        imageView.setLayoutParams(layoutParams);
                                        imageView.setAdjustViewBounds(true);
                                        Glide.with(act)
                                                .load(NetUrls.DOMAIN_ORIGIN + StringUtil.getStr(job, "bf_dir") + StringUtil.getStr(job, "bf_file"))
                                                .into(imageView);

                                        binding.webviewArea.addView(imageView);
                                    }
                                }
                            } else {
                                String finalB_contents = b_contents;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        binding.webviewArea.setVisibility(View.GONE);
                                        binding.appArea.setVisibility(View.VISIBLE);

                                        binding.contents.setText(finalB_contents);
                                    }
                                });

                                if (jo.has("file") && !StringUtil.getStr(jo, "file").equalsIgnoreCase("null") && !StringUtil.isNull(StringUtil.getStr(jo, "file"))) {
                                    JSONArray file = jo.getJSONArray("file");
                                    for (int i = 0; i < file.length(); i++) {
                                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                        layoutParams.topMargin = 20;
                                        JSONObject job = file.getJSONObject(i);
                                        ImageView imageView = new ImageView(act);
                                        imageView.setLayoutParams(layoutParams);
                                        imageView.setAdjustViewBounds(true);
                                        Glide.with(act)
                                                .load(NetUrls.DOMAIN_ORIGIN + StringUtil.getStr(job, "bf_dir") + StringUtil.getStr(job, "bf_file"))
                                                .into(imageView);

                                        binding.imageArea.addView(imageView);
                                    }
                                }
                            }


                            String finalB_title = b_title;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.nick.setText(b_name);
                                    binding.title02.setText(finalB_title);
                                    binding.reviewCountTop.setText(b_comment_cnt);
                                    binding.reviewCountBottom.setText(b_comment_cnt);
                                    binding.viewCount.setText(b_hits);
                                    binding.date.setText(b_regdate);
                                }
                            });


                            // 댓글 데이터 세팅
                            if (job_data.has("commentlist")) {

                                JSONArray commentlist = job_data.getJSONArray("commentlist");
                                for (int i = 0; i < commentlist.length(); i++) {
                                    JSONObject job_comment = commentlist.getJSONObject(i);

                                    String m_idx = StringUtil.getStr(job_comment, "m_idx");
                                    String m_name = StringUtil.getStr(job_comment, "m_nick");
                                    String c_idx = StringUtil.getStr(job_comment, "c_idx");
                                    String c_comment = StringUtil.getStr(job_comment, "c_comment");
                                    String c_regdate = StringUtil.getStr(job_comment, "c_regdate");
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    try {
                                        c_regdate = Common.formatImeString(format.parse(c_regdate), act);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    ArrayList<CommentDoubleData> list_double = new ArrayList<>();
                                    if (job_comment.has("commentlist_double")) {
                                        JSONArray ja_double = job_comment.getJSONArray("commentlist_double");
                                        for (int j = 0; j < ja_double.length(); j++) {
                                            JSONObject job_double = ja_double.getJSONObject(j);

                                            String m_idx2 = StringUtil.getStr(job_double, "m_idx");
                                            String m_name2 = StringUtil.getStr(job_double, "m_nick");
                                            String c_idx2 = StringUtil.getStr(job_double, "c_idx");
                                            String c_comment2 = StringUtil.getStr(job_double, "c_comment");
                                            String c_regdate2 = StringUtil.getStr(job_double, "c_regdate");
                                            SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                                            try {
                                                c_regdate2 = Common.formatImeString(format2.parse(c_regdate2), act);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                            list_double.add(new CommentDoubleData(c_idx2, m_idx2, m_name, m_name2, c_comment2, c_regdate2));
                                        }
                                    }

                                    list_comment.add(new CommentData(c_idx, m_idx, m_name, c_comment, c_regdate, list_double));
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter_comment.setList(list_comment);
                                    }
                                });
                            }

                        } else {
                            Common.showToast(act, StringUtil.getStr(jo, "message"));
                            finish();
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

        server.setTag("Board Detail");
        server.addParams("dbControl", NetUrls.BOARD_DETAIL);
        server.addParams("BCODE", b_type);
        server.addParams("CODE", b_idx);
        server.execute(true, true);
    }


    private void doComment() {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            Common.showToast(act, "댓글이 정상적으로 등록되었습니다.");
                            setCommentLayout();
                            getBoardDetail();
                        } else {
                            Common.showToast(act, StringUtil.getStr(jo, "message"));
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

        server.setTag("Comment");
        server.addParams("dbControl", NetUrls.BOARD_COMMENT);
        server.addParams("BCODE", b_type);
        server.addParams("CODE", b_idx);
        server.addParams("m_idx", AppPreference.getPrefString(act, AppPreference.PREF_MIDX));
        server.addParams("c_comment", binding.comment.getText().toString());
        server.addParams("c_level", "0");
        server.execute(true, false);
    }

    private void doCommentDouble() {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            Common.showToast(act, "댓글이 정상적으로 등록되었습니다.");
                            setCommentLayout();
                            getBoardDetail();
                        } else {
                            Common.showToast(act, StringUtil.getStr(jo, "message"));
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

        server.setTag("Comment Double");
        server.addParams("dbControl", NetUrls.BOARD_COMMENT);
        server.addParams("BCODE", b_type);
        server.addParams("CODE", b_idx);
        server.addParams("m_idx", AppPreference.getPrefString(act, AppPreference.PREF_MIDX));
        server.addParams("parentCode", commentData.getC_idx());
        server.addParams("c_comment", binding.commentDouble.getText().toString());
        server.addParams("c_level", "1");
        server.execute(true, false);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_write:
                if (binding.comment.length() == 0) {
                    Common.showToast(act, "댓글을 입력해주세요");
                } else {
                    doComment();
                }
                break;

            case R.id.btn_write_double:
                if (binding.commentDouble.length() == 0) {
                    Common.showToast(act, "댓글을 입력해주세요");
                } else {
                    doCommentDouble();
                }
                break;

            case R.id.btn_back:
                finish();
                break;

            case R.id.btn_close:
                setCommentLayout();
                break;
        }
    }
}