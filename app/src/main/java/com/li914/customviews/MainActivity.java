package com.li914.customviews;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.li914.customviews.customview.NavBottomBarView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private NavBottomBarView conversation,contact,plugin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }
    private void initView(){
        conversation=findViewById(R.id.conversation);
        contact=findViewById(R.id.contact);
        plugin=findViewById(R.id.plugin);
        conversation.setOnClickListener(this);
        contact.setOnClickListener(this);
        plugin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.conversation:
                reSetIcon();
                conversation.setmBigIcon(R.drawable.conversation_selected_big);
                conversation.setmSmallIcon(R.drawable.conversation_selected_small);
                conversation.setNameTextColor(R.color.seleted);
                showToast("消息");
                break;
            case R.id.contact:
                reSetIcon();
                contact.setNameTextColor(R.color.seleted);
                contact.setmSmallIcon(R.drawable.contact_selected_small);
                contact.setmBigIcon(R.drawable.contact_selected_big);
                showToast("联系人");
                break;
            case R.id.plugin:
                reSetIcon();
                plugin.setmBigIcon(R.drawable.plugin_selected_big);
                plugin.setmSmallIcon(R.drawable.plugin_selected_small);
                plugin.setNameTextColor(R.color.seleted);
                showToast("动态");
                break;
                default:
                    break;
        }
    }

    //恢复状态
    private void reSetIcon(){
        conversation.setmBigIcon(R.drawable.conversation_normal_big);
        conversation.setmSmallIcon(R.drawable.conversation_normal_small);
        conversation.setNameTextColor(R.color.normal);

        contact.setNameTextColor(R.color.normal);
        contact.setmSmallIcon(R.drawable.contact_normal_small);
        contact.setmBigIcon(R.drawable.contact_normal_big);

        plugin.setmBigIcon(R.drawable.plugin_normal_big);
        plugin.setmSmallIcon(R.drawable.plugin_normal_big);
        plugin.setNameTextColor(R.color.normal);
    }

    private void showToast(String msg){
        Toast.makeText(MainActivity.this,msg,Toast.LENGTH_LONG).show();
    }
}
