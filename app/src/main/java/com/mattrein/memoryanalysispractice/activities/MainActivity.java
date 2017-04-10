package com.mattrein.memoryanalysispractice.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.mattrein.memoryanalysispractice.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private Unbinder unbinder;

    @BindView(R.id.listenerLeak)
    Button listenerLeakButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        listenerLeakButton.setOnClickListener(v -> goToListenerLeakActivity());
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }

        super.onDestroy();
    }

    @OnClick(R.id.listenerLeak)
    public void goToListenerLeakActivity() {
        Timber.d("ListenerLeak Button Clicked!");
        startActivity(new Intent(this, ListenerLeakActivity.class));
    }

}
