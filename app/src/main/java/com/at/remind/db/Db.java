package com.at.remind.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.SystemClock;
import android.util.Log;

import java.util.Date;

/**
 * Created by user on 16-12-19.
 */

public class Db extends SQLiteOpenHelper {
    private static  Db mDb;
    private Db(Context context)  {
        super(context, "at_remind.mDb", null, 1);
    }

    public static Db getInstance(Context context){
        if(mDb ==null){
            synchronized (Db.class){
                mDb = new Db(context);
            }
        }
        return mDb;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table "+AtRemind.TABLE_NAME+"("
                + AtRemind.ID+" integer primary key autoincrement,"
                + AtRemind.TEXT+" varchar(100) not null,"
                + AtRemind.TIME+" datetime not null unique );");
        long time=System.currentTimeMillis();
        Log.i("hongtao.fu","time= "+time);
        sqLiteDatabase.execSQL(" insert into "+AtRemind.TABLE_NAME+" ("+AtRemind.TEXT+","+AtRemind.TIME+") values('@所有人','"+time+"'); ");
        SystemClock.sleep(5);
        time=System.currentTimeMillis();
        Log.i("hongtao.fu","time= "+time);
        sqLiteDatabase.execSQL(" insert into "+AtRemind.TABLE_NAME+" ("+AtRemind.TEXT+","+AtRemind.TIME+") values('@ALL','"+time+"'); ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
