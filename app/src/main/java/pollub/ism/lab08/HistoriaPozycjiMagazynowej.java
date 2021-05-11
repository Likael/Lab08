package pollub.ism.lab08;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Historia")
public class HistoriaPozycjiMagazynowej {

    @PrimaryKey(autoGenerate = true)
    public int _id;
    public String NAME;
    public String DATE;
    public int OLD_QUANTITY;
    public int NEW_QUANTITY;

    @NonNull
    @Override
    public String toString() {
        return DATE + " " + OLD_QUANTITY + " => " + NEW_QUANTITY;
    }
}
