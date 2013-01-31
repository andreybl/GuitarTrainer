package com.ago.guitartrainer.db;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ago.guitartrainer.R;
import com.ago.guitartrainer.lessons.QuestionMetrics;
import com.ago.guitartrainer.lessons.custom.QuestionScalegridDegree2Position;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private final String TAG = "GT-" + DatabaseHelper.class.getName();

    private static DatabaseHelper INSTANCE;

    // name of the database file for your application -- change to something appropriate for your app
    private static final String DATABASE_NAME = "guitartrainer.db";

    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 13;

    // the DAO object we use to access the SimpleData table
    private Dao<QuestionScalegridDegree2Position, Integer> simpleDao = null;
    private RuntimeExceptionDao<QuestionScalegridDegree2Position, Integer> simpleRuntimeDao = null;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    /**
     * Create and initialize the DatabseHelper. Is usually called once.
     * 
     * @param context
     */
    public static void initDatabaseHelperInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DatabaseHelper(context);
        }
    }

    public static DatabaseHelper getInstance() {
        if (INSTANCE == null)
            throw new NullPointerException("The DatabaseHelper must be initialized before getIntance() called");
        return INSTANCE;
    }

    /**
     * This is called when the database is first created. Usually you should call createTable statements here to create
     * the tables that will store your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(TAG, "onCreate");
            for (Class<?> clazz : DatabaseConfigUtil.classes) {
                TableUtils.createTable(connectionSource, clazz);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Can't create database", e);
            throw new RuntimeException(e);
        }

    }

    /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
     * the various data to match the new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(TAG, "onUpgrade");
            for (Class<?> clazz : DatabaseConfigUtil.classes) {
                TableUtils.dropTable(connectionSource, clazz, true);
            }
            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(TAG, "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    public void resetData() {
        try {
            Log.i(TAG, "resetData");
            for (Class<?> clazz : DatabaseConfigUtil.classes) {
                TableUtils.clearTable(getConnectionSource(), clazz);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Can't reset databases data", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the Database Access Object (DAO) for our QuestionScalegridDegree2Position class. It will create it or
     * just give the cached value.
     */
    public Dao<QuestionScalegridDegree2Position, Integer> getDao() throws SQLException {
        if (simpleDao == null) {
            simpleDao = getDao(QuestionScalegridDegree2Position.class);
        }
        return simpleDao;
    }

    /**
     * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our SimpleData class. It will
     * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
     */
    public RuntimeExceptionDao<QuestionScalegridDegree2Position, Integer> getDao2() {
        if (simpleRuntimeDao == null) {
            simpleRuntimeDao = getRuntimeExceptionDao(QuestionScalegridDegree2Position.class);
        }
        return simpleRuntimeDao;
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
        simpleRuntimeDao = null;
    }
}
