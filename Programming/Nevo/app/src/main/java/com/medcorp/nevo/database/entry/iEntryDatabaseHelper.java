package com.medcorp.nevo.database.entry;

import com.medcorp.nevo.ble.util.Optional;

import java.util.Date;
import java.util.List;

/**
 * Created by karl-john on 17/11/15.
 */
public interface iEntryDatabaseHelper<T> {

    public Optional<T> add(T object);
    public boolean update(T object);

    public boolean remove(int userId,Date date);
    public List<Optional<T>> get(int userId);
    public Optional<T> get(int userId,Date date);
    public List<Optional<T>> getAll();

    public List<T> convertToNormalList(List <Optional<T>> optionalList);

}
