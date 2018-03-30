package com.example.a9.myapplication2;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;


/**
 * 自定义控件实现 TabLayout效果
 *
 * @author Liuhaijun
 * @time 2018/2/30  14:13
 */
public class TabLayoutView extends RelativeLayout {

    private static final String TAG = "TabLayoutView";

    private HorizontalScrollView hs_indicator;
    private RadioGroup rg_indicator;
    private View iv_indicator, relativeLayout_indicator;//滑块,整个容器
    private Context mContext;
    /**
     * radioButton 状态颜色选择集合
     **/
    private ColorStateList colorStateList;


    //新的属性
    private int tabWidth, tabWidthInit;
    private int tabTextSize;
    private int tabIndicatorColor;
    private int tabIndicatorHeight;
    private int tabIndicatorWidth;
    private ViewPager viewPager;
    private int tabPadding, tabIndicatorMarginBottom, stopTablNumber = -1;
    private View view;

    public TabLayoutView(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public TabLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public TabLayoutView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TabLayoutView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, 0);
    }


    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        mContext = context;

        TypedArray array = mContext.getTheme().obtainStyledAttributes(new int[]{
                android.R.attr.colorPrimary,
                android.R.attr.colorPrimaryDark,
                android.R.attr.colorAccent,
        });
        int colorPrimary = array.getColor(0, 0);

        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TabLayoutView, defStyleAttr, defStyleRes);

        //tabItem的属性初始化
        tabWidthInit = typedArray.getDimensionPixelSize(R.styleable.TabLayoutView_tabWidth, 0);//tab宽度，不设置就是wrap_content
        tabTextSize = typedArray.getDimensionPixelSize(R.styleable.TabLayoutView_tabTextSize, sp2px(mContext, 15f));//tab字体大小,默认15sp
        tabTextSize = px2sp(mContext, tabTextSize);
        int tabTextColor = typedArray.getColor(R.styleable.TabLayoutView_tabTextColor, Color.BLACK);//tab颜色
        int tabSelectedTextColor = typedArray.getColor(R.styleable.TabLayoutView_tabSelectedTextColor, colorPrimary);//tab选中的颜色
        colorStateList = createColorStateList(tabSelectedTextColor, tabTextColor);

        //tabIndicator的属性初始化
        tabIndicatorColor = typedArray.getColor(R.styleable.TabLayoutView_tabIndicatorColor, colorPrimary);//滑块的颜色
        tabIndicatorHeight = typedArray.getDimensionPixelSize(R.styleable.TabLayoutView_tabIndicatorHeight, 3);//滑块的高度
        tabIndicatorWidth = typedArray.getDimensionPixelSize(R.styleable.TabLayoutView_tabIndicatorWidth, 0);//滑块的宽度，不传就和tab一样宽
        tabPadding = typedArray.getDimensionPixelSize(R.styleable.TabLayoutView_tabPadding, dip2px(mContext, 26f));//如果tab没有设置Padding，默认26dp
        tabIndicatorMarginBottom = typedArray.getDimensionPixelSize(R.styleable.TabLayoutView_tabIndicatorMarginBottom, dip2px(mContext, 4f));//如果滑块没有设置MarginBottom，默认4dp
        typedArray.recycle();

        //把我们的布局添加到当前控件中
        view = View.inflate(context, R.layout.item_tab_radiogroup, null);
        hs_indicator = (HorizontalScrollView) view.findViewById(R.id.hs_indicator);
        rg_indicator = (RadioGroup) view.findViewById(R.id.rg_indicator);
        iv_indicator = view.findViewById(R.id.iv_indicator);
        relativeLayout_indicator = view.findViewById(R.id.relativeLayout_indicator);
        addView(view);


        rg_indicator.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (rg_indicator.getChildAt(checkedId) != null) {

                    RadioButton radioButton1 = (RadioButton) rg_indicator
                            .getChildAt(checkedId);

                    Resources resources = mContext.getResources();
                    DisplayMetrics dm = resources.getDisplayMetrics();
                    int width = dm.widthPixels;//获取屏幕宽度

                    stopTablNumber = (stopTablNumber == -1) ? ((width / tabWidth / 2)) : stopTablNumber;//0表示停留在第一个 1就是第二个.... (默认停留中间)
                    RadioButton radioButton2 = (RadioButton) rg_indicator
                            .getChildAt(stopTablNumber);
                    if (radioButton1 != null && radioButton2 != null) {
                        hs_indicator.smoothScrollTo(
                                (checkedId > stopTablNumber ? radioButton1.getLeft() : 0)
                                        - radioButton2.getLeft(), 0);
                        if (viewPager != null) {
                            viewPager.setCurrentItem(checkedId);
                        }
                    }
                }

            }
        });

    }

    /**
     * 重新计算绘制后的高度
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        view.setLayoutParams(new RelativeLayout.LayoutParams(widthMeasureSpec, heightMeasureSpec));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        postInvalidate();
    }

    /**
     * 设置tab停留的位置（默认停留在中间）
     *
     * @param stopTablNumber
     */
    public void setStopTablNumber(int stopTablNumber) {
        this.stopTablNumber = stopTablNumber;
    }

    /**
     * convert px to its equivalent sp
     * <p>
     * 将px转换为sp
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * convert sp to its equivalent px
     * <p>
     * 将sp转换为px
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 对TextView设置不同状态时其文字显示颜色
     */
    private ColorStateList createColorStateList(int check, int normal) {
        int[] colors = new int[]{check, normal};
        int[][] states = new int[2][];
        states[0] = new int[]{android.R.attr.state_checked};
        states[1] = new int[]{};
        ColorStateList colorList = new ColorStateList(states, colors);
        return colorList;
    }

    /**
     * 让我们的自定义控件和viewpager相关联
     */
    public void setupWithViewPager(@Nullable ViewPager viewPager) {

        this.viewPager = viewPager;
        PagerAdapter pagerAdapter = viewPager.getAdapter();
        rg_indicator.removeAllViews();
        for (int position = 0; position < pagerAdapter.getCount(); position++) {
            RadioButton rb = new RadioButton(mContext);
            rb.setText(pagerAdapter.getPageTitle(position));
            rb.setId(position);

            int content = tabWidthInit == 0 ? RadioGroup.LayoutParams.WRAP_CONTENT : tabWidthInit;//判断是否有设置，没有就是wrap_content
            RadioGroup.LayoutParams layoutParam = new RadioGroup.LayoutParams(
                    new RadioGroup.LayoutParams(content, RadioGroup.LayoutParams.MATCH_PARENT));
            rb.setLayoutParams(layoutParam);
            rb.setTextSize(tabTextSize);
            rb.setTextColor(colorStateList);
            rb.setGravity(Gravity.CENTER_VERTICAL);
            rb.setBackgroundColor(Color.TRANSPARENT);
            rb.setButtonDrawable(android.R.color.transparent);
            //设置padding
            rb.setPadding(tabPadding, 0, tabPadding, 0);
            rb.setTag(tabPadding);
            if (position == 0) {//默认选中第一个
                rb.setChecked(true);
            }
            rg_indicator.addView(rb);
        }

        Resources resources = mContext.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        int width = dm.widthPixels;//获取屏幕宽度

        if (relativeLayout_indicator.getWidth() < width) {//让宽度没有占满屏幕居中
            FrameLayout.LayoutParams layoutParam2 = new FrameLayout.LayoutParams(width, LayoutParams.MATCH_PARENT);
            relativeLayout_indicator.setLayoutParams(layoutParam2);
        }

        getAlbum(0);//滑块默认是第一个RadioButton的宽度
//        setCurrentSelectItem(0);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                setCurrentSelectItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                int s = 0;
                LayoutParams layoutParams = (LayoutParams) iv_indicator.getLayoutParams();
                for (int i = 0; i < arg0; i++) {
                    s += rg_indicator.getChildAt(i).getWidth();
                }
                int width = rg_indicator.getChildAt(arg0).getWidth();

                if (arg1 == 0f) { // 停止滚动
                    layoutParams.setMargins(s + tabPadding, 0, 0, tabIndicatorMarginBottom);
                } else {
                    layoutParams.setMargins((int) (s + (width * arg1)) + tabPadding, 0, 0, tabIndicatorMarginBottom);
                }
                iv_indicator.setLayoutParams(layoutParams);
            }

            @Override
            public void onPageScrollStateChanged(int position) {
            }
        });

    }


    /**
     *  根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */

    public static int dip2px(Context context, float dpValue) {

        final float scale = context.getResources().getDisplayMetrics().density;

        return (int) (dpValue * scale + 0.5f);
    }


    /**
     * 根据RadioButton  动态计算滑块的宽度
     *
     * @param position
     */
    public void getAlbum(int position) {
        RadioButton rb = (RadioButton) rg_indicator.getChildAt(position);
        if (rb == null) {
            return;
        }
        //获取字的宽度
        CharSequence text = rb.getText();
        TextPaint paint = rb.getPaint();
        int tabWidth2 = (int) (paint.measureText((String) text) + 0.5);
        tabWidth = tabWidth2 + 2 * tabPadding;

        LayoutParams indicator_LayoutParams = (LayoutParams) iv_indicator.getLayoutParams();
        indicator_LayoutParams.height = tabIndicatorHeight;//设置滑块的高度
        indicator_LayoutParams.width = tabIndicatorWidth == 0 ? tabWidth2 : tabIndicatorWidth;//设置滑块的宽度(不传就和上边tab一样长)
        indicator_LayoutParams.setMargins(tabPadding, 0, 0, tabIndicatorMarginBottom);//设置滑块的Margins 为了和RadioButton对齐
        iv_indicator.setLayoutParams(indicator_LayoutParams);
        iv_indicator.setBackgroundColor(tabIndicatorColor);//绘制滑块的颜色
        postInvalidate();//重新绘制界面

    }

    /**
     * 设置当前选中条目
     *
     * @param currentPosition
     */
    private void setCurrentSelectItem(int currentPosition) {
        getAlbum(currentPosition);//每次滑动就要算滑块的宽度

        RadioButton radioButton = ((RadioButton) rg_indicator
                .getChildAt(currentPosition));
        if (radioButton != null)
            radioButton.performClick();
    }

    /**
     * 设置当前选中条目
     *
     * @param currentPosition
     */
    boolean isSet = false;

    public void setCurrent(int currentPosition) {
        getAlbum(currentPosition);//每次滑动就要算滑块的宽度

        final RadioButton radioButton = ((RadioButton) rg_indicator
                .getChildAt(currentPosition));
        if (radioButton != null) {
            //view加载完成时回调
            view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (!isSet) {
                        radioButton.performClick();
                    }
                    isSet = true;
                }
            });
        }
    }

    /**
     * 获取当前选中条目的position
     *
     * @return
     */
    public int getCurrentSelectPosition() {
        int currentIdPosition = rg_indicator.getCheckedRadioButtonId();
        return currentIdPosition;
    }


}