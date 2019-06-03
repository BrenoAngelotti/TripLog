package com.angelotti.triplog.Persistence;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Update;
import android.arch.persistence.room.Query;
import com.angelotti.triplog.Model.Trip;
import java.util.List;

@Dao
public interface TripDAO {

    @Insert
    long insert(Trip trip);

    @Delete
    void delete(Trip trip);

    @Update
    void update(Trip trip);

    @Query("SELECT * FROM trips WHERE id = :id")
    Trip getById(int id);

    @Query("SELECT * FROM trips WHERE title like '%' || :search || '%' ORDER BY title ASC")
    List<Trip> getByName(String search);

    @Query("SELECT * FROM trips WHERE typeId = :typeId ORDER BY title ASC")
    List<Trip> getByType(int typeId);

    @Query("SELECT * FROM trips ORDER BY title ASC")
    List<Trip> getAll();
}
