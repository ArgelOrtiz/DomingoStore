package com.tec.domingostore.DB;

import android.content.Context;
import android.content.SharedPreferences;

import com.tec.domingostore.DB.Entities.DaoMaster;
import com.tec.domingostore.DB.Entities.DaoSession;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.WhereCondition;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class DAOAccess {

    private static DaoSession daoSession;
    private static final int DB_VERSION         = 9;
    public static final String PACKAGE_NAME     = "com.tec.domingostore";
    private static final String PACKAGE_NAME_DB = PACKAGE_NAME+".DB.Entities.";

    private static DAOAccess _sharedInstance;


    static public DAOAccess sharedInstance(){
        if (_sharedInstance == null) {
            _sharedInstance = new DAOAccess();
        }
        return _sharedInstance;
    }

    public static void setup(Context context){

        //Do this once, for example in your Application class.
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "logy-store-db", null);
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();

        //Update schema if db version changes.
        SharedPreferences sharedPref = context.getSharedPreferences(PACKAGE_NAME,Context.MODE_PRIVATE);
        int current_version = sharedPref.getInt("DB_VERSION", 0);
        if (current_version != DB_VERSION) {
            if (current_version != 0) {
                DaoMaster.dropAllTables(db, true);
                DaoMaster.createAllTables(db, false);

            }

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("DB_VERSION", DB_VERSION);
            editor.apply();
        }
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
