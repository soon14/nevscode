package com.nevs.car.tools.util;

import android.text.method.ReplacementTransformationMethod;

//小写转大写的方法   editText.setTransformationMethod(new UpperCaseTransform());
public class UpperCaseTransform extends ReplacementTransformationMethod {
    @Override
    protected char[] getOriginal() {
        char[] aa = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        return aa;
    }

    @Override
    protected char[] getReplacement() {
        char[] cc = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        return cc;
    }
}

