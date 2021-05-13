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

package com.jobesk.nourv.locator.viewModel;


import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.jobesk.nourv.locator.model.IManagedBeacon;
import com.jobesk.nourv.locator.model.TrackedBeacon;
import com.jobesk.nourv.locator.ui.fragment.BeaconFragment;

/**
 * Created by vitas on 19/10/15.
 */
public class DetectedBeaconViewModel extends BeaconViewModel {

    public DetectedBeaconViewModel(@NonNull BeaconFragment fragment, @NonNull IManagedBeacon managedBeacon) {
        super(fragment, managedBeacon);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void clickBeacon() {
        mFragment.selectBeacon(new TrackedBeacon(mManagedBeacon));
    }
}
