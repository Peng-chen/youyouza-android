package com.youyouza.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.youyouza.data.bean.Step;

import java.util.ArrayList;
import java.util.List;

/**
 * 对数据库pedometer里的各个表进行增删改查
 * 
 * @author youyouza
 * 
 */
public class PedometerDB {

	private static PedometerDB pedometerDB;

	private SQLiteDatabase db;

    private static final String STEP_TABLE_NAME = "step";

    private static final String DATABASE_NAME = "step.db";

//    public static final String CREATE_STEP = "create table  "+STEP_TABLE_NAME+"("
//            + "id integer primary key autoincrement,"
//            + "date integer ,"
//            + "total_step integer,"
//            + "step_in_hand integer,"
//            + "step_in_pocket integer,"
//            + "step_in_run integer)";

    String stepData[]={"id","date","total_step","step_in_hand","step_in_pocket","step_in_run"};

	/**
	 * 将PedometerDB的构造方法设置为私有方法，在别的类里不能通过new来创建这个对象
	 * 
	 *
	 */
	private PedometerDB(Context context) {
		PedometerOpenHelper pHelper = new PedometerOpenHelper(context);
		db = pHelper.getWritableDatabase();
	}

	/**
	 * 使用单例模式创建数据库
	 */
	public synchronized static PedometerDB getInstance(Context context) {
		if (pedometerDB == null) {
			pedometerDB = new PedometerDB(context);
		}
		return pedometerDB;
	}


	/**
	 * 增加step表里的数据
	 * 
	 */
	public void saveStep(Step step) {

		if (step != null) {
			ContentValues values = new ContentValues();
            values.put("date", step.getDate());
            values.put("total_step", step.getTotal_step());
            values.put("step_in_hand", step.getStep_in_hand());
            values.put("step_in_pocket", step.getStep_pocket());
            values.put("step_in_run", step.getStep_in_run());
			 db.insert(STEP_TABLE_NAME, null, values);
		}
	}

	/**
	 * 升级step表里的数据
	 *  String stepData[]={"id","date","total_step","step_in_hand","step_in_pocket","step_in_run"};
	 */
	public void updateStep(Step step) {
		if (step != null) {
			ContentValues values = new ContentValues();
            values.put("date", step.getDate());
            values.put("total_step", step.getTotal_step());
            values.put("step_in_hand", step.getStep_in_hand());
            values.put("step_in_pocket", step.getStep_pocket());
            values.put("step_in_run", step.getStep_in_run());

			db.update(STEP_TABLE_NAME, values, "id = ? and date = ?", new String[] {
					step.getId()+"", step.getDate() });
		}
	}

    /**
     * 升级step表里指定时间的数据
     *  String stepData[]={"id","date","total_step","step_in_hand","step_in_pocket","step_in_run"};
     */
    public void updateStepWithTime(Step step) {
        if (step != null) {
            ContentValues values = new ContentValues();
            values.put("date", step.getDate());
            values.put("total_step", step.getTotal_step());
            values.put("step_in_hand", step.getStep_in_hand());
            values.put("step_in_pocket", step.getStep_pocket());
            values.put("step_in_run", step.getStep_in_run());

            db.update(STEP_TABLE_NAME, values, " date = ?", new String[] {
                    step.getDate() });
        }
    }


	/**
	 * id和date来取数据
	 * String stepData[]={"id","date","total_step","step_in_hand","step_in_pocket","step_in_run"};
	 */
	public Step loadSteps(String id, String date) {
		Step step = null;
		Cursor cursor = db.query(STEP_TABLE_NAME, null, "id = ? and date = ?",
				new String[] { id, date }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				step = new Step();
                step.setId(cursor.getInt(cursor.getColumnIndex("id")));
                step.setDate(cursor.getString(cursor.getColumnIndex("date")));
                step.setTotal_step(cursor.getInt(cursor.getColumnIndex("total_step")));
                step.setStep_in_hand(cursor.getInt(cursor.getColumnIndex("step_in_hand")));
                step.setStep_pocket(cursor.getInt(cursor.getColumnIndex("step_in_pocket")));
                step.setStep_in_run(cursor.getInt(cursor.getColumnIndex("step_in_run")));

			} while (cursor.moveToNext());

		} else {
			Log.i("tag", "step is null!");
		}
		cursor.close();

		return step;
	}

    /**
     * date来取数据
     * String stepData[]={"id","date","total_step","step_in_hand","step_in_pocket","step_in_run"};
     */
    public Step loadStepsWithTime(String date) {
        Step step = null;
        Cursor cursor = db.query(STEP_TABLE_NAME, null, " date = ?",
                new String[] {  date }, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                step = new Step();
                step.setId(cursor.getInt(cursor.getColumnIndex("id")));
                step.setDate(cursor.getString(cursor.getColumnIndex("date")));
                step.setTotal_step(cursor.getInt(cursor.getColumnIndex("total_step")));
                step.setStep_in_hand(cursor.getInt(cursor.getColumnIndex("step_in_hand")));
                step.setStep_pocket(cursor.getInt(cursor.getColumnIndex("step_in_pocket")));
                step.setStep_in_run(cursor.getInt(cursor.getColumnIndex("step_in_run")));

            } while (cursor.moveToNext());

        } else {
            Log.i("tag", "step is null!");
        }
        cursor.close();

        return step;
    }


	/**
	 * date来取数据
	 * String stepData[]={"id","date","total_step","step_in_hand","step_in_pocket","step_in_run"};
	 */
	public List<Step> loadStepsofToday(String date) {
		List<Step> list = new ArrayList<>();
		Cursor cursor = db.query(STEP_TABLE_NAME, null, " date LIKE ?",
				new String[] {  date+"%" }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				Step step = new Step();
				step.setId(cursor.getInt(cursor.getColumnIndex("id")));
				step.setDate(cursor.getString(cursor.getColumnIndex("date")));
				step.setTotal_step(cursor.getInt(cursor.getColumnIndex("total_step")));
				step.setStep_in_hand(cursor.getInt(cursor.getColumnIndex("step_in_hand")));
				step.setStep_pocket(cursor.getInt(cursor.getColumnIndex("step_in_pocket")));
				step.setStep_in_run(cursor.getInt(cursor.getColumnIndex("step_in_run")));

				list.add(step);

//				Log.i("db--->","get one data"+step.getDate());

			} while (cursor.moveToNext());

		} else {
			Log.i("tag", "step is null!");
		}
		cursor.close();

		return list;
	}

	/**
	 * date来取数据
	 * String stepData[]={"id","date","total_step","step_in_hand","step_in_pocket","step_in_run"};
	 */
	public List<Step> loadStepsofWeek(String dateBefore,String dateAfter) {
		List<Step> list = new ArrayList<>();

		Log.i("DB---->",dateBefore+"  "+dateAfter);

		Cursor cursor = db.query(STEP_TABLE_NAME, null, " date >? and date <?",
				new String[] {  dateBefore,dateAfter }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				Step step = new Step();
				step.setId(cursor.getInt(cursor.getColumnIndex("id")));
				step.setDate(cursor.getString(cursor.getColumnIndex("date")));
				step.setTotal_step(cursor.getInt(cursor.getColumnIndex("total_step")));
				step.setStep_in_hand(cursor.getInt(cursor.getColumnIndex("step_in_hand")));
				step.setStep_pocket(cursor.getInt(cursor.getColumnIndex("step_in_pocket")));
				step.setStep_in_run(cursor.getInt(cursor.getColumnIndex("step_in_run")));

				list.add(step);

//				Log.i("db--->","get one data"+step.getDate()+"  "+step.getTotal_step());

			} while (cursor.moveToNext());

		} else {
			Log.i("tag", "step is null!");
		}
		cursor.close();

		return list;
	}


	/**
	 * date来取数据
	 * String stepData[]={"id","date","total_step","step_in_hand","step_in_pocket","step_in_run"};
	 */
	public List<Step> loadStepsofDate(String dateBefore,String dateAfter) {
		List<Step> list = new ArrayList<>();

		Log.i("DB---->",dateBefore+"  "+dateAfter);

		Cursor cursor = db.query(STEP_TABLE_NAME, null, " date >? and date <?",
				new String[] {  dateBefore,dateAfter }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				Step step = new Step();
				step.setId(cursor.getInt(cursor.getColumnIndex("id")));
				step.setDate(cursor.getString(cursor.getColumnIndex("date")));
				step.setTotal_step(cursor.getInt(cursor.getColumnIndex("total_step")));
				step.setStep_in_hand(cursor.getInt(cursor.getColumnIndex("step_in_hand")));
				step.setStep_pocket(cursor.getInt(cursor.getColumnIndex("step_in_pocket")));
				step.setStep_in_run(cursor.getInt(cursor.getColumnIndex("step_in_run")));

				list.add(step);

//				Log.i("db--->","get one data"+step.getDate()+"  "+step.getTotal_step());

			} while (cursor.moveToNext());

		} else {
			Log.i("tag", "step is null!");
		}
		cursor.close();

		return list;
	}


	/**
	 * 更具date取出所有的step数据
	 * 
	 */

	public List<Step> loadListSteps() {
		List<Step> list = new ArrayList<>();

		Cursor cursor = db.rawQuery("select * from  "+STEP_TABLE_NAME+" order by date desc",
				null);
		if (cursor.moveToFirst()) {
			do {
				Step step = new Step();
                step.setId(cursor.getInt(cursor.getColumnIndex("id")));
                step.setDate(cursor.getString(cursor.getColumnIndex("date")));
                step.setTotal_step(cursor.getInt(cursor.getColumnIndex("total_step")));
                step.setStep_in_hand(cursor.getInt(cursor.getColumnIndex("step_in_hand")));
                step.setStep_pocket(cursor.getInt(cursor.getColumnIndex("step_in_pocket")));
                step.setStep_in_run(cursor.getInt(cursor.getColumnIndex("step_in_run")));
				list.add(step);

			} while (cursor.moveToNext());

		}
		cursor.close();

		return list;
	}



	public void DeleteStepWithTime(String stringTime) {

        Log.i("db","delete time is :"+stringTime);

		if (stringTime.length()>1) {
			db.delete(STEP_TABLE_NAME, " date<?", new String[] {stringTime});
		}
	}


}
