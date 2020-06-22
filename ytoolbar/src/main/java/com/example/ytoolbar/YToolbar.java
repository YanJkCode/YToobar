package com.example.ytoolbar;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.ytoolbar.utils.ImmersionModeUtil;
import com.example.ytoolbar.utils.ScreenUtil;


public class YToolbar extends LinearLayout {
    /**
     * 宽 没啥用.默认占满横向
     */
    private float mWidth;

    /**
     * 高 以内容自适应
     */
    private float mHeight;

    /**
     * 导航栏透明 底部操作键 true关闭  false正常显示
     */
    private boolean statusBarNavi;
    /**
     * 状态栏颜色 true黑  false白色
     */
    private boolean statusIcon;

    /**
     * 是否开启 返回按钮监听
     */
    private boolean openBack;
    /**
     * 标题 内容
     */
    private String toolbarTitleText;
    /**
     * 标题颜色
     */
    private int toolbarTitleColor;
    /**
     * 标题大小
     */
    private float toolbarTitleSize;
    /**
     * 是否开启 加粗
     */
    private boolean toolbarTitleOpenBold;
    /**
     * TvBtn 字体大小
     */
    private int toolbarRightTvBtnSize;
    /**
     * TvBtn 是否加粗
     */
    private boolean toolbarRightTvBtnOpenBold;
    /**
     * TvBtn 内容颜色
     */
    private int toolbarRightTvBtnColor;
    /**
     * TvBtn 内容
     */
    private String toolbarRightTvBtnText;
    /**
     * 是否显示TvBtn
     */
    private boolean openRightTvBtn;
    /**
     * 是否显示ImgBtn
     */
    private boolean openRightImgBtn;

    private ConstraintLayout toolbar;
    private ImageView backIcon;
    private TextView title;
    private TextView tvBtn;
    private ImageView imgBtn;
    private boolean isOpenStatusBar;

    public YToolbar(Context context) {
        this(context, null);
    }

    public YToolbar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public YToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        initView(context);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.YToolbar);
        if (ta != null) {
            ////宽高
            mWidth = ta.getLayoutDimension(R.styleable.YToolbar_android_layout_width, ScreenUtil.getScreenWidth(context));
            mHeight = ta.getLayoutDimension(R.styleable.YToolbar_android_layout_height, android.R.attr.actionBarSize);

            statusBarNavi = ta.getBoolean(R.styleable.YToolbar_toolbarStatusBarNavi, false);
            statusIcon = ta.getBoolean(R.styleable.YToolbar_toolbarStatusIcon, true);
            isOpenStatusBar = ta.getBoolean(R.styleable.YToolbar_toolbarIsOpenStatusBar, true);
            openBack = ta.getBoolean(R.styleable.YToolbar_toolbarOpenBack, false);

            //使用final 可以让图片直接显示到 预览布局中 要不然只能在运行后才可以看到
            final Drawable toolbarBackIcon = ta.getDrawable(R.styleable.YToolbar_toolbarBackIcon);
            setBackIcon(toolbarBackIcon);

            final Drawable toolbarBackground = ta.getDrawable(R.styleable.YToolbar_toolbarBackground);
            setToolbarBackground(toolbarBackground);

            toolbarTitleText = ta.getString(R.styleable.YToolbar_toolbarTitleText);
            toolbarTitleColor = ta.getColor(R.styleable.YToolbar_toolbarTitleColor, Color.BLACK);
            toolbarTitleOpenBold = ta.getBoolean(R.styleable.YToolbar_toolbarTitleOpenBold, false);
            toolbarTitleSize = ScreenUtil.px2dip(context, ta.getDimension(R.styleable.YToolbar_toolbarTitleSize,
                    ScreenUtil.dip2px(context, 18)));


            final Drawable toolbarRightImgBtnIcon = ta.getDrawable(R.styleable.YToolbar_toolbarRightImgBtnIcon);
            setRightImgIcon(toolbarRightImgBtnIcon);

            toolbarRightTvBtnText = ta.getString(R.styleable.YToolbar_toolbarRightTvBtnText);
            toolbarRightTvBtnColor = ta.getColor(R.styleable.YToolbar_toolbarRightTvBtnColor, Color.BLACK);
            toolbarRightTvBtnOpenBold = ta.getBoolean(R.styleable.YToolbar_toolbarRightTvBtnOpenBold, false);
            toolbarRightTvBtnSize = ScreenUtil.px2dip(context, ta.getDimension(R.styleable.YToolbar_toolbarRightTvBtnSize,
                    ScreenUtil.dip2px(context, 14)));


            openRightTvBtn = ta.getBoolean(R.styleable.YToolbar_toolbarOpenRightTvBtn, false);
            openRightImgBtn = ta.getBoolean(R.styleable.YToolbar_toolbarOpenRightImgBtn, false);


            //获取属性完毕 释放资源
            ta.recycle();
        } else {
            mWidth = ViewGroup.LayoutParams.MATCH_PARENT;
            mHeight = android.R.attr.actionBarSize;

            statusIcon = true;

            toolbarTitleColor = Color.BLACK;
            toolbarTitleSize = 18;

            toolbarRightTvBtnColor = Color.BLACK;
            toolbarRightTvBtnSize = 14;
        }
        setListener(context);
        lodingViewData();
    }

    private void initView(Context context) {
        toolbar = (ConstraintLayout) LayoutInflater.from(context).inflate(R.layout.y_toolbar_view, this, false);
        addView(toolbar);
        backIcon = toolbar.findViewById(R.id.toolbar_back);
        title = toolbar.findViewById(R.id.toolbar_title);
        tvBtn = toolbar.findViewById(R.id.toolbar_right_tv_btn);
        imgBtn = toolbar.findViewById(R.id.toolbar_right_img_btn);
    }

    private void setListener(Context context) {
        if (context instanceof Activity) {
            setStatusBar((Activity) context, statusBarNavi, statusIcon);
            if (openBack) {
                setOnBackListener(new OnBackListener() {
                    @Override
                    public void onBackClick(View v) {
                        if (v.getVisibility() == ViewGroup.VISIBLE) {
                            if (YToolbar.this != null) {
                                Context backContext = getContext();
                                if (backContext != null && backContext instanceof Activity) {
                                    ((Activity) backContext).finish();
                                }
                            }
                        }
                    }
                });
            }
        }
    }

    private void lodingViewData() {
        setTitle(toolbarTitleText);
        setTitleColor(toolbarTitleColor);
        setTitleSize(toolbarTitleSize);
        setBold(toolbarTitleOpenBold);

        setRightTvBtnText(toolbarRightTvBtnText);
        setRightTvBtnColor(toolbarRightTvBtnColor);
        setRightTvBtnSize(toolbarRightTvBtnSize);
        setRightTvBtnBold(toolbarRightTvBtnOpenBold);

        if (openRightTvBtn) {
            setRightTvBtnVisibility(true);
        } else {
            setRightImgBtnVisibility(openRightImgBtn);
        }
    }

    @Override
    public void setOrientation(int orientation) {
        super.setOrientation(VERTICAL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMinimumHeight((int) mHeight);
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        setLayoutParams(layoutParams);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ViewParent parent = getParent();
        if (parent != null && parent instanceof ViewGroup) {
            View childAt = ((ViewGroup) parent).getChildAt(0);
            if (childAt == this && isOpenStatusBar) {
                toolbar.setPadding(toolbar.getPaddingLeft(),
                        ImmersionModeUtil.getStatusBarHeight(getContext()),
                        toolbar.getPaddingRight(),
                        toolbar.getPaddingBottom());
            }
        }
    }

    public void setRightTvBtnVisibility(boolean visibility) {
        openRightTvBtn = visibility;
        if (visibility) {
            tvBtn.setVisibility(VISIBLE);
            imgBtn.setVisibility(GONE);
        } else {
            tvBtn.setVisibility(GONE);
        }
    }

    public void setRightImgBtnVisibility(boolean visibility) {
        openRightImgBtn = visibility;
        if (visibility) {
            imgBtn.setVisibility(VISIBLE);
            tvBtn.setVisibility(GONE);
        } else {
            imgBtn.setVisibility(GONE);
        }
    }

    public void setBold(boolean isBold) {
        TextPaint tp = title.getPaint();
        tp.setFakeBoldText(isBold);
    }

    public void setRightTvBtnBold(boolean isBold) {
        TextPaint tp = tvBtn.getPaint();
        tp.setFakeBoldText(isBold);
    }


    public void setToolbarBackground(Drawable background) {
        if (background != null && toolbar != null)
            toolbar.setBackgroundDrawable(background);
    }

    public void setToolbarBackground(@DrawableRes int resid) {
        if (resid != 0 && toolbar != null)
            toolbar.setBackgroundResource(resid);
    }

    public void setBackIcon(Drawable background) {
        if (background != null && backIcon != null)
            backIcon.setImageDrawable(background);
    }

    public void setBackIcon(@DrawableRes int resid) {
        if (resid != 0 && backIcon != null)
            backIcon.setImageResource(resid);
    }

    public void setRightImgIcon(Drawable background) {
        if (background != null && imgBtn != null)
            imgBtn.setImageDrawable(background);
    }

    public void setRightImgIcon(@DrawableRes int resid) {
        if (resid != 0 && imgBtn != null)
            imgBtn.setImageResource(resid);
    }

    public void setTitle(CharSequence text) {
        if (title != null)
            title.setText(text);
    }

    public void setTitle(@StringRes int resid) {
        if (title != null)
            title.setText(resid);
    }

    public void setTitleColor(@ColorInt int colorInt) {
        if (title != null)
            title.setTextColor(colorInt);
    }

    public void setTitleSize(float titleSize) {
        if (title != null)
            title.setTextSize(titleSize);
    }

    public void setRightTvBtnText(CharSequence text) {
        if (tvBtn != null)
            tvBtn.setText(text);
    }

    public void setRightTvBtnText(@StringRes int resid) {
        if (tvBtn != null)
            tvBtn.setText(resid);
    }

    public void setRightTvBtnColor(@ColorInt int colorInt) {
        if (tvBtn != null)
            tvBtn.setTextColor(colorInt);
    }

    public void setRightTvBtnSize(float titleSize) {
        if (tvBtn != null)
            tvBtn.setTextSize(titleSize);
    }

    public void setStatusBar(Activity activity) {
        setStatusBar(activity, true, true);
    }

    public void setStatusBar(Activity activity, boolean navi) {
        setStatusBar(activity, navi, true);
    }

    public void setStatusBar(Activity activity, boolean navi, boolean darkStatusIcon) {
        ImmersionModeUtil.setStatusBar(activity, navi, darkStatusIcon);
    }

    private OnBackListener mOnBackListener;

    public void setOnBackListener(OnBackListener onBackListener) {
        mOnBackListener = onBackListener;
        backIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getVisibility() == ViewGroup.VISIBLE) {
                    if (mOnBackListener != null) {
                        mOnBackListener.onBackClick(v);
                    }
                }
            }
        });
    }

    public interface OnBackListener {
        void onBackClick(View v);
    }

    private OnRightImgBtnListener mOnRightImgBtnListener;

    public void setOnRightImgBtnListener(OnRightImgBtnListener onRightImgBtnListener) {
        mOnRightImgBtnListener = onRightImgBtnListener;
        if (imgBtn != null)
            imgBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getVisibility() == ViewGroup.VISIBLE) {
                        if (mOnRightImgBtnListener != null) {
                            mOnRightImgBtnListener.onClick(v);
                        }
                    }
                }
            });
    }

    public interface OnRightImgBtnListener {
        void onClick(View v);
    }

    private OnRightImgBtnLongListener mOnRightImgBtnLongListener;

    public void setOnRightImgBtnLongListener(OnRightImgBtnLongListener onRightImgBtnLongListener) {
        mOnRightImgBtnLongListener = onRightImgBtnLongListener;
        if (imgBtn != null)
            imgBtn.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (v.getVisibility() == ViewGroup.VISIBLE) {
                        if (mOnRightImgBtnLongListener != null) {
                            return mOnRightImgBtnLongListener.onLongClick(v);
                        }
                    }
                    return false;
                }
            });
    }

    public interface OnRightImgBtnLongListener {
        boolean onLongClick(View v);
    }


    private OnRightTvBtnListener mOnRightTvBtnListener;

    public void setOnRightTvBtnListener(OnRightTvBtnListener onRightTvBtnListener) {
        mOnRightTvBtnListener = onRightTvBtnListener;
        if (tvBtn != null)
            tvBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getVisibility() == ViewGroup.VISIBLE) {
                        if (mOnRightTvBtnListener != null) {
                            mOnRightTvBtnListener.onClick(v);
                        }
                    }
                }
            });
    }

    public interface OnRightTvBtnListener {
        void onClick(View v);
    }

    private OnRightTvBtnLongListener mOnRightTvBtnLongListener;

    public void setOnRightTvBtnLongListener(OnRightTvBtnLongListener onRightTvBtnLongListener) {
        mOnRightTvBtnLongListener = onRightTvBtnLongListener;
        if (tvBtn != null)
            tvBtn.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (v.getVisibility() == ViewGroup.VISIBLE) {
                        if (mOnRightTvBtnListener != null) {
                            return mOnRightTvBtnLongListener.onLongClick(v);
                        }
                    }
                    return false;
                }
            });
    }

    public interface OnRightTvBtnLongListener {
        boolean onLongClick(View v);
    }
}
