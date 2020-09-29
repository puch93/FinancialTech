package kr.co.core.financial_tech.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import org.json.JSONException;
import org.json.JSONObject;

import kr.co.core.financial_tech.R;
import kr.co.core.financial_tech.activity.FindResultAct;
import kr.co.core.financial_tech.activity.JoinAct;
import kr.co.core.financial_tech.activity.TermAct;
import kr.co.core.financial_tech.databinding.FragmentFind01Binding;
import kr.co.core.financial_tech.databinding.FragmentFind02Binding;
import kr.co.core.financial_tech.databinding.FragmentJoin01Binding;
import kr.co.core.financial_tech.server.ReqBasic;
import kr.co.core.financial_tech.server.netUtil.HttpResult;
import kr.co.core.financial_tech.server.netUtil.NetUrls;
import kr.co.core.financial_tech.util.Common;
import kr.co.core.financial_tech.util.StringUtil;

public class Find02Frag extends BaseFrag {
    FragmentFind02Binding binding;
    Activity act;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_find_02, container, false);
        act = getActivity();

        binding.btnFind.setOnClickListener(view -> {
            if (binding.id.length() == 0) {
                Common.showToast(act, "아이디를 입력해주세요");
            } else if (binding.name.length() == 0) {
                Common.showToast(act, "이름을 입력해주세요");
            } else if (binding.phone.length() == 0 || !Common.checkCellnum(binding.phone.getText().toString())) {
                Common.showToast(act, "전화번호를 확인해주세요");
            } else {
                doFindPw();
            }
        });

        return binding.getRoot();
    }

    private void doFindPw() {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            String m_id = StringUtil.getStr(jo, "m_id");
                            String m_pass = StringUtil.getStr(jo, "m_pass");
                            startActivity(new Intent(act, FindResultAct.class).putExtra("id", binding.id.getText().toString()).putExtra("pw", m_pass));
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

        server.setTag("Find Pw");
        server.addParams("dbControl", NetUrls.FIND_ID_PW);
        server.addParams("m_id", binding.id.getText().toString());
        server.addParams("m_hp", binding.phone.getText().toString());
        server.addParams("m_name", binding.name.getText().toString());
        server.execute(true, false);
    }
}
