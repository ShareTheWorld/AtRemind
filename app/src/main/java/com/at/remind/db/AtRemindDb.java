package com.at.remind.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.SyncStateContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 16-12-19.
 */

public class AtRemindDb {
    private Db mDb;
    private static AtRemindDb mAtRemindDb;
    private SQLiteDatabase mSqlDb;
    private  AtRemindDb(Context context){
        mDb= Db.getInstance(context);
        mSqlDb=mDb.getWritableDatabase();
    }

    public static AtRemindDb getInstance(Context context){
        if(mAtRemindDb==null){
            synchronized (AtRemindDb.class){
                mAtRemindDb=new AtRemindDb(context);
            }
        }
        return mAtRemindDb;
    }
    public void inset(AtRemind atRemind){
        try {
            ContentValues values = new ContentValues();
            values.put(AtRemind.TEXT, atRemind.getText());
            values.put(AtRemind.TIME, atRemind.getTime());
            mSqlDb.insert(AtRemind.TABLE_NAME, null, values);
        }catch (Exception e){

        }

    }
    public List<AtRemind> select(){
        String sql="select * from "+AtRemind.TABLE_NAME;
        List<AtRemind> list=new ArrayList<AtRemind>();
        Cursor c=mSqlDb.rawQuery(sql,null,null);
        if(c!=null){
            while(c.moveToNext()){
                int id=c.getInt(c.getColumnIndex(AtRemind.ID));
                String text=c.getString(c.getColumnIndex(AtRemind.TEXT));
                long time =c.getLong(c.getColumnIndex(AtRemind.TIME));
                AtRemind ar=new AtRemind(id,text,time);
                list.add(ar);
            }
        }
        return list;
    }

    public List<String> selectAllKeyWord(){
        String sql="select * from "+AtRemind.TABLE_NAME;
        List<String> list=new ArrayList<String>();
        Cursor c=mSqlDb.rawQuery(sql,null,null);
        if(c!=null){
            while(c.moveToNext()){
                String text=c.getString(c.getColumnIndex(AtRemind.TEXT));
                list.add(text);
            }
        }
        return list;
    }
    public void delete(int id){
        String sql="delete  from "+AtRemind.TABLE_NAME +" where id="+id+" ;";
        mSqlDb.execSQL(sql);
    }
}
