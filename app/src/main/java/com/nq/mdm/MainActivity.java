package com.nq.mdm;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.forsuntech.nq.manage.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context;
    /**
     * 系统根路径
     */
    private String apkPath;
    /**
     * 应用文件名
     */
    private static final String APKFOLDER = "/MDM_MANAGER";

    /**
     * GX-MDM 文件名
     */
    private static String MDMNAME;
    /**
     * GX-MDM-ADMIN 文件名
     */
    private static String MDMADMINNAME;

    /**
     * ZY-MDM 文件名
     */
    private final static String EMMNAME="/EMM.apk";


    /**
     * ZY-MDM 包名
     */
    private static String EMMHELPER = "com.uusafe.emm.android.helper";

    /**
     * GX-MDM版本号
     */
    private static String GXMDMCODE = "610000000";
    /**
     * GX-MDM 包名
     */
    private static String GXMDMADMIN = "com.nq.mdm.admin";
    private static String GXMDM = "com.nq.mdm";

    private Button btnInstallemm;
    private Button btnReplaceMdm;
    private Button btnReplaceMdmAdmin;
    private Button btnReboot;

    private View imgInstallemm;
    private View imgReplaceMdm;
    private View imgReplaceMdmAdmin;
    private View imgReboot;

    private ImageView imgReplaceemmTip;
    private ImageView imgReplaceMdmAdminTip;
    private ImageView imgReplaceMdmTip;


    private LinearLayout llInstallemm;
    private LinearLayout llReplaceMdm;
    private LinearLayout llReplaceMdmAdmin;
    private LinearLayout llReboot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
        context = this;
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showCheck();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
//                    isStorageOK(APKFOLDER);
                }else{
                    Toast.makeText(context,"您拒绝了访问SD卡的权限",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void showCheck() {




        if (findPkg(EMMHELPER)) {
            imgInstallemm.setVisibility(View.VISIBLE);
            llInstallemm.setVisibility(View.GONE);
        } else {
            imgInstallemm.setVisibility(View.INVISIBLE);
            llInstallemm.setVisibility(View.VISIBLE);

            llReplaceMdm.setVisibility(View.GONE);
            llReplaceMdmAdmin.setVisibility(View.GONE);
        }


        if (findPkg(GXMDMADMIN)) {
            if (islaster(GXMDMADMIN)) {
                imgReplaceMdmAdmin.setVisibility(View.VISIBLE);
                llReplaceMdmAdmin.setVisibility(View.GONE);
            } else {
                imgReplaceMdmAdmin.setVisibility(View.INVISIBLE);
                llReplaceMdmAdmin.setVisibility(View.VISIBLE);

                llReplaceMdm.setVisibility(View.GONE);
                llInstallemm.setVisibility(View.GONE);
            }
        } else {
            imgReplaceMdmAdmin.setVisibility(View.VISIBLE);
            llReplaceMdmAdmin.setVisibility(View.GONE);
        }
        if (findPkg(GXMDM)) {
            if (islaster(GXMDM)) {
                imgReplaceMdm.setVisibility(View.VISIBLE);
                llReplaceMdm.setVisibility(View.GONE);
            } else {
                imgReplaceMdm.setVisibility(View.INVISIBLE);
                llReplaceMdm.setVisibility(View.VISIBLE);

                llReplaceMdmAdmin.setVisibility(View.GONE);
                llInstallemm.setVisibility(View.GONE);
            }
        } else {
            imgReplaceMdm.setVisibility(View.VISIBLE);
            llReplaceMdm.setVisibility(View.GONE);
        }
        if((imgInstallemm.getVisibility()==View.VISIBLE)&&(imgReplaceMdmAdmin.getVisibility()==View.VISIBLE)&&(imgReplaceMdm.getVisibility()==View.VISIBLE)){
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setTitle("注意：");
            dialog.setMessage("已完成所有操作，请自行重启系统并卸载该软件！");
            dialog.setCancelable(false);
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            }).show();
        }
    }

    private void init() {
        btnInstallemm = (Button) findViewById(R.id.btn_installemm);
        btnInstallemm.setOnClickListener(this);
        btnReplaceMdm = (Button) findViewById(R.id.btn_replacemdm);
        btnReplaceMdm.setOnClickListener(this);
        btnReplaceMdmAdmin = (Button) findViewById(R.id.btn_replacemdmadmin);
        btnReplaceMdmAdmin.setOnClickListener(this);
        btnReboot= (Button) findViewById(R.id.btn_reboot);
        btnReboot.setOnClickListener(this);

        imgInstallemm = findViewById(R.id.img_installemm);
        imgReplaceMdm = findViewById(R.id.img_replacemdm);
        imgReplaceMdmAdmin = findViewById(R.id.img_replacemdmadmin);
        imgReboot=findViewById(R.id.btn_reboot);

        imgReplaceemmTip=(ImageView) findViewById(R.id.img_installemmtip);
        imgReplaceMdmAdminTip=(ImageView)findViewById(R.id.img_replacemdmadmintip);
        imgReplaceMdmTip=(ImageView)findViewById(R.id.img_replacemdmtip);


        llInstallemm = (LinearLayout) findViewById(R.id.ll_installemm);
        llReplaceMdm = (LinearLayout) findViewById(R.id.ll_replacemdm);
        llReplaceMdmAdmin = (LinearLayout) findViewById(R.id.ll_replacemdmadmin);
        llReboot= (LinearLayout) findViewById(R.id.ll_reboot);


        //根据机器品牌初始化提示图片与相关应用
        if(Build.BRAND.equalsIgnoreCase("ZTE")){
//            imgReplaceemmTip.setImageResource(R.mipmap.zte_install);
//            imgReplaceMdmAdminTip.setImageResource(R.mipmap.zte_install);
//            imgReplaceMdmTip.setImageResource(R.mipmap.zte_install);
//
//            MDMNAME="/MDM-ZTE-SIGNED.apk";
//            MDMADMINNAME="/MDM-ADMIN-ZTE-SIGNED.apk";

        }else if(Build.BRAND.equalsIgnoreCase("GIONEE")){
            imgReplaceemmTip.setImageResource(R.mipmap.gionee_install);
            imgReplaceMdmAdminTip.setImageResource(R.mipmap.gionee_install);
            imgReplaceMdmTip.setImageResource(R.mipmap.gionee_install);

            MDMNAME="/MDM-GIONEE-SIGNED.apk";
            MDMADMINNAME="/MDM-ADMIN-GIONEE-SIGNED.apk";

        }else if(Build.BRAND.equalsIgnoreCase("OPPO")){
            imgReplaceemmTip.setImageResource(R.mipmap.oppo_install);
            imgReplaceMdmAdminTip.setImageResource(R.mipmap.oppo_install);
            imgReplaceMdmTip.setImageResource(R.mipmap.oppo_install);
            MDMNAME="/MDM-OPPO-SIGNED.apk";
            MDMADMINNAME="/MDM-ADMIN-OPPO-SIGNED.apk";
        }else if (Build.BRAND.equalsIgnoreCase("COOLPAD")){
            imgReplaceemmTip.setImageResource(R.mipmap.coolpad_install);
            imgReplaceMdmAdminTip.setImageResource(R.mipmap.coolpad_install);
            imgReplaceMdmTip.setImageResource(R.mipmap.coolpad_install);
            MDMNAME="/MDM-COOLPAD-SIGNED.apk";
            MDMADMINNAME="/MDM-ADMIN-COOLPAD-SIGNED.apk";
        }else{

        }
    }

//    /**
//     * 隐藏图标
//     */
//    private void hideIcon(){
//        PackageManager packageManager = getPackageManager();
//        ComponentName componentName = new ComponentName(this, MainActivity.class);
//        int res = packageManager.getComponentEnabledSetting(componentName);
//        if (res == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT
//                || res == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
//            // 隐藏应用图标
//            packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//                    PackageManager.DONT_KILL_APP);
//        } else {
//            // 显示应用图标
////            packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,
////                    PackageManager.DONT_KILL_APP);
//        }
//    }


    /**
     * 将应用中apk解压到SD卡中
     *
     * @param strInFileName  等待解压的文件名称
     * @param strOutFileName 解压后的文件名称
     * @throws IOException
     */
    private void copyBigDataToSD(String strInFileName, String strOutFileName) throws IOException {
        InputStream myInput;
        OutputStream myOutput = new FileOutputStream(strOutFileName);
        myInput = getAssets().open(strInFileName.substring(1));
        byte[] buffer = new byte[1024];
        int length = myInput.read(buffer);
        while (length > 0) {
            myOutput.write(buffer, 0, length);
            length = myInput.read(buffer);
        }
        myOutput.flush();
        myInput.close();
        myOutput.close();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(strOutFileName)), "application/vnd.android.package-archive");
        startActivity(intent);

    }

    @Override
    public void onClick(View view) {

        if(TextUtils.isEmpty(MDMADMINNAME)||TextUtils.isEmpty(MDMNAME)){
            Snackbar.make(view,"不支持的机型："+Build.BRAND,Snackbar.LENGTH_INDEFINITE).setAction("退出", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            }).show();
            return ;
        }
        switch (view.getId()) {

            case R.id.btn_replacemdm:


                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                } else {

                    if (isStorageOK(APKFOLDER)) {
                        String mdmPath = apkPath + MDMNAME;
                        try {
                            copyBigDataToSD(MDMNAME, mdmPath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                break;
            case R.id.btn_replacemdmadmin:
                if (isStorageOK(APKFOLDER)) {
                    String mdmAdminPath = apkPath + MDMADMINNAME;
                    try {
                        copyBigDataToSD(MDMADMINNAME, mdmAdminPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btn_installemm:
                if (isStorageOK(APKFOLDER)) {
                    String emmPath = apkPath + EMMNAME;
                    try {
                        copyBigDataToSD(EMMNAME, emmPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            default:
                break;
        }
    }


    /**
     * 安装包是否已存在
     *
     * @param packName
     * @return
     */
    private boolean findPkg(String packName) {

        PackageManager packageManager = getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        for (PackageInfo info : packageInfos) {
            if (info.packageName.equals(packName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 查看GXMDM版本是否为最新
     *
     * @return
     */
    private boolean islaster(String pkgName) {
        PackageManager packageManager = getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        for (PackageInfo info : packageInfos) {
            if (info.packageName.equals(pkgName)) {
                String code = String.valueOf(info.versionCode);
                if (code.equals(GXMDMCODE)) {
                    return true;
                }
            }
        }
        return false;
    }


    private boolean isStorageOK(String path) {
            String status = Environment.getExternalStorageState();
            if (!status.equals(Environment.MEDIA_MOUNTED)) {
                Toast.makeText(context, "未找到SD卡", Toast.LENGTH_SHORT).show();
                return false;
            }
        if(ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            return false;
        }else {
            apkPath = Environment.getExternalStorageDirectory().getPath() + path;
            File file1 = null;
            try {
                file1 = new File(apkPath);
                if (!file1.exists()) {
                    file1.mkdir();
                    return true;
                } else {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "创建文件失败", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
    }
}
