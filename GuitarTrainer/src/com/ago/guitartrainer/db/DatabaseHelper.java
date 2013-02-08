package com.ago.guitartrainer.db;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ago.guitartrainer.R;
import com.ago.guitartrainer.lessons.ILesson;
import com.ago.guitartrainer.lessons.LessonMetrics;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
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
    private static final int DATABASE_VERSION = 16;

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

    public LessonMetrics findLessonMetrics(Class<? extends ILesson> lessonClazz) {
        RuntimeExceptionDao<LessonMetrics, Integer> dao = DatabaseHelper.getInstance().getRuntimeExceptionDao(
                LessonMetrics.class);
        LessonMetrics result = null;
        try {
            List<LessonMetrics> results = dao.queryBuilder().where().eq("lessonClazz", lessonClazz.getSimpleName())
                    .query();
            result = (results.size() > 0) ? results.get(0) : null;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;
    }
}
