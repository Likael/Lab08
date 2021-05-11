package pollub.ism.lab08;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface HistoriaPozycjiMagazynowejDAO {
    @Insert
    void insert(HistoriaPozycjiMagazynowej pozycja);

    @Update
    void update(HistoriaPozycjiMagazynowej pozycja);

    @Query("SELECT * FROM HISTORIA WHERE NAME = :wybraneWarzywoNazwa")
    List<HistoriaPozycjiMagazynowej> findHistoryByName(String wybraneWarzywoNazwa);

    @Query("SELECT COUNT(*) FROM Historia WHERE NAME = :wybraneWarzywoNazwa")
    int size(String wybraneWarzywoNazwa);
}
