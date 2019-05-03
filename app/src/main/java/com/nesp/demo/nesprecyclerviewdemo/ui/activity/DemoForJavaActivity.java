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

package com.nesp.demo.nesprecyclerviewdemo.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.nesp.demo.nesprecyclerviewdemo.R;
import com.nesp.demo.nesprecyclerviewdemo.model.DataModel;
import com.nesp.demo.nesprecyclerviewdemo.presenter.ILoadDataPresenter;
import com.nesp.demo.nesprecyclerviewdemo.presenter.LoadDataPresenterImpl;
import com.nesp.demo.nesprecyclerviewdemo.ui.adapter.RecyclerViewAdapter;
import com.nesp.sdk.android.widget.NespRecyclerView;

import java.util.ArrayList;
import java.util.List;


/**
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * @team NESP Technology
 * @time: Created 19-1-1 下午4:45
 * @project NespRecyclerViewDemo
 **/
public class DemoForJavaActivity extends BaseActivity implements ILoadDataVIew, Thread.UncaughtExceptionHandler {

    private static final String TAG = "DemoForJavaActivity";

    private NespRecyclerView nespRecyclerView;
    private ProgressBar progressBar;
    private ILoadDataPresenter iLoadDataPresenter;
    private List<DataModel> dataModelList = new ArrayList<>();
    private Boolean isFistLoad = true;
    private RecyclerViewAdapter recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.title_activity_demo_for_java));
        setContentView(R.layout.activity_demo_for_java);
        progressBar = findViewById(R.id.activity_pb);

        iLoadDataPresenter = new LoadDataPresenterImpl(this);
        dataModelList.clear();
        iLoadDataPresenter.doLoad(dataModelList);
    }

    @Override
    protected void initView() {
        nespRecyclerView = findViewById(R.id.activity_nrcv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
        recyclerViewAdapter = new RecyclerViewAdapter(context, dataModelList);
        nespRecyclerView.setLayoutManager(gridLayoutManager);
        nespRecyclerView.setAdapter(recyclerViewAdapter);
        nespRecyclerView
                .setOnRefreshListener(() -> iLoadDataPresenter.refresh())
                // Set Content Item Click Listener
                .setOnContentItemClickListener((holder, contentPosition) -> showToast("Click Content Item " + contentPosition))
                // Set Default Empty View Or call setEmptyView(int),setEmptyView(View)
                .setDefaultEmptyView()
                // Set Load More Listener
                .setOnLoadMoreListener(() -> iLoadDataPresenter.doLoad(dataModelList));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Add Header View");
        menu.add(0, 1, 1, "Add Footer View");
        menu.add(0, 2, 2, "Remove Header View");
        menu.add(0, 3, 3, "Remove Footer View");
        menu.add(0, 4, 4, "Delete All Data");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                // Set Header
                nespRecyclerView.setHeaderView(R.layout.header);
                return true;
            case 1:
                // Set Footer
                nespRecyclerView.setFooterView(R.layout.footer);
                return true;
            case 2:
                // Remove Header
                nespRecyclerView.removeHeaderView();
                return true;
            case 3:
                // Remove Footer
                nespRecyclerView.removeFooterView();
                return true;
            case 4:
                dataModelList.clear();
                nespRecyclerView.notifyLoadMoreSuccessful(false);
//                recyclerViewAdapter.notifyDataSetChanged();
                showToast("Deleted all data");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoadPrepare() {
        if (!isFistLoad) return;
        showProgressBar();
    }

    @Override
    public void onLoadFailed() {
        hideProgressBar();
        if (!isFistLoad) {
            nespRecyclerView.notifyLoadMoreFailed();
        }
        showToast("Load Failed");
    }

    @Override
    public void onLoadSuccess() {
        hideProgressBar();
        if (isFistLoad) {
            isFistLoad = false;
            initView();
            recyclerViewAdapter.notifyDataSetChanged();
        } else {
            nespRecyclerView.notifyLoadMoreSuccessful(dataModelList.size() <= 100);
            showToast("Load Success");
        }
    }

    @Override
    public void onRefreshFailed() {
        nespRecyclerView.notifyRefreshFinish(false);
    }

    @Override
    public void onRefreshSuccess() {
        nespRecyclerView.notifyRefreshFinish(true);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Log.e(TAG, "DemoForJavaActivity.uncaughtException: e \n" + e);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
