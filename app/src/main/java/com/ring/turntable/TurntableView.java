package com.ring.turntable;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TurntableView extends View {

    private int[] colors = {0xFFFF4500, 0xFFFFF0F5, 0xFFFFE4E1, 0xFFFF6347, 0xFFFFD700, 0xFFFFB6C1,
            0xFFFF00FF, 0xFFAFEEEE, 0xFFADD8E6, 0xFFE9967A, 0xFFFFC0CB, 0xFFFFA500, 0xFFFFEBCD,
            0xFFF08080, 0xFF6A5ACD, 0xFFC71585, 0xFFDAA520, 0xFFFF0000, 0xFFFA8072, 0xFFE0FFFF};

    private Context mContext;
    private List<String> tagList = new ArrayList<>();//标签数组
    private Paint pointPaint;//画圆心和指针的
    private Paint centreText;//中心文字
    private Paint circlePaint;//画圆的
    private Paint bgPaint;//外边好看的

    private int mCurrentRotateAngle = 0;
    private boolean isReset = true;

    public TurntableView(Context context) {
        this(context, null);
    }

    public TurntableView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TurntableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        pointPaint = new Paint();
        centreText = new Paint();
        circlePaint = new Paint();
        bgPaint = new Paint();
        pointPaint.setStyle(Paint.Style.FILL);
        centreText.setStyle(Paint.Style.FILL);
        centreText.setTextAlign(Paint.Align.CENTER);
        centreText.setTextSize(30);
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setStrokeWidth(40);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(165);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int centerWidth = getWidth() / 2;
        int centerHeight = getHeight() / 2;
        if (tagList.size() <= 0) {
            //没有标签的时候，画个圆，并且，不能开始
            circlePaint.setColor(ContextCompat.getColor(mContext, R.color.darkRed));
            canvas.drawCircle(centerWidth, centerHeight, 138, circlePaint);
        } else {
            //有标签了，按标签数量等分圆，设置不同的颜色
            for (int i = 0; i < tagList.size(); i++) {
                int startAngle = i * 360 / tagList.size() - 90 - mCurrentRotateAngle;
                int sweepAngle = 360 / tagList.size();
                circlePaint.setColor(colors[i % colors.length]);
                canvas.drawArc(centerWidth - 138, centerHeight - 138, centerWidth + 138, centerHeight + 138, startAngle, sweepAngle, false, circlePaint);

                centreText.setColor(ContextCompat.getColor(mContext, R.color.text1));
                float textAngle = startAngle + sweepAngle / 2; //计算文字位置角度
                float x = centerWidth + (float) (138 * Math.cos(textAngle * Math.PI / 180)); //计算文字位置坐标
                float y = centerHeight + (float) (138 * Math.sin(textAngle * Math.PI / 180));
                canvas.drawText(tagList.get(i), x, y, centreText); //绘制文字
            }
        }
        //画个指针和圆心
        pointPaint.setColor(ContextCompat.getColor(mContext, R.color.darkYellow));
        Path path1 = new Path();
        path1.moveTo(centerWidth - 25, centerHeight - 40);
        path1.lineTo(centerWidth, centerHeight - 95);
        path1.lineTo(centerWidth + 25, centerHeight - 40);
        canvas.drawPath(path1, pointPaint);
        canvas.drawCircle(centerWidth, centerHeight, 55, pointPaint);

        pointPaint.setColor(ContextCompat.getColor(mContext, R.color.lightYellow));
        Path path = new Path();
        path.moveTo(centerWidth - 15, centerHeight - 30);
        path.lineTo(centerWidth, centerHeight - 80);
        path.lineTo(centerWidth + 15, centerHeight - 30);
        canvas.drawPath(path, pointPaint);
        canvas.drawCircle(centerWidth, centerHeight, 40, pointPaint);

        centreText.setColor(ContextCompat.getColor(mContext, R.color.text1));
        RectF rectF = new RectF(centerWidth - 40, centerHeight - 40, centerWidth + 40, centerHeight + 40);
        Paint.FontMetrics fontMetrics = centreText.getFontMetrics();
        float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
        float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom
        int baseLineY = (int) (rectF.centerY() - top / 2 - bottom / 2);//基线中间点的y轴计算公式
        canvas.drawText("开始", rectF.centerX(), baseLineY, centreText);

        bgPaint.setColor(ContextCompat.getColor(mContext, R.color.red));
        canvas.drawCircle(centerWidth, centerHeight, 240, bgPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                float currentX = event.getX();
                float currentY = event.getY();
                if (currentX >= getWidth() / 2 - 60 && currentX <= getWidth() / 2 + 60 &&
                        currentY >= getHeight() / 2 - 60 && currentY <= getHeight() / 2 + 60) {
                    startTurn();
                }
                break;
        }
        return true;
    }

    //添加标签
    public void addTag(String tag) {
        if (!isReset) {
            Toast.makeText(mContext, "如果想要重新开始，点击重置，如果想要继续添加，选择不重置", Toast.LENGTH_SHORT).show();
            return;
        }
        tagList.add(tag);
        invalidate();
    }

    //移除标签
    public void removeTag(int index) {
        if (tagList.size() <= index) return;
        tagList.remove(index);
        invalidate();
    }

    //重置标签
    public void resetTagList() {
        tagList = new ArrayList<>();
        mCurrentRotateAngle = 0;
        invalidate();
    }

    //不重置，重新开始
    public void continueLast() {
        isReset = true;
    }

    public void startTurn() {
        if (!isReset) {
            Toast.makeText(mContext, "要先重置，或者选择不重置，继续游戏哦~", Toast.LENGTH_SHORT).show();
            return;
        }
        if (tagList.size() <= 0) {
            Toast.makeText(mContext, "要先添加标签哦~", Toast.LENGTH_SHORT).show();
            return;
        }
        isReset = false;
        //开始转动转盘
        int angle = new Random().nextInt(360);
        ValueAnimator mValueAnimator;
        if (angle < 120) {
            mValueAnimator = ValueAnimator.ofInt(0, 120, 240, 360, 480, 600, 720, 840, 960, 1080, angle + 1080);
        } else if (angle < 240) {
            mValueAnimator = ValueAnimator.ofInt(0, 120, 240, 360, 480, 600, 720, 840, 960, 1080, 1200, angle + 1080);
        } else {
            mValueAnimator = ValueAnimator.ofInt(0, 120, 240, 360, 480, 600, 720, 840, 960, 1080, 1200, 1320, angle + 1080);
        }
        mValueAnimator.setRepeatCount(0);
        mValueAnimator.setDuration(2000);
        mValueAnimator.setInterpolator(new LinearInterpolator());

        mValueAnimator.addUpdateListener(animation -> {
            mCurrentRotateAngle = (int) animation.getAnimatedValue();
            invalidate();
        });

        mValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                int index = angle / (360 / tagList.size());
                Toast.makeText(mContext, "选中了" + tagList.get(index), Toast.LENGTH_SHORT).show();
            }
        });

        mValueAnimator.start();
    }
}
