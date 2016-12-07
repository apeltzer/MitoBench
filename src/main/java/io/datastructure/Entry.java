package io.datastructure;

import io.IInputType;

/**
 * This class is intended to provide a generic view.data type resource of the form <ID + view.data>. Data can be whatever we want to have, we just need to define it once as String, integer, double.
 * Created by peltzer on 17/11/2016.
 */
public class Entry<T> {
    private String identifier;
    private IInputType type;
    private T data;


    public Entry(String identifier, IInputType type, T data) {
        this.identifier = identifier;
        this.type = type;
        this.data = data;
    }

    public IInputType getType() {
        return type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public T getData() {
        return data;
    }

}
