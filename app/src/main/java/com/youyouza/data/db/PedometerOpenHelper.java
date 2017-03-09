package com.youyouza.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class PedometerOpenHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "step.db";
    private static final int DATABASE_VERSION = 1;
    private static final String STEP_TABLE_NAME = "step";



	/**
	 * 创建step表
	 */
	public static final String CREATE_STEP = "create table  "+STEP_TABLE_NAME+"("
            + "id integer primary key autoincrement,"
            + "date integer ,"
			+ "total_step integer,"
			+ "step_in_hand integer,"
            + "step_in_pocket integer,"
            + "step_in_run integer)";


	/**
	 * 带参数的PedometerOpenHelper构造函数
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 */
	public PedometerOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

    public PedometerOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


	@Override
	/**
	 * 创建数据库
	 */
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_STEP);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS notes");
        onCreate(db);
	}

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }


}
