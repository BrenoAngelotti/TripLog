package com.angelotti.triplog.Persistence;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.support.annotation.NonNull;
import com.angelotti.triplog.Model.Trip;
import com.angelotti.triplog.Model.Type;
import com.angelotti.triplog.R;

import java.util.concurrent.Executors;

@Database(entities = {Type.class, Trip.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract TypeDAO typeDAO();

    public abstract TripDAO tripDAO();

    private static AppDatabase instance;

    public static AppDatabase getDatabase(final Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    Builder builder =  Room.databaseBuilder(context,
                            AppDatabase.class,
                            "triplogDB.db").allowMainThreadQueries();

                    builder.addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                                @Override
                                public void run() {
                                    loadInitialTypes(context);
                                }
                            });
                        }
                    });
                    instance = (AppDatabase) builder.build();
                }
            }
        }
        return instance;
    }

    private static void loadInitialTypes(final Context context){
        String[] names = context.getResources().getStringArray(R.array.type_names);
        String[] colors = context.getResources().getStringArray(R.array.type_colors);

        for(int i = 0; i < names.length; i++)
            instance.typeDAO().insert(new Type(names[i], colors[i]));
    }

}
