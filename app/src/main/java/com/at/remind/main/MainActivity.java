package com.at.remind.main;

import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.at.remind.R;
import com.at.remind.db.AtRemind;
import com.at.remind.db.AtRemindDb;
import com.at.remind.ui.VolumeView;
import com.at.remind.util.L;
import com.at.remind.util.SP;
import com.at.remind.util.SoundPlayer;

import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 1101;
    private static final int AUDIO_REQUEST_CODE=1001;
    private SeekBar mVolumeView;
    private GridView mGridView;
    private GridViewAdapter mGridViewAdapter;
    private Switch mSwitchLockScreenNotification;
    private Switch mSwitchNotCurrentAppNotification;
    private TextView mOpenPermission;
    private TextView mAddNotificationPermission;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                addRemindDialog();
            }
        });
        //@提醒服务没有启动
        startServiceDialog();
        init();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 引导用户开启服务
     */
    public void startServiceDialog(){
        //如果服务没有打开，就弹出一个对话框，提醒用户开启服务
        if (!isEnabled()) {
            //获得屏幕的像素密度
            float scale = getResources().getDisplayMetrics().density;
            //计算出一50dp相当与多少px
            int size = (int) (50 * scale + 0.5f);// dp 转 px
            //从资源中加载一张图片到内存里
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.at_remind_logo);
            //将图片压缩/放大为size*size的大小
            bitmap = Bitmap.createScaledBitmap(bitmap, size, size, false);
            //创建一个Alertilaog的builder，并且在Dialog上面设置提提示的信息和事件的处理代码
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(new BitmapDrawable(bitmap));
            builder.setTitle("开启服务");
            builder.setMessage("现在就去开启@提醒服务");
            builder.setPositiveButton("去开启", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        //如果用户点击了去开启，那么就通过一个意图去启动手动开启服务的界面
                        Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            builder.setNegativeButton("等一下", null);
            //创建出Dilaog
            AlertDialog dialog = builder.create();
            //将创建出来的Dialog显示在界面上
            dialog.show();
        }
    }

    /**
     * 判断NotificationListenerService是否开启了
     * @return
     */
    private boolean isEnabled() {
        //获取到本应用的报名
        String pkgName = getPackageName();
        //通过报名去系统的安全设置中过得所有的服务开启信息
        final String flat = Settings.Secure.getString(getContentResolver(), ENABLED_NOTIFICATION_LISTENERS);
        //如果获取到的信息不为null，就进行下一步
        if (!TextUtils.isEmpty(flat)) {
            //通过“：”将flat拆分为一个数组
            final String[] names = flat.split(":");
            //遍历数组，并且查找pakName
            for (int i = 0; i < names.length; i++) {
                ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    //如果在拆分的数组中找到了pkgName，就说明服务已经开启了
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        //服务没有开启，就返回false
        return false;
    }
    public void addRemindDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater= LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.add_remind_dialog,null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();

        final EditText keywordInput=(EditText)view.findViewById(R.id.keyword_input);

        Button allow=(Button)view.findViewById(R.id.allow);
        Button refuse=(Button)view.findViewById(R.id.refuse);

        keywordInput.setSelection(1);
        allow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AtRemindDb atRemindDb=AtRemindDb.getInstance(getApplicationContext());
                atRemindDb.inset(new AtRemind(keywordInput.getText().toString(), new Date().getTime()));
                List<AtRemind> list=atRemindDb.select();
                mGridViewAdapter.setmList(list);
                mGridViewAdapter.notifyDataSetChanged();
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
    public void init(){
        mVolumeView = (SeekBar) findViewById(R.id.volume_view);
        String v=SP.getString(getApplicationContext(),SP.AT_REMIND_VOLUME);
        if("".equals(v)){
            mVolumeView.setProgress(8);
        }else{
            mVolumeView.setProgress(Integer.valueOf(v));
        }
        AudioManager audioManager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mVolumeView.setMax(maxVolume);
        mVolumeView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                SP.putString(getApplication(),SP.AT_REMIND_VOLUME,String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //SoundPlayer.playSouned(getApplicationContext(),R.raw.at_remind_1);
                SoundPlayer.playSouned(getApplicationContext());
            }
        });


        AtRemindDb atRemindDb=AtRemindDb.getInstance(this);
        List<AtRemind> list=atRemindDb.select();
        mGridView = (GridView)findViewById(R.id.grid_view);
        mGridViewAdapter=new GridViewAdapter(this,list);
        mGridView.setAdapter(mGridViewAdapter);

        boolean b=SP.getBoolean(getApplicationContext(),SP.TIPS_SCREEN_ON_OFF);
        mSwitchLockScreenNotification =(Switch)findViewById(R.id.switch_lock_screen_notification);
        mSwitchNotCurrentAppNotification=(Switch)findViewById(R.id.switch_not_current_app_notification);
        mOpenPermission=(TextView)findViewById(R.id.open_permission);
        mAddNotificationPermission=(TextView)findViewById(R.id.add_notification_permission);
        mSwitchLockScreenNotification.setChecked(b);
        mSwitchNotCurrentAppNotification.setChecked(!b);
        openPermissionShowHelper();
        mSwitchLockScreenNotification.setOnCheckedChangeListener(this);
        mSwitchNotCurrentAppNotification.setOnCheckedChangeListener(this);
    }

    //检测用户是否对本app开启了“Apps with usage access”权限
    private boolean hasPermission() {
        AppOpsManager appOps = (AppOpsManager)
                getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), getPackageName());
        }
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    /**
     * 当用户选择完铃声过后，回到主界面时就会调用这个方法，这个方法中会处理用户返回的数据
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //通过判断请求码，判断是否请求的是选择铃声
        if(requestCode==AUDIO_REQUEST_CODE){
            if(data==null)
                return ;
            //得到返回的铃声的uri
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if(uri==null)
                return;
            L.i("atremind","uri= "+uri+ "  "+uri.getPath());
            //将用户选择的铃声的uri存储起来
            SP.putString(getApplicationContext(),SP.AT_REMIND_SOUND_URI,uri.toString());

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        openPermissionShowHelper();
    }

    public void openPermissionShowHelper(){

       if(mSwitchNotCurrentAppNotification.isChecked()==true ){
           if(!hasPermission()){
               mOpenPermission.setVisibility(View.VISIBLE);
               mOpenPermission.setText("打开权限");
           }else{
               mOpenPermission.setVisibility(View.VISIBLE);
               mOpenPermission.setText("权限已开");
           }
       }else{
           mOpenPermission.setVisibility(View.GONE);
       }
        SP.putBoolean(getApplicationContext(),SP.TIPS_SCREEN_ON_OFF,mSwitchLockScreenNotification.isChecked());

        if(isEnabled()) {
            mAddNotificationPermission.setText("必要服务已打开");
        }else{
            mAddNotificationPermission.setText("请打开必要服务");
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()){
            case R.id.switch_lock_screen_notification:
                mSwitchNotCurrentAppNotification.setChecked(!b);
                break;
            case R.id.switch_not_current_app_notification:
                mSwitchLockScreenNotification.setChecked(!b);
                break;
        }
        openPermissionShowHelper();
    }
    public void openPermission(View view){
        startActivityForResult(
                new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
    }
    public void openService(View view){
        Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
        startActivity(intent);
    }


    /**
     * 通过系统的RingtoneManager服务选择提示音
     * @param view
     */
    public void selectNotification(View view){
        //创建一个意图，去打开系统提供的铃声选择Activity
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false);
        //设置选择的铃声类型为Notification类型
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,RingtoneManager.TYPE_NOTIFICATION);
        //以请求结果的方式启动Activity，并且设置请求码为1001
        this.startActivityForResult(intent,1001);
    }
}
