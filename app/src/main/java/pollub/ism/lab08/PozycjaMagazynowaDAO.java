package pollub.ism.lab08;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface PozycjaMagazynowaDAO {
    @Insert
    void insert(PozycjaMagazynowa pozycja);

    @Update
    void update(PozycjaMagazynowa pozycja);

    @Query("SELECT QUANTITY FROM Warzywniak WHERE NAME= :wybraneWarzywoNazwa")
    int findQuantityByName(String wybraneWarzywoNazwa);

    @Query("UPDATE Warzywniak SET QUANTITY = :wybraneWarzywoNowaIlosc WHERE NAME = :wybraneWarzywoNazwa")
    void updateQuantityByName(String wybraneWarzywoNazwa, int wybraneWarzywoNowaIlosc);

    @Query("SELECT COUNT(*) FROM WARZYWNIAK")
    int size();

}
