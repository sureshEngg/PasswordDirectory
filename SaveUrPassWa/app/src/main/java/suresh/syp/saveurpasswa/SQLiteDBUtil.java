package suresh.syp.saveurpasswa;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Vector;

public class SQLiteDBUtil {
	
	public static SQLiteDatabase db;
	Context mContext;
	
	public SQLiteDBUtil(Context context) {
		// TODO Auto-generated constructor stub
		mContext = context;
	}
	
	public void CreateDB() {
		// TODO Auto-generated method stub
		try {
			if(!checkDatabase())
			{
				db = mContext.openOrCreateDatabase(Constant.DATABASENAME,
						SQLiteDatabase.CREATE_IF_NECESSARY, null);
				db.setVersion(1);
				db.setLocale(Locale.getDefault());
				db.setLockingEnabled(true);
				
				db.execSQL(Constant.PASSWORDS_TBL);
				db.execSQL(Constant.USER_TBL);

				//db.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		} 
	}
	
	public void insertIntoPasswordTbl(ArrayList<Object> data) {
		String query_passwwa = "INSERT OR REPLACE INTO "+Constant.PASSWORD_TBL_NAME+" values(\""
		+(String)data.get(0)+"\",\""
		+(String)data.get(1)+"\",\""
		+(String)data.get(2)+"\",\""
		+(String)data.get(3)+"\",\""
		+(String)data.get(4)+"\""
		+");";
		
		execQuaryInitialSync(mContext, query_passwwa);
	}
	
	public void insertIntoPinTbl(ArrayList<Object> data) {
		String query_user_pin = "INSERT INTO "+Constant.USER_TBL_NAME+" values(\""
				+(String)data.get(0)+"\""
				+");";
				
		execQuaryInitialSync(mContext, query_user_pin);
	}

	public boolean checkDatabase() {
		// TODO Auto-generated method stub
		SQLiteDatabase checkDB = null;
		try {
			checkDB = SQLiteDatabase.openDatabase(Constant.DATABASELOCATION,
					null, SQLiteDatabase.OPEN_READWRITE);
			//checkDB.close();
		} catch (SQLiteException e) {
			// database doesn't exist yet.
			return false;
		}
		return checkDB != null ? true : false;
	}

	public void OpenDB() {
		// TODO Auto-generated method stub
		try {
			db = mContext.openOrCreateDatabase(Constant.DATABASELOCATION, SQLiteDatabase.CREATE_IF_NECESSARY, null);
			db.setVersion(1);
			db.setLocale(Locale.getDefault());
			db.setLockingEnabled(true);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	// ****************** Exucute quary method*********************//
	public void execQuary(Context context, String quary) {
			try {
				db.execSQL(quary);
				db.close();
			} catch (Exception e) {
				System.out.println("error is quary running :-" + e.getMessage());
			}
		}
		
	public void execQuaryInitialSync(Context context, String quary) {
			try {
				System.out.println("the quary=================="+quary + "======="+context);
				db.execSQL(quary);
			} catch (Exception e) {
				System.out.println("######error is quary running :-" + e.getMessage());
			}
		}
	
		public Vector<Cursor> quaryData(Context context, String quary) {
			Vector<Cursor> v = new Vector<Cursor>();
			Cursor cur = null;
			try {
				cur = db.rawQuery(quary, null);
				v.add(cur);
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("error is quary running :-" + e.getMessage());
			}
			return v;
		}
		
		// get the values of the Animal from the database
		public ArrayList<String[]> getDataOnDemand(Context context,String query_data) {
			// TODO Auto-generated method stub
			ArrayList<String[]> animalArray = new ArrayList<String[]>();
			
			try {
				String quary = query_data;
				
				System.out.println("the Query ====="+quary);

				Vector<Cursor> cursorVector = quaryData(context, quary);
				System.out.println("quaryData==="+cursorVector);
				
				Cursor cur = cursorVector.get(0);
				cur.moveToFirst();
				
				while (cur.isAfterLast() == false) {
					try {
						String[] data = new String[cur.getColumnCount()];
						for(int index = 0 ; index < cur.getColumnCount();index++)
						{
							System.out.println("teh cur getColumnCount==="+cur.getColumnName(index));
							data[index] = cur.getString(index);
						}
						animalArray.add(data);
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					cur.moveToNext();
				}

				cur.close();
				/*if(db.isOpen())
				db.close();*/
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("the exceptionis :- " +e.getMessage());
			}
			
			return animalArray;
			
		}
		
	public void CloseDB() {
		// TODO Auto-generated method stub
		try {
			if (db.isOpen()) {
				System.out.println("1#1#database closing....");
				db.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("111###the exception at close DB:-"+e.getMessage());
		}
	}
}
