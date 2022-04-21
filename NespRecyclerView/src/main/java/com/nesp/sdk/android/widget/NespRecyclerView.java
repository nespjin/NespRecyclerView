/*
 * Copyright (C) 2021 The NESP Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nesp.sdk.android.widget;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
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
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * @author <a href="mailto:1756404649@qq.com">Jinzhaolu Email:1756404649@qq.com</a>
 * @team NESP Technology
 * @time: Created 19-1-3 上午9:08
 * @project NespRecyclerView
 **/
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class NespRecyclerView extends RecyclerView {

    //================================Normal Filed=======================================

    private static final String TAG = "NespRecyclerView";

    /**
     * Internal RecyclerAdapter.
     * <p>
     * Wrapper external RecyclerAdapter.
     */
    private NespRecyclerViewAdapter mNespAdapter;

    /**
     * Indicate whether the RecyclerView is scroll top.
     * <p>
     * {@link #isScrollTop()}
     */
    private boolean mIsScrollTop = false;

    /**
     * Indicate whether the RecyclerView is scroll bottom.
     * <p>
     * {@link #isScrollBottom()} ()}
     */
    private boolean mIsScrollBottom = false;

    /**
     * Indicate whether the RecyclerView is scrolling up.
     * <p>
     * {@link #isScrollUp()} ()}
     */
    private boolean mIsScrollUp = false;

    /**
     * Indicate whether the RecyclerView is scrolling down.
     * <p>
     * {@link #isScrollDown()}
     */
    private boolean mIsScrollDown = false;

    //================================Empty View,Header View,Footer View Filed=======================================

    /**
     * A fixed view which displayed when no data.
     */
    private View mEmptyView;

    /**
     * Drawable for default emptyView
     */
    private Drawable mEmptyDrawable;

    /**
     * EmptyText for default emptyView
     */
    private String mEmptyText = "";

    /**
     * A fixed view to appear at the top of the list.
     */
    private View mHeaderView;
    /**
     * A fixed view to appear at the bottom of the list.
     */
    private View mFooterView;

    //================================Load More Filed=======================================

    private View mLoadMoreView;
    /**
     * Loading state
     */
    private LoadMoreState mLoadMoreState;
    /**
     * Loading text
     */
    private TextView mTvLoadMoreText;
    /**
     * Loading progressBar
     */
    private ProgressBar mPbLoadMore;
    /**
     * Loading text size
     */
    private float mLoadMoreTextSize;
    /**
     * Loading text color
     */
    private int mLoadMoreTextColor;
    /**
     * Loading view background color
     */
    private int mLoadMoreBackgroundColor;
    /**
     * No more data text
     */
    private String mNoMoreDataText;
    /**
     * Load more data text
     */
    private String mLoadingMoreDataText;
    /**
     * Load more data failed text
     */
    private String mLoadMoreDataFailedText;
    /**
     * Loading more enable
     */
    private Boolean mLoadMoreEnable = false;


    /**
     * The maximum number of Items in screen,if the current number of items Less than the maximum number，
     * <p>
     * will not display Load-more-view
     */
    private int mMaxScreenItems = -1;

    private OnLoadMoreListener mOnLoadMoreListener;
    private OnContentItemClickListener mOnContentItemClickListener;

    private String mLastPbTvLoadMoreTextTemp;
    private int mLastPbLoadMoreVisibilityTemp;

    /**
     * Do not display {@link #mNoMoreDataText} when no more data.
     * <p>
     * Default:false.
     */
    private boolean mIsHideNoMoreData;
    /**
     * Drawable of loading more progressBar
     */
    private Drawable mLoadMoreProgressIndeterminateDrawable;
    private int mLoadMoreProgressIndeterminateDrawableTintColor;

    /**
     * OnScrollListener for load more.
     */
    private final OnScrollListener mOnScrollListenerForLoadMore = new OnScrollListener() {
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

            if (mLoadMoreEnable && mLoadMoreState != LoadMoreState.LOADING && dy > 0) {
                if (findLastVisibleItemPosition() == mNespAdapter.getItemCount() - 1) {
                    mLoadMoreState = LoadMoreState.LOADING;
                    if (mOnLoadMoreListener != null) mOnLoadMoreListener.onLoadMore();
                }
            }
        }
    };

    ///////////////////////////////////////////////////////////////////////////
    // Refresh Field
    ///////////////////////////////////////////////////////////////////////////

    private View mRefreshHeaderView;
    private LinearLayout mLinearLayoutRefreshRoot;
    private ImageView mImageViewRefreshArrow;
    private TextView mTextViewRefresh;
    private ProgressBar mProgressBarRefresh;

    private OnRefreshListener mOnRefreshListener;

    private Boolean mIsRefreshing = false;
    private Boolean mIsRefreshEnable = true;

    /**
     * @see MotionEvent#ACTION_DOWN
     */
    private float mPointYDown;
    /**
     * @see MotionEvent#ACTION_MOVE
     */
    private float mPointYMove;
    private float mFingerSideOffset;
    private float mViewSlideOffset;

    /**
     * Within a certain range of pull-down,it will be display
     */
    private String mPullDownText;
    /**
     * Outside a certain range of pull-down,it will be display
     */
    private String mUpToRefreshText;
    private String mRefreshingText;
    private String mRefreshSuccessText;
    private String mRefreshFailedText;
    private int mRefreshBackgroundColor;
    private float mRefreshTextSize;
    private int mRefreshTextColor;
    private int mRefreshArrowTintColor;
    private Drawable mRefreshArrowDrawable;
    private Drawable mRefreshProgressIndeterminateDrawable;
    private int mRefreshProgressIndeterminateDrawableTintColor;
    private float mRefreshMaxOffset;
    private float mRefreshMinOffset;
    private float mRefreshRotateOffset;


    private LinearLayout.LayoutParams mLlRefreshRootLayoutParams;
    private int mLinearLayoutRefreshingHeight = 122;
    private final int mLlInitHeight = 1;

    /**
     * When {@link #mIsScrollTop} changes from false to true,
     * it does not call {@link MotionEvent#ACTION_DOWN} but calls {@link MotionEvent#ACTION_MOVE} directly,
     * so it won't initialize {@link #mPointYDown} in {@link MotionEvent#ACTION_DOWN}.
     * <p>
     * To fix this bug, we set a flag isCalledActionDown
     * to determine if it calls {@link MotionEvent#ACTION_DOWN},
     * if not, we will use the first value of {@link #mPointYMove}
     * in the {@link MotionEvent#ACTION_DOWN} event to initialize { @link #pointYDown}.
     */
    private Boolean mIsCalledActionDown = false;

    private Boolean mIsCalledActionMove = false;

    private Boolean mIsRefreshRowRotatedUp = false;

    /*********************************Field End*************************************/
    public NespRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public NespRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NespRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NespRecyclerView);
        initAttrs(context, typedArray);
        typedArray.recycle();
    }

    private void initAttrs(Context context, TypedArray typedArray) {
        // Add the customize of defaultEmptyView from layout
        mEmptyText = typedArray.getString(R.styleable.NespRecyclerView_emptyText);

        int emptyDrawableResId = typedArray.getResourceId(R.styleable.NespRecyclerView_emptyDrawable, -1);
        if (emptyDrawableResId != -1) {
            mEmptyDrawable = ResourcesCompat.getDrawable(getResources(), emptyDrawableResId, context.getTheme());
        }

        // Add the customize of loadMoreView from layout
        mMaxScreenItems = typedArray.getInteger(R.styleable.NespRecyclerView_maxScreenItems, -1);
        mLoadMoreEnable = typedArray.getBoolean(R.styleable.NespRecyclerView_loadMoreEnable, false);
        mIsHideNoMoreData = typedArray.getBoolean(R.styleable.NespRecyclerView_hideNoMoreData, false);
        mLoadMoreTextColor = typedArray.getColor(R.styleable.NespRecyclerView_loadMoreTextColor, Color.GRAY);
        mLoadMoreTextSize = typedArray.getDimensionPixelSize(R.styleable.NespRecyclerView_loadMoreTextSize, getResources().getDimensionPixelSize(R.dimen.load_more_text_size));

        mLoadMoreBackgroundColor = typedArray.getColor(R.styleable.NespRecyclerView_loadMoreBackgroundColor, Color.TRANSPARENT);
        mLoadMoreProgressIndeterminateDrawableTintColor = typedArray.getColor(R.styleable.NespRecyclerView_loadMoreProgressIndeterminateDrawableTintColor, 0);

        int indeterminateDrawableResId = typedArray.getResourceId(R.styleable.NespRecyclerView_loadMoreProgressIndeterminateDrawable, -1);
        if (indeterminateDrawableResId != -1) {
            mLoadMoreProgressIndeterminateDrawable = ResourcesCompat.getDrawable(getResources(), indeterminateDrawableResId, context.getTheme());
        }

        mLoadingMoreDataText = typedArray.getString(R.styleable.NespRecyclerView_loadingMoreText);
        mLoadMoreDataFailedText = typedArray.getString(R.styleable.NespRecyclerView_loadMoreFailedText);
        mNoMoreDataText = typedArray.getString(R.styleable.NespRecyclerView_noMoreDataText);
        if (TextUtils.isEmpty(mLoadingMoreDataText))
            mLoadingMoreDataText = getResources().getString(R.string.nesprecyclerview_text_loading_more);
        if (TextUtils.isEmpty(mLoadMoreDataFailedText))
            mLoadMoreDataFailedText = getResources().getString(R.string.nesprecyclerview_text_load_more_failed);
        if (TextUtils.isEmpty(mNoMoreDataText))
            mNoMoreDataText = getResources().getString(R.string.nesprecyclerview_text_no_more_data);

        /*
         *  Add the customize of refresh from layout
         */
        mIsRefreshEnable = typedArray.getBoolean(R.styleable.NespRecyclerView_refreshEnable, true);
        mPullDownText = typedArray.getString(R.styleable.NespRecyclerView_pullDownText);
        mUpToRefreshText = typedArray.getString(R.styleable.NespRecyclerView_upToRefreshText);
        mRefreshingText = typedArray.getString(R.styleable.NespRecyclerView_refreshingText);
        mRefreshSuccessText = typedArray.getString(R.styleable.NespRecyclerView_refreshSuccessText);
        mRefreshFailedText = typedArray.getString(R.styleable.NespRecyclerView_refreshFailedText);
        if (TextUtils.isEmpty(mPullDownText))
            mPullDownText = getResources().getString(R.string.nesprecyclerview_text_pull_down);
        if (TextUtils.isEmpty(mUpToRefreshText))
            mUpToRefreshText = getResources().getString(R.string.nesprecyclerview_text_up_to_refresh);
        if (TextUtils.isEmpty(mRefreshingText))
            mRefreshingText = getResources().getString(R.string.nesprecyclerview_text_refreshing);
        if (TextUtils.isEmpty(mRefreshSuccessText))
            mRefreshSuccessText = getResources().getString(R.string.nesprecyclerview_text_refresh_success);
        if (TextUtils.isEmpty(mRefreshFailedText))
            mRefreshFailedText = getResources().getString(R.string.nesprecyclerview_text_refresh_failed);

        mRefreshBackgroundColor = typedArray.getColor(R.styleable.NespRecyclerView_refreshBackgroundColor, Color.TRANSPARENT);

        mRefreshTextSize = typedArray.getDimension(R.styleable.NespRecyclerView_refreshTextSize, getResources().getDimensionPixelSize(R.dimen.refresh_text_size));
        mRefreshTextColor = typedArray.getColor(R.styleable.NespRecyclerView_refreshTextColor, Color.GRAY);
        mRefreshArrowTintColor = typedArray.getColor(R.styleable.NespRecyclerView_refreshArrowTintColor, Color.parseColor("#8a8a8a"));
        mRefreshProgressIndeterminateDrawableTintColor = typedArray.getColor(R.styleable.NespRecyclerView_refreshProgressIndeterminateDrawableTintColor, 0);

        mRefreshMaxOffset = typedArray.getFloat(R.styleable.NespRecyclerView_refreshMaxOffset, 1000);
        mRefreshMinOffset = typedArray.getFloat(R.styleable.NespRecyclerView_refreshMinOffset, 0);
        mRefreshRotateOffset = typedArray.getFloat(R.styleable.NespRecyclerView_refreshRotateOffset, -1);

        mRefreshArrowDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_nesprecyclerview_arrow_down, null);

        int refreshArrowDrawableResId = typedArray.getResourceId(R.styleable.NespRecyclerView_refreshArrowDrawable, -1);
        int refreshProgressIndeterminateDrawableResId = typedArray.getResourceId(R.styleable.NespRecyclerView_refreshProgressIndeterminateDrawable, -1);
        if (indeterminateDrawableResId != -1) {
            mRefreshArrowDrawable = ResourcesCompat.getDrawable(getResources(), refreshArrowDrawableResId, context.getTheme());
        }
        if (refreshProgressIndeterminateDrawableResId != -1) {
            mRefreshProgressIndeterminateDrawable =
                    ResourcesCompat.getDrawable(getResources(), refreshProgressIndeterminateDrawableResId, context.getTheme());
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // Normal API
    ///////////////////////////////////////////////////////////////////////////

    /**
     * The {@link NespRecyclerView} whether is currently scrolled top.
     *
     * @return {@link #mIsScrollTop}
     */
    public Boolean isScrollTop() {
        return this.mIsScrollTop;
    }

    /**
     * The {@link NespRecyclerView} whether is currently scrolled bottom.
     *
     * @return {@link #mIsScrollBottom}
     */
    public Boolean isScrollBottom() {
        return this.mIsScrollBottom;
    }

    /**
     * The {@link NespRecyclerView} whether is currently scrolling up.
     *
     * @return {@link #mIsScrollUp}
     */
    public Boolean isScrollUp() {
        return mIsScrollUp;
    }

    /**
     * The {@link NespRecyclerView} whether is currently scrolling down.
     *
     * @return {@link #mIsScrollDown}
     */
    public Boolean isScrollDown() {
        return mIsScrollDown;
    }

    /**
     * Find the first current visible position depends on LayoutManger
     *
     * @return the first visible position
     * @throws IllegalStateException if LayoutManager is null
     * @see #setOnRefreshListener(OnRefreshListener)
     */
    public int findFirstVisibleItemPosition() {
        final LayoutManager manager = getLayoutManager();
        if (manager == null) {
            throw new IllegalStateException("Please set LayoutManager first");
        }
        int position;
        if (manager instanceof GridLayoutManager) {
            position = ((GridLayoutManager) manager).findFirstVisibleItemPosition();
        } else if (manager instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) manager).findFirstVisibleItemPosition();
        } else if (manager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) manager;
            int[] lastPositions = layoutManager.findFirstVisibleItemPositions(new int[layoutManager.getSpanCount()]);
            position = findMinPosition(lastPositions);
        } else {
            position = manager.getItemCount() - 1;
        }
        return position;
    }

    /**
     * Find the last current visible position depends on LayoutManger
     *
     * @return the last visible position
     * @throws IllegalStateException if LayoutManager is null
     * @see #setOnLoadMoreListener(OnLoadMoreListener)
     */
    public int findLastVisibleItemPosition() {
        final LayoutManager manager = getLayoutManager();
        if (manager == null) {
            throw new IllegalStateException("Please set LayoutManager first");
        }
        int position;
        if (manager instanceof GridLayoutManager) {
            position = ((GridLayoutManager) manager).findLastVisibleItemPosition();
        } else if (manager instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) manager).findLastVisibleItemPosition();
        } else if (manager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) manager;
            int[] lastPositions = layoutManager.findLastVisibleItemPositions(new int[layoutManager.getSpanCount()]);
            position = findMaxPosition(lastPositions);
        } else {
            position = manager.getItemCount() - 1;
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
     * @param position Position of the item that has changed
     * @see RecyclerView.Adapter#notifyItemChanged(int)
     * @see #notifyItemRemoved(int)
     */
    public final void notifyDateItemChanged(int position) {
        if (getAdapter() == null || mNespAdapter == null) return;
        mNespAdapter.notifyItemChanged(position);
    }

    /**
     * @param position Position of the item that has changed
     * @see RecyclerView.Adapter#notifyItemRemoved(int)
     * @see #notifyDateItemChanged(int)
     */
    public final void notifyItemRemoved(int position) {
        if (getAdapter() == null || mNespAdapter == null) return;
        mNespAdapter.notifyItemRemoved(position);
    }

    @Deprecated
    @SuppressLint("NotifyDataSetChanged")
    public final void notifyDataSetChanged() {
        if (getAdapter() == null || mNespAdapter == null) return;
        mNespAdapter.notifyDataSetChanged();
    }

    /**
     * It must be called after {@link #setLayoutManager(LayoutManager)} to adapter
     * {@link GridLayoutManager} and {@link StaggeredGridLayoutManager}
     *
     * @param adapter the original adapter
     */
    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        if (adapter == null) return;
        mNespAdapter = new NespRecyclerViewAdapter(adapter);
        super.setAdapter(mNespAdapter);
    }

    /**
     * adapt  {@link GridLayoutManager} for {@link NespRecyclerView} by set span size
     *
     * @param layoutManager layoutManager
     * @see NespRecyclerViewAdapter#adaptLayoutManager(LayoutManager)
     */
    @Override
    public void setLayoutManager(@Nullable LayoutManager layoutManager) {
        if (mNespAdapter != null) {
            mNespAdapter.adaptLayoutManager(layoutManager);
        }
        super.setLayoutManager(layoutManager);
    }

    @Nullable
    @Override
    @SuppressWarnings("rawtypes")
    public Adapter getAdapter() {
        if (mNespAdapter != null) return mNespAdapter.getOriginAdapter();
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
            if (mLoadMoreEnable && mLoadMoreState != LoadMoreState.LOADING && dy > 0) {
                if (scrollY == ((NestedScrollView) v).getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    mLoadMoreState = LoadMoreState.LOADING;
                    if (mOnLoadMoreListener != null) mOnLoadMoreListener.onLoadMore();
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
    @SuppressLint("NotifyDataSetChanged")
    public NespRecyclerView setEmptyView(View emptyView) {
        if (getAdapter() == null) {
            throw new RuntimeException("You need to call setAdapter(Adapter) before call setEmptyView()");
        }

        if (getLayoutManager() == null) {
            throw new RuntimeException("You need to call setLayoutManager(LayoutManager) before call setEmptyView()");
        }

        if (emptyView == null || this.mEmptyView != null) return this;
        this.mEmptyView = emptyView;
        mNespAdapter.notifyDataSetChanged();
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
        this.mEmptyDrawable = emptyDrawable;
        return this;
    }

    /**
     * Set empty drawable for default empty view
     * need to call {@link #setDefaultEmptyView()} before call this method.
     *
     * @param id Drawable resource id
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setEmptyDrawable(@DrawableRes int id) {
        return setEmptyDrawable(ResourcesCompat.getDrawable(getResources(), id, null));
    }

    /**
     * Set empty text for default empty view
     * need to call {@link #setDefaultEmptyView()} before call this method.
     *
     * @param emptyText Text
     * @return {@link NespRecyclerView}
     * @see #setEmptyText(int)
     */
    public NespRecyclerView setEmptyText(String emptyText) {
        this.mEmptyText = emptyText;
        return this;
    }

    /**
     * Set empty text for default empty view,
     * need to call {@link #setDefaultEmptyView()} before call this method.
     *
     * @param id empty text resource id
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setEmptyText(@StringRes int id) {
        return setEmptyText(getResources().getString(id));
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

        if (headerView == null || this.mHeaderView != null) return this;
        this.mHeaderView = headerView;
        mNespAdapter.notifyItemInserted(mRefreshHeaderView == null ? 0 : 1);
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
        return mHeaderView;
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

        if (footerView == null || this.mFooterView != null) return this;
        this.mFooterView = footerView;
        mNespAdapter.notifyItemChanged(mNespAdapter.getItemCount() - 1);
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
        if (this.mHeaderView == null) return this;
        this.mHeaderView = null;
        mNespAdapter.notifyItemRemoved(mRefreshHeaderView == null ? 0 : 1);
        return this;
    }

    /**
     * Remove footer view of {@link NespRecyclerView}
     *
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView removeFooterView() {
        if (this.mFooterView == null) return this;
        this.mFooterView = null;
        mNespAdapter.notifyItemRemoved(mNespAdapter.getItemCount() - 1);
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
        this.mOnLoadMoreListener = onLoadMoreListener;
        this.mLoadMoreEnable = true;
        this.addOnScrollListener(mOnScrollListenerForLoadMore);
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
        this.mLoadMoreEnable = loadMoreEnable;
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
        this.mLoadMoreTextSize = loadMoreTextSize;
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
        this.mLoadMoreTextColor = loadMoreTextColor;
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
        this.mLoadMoreBackgroundColor = loadMoreBackgroundColor;
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
        this.mNoMoreDataText = noMoreDataText;
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
        this.mLoadingMoreDataText = loadingMoreDataText;
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
        this.mLoadMoreDataFailedText = loadMoreDataFailedText;
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
     * Whether to hide {@link #mNoMoreDataText},default:false.
     * <p>
     * You always can use it in xml:
     * <code>app:hideNoMoreData</code>
     *
     * @param hideNoMoreData hide or not
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setHideNoMoreData(boolean hideNoMoreData) {
        this.mIsHideNoMoreData = hideNoMoreData;
        return this;
    }

    /**
     * {@link #mMaxScreenItems}
     */
    public NespRecyclerView setMaxScreenItems(int maxScreenItems) {
        this.mMaxScreenItems = maxScreenItems;
        return this;
    }

    /**
     * {@link #mMaxScreenItems}
     */
    public int getMaxScreenItems() {
        return mMaxScreenItems;
    }

    /**
     * Set IndeterminateDrawable of  progressBar when loading more.
     * <p>
     * You always can use it in xml:
     * <code>app:loadMoreIndeterminateDrawable</code>
     *
     * @param drawable IndeterminateDrawable of  progressBar
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setLoadMoreProgressIndeterminateDrawable(Drawable drawable) {
        this.mLoadMoreProgressIndeterminateDrawable = drawable;
        return this;
    }

    /**
     * Set IndeterminateDrawable of  progressBar when loading more from resource.
     * <p>
     * You always can use it in xml:
     * <code>app:loadMoreIndeterminateDrawable</code>
     *
     * @param id IndeterminateDrawable of  progressBar
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setLoadMoreProgressIndeterminateDrawable(@DrawableRes int id) {
        setLoadMoreProgressIndeterminateDrawable(ResourcesCompat.getDrawable(getResources(), id, null));
        return this;
    }

    /**
     * Set the TintColor used in the Indeterminate Drawable of load-more progress.
     *
     * @param color the color of the progress indeterminate drawable tint
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setLoadMoreProgressIndeterminateDrawableTintColor(@ColorInt int color) {
        this.mLoadMoreProgressIndeterminateDrawableTintColor = color;
        return this;
    }

    /**
     * Set the TintColor resource used in the Indeterminate Drawable of load-more progress from color resource.
     *
     * @param id the color resource id of the progress indeterminate drawable tint
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setLoadMoreProgressIndeterminateDrawableTintColorRes(@ColorRes int id) {
        setLoadMoreProgressIndeterminateDrawableTintColor(getResources().getColor(id));
        return this;
    }

    /**
     * Set content click item listener,not include empty view,header view and footer view
     *
     * @param listener onContentItemClickListener
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setOnContentItemClickListener(OnContentItemClickListener listener) {
        this.mOnContentItemClickListener = listener;
        return this;
    }

    /**
     * get {@link #mLoadMoreView}
     *
     * @return {@link #mLoadMoreView}
     */
    public View getLoadMoreView() {
        return mLoadMoreView;
    }

    /**
     * Called when load more data failed
     *
     * @see #notifyLoadMoreFinish(boolean, boolean)
     */
    public void notifyLoadMoreFailed() {
        notifyLoadMoreFinishRange(false, true, 0, 0);
    }

    /**
     * Called when load more data successful
     *
     * @param hasMore has more data or not
     * @see #notifyLoadMoreFinish(boolean, boolean)
     * @deprecated use {@link #notifyLoadMoreSuccessfulRange(boolean, int, int)} instead
     */
    @Deprecated
    public void notifyLoadMoreSuccessful(boolean hasMore) {
        notifyLoadMoreFinish(true, hasMore);
    }

    /**
     * Called when load more data successful
     *
     * @param hasMore       has more data or not
     * @param positionStart position start to changed
     * @param itemCount     item count to changed
     */
    public void notifyLoadMoreSuccessfulRange(final boolean hasMore, final int positionStart,
                                              final int itemCount) {
        notifyLoadMoreFinishRange(true, hasMore, positionStart, itemCount);
    }

    /**
     * Notify mLoadMoreView do some UI change
     *
     * @param success Is load more data successful
     * @param hasMore Whether has more data to be loaded
     * @see #setOnLoadMoreListener(OnLoadMoreListener)
     */
    @Deprecated
    @SuppressLint("NotifyDataSetChanged")
    private void notifyLoadMoreFinish(final boolean success, final boolean hasMore) {
        this.removeOnScrollListener(mOnScrollListenerForLoadMore);

        mLoadMoreState = LoadMoreState.LOAD_FAILED;

        if (success) {
            mLoadMoreState = LoadMoreState.LOAD_SUCCESS;
            mNespAdapter.notifyDataSetChanged();
        }

        if (mLoadMoreView != null) {
            changeLoadMoreUi(success, hasMore);
        } else {
            mNespAdapter.setLoadMoreInflateListener(() -> {
                changeLoadMoreUi(success, hasMore);
                mNespAdapter.setLoadMoreInflateListener(null);
            });
        }
    }

    private void notifyLoadMoreFinishRange(final boolean success, final boolean hasMore,
                                           final int positionStart, final int itemCount) {
        this.removeOnScrollListener(mOnScrollListenerForLoadMore);

        mLoadMoreState = LoadMoreState.LOAD_FAILED;

        if (success) {
            mLoadMoreState = LoadMoreState.LOAD_SUCCESS;
            mNespAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        if (mLoadMoreView != null) {
            changeLoadMoreUi(success, hasMore);
        } else {
            mNespAdapter.setLoadMoreInflateListener(() -> {
                changeLoadMoreUi(success, hasMore);
                mNespAdapter.setLoadMoreInflateListener(null);
            });
        }
    }

    private void changeLoadMoreUi(boolean success, boolean hasMore) {

        if (success) {
            if (hasMore) {
                showView(mPbLoadMore);
                mTvLoadMoreText.setText(mLoadingMoreDataText);
                this.addOnScrollListener(mOnScrollListenerForLoadMore);
            } else {
                if (mIsHideNoMoreData) {
                    //the mLoadMoreView will GONE when no more data to be loaded
                    mLoadMoreEnable = false;
                    hideView(mPbLoadMore);
                    hideView(mTvLoadMoreText);
                } else {
                    //the mLoadMoreView will display "No More Data" when no more data to be loaded
                    //and the progressBar will GONE
                    mLoadMoreView.setOnClickListener(null);
                    hideView(mPbLoadMore);
                    mTvLoadMoreText.setText(mNoMoreDataText);
                }
            }
        } else {
            //the loadMoreView will display {#tvTextLoadFailed} when load more data failed
            //and the progressBar will GONE
            mTvLoadMoreText.setText(mLoadMoreDataFailedText);
            hideView(mPbLoadMore);
        }
        mLastPbLoadMoreVisibilityTemp = mPbLoadMore.getVisibility();
        mLastPbTvLoadMoreTextTemp = mTvLoadMoreText.getText().toString();
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
        this.mRefreshBackgroundColor = refreshBackgroundColor;
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
     * @param pullDownText {@link #mPullDownText}
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setPullDownText(String pullDownText) {
        this.mPullDownText = pullDownText;
        return this;
    }

    /**
     * Set text which displayed when within a certain range of pull-down from resource.
     * <p>
     * You always can use it in xml:
     * <code>app:pullDownText</code>
     *
     * @param pullDownTextResId {@link #mPullDownText}
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
     * @param upToRefreshText {@link #mUpToRefreshText}
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setUpToRefreshText(String upToRefreshText) {
        this.mUpToRefreshText = upToRefreshText;
        return this;
    }

    /**
     * Set text which displayed when outside a certain range of pull-down from resource.
     * <p>
     * You always can use it in xml:
     * <code>app:upToRefreshText</code>
     *
     * @param upToRefreshTextResId {@link #mUpToRefreshText}
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
     * @param refreshingText {@link #mRefreshingText}
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setRefreshingText(String refreshingText) {
        this.mRefreshingText = refreshingText;
        return this;
    }

    /**
     * Set text which displayed when refreshing from resource.
     * <p>
     * You always can use it in xml:
     * <code>app:refreshingText</code>
     *
     * @param refreshingTextResId {@link #mRefreshingText}
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
     * @param refreshSuccessText {@link #mRefreshSuccessText}
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setRefreshSuccessText(String refreshSuccessText) {
        this.mRefreshSuccessText = refreshSuccessText;
        return this;
    }

    /**
     * Set text which displayed when refresh success from resource.
     * <p>
     * You always can use it in xml:
     * <code>app:refreshSuccessText</code>
     *
     * @param refreshSuccessTextResId {@link #mRefreshSuccessText}
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
     * @param refreshFailedText {@link #mRefreshFailedText}
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setRefreshFailedText(String refreshFailedText) {
        this.mRefreshFailedText = refreshFailedText;
        return this;
    }

    /**
     * Set text which displayed when refresh failed from resource.
     * <p>
     * You always can use it in xml:
     * <code>app:refreshFailedText</code>
     *
     * @param refreshFailedTextResId {@link #mRefreshFailedText}
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
        return this.mIsRefreshing;
    }

    /**
     * Called when refresh finish
     *
     * @param isRefreshSuccess true->Success,false->failed
     */
    @SuppressWarnings("NotifyDataSetChanged")
    public void notifyRefreshFinish(Boolean isRefreshSuccess) {
        hideView(mProgressBarRefresh);
        if (isRefreshSuccess) {
            mTextViewRefresh.setText(mRefreshSuccessText);
        } else {
            mTextViewRefresh.setText(mRefreshFailedText);
        }
        ValueAnimator animator = new ValueAnimator();
        animator.setIntValues(mLinearLayoutRefreshingHeight, mLlInitHeight);
        animator.setStartDelay(800);
        animator.setDuration(400);
        animator.addUpdateListener(animation -> {
            mLlRefreshRootLayoutParams.height = (int) animation.getAnimatedValue();
            mLinearLayoutRefreshRoot.setLayoutParams(mLlRefreshRootLayoutParams);
        });
        animator.start();
        if (isRefreshSuccess) mNespAdapter.notifyDataSetChanged();
        mIsRefreshing = false;
    }

    /**
     * Whether to enable Refresh.
     *
     * @param refreshEnable enable or not
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setRefreshEnable(Boolean refreshEnable) {
        mIsRefreshEnable = refreshEnable;
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
        this.mOnRefreshListener = onRefreshListener;
        return this;
    }

    /**
     * Set text size of {@link #mTextViewRefresh}
     *
     * @param refreshTextSize refreshTextSize
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setRefreshTextSize(float refreshTextSize) {
        this.mRefreshTextSize = refreshTextSize;
        return this;
    }

    /**
     * Set text size of {@link #mTextViewRefresh} from resource
     *
     * @param refreshTextSizeResId refreshTextSize
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setRefreshTextSize(@DimenRes int refreshTextSizeResId) {
        setRefreshTextSize(getResources().getDimension(refreshTextSizeResId));
        return this;
    }

    /**
     * Set text color of {@link #mTextViewRefresh}
     *
     * @param refreshTextColor refreshTextColor
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setRefreshTextColor(@ColorInt int refreshTextColor) {
        this.mRefreshTextColor = refreshTextColor;
        return this;
    }

    /**
     * Set text color of {@link #mTextViewRefresh} from resource
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
        this.mRefreshArrowTintColor = refreshArrowTintColor;
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
        this.mRefreshArrowDrawable = refreshArrowDrawable;
        return this;
    }

    /**
     * Set drawable of down arrow from resource.
     *
     * @param id the resource id of refresh arrow drawable.
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setRefreshArrowDrawable(@DrawableRes int id) {
        setRefreshArrowDrawable(ResourcesCompat.getDrawable(getResources(), id, null));
        return this;
    }

    /**
     * Set the TintColor used in the Indeterminate Drawable of refresh progress.
     *
     * @param color color
     */
    public NespRecyclerView setRefreshProgressIndeterminateDrawableTintColor(int color) {
        Log.e(TAG, "NespRecyclerView.setRefreshProgressIndeterminateDrawableTintColor: color" + color);
        this.mRefreshProgressIndeterminateDrawableTintColor = color;
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
        this.mRefreshProgressIndeterminateDrawable = refreshProgressIndeterminateDrawable;
        return this;
    }

    /**
     * Set IndeterminateDrawable of  progressBar when refreshing.
     *
     * @param id id
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setRefreshProgressIndeterminateDrawable(@DrawableRes int id) {
        setRefreshProgressIndeterminateDrawable(ResourcesCompat.getDrawable(getResources(), id, null));
        return this;
    }

    /**
     * Set max finger swipe offset of stopping change layout height.
     *
     * @param offset the max finger swipe offset of stopping change layout height
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setRefreshMaxOffset(float offset) {
        this.mRefreshMaxOffset = offset;
        return this;
    }

    /**
     * Set min finger swipe offset of starting change layout height.
     *
     * @param offset the min finger swipe offset of starting change layout height
     * @return {@link NespRecyclerView}
     */
    public NespRecyclerView setRefreshMinOffset(float offset) {
        this.mRefreshMinOffset = offset;
        return this;
    }

    /**
     * Set min finger swipe offset of starting rotate arrow.
     * <p>
     * <b>Deprecated:</b>
     * It is recommended to use the default value
     *
     * @param offset the min finger swipe offset of starting rotate arrow
     * @return {@link NespRecyclerView}
     */
    @Deprecated
    public NespRecyclerView setRefreshRotateOffset(float offset) {
        this.mRefreshRotateOffset = offset;
        return this;
    }

    /**
     * Reset field prevents useless cached data after a one-cycle pull-down-to-refresh event
     */
    private void resetRefreshField() {
        mPointYDown = 0;
        mPointYMove = 0;
        mFingerSideOffset = 0;
        mViewSlideOffset = 0;
    }

    private int getLinearLayoutRefreshingHeight() {
        mImageViewRefreshArrow.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        mTextViewRefresh.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        //Calculate linearLayoutRefreshingHeight
        return mLinearLayoutRefreshingHeight = Math.max(mImageViewRefreshArrow.getHeight(), mTextViewRefresh.getHeight()) + 50;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mIsScrollTop = !canScrollVertically(-1);
//        Log.e(TAG, "NespRecyclerView.onTouchEvent: \n" + isScrollTop);
        if (!mIsRefreshEnable
                || !mIsScrollTop
                || getScrollState() != SCROLL_STATE_DRAGGING
                || mLoadMoreState == LoadMoreState.LOADING) return super.onTouchEvent(event);

        if (mIsRefreshing) return true;

        int eventAction = event.getAction();

        if (eventAction == MotionEvent.ACTION_DOWN) {
            hideView(mProgressBarRefresh);
            mPointYDown = event.getY();
            mIsCalledActionDown = true;
        }

        if (eventAction == MotionEvent.ACTION_MOVE) {

            mIsCalledActionMove = true;

            mPointYMove = event.getY();

            if (!mIsCalledActionDown) {
                hideView(mProgressBarRefresh);
                mPointYDown = mPointYMove;
                mIsCalledActionDown = true;
            }

            //Customize Refresh View
            mImageViewRefreshArrow.setImageDrawable(mRefreshArrowDrawable);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mImageViewRefreshArrow.getDrawable().setTint(mRefreshArrowTintColor);
            }

            mFingerSideOffset = mPointYMove - mPointYDown;
            mViewSlideOffset = mFingerSideOffset / 2;

            //If over minRefreshOffset it will start to move downward
            if (mFingerSideOffset > mRefreshMinOffset) {
                mTextViewRefresh.setText(mPullDownText);

                //If over maxRefreshOffset it will stop to move downward
                if (mRefreshRotateOffset == -1)
                    //Set value from xml resource
                    mRefreshRotateOffset = NespRecyclerView.this.getLinearLayoutRefreshingHeight() + 30;

                if (mViewSlideOffset > mRefreshRotateOffset) {

                    mTextViewRefresh.setText(mUpToRefreshText);
                    if (!mIsRefreshRowRotatedUp) {
                        rotateView(mImageViewRefreshArrow, true);
                        mIsRefreshRowRotatedUp = true;
                    }
                } else {
                    mTextViewRefresh.setText(mPullDownText);
                    if (mIsRefreshRowRotatedUp) {
                        rotateView(mImageViewRefreshArrow, false);
                        mIsRefreshRowRotatedUp = false;
                    }
                }

                if (mViewSlideOffset <= mRefreshMaxOffset) {
                    mLlRefreshRootLayoutParams.height = (int) mViewSlideOffset;
                    mLinearLayoutRefreshRoot.setLayoutParams(mLlRefreshRootLayoutParams);
                }

                mLinearLayoutRefreshRoot.setBackgroundColor(mRefreshBackgroundColor);
            } else if (mFingerSideOffset < 0) {
                mIsCalledActionDown = false;
                return super.onTouchEvent(event);
            } else {
                return super.onTouchEvent(event);
            }
        }

        if (eventAction == MotionEvent.ACTION_UP) {
//            Log.e(TAG, "NespRecyclerView.onTouchEvent: isCalledActionMove " + isCalledActionMove);
            if (!mIsCalledActionMove) return super.onTouchEvent(event);

            mIsCalledActionMove = false;
            mIsCalledActionDown = false;
            mIsScrollTop = false;

            mFingerSideOffset = mPointYMove - mPointYDown;

            ValueAnimator valueAnimator = new ValueAnimator();

            if (mIsRefreshRowRotatedUp) {
                mIsRefreshRowRotatedUp = false;
                mIsRefreshing = true;
                showView(mProgressBarRefresh);
                mTextViewRefresh.setText(mRefreshingText);
                mImageViewRefreshArrow.setImageDrawable(null);

                rotateView(mImageViewRefreshArrow, false);

                valueAnimator.setIntValues((int) mViewSlideOffset, getLinearLayoutRefreshingHeight());
                valueAnimator.setInterpolator(new AnticipateOvershootInterpolator());
                if (mOnRefreshListener != null) mOnRefreshListener.onRefresh();
            } else {
                valueAnimator.setIntValues((int) mViewSlideOffset, mLlInitHeight);
            }

            if (mFingerSideOffset > 0) {
                valueAnimator.setDuration((long) (mViewSlideOffset));
                valueAnimator.addUpdateListener(animation -> {
                    mLlRefreshRootLayoutParams.height = (Integer) animation.getAnimatedValue();
                    mLinearLayoutRefreshRoot.setLayoutParams(mLlRefreshRootLayoutParams);
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
     * Note:This variable uses a different judgment with {@link #mOnScrollListenerForLoadMore} to reach the bottom
     */
    @Override
    public void onScrolled(int dx, int dy) {
        mIsScrollTop = !canScrollVertically(-1);
        mIsScrollBottom = !canScrollVertically(1);
        mIsScrollUp = dy > 0;
        mIsScrollDown = dy < 0;
    }

//================================Adapter=======================================

    @SuppressWarnings("rawtypes")
    public class NespRecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder> {

        private final Adapter originAdapter;

        private LoadMoreInflateListener mLoadMoreInflateListener;

        //================================Item Type=======================================
        static final int ITEM_TYPE_REFRESH_HEADER = 0x000041;
        static final int ITEM_TYPE_NORMAL = 0x000042;
        static final int ITEM_TYPE_EMPTY = 0x000043;
        static final int ITEM_TYPE_HEADER = 0x000044;
        static final int ITEM_TYPE_FOOTER = 0x000045;
        static final int ITEM_TYPE_LOAD_MORE = 0x000046;
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
                    mRefreshHeaderView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.nesprecyclerview_header_refresh_content, parent, false);
                    mLinearLayoutRefreshRoot = mRefreshHeaderView.findViewById(R.id.nrv_header_refresh_root);
                    mTextViewRefresh = mRefreshHeaderView.findViewById(R.id.nrv_header_refresh_tv);
                    mImageViewRefreshArrow = mRefreshHeaderView.findViewById(R.id.nrv_header_refresh_iv);
                    mProgressBarRefresh = mRefreshHeaderView.findViewById(R.id.nrv_header_refresh_pb);
                    mLlRefreshRootLayoutParams = (LinearLayout.LayoutParams) mLinearLayoutRefreshRoot.getLayoutParams();

                    /*
                    Customize for refresh
                     */
                    mTextViewRefresh.setTextColor(mRefreshTextColor);
                    mTextViewRefresh.setTextSize(mRefreshTextSize);

                    if (mRefreshProgressIndeterminateDrawable != null) {
                        mProgressBarRefresh.setIndeterminateDrawable(mRefreshProgressIndeterminateDrawable);
                    }

                    if (mRefreshProgressIndeterminateDrawableTintColor != 0) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            mProgressBarRefresh.getIndeterminateDrawable().setTint(mRefreshProgressIndeterminateDrawableTintColor);
                        }
                    }

                    return new NespRecyclerViewViewHolder(mRefreshHeaderView);
                case ITEM_TYPE_HEADER:
                    return new NespRecyclerViewViewHolder(mHeaderView);
                case ITEM_TYPE_EMPTY: {
                    if (mEmptyDrawable != null) {
                        try {
                            ((ImageView) mEmptyView.findViewById(R.id.nrcv_empty_iv))
                                    .setImageDrawable(mEmptyDrawable);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (mEmptyText != null && !mEmptyText.isEmpty()) {
                        try {
                            ((TextView) mEmptyView.findViewById(R.id.nrcv_empty_tv))
                                    .setText(mEmptyText);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    return new NespRecyclerViewViewHolder(mEmptyView);
                }
                case ITEM_TYPE_FOOTER:
                    return new NespRecyclerViewViewHolder(mFooterView);
                case ITEM_TYPE_LOAD_MORE:
                    mLoadMoreView = LayoutInflater.from(getContext())
                            .inflate(R.layout.nesprecyclerview_load_more, parent, false);

                    /*
                     *  Customize the loadMoreView
                     */
                    mLoadMoreView.setBackgroundColor(mLoadMoreBackgroundColor);
                    mPbLoadMore = mLoadMoreView.findViewById(R.id.nrv_load_more_pb);
                    if (mLoadMoreProgressIndeterminateDrawable != null) {
                        mPbLoadMore.setIndeterminateDrawable(mLoadMoreProgressIndeterminateDrawable);
                    }

                    if (mLoadMoreProgressIndeterminateDrawableTintColor != 0) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            mPbLoadMore.getIndeterminateDrawable().setTint(mLoadMoreProgressIndeterminateDrawableTintColor);
                        }
                    }
                    mTvLoadMoreText = mLoadMoreView.findViewById(R.id.nrv_load_more_tv);
                    mTvLoadMoreText.setTextSize(mLoadMoreTextSize);
                    mTvLoadMoreText.setTextColor(mLoadMoreTextColor);
                    mTvLoadMoreText.setText(mLoadingMoreDataText);

                    //Click to reload data if load more failed
                    mLoadMoreView.setOnClickListener(v -> {
                        if (mLoadMoreState != LoadMoreState.LOAD_FAILED) return;
                        mLoadMoreState = LoadMoreState.LOADING;
                        mTvLoadMoreText.setText(mLoadingMoreDataText);
                        showView(mTvLoadMoreText);
                        showView(mPbLoadMore);
                        if (mOnLoadMoreListener != null) mOnLoadMoreListener.onLoadMore();
                    });

                    //when remove footerView will trigger onCreateViewHolder()
                    if (!TextUtils.isEmpty(mLastPbTvLoadMoreTextTemp)) {
                        mTvLoadMoreText.setText(mLastPbTvLoadMoreTextTemp);
                        mPbLoadMore.setVisibility(mLastPbLoadMoreVisibilityTemp);
                    }

                    if (mLoadMoreInflateListener != null) {
                        mLoadMoreInflateListener.onLoadMoreInflated();
                    }
                    return new NespRecyclerViewViewHolder(mLoadMoreView);
                default:
                    return originAdapter.onCreateViewHolder(parent, viewType);
            }

        }

        @Override
        @SuppressWarnings("unchecked")
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
                if (mOnContentItemClickListener != null)
                    mOnContentItemClickListener.onContentItemClick(holder, realPosition);
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
            if (mIsRefreshEnable)
                itemCount++;
            //Add HeaderView
            if (mHeaderView != null) itemCount++;
            //Add FooterView
            if (mFooterView != null) itemCount++;
            //Add EmptyView
            if (mEmptyView != null && originItemCount == 0) {
                itemCount++;
                //If the content data is empty,no need to add LoadMoreView
                return itemCount;
            }
            //Add LoadMoreView
            if (mLoadMoreState != LoadMoreState.LOAD_NOT
                    && mLoadMoreEnable
                    && originItemCount >= mMaxScreenItems
            ) itemCount++;
            return itemCount;
        }

        @Override
        public int getItemViewType(int position) {
            /*
             * The order of the view is very important to be cautiously modified, otherwise it will be confused and return wrong type.
             */
            if (mIsRefreshEnable && position == 0) return ITEM_TYPE_REFRESH_HEADER;
            if (mHeaderView != null && position == (mRefreshHeaderView == null ? 0 : 1))
                return ITEM_TYPE_HEADER;
            if (mFooterView != null && position == getItemCount() - 1) return ITEM_TYPE_FOOTER;
            if (mEmptyView != null && originAdapter.getItemCount() == 0) {
                //If the content data is empty,no need to add LoadMoreView
                return ITEM_TYPE_EMPTY;
            } else if (mLoadMoreState != LoadMoreState.LOAD_NOT
                    && position == getLoadMorePosition()
                    && mLoadMoreEnable
                    && originAdapter.getItemCount() >= mMaxScreenItems
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

            if (mRefreshHeaderView != null) {
                position--;
            }

            if (mHeaderView != null) {
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
            if (mFooterView == null) {
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
        return LayoutInflater.from(getContext()).inflate(layoutRes, NespRecyclerView.this, false);
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
