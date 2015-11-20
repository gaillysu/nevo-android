package com.medcorp.nevo.database.entry;

import com.j256.ormlite.dao.Dao;
import com.medcorp.nevo.application.ApplicationModel;
import com.medcorp.nevo.database.DatabaseHelper;
import com.medcorp.nevo.database.dao.UserDAO;
import com.medcorp.nevo.model.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by karl-john on 17/11/15.
 */
public class UserDatabaseHelper implements iEntryDatabaseHelper<User> {

    // instance of the database goes here as private variable
    private DatabaseHelper mDatabaseHelper;

    public UserDatabaseHelper() {
        // Open the database & initlialize
        mDatabaseHelper = DatabaseHelper.getInstance(ApplicationModel.getApplicationModel());
    }

    @Override
    public boolean add(User object) {
        int result = -1;
        try {
            result = mDatabaseHelper.getUserDao().create(convertToDao(object));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result>=0;
    }

    @Override
    public boolean update(User object) {
        int result = -1;
        try {
            List<UserDAO> userDAOList = mDatabaseHelper.getUserDao().queryBuilder().where().eq(UserDAO.fID, object.getId()).query();
            if(userDAOList.isEmpty()) return add(object);
            UserDAO daoobject = convertToDao(object);
            daoobject.setID(userDAOList.get(0).getID());
            result = mDatabaseHelper.getUserDao().update(daoobject);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result>=0;
    }

    @Override
    public boolean remove(int userid,Date date) {
        try {
            List<UserDAO> userDAOList = mDatabaseHelper.getUserDao().queryBuilder().where().eq(UserDAO.fID, userid).query();
            if(!userDAOList.isEmpty()) mDatabaseHelper.getUserDao().delete(userDAOList);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public User get(int userid,Date date) {
        List<User> userList = new ArrayList<User>();
        try {
            List<UserDAO> userDAOList = mDatabaseHelper.getUserDao().queryBuilder().where().eq(UserDAO.fID, userid).query();
            for(UserDAO userDAO: userDAOList) {
                userList.add(convertToNormal(userDAO));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userList.isEmpty()?null: userList.get(0);
    }

    @Override
    public List<User> getAll() {
        List<User> userList = new ArrayList<User>();
        try {
            List<UserDAO> userDAOList  = mDatabaseHelper.getUserDao().queryBuilder().query();
            for(UserDAO userDAO: userDAOList) {
                userList.add(convertToNormal(userDAO));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userList;
    }

    private UserDAO convertToDao(User user){
        UserDAO userDAO = new UserDAO();
        userDAO.setCreatedDate(user.getCreatedDate());
        userDAO.setHeight(user.getHeight());
        userDAO.setAge(user.getAge());
        userDAO.setBirthday(user.getBirthday());
        userDAO.setWeight(user.getWeight());
        userDAO.setRemarks(user.getRemarks());
        return userDAO;
    }

    private User convertToNormal(UserDAO userDAO){
        User user = new User(userDAO.getID(), userDAO.getCreatedDate());
        user.setAge(userDAO.getAge());
        user.setHeight(userDAO.getHeight());
        user.setBirthday(userDAO.getBirthday());
        user.setWeight(userDAO.getWeight());
        user.setRemarks(userDAO.getRemarks());
        return user;
    }
}