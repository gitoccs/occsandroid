package com.occs.ldsoft.occs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import static com.occs.ldsoft.occs.WebFunctionHelper.FetchLogoutTask;

/**
 * Created by yeliu on 15/8/12.
 */
public class LogoutFragment extends Fragment {

    Button logoutBtn;
    private boolean isLoadingWeb = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.logout_layout, container, false);
        logoutBtn = (Button) v.findViewById(R.id.logout_btn);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!FetchLogoutTask.isLoadingWeb && Tools.isNetworkConnected(getActivity())) {
                    new FetchLogoutTask(LogoutFragment.this){}.execute();
                }
            }
        });
        return v;
    }
}
