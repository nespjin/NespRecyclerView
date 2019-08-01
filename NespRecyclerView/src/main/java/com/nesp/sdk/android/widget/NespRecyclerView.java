/*
 *
 *   Copyright (c) 2019  NESP Technology Corporation. All rights reserved.
 *
 *   This program is free software; you can redistribute it and/or modify it
 *   under the terms and conditions of the GNU General Public License,
 *   version 2, as published by the Free Software Foundation.
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License.See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *   If you have any questions or if you find a bug,
 *   please contact the author by email or ask for Issues.
 *
 *   Author:JinZhaolu <1756404649@qq.com>
 */

package com.nesp.sdk.android.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * @team NESP Technology
 * @time: Created 19-1-3 上午9:08
 * @project NespRecyclerView
 **/
public class NespRecyclerView extends RecyclerView {

    //================================Normal Filed=======================================

    private Context context;

    private static final String TAG = "NespRecyclerView";
    /**
     * Internal RecyclerAdapter.
     * <p>
     * Wrapper external RecyclerAdapter.
     */
    private NespRecyclerViewAdapter nespRecyclerViewAdapter;

    private boolean isScrollTop = false;
    private boolean isScrollBottom = false;
    private boolean isScrollUp = false;
    private boolean isScrollDown = false;

    //================================Empty View,Header View,Footer View Filed=======================================

    /**
     * A fixed view which displayed when no data.
     */
    private View emptyView;

    /**
     * Drawable for default emptyView
     */
    private Drawable emptyDrawable;

    /**
     * EmptyText for default emptyView
     */
    private String emptyText = "";

    /**
     * A fixed view to appear at the top of the list.
     */
    private View headerView;
    /**
     * A fixed view to appear at the bottom of the list.
     */
    private View footerView;

    //================================Load More Filed=======================================

    private View loadMoreView;
    /**
     * Loading state
     */
    private LoadMoreState loadMoreState;
    /**
     * Loading text
     */
    private TextView tvLoadMoreText;
    /**
     * Loading progressBar
     */
    private ProgressBar pbLoadMore;
    /**
     * Loading text size
     */
    private float loadMoreTextSize;
    /**
     * Loading text color
     */
    private int loadMoreTextColor;
    /**
     * Loading view background color
     */
    private int loadMoreBackgroundColor;
    /**
     * No more data text
     */
    private String noMoreDataText;
    /**
     * Load more data text
     */
    private String loadingMoreDataText;
    /**
     * Load more data failed text
     */
    private String loadMoreDataFailedText;
    /**
     * Loading more enable
     */
    private Boolean loadMoreEnable = false;


    /**
     * The maximum number of Items in screen,if the current number of items Less than the maximum number，
     * <p>
     * will not display Load-more-view
     */
    private int maxScreenItems = -1;

    private OnLoadMoreListener onLoadMoreListener;
    private OnContentItemClickListener onContentItemClickListener;

    private String lastPbTvLoadMoreTextTemp;
    private int lastPbLoadMoreVisibilityTemp;

    /**
     * Do not display {@link #noMoreDataText} when no more data.
     * <p>
     * Default:false.
     */
    private boolean isHideNoMoreData;
    /**
     * Drawable of loading more progressBar
     */
    private Drawable loadMoreProgressIndeterminateDrawable;
    private int loadMoreProgressIndeterminateDrawableTintColor;

    /**
     * OnScrollListener for load more.
     */
    private OnScrollListener onScrollListenerForLoadMore = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (isAttachedNestedScrollView) return;
//            if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
//                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                int itemCount = layoutManager.getItemCount();
//                int lastPosition = layoutManager.findLastCompletelyVisibleItemPosition();
//                XLog.tag("bqt3").i("【总数】" + itemCount + "【位置】" + lastPosition);
//                if (lastPosition == layoutManager.getItemCount() - 1) {//容错处理，保证滑到最后一条时一定可以加载更多
//                    this.onLoadMore();
//                } else {
//                    if (itemCount > minLeftItemCount) {
//                        if (lastPosition == itemCount - minLeftItemCount) {
//                            //一定要意识到，onScrolled方法并不是一直被回调的，估计最多一秒钟几十次
//                            //所以当此条件满足时，可能并没有回调onScrolled方法，也就不会调用onLoadMore方法
//                            //所以一定要想办法弥补这隐藏的bug，最简单的方式就是当滑到最后一条时一定可以加载更多
//                            this.onLoadMore();
//                        }
//                    } else {//（第一次进入时）如果总数特别少，直接加载更多
//                        this.onLoadMore();
//                    }
//                }
//            }
            if (loadMoreEnable && loadMoreState != LoadMoreState.LOADING && dy > 0) {
                if (findLastVisibleItemPosition() == nespRecyclerViewAdapter.getItemCount() - 1) {
                    loadMoreState = LoadMoreState.LOADING;
                    if (onLoadMoreListener != null) onLoadMoreListener.onLoadMore();
                }
            }
        }
    };

    /*********************************Refresh Field*************************************/
    //TODO:Refresh Field

    private View refreshHeaderView;
    private LinearLayout linearLayoutRefreshRoot;
    private ImageView imageViewRefreshArrow;
    private TextView textViewRefresh;
    private ProgressBar progressBarRefresh;

    private OnRefreshListener onRefreshListener;

    private Boolean isRefreshing = false;
    private Boolean isRefreshEnable = true;

    /**
     * @see MotionEvent#ACTION_DOWN
     */
    private float pointYDown;
    /**
     * @see MotionEvent#ACTION_MOVE
     */
    private float pointYMove;
    private float fingerSideOffset;
    private float viewSlideOffset;

    /**
     * Within a certain range of pull-down,it will be display
     */
    private String pullDownText;
    /**
     * Outside a certain range of pull-down,it will be display
     */
    private String upToRefreshText;
    private String refreshingText;
    private String refreshSuccessText;
    private String refreshFailedText;
    private int refreshBackgroundColor;
    private float refreshTextSize;
    private int refreshTextColor;
    private int refreshArrowTintColor;
    private Drawable refreshArrowDrawable = getResources().getDrawable(R.drawable.ic_nesprecyclerview_arrow_down);
    private Drawable refreshProgressIndeterminateDrawable;
    private int refreshProgressIndeterminateDrawableTintColor;
    private float refreshMaxOffset;
    private float refreshMinOffset;
    private float refreshRotateOffset;


    private LinearLayout.LayoutParams linearLayoutRefreshRootLayoutParams;
    private int linearLayoutRefreshingHeight = 122;
    private int linearLayoutInitHeight = 1;
    /**
     * When {@link #isScrollTop} changes from false to true, it does not call {@link MotionEvent#ACTION_DOWN} but calls {@link MotionEvent#ACTION_MOVE} directly,
     * so it won't initialize {@link #pointYDown} in {@link MotionEvent#ACTION_DOWN}.
     * <p>
     * To fix this bug, we set a flag {@link #isCalledActionDown} to determine if it calls {@link MotionEvent#ACTION_DOWN},
     * if not, we will use the first value of {@link #pointYMove} in the {@link MotionEvent#ACTION_DOWN} event to initialize { @link #pointYDown}.
     */
    private Boolean isCalledActionDown = false;

    private Boolean isCalledActionMove = false;

    private Boolean isRefreshRowRotatedUp = false;

    /*********************************Field End*************************************/
    //TODO:Field End
    public NespRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public NespRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NespRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NespRecyclerView);
        initAttrs(context, typedArray);
        typedArray.recycle();
    }

    private void initAttrs(Context context, TypedArray typedArray) {
        /*
         * Add the customize of defaultEmptyView from layout
         */
        emptyText = typedArray.getString(R.styleable.NespRecyclerView_emptyText);

        int emptyDrawableResId = typedArray.getResourceId(R.styleable.NespRecyclerView_emptyDrawable, -1);
        if (emptyDrawableResId != -1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                emptyDrawable = getResources().getDrawable(emptyDrawableResId, context.getTheme());
            } else {
                //noinspection deprecation
                emptyDrawable = getResources().getDrawable(emptyDrawableResId);
            }
        }

        /*
         *  Add the customize of loadMoreView from layout
         */
        maxScreenItems = typedArray.getInteger(R.styleable.NespRecyclerView_maxScreenItems, -1);
        loadMoreEnable = typedArray.getBoolean(R.styleable.NespRecyclerView_loadMoreEnable, false);
        isHideNoMoreData = typedArray.getBoolean(R.styleable.NespRecyclerView_hideNoMoreData, false);
        loadMoreTextColor = typedArray.getColor(R.styleable.NespRecyclerView_loadMoreTextColor, Color.GRAY);
        loadMoreTextSize = typedArray.getDimensionPixelSize(R.styleable.NespRecyclerView_loadMoreTextSize, getResources().getDimensionPixelSize(R.dimen.load_more_text_size));

        loadMoreBackgroundColor = typedArray.getColor(R.styleable.NespRecyclerView_loadMoreBackgroundColor, Color.TRANSPARENT);
        loadMoreProgressIndeterminateDrawableTintColor = typedArray.getColor(R.styleable.NespRecyclerView_loadMoreProgressIndeterminateDrawableTintColor, 0);

        int indeterminateDrawableResId = typedArray.getResourceId(R.styleable.NespRecyclerView_loadMoreProgressIndeterminateDrawable, -1);
        if (indeterminateDrawableResId != -1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                loadMoreProgressIndeterminateDrawable = getResources().getDrawable(indeterminateDrawableResId, context.getTheme());
            } else {
                //noinspection deprecation
                loadMoreProgressIndeterminateDrawable = getResources().getDrawable(indeterminateDrawableResId);
            }
        }

        loadingMoreDataText = typedArray.getString(R.styleable.NespRecyclerView_loadingMoreText);
        loadMoreDataFailedText = typedArray.getString(R.styleable.NespRecyclerView_loadMoreFailedText);
        noMoreDataText = typedArray.getString(R.styleable.NespRecyclerView_noMoreDataText);
        if (TextUtils.isEmpty(loadingMoreDataText))
            loadingMoreDataText = getResources().getString(R.string.nesprecyclerview_text_loading_more);
        if (TextUtils.isEmpty(loadMoreDataFailedText))
            loadMoreDataFailedText = getResources().getString(R.string.nesprecyclerview_text_load_more_failed);
        if (TextUtils.isEmpty(noMoreDataText))
            noMoreDataText = getResources().getString(R.string.nesprecyclerview_text_no_more_data);

        /*
         *  Add the customize of refresh from layout
         */
        isRefreshEnable = typedArray.getBoolean(R.styleable.NespRecyclerView_refreshEnable, true);
        pullDownText = typedArray.getString(R.styleable.NespRecyclerView_pullDownText);
        upToRefreshText = typedArray.getString(R.styleable.NespRecyclerView_upToRefreshText);
        refreshingText = typedArray.getString(R.styleable.NespRecyclerView_refreshingText);
        refreshSuccessText = typedArray.getString(R.styleable.NespRecyclerView_refreshSuccessText);
        refreshFailedText = typedArray.getString(R.styleable.NespRecyclerView_refreshFailedText);
        if (TextUtils.isEmpty(pullDownText))
            pullDownText = getResources().getString(R.string.nesprecyclerview_text_pull_down);
        if (TextUtils.isEmpty(upToRefreshText))
            upToRefreshText = getResources().getString(R.string.nesprecyclerview_text_up_to_refresh);
        if (TextUtils.isEmpty(refreshingText))
            refreshingText = getResources().getString(R.string.nesprecyclerview_text_refreshing);
        if (TextUtils.isEmpty(refreshSuccessText))
            refreshSuccessText = getResources().getString(R.string.nesprecyclerview_text_refresh_success);
        if (TextUtils.isEmpty(refreshFailedText))
            refreshFailedText = getResources().getString(R.string.nesprecyclerview_text_refresh_failed);

        refreshBackgroundColor = typedArray.getColor(R.styleable.NespRecyclerView_refreshBackgroundColor, Color.TRANSPARENT);

        refreshTextSize = typedArray.getDimension(R.styleable.NespRecyclerView_refreshTextSize, getResources().getDimensionPixelSize(R.dimen.refresh_text_size));
        refreshTextColor = typedArray.getColor(R.styleable.NespRecyclerView_refreshTextColor, Color.GRAY);
        refreshArrowTintColor = typedArray.getColor(R.styleable.NespRecyclerView_refreshArrowTintColor, Color.parseColor("#8a8a8a"));
        refreshProgressIndeterminateDrawableTintColor = typedArray.getColor(R.styleable.NespRecyclerView_refreshProgressIndeterminateDrawableTintColor, 0);

        refreshMaxOffset = typedArray.getFloat(R.styleable.NespRecyclerView_refreshMaxOffset, 1000);
        refreshMinOffset = typedArray.getFloat(R.styleable.NespRecyclerView_refreshMinOffset, 0);
        refreshRotateOffset = typedArray.getFloat(R.styleable.NespRecyclerView_refreshRotateOffset, -1);

        int refreshArrowDrawableResId = typedArray.getResourceId(R.styleable.NespRecyclerView_refreshArrowDrawable, -1);
        int refreshProgressIndeterminateDrawableResId = typedArray.getResourceId(R.styleable.NespRecyclerView_refreshProgressIndeterminateDrawable, -1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (indeterminateDrawableResId != -1) {
                refreshArrowDrawable = getResources().getDrawable(refreshArrowDrawableResId, context.getTheme());
            }
            if (refreshProgressIndeterminateDrawableResId != -1) {
                refreshProgressIndeterminateDrawable = getResources().getDrawable(refreshProgressIndeterminateDrawableResId, context.getTheme());
            }
        } else {
            //noinspection deprecation
            if (indeterminateDrawableResId != -1) {
                refreshArrowDrawable = getResources().getDrawable(refreshArrowDrawableResId);
            }
            if (refreshProgressIndeterminateDrawableResId != -1) {
                refreshProgressIndeterminateDrawable = getResources().getDrawable(refreshProgressIndeterminateDrawableResId);
            }
        }
    }

    /*********************************API*************************************/
    //TODO:API
    /*********************************Normal API*************************************/
    //TODO:Normal API

    /**
     * The {@link NespRecyclerView} whether is currently scrolled top.
     *
     * @return {@link #isScrollTop}
     */
    public Boolean isScrollTop() {
        return this.isScrollTop;
    }

    /**
     * The {@link NespRecyclerView} whether is currently scrolled bottom.
     *
     * @return {@link #isScrollBottom}
     */
    public Boolean isScrollBottom() {
        return this.isScrollBottom;
    }

    /**
     * The {@link NespRecyclerView} whether is currently scrolling up.
     *
     * @return {@link #isScrollUp}
     */
    public Boolean isScrollUp() {
        return isScrollUp;
    }

    /**
     * The {@link NespRecyclerView} whether is currently scrolling down.
     *
     * @return {@link #isScrollDown}
     */
    public Boolean isScrollDown() {
        return isScrollDown;
    }

    /**
     * Find the first current visible position depends on LayoutManger
     *
     * @return the first visible position
     * @see #setOnRefreshListener(OnRefreshListener)
     */
    public int findFirstVisibleItemPosition() {
        int position;
        if (getLayoutManager() instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        } else if (getLayoutManager() instanceof GridLayoutManager) {
            position = ((GridLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        } else if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) getLayoutManager();
            int[] lastPositions = layoutManager.findFirstVisibleItemPositions(new int[layoutManager.getSpanCount()]);
            position = findMinPosition(lastPositions);
        } else {
            position = getLayoutManager().getItemCount() - 1;
        }
        return position;
    }

    /**
     * Find the last current visible position depends on LayoutManger
     *
     * @return the last visible position
     * @see #setOnLoadMoreListener(OnLoadMoreListener)
     */
    public int findLastVisibleItemPosition() {
        int position;
        if (getLayoutManager() instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        } else if (getLayoutManager() instanceof GridLayoutManager) {
            position = ((GridLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        } else if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) getLayoutManager();
            int[] lastPositions = layoutManager.findLastVisibleItemPositions(new int[layoutManager.getSpanCount()]);
            position = findMaxPosition(lastPositions);
        } else {
            position = getLayoutManager().getItemCount() - 1;
        }
        return position;
    }

    /**
     * Find StaggeredGridLayoutManager the last visible position
     *
     * @see #findLastVisibleItemPosition()
     */
    private int findMaxPosition(int[] positions) {
        int maxPosition = 0;
        for (int position : positions) {
            maxPosition = Math.max(maxPosition, position);
        }
        return maxPosition;
    }

    /**
     * Find StaggeredGridLayoutManager the first visible position
     *
     * @see #findFirstVisibleItemPosition()
     */
    private int findMinPosition(int[] positions) {
        int maxPosition = 0;
        for (int position : positions) {
            maxPosition = Math.min(maxPosition, position);
        }
        return maxPosition;
    }

    /**
     * It must be called after {@link #setLayoutManager(LayoutManager)} to adapter {@link GridLayoutManager} and {@link StaggeredGridLayoutManager}
     *
     * @param originAdapter originAdapter
     */
    @Override
    public void setAdapter(@Nullable Adapter originAdapter) {
        if (originAdapter == null) return;
        nespRecyclerViewAdapter = new NespRecyclerViewAdapter(originAdapter);
        super.setAdapter(nespRecyclerViewAdapter);
    }

    /**
     * adapt  {@link GridLayoutManager} for {@link NespRecyclerView} by set span size
     *
     * @param layoutManager layoutManager
     * @see NespRecyclerViewAdapter#adaptLayoutManager(LayoutManager)
     */
    @Override
    public void setLayoutManager(@Nullable LayoutManager layoutManager) {
        if (nespRecyclerViewAdapter != null) {
            nespRecyclerViewAdapter.adaptLayoutManager(layoutManager);
        }
        super.setLayoutManager(layoutManager);
    }

    @Nullable
    @Override
    public Adapter getAdapter() {
        if (nespRecyclerViewAdapter != null) return nespRecyclerViewAdapter.getOriginAdapter();
        return null;
    }

    private Boolean isAttachedNestedScrollView = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void attachToNestedScrollView(NestedScrollView nestedScrollView) {
        isAttachedNestedScrollView = true;
        nestedScrollView.setOnScrollChangeListener((OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            //dy >0 手上滑动
            //dy <0 手下滑动
            int dy = scrollY - oldScrollY;
            if (loadMoreEnable && loadMoreState != LoadMoreState.LOADING && dy > 0) {
                if (scrollY == ((NestedScrollView) v).getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    loadMoreState = LoadMoreState.LOADING;
                    if (onLoadMoreListener != null) onLoadMoreListener.onLoadMore();
                }
            }
        });
    }


    //================================Empty View,Header View,Footer View API=======================================

    /**
     * Set empty view for {@link NespRecyclerView} with view.
     * <p>
     * It must be called after {@link #setAdapter(Adapter)} and {@link #setLayoutManager(LayoutManager)}
     * <p>
     *
     * @param emptyView emptyView
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setEmptyView(View emptyView) {
        if (getAdapter() == null) {
            throw new RuntimeException("You need to call setAdapter(Adapter) before call setEmptyView()");
        }

        if (getLayoutManager() == null) {
            throw new RuntimeException("You need to call setLayoutManager(LayoutManager) before call setEmptyView()");
        }

        if (emptyView == null || this.emptyView != null) return this;
        this.emptyView = emptyView;
        nespRecyclerViewAdapter.notifyDataSetChanged();
        return this;
    }

    /**
     * Set empty view for {@link NespRecyclerView}  with layout resource id.
     *
     * <p>
     * {@link #setEmptyView(View)}
     *
     * @param emptyViewLayoutResId empty view layout resource id
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setEmptyView(@LayoutRes int emptyViewLayoutResId) {
        return setEmptyView(inflateView(emptyViewLayoutResId));
    }

    /**
     * Set default empty view for {@link NespRecyclerView} with internal empty view.
     * <p>
     * It must be called after {@link #setAdapter(Adapter)} and {@link #setLayoutManager(LayoutManager)}
     * <p>
     * {@link #setEmptyView(View)}
     *
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setDefaultEmptyView() {
        if (getAdapter() == null)
            throw new RuntimeException("You need to call setAdapter(Adapter) before call setDefaultEmptyView()");

        if (getLayoutManager() == null)
            throw new RuntimeException("You need to call setLayoutManager(LayoutManager) before call setDefaultEmptyView()");

        return setEmptyView(inflateView(R.layout.nesprecyclerview_empty));
    }

    /**
     * Set empty drawable for default empty view
     * need to call {@link #setDefaultEmptyView()} before call this method.
     *
     * @param emptyDrawable Drawable
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setEmptyDrawable(Drawable emptyDrawable) {
        this.emptyDrawable = emptyDrawable;
        return this;
    }

    /**
     * Set empty drawable for default empty view
     * need to call {@link #setDefaultEmptyView()} before call this method.
     *
     * @param emptyDrawableRes Drawable resource id
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setEmptyDrawable(@DrawableRes int emptyDrawableRes) {
        return setEmptyDrawable(getResources().getDrawable(emptyDrawableRes));
    }

    /**
     * Set empty text for default empty view
     * need to call {@link #setDefaultEmptyView()} before call this method.
     *
     * @param emptyText Text
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setEmptyText(String emptyText) {
        this.emptyText = emptyText;
        return this;
    }

    /**
     * Set empty text for default empty view,
     * need to call {@link #setDefaultEmptyView()} before call this method.
     *
     * @param emptyTextRes empty text resource id
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setEmptyText(@StringRes int emptyTextRes) {
        return setEmptyText(getResources().getString(emptyTextRes));
    }

    /**
     * Set header view for {@link NespRecyclerView} with view.
     * <p>
     * It must be called after {@link #setAdapter(Adapter)} and {@link #setLayoutManager(LayoutManager)}
     * <p>
     *
     * @param headerView header view
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setHeaderView(View headerView) {
        if (getAdapter() == null) {
            throw new RuntimeException("You need to call setAdapter(Adapter) before call setHeaderView()");
        }

        if (getLayoutManager() == null) {
            throw new RuntimeException("You need to call setLayoutManager(LayoutManager) before call setHeaderView()");
        }

        if (headerView == null || this.headerView != null) return this;
        this.headerView = headerView;
        nespRecyclerViewAdapter.notifyItemInserted(refreshHeaderView == null ? 0 : 1);
        return this;
    }

    /**
     * Set header view for {@link NespRecyclerView} with layout resource id.
     * <p>
     * {@link #setHeaderView(View)}
     *
     * @param headerViewLayoutResId header view layout resource id
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setHeaderView(@LayoutRes int headerViewLayoutResId) {
        return setHeaderView(inflateView(headerViewLayoutResId));
    }

    public View getHeaderView() {
        return headerView;
    }

    /**
     * Set footer view for {@link NespRecyclerView} with View.
     * <p>
     * It must be called after {@link #setAdapter(Adapter)} and {@link #setLayoutManager(LayoutManager)}
     * <p>
     *
     * @param footerView footer view
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setFooterView(View footerView) {
        if (getAdapter() == null) {
            throw new RuntimeException("You need to call setAdapter(Adapter) before call setFooterView()");
        }

        if (getLayoutManager() == null) {
            throw new RuntimeException("You need to call setLayoutManager(LayoutManager) before call setFooterView()");
        }

        if (footerView == null || this.footerView != null) return this;
        this.footerView = footerView;
        nespRecyclerViewAdapter.notifyItemChanged(nespRecyclerViewAdapter.getItemCount() - 1);
        return this;
    }

    /**
     * Set footer view for {@link NespRecyclerView} with layout resource id.
     * <p>
     * {@link #setFooterView(View)}
     *
     * @param footerViewLayoutResId footer view resource id
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setFooterView(@LayoutRes int footerViewLayoutResId) {
        return setFooterView(inflateView(footerViewLayoutResId));
    }

    /**
     * Remove header view of {@link NespRecyclerView}
     *
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView removeHeaderView() {
        if (this.headerView == null) return this;
        this.headerView = null;
        nespRecyclerViewAdapter.notifyItemRemoved(refreshHeaderView == null ? 0 : 1);
        return this;
    }

    /**
     * Remove footer view of {@link NespRecyclerView}
     *
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView removeFooterView() {
        if (this.footerView == null) return this;
        this.footerView = null;
        nespRecyclerViewAdapter.notifyItemRemoved(nespRecyclerViewAdapter.getItemCount() - 1);
        return this;
    }

    //================================Load More API=======================================

    /**
     * Set Listener of load more event callback
     *
     * @param onLoadMoreListener onLoadMoreListener
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        if (onLoadMoreListener == null) return this;
        this.onLoadMoreListener = onLoadMoreListener;
        this.loadMoreEnable = true;
        this.addOnScrollListener(onScrollListenerForLoadMore);
        return this;
    }

    /**
     * Set {@link NespRecyclerView} the load more feature whether is enabled
     * <p>
     * LoadMoreEnable also will be auto set when calling {@link #setOnLoadMoreListener(OnLoadMoreListener)} or {@link #changeLoadMoreUi(boolean, boolean)}
     *
     * @param loadMoreEnable default:false
     *                       true -> load more feature is enabled
     *                       false ->load more feature is not enable
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setLoadMoreEnable(Boolean loadMoreEnable) {
        this.loadMoreEnable = loadMoreEnable;
        return this;
    }

    /**
     * Set size of loading more text,default:5sp.
     * <p>
     * You always can use it in xml:
     * <code>app:loadMoreTextSize</code>
     *
     * @param loadMoreTextSize size of loading more text
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setLoadMoreTextSize(float loadMoreTextSize) {
        this.loadMoreTextSize = loadMoreTextSize;
        return this;
    }

    /**
     * Set size of loading more text from resource,default:5sp.
     * <p>
     * You always can use it in xml:
     * <code>app:loadMoreTextSize</code>
     *
     * @param loadMoreTextSizeResId size of loading more text
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setLoadMoreTextSize(@DimenRes int loadMoreTextSizeResId) {
        setLoadMoreTextSize(getResources().getDimension(loadMoreTextSizeResId));
        return this;
    }

    /**
     * Set color of loading more text,default:{@link Color#GRAY}
     * <p>
     * You always can use it in xml:
     * <code>app:loadMoreTextColor</code>
     *
     * @param loadMoreTextColor color of loading more text
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setLoadMoreTextColor(@ColorInt int loadMoreTextColor) {
        this.loadMoreTextColor = loadMoreTextColor;
        return this;
    }

    /**
     * Set color of loading more text,default:{@link Color#GRAY}
     * <p>
     * You always can use it in xml:
     * <code>app:loadMoreTextColor</code>
     *
     * @param loadMoreTextColorResId color of loading more text
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setLoadMoreTextColorRes(@ColorRes int loadMoreTextColorResId) {
        setLoadMoreTextColor(getResources().getColor(loadMoreTextColorResId));
        return this;
    }

    /**
     * Set color of background which loading more view,default:{@link Color#TRANSPARENT}
     * <p>
     * You always can use it in xml:
     * <code>app:loadMoreBackground</code>
     *
     * @param loadMoreBackgroundColor color of background which loading more view
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setLoadMoreBackgroundColor(@ColorInt int loadMoreBackgroundColor) {
        this.loadMoreBackgroundColor = loadMoreBackgroundColor;
        return this;
    }

    /**
     * Set color of background which loading more view,default:{@link Color#TRANSPARENT}
     * <p>
     * You always can use it in xml:
     * <code>app:loadMoreBackground</code>
     *
     * @param loadMoreBackgroundColorResId color of background which loading more view
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setLoadMoreBackgroundColorRes(int loadMoreBackgroundColorResId) {
        setLoadMoreBackgroundColor(getResources().getColor(loadMoreBackgroundColorResId));
        return this;
    }

    /**
     * Set text which displayed when no more data,default:no more data
     * <p>
     * You always can use it in xml:
     * <code>app:noMoreDataText</code>
     *
     * @param noMoreDataText text which displayed when no more data
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setNoMoreDataText(String noMoreDataText) {
        this.noMoreDataText = noMoreDataText;
        return this;
    }

    /**
     * Set text which displayed when no more data from resource,default:no more data
     * <p>
     * You always can use it in xml:
     * <code>app:noMoreDataText</code>
     *
     * @param noMoreDataTextResId text which displayed when no more data
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setNoMoreDataText(@StringRes int noMoreDataTextResId) {
        setNoMoreDataText(getResources().getString(noMoreDataTextResId));
        return this;
    }

    /**
     * Set text which displayed when loading more data,default:loading.
     * <p>
     * You always can use it in xml:
     * <code>app:loadingMoreText</code>
     *
     * @param loadingMoreDataText text which displayed when loading more data
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setLoadingMoreDataText(String loadingMoreDataText) {
        this.loadingMoreDataText = loadingMoreDataText;
        return this;
    }

    /**
     * Set text which displayed when loading more data from resource,default:loading.
     * <p>
     * You always can use it in xml:
     * <code>app:loadingMoreText</code>
     *
     * @param loadingMoreDataTextResId text id which displayed when loading more data
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setLoadingMoreDataText(@StringRes int loadingMoreDataTextResId) {
        setLoadingMoreDataText(getResources().getString(loadingMoreDataTextResId));
        return this;
    }

    /**
     * Set text which displayed when load failed,default:Load failed(Click to load again).
     * <p>
     * You always can use it in xml:
     * <code>app:loadMoreFailedText</code>
     *
     * @param loadMoreDataFailedText the text which displayed when load failed.
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setLoadMoreDataFailedText(String loadMoreDataFailedText) {
        this.loadMoreDataFailedText = loadMoreDataFailedText;
        return this;
    }

    /**
     * Set text which displayed when load failed from resource,default:Load failed(Click to load again).
     * <p>
     * You always can use it in xml:
     * <code>app:loadMoreFailedText</code>
     *
     * @param loadMoreDataFailedTextResId the text which displayed when load failed.
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setLoadMoreDataFailedText(@StringRes int loadMoreDataFailedTextResId) {
        setLoadMoreDataFailedText(getResources().getString(loadMoreDataFailedTextResId));
        return this;
    }

    /**
     * Whether to hide {@link #noMoreDataText},default:false.
     * <p>
     * You always can use it in xml:
     * <code>app:hideNoMoreData</code>
     *
     * @param hideNoMoreData hide or not
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setHideNoMoreData(boolean hideNoMoreData) {
        this.isHideNoMoreData = hideNoMoreData;
        return this;
    }

    /**
     * {@link #maxScreenItems}
     */
    public NespRecyclerView setMaxScreenItems(int maxScreenItems) {
        this.maxScreenItems = maxScreenItems;
        return this;
    }

    /**
     * Set IndeterminateDrawable of  progressBar when loading more.
     * <p>
     * You always can use it in xml:
     * <code>app:loadMoreIndeterminateDrawable</code>
     *
     * @param loadMoreProgressIndeterminateDrawable IndeterminateDrawable of  progressBar
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setLoadMoreProgressIndeterminateDrawable(Drawable loadMoreProgressIndeterminateDrawable) {
        this.loadMoreProgressIndeterminateDrawable = loadMoreProgressIndeterminateDrawable;
        return this;
    }

    /**
     * Set IndeterminateDrawable of  progressBar when loading more from resource.
     * <p>
     * You always can use it in xml:
     * <code>app:loadMoreIndeterminateDrawable</code>
     *
     * @param loadMoreProgressIndeterminateDrawableResId IndeterminateDrawable of  progressBar
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setLoadMoreProgressIndeterminateDrawable(@DrawableRes int loadMoreProgressIndeterminateDrawableResId) {
        setLoadMoreProgressIndeterminateDrawable(getResources().getDrawable(loadMoreProgressIndeterminateDrawableResId));
        return this;
    }

    /**
     * Set the TintColor used in the Indeterminate Drawable of load-more progress.
     *
     * @param loadMoreProgressIndeterminateDrawableTintColor loadMoreProgressIndeterminateDrawableTintColor
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setLoadMoreProgressIndeterminateDrawableTintColor(@ColorInt int loadMoreProgressIndeterminateDrawableTintColor) {
        this.loadMoreProgressIndeterminateDrawableTintColor = loadMoreProgressIndeterminateDrawableTintColor;
        return this;
    }

    /**
     * Set the TintColor resource used in the Indeterminate Drawable of load-more progress from color resource.
     *
     * @param loadMoreProgressIndeterminateDrawableTintColorResId loadMoreProgressIndeterminateDrawableTintColorResId
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setLoadMoreProgressIndeterminateDrawableTintColorRes(@ColorRes int loadMoreProgressIndeterminateDrawableTintColorResId) {
        setLoadMoreProgressIndeterminateDrawableTintColor(getResources().getColor(loadMoreProgressIndeterminateDrawableTintColorResId));
        return this;
    }

    /**
     * Set content click item listener,not include empty view,header view and footer view
     *
     * @param onContentItemClickListener onContentItemClickListener
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setOnContentItemClickListener(OnContentItemClickListener onContentItemClickListener) {
        this.onContentItemClickListener = onContentItemClickListener;
        return this;
    }

    /**
     * get {@link #loadMoreView}
     *
     * @return {@link #loadMoreView}
     */
    public View getLoadMoreView() {
        return loadMoreView;
    }

    /**
     * Called when load more data failed
     *
     * @see #notifyLoadMoreFinish(boolean, boolean)
     */
    public void notifyLoadMoreFailed() {
        notifyLoadMoreFinish(false, true);
    }

    /**
     * Called when load more data successful
     *
     * @param hasMore has more data or not
     * @see #notifyLoadMoreFinish(boolean, boolean)
     */
    public void notifyLoadMoreSuccessful(boolean hasMore) {
        notifyLoadMoreFinish(true, hasMore);
    }

    /**
     * Notify mLoadMoreView do some UI change
     *
     * @param success Is load more data successful
     * @param hasMore Whether has more data to be loaded
     * @see #setOnLoadMoreListener(OnLoadMoreListener)
     */
    private void notifyLoadMoreFinish(final boolean success, final boolean hasMore) {
        this.removeOnScrollListener(onScrollListenerForLoadMore);

        loadMoreState = LoadMoreState.LOAD_FAILED;

        if (success) {
            loadMoreState = LoadMoreState.LOAD_SUCCESS;
            nespRecyclerViewAdapter.notifyDataSetChanged();
        }

        if (loadMoreView != null) {
            changeLoadMoreUi(success, hasMore);
        } else {
            nespRecyclerViewAdapter.setLoadMoreInflateListener(() -> {
                changeLoadMoreUi(success, hasMore);
                nespRecyclerViewAdapter.setLoadMoreInflateListener(null);
            });
        }
    }

    private void changeLoadMoreUi(boolean success, boolean hasMore) {

        if (success) {
            if (hasMore) {
                showView(pbLoadMore);
                tvLoadMoreText.setText(loadingMoreDataText);
                this.addOnScrollListener(onScrollListenerForLoadMore);
            } else {
                if (isHideNoMoreData) {
                    //the mLoadMoreView will GONE when no more data to be loaded
                    loadMoreEnable = false;
                    hideView(pbLoadMore);
                    hideView(tvLoadMoreText);
                } else {
                    //the mLoadMoreView will display "No More Data" when no more data to be loaded
                    //and the progressBar will GONE
                    loadMoreView.setOnClickListener(null);
                    hideView(pbLoadMore);
                    tvLoadMoreText.setText(noMoreDataText);
                }
            }
        } else {
            //the loadMoreView will display {#tvTextLoadFailed} when load more data failed
            //and the progressBar will GONE
            tvLoadMoreText.setText(loadMoreDataFailedText);
            hideView(pbLoadMore);
        }
        lastPbLoadMoreVisibilityTemp = pbLoadMore.getVisibility();
        lastPbTvLoadMoreTextTemp = tvLoadMoreText.getText().toString();
    }

    /**
     * Load more state
     *
     * @see LoadMoreState#LOAD_NOT is initialization state.
     * @see LoadMoreState#LOADING is currently loading.
     * @see LoadMoreState#LOAD_SUCCESS is currently complete and load success.
     * @see LoadMoreState#LOAD_NO_MORE_DATA is currently complete and don't has more data which need to loaded.
     * @see LoadMoreState#LOAD_FAILED is currently complete and load failed.
     */
    public enum LoadMoreState {
        LOAD_NOT,
        LOADING,//It is loading
        LOAD_SUCCESS,//Load Success
        LOAD_NO_MORE_DATA,//No more data
        LOAD_FAILED//Load Failed
    }

    //===============================Refresh API============================================

    /**
     * Set background color of refresh view.
     *
     * @param refreshBackgroundColor refreshBackgroundColor
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setRefreshBackgroundColor(@ColorInt int refreshBackgroundColor) {
        this.refreshBackgroundColor = refreshBackgroundColor;
        return this;
    }

    /**
     * Set background color of refresh view from resource.
     *
     * @param refreshBackgroundColorResId refreshBackgroundColorResId
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setRefreshBackgroundColorRes(@ColorRes int refreshBackgroundColorResId) {
        setRefreshBackgroundColor(getResources().getColor(refreshBackgroundColorResId));
        return this;
    }

    /**
     * Set text which displayed when within a certain range of pull-down.
     * <p>
     * You always can use it in xml:
     * <code>app:pullDownText</code>
     *
     * @param pullDownText {@link #pullDownText}
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setPullDownText(String pullDownText) {
        this.pullDownText = pullDownText;
        return this;
    }

    /**
     * Set text which displayed when within a certain range of pull-down from resource.
     * <p>
     * You always can use it in xml:
     * <code>app:pullDownText</code>
     *
     * @param pullDownTextResId {@link #pullDownText}
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setPullDownText(@StringRes int pullDownTextResId) {
        setPullDownText(getResources().getString(pullDownTextResId));
        return this;
    }

    /**
     * Set text which displayed when outside a certain range of pull-down.
     * <p>
     * You always can use it in xml:
     * <code>app:upToRefreshText</code>
     *
     * @param upToRefreshText {@link #upToRefreshText}
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setUpToRefreshText(String upToRefreshText) {
        this.upToRefreshText = upToRefreshText;
        return this;
    }

    /**
     * Set text which displayed when outside a certain range of pull-down from resource.
     * <p>
     * You always can use it in xml:
     * <code>app:upToRefreshText</code>
     *
     * @param upToRefreshTextResId {@link #upToRefreshText}
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setUpToRefreshText(@StringRes int upToRefreshTextResId) {
        setUpToRefreshText(getResources().getString(upToRefreshTextResId));
        return this;
    }

    /**
     * Set text which displayed when refreshing.
     * <p>
     * You always can use it in xml:
     * <code>app:refreshingText</code>
     *
     * @param refreshingText {@link #refreshingText}
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setRefreshingText(String refreshingText) {
        this.refreshingText = refreshingText;
        return this;
    }

    /**
     * Set text which displayed when refreshing from resource.
     * <p>
     * You always can use it in xml:
     * <code>app:refreshingText</code>
     *
     * @param refreshingTextResId {@link #refreshingText}
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setRefreshingText(@StringRes int refreshingTextResId) {
        setRefreshingText(getResources().getString(refreshingTextResId));
        return this;
    }

    /**
     * Set text which displayed when refresh success.
     * <p>
     * You always can use it in xml:
     * <code>app:refreshSuccessText</code>
     *
     * @param refreshSuccessText {@link #refreshSuccessText}
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setRefreshSuccessText(String refreshSuccessText) {
        this.refreshSuccessText = refreshSuccessText;
        return this;
    }

    /**
     * Set text which displayed when refresh success from resource.
     * <p>
     * You always can use it in xml:
     * <code>app:refreshSuccessText</code>
     *
     * @param refreshSuccessTextResId {@link #refreshSuccessText}
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setRefreshSuccessText(@StringRes int refreshSuccessTextResId) {
        setRefreshSuccessText(getResources().getString(refreshSuccessTextResId));
        return this;
    }

    /**
     * Set text which displayed when refresh failed.
     * <p>
     * You always can use it in xml:
     * <code>app:refreshFailedText</code>
     *
     * @param refreshFailedText {@link #refreshFailedText}
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setRefreshFailedText(String refreshFailedText) {
        this.refreshFailedText = refreshFailedText;
        return this;
    }

    /**
     * Set text which displayed when refresh failed from resource.
     * <p>
     * You always can use it in xml:
     * <code>app:refreshFailedText</code>
     *
     * @param refreshFailedTextResId {@link #refreshFailedText}
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setRefreshFailedText(@StringRes int refreshFailedTextResId) {
        setRefreshFailedText(getResources().getString(refreshFailedTextResId));
        return this;
    }

    /**
     * Is {@link NespRecyclerView} currently refreshing?
     *
     * @return refreshing or not.
     */
    public Boolean isRefreshing() {
        return this.isRefreshing;
    }

    /**
     * Called when refresh finish
     *
     * @param isRefreshSuccess true->Success,false->failed
     */
    public void notifyRefreshFinish(Boolean isRefreshSuccess) {

        hideView(progressBarRefresh);
        if (isRefreshSuccess) {
            textViewRefresh.setText(refreshSuccessText);
        } else {
            textViewRefresh.setText(refreshFailedText);
        }
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setIntValues(linearLayoutRefreshingHeight, linearLayoutInitHeight);
        valueAnimator.setStartDelay(800);
        valueAnimator.setDuration(400);
        valueAnimator.addUpdateListener(animation -> {
            linearLayoutRefreshRootLayoutParams.height = (int) animation.getAnimatedValue();
            linearLayoutRefreshRoot.setLayoutParams(linearLayoutRefreshRootLayoutParams);
        });
        valueAnimator.start();
        if (isRefreshSuccess) nespRecyclerViewAdapter.notifyDataSetChanged();
        isRefreshing = false;
    }

    /**
     * Whether to enable Refresh.
     *
     * @param refreshEnable enable or not
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setRefreshEnable(Boolean refreshEnable) {
        isRefreshEnable = refreshEnable;
        return this;
    }

    /**
     * Set listener called when refreshing.
     *
     * @param onRefreshListener onRefreshListener
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setOnRefreshListener(OnRefreshListener onRefreshListener) {
        if (onRefreshListener == null) return this;
        this.onRefreshListener = onRefreshListener;
        return this;
    }

    /**
     * Set text size of {@link #textViewRefresh}
     *
     * @param refreshTextSize refreshTextSize
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setRefreshTextSize(float refreshTextSize) {
        this.refreshTextSize = refreshTextSize;
        return this;
    }

    /**
     * Set text size of {@link #textViewRefresh} from resource
     *
     * @param refreshTextSizeResId refreshTextSize
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setRefreshTextSize(@DimenRes int refreshTextSizeResId) {
        setRefreshTextSize(getResources().getDimension(refreshTextSizeResId));
        return this;
    }

    /**
     * Set text color of {@link #textViewRefresh}
     *
     * @param refreshTextColor refreshTextColor
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setRefreshTextColor(@ColorInt int refreshTextColor) {
        this.refreshTextColor = refreshTextColor;
        return this;
    }

    /**
     * Set text color of {@link #textViewRefresh} from resource
     *
     * @param refreshTextColorResId refreshTextColor
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setRefreshTextColorRes(@ColorRes int refreshTextColorResId) {
        setRefreshTextColor(getResources().getColor(refreshTextColorResId));
        return this;
    }

    /**
     * Set tint color of arrow.
     *
     * @param refreshArrowTintColor refreshArrowTintColor
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setRefreshArrowTintColor(@ColorInt int refreshArrowTintColor) {
        this.refreshArrowTintColor = refreshArrowTintColor;
        return this;
    }

    /**
     * Set tint color of arrow from resource
     *
     * @param refreshArrowTintColorResId refreshArrowTintColor
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setRefreshArrowTintColorRes(@ColorRes int refreshArrowTintColorResId) {
        setRefreshArrowTintColor(getResources().getColor(refreshArrowTintColorResId));
        return this;
    }

    /**
     * Set drawable of down arrow.
     *
     * @param refreshArrowDrawable refreshArrowDrawable
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setRefreshArrowDrawable(Drawable refreshArrowDrawable) {
        this.refreshArrowDrawable = refreshArrowDrawable;
        return this;
    }

    /**
     * Set drawable of down arrow from resource.
     *
     * @param refreshArrowDrawableResId refreshArrowDrawable
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setRefreshArrowDrawable(@DrawableRes int refreshArrowDrawableResId) {
        setRefreshArrowDrawable(getResources().getDrawable(refreshArrowDrawableResId));
        return this;
    }

    /**
     * Set the TintColor used in the Indeterminate Drawable of refresh progress.
     *
     * @param color color
     */
    public NespRecyclerView setRefreshProgressIndeterminateDrawableTintColor(int color) {
        Log.e(TAG, "NespRecyclerView.setRefreshProgressIndeterminateDrawableTintColor: color" + color);
        this.refreshProgressIndeterminateDrawableTintColor = color;
        return this;
    }

    /**
     * Set the TintColor resource used in the Indeterminate Drawable of refresh progress from color resource.
     *
     * @param colorResId colorResId
     */
    public NespRecyclerView setRefreshProgressIndeterminateDrawableTintColorRes(@ColorRes int colorResId) {
        setRefreshProgressIndeterminateDrawableTintColor(getResources().getColor(colorResId));
        return this;
    }

    /**
     * Set IndeterminateDrawable of  progressBar when refreshing.
     *
     * @param refreshProgressIndeterminateDrawable refreshProgressIndeterminateDrawable
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setRefreshProgressIndeterminateDrawable(Drawable refreshProgressIndeterminateDrawable) {
        this.refreshProgressIndeterminateDrawable = refreshProgressIndeterminateDrawable;
        return this;
    }

    /**
     * Set IndeterminateDrawable of  progressBar when refreshing.
     *
     * @param refreshProgressIndeterminateDrawableResId refreshProgressIndeterminateDrawableResId
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setRefreshProgressIndeterminateDrawable(@DrawableRes int refreshProgressIndeterminateDrawableResId) {
        setRefreshProgressIndeterminateDrawable(getResources().getDrawable(refreshProgressIndeterminateDrawableResId));
        return this;
    }

    /**
     * Set max finger swipe offset of stopping change layout height.
     *
     * @param refreshMaxOffset refreshMaxOffset
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setRefreshMaxOffset(float refreshMaxOffset) {
        this.refreshMaxOffset = refreshMaxOffset;
        return this;
    }

    /**
     * Set min finger swipe offset of starting change layout height.
     *
     * @param refreshMinOffset refreshMinOffset
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setRefreshMinOffset(float refreshMinOffset) {
        this.refreshMinOffset = refreshMinOffset;
        return this;
    }

    /**
     * Set min finger swipe offset of starting rotate arrow.
     * <p>
     * <b>Deprecated:</b>
     * It is recommended to use the default value
     *
     * @param refreshRotateOffset refreshRotateOffset
     * @return {@link NespRecyclerView}
     */
    @Deprecated
    public NespRecyclerView setRefreshRotateOffset(float refreshRotateOffset) {
        this.refreshRotateOffset = refreshRotateOffset;
        return this;
    }

    /**
     * Reset field prevents useless cached data after a one-cycle pull-down-to-refresh event
     */
    private void resetRefreshField() {
        pointYDown = 0;
        pointYMove = 0;
        fingerSideOffset = 0;
        viewSlideOffset = 0;
    }

    private int getLinearLayoutRefreshingHeight() {
        imageViewRefreshArrow.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        textViewRefresh.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        //Calculate linearLayoutRefreshingHeight
        return linearLayoutRefreshingHeight = Math.max(imageViewRefreshArrow.getHeight(), textViewRefresh.getHeight()) + 50;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        isScrollTop = !canScrollVertically(-1);
//        Log.e(TAG, "NespRecyclerView.onTouchEvent: \n" + isScrollTop);
        if (!isRefreshEnable
                || !isScrollTop
                || getScrollState() != SCROLL_STATE_DRAGGING
                || loadMoreState == LoadMoreState.LOADING) return super.onTouchEvent(event);

        if (isRefreshing) return true;

        int eventAction = event.getAction();

        if (eventAction == MotionEvent.ACTION_DOWN) {
            hideView(progressBarRefresh);
            pointYDown = event.getY();
            isCalledActionDown = true;
        }

        if (eventAction == MotionEvent.ACTION_MOVE) {

            isCalledActionMove = true;

            pointYMove = event.getY();

            if (!isCalledActionDown) {
                hideView(progressBarRefresh);
                pointYDown = pointYMove;
                isCalledActionDown = true;
            }

            //Customize Refresh View
            imageViewRefreshArrow.setImageDrawable(refreshArrowDrawable);
            imageViewRefreshArrow.getDrawable().setTint(refreshArrowTintColor);

            fingerSideOffset = pointYMove - pointYDown;
            viewSlideOffset = fingerSideOffset / 2;

            //If over minRefreshOffset it will start to move downward
            if (fingerSideOffset > refreshMinOffset) {
                textViewRefresh.setText(pullDownText);

                //If over maxRefreshOffset it will stop to move downward
                if (refreshRotateOffset == -1)
                    //Set value from xml resource
                    refreshRotateOffset = NespRecyclerView.this.getLinearLayoutRefreshingHeight() + 30;

                if (viewSlideOffset > refreshRotateOffset) {

                    textViewRefresh.setText(upToRefreshText);
                    if (!isRefreshRowRotatedUp) {
                        rotateView(imageViewRefreshArrow, true);
                        isRefreshRowRotatedUp = true;
                    }
                } else {
                    textViewRefresh.setText(pullDownText);
                    if (isRefreshRowRotatedUp) {
                        rotateView(imageViewRefreshArrow, false);
                        isRefreshRowRotatedUp = false;
                    }
                }

                if (viewSlideOffset <= refreshMaxOffset) {
                    linearLayoutRefreshRootLayoutParams.height = (int) viewSlideOffset;
                    linearLayoutRefreshRoot.setLayoutParams(linearLayoutRefreshRootLayoutParams);
                }

                linearLayoutRefreshRoot.setBackgroundColor(refreshBackgroundColor);
            } else if (fingerSideOffset < 0) {
                isCalledActionDown = false;
                return super.onTouchEvent(event);
            } else {
                return super.onTouchEvent(event);
            }
        }

        if (eventAction == MotionEvent.ACTION_UP) {
//            Log.e(TAG, "NespRecyclerView.onTouchEvent: isCalledActionMove " + isCalledActionMove);
            if (!isCalledActionMove) return super.onTouchEvent(event);

            isCalledActionMove = false;
            isCalledActionDown = false;
            isScrollTop = false;

            fingerSideOffset = pointYMove - pointYDown;

            ValueAnimator valueAnimator = new ValueAnimator();

            if (isRefreshRowRotatedUp) {
                isRefreshRowRotatedUp = false;
                isRefreshing = true;
                showView(progressBarRefresh);
                textViewRefresh.setText(refreshingText);
                imageViewRefreshArrow.setImageDrawable(null);

                rotateView(imageViewRefreshArrow, false);

                valueAnimator.setIntValues((int) viewSlideOffset, getLinearLayoutRefreshingHeight());
                valueAnimator.setInterpolator(new AnticipateOvershootInterpolator());
                if (onRefreshListener != null) onRefreshListener.onRefresh();
            } else {
                valueAnimator.setIntValues((int) viewSlideOffset, linearLayoutInitHeight);
            }

            if (fingerSideOffset > 0) {
                valueAnimator.setDuration((long) (viewSlideOffset));
                valueAnimator.addUpdateListener(animation -> {
                    linearLayoutRefreshRootLayoutParams.height = (Integer) animation.getAnimatedValue();
                    linearLayoutRefreshRoot.setLayoutParams(linearLayoutRefreshRootLayoutParams);
                });
                valueAnimator.start();
            }
            resetRefreshField();
            return super.onTouchEvent(event);
        }
        return true;
    }

    //================================Other=======================================

    /**
     * {@link #isScrollTop() , #isScrollBottom(), #isScrollUp(), #isScrollDown()}.
     * <p>
     * Note:This variable uses a different judgment with {@link #onScrollListenerForLoadMore} to reach the bottom
     */
    @Override
    public void onScrolled(int dx, int dy) {
        isScrollTop = !canScrollVertically(-1);
        isScrollBottom = !canScrollVertically(1);
        isScrollUp = dy > 0;
        isScrollDown = dy < 0;
    }

//================================Adapter=======================================

    public class NespRecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder> {

        private final Adapter originAdapter;

        private LoadMoreInflateListener mLoadMoreInflateListener;

        //================================Item Type=======================================
        public static final int ITEM_TYPE_REFRESH_HEADER = 0x000041;
        public static final int ITEM_TYPE_NORMAL = 0x000042;
        public static final int ITEM_TYPE_EMPTY = 0x000043;
        public static final int ITEM_TYPE_HEADER = 0x000044;
        public static final int ITEM_TYPE_FOOTER = 0x000045;
        public static final int ITEM_TYPE_LOAD_MORE = 0x000046;
        //=======================================================================

        //Wrapper Adapter here.
        NespRecyclerViewAdapter(Adapter originAdapter) {
            this.originAdapter = originAdapter;
        }

        void setLoadMoreInflateListener(LoadMoreInflateListener mLoadMoreInflateListener) {
            this.mLoadMoreInflateListener = mLoadMoreInflateListener;
        }

        Adapter getOriginAdapter() {
            return originAdapter;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            switch (viewType) {

                case ITEM_TYPE_REFRESH_HEADER:
                    refreshHeaderView = LayoutInflater.from(context).inflate(R.layout.nesprecyclerview_header_refresh_content, parent, false);
                    linearLayoutRefreshRoot = refreshHeaderView.findViewById(R.id.nrv_header_refresh_root);
                    textViewRefresh = refreshHeaderView.findViewById(R.id.nrv_header_refresh_tv);
                    imageViewRefreshArrow = refreshHeaderView.findViewById(R.id.nrv_header_refresh_iv);
                    progressBarRefresh = refreshHeaderView.findViewById(R.id.nrv_header_refresh_pb);
                    linearLayoutRefreshRootLayoutParams = (LinearLayout.LayoutParams) linearLayoutRefreshRoot.getLayoutParams();

                    /*
                    Customize for refresh
                     */
                    textViewRefresh.setTextColor(refreshTextColor);
                    textViewRefresh.setTextSize(refreshTextSize);

                    if (refreshProgressIndeterminateDrawable != null) {
                        progressBarRefresh.setIndeterminateDrawable(refreshProgressIndeterminateDrawable);
                    }

                    if (refreshProgressIndeterminateDrawableTintColor != 0) {
                        progressBarRefresh.getIndeterminateDrawable().setTint(refreshProgressIndeterminateDrawableTintColor);
                    }

                    return new NespRecyclerViewViewHolder(refreshHeaderView);
                case ITEM_TYPE_HEADER:
                    return new NespRecyclerViewViewHolder(headerView);
                case ITEM_TYPE_EMPTY: {
                    if (emptyDrawable != null) {
                        try {
                            ((ImageView) emptyView.findViewById(R.id.nrcv_empty_iv))
                                    .setImageDrawable(emptyDrawable);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (emptyText != null && !emptyText.isEmpty()) {
                        try {
                            ((TextView) emptyView.findViewById(R.id.nrcv_empty_tv))
                                    .setText(emptyText);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    return new NespRecyclerViewViewHolder(emptyView);
                }
                case ITEM_TYPE_FOOTER:
                    return new NespRecyclerViewViewHolder(footerView);
                case ITEM_TYPE_LOAD_MORE:
                    loadMoreView = LayoutInflater.from(getContext())
                            .inflate(R.layout.nesprecyclerview_load_more, parent, false);

                    /*
                     *  Customize the loadMoreView
                     */
                    loadMoreView.setBackgroundColor(loadMoreBackgroundColor);
                    pbLoadMore = loadMoreView.findViewById(R.id.nrv_load_more_pb);
                    if (loadMoreProgressIndeterminateDrawable != null) {
                        pbLoadMore.setIndeterminateDrawable(loadMoreProgressIndeterminateDrawable);
                    }

                    if (loadMoreProgressIndeterminateDrawableTintColor != 0) {
                        pbLoadMore.getIndeterminateDrawable().setTint(loadMoreProgressIndeterminateDrawableTintColor);
                    }
                    tvLoadMoreText = loadMoreView.findViewById(R.id.nrv_load_more_tv);
                    tvLoadMoreText.setTextSize(loadMoreTextSize);
                    tvLoadMoreText.setTextColor(loadMoreTextColor);
                    tvLoadMoreText.setText(loadingMoreDataText);

                    //Click to reload data if load more failed
                    loadMoreView.setOnClickListener(v -> {
                        if (loadMoreState != LoadMoreState.LOAD_FAILED) return;
                        loadMoreState = LoadMoreState.LOADING;
                        tvLoadMoreText.setText(loadingMoreDataText);
                        showView(tvLoadMoreText);
                        showView(pbLoadMore);
                        if (onLoadMoreListener != null) onLoadMoreListener.onLoadMore();
                    });

                    //when remove footerView will trigger onCreateViewHolder()
                    if (!TextUtils.isEmpty(lastPbTvLoadMoreTextTemp)) {
                        tvLoadMoreText.setText(lastPbTvLoadMoreTextTemp);
                        pbLoadMore.setVisibility(lastPbLoadMoreVisibilityTemp);
                    }

                    if (mLoadMoreInflateListener != null) {
                        mLoadMoreInflateListener.onLoadMoreInflated();
                    }
                    return new NespRecyclerViewViewHolder(loadMoreView);
                default:
                    return originAdapter.onCreateViewHolder(parent, viewType);
            }

        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            int realPosition = getRealItemPosition(position);
            int type = getItemViewType(position);

            if (type == ITEM_TYPE_REFRESH_HEADER
                    || type == ITEM_TYPE_HEADER
                    || type == ITEM_TYPE_FOOTER
                    || type == ITEM_TYPE_EMPTY
                    || type == ITEM_TYPE_LOAD_MORE) {
                return;
            }

            holder.itemView.setOnClickListener(v -> {
                if (onContentItemClickListener != null)
                    onContentItemClickListener.onContentItemClick(holder, realPosition);
            });

            originAdapter.onBindViewHolder(holder, realPosition);
        }

        @Override
        public int getItemCount() {
            //Get the number of real data


            int itemCount = originAdapter.getItemCount();

            int originItemCount = itemCount;
            /*
             * Add the new View in RecyclerView.
             */

            //Add RefreshHeaderView
            if (isRefreshEnable)
                itemCount++;
            //Add HeaderView
            if (headerView != null) itemCount++;
            //Add FooterView
            if (footerView != null) itemCount++;
            //Add EmptyView
            if (emptyView != null && originItemCount == 0) {
                itemCount++;
                //If the content data is empty,no need to add LoadMoreView
                return itemCount;
            }
            //Add LoadMoreView
            if (loadMoreState != LoadMoreState.LOAD_NOT
                    && loadMoreEnable
                    && originItemCount >= maxScreenItems
            ) itemCount++;
            return itemCount;
        }

        @Override
        public int getItemViewType(int position) {

            /*
             * The order of the view is very important to be cautiously modified, otherwise it will be confused and return wrong type.
             */
            if (isRefreshEnable && position == 0) return ITEM_TYPE_REFRESH_HEADER;
            if (headerView != null && position == (refreshHeaderView == null ? 0 : 1))
                return ITEM_TYPE_HEADER;
            if (footerView != null && position == getItemCount() - 1) return ITEM_TYPE_FOOTER;
            if (emptyView != null && originAdapter.getItemCount() == 0) {
                //If the content data is empty,no need to add LoadMoreView
                return ITEM_TYPE_EMPTY;
            } else if (loadMoreState != LoadMoreState.LOAD_NOT
                    && position == getLoadMorePosition()
                    && loadMoreEnable
                    && originAdapter.getItemCount() >= maxScreenItems
            ) {
                return ITEM_TYPE_LOAD_MORE;
            }
            return ITEM_TYPE_NORMAL;
        }

        /**
         * Get real item position which loaded by user.
         *
         * @param position All item position, include new view which created by {@link NespRecyclerView}
         * @return Real item position
         */
        private int getRealItemPosition(int position) {

            if (refreshHeaderView != null) {
                position--;
            }

            if (headerView != null) {
                position--;
            }

            return position;
        }

        /**
         * Adapt {@link StaggeredGridLayoutManager} for {@link NespRecyclerView} by set span size
         *
         * @param holder holder
         * @see #adaptLayoutManager(LayoutManager)
         */
        @Override
        public void onViewAttachedToWindow(@NonNull ViewHolder holder) {

            int position = holder.getLayoutPosition();

            int type = getItemViewType(position);

            if (type == ITEM_TYPE_REFRESH_HEADER
                    || type == ITEM_TYPE_HEADER
                    || type == ITEM_TYPE_FOOTER
                    || type == ITEM_TYPE_EMPTY
                    || type == ITEM_TYPE_LOAD_MORE) {
                ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
                if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
                    StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) layoutParams;
                    p.setFullSpan(true);//占满一行
                }
            }
        }

        /**
         * Adapt {@link GridLayoutManager} for {@link NespRecyclerView} by set span size
         *
         * @param recyclerView recyclerView
         * @see #adaptLayoutManager(LayoutManager)
         */
        @Override
        public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {

            final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

            adaptLayoutManager(layoutManager);
        }

        /**
         * adapt  {@link GridLayoutManager} for {@link NespRecyclerView} by set span size
         *
         * @param layoutManager layoutManager
         * @see #setLayoutManager(LayoutManager)
         */
        void adaptLayoutManager(LayoutManager layoutManager) {
            if (!(layoutManager instanceof GridLayoutManager)) return;

            ((GridLayoutManager) layoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int type = getItemViewType(position);

                    if (type == ITEM_TYPE_REFRESH_HEADER
                            || type == ITEM_TYPE_HEADER
                            || type == ITEM_TYPE_FOOTER
                            || type == ITEM_TYPE_EMPTY
                            || type == ITEM_TYPE_LOAD_MORE) {
                        return ((GridLayoutManager) layoutManager).getSpanCount();
                    }


//                        Log.e(TAG, "NespRecyclerViewAdapter.getSpanSize: ((GridLayoutManager) layoutManager).getSpanCount()" + ((GridLayoutManager) layoutManager).getSpanCount());
//
//                        if (refreshHeaderView != null && position == 0) {
//                            Log.e(TAG, "NespRecyclerViewAdapter.getSpanSize: 1 " + 1);
//                            return ((GridLayoutManager) layoutManager).getSpanCount();
//                        }
//
//                        if (headerView != null && position == (refreshHeaderView == null ? 0 : 1)) {
//                            Log.e(TAG, "NespRecyclerViewAdapter.getSpanSize: 2 " + 2);
//                            return ((GridLayoutManager) layoutManager).getSpanCount();
//                        }
//
//                        if (footerView != null && position == getLoadMorePosition() + 1) {
//                            Log.e(TAG, "NespRecyclerViewAdapter.getSpanSize: 3 " + 3);
//                            return ((GridLayoutManager) layoutManager).getSpanCount();
//                        }
//
//                        if (position == getLoadMorePosition()) {
//                            Log.e(TAG, "NespRecyclerViewAdapter.getSpanSize: 4 " + 4);
//                            return ((GridLayoutManager) layoutManager).getSpanCount();
//                        }
//
//                        if (emptyView != null && getOriginAdapter().getItemCount() == 0) {
//                            Log.e(TAG, "NespRecyclerViewAdapter.getSpanSize: 5 " + 5);
//                            return ((GridLayoutManager) layoutManager).getSpanCount();
//                        }

                    return 1;
                }
            });

        }

        /**
         * Get the loadMore position
         * <p>
         * if footerView is null , loadMore position will display at the end,
         * or it will display at the last but one
         *
         * @return the loadMore position
         */
        private int getLoadMorePosition() {
            if (footerView == null) {
                return getItemCount() - 1;
            } else {
                return getItemCount() - 2;
            }
        }

        class NespRecyclerViewViewHolder extends ViewHolder {
            NespRecyclerViewViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }

    }

//================================Listener=======================================

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public interface LoadMoreInflateListener {
        void onLoadMoreInflated();
    }

    public interface OnContentItemClickListener {
        void onContentItemClick(ViewHolder holder, int contentPosition);
    }

    public interface OnRefreshListener {
        void onRefresh();

    }

    //================================Utils=======================================

    /**
     * Inflate a new view hierarchy from the specified xml resource.
     * Note: the param <code>NespRecyclerView.this</code> is very important when you calling {@link android.view.LayoutInflater#inflate(int, ViewGroup, boolean)}
     * <p>
     * {@link android.view.LayoutInflater#inflate(int, ViewGroup)}
     *
     * @param layoutRes ID for an XML layout resource to load (e.g.,
     *                  <code>R.layout.main_page</code>)
     * @return The root View of the inflated hierarchy. If root was supplied,
     * this is the root View; otherwise it is the root of the inflated
     * XML file.
     */
    public View inflateView(int layoutRes) {
        if (layoutRes == -1) return null;
        return LayoutInflater.from(context).inflate(layoutRes, NespRecyclerView.this, false);
    }

    private void showView(View view) {
        if (view != null) view.setVisibility(VISIBLE);
    }

    private void hideView(View view) {
        if (view != null) view.setVisibility(GONE);
    }

    private void rotateView(View view, Boolean toUp) {

        RotateAnimation rotateAnimation = toUp ? new RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
                : new RotateAnimation(180f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setDuration(500);
        view.startAnimation(rotateAnimation);
    }
}
