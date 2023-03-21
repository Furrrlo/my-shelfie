package it.polimi.ingsw.model;

/**
 * 
 */
interface Property<T> extends Provider<T> {


    /**
     * @param val 
     * @return
     */
    public void set(T val);

}