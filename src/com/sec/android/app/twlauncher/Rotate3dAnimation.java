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

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

class Rotate3dAnimation extends Animation
{

    public Rotate3dAnimation(float f, float f1, float f2, float f3, float f4, boolean flag)
    {
        mFromDegrees = f;
        mToDegrees = f1;
        mCenterX = f2;
        mCenterY = f3;
        mDepthZ = f4;
        mReverse = flag;
    }

    protected void applyTransformation(float f, Transformation transformation)
    {
        float f2;
        Camera camera;
        int i;
        float f1 = mFromDegrees;
        f2 = f1 + f * (mToDegrees - f1);
        float f3 = mCenterX;
        float f4 = mCenterY;
        camera = mCamera;
        Matrix matrix = transformation.getMatrix();
        camera.save();
        if(mReverse)
            camera.translate(0.0F, 0.0F, f * mDepthZ);
        else
            camera.translate(0.0F, 0.0F, mDepthZ * (1.0F - f));
        i = mRotateAxis;
        if(i != 0) goto _L2; else goto _L1
_L1:
        camera.rotateY(f2);
_L4:
        camera.getMatrix(matrix);
        camera.restore();
        matrix.preTranslate(-f3, -f4);
        matrix.postTranslate(f3, f4);
        transformation.setTransformationType(Transformation.TYPE_BOTH);
        return;
_L2:
        if(i == 1)
            camera.rotateX(f2);
        if(true) goto _L4; else goto _L3
_L3:
    }

    public void initialize(int i, int j, int k, int l)
    {
        initialize(i, j, k, l);
        mCamera = new Camera();
    }

    public void setRotateAxis(int i)
    {
        if(i == 0 || i == 1)
            mRotateAxis = i;
    }

    private Camera mCamera;
    private final float mCenterX;
    private final float mCenterY;
    private final float mDepthZ;
    private final float mFromDegrees;
    private final boolean mReverse;
    private int mRotateAxis;
    private final float mToDegrees;
}
