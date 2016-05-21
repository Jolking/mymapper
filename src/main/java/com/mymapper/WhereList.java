package com.mymapper;

import com.mymapper.oper.KVOper;
import com.mymapper.oper.Oper;

import java.util.ArrayList;

public class WhereList extends ArrayList<Oper> {
    public WhereList() {
        super();
    }

    public WhereList eq(Object key, Object value) {
        add(new KVOper(key.toString(), " = ", value.toString()));
        return this;
    }

    public WhereList notEq(String key, Object value) {
        add(new KVOper(key, " != ", value));
        return this;
    }

    public WhereList gt(String key, Object value) {
        add(new KVOper(key, " > ", value));
        return this;
    }

    public WhereList gtAndEq(String key, Object value) {
        add(new KVOper(key, " >= ", value));
        return this;
    }

    public WhereList lt(String key, Object value) {
        add(new KVOper(key, " < ", value));
        return this;
    }

    public WhereList ltAndEq(String key, Object value) {
        add(new KVOper(key, " <= ", value));
        return this;
    }

    public WhereList and() {
        add(new Oper(" AND "));
        return this;
    }

    public WhereList or() {
        add(new Oper(" OR "));
        return this;
    }

    public WhereList orderBy(String orderBy) {
        add(new Oper(" ORDER BY " + orderBy + " "));
        return this;
    }

    public WhereList desc() {
        add(new Oper(" DESC "));
        return this;
    }

    public WhereList asc() {
        add(new Oper(" ASC "));
        return this;
    }

    public WhereList isTrue(String key) {
        add(new KVOper(key, " = ", true));
        return this;
    }

    public WhereList notTrue(String key) {
        add(new KVOper(key, " != ", true));
        return this;
    }

    public WhereList isFalse(String key) {
        add(new KVOper(key, " = ", false));
        return this;
    }

    public WhereList notFalse(String key) {
        add(new KVOper(key, " != ", false));
        return this;
    }
}