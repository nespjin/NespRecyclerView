package com.nesp.demo.nesprecyclerviewdemo.ui.activity;

/**
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * @team NESP Technology
 * @time: Created 19-1-3 下午6:12
 * @project NespRecyclerView
 **/
public interface ILoadDataVIew {

    void onLoadPrepare();

    void onLoadFailed();

    void onLoadSuccess();

    void onRefreshFailed();

    void onRefreshSuccess();
}
