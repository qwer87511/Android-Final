package com.example.coffee.android_final;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.List;

public class AnalysisFragment extends Fragment {

    private List<String> expandItems;
    private List<String> incomeItems;
    private TextView mTextExpand;
    private TextView mTextIncome;
    private ListView mExpandList;
    private ListView mIncomeList;
    private ArrayAdapter<String> expandListAdapter;
    private ArrayAdapter<String> incomeListAdapter;
    ArrayList<ContentValues> accountDataList;

    public AnalysisFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        expandItems = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.expendItems)));
        incomeItems = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.incomeItems)));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_analysis, container, false);
        mTextExpand = view.findViewById(R.id.textExpand);
        mTextIncome = view.findViewById(R.id.textIncome);
        mExpandList = view.findViewById(R.id.expandList);
        mIncomeList = view.findViewById(R.id.incomeList);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        expandListAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);
        incomeListAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);
        mExpandList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mIncomeList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mExpandList.clearChoices();
        mIncomeList.clearChoices();
        mExpandList.setAdapter(expandListAdapter);
        mIncomeList.setAdapter(incomeListAdapter);
    }

    public void analysis(ArrayList<ContentValues> nAccountDataList) {
        if(nAccountDataList == null || nAccountDataList.size() == 0)
        {
            Toast.makeText(getContext(), "沒有資料",Toast.LENGTH_SHORT).show();
            return;
        }
        accountDataList = nAccountDataList;
        int num = nAccountDataList.size();
        int[] expands = new int[7];
        int[] incomes = new int[5];
        for(int i = 0; i < num; i++){
            ContentValues accountData = accountDataList.get(i);
            // IllegalStateException
            if(expandItems == null)
                throw new IllegalStateException("IllegalStateException");
            if(accountData.getAsString("method").equals("支出"))
                expands[expandItems.indexOf(accountData.getAsString("item"))] += accountData.getAsInteger("amount");
            else
                incomes[incomeItems.indexOf(accountData.getAsString("item"))] += accountData.getAsInteger("amount");
        }

        int expandAmount = 0;
        int incomeAmount = 0;
        for(int i = 0; i < expands.length; i++) {
            expandAmount += expands[i];
        }
        for(int i = 0; i < incomes.length; i++) {
            incomeAmount += incomes[i];
        }

        // 計算從最後一筆資料到現在的日期
        Calendar calendar = Calendar.getInstance();
        ContentValues oldAccountData = accountDataList.get(accountDataList.size() - 1);
        calendar.set(oldAccountData.getAsInteger("year") ,oldAccountData.getAsInteger("month") ,oldAccountData.getAsInteger("day"));
        Date oldDate = calendar.getTime();
        long days = (new Date().getTime() - oldDate.getTime()) / (1000 * 3600 * 24) + 1;

        //Create List
        expandListAdapter.clear();
        incomeListAdapter.clear();
        mTextExpand.setText("支出    金額 : " + String.valueOf(expandAmount));
        mTextIncome.setText("收入    金額 : " + String.valueOf(incomeAmount));
        Formatter formatter = new Formatter();

        for (int i = 0; i < expandItems.size(); i++) {
            expandListAdapter.add(expandItems.get(i) + "    " + ((expandAmount > 0) ? String.valueOf(expands[i] * 100 / expandAmount) : "0") +
                    "%    金額 : " + String.valueOf(expands[i]) + "    平均每日 : " + ((days > 0) ? String.valueOf(expands[i] / days) : "0"));
        }
        for (int i = 0; i < incomeItems.size(); i++) {
            incomeListAdapter.add(incomeItems.get(i) + "    " + ((incomeAmount > 0) ? String.valueOf(incomes[i] * 100 / incomeAmount) : 0) +
                    "%    金額 : " + String.valueOf(incomes[i]) + "    平均每日 : " + ((days > 0) ? String.valueOf(incomes[i] / days) : "0"));
        }
        expandListAdapter.notifyDataSetChanged();
        incomeListAdapter.notifyDataSetChanged();
        mExpandList.clearChoices();
        mIncomeList.clearChoices();
        mExpandList.requestLayout();
        mIncomeList.requestLayout();
    }
}
