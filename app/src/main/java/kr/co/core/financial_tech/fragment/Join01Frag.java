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

import kr.co.core.financial_tech.R;
import kr.co.core.financial_tech.activity.JoinAct;
import kr.co.core.financial_tech.activity.TermAct;
import kr.co.core.financial_tech.databinding.FragmentJoin01Binding;
import kr.co.core.financial_tech.util.Common;

public class Join01Frag extends BaseFrag implements CheckBox.OnCheckedChangeListener, View.OnClickListener {
    FragmentJoin01Binding binding;
    Activity act;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_join_01, container,false);
        act = getActivity();
        binding.ckPrivate.setOnCheckedChangeListener(this);
        binding.ckUse.setOnCheckedChangeListener(this);
        binding.btnTermUse.setOnClickListener(this);
        binding.btnTermPrivate.setOnClickListener(this);
        binding.btnTermUse.setTag("di_terms");
        binding.btnTermPrivate.setTag("di_personal_information");

        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!binding.ckUse.isChecked()) {
                    Common.showToast(act, "이용 약관에 동의해주세요");
                } else if(!binding.ckPrivate.isChecked()) {
                    Common.showToast(act, "개인정보 처리방침에 동의해주세요");
                }else {
                    nextProcess();
                }
            }
        });


        binding.btnAgreeAll.setOnClickListener(v -> {
            if(binding.btnAgreeAll.isSelected()) {
                binding.btnAgreeAll.setSelected(false);
                binding.ckPrivate.setChecked(false);
                binding.ckUse.setChecked(false);
            } else {
                binding.btnAgreeAll.setSelected(true);
                binding.ckPrivate.setChecked(true);
                binding.ckUse.setChecked(true);
            }
        });
        return binding.getRoot();
    }

    private void nextProcess() {
        BaseFrag fragment = new Join02Frag();
        ((JoinAct) act).replaceFragment(fragment, 2);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(binding.ckUse.isChecked() && binding.ckPrivate.isChecked()) {
            binding.btnAgreeAll.setSelected(true);
            binding.btnNext.setSelected(true);
        } else {
            binding.btnAgreeAll.setSelected(false);
            binding.btnNext.setSelected(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_term_use:
            case R.id.btn_term_private:
                startActivity(new Intent(act, TermAct.class).putExtra("code", (String) v.getTag()));
                break;
        }
    }
}
