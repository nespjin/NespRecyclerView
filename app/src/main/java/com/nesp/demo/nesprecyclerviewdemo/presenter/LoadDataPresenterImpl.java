/*
 *
 * Copyright (c) 2019 NESP Technology Corporation. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms and conditions of the GNU General Public License,
 * version 3, as published by the Free Software Foundation.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License.See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * If you have any questions or if you find a bug,
 * please contact the author by email or ask for Issues.
 *
 * Author:JinZhaolu <1756404649@qq.com>
 *
 */

package com.nesp.demo.nesprecyclerviewdemo.presenter;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.nesp.demo.nesprecyclerviewdemo.model.DataModel;
import com.nesp.demo.nesprecyclerviewdemo.ui.activity.ILoadDataVIew;

import java.util.List;
import java.util.Random;

/**
 * @author <a href="mailto:1756404649@qq.com">Jinzhaolu Email:1756404649@qq.com</a>
 * @team NESP Technology
 * @time: Created 19-1-3 下午6:10
 * @project NespRecyclerView
 **/
public class LoadDataPresenterImpl implements ILoadDataPresenter {

    private ILoadDataVIew iLoadDataView;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (iLoadDataView != null) iLoadDataView.onLoadFailed();
                    break;
                case 1:
                    if (iLoadDataView != null) iLoadDataView.onLoadSuccess();
                    break;
            }
        }
    };


    @SuppressLint("HandlerLeak")
    private Handler handlerRefresh = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (iLoadDataView != null) iLoadDataView.onRefreshFailed();
                    break;
                case 1:
                    if (iLoadDataView != null) iLoadDataView.onRefreshSuccess();
                    break;
            }
        }
    };

    public LoadDataPresenterImpl(ILoadDataVIew iLoadDataView) {
        this.iLoadDataView = iLoadDataView;
    }

    private int j = 0;
    private Boolean isFirstLoad = true;

    @Override
    public void doLoad(List<DataModel> dataModelList) {
        if (iLoadDataView != null) iLoadDataView.onLoadPrepare();
        new Thread(() -> {
            Looper.prepare();
            Message message = new Message();

            Boolean loadError = getRandomBooleanWithWeights(3, 10);
            if (isFirstLoad) {
                loadError = false;
                isFirstLoad = false;
            }

            if (loadError) {
                message.what = 0;
            } else {

                try {
                    //Imitation of network access
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    message.what = 0;
                    handler.sendMessage(message);
                    return;
                }

                for (int i = 0; i < 20; i++) {
                    dataModelList.add(new DataModel("Name" + String.valueOf(j), "Desc" + String.valueOf(j)));
                    j++;
                }
                message.what = 1;
            }
            handler.sendMessage(message);
            Looper.loop();
        }).start();
    }

    @Override
    public void refresh() {
        new Thread(() -> {
            Looper.prepare();

            Message message = new Message();

            try {
                Thread.sleep(3000);
                message.what = 1;
            } catch (InterruptedException e) {
                e.printStackTrace();
                message.what = 0;
            }
            handlerRefresh.sendMessage(message);
            Looper.loop();
        }).start();
    }

    private Boolean getRandomBooleanWithWeights(int trueSum, int totalSum) {
        float weightsOfTrue = trueSum / totalSum;
        if (weightsOfTrue > 1) return true;
        else {
            Boolean[] total = new Boolean[totalSum];
            for (int i = 0; i < totalSum; i++) {
                total[i] = i < trueSum;
            }

            int ranI = new Random().nextInt(totalSum - 1) % (totalSum);
            return total[ranI];
        }
    }

}
