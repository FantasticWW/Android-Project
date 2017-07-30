package com.example.com.customdialog;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openDialog(View view) {
        Dialog dialog = new Dialog(this, R.style.MyDialog);
        //加载布局
        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog,null);

        dialog.setContentView(dialogView);

        //设置对话框显示的位置
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.y = 10;
        dialogWindow.setAttributes(lp);

        dialog.show();

    }

}
