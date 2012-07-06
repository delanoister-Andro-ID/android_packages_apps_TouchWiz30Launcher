/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sec.android.app.twlauncher;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

// Referenced classes of package com.sec.android.app.twlauncher:
//            Folder

public class CellLayout extends ViewGroup
{
    static final class CellInfo
        implements android.view.ContextMenu.ContextMenuInfo
    {
        static final class VacantCell
        {

            static VacantCell acquire()
            {
                Object obj = sLock;
                obj;
                JVM INSTR monitorenter ;
                VacantCell vacantcell1;
                if(sRoot == null)
                {
                    VacantCell vacantcell = new VacantCell();
                    vacantcell1 = vacantcell;
                } else
                {
                    VacantCell vacantcell2 = sRoot;
                    sRoot = vacantcell2.next;
                    sAcquiredCount--;
                    vacantcell1 = vacantcell2;
                }
                return vacantcell1;
            }

            void release()
            {
                Object obj = sLock;
                obj;
                JVM INSTR monitorenter ;
                if(sAcquiredCount < 100)
                {
                    sAcquiredCount = 1 + sAcquiredCount;
                    next = sRoot;
                    sRoot = this;
                }
                return;
            }

            public String toString()
            {
                return (new StringBuilder()).append("VacantCell[x=").append(cellX).append(", y=").append(cellY).append(", spanX=").append(spanX).append(", spanY=").append(spanY).append("]").toString();
            }

            private static int sAcquiredCount = 0;
            private static final Object sLock = new Object();
            private static VacantCell sRoot;
            int cellX;
            int cellY;
            private VacantCell next;
            int spanX;
            int spanY;


            VacantCell()
            {
            }
        }


        void clearVacantCells()
        {
            ArrayList arraylist = vacantCells;
            int i = arraylist.size();
            for(int j = 0; j < i; j++)
                ((VacantCell)arraylist.get(j)).release();

            arraylist.clear();
        }

        boolean findCellForSpan(int ai[], int i, int j)
        {
            return findCellForSpan(ai, i, j, true);
        }

        boolean findCellForSpan(int ai[], int i, int j, boolean flag)
        {
            ArrayList arraylist;
            int k;
            boolean flag1;
            int l;
            arraylist = vacantCells;
            k = arraylist.size();
            flag1 = false;
            if(spanX >= i && spanY >= j)
            {
                ai[0] = cellX;
                ai[1] = cellY;
                flag1 = true;
            }
            l = 0;
_L5:
            if(l >= k) goto _L2; else goto _L1
_L1:
            VacantCell vacantcell1 = (VacantCell)arraylist.get(l);
            if(vacantcell1.spanX != i || vacantcell1.spanY != j) goto _L4; else goto _L3
_L3:
            ai[0] = vacantcell1.cellX;
            ai[1] = vacantcell1.cellY;
            flag1 = true;
_L2:
            int i1 = 0;
_L6:
            if(i1 < k)
            {
                VacantCell vacantcell = (VacantCell)arraylist.get(i1);
                if(vacantcell.spanX < i || vacantcell.spanY < j)
                    break MISSING_BLOCK_LABEL_185;
                ai[0] = vacantcell.cellX;
                ai[1] = vacantcell.cellY;
                flag1 = true;
            }
            if(flag)
                clearVacantCells();
            return flag1;
_L4:
            l++;
              goto _L5
            i1++;
              goto _L6
        }

        void findVacantCellsFromOccupied(boolean aflag[], int i, int j)
        {
            if(cellX < 0 || cellY < 0)
            {
                maxVacantSpanXSpanY = 0x80000000;
                maxVacantSpanX = 0x80000000;
                maxVacantSpanYSpanX = 0x80000000;
                maxVacantSpanY = 0x80000000;
                clearVacantCells();
            } else
            {
                int ai[] = new int[2];
                ai[0] = i;
                ai[1] = j;
                boolean aflag1[][] = (boolean[][])Array.newInstance(Boolean.TYPE, ai);
                for(int k = 0; k < j; k++)
                {
                    for(int l = 0; l < i; l++)
                        aflag1[l][k] = aflag[l + k * i];

                }

                CellLayout.findIntersectingVacantCells(this, cellX, cellY, i, j, aflag1);
            }
        }

        public String toString()
        {
            StringBuilder stringbuilder = (new StringBuilder()).append("Cell[view=");
            Object obj;
            if(cell == null)
                obj = "null";
            else
                obj = cell.getClass();
            return stringbuilder.append(obj).append(", x=").append(cellX).append(", y=").append(cellY).append("]").toString();
        }

        View cell;
        int cellX;
        int cellY;
        final Rect current = new Rect();
        int maxVacantSpanX;
        int maxVacantSpanXSpanY;
        int maxVacantSpanY;
        int maxVacantSpanYSpanX;
        int screen;
        int spanX;
        int spanY;
        final ArrayList vacantCells = new ArrayList(100);
        boolean valid;

        CellInfo()
        {
        }
    }

    public static class LayoutParams extends android.view.ViewGroup.MarginLayoutParams
    {

        public void setup(int i, int j, int k, int l, int i1, int j1)
        {
            int k1 = cellHSpan;
            int l1 = cellVSpan;
            int i2 = cellX;
            int j2 = cellY;
            width = (k1 * i + k * (k1 - 1)) - leftMargin - rightMargin;
            height = (l1 * j + l * (l1 - 1)) - topMargin - bottomMargin;
            x = i1 + i2 * (i + k) + leftMargin;
            y = j1 + j2 * (j + l) + topMargin;
        }

        public int cellHSpan;
        public int cellVSpan;
        public int cellX;
        public int cellY;
        boolean dropped;
        public boolean isDragging;
        boolean regenerateId;
        int x;
        int y;

        public LayoutParams(int i, int j, int k, int l)
        {
            super(-1, -1);
            cellX = i;
            cellY = j;
            cellHSpan = k;
            cellVSpan = l;
        }

        public LayoutParams(Context context, AttributeSet attributeset)
        {
            super(context, attributeset);
            cellHSpan = 1;
            cellVSpan = 1;
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams layoutparams)
        {
            super(layoutparams);
            cellHSpan = 1;
            cellVSpan = 1;
        }
    }


    public CellLayout(Context context)
    {
        this(context, null);
    }

    public CellLayout(Context context, AttributeSet attributeset)
    {
        this(context, attributeset, 0);
    }

    public CellLayout(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
        mRect = new Rect();
        mCellInfo = new CellInfo();
        mCellXY = new int[2];
        mDragRect = new RectF();
        mLastDownOnOccupiedCell = false;
        TypedArray typedarray = context.obtainStyledAttributes(attributeset, R.styleable.CellLayout, i, 0);
        mCellWidth = typedarray.getDimensionPixelSize(0, 10);
        mCellHeight = typedarray.getDimensionPixelSize(1, 10);
        mLongAxisStartPadding = typedarray.getDimensionPixelSize(2, 10);
        mLongAxisEndPadding = typedarray.getDimensionPixelSize(3, 10);
        mShortAxisStartPadding = typedarray.getDimensionPixelSize(4, 10);
        mShortAxisEndPadding = typedarray.getDimensionPixelSize(5, 10);
        mShortAxisCells = typedarray.getInt(6, 4);
        mLongAxisCells = typedarray.getInt(7, 4);
        typedarray.recycle();
        setAlwaysDrawnWithCacheEnabled(false);
        if(mOccupied == null)
            if(mPortrait)
            {
                int l = mShortAxisCells;
                int i1 = mLongAxisCells;
                int ai1[] = new int[2];
                ai1[0] = l;
                ai1[1] = i1;
                mOccupied = (boolean[][])Array.newInstance(Boolean.TYPE, ai1);
            } else
            {
                int j = mLongAxisCells;
                int k = mShortAxisCells;
                int ai[] = new int[2];
                ai[0] = j;
                ai[1] = k;
                mOccupied = (boolean[][])Array.newInstance(Boolean.TYPE, ai);
            }
        mWallpaperManager = WallpaperManager.getInstance(context);
        mActualCellWidth = context.getResources().getDimensionPixelSize(0x7f090003);
        mActualCellHeight = context.getResources().getDimensionPixelSize(0x7f090004);
        mIspadding = context.getResources().getBoolean(0x7f080000);
        mDimPaint = new Paint();
        mDimPaint.setColorFilter(new PorterDuffColorFilter(0x7fff0000, android.graphics.PorterDuff.Mode.SRC_ATOP));
    }

    private static void addVacantCell(Rect rect, CellInfo cellinfo)
    {
        CellInfo.VacantCell vacantcell = CellInfo.VacantCell.acquire();
        vacantcell.cellX = rect.left;
        vacantcell.cellY = rect.top;
        vacantcell.spanX = 1 + (rect.right - rect.left);
        vacantcell.spanY = 1 + (rect.bottom - rect.top);
        if(vacantcell.spanX > cellinfo.maxVacantSpanX)
        {
            cellinfo.maxVacantSpanX = vacantcell.spanX;
            cellinfo.maxVacantSpanXSpanY = vacantcell.spanY;
        }
        if(vacantcell.spanY > cellinfo.maxVacantSpanY)
        {
            cellinfo.maxVacantSpanY = vacantcell.spanY;
            cellinfo.maxVacantSpanYSpanX = vacantcell.spanX;
        }
        cellinfo.vacantCells.add(vacantcell);
    }

    private static void findIntersectingVacantCells(CellInfo cellinfo, int i, int j, int k, int l, boolean aflag[][])
    {
        cellinfo.maxVacantSpanX = 0x80000000;
        cellinfo.maxVacantSpanXSpanY = 0x80000000;
        cellinfo.maxVacantSpanY = 0x80000000;
        cellinfo.maxVacantSpanYSpanX = 0x80000000;
        cellinfo.clearVacantCells();
        if(!aflag[i][j])
        {
            cellinfo.current.set(i, j, i, j);
            findVacantCell(cellinfo.current, k, l, aflag, cellinfo);
        }
    }

    private void findOccupiedCells(int i, int j, boolean aflag[][], View view)
    {
        for(int k = 0; k < i; k++)
        {
            for(int l1 = 0; l1 < j; l1++)
                aflag[k][l1] = false;

        }

        int l = getChildCount();
        int i1 = 0;
        while(i1 < l) 
        {
            View view1 = getChildAt(i1);
            if(!(view1 instanceof Folder) && view1 != view)
            {
                LayoutParams layoutparams = (LayoutParams)view1.getLayoutParams();
                int j1 = layoutparams.cellX;
                while(j1 < layoutparams.cellX + layoutparams.cellHSpan && j1 < i) 
                {
                    for(int k1 = layoutparams.cellY; k1 < layoutparams.cellY + layoutparams.cellVSpan && k1 < j; k1++)
                        aflag[j1][k1] = true;

                    j1++;
                }
            }
            i1++;
        }
    }

    private static void findVacantCell(Rect rect, int i, int j, boolean aflag[][], CellInfo cellinfo)
    {
        addVacantCell(rect, cellinfo);
        if(rect.left > 0 && isColumnEmpty(rect.left - 1, rect.top, rect.bottom, aflag))
        {
            rect.left = rect.left - 1;
            findVacantCell(rect, i, j, aflag, cellinfo);
            rect.left = 1 + rect.left;
        }
        if(rect.right < i - 1 && isColumnEmpty(1 + rect.right, rect.top, rect.bottom, aflag))
        {
            rect.right = 1 + rect.right;
            findVacantCell(rect, i, j, aflag, cellinfo);
            rect.right = rect.right - 1;
        }
        if(rect.top > 0 && isRowEmpty(rect.top - 1, rect.left, rect.right, aflag))
        {
            rect.top = rect.top - 1;
            findVacantCell(rect, i, j, aflag, cellinfo);
            rect.top = 1 + rect.top;
        }
        if(rect.bottom < j - 1 && isRowEmpty(1 + rect.bottom, rect.left, rect.right, aflag))
        {
            rect.bottom = 1 + rect.bottom;
            findVacantCell(rect, i, j, aflag, cellinfo);
            rect.bottom = rect.bottom - 1;
        }
    }

    static boolean findVacantCell(int ai[], int i, int j, int k, int l, boolean aflag[][])
    {
        int i1 = 0;
_L13:
        int j1;
        if(i1 >= k)
            break MISSING_BLOCK_LABEL_157;
        j1 = 0;
_L12:
        if(j1 >= l) goto _L2; else goto _L1
_L1:
        boolean flag;
        boolean flag1;
        int k1;
        int l1;
        if(!aflag[i1][j1])
            flag1 = true;
        else
            flag1 = false;
        k1 = i1;
_L11:
        if(k1 >= (i1 + i) - 1 || i1 >= k) goto _L4; else goto _L3
_L3:
        l1 = j1;
_L10:
        if(l1 >= (j1 + j) - 1 || j1 >= l) goto _L6; else goto _L5
_L5:
        if(flag1 && !aflag[k1][l1])
            flag1 = true;
        else
            flag1 = false;
        if(flag1) goto _L7; else goto _L4
_L4:
        if(!flag1) goto _L9; else goto _L8
_L8:
        ai[0] = i1;
        ai[1] = j1;
        flag = true;
_L14:
        return flag;
_L7:
        l1++;
          goto _L10
_L6:
        k1++;
          goto _L11
_L9:
        j1++;
          goto _L12
_L2:
        i1++;
          goto _L13
        flag = false;
          goto _L14
    }

    private static boolean isColumnEmpty(int i, int j, int k, boolean aflag[][])
    {
        int l = j;
_L3:
        if(l > k)
            break MISSING_BLOCK_LABEL_30;
        if(!aflag[i][l]) goto _L2; else goto _L1
_L1:
        boolean flag = false;
_L4:
        return flag;
_L2:
        l++;
          goto _L3
        flag = true;
          goto _L4
    }

    private static boolean isRowEmpty(int i, int j, int k, boolean aflag[][])
    {
        int l = j;
_L3:
        if(l > k)
            break MISSING_BLOCK_LABEL_30;
        if(!aflag[l][i]) goto _L2; else goto _L1
_L1:
        boolean flag = false;
_L4:
        return flag;
_L2:
        l++;
          goto _L3
        flag = true;
          goto _L4
    }

    public void addView(View view, int i, android.view.ViewGroup.LayoutParams layoutparams)
    {
        ((LayoutParams)layoutparams).regenerateId = true;
        super.addView(view, i, layoutparams);
    }

    public void cancelLongPress()
    {
        super.cancelLongPress();
        int i = getChildCount();
        for(int j = 0; j < i; j++)
            getChildAt(j).cancelLongPress();

    }

    void cellToPoint(int i, int j, int ai[])
    {
        boolean flag = mPortrait;
        int k;
        int l;
        if(flag)
            k = mShortAxisStartPadding;
        else
            k = mLongAxisStartPadding;
        if(flag)
            l = mLongAxisStartPadding;
        else
            l = mShortAxisStartPadding;
        ai[0] = k + i * (mCellWidth + mWidthGap);
        ai[1] = l + j * (mCellHeight + mHeightGap);
    }

    protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams layoutparams)
    {
        return layoutparams instanceof LayoutParams;
    }

    protected boolean drawChild(Canvas canvas, View view, long l)
    {
        return super.drawChild(canvas, view, l);
    }

    CellInfo findAllVacantCells(boolean aflag[], View view)
    {
        boolean flag = mPortrait;
        int i;
        int j;
        boolean aflag1[][];
        if(flag)
            i = mShortAxisCells;
        else
            i = mLongAxisCells;
        if(flag)
            j = mLongAxisCells;
        else
            j = mShortAxisCells;
        aflag1 = mOccupied;
        if(aflag != null)
        {
            for(int k = 0; k < j; k++)
            {
                for(int l = 0; l < i; l++)
                    aflag1[l][k] = aflag[l + k * i];

            }

        } else
        {
            findOccupiedCells(i, j, aflag1, view);
        }
        return findAllVacantCellsFromOccupied(aflag1, i, j);
    }

    CellInfo findAllVacantCellsFromOccupied(boolean aflag[][], int i, int j)
    {
        CellInfo cellinfo = new CellInfo();
        cellinfo.cellX = -1;
        cellinfo.cellY = -1;
        cellinfo.spanY = 0;
        cellinfo.spanX = 0;
        cellinfo.maxVacantSpanX = 0x80000000;
        cellinfo.maxVacantSpanXSpanY = 0x80000000;
        cellinfo.maxVacantSpanY = 0x80000000;
        cellinfo.maxVacantSpanYSpanX = 0x80000000;
        cellinfo.screen = mCellInfo.screen;
        Rect rect = cellinfo.current;
        for(int k = 0; k < i; k++)
        {
            for(int l = 0; l < j; l++)
                if(!aflag[k][l])
                {
                    rect.set(k, l, k, l);
                    findVacantCell(rect, i, j, aflag, cellinfo);
                    aflag[k][l] = true;
                }

        }

        boolean flag;
        if(cellinfo.vacantCells.size() > 0)
            flag = true;
        else
            flag = false;
        cellinfo.valid = flag;
        return cellinfo;
    }

    int[] findNearestVacantArea(int i, int j, int k, int l, CellInfo cellinfo, int ai[])
    {
        int ai1[];
        int ai2[];
        double d;
        int ai3[];
        if(ai != null)
            ai1 = ai;
        else
            ai1 = new int[2];
        ai2 = mCellXY;
        d = 1.7976931348623157E+308D;
        if(cellinfo.valid) goto _L2; else goto _L1
_L1:
        ai3 = null;
_L4:
        return ai3;
_L2:
        int i1 = cellinfo.vacantCells.size();
        int j1 = 0;
        while(j1 < i1) 
        {
            CellInfo.VacantCell vacantcell = (CellInfo.VacantCell)cellinfo.vacantCells.get(j1);
            if(vacantcell.spanX == k && vacantcell.spanY == l)
            {
                cellToPoint(vacantcell.cellX, vacantcell.cellY, ai2);
                double d1 = Math.sqrt(Math.pow(ai2[0] - i, 2D) + Math.pow(ai2[1] - j, 2D));
                if(d1 <= d)
                {
                    d = d1;
                    ai1[0] = vacantcell.cellX;
                    ai1[1] = vacantcell.cellY;
                }
            }
            j1++;
        }
        if(d < 1.7976931348623157E+308D)
            ai3 = ai1;
        else
            ai3 = null;
        if(true) goto _L4; else goto _L3
_L3:
    }

    public android.view.ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeset)
    {
        return new LayoutParams(getContext(), attributeset);
    }

    protected android.view.ViewGroup.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams layoutparams)
    {
        return new LayoutParams(layoutparams);
    }

    int getCountX()
    {
        int i;
        if(mPortrait)
            i = mShortAxisCells;
        else
            i = mLongAxisCells;
        return i;
    }

    int getCountY()
    {
        int i;
        if(mPortrait)
            i = mLongAxisCells;
        else
            i = mShortAxisCells;
        return i;
    }

    int getLongAxisCells()
    {
        return mLongAxisCells;
    }

    boolean[] getOccupiedCells()
    {
        boolean flag = mPortrait;
        int i;
        int j;
        boolean aflag[][];
        boolean aflag1[];
        if(flag)
            i = mShortAxisCells;
        else
            i = mLongAxisCells;
        if(flag)
            j = mLongAxisCells;
        else
            j = mShortAxisCells;
        aflag = mOccupied;
        findOccupiedCells(i, j, aflag, null);
        aflag1 = new boolean[i * j];
        for(int k = 0; k < j; k++)
        {
            for(int l = 0; l < i; l++)
                aflag1[l + k * i] = aflag[l][k];

        }

        return aflag1;
    }

    int getShortAxisCells()
    {
        return mShortAxisCells;
    }

    public CellInfo getTag()
    {
        CellInfo cellinfo = (CellInfo)super.getTag();
        if(mDirtyTag && cellinfo.valid)
        {
            boolean flag = mPortrait;
            int i;
            int j;
            boolean aflag[][];
            if(flag)
                i = mShortAxisCells;
            else
                i = mLongAxisCells;
            if(flag)
                j = mLongAxisCells;
            else
                j = mShortAxisCells;
            aflag = mOccupied;
            findOccupiedCells(i, j, aflag, null);
            findIntersectingVacantCells(cellinfo, cellinfo.cellX, cellinfo.cellY, i, j, aflag);
            mDirtyTag = false;
        }
        return cellinfo;
    }

    public volatile Object getTag()
    {
        return getTag();
    }

    public boolean lastDownOnOccupiedCell()
    {
        return mLastDownOnOccupiedCell;
    }

    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        mCellInfo.screen = ((ViewGroup)getParent()).indexOfChild(this);
    }

    void onDragChild(View view)
    {
        ((LayoutParams)view.getLayoutParams()).isDragging = true;
        mDragRect.setEmpty();
    }

    void onDropAborted(View view)
    {
        if(view != null)
        {
            ((LayoutParams)view.getLayoutParams()).isDragging = false;
            invalidate();
        }
        mDragRect.setEmpty();
    }

    void onDropChild(View view, int ai[])
    {
        if(view != null)
        {
            LayoutParams layoutparams = (LayoutParams)view.getLayoutParams();
            layoutparams.cellX = ai[0];
            layoutparams.cellY = ai[1];
            layoutparams.isDragging = false;
            layoutparams.dropped = true;
            mDragRect.setEmpty();
            view.requestLayout();
            invalidate();
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent motionevent)
    {
        int i;
        CellInfo cellinfo;
        Rect rect;
        int j;
        int k;
        boolean flag;
        int i1;
        i = motionevent.getAction();
        cellinfo = mCellInfo;
        if(i != 0)
            break MISSING_BLOCK_LABEL_367;
        rect = mRect;
        j = (int)motionevent.getX() + mScrollX;
        k = (int)motionevent.getY() + mScrollY;
        int l = getChildCount();
        flag = false;
        i1 = l - 1;
_L6:
        if(i1 < 0) goto _L2; else goto _L1
_L1:
        View view = getChildAt(i1);
        if(view.getVisibility() != 0 && view.getAnimation() == null) goto _L4; else goto _L3
_L3:
        view.getHitRect(rect);
        if(!rect.contains(j, k)) goto _L4; else goto _L5
_L5:
        LayoutParams layoutparams = (LayoutParams)view.getLayoutParams();
        cellinfo.cell = view;
        cellinfo.cellX = layoutparams.cellX;
        cellinfo.cellY = layoutparams.cellY;
        cellinfo.spanX = layoutparams.cellHSpan;
        cellinfo.spanY = layoutparams.cellVSpan;
        cellinfo.valid = true;
        flag = true;
        mDirtyTag = false;
_L2:
        if(!flag)
        {
            int ai[] = mCellXY;
            pointToCellExact(j, k, ai);
            boolean flag1 = mPortrait;
            int j1;
            int k1;
            boolean aflag[][];
            boolean flag2;
            if(flag1)
                j1 = mShortAxisCells;
            else
                j1 = mLongAxisCells;
            if(flag1)
                k1 = mLongAxisCells;
            else
                k1 = mShortAxisCells;
            aflag = mOccupied;
            findOccupiedCells(j1, k1, aflag, null);
            cellinfo.cell = null;
            cellinfo.cellX = ai[0];
            cellinfo.cellY = ai[1];
            cellinfo.spanX = 1;
            cellinfo.spanY = 1;
            if(ai[0] >= 0 && ai[1] >= 0 && ai[0] < j1 && ai[1] < k1 && !aflag[ai[0]][ai[1]])
                flag2 = true;
            else
                flag2 = false;
            cellinfo.valid = flag2;
            mDirtyTag = true;
        }
        setTag(cellinfo);
_L7:
        return false;
_L4:
        i1--;
          goto _L6
        if(i == 1)
        {
            cellinfo.cell = null;
            cellinfo.cellX = -1;
            cellinfo.cellY = -1;
            cellinfo.spanX = 0;
            cellinfo.spanY = 0;
            cellinfo.valid = false;
            mDirtyTag = false;
            setTag(cellinfo);
        }
          goto _L7
    }

    protected void onLayout(boolean flag, int i, int j, int k, int l)
    {
        int i1 = getChildCount();
        for(int j1 = 0; j1 < i1; j1++)
        {
            View view = getChildAt(j1);
            if(view.getVisibility() == 8)
                continue;
            LayoutParams layoutparams = (LayoutParams)view.getLayoutParams();
            int k1 = layoutparams.x;
            int l1 = layoutparams.y;
            view.layout(k1, l1, k1 + layoutparams.width, l1 + layoutparams.height);
            if(layoutparams.dropped)
            {
                layoutparams.dropped = false;
                mWallpaperManager.sendWallpaperCommand(getWindowToken(), "android.home.drop", k1 + layoutparams.width / 2, l1 + layoutparams.height / 2, 0, null);
            }
        }

    }

    protected void onMeasure(int i, int j)
    {
        int k = android.view.View.MeasureSpec.getMode(i);
        int l = android.view.View.MeasureSpec.getSize(i);
        int i1 = android.view.View.MeasureSpec.getMode(j);
        int j1 = android.view.View.MeasureSpec.getSize(j);
        if(k == 0 || i1 == 0)
            throw new RuntimeException("CellLayout cannot have UNSPECIFIED dimensions");
        int k1 = mShortAxisCells;
        int l1 = mLongAxisCells;
        int i2 = mLongAxisStartPadding;
        int j2 = mLongAxisEndPadding;
        int k2 = mShortAxisStartPadding;
        int l2 = mShortAxisEndPadding;
        int i3 = mCellWidth;
        int j3 = mCellHeight;
        boolean flag;
        int k3;
        int l3;
        if(j1 > l)
            flag = true;
        else
            flag = false;
        mPortrait = flag;
        k3 = k1 - 1;
        l3 = l1 - 1;
        if(mPortrait)
        {
            mHeightGap = (j1 - i2 - j2 - j3 * l1) / l3;
            int l4 = l - k2 - l2 - i3 * k1;
            int j4;
            int k4;
            View view;
            if(k3 > 0)
                mWidthGap = l4 / k3;
            else
                mWidthGap = 0;
        } else
        {
            mWidthGap = (l - i2 - j2 - i3 * l1) / l3;
            int i4 = j1 - k2 - l2 - j3 * k1;
            if(k3 > 0)
                mHeightGap = i4 / k3;
            else
                mHeightGap = 0;
        }
        if(!getResources().getBoolean(0x7f080003))
            mWidthGap = 0;
        if(!mIspadding)
            mHeightGap = 0;
        j4 = getChildCount();
        k4 = 0;
        while(k4 < j4) 
        {
            view = getChildAt(k4);
            LayoutParams layoutparams = (LayoutParams)view.getLayoutParams();
            if(mPortrait)
                layoutparams.setup(i3, j3, mWidthGap, mHeightGap, k2, i2);
            else
                layoutparams.setup(i3, j3, mWidthGap, mHeightGap, i2, k2);
            if(layoutparams.regenerateId)
            {
                view.setId((0xff & getId()) << 16 | (0xff & layoutparams.cellX) << 8 | 0xff & layoutparams.cellY);
                layoutparams.regenerateId = false;
            }
            view.measure(android.view.View.MeasureSpec.makeMeasureSpec(layoutparams.width, 0x40000000), android.view.View.MeasureSpec.makeMeasureSpec(layoutparams.height, 0x40000000));
            k4++;
        }
        setMeasuredDimension(l, j1);
    }

    void pointToCellExact(int i, int j, int ai[])
    {
        boolean flag = mPortrait;
        int k;
        int l;
        int i1;
        int j1;
        if(flag)
            k = mShortAxisStartPadding;
        else
            k = mLongAxisStartPadding;
        if(flag)
            l = mLongAxisStartPadding;
        else
            l = mShortAxisStartPadding;
        ai[0] = (i - k) / (mCellWidth + mWidthGap);
        ai[1] = (j - l) / (mCellHeight + mHeightGap);
        if(flag)
            i1 = mShortAxisCells;
        else
            i1 = mLongAxisCells;
        if(flag)
            j1 = mLongAxisCells;
        else
            j1 = mShortAxisCells;
        if(ai[0] < 0)
            ai[0] = 0;
        if(ai[0] >= i1)
            ai[0] = i1 - 1;
        if(ai[1] < 0)
            ai[1] = 0;
        if(ai[1] >= j1)
            ai[1] = j1 - 1;
    }

    public int[] rectToCell(int i, int j)
    {
        int k = Math.min(mActualCellWidth, mActualCellHeight);
        int l = (i + k) / k;
        int i1 = (j + k) / k;
        int ai[] = new int[2];
        ai[0] = l;
        ai[1] = i1;
        return ai;
    }

    public int[] rectToCell2(int i, int j)
    {
        int k = mActualCellWidth;
        int l = mActualCellHeight;
        int i1;
        int j1;
        int ai[];
        if(i % k != 0)
            i1 = 1 + i / k;
        else
            i1 = i / k;
        if(j % l != 0)
            j1 = 1 + j / l;
        else
            j1 = j / l;
        if(i1 > mShortAxisCells)
            i1 = mShortAxisCells;
        if(j1 > mLongAxisCells)
            j1 = mLongAxisCells;
        ai = new int[2];
        ai[0] = i1;
        ai[1] = j1;
        return ai;
    }

    public void requestChildFocus(View view, View view1)
    {
        super.requestChildFocus(view, view1);
        if(view != null)
        {
            Rect rect = new Rect();
            view.getDrawingRect(rect);
            requestRectangleOnScreen(rect);
        }
    }

    protected void setChildrenDrawingCacheEnabled(boolean flag)
    {
        int i = getChildCount();
        for(int j = 0; j < i; j++)
        {
            View view = getChildAt(j);
            view.setDrawingCacheEnabled(flag);
            view.buildDrawingCache(true);
        }

    }

    protected void setChildrenDrawnWithCacheEnabled(boolean flag)
    {
        super.setChildrenDrawnWithCacheEnabled(flag);
    }

    private int mActualCellHeight;
    private int mActualCellWidth;
    private int mCellHeight;
    private final CellInfo mCellInfo;
    private int mCellWidth;
    int mCellXY[];
    private Paint mDimPaint;
    private boolean mDirtyTag;
    private RectF mDragRect;
    private int mHeightGap;
    private boolean mIspadding;
    private boolean mLastDownOnOccupiedCell;
    private int mLongAxisCells;
    private int mLongAxisEndPadding;
    private int mLongAxisStartPadding;
    boolean mOccupied[][];
    private boolean mPortrait;
    private final Rect mRect;
    private int mShortAxisCells;
    private int mShortAxisEndPadding;
    private int mShortAxisStartPadding;
    private final WallpaperManager mWallpaperManager;
    private int mWidthGap;

}
