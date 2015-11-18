package com.medcorp.nevo.database.entry;

import java.util.List;

/**
 * Created by karl-john on 17/11/15.
 */
public interface iEntryDatabaseHelper<T> {

    public boolean add(T object);
    public boolean update(T object);
    public boolean remove(int id);
    public T get(int id);
    public List<T> getAll();

}
