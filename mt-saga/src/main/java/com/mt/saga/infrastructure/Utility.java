package com.mt.saga.infrastructure;

public class Utility {
    public static String getCancelLtxName(String ltxName) {
        return "CANCEL_" + ltxName;
    }
}
