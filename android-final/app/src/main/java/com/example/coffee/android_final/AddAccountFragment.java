package com.example.coffee.android_final;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class AddAccountFragment extends Fragment {

    private static ContentResolver mContentResolver;

    private ArrayList<ContentValues> accountDataList;

    private ArrayAdapter<String> spnExpendAdapter;
    private ArrayAdapter<String> spnIncomeAdapter;

    private DatePicker mDatePicker;
    private Spinner mSpnMethod;
    private Spinner mSpnItem;
    private EditText mEdtComment;
    private EditText mEdtAmount;
    private Button mBtnAdd;

    public AddAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContentResolver = getActivity().getContentResolver();
        accountDataList = new ArrayList<>();
        spnExpendAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.expendItems));
        spnIncomeAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.incomeItems));
        spnExpendAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnIncomeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // 讀檔 // !!! 不可以放在onActivityCreate否則會讀很多次 !!!
        Cursor cursor = mContentResolver.query(AccountDataContentProvider.CONTENT_URI, null,
                null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("_id", cursor.getInt(0));
                contentValues.put("year", cursor.getInt(1));
                contentValues.put("month", cursor.getInt(2));
                contentValues.put("day", cursor.getInt(3));
                contentValues.put("method", cursor.getString(4));
                contentValues.put("item", cursor.getString(5));
                contentValues.put("comment", cursor.getString(6));
                contentValues.put("amount", cursor.getInt(7));
                accountDataList.add(contentValues);
                cursor.moveToNext();
            }
            cursor.close();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_account, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();

        mDatePicker = (DatePicker) view.findViewById(R.id.datePicker);
        mSpnMethod = (Spinner) view.findViewById(R.id.spnMethod);
        mSpnItem = (Spinner) view.findViewById(R.id.spnItem);
        mEdtComment = (EditText) view.findViewById(R.id.edtComment);
        mEdtAmount = (EditText) view.findViewById(R.id.edtAmount);
        mBtnAdd = view.findViewById(R.id.btnAdd);
        mBtnAdd.setOnClickListener(btnAddOnClick);
        mSpnMethod.setOnItemSelectedListener(spnMethodOnSelected);
    }

    // 當方法被選擇，項目就會改變
    private AdapterView.OnItemSelectedListener spnMethodOnSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ArrayAdapter<String> spnAdapter;
            switch (position) {
                case 0: // 選擇支出
                    mSpnItem.setAdapter(spnExpendAdapter);
                    break;
                case 1: // 選擇收入
                    mSpnItem.setAdapter(spnIncomeAdapter);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private View.OnClickListener btnAddOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mEdtAmount.getText().toString().equals("")) {
                Toast.makeText(getContext(), "請輸入金額", Toast.LENGTH_SHORT).show();
            }
            else {
                ContentValues contentValues = getAccountData();
                accountDataList.add(contentValues);
                mContentResolver.insert(AccountDataContentProvider.CONTENT_URI, contentValues);
                Toast.makeText(getContext(), "加入成功", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private ContentValues getAccountData() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("year", mDatePicker.getYear());
        contentValues.put("month", mDatePicker.getMonth());
        contentValues.put("day", mDatePicker.getDayOfMonth());
        contentValues.put("method", mSpnMethod.getSelectedItem().toString());
        contentValues.put("item", mSpnItem.getSelectedItem().toString());
        contentValues.put("comment", mEdtComment.getText().toString());
        contentValues.put("amount", Integer.parseInt(mEdtAmount.getText().toString()));
        return contentValues;
    }

    public ArrayList<ContentValues> getAccountDataList() {
        return new ArrayList<>(accountDataList);
    }

    public void setAccountDataByIndex(int index) {
        ContentValues contentValues = accountDataList.get(index);
        mDatePicker.updateDate(contentValues.getAsInteger("year"), contentValues.getAsInteger("month") - 1, contentValues.getAsInteger("day"));
        String method = contentValues.getAsString("method");
        mSpnMethod.setSelection(Arrays.asList(getResources().getStringArray(R.array.methods)).indexOf(method));
        if(method.equals("支出"))
        {
            mSpnItem.setAdapter(spnExpendAdapter);
            mSpnItem.setSelection(Arrays.asList(getResources().getStringArray(R.array.expendItems)).indexOf(contentValues.getAsString("item")));
        }
        else
        {
            mSpnItem.setAdapter(spnIncomeAdapter);
            mSpnItem.setSelection(Arrays.asList(getResources().getStringArray(R.array.incomeItems)).indexOf(contentValues.getAsString("item")));
        }
        mEdtComment.setText(contentValues.getAsString("comment"));
        mEdtAmount.setText(contentValues.getAsString("amount"));
    }

    public void deleteAccountDataByIndex(int index) {
        mContentResolver.delete(AccountDataContentProvider.CONTENT_URI, "_id = " + accountDataList.get(index).getAsInteger("_id"), null);
        accountDataList.remove(index);
    }
}
