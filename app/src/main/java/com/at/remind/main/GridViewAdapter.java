package com.at.remind.main;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.at.remind.R;
import com.at.remind.db.AtRemind;
import com.at.remind.db.AtRemindDb;

import java.util.Date;
import java.util.List;

/**
 * Created by user on 16-12-19.
 */

public class GridViewAdapter extends BaseAdapter implements View.OnClickListener{
    public List<AtRemind> mList;
    public Context mContext;

    public GridViewAdapter(Context context, List<AtRemind> mList) {
        this.mList = mList;
        this.mContext=context;
    }

    public void setmList(List<AtRemind> mList) {
        this.mList = mList;
    }

    @Override
    public int getCount() {
        if (mList == null)
            return 0;
        else
            return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view ==null) {
            LayoutInflater li = LayoutInflater.from(mContext);
            view = li.inflate(R.layout.at_remind_item, null);
        }
        TextView text=(TextView)view.findViewById(R.id.text);
        text.setTag(mList.get(i).getId());
        text.setOnClickListener(this);
        text.setText(mList.get(i).getText());
        return view;
    }

    @Override
    public void onClick(View view) {

        final Integer id=(Integer)(view.getTag());
        Log.i("hongtao.fu","Id="+id);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater layoutInflater= LayoutInflater.from(mContext);
        View dialogView = layoutInflater.inflate(R.layout.delete_remind_dialog,null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        dialog.show();

        TextView tips=(TextView)dialogView.findViewById(R.id.tips);
        Button allow=(Button)dialogView.findViewById(R.id.allow);
        Button refuse=(Button)dialogView.findViewById(R.id.refuse);
        tips.setText("确定要删除 "+((TextView)view).getText().toString());
        allow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AtRemindDb atRemindDb=AtRemindDb.getInstance(mContext);
                atRemindDb.delete(id);
                mList=atRemindDb.select();
                notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
}
