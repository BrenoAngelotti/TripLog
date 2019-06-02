package com.angelotti.triplog.Persistence;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Update;
import android.arch.persistence.room.Query;
import com.angelotti.triplog.Model.Type;
import java.util.List;

@Dao
public interface TypeDAO {

    @Insert
    long insert(Type type);

    @Delete
    void delete(Type type);

    @Update
    void update(Type type);

    @Query("SELECT * FROM types ORDER BY name ASC")
    List<Type> getAll();

    @Query("SELECT * FROM types WHERE id = :id")
    Type getById(int id);
}
