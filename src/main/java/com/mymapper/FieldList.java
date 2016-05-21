package com.mymapper;

import java.util.ArrayList;

/**
 * Created by huang on 4/3/16.
 */
public class FieldList extends ArrayList<String> {
    public FieldList(String... fieldList) {
        for (String field : fieldList) {
            add(field);
        }
    }
}
