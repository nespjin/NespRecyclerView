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

import com.nesp.demo.nesprecyclerviewdemo.model.DataModel;

import java.util.List;

/**
 * @author <a href="mailto:1756404649@qq.com">Jinzhaolu Email:1756404649@qq.com</a>
 * @team NESP Technology
 * @time: Created 19-1-3 下午6:07
 * @project NespRecyclerView
 **/
public interface ILoadDataPresenter {

    void doLoad(List<DataModel> dataModelList);

    void refresh();

}
