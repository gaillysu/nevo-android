package com.medcorp.nevo.database.entry;

import java.util.Date;
import java.util.List;

/**
 * Created by karl-john on 17/11/15.
 */
public interface iEntryDatabaseHelper<T> {

    public T add(T object);
    public boolean update(T object);

    /**
     *
     * @param userid
     * @param date : which day's record, if the table is "User", ignore it.
     * @return
     */

    public boolean remove(int userid,Date date);
    public T get(int userid,Date date);
    public List<T> getAll();

}
