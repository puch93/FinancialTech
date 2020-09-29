package kr.co.core.financial_tech.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import org.json.JSONException;
import org.json.JSONObject;

import kr.co.core.financial_tech.R;
import kr.co.core.financial_tech.databinding.ActivityLoginBinding;
import kr.co.core.financial_tech.server.ReqBasic;
import kr.co.core.financial_tech.server.netUtil.HttpResult;
import kr.co.core.financial_tech.server.netUtil.NetUrls;
import kr.co.core.financial_tech.util.AppPreference;
import kr.co.core.financial_tech.util.Common;
import kr.co.core.financial_tech.util.StatusBarUtil;
import kr.co.core.financial_tech.util.StringUtil;

public class LoginAct extends BaseAct implements View.OnClickListener {
    ActivityLoginBinding binding;
    public static Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login, null);
        act = this;

        StatusBarUtil.setStatusBarColor(this, StatusBarUtil.StatusBarColorType.DEFAULT_STATUS_BAR);

        binding.btnLogin.setOnClickListener(this);
        binding.btnJoin.setOnClickListener(this);
        binding.btnFindAccount.setOnClickListener(this);
    }

    private void doLogin() {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if( StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            JSONObject job = jo.getJSONObject("data");

                            AppPreference.setPrefString(act, AppPreference.PREF_MIDX, StringUtil.getStr(job, "m_idx"));
                            AppPreference.setPrefString(act, AppPreference.PREF_ID, binding.id.getText().toString());
                            AppPreference.setPrefString(act, AppPreference.PREF_PW, binding.pw.getText().toString());
                            AppPreference.setPrefString(act, AppPreference.PREF_NICK, StringUtil.getStr(job, "m_nick"));
                            AppPreference.setPrefBoolean(act, AppPreference.AUTO_LOGIN, binding.ckKeep.isChecked());
                            AppPreference.setPrefBoolean(act, AppPreference.LOGIN_STATE, true);

                            Intent intent = new Intent(act, MainAct.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
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

        server.setTag("Login");
        server.addParams("dbControl", NetUrls.LOGIN);
        server.addParams("m_id", binding.id.getText().toString());
        server.addParams("m_pass", binding.pw.getText().toString());
        server.addParams("fcm", AppPreference.getPrefString(act, AppPreference.PREF_FCM));
        server.addParams("m_uniq", Common.getDeviceId(act));
        server.execute(true, false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                if (binding.id.length() == 0) {
                    Common.showToast(act, "아이디를 입력해주세요");
                } else if (binding.pw.length() == 0) {
                    Common.showToast(act, "비밀번호를 입력해주세요");
                } else {
                    doLogin();
                }
                break;

            case R.id.btn_join:
                startActivity(new Intent(act, JoinAct.class));
                break;

            case R.id.btn_find_account:
                startActivity(new Intent(act, FindAct.class));
                break;
        }
    }
}