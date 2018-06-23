package com.example.coffee.android_final;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class RecordFragment extends Fragment {

    private ArrayAdapter<String> listDataAdapter;
    private int longClickedItemIndex;
    private ListView mListData;

    public RecordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listDataAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_record, container, false);
        mListData = view.findViewById(R.id.listData);
        registerForContextMenu(mListData);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListData.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mListData.clearChoices();
        mListData.setAdapter(listDataAdapter);
        // !!! 設定 OnItemLongClickListener "可能"導致無法使用 Context Menu，解決方法在下 !!!
        mListData.setOnItemLongClickListener(listDataOnLongClickListener);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.content_menu, menu);
        super.onCreateContextMenu(menu, view, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }

    public void createList(ArrayList<ContentValues> accountDataList){
        listDataAdapter.clear();
        for (int i = 0; i < accountDataList.size(); ++i) {
            listDataAdapter.add(accountDataToString(accountDataList.get(i)));
        }
        listDataAdapter.notifyDataSetChanged();
        mListData.clearChoices();
        mListData.requestLayout();
    }

    public int getLongClickedItemIndex() {
        return longClickedItemIndex;
    }

    private ListView.OnItemLongClickListener listDataOnLongClickListener = new ListView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            longClickedItemIndex = position;
            return false; // !!! 必須回傳false 否則無法呼叫 context menu !!! // CANNOT consumed the long click
        }
    };

    public String accountDataToString(ContentValues contentValues) {
        String string = "";
        string += "日期: " + String.valueOf(contentValues.getAsInteger("year")) + " / " +
                String.valueOf(contentValues.getAsInteger("month")) + " / " +
                String.valueOf(contentValues.getAsInteger("day")) + "\n" +
                "方法: " + contentValues.getAsString("method") + " " +
                "項目: " + contentValues.getAsString("item") + " " +
                "備註: " + contentValues.getAsString("comment") + "\n" +
                "金額: " + String.valueOf(contentValues.getAsInteger("amount")) + "\n";
        return string;
    }
}