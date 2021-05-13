/*
 *
 *  Copyright (c) 2015 SameBits UG. All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.jobesk.nourv.locator.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.jobesk.nourv.Activities.LinkProductActivity;
import com.jobesk.nourv.BuildConfig;
import com.jobesk.nourv.R;
import com.jobesk.nourv.locator.model.TrackedBeacon;
import com.jobesk.nourv.locator.ui.activity.MainNavigationActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * Created by vitas on 8/12/15.
 */
public class BeaconFragment extends Fragment {

    protected Unbinder unbinder;
    protected boolean isDebug() {
        return BuildConfig.DEBUG;
    }
    private OnTrackedBeaconSelectedListener mBeaconSelectedListener;

    public interface OnTrackedBeaconSelectedListener {
        void onBeaconSelected(TrackedBeacon beacon);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() instanceof MainNavigationActivity) {
            ((MainNavigationActivity) getActivity()).swappingFloatingIcon();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mBeaconSelectedListener = (OnTrackedBeaconSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnTrackedBeaconSelectedListener");
        }
    }


    public class EmptyView {

        @BindView(R.id.empty_text)
        TextView text;

        public EmptyView(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void selectBeacon(TrackedBeacon trackedBeacon) {
        if (mBeaconSelectedListener != null) {
            Intent intent=new Intent(getContext(), LinkProductActivity.class);
            intent.putExtra("obj",trackedBeacon);
            startActivity(intent);
            //mBeaconSelectedListener.onBeaconSelected(trackedBeacon);
        }
    }

}