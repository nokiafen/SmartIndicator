package heli.mrc.smartIndicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class SmartIndicator extends LinearLayout implements  OnTouchListener{

    private Integer mAccentColor;
    private Integer mFadeAccentColor;
    private Integer mTextLightColor;
    private Integer mTextDarkColor;
    private int touchViewPosition;
    private int pickedViewPosition;
    private float segmentWidth;
    private float indicatorLineHeight;
    Paint paint;
    private Color accentColor;
    private Color fadeAccentColor;
    private Color lightAccentColor;
    private Color darkAccentColor;
    private boolean onDowning;
    private Rect backRect;
    private Rect indicatorRect;
    public SmartIndicator(Context context) {
        super(context);
    }

    public SmartIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SmartIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private void init(Context context,  AttributeSet attrs) {
        DisplayMetrics dm = new DisplayMetrics();
        context.getDisplay().getMetrics(dm);
        indicatorLineHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,3,dm);
//
//
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.smart_indicator);
        mAccentColor= typedArray.getColor(R.styleable.smart_indicator_accent_Color,0xffff4081);
        mTextLightColor= typedArray.getColor(R.styleable.smart_indicator_textLightColor,0xff212121);
        indicatorLineHeight= typedArray.getDimension(R.styleable.smart_indicator_indicatorHeight,10);



        if (mAccentColor == null) {
            mAccentColor = 0xffff4081;
        }

        if (mTextLightColor == null) {
            mTextLightColor = 0xff212121;
        }

        mFadeAccentColor =mAccentColor & 0x30FFFFFF;
        mTextDarkColor =mTextLightColor & 0xAAFFFFFF;

        accentColor = Color.valueOf(mAccentColor) ;
        fadeAccentColor = Color.valueOf(mFadeAccentColor);
        lightAccentColor =Color.valueOf(mTextLightColor);
        darkAccentColor =Color.valueOf(mTextDarkColor);

        paint =new Paint();
        setOrientation(HORIZONTAL);
        setOnTouchListener(this);
        if (getBackground()==null) {
            setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent touchEvent) {
        float eventX= touchEvent.getX();
        int action =touchEvent.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                touchViewPosition = getCurrentTouchPosition(eventX);
                onDowning=true;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                pickedViewPosition = getCurrentTouchPosition(eventX);
                onDowning=false;
                invalidate();
                updateChildViewState();
                if (componentClickListener!=null) {
                    componentClickListener.onComponentClicked(this, pickedViewPosition);
                }
                break;
            case MotionEvent.ACTION_CANCEL: //when intercepted
                break;

        }

        return true;
    }


    private int getCurrentTouchPosition(float touchX){
        int childCount = getChildCount();
        segmentWidth = getWidth()/childCount;
        return Float.valueOf(touchX/segmentWidth).intValue();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (segmentWidth==0) {
            segmentWidth = getWidth()/getChildCount();
        }
        //draw bottom line
        float lineStartX = pickedViewPosition *segmentWidth;
        if(indicatorRect==null){
            indicatorRect=new Rect();
        }
        indicatorRect.set((int)lineStartX,(int)(getHeight()-indicatorLineHeight),(int)(lineStartX+segmentWidth),getHeight());
        paint.setColor(mAccentColor);
        canvas.drawRect(indicatorRect,paint);
        /* ----------------------------------- */

        //draw downBackGround
        float backGroundStartX = touchViewPosition*segmentWidth;
        if(backRect==null){
            backRect=new Rect();
        }
        backRect.set((int)backGroundStartX,0,(int)(backGroundStartX+segmentWidth),getHeight());
        paint.setColor(mFadeAccentColor);
        if (onDowning) {
            canvas.drawRect(backRect,paint);
        }

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        segmentWidth = getWidth()/childCount;
        for (int i = 0; i <getChildCount() ; i++) {
            View childView = getChildAt(i);
            if(childView instanceof TextView){ //init text color;
                updateTextState(((TextView)childView),i==pickedViewPosition?lightAccentColor:darkAccentColor);
            }

            LayoutParams layoutConfig = (LayoutParams) getChildAt(i).getLayoutParams();
            layoutConfig.weight=1;
            layoutConfig.width=0;
            childView.setLayoutParams(layoutConfig);
        }
        setGravity(Gravity.CENTER_VERTICAL);
    }



    public interface  OnComponentClickListener{
        void onComponentClicked(ViewGroup parent, int index);
    }

    public void setComponentClickListener(OnComponentClickListener componentClickListener) {
        this.componentClickListener = componentClickListener;
    }

    private OnComponentClickListener componentClickListener;


    private void updateChildViewState() {
        for (int i = 0; i <getChildCount() ; i++) {
            View childView = getChildAt(i);
            if(childView instanceof TextView){
                updateTextState(((TextView)childView),i==pickedViewPosition?lightAccentColor:darkAccentColor);
            }
        }
    }

    private void updateTextState(TextView text,Color targetColor){
        text.setTextColor(targetColor.toArgb());
    }



    public void setAccentColor(Integer mAccentColor) {
        this.mAccentColor = mAccentColor;
        mFadeAccentColor =mAccentColor & 0x30FFFFFF;
        accentColor =Color.valueOf(mAccentColor);
        fadeAccentColor = Color.valueOf(mFadeAccentColor);
        invalidate();
    }

    public void setTextLightColor(Integer mTextLightColor) {
        this.mTextLightColor = mTextLightColor;
        mTextDarkColor =mTextLightColor & 0xAAFFFFFF;
        lightAccentColor = Color.valueOf(mTextLightColor);
        darkAccentColor =  Color.valueOf(mTextDarkColor);
        invalidate();
    }

    public void  setSelection(int index) {
        if ((index<0||index>=getChildCount())) {
             throw  new RuntimeException("index out of range, check your index");
        }
        pickedViewPosition = index;
        invalidate();
    }
}
