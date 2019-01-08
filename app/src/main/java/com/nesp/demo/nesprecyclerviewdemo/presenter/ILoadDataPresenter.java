package com.nesp.demo.nesprecyclerviewdemo.presenter;

import com.nesp.demo.nesprecyclerviewdemo.model.DataModel;

import java.util.List;

/**
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * @team NESP Technology
 * @time: Created 19-1-3 下午6:07
 * @project NespRecyclerView
 **/
public interface ILoadDataPresenter {

    void doLoad(List<DataModel> dataModelList);

    void refresh();

}
