package com.ln.himaxtouch.CustomView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.view.View;

import com.ln.himaxtouch.ObjectiveTest.PatternModel;
import com.ln.himaxtouch.R;


/**
 * Created by 903622 on 2018/4/13.
 */

public class ObjectivePatternView extends View {

    private Paint mPaint;
    private TextPaint mPaintText;
    private int mRow;
    private int mCol;
    public Cell[][] mPatternCells;
    private int mLayoutWidth;
    private int mLayoutLength;

    public final static int PATTERN_SIGNAL_MEASUREMENT = 0;
    public final static int PATTERN_CUSTOMER_ONE = 1;
    public final static int PATTERN_CUSTOMER_TWO = 2;
    public final static int PATTERN_CUSTOMER_THREE = 3;
    public final static int PATTERN_CUSTOMER_FOUR = 4;
    public final static int PATTERN_CUSTOMER_FIVE = 5;
    public final static int PATTERN_CUSTOMER_SIX = 6;
    public final static int PATTERN_JITTER = 7;
    public final static int PATTERN_MAX_POINT_COUNT = 8;
    public final static int PATTERN_GHOST_POINT_RECORD = 9;
    public final static int PATTERN_PALM_TEST = 10;
    public final static int PATTERN_BOARD_PROTECTION = 11;
    public final static int PATTERN_EXTEND_BOARD = 12;

    private int mPatternType;

    public float mBoardSpace = 0;

    public PatternModel mModel;


    public static class Cell {
        public boolean isShouldBeTouched;
        public boolean isTouched;
        public float x_start;
        public float y_start;
        public float cell_width;
        public int grounp = 0;

        public Cell(float x, float y, float width) {
            x_start = x;
            y_start = y;
            cell_width = width;
            isTouched = false;
            isShouldBeTouched = false;
        }
    }

    public ObjectivePatternView(Context context, int row, int col, int layout_width, int layout_length, int pattern_type) {
        super(context);

        if ((layout_width > layout_length && col > row) || (layout_length > layout_width && row > col)) {
            mRow = col;
            mCol = row;
        } else {
            mRow = row;
            mCol = col;
        }

        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#99000000"));
        this.setBackgroundColor(Color.parseColor("#00000000"));
        this.setTranslationX(0);
        this.setTranslationY(0);
        mPatternType = pattern_type;

        mPaintText = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
        mPaintText.setColor(Color.WHITE);
        mPaintText.setTextSize(60);
        mPaintText.setTextAlign(Paint.Align.CENTER);

        mLayoutWidth = layout_width;
        mLayoutLength = layout_length;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = mLayoutWidth;
        int length = mLayoutLength;

        switch (mPatternType) {
            case PATTERN_SIGNAL_MEASUREMENT: {

                float width_step = (float) width / (float) (mRow);
                float length_step = (float) length / (float) (mCol);
                for (int i = 1; i < mRow; i++) {
                    float x = width_step * (float) i;
                    canvas.drawLine(x, 0, x, length, mPaint);
                }
                for (int j = 1; j < mCol; j++) {
                    float y = length_step * (float) j;
                    canvas.drawLine(0, y, width, y, mPaint);
                }
                break;
            }
            case PATTERN_CUSTOMER_ONE: {
                Paint tt = new Paint();
                tt.setColor(Color.RED);
                for (int i = 0; i < mPatternCells.length; i++) {
                    for (int j = 0; j < mPatternCells[0].length; j++) {
                        if (mPatternCells[i][j].isShouldBeTouched) {
                            mPaint.setColor(Color.parseColor("#44000000"));
                            mPaint.setStyle(Paint.Style.FILL);
                            ObjectivePatternView.Cell c = mPatternCells[i][j];
                            canvas.drawRect(c.x_start, c.y_start, c.x_start + c.cell_width, c.y_start + c.cell_width, mPaint);
//                            canvas.drawLine(c.x_start, c.y_start, c.x_start+c.cell_width, c.y_start, mPaint);
//                            canvas.drawLine(c.x_start, c.y_start+c.cell_width, c.x_start+c.cell_width, c.y_start+c.cell_width, mPaint);
//                            canvas.drawLine(c.x_start+c.cell_width, c.y_start, c.x_start+c.cell_width, c.y_start+c.cell_width, mPaint);
//                            canvas.drawLine(c.x_start, c.y_start, c.x_start, c.y_start+c.cell_width, mPaint);
                        } else {
                            ObjectivePatternView.Cell c = mPatternCells[i][j];
                            canvas.drawRect(c.x_start, c.y_start, c.x_start + c.cell_width, c.y_start + c.cell_width, tt);
                        }
                    }
                }
                break;
            }
            case PATTERN_CUSTOMER_TWO: {
                for (int i = 0; i < mPatternCells.length; i++) {
                    for (int j = 0; j < mPatternCells[0].length; j++) {
                        if (mPatternCells[i][j].isShouldBeTouched) {
                            mPaint.setColor(Color.parseColor("#44000000"));
                            mPaint.setStyle(Paint.Style.FILL);
                            ObjectivePatternView.Cell c = mPatternCells[i][j];
                            canvas.drawRect(c.x_start, c.y_start, c.x_start + c.cell_width, c.y_start + c.cell_width, mPaint);
                        }
                    }
                }
                break;
            }
            case PATTERN_CUSTOMER_THREE: {
                for (int i = 0; i < mPatternCells.length; i++) {
                    for (int j = 0; j < mPatternCells[0].length; j++) {
                        if (mPatternCells[i][j].isShouldBeTouched) {
                            mPaint.setColor(Color.parseColor("#44000000"));
                            mPaint.setStyle(Paint.Style.FILL);
                            ObjectivePatternView.Cell c = mPatternCells[i][j];
                            canvas.drawRect(c.x_start, c.y_start, c.x_start + c.cell_width, c.y_start + c.cell_width, mPaint);
                        }
                    }
                }
                break;
            }
            case PATTERN_CUSTOMER_FOUR: {
                for (int i = 0; i < mPatternCells.length; i++) {
                    for (int j = 0; j < mPatternCells[0].length; j++) {
                        if (mPatternCells[i][j].isShouldBeTouched) {
                            mPaint.setColor(Color.parseColor("#44000000"));
                            mPaint.setStyle(Paint.Style.FILL);
                            ObjectivePatternView.Cell c = mPatternCells[i][j];
                            canvas.drawRect(c.x_start, c.y_start, c.x_start + c.cell_width, c.y_start + c.cell_width, mPaint);
                        }
                    }
                }
                break;
            }
            case PATTERN_CUSTOMER_FIVE: {
                for (int i = 0; i < mPatternCells.length; i++) {
                    for (int j = 0; j < mPatternCells[0].length; j++) {
                        if (mPatternCells[i][j].isShouldBeTouched) {
                            mPaint.setColor(Color.parseColor("#44000000"));
                            mPaint.setStyle(Paint.Style.FILL);
                            ObjectivePatternView.Cell c = mPatternCells[i][j];
                            canvas.drawRect(c.x_start, c.y_start, c.x_start + c.cell_width, c.y_start + c.cell_width, mPaint);
                        }
                    }
                }
                break;
            }
            case PATTERN_CUSTOMER_SIX: {
                for (int i = 0; i < mPatternCells.length; i++) {
                    for (int j = 0; j < mPatternCells[0].length; j++) {
                        if (mPatternCells[i][j].isShouldBeTouched) {
                            mPaint.setColor(Color.parseColor("#44000000"));
                            mPaint.setStyle(Paint.Style.FILL);
                            ObjectivePatternView.Cell c = mPatternCells[i][j];
                            canvas.drawRect(c.x_start, c.y_start, c.x_start + c.cell_width, c.y_start + c.cell_width, mPaint);
                        }
                    }
                }
                break;
            }
            case PATTERN_JITTER: {
                drawCenter(canvas, mPaintText, getResources().getString(R.string.objective_jitter_test));
                break;
            }
            case PATTERN_MAX_POINT_COUNT: {
                drawCenter(canvas, mPaintText, getResources().getString(R.string.objective_record_max_point_count));
                break;
            }
            case PATTERN_GHOST_POINT_RECORD: {
                drawCenter(canvas, mPaintText, getResources().getString(R.string.objective_ghost_point_record));
                break;
            }
            case PATTERN_PALM_TEST: {
                drawCenter(canvas, mPaintText, getResources().getString(R.string.objective_palm_test));
                break;
            }
            case PATTERN_BOARD_PROTECTION: {
                mPaint.setColor(Color.parseColor("#44000000"));
                RectF rectangle = new RectF(mBoardSpace, mBoardSpace, (width - mBoardSpace), (length - mBoardSpace));
                canvas.drawRect(rectangle, mPaint);
                drawCenter(canvas, mPaintText, getResources().getString(R.string.objective_board_protection));
                break;
            }
            case PATTERN_EXTEND_BOARD: {
                mPaint.setColor(Color.parseColor("#44000000"));
                RectF rectangle = new RectF(mBoardSpace, mBoardSpace, (width - mBoardSpace), (length - mBoardSpace));
                canvas.drawRect(rectangle, mPaint);
                drawCenter(canvas, mPaintText, getResources().getString(R.string.objective_extend_board));
                break;
            }
            default:
                break;
        }
    }

    private Rect r = new Rect();

    private void drawCenter(Canvas canvas, Paint paint, String text) {
        canvas.getClipBounds(r);
        int cHeight = r.height();
        int cWidth = r.width();
        paint.setTextAlign(Paint.Align.LEFT);
        paint.getTextBounds(text, 0, text.length(), r);
        float x = cWidth / 2f - r.width() / 2f - r.left;
        float y = cHeight / 2f + r.height() / 2f - r.bottom;
        canvas.drawText(text, x, y, paint);
    }

    public void labelCustomerPatternCells() {
        switch (mPatternType) {
            case PATTERN_CUSTOMER_ONE: {
                // X pattern
                int rowNum = mPatternCells.length;
                int colNum = mPatternCells[0].length;
//                int shiftX = (rowNum>colNum) ? ((rowNum-colNum)/2) : 0;
//                int shiftY = (colNum>rowNum) ? ((colNum-rowNum)/2) : 0;
//                for(int i=0; i<mPatternCells.length; i++) {
//                    for(int j=0; j<mPatternCells[0].length; j++) {
//                        if(mPatternCells[i][j].isShouldBeTouched) {
//                            continue;
//                        }
//                        int i_shift = i - shiftX;
//                        int j_shift = j - shiftY;
//                        if(i_shift==j_shift) {
//                            mPatternCells[i][j].isShouldBeTouched = true;
//                            if(i+1 < mPatternCells.length) {
//                                mPatternCells[i+1][j].isShouldBeTouched = true;
//                            }
//                            if(i-1 >= 0) {
//                                mPatternCells[i-1][j].isShouldBeTouched = true;
//                            }
//                            if(j+1 < mPatternCells[0].length) {
//                                mPatternCells[i][j+1].isShouldBeTouched = true;
//                            }
//                            if(j-1 >= 0) {
//                                mPatternCells[i][j-1].isShouldBeTouched = true;
//                            }
//                        }
//                        int i_cross = (mPatternCells.length-1) - i;
//                        if(i_cross==j_shift) {
//                            mPatternCells[i][j].isShouldBeTouched = true;
//                            if(i+1 < mPatternCells.length) {
//                                mPatternCells[i+1][j].isShouldBeTouched = true;
//                            }
//                            if(i-1 >= 0) {
//                                mPatternCells[i-1][j].isShouldBeTouched = true;
//                            }
//                            if(j+1 < mPatternCells[0].length) {
//                                mPatternCells[i][j+1].isShouldBeTouched = true;
//                            }
//                            if(j-1 >= 0) {
//                                mPatternCells[i][j-1].isShouldBeTouched = true;
//                            }
//                        }
//                    }
//                }

                int centerX = rowNum / 2;
                int centerY = colNum / 2;
                drawLine(0, 0, rowNum, colNum);
//                drawLine(centerX, centerY, rowNum, 0);
//                drawLine(centerX, centerY, rowNum, colNum);
//                drawLine(0, centerY, centerX, colNum);

                break;
            }
            case PATTERN_CUSTOMER_TWO: {
                // 回 pattern
                int rowNum = mPatternCells.length;
                int colNum = mPatternCells[0].length;

                for (int i = 0; i < mPatternCells.length; i++) {
                    for (int j = 0; j < mPatternCells[0].length; j++) {

                        int layerOne_x = rowNum / 10;
                        int layerOne_y = colNum / 10;
                        if (i - layerOne_x == 0 && j - layerOne_y >= 0 && j < mPatternCells[0].length - layerOne_y) {
                            mPatternCells[i][j].isShouldBeTouched = true;
                            mPatternCells[i + 1][j].isShouldBeTouched = true;
                            mPatternCells[i + 2][j].isShouldBeTouched = true;
                        }
                        if (i == (mPatternCells.length - 1) - layerOne_x && j - layerOne_y >= 0 && j < mPatternCells[0].length - layerOne_y) {
                            mPatternCells[i][j].isShouldBeTouched = true;
                            mPatternCells[i - 1][j].isShouldBeTouched = true;
                            mPatternCells[i - 2][j].isShouldBeTouched = true;
                        }
                        if (j - layerOne_y == 0 && i - layerOne_x >= 0 && i < mPatternCells.length - layerOne_x) {
                            mPatternCells[i][j].isShouldBeTouched = true;
                            mPatternCells[i][j + 1].isShouldBeTouched = true;
                            mPatternCells[i][j + 2].isShouldBeTouched = true;
                        }
                        if (j == (mPatternCells[0].length - 1) - layerOne_y && i - layerOne_x >= 0 && i < mPatternCells.length - layerOne_x) {
                            mPatternCells[i][j].isShouldBeTouched = true;
                            mPatternCells[i][j - 1].isShouldBeTouched = true;
                            mPatternCells[i][j - 2].isShouldBeTouched = true;
                        }

                        int layerTwo_x = rowNum / 4;
                        int layerTwo_y = colNum / 4;
                        if (i - layerTwo_x == 0 && j - layerTwo_y >= 0 && j < mPatternCells[0].length - layerTwo_y) {
                            mPatternCells[i][j].isShouldBeTouched = true;
                            mPatternCells[i + 1][j].isShouldBeTouched = true;
                            mPatternCells[i + 2][j].isShouldBeTouched = true;
                        }
                        if (i == (mPatternCells.length - 1) - layerTwo_x && j - layerTwo_y >= 0 && j < mPatternCells[0].length - layerTwo_y) {
                            mPatternCells[i][j].isShouldBeTouched = true;
                            mPatternCells[i - 1][j].isShouldBeTouched = true;
                            mPatternCells[i - 2][j].isShouldBeTouched = true;
                        }
                        if (j - layerTwo_y == 0 && i - layerTwo_x >= 0 && i < mPatternCells.length - layerTwo_x) {
                            mPatternCells[i][j].isShouldBeTouched = true;
                            mPatternCells[i][j + 1].isShouldBeTouched = true;
                            mPatternCells[i][j + 2].isShouldBeTouched = true;
                        }
                        if (j == (mPatternCells[0].length - 1) - layerTwo_y && i - layerTwo_x >= 0 && i < mPatternCells.length - layerTwo_x) {
                            mPatternCells[i][j].isShouldBeTouched = true;
                            mPatternCells[i][j - 1].isShouldBeTouched = true;
                            mPatternCells[i][j - 2].isShouldBeTouched = true;
                        }
                    }
                }
                break;
            }
            case PATTERN_CUSTOMER_THREE: {
                // board pattern
                for (int i = 0; i < mPatternCells.length; i++) {
                    for (int j = 0; j < mPatternCells[0].length; j++) {
                        if (i == 0) {
                            mPatternCells[i][j].isShouldBeTouched = true;
                            mPatternCells[i + 1][j].isShouldBeTouched = true;
                            mPatternCells[i + 2][j].isShouldBeTouched = true;
                        }
                        if (i == mPatternCells.length - 1) {
                            mPatternCells[i][j].isShouldBeTouched = true;
                            mPatternCells[i - 1][j].isShouldBeTouched = true;
                            mPatternCells[i - 2][j].isShouldBeTouched = true;
                        }
                        if (j == 0) {
                            mPatternCells[i][j].isShouldBeTouched = true;
                            mPatternCells[i][j + 1].isShouldBeTouched = true;
                            mPatternCells[i][j + 2].isShouldBeTouched = true;
                        }
                        if (j == mPatternCells[0].length - 1) {
                            mPatternCells[i][j].isShouldBeTouched = true;
                            mPatternCells[i][j - 1].isShouldBeTouched = true;
                            mPatternCells[i][j - 2].isShouldBeTouched = true;
                        }
                    }
                }
                break;
            }
            case PATTERN_CUSTOMER_FOUR: {
                // fast draw line
                break;
            }
            case PATTERN_CUSTOMER_FIVE: {
                // keep thumb and draw arc
                int row = mPatternCells.length;
                int col = mPatternCells[0].length;

                int squareWidth = row / 8;
                int centerX = row / 2;
                int centerY = col / 2;
                int countX = squareWidth;
                int countY = 0;

                for (int i = 0; i < centerX; i++) {
                    for (int j = 0; j < mPatternCells[0].length; j++) {
                        if (i <= centerX + countX && i >= centerX - countX && j <= centerY + countY && j >= centerY - countY) {
                            mPatternCells[i][j].isShouldBeTouched = true;
                        }
                    }
                    if (i <= centerX + countX && i >= centerX - countX) {
                        countY++;
                    }
                }
                countY = squareWidth;
                for (int i = centerX; i < mPatternCells.length; i++) {
                    for (int j = 0; j < mPatternCells[0].length; j++) {
                        if (i <= centerX + countX && i >= centerX - countX && j <= centerY + countY && j >= centerY - countY) {
                            mPatternCells[i][j].isShouldBeTouched = true;
                        }
                    }
                    if (i <= centerX + countX && i >= centerX - countX) {
                        countY--;
                    }
                }

                int numArcRow = squareWidth * 3;
                int spaceBtwArcAndSquare = -squareWidth;
                int countArcY = numArcRow;

                for (int i = 0; i < mPatternCells.length; i++) {
                    for (int j = 0; j < mPatternCells[0].length; j++) {
                        if (i < centerX + spaceBtwArcAndSquare + numArcRow && i >= centerX + spaceBtwArcAndSquare) {
                            if (j <= (centerY + countArcY * (squareWidth / 2)) && j >= (centerY + (countArcY - 1) * (squareWidth / 2))) {
                                mPatternCells[i][j].isShouldBeTouched = true;
                                mPatternCells[i + 1][j].isShouldBeTouched = true;
                            }
                            if (j >= (centerY - countArcY * (squareWidth / 2)) && j <= (centerY - (countArcY - 1) * (squareWidth / 2))) {
                                mPatternCells[i][j].isShouldBeTouched = true;
                                mPatternCells[i + 1][j].isShouldBeTouched = true;
                            }
                        }
                    }
                    if (i < centerX + spaceBtwArcAndSquare + numArcRow && i >= centerX + spaceBtwArcAndSquare) {
                        countArcY--;
                    }
                }


                break;
            }
            case PATTERN_CUSTOMER_SIX: {
                int row = mPatternCells.length;
                int col = mPatternCells[0].length;

                int totalRow = mModel.mLineWidth * mModel.mVerticalLineNum + mModel.mLineSpace * (mModel.mVerticalLineNum - 1);
                int totalCol = mModel.mLineWidth * mModel.mHorizontalLineNum + mModel.mLineSpace * (mModel.mHorizontalLineNum - 1);

                int row_shift = (row - totalRow) / 2;
                int col_shift = (col - totalCol) / 2;

                for (int i = 0; i < mPatternCells.length; i++) {
                    for (int j = 0; j < mPatternCells[0].length; j++) {
                        if (i >= row_shift && i < (totalRow + row_shift)) {
                            int temp = (i - row_shift) % (mModel.mLineWidth + mModel.mLineSpace);
                            if (temp >= 0 && temp < mModel.mLineWidth) {
                                mPatternCells[i][j].isShouldBeTouched = true;
                            }
                        }
                        if (j >= col_shift && j < (totalCol + col_shift)) {
                            int temp = (j - col_shift) % (mModel.mLineWidth + mModel.mLineSpace);
                            if (temp >= 0 && temp < mModel.mLineWidth) {
                                mPatternCells[i][j].isShouldBeTouched = true;
                            }
                        }
                    }
                }

                break;
            }
        }
    }

    private void drawLine(int x_start, int y_start, int x_end, int y_end) {
        int x_w = Math.abs(x_start - x_end);
        int y_w = Math.abs(y_start - y_end);

        if (x_w > y_w) {
            int c = x_w / y_w;
            for (int t = 0; t < y_w; t++) {
                if (t == 0) {
                    drawRect(x_start, y_start, (int) (x_start + 1.5 * c), (y_start));
                } else if (t == c - 1) {
                    drawRect((int) (x_end - 1.5 * c), y_end, x_end, y_end);
                } else {
                    drawRect((int) (x_start + (t - 0.5) * c), (y_start + t), (int) (x_start + (t + 1.5) * c), (y_start + t));
                }
            }
        } else {
            int c = y_w / x_w + 1;
            int c_r = y_w % x_w;
            int c_rf = c_r / 2;
            int c_rfr = c_r % 2;
            int prev = 0;
            for (int t = 0; t < x_w; t++) {
                if (t == 0) {
                    drawRect(x_start, y_start, (x_start), (y_start + 2 * c));
                    prev = y_start;
                } else if (t == x_w - 1) {
                    drawRect((x_end), (y_end - 2 * c), x_end, y_end);
                } else {
                    if (t - c_rf + 1 <= 0) {
                        drawRect((x_start + t), (int) (y_start + (t - 1 - 1) * c), (x_start + t), (int) (y_start + (t + 1) * c));
                    } else if (t + 1 + c_rf > x_w) {
                        drawRect((x_start + t), (int) (y_start + (t - 1) * c), (x_start + t), (int) (y_start + (t + 1 + 1) * c));
                    } else {
                        drawRect((x_start + t), (int) (y_start + (t - 1 - 1) * c), (x_start + t), (int) (y_start + (t + 1 + 1) * c));
                    }
//                    }

                }
            }
        }
    }

    private void drawRect(int x_start, int y_start, int x_end, int y_end) {
        for (int i = x_start; i <= x_end; i++) {
            for (int j = y_start; j <= y_end; j++) {
                if (i < mPatternCells.length && j < mPatternCells[0].length && i >= 0 && j >= 0)
                    mPatternCells[i][j].isShouldBeTouched = true;
            }
        }
    }
}
