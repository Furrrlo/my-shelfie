package it.polimi.ingsw.model;

public interface Property<T> extends Provider<T> {

    void set(T val);
}