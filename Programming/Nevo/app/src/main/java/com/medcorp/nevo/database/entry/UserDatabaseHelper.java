package com.medcorp.nevo.database.entry;

import android.content.Context;

import com.medcorp.nevo.database.DatabaseHelper;
import com.medcorp.nevo.database.dao.UserDAO;
import com.medcorp.nevo.model.User;

import net.medcorp.library.ble.util.Optional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by karl-john on 17/11/15.
 */
public class UserDatabaseHelper implements iEntryDatabaseHelper<User> {

    private DatabaseHelper databaseHelper;

    public UserDatabaseHelper(Context context) {
        databaseHelper = DatabaseHelper.getInstance(context);
    }

    @Override
    public Optional<User> add(User object) {
        Optional<User> userOptional = new Optional<>();
        try {
            UserDAO res = databaseHelper.getUserDao().createIfNotExists(convertToDao(object));
            if(res!=null)
            {
                //here set the ID which comes from database
                object.setId(res.getID());
                userOptional.set(convertToNormal(res));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userOptional;
    }

    @Override
    public boolean update(User object) {
        int result = -1;
        try {
            List<UserDAO> userDAOList = databaseHelper.getUserDao().queryBuilder().where().eq(UserDAO.fID, object.getId()).query();
            if(userDAOList.isEmpty()){
                return add(object)!=null;
            }
            UserDAO daoObject = convertToDao(object);
            daoObject.setID(userDAOList.get(0).getID());
            result = databaseHelper.getUserDao().update(daoObject);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result>=0;
    }

    @Override
    public boolean remove(int id,Date date) {
        try {
            List<UserDAO> userDAOList = databaseHelper.getUserDao().queryBuilder().where().eq(UserDAO.fID, id).query();
            if(!userDAOList.isEmpty())
            {
                return databaseHelper.getUserDao().delete(userDAOList)>=0;                
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Optional<User>> get(int userId) {
        List<Optional<User>> userList = new ArrayList<Optional<User>>();
        try {
            List<UserDAO> userDAOList = databaseHelper.getUserDao().queryBuilder().where().eq(UserDAO.fID, userId).query();
            for(UserDAO userDAO: userDAOList) {
                Optional<User> userOptional = new Optional<>();
                userOptional.set(convertToNormal(userDAO));
                userList.add(userOptional);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userList;
    }

    @Override
    public Optional<User> get(int id,Date date) {
        List<Optional<User>> userList = get(id);
        return userList.isEmpty()?new Optional<User>(): userList.get(0);
    }

    @Override
    public List<Optional<User>> getAll() {
        List<Optional<User>> userList = new ArrayList<Optional<User>>();
        try {
            List<UserDAO> userDAOList  = databaseHelper.getUserDao().queryBuilder().query();
            for(UserDAO userDAO: userDAOList) {
                Optional<User> userOptional = new Optional<>();
                userOptional.set(convertToNormal(userDAO));
                userList.add(userOptional);
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
        userDAO.setFirstName(user.getFirstName());
        userDAO.setLastName(user.getLastName());
        userDAO.setSex(user.getSex());
        return userDAO;
    }

    private User convertToNormal(UserDAO userDAO){
        User user = new User(userDAO.getCreatedDate());
        user.setId(userDAO.getID());
        user.setAge(userDAO.getAge());
        user.setHeight(userDAO.getHeight());
        user.setBirthday(userDAO.getBirthday());
        user.setWeight(userDAO.getWeight());
        user.setRemarks(userDAO.getRemarks());
        user.setFirstName(userDAO.getFirstName());
        user.setLastName(userDAO.getLastName());
        user.setSex(userDAO.getSex());
        return user;
    }

    @Override
    public List<User> convertToNormalList(List<Optional<User>> optionals) {
        List<User> userList = new ArrayList<>();
        for (Optional<User> userOptional: optionals) {
            if (userOptional.notEmpty()){
                userList.add(userOptional.get());
            }
        }
        return userList;
    }
}