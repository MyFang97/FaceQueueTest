package com.visualdeep.libs.util;

import java.text.DecimalFormat;

public class FormatUtils {

    private static DecimalFormat df = new DecimalFormat("0.00");
    public static String format( double d )
    {
        return df.format( d );
    }
}
