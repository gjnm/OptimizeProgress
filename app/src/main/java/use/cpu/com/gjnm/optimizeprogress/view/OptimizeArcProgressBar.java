package use.cpu.com.gjnm.optimizeprogress.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import use.cpu.com.gjnm.optimizeprogress.R;


public class OptimizeArcProgressBar extends View {
    public static final int LEFT_PROGRESS = 0;
    public static final int RIGHT_PROGRESS = 1;
    private final int DEGREE_PROGRESS_DISTANCE = dipToPx(10);
    private final int MAX_VALUES = 100;
    private final int ANI_SPEED = 1000;

    private Paint inBackArcPaint;
    private Paint inProgressPaint;
    private Paint outBackArcPaint;
    private Paint outProgressPaint;
    private Paint outStandTextPaint;
    private Paint outValuesTextPaint;
    private Paint curValuesTextPaint;
    private RectF inBgRect;
    private RectF outBgRect;


    // 绘制圆圈的颜色  ［1］内圆背景［2］内圆绘制层［3］外圆背景［4］外圆绘制层
    private int[] colors = new int[]{Color.GRAY, Color.BLACK, Color.GRAY, Color.BLACK};
    private int diameter;  // 直径
    private float centerX;  //圆心X坐标
    private float centerY;  //圆心Y坐标
    private float leftStandX;  //左侧底部文案X坐标
    private float leftStandY;  //左侧底部文案Y坐标
    private float rightStandX;  //右侧底部文案X坐标
    private float rightStandY;  //右侧底部文案Y坐标
    private float leftOccupyX;  //左侧占比文案X坐标
    private float leftOccupyY;  //左侧占比文案Y坐标
    private float rightOccupyX;  //右侧占比文案X坐标
    private float rightOccupyY;  //右侧占比文案Y坐标

    private float startAngle = 135;
    private float sweepAngle = 270;
    private float currentAngle = 0;
    private float rightCurrentAngle = 0;
    private float leftCurrentAngle = 0;
    private float leftStartAngle = 150;
    private float rightStartAngle = -40;
    private float outEndAngle = 70;
    private int leftCurValues;
    private int rightCurValues;
    private float progressWidth;
    private float outProArcWidth;
    private float curValues;
    private float curValuesTextSize;
    private float inCircleScale;
    private float outCircleScale;
    private float inLastAngle;

    private String leftTitleString;
    private String rightTitleString;

    private PaintFlagsDrawFilter mDrawFilter;
    private ValueAnimator progressAnimator;


    public OptimizeArcProgressBar(Context context) {
        super(context, null);
        initView();
    }

    public OptimizeArcProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        initCofig(context, attrs);
        initView();
    }

    public OptimizeArcProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCofig(context, attrs);
        initView();
    }

    /**
     * 初始化布局配置
     *
     * @param context
     * @param attrs
     */
    private void initCofig(Context context, AttributeSet attrs) {
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.OptimizeArcProgressBar);
        int color1 = arr.getColor(R.styleable.OptimizeArcProgressBar_in_back_color, colors[0]);
        int color2 = arr.getColor(R.styleable.OptimizeArcProgressBar_in_front_color, colors[1]);
        int color3 = arr.getColor(R.styleable.OptimizeArcProgressBar_out_back_color, colors[2]);
        int color4 = arr.getColor(R.styleable.OptimizeArcProgressBar_out_front_color, colors[3]);
        colors = new int[]{color1, color2, color3, color4};

        progressWidth = arr.getDimension(R.styleable.OptimizeArcProgressBar_in_front_width, dipToPx(15));
        outProArcWidth = arr.getDimension(R.styleable.OptimizeArcProgressBar_out_back_width, dipToPx(2));
        curValues = arr.getFloat(R.styleable.OptimizeArcProgressBar_current_value, 0);
        curValuesTextSize = arr.getFloat(R.styleable.OptimizeArcProgressBar_current_text_size, dipToPx(60));
        leftTitleString = arr.getString(R.styleable.OptimizeArcProgressBar_left_title_text);
        rightTitleString = arr.getString(R.styleable.OptimizeArcProgressBar_right_title_text);
        setCurrentValues(curValues);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int width = (int) (progressWidth + diameter + 2 * DEGREE_PROGRESS_DISTANCE);
//        int height = (int) (progressWidth + diameter + 2 * DEGREE_PROGRESS_DISTANCE);
        int width = getScreenWidth();
        int height = getScreenWidth();
        setMeasuredDimension(width, height);
    }


    private void initView() {

        diameter = 3 * getScreenWidth() / 5;
        //圆心
        centerX = getScreenWidth() / 2;
        centerY = getScreenWidth() / 2;
        inCircleScale = sweepAngle / MAX_VALUES;
        outCircleScale = outEndAngle / MAX_VALUES;
        leftCurValues = 0;
        rightCurValues = 0;
        // 内部粗弧形的矩阵区域
        inBgRect = new RectF();
        inBgRect.top = centerX - diameter / 2 + progressWidth / 2;
        inBgRect.left = centerX - diameter / 2 + progressWidth / 2;
        inBgRect.right = centerX - progressWidth / 2 + diameter / 2;
        inBgRect.bottom = centerX - progressWidth / 2 + diameter / 2;


        // 内部整个弧形 背景
        inBackArcPaint = new Paint();
        inBackArcPaint.setAntiAlias(true);
        inBackArcPaint.setStyle(Paint.Style.STROKE);
        inBackArcPaint.setStrokeWidth(progressWidth);
        inBackArcPaint.setColor(colors[0]);
        inBackArcPaint.setStrokeCap(Paint.Cap.ROUND);

        // 内部圆环当前进度的弧形
        inProgressPaint = new Paint();
        inProgressPaint.setAntiAlias(true);
        inProgressPaint.setStyle(Paint.Style.STROKE);
        inProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        inProgressPaint.setStrokeWidth(progressWidth);
        inProgressPaint.setColor(colors[1]);

        //内容显示文字
        curValuesTextPaint = new Paint();
        curValuesTextPaint.setTextSize(curValuesTextSize);
        curValuesTextPaint.setColor(Color.BLACK);
        curValuesTextPaint.setTextAlign(Paint.Align.CENTER);

        outStandTextPaint = new Paint();
        outStandTextPaint.setTextSize(dipToPx(14));
        outStandTextPaint.setColor(Color.BLACK);
        outStandTextPaint.setTextAlign(Paint.Align.RIGHT);

        outValuesTextPaint = new Paint();
        outValuesTextPaint.setTextSize(dipToPx(12));
        outValuesTextPaint.setColor(Color.BLACK);
        outValuesTextPaint.setTextAlign(Paint.Align.RIGHT);

        // 外侧弧形的矩阵区域
        outBgRect = new RectF();
        outBgRect.top = centerX - diameter / 2 - DEGREE_PROGRESS_DISTANCE + outProArcWidth / 2;
        outBgRect.left = centerX - diameter / 2 - DEGREE_PROGRESS_DISTANCE + outProArcWidth / 2;
        outBgRect.right = centerX - outProArcWidth / 2 + diameter / 2 + DEGREE_PROGRESS_DISTANCE;
        outBgRect.bottom = centerX - outProArcWidth / 2 + diameter / 2 + DEGREE_PROGRESS_DISTANCE;

        // 外部的圆环 背景
        outBackArcPaint = new Paint();
        outBackArcPaint.setAntiAlias(true);
        outBackArcPaint.setStyle(Paint.Style.STROKE);
        outBackArcPaint.setStrokeCap(Paint.Cap.ROUND);
        outBackArcPaint.setColor(colors[2]);

        // 外部的圆环 进度
        outProgressPaint = new Paint();
        outProgressPaint.setAntiAlias(true);
        outProgressPaint.setStyle(Paint.Style.STROKE);
        outProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        outProgressPaint.setStrokeWidth(outProArcWidth);
        outProgressPaint.setColor(colors[3]);

        // 文字坐标
        float radian = (float) Math.abs(Math.PI * (180 - startAngle) / 180);
        int y = (int) Math.abs((Math.sin(radian) * diameter / 2));
        int x = (int) Math.abs(Math.sqrt(Math.pow(diameter / 2, 2) - Math.pow(y, 2)));
        leftStandX = centerX - x;
        leftStandY = centerY + y;

        rightStandX = centerX + x;
        rightStandY = centerY + y;
        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //抗锯齿
        canvas.setDrawFilter(mDrawFilter);

        //整个内弧
        canvas.drawArc(inBgRect, startAngle, sweepAngle, false, inBackArcPaint);
        //当前进度
        canvas.drawArc(inBgRect, startAngle, currentAngle, false, inProgressPaint);

        //右侧外弧
        canvas.drawArc(outBgRect, rightStartAngle, outEndAngle, false, outBackArcPaint);
        //右侧进度
        canvas.drawArc(outBgRect, rightStartAngle + outEndAngle, -rightCurrentAngle, false, outProgressPaint);

        //左侧内弧
        canvas.drawArc(outBgRect, leftStartAngle, outEndAngle, false, outBackArcPaint);
        // 不知道为什么，为0时，画了一个整圆？
        if(leftCurrentAngle == 0){
            leftCurrentAngle = 0.01f;
        }
        //左侧进度
        canvas.drawArc(outBgRect, leftStartAngle, leftCurrentAngle, false, outProgressPaint);

        // 中心数字
        canvas.drawText(String.format("%.0f", curValues), centerX, centerY + curValuesTextSize / 3, curValuesTextPaint);

        // 左方文案
        outStandTextPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(leftTitleString, leftStandX - progressWidth / 2 - 5, leftStandY, outStandTextPaint);
        if(leftCurValues > 5) {
            outValuesTextPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(leftCurValues + "%", leftOccupyX - 5, leftOccupyY, outValuesTextPaint);
        }

        // 右方文案
        outStandTextPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(rightTitleString, rightStandX + progressWidth / 2 + 5, rightStandY, outStandTextPaint);
        if(rightCurValues > 5) {
            outValuesTextPaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(rightCurValues + "%", rightOccupyX + 5, rightOccupyY, outValuesTextPaint);
        }

        invalidate();
    }

    /**
     * 设置内侧大圆当前值
     *
     * @param currentValues
     */
    public void setCurrentValues(float currentValues) {
        if (currentValues > MAX_VALUES) {
            currentValues = MAX_VALUES;
        }
        if (currentValues < 0) {
            currentValues = 0;
        }
        this.curValues = currentValues;
        inLastAngle = currentAngle;
        setAnimation(inLastAngle, currentValues * inCircleScale, ANI_SPEED);
    }

    /**
     * 为内侧大圆进度设置动画
     *
     * @param last
     * @param current
     */
    private void setAnimation(float last, float current, int length) {
        progressAnimator = ValueAnimator.ofFloat(last, current);
        progressAnimator.setDuration(length);
        progressAnimator.setTarget(currentAngle);
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentAngle = (float) animation.getAnimatedValue();
                curValues = currentAngle / inCircleScale;
            }
        });
        progressAnimator.start();
    }

    /**
     * 设置外侧半圆当前值
     *
     * @param values
     */
    public void setOutCurrentValues(float values, final int type) {
        if (values > MAX_VALUES) {
            values = MAX_VALUES;
        }
        if (values < 0) {
            values = 1;
        }

        float angleValues = values * outCircleScale;

        ValueAnimator outProAnimator;
        if (type == RIGHT_PROGRESS) {
            outProAnimator = ValueAnimator.ofFloat(rightCurrentAngle, angleValues);
            outProAnimator.setTarget(rightCurrentAngle);

        } else {
            outProAnimator = ValueAnimator.ofFloat(leftCurrentAngle, angleValues);
            outProAnimator.setTarget(leftCurrentAngle);
        }

        outProAnimator.setDuration(ANI_SPEED);
        outProAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (type == RIGHT_PROGRESS) {
                    rightCurrentAngle = (float) animation.getAnimatedValue();
                    occupyLocation(type, rightCurrentAngle / outCircleScale);
                } else {
                    leftCurrentAngle = (float) animation.getAnimatedValue();
                    occupyLocation(type, leftCurrentAngle / outCircleScale);
                }
            }
        });
        outProAnimator.start();
    }

    public void occupyLocation(int type, float values) {
        float coord[] = ratioToCoord(type, values);
        switch (type) {
            case LEFT_PROGRESS:
                leftCurValues = (int)values;
                leftOccupyX = centerX - coord[0];
                leftOccupyY = centerY + coord[1];
                break;
            case RIGHT_PROGRESS:
                rightCurValues = (int)values;
                rightOccupyX = centerX + coord[0];
                rightOccupyY = centerY + coord[1];
                break;
        }
    }

    /**
     * 外圆中，根据占比计算坐标
     *
     * @param type
     * @param ratio
     * @return
     */
    public float[] ratioToCoord(int type, float ratio) {
        float coord[] = new float[2];
        float angle = 0;
        float radian;
        boolean opposite = false;
        switch (type) {
            case LEFT_PROGRESS:
                angle = ratio * outCircleScale + leftStartAngle;
                if (angle > 180) {
                    opposite = true;
                }
                break;
            case RIGHT_PROGRESS:
                angle = rightStartAngle + outEndAngle - ratio * outCircleScale;
                if (angle < 0) {
                    opposite = true;
                }
                break;

            default:
                break;
        }
        radian = (float) Math.abs(Math.PI * (180 - angle) / 180);
        coord[1] = (int) Math.abs((Math.sin(radian) * diameter / 2));
        coord[0] = (int) Math.abs(Math.sqrt(Math.pow(diameter / 2 + DEGREE_PROGRESS_DISTANCE, 2) - Math.pow(coord[1], 2)));

        if(opposite){
            coord[1] = -coord[1];
        }

        return coord;
    }

    /**
     * dip 转换成px
     *
     * @param dip
     * @return
     */

    private int dipToPx(float dip) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f * (dip >= 0 ? 1 : -1));
    }

    /**
     * 得到屏幕宽度
     *
     * @return
     */
    private int getScreenWidth() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }
}
