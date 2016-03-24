package com.medcorp.nevo.database.entry;


import net.medcorp.library.ble.util.Optional;

import java.util.Date;
import java.util.List;

/**
 * Created by karl-john on 17/11/15.
 */
public interface iEntryDatabaseHelper<T> {

    public Optional<T> add(T object);
    public boolean update(T object);

    /**
     * here userId is generated by nevo API, it is a number string,such as "1242342"
     * @param userId
     * @param date
     * @return
     */
    public boolean remove(String userId,Date date);
    public List<Optional<T>> get(String userId);
    public Optional<T> get(String userId,Date date);
    public List<Optional<T>> getAll(String userId);

    public List<T> convertToNormalList(List <Optional<T>> optionalList);

}
