package kr.co.core.financial_tech.activity;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kr.co.core.financial_tech.R;
import kr.co.core.financial_tech.databinding.ActivityTermBinding;
import kr.co.core.financial_tech.server.ReqBasic;
import kr.co.core.financial_tech.server.netUtil.HttpResult;
import kr.co.core.financial_tech.server.netUtil.NetUrls;
import kr.co.core.financial_tech.util.Common;
import kr.co.core.financial_tech.util.StringUtil;

public class TermAct extends BaseAct {
    ActivityTermBinding binding;
    Activity act;

    String code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_term, null);
        act = this;

        code = getIntent().getStringExtra("code");
        if(code.equalsIgnoreCase("di_terms")) {
            binding.title.setText("이용약관");
        } else {
            binding.title.setText("개인정보처리방침");
        }

        getTerm();

        binding.btnBack.setOnClickListener(view -> {
            finish();
        });
    }

    private void getTerm() {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if( StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            JSONArray ja = jo.getJSONArray("data");
                            JSONObject job = ja.getJSONObject(0);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.term.setText(StringUtil.getStr(job, code));
                                }
                            });
                        } else {
                            Common.showToastNetwork(act);
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

        server.setTag("Term");
        server.addParams("dbControl", NetUrls.TERM);
        server.execute(true, false);
    }
}