package pollub.ism.lab08;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import pollub.ism.lab08.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private String wybraneWarzywoNazwa = null;
    private Integer wybraneWarzywoIlosc = null;
    private ActivityMainBinding binding;
    private ArrayAdapter<CharSequence> adapter;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.getDefault());

    public enum OperacjaMagazynowa {SKLADUJ, WYDAJ}

    private BazaMagazynowa bazaDanych;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        bazaDanych = Room.databaseBuilder(getApplicationContext(), BazaMagazynowa.class, BazaMagazynowa.NAZWA_BAZY)
                .allowMainThreadQueries().fallbackToDestructiveMigration().build();
        if (bazaDanych.pozycjaMagazynowaDAO().size() == 0) {
            String[] asortyment = getResources().getStringArray(R.array.Asortyment);
            for (String nazwa : asortyment) {
                PozycjaMagazynowa pozycjaMagazynowa = new PozycjaMagazynowa();
                pozycjaMagazynowa.NAME = nazwa;
                pozycjaMagazynowa.QUANTITY = 0;
                bazaDanych.pozycjaMagazynowaDAO().insert(pozycjaMagazynowa);
            }
        }
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        adapter = ArrayAdapter.createFromResource(this, R.array.Asortyment, android.R.layout.simple_dropdown_item_1line);
        binding.spinner.setAdapter(adapter);

        binding.addToDatabase.setOnClickListener(view -> zmienStan(OperacjaMagazynowa.SKLADUJ));

        binding.removeFromDatabase.setOnClickListener(view -> zmienStan(OperacjaMagazynowa.WYDAJ));

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                wybraneWarzywoNazwa = adapter.getItem(i).toString();
                aktualizuj();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void aktualizuj() {
        wybraneWarzywoIlosc = bazaDanych.pozycjaMagazynowaDAO().findQuantityByName(wybraneWarzywoNazwa);
        int ilosc = bazaDanych.historiaPozycjiMagazynowejDAO().size(wybraneWarzywoNazwa);
        binding.state.setText("Stan magazynu dla " + wybraneWarzywoNazwa + " wynosi: " + wybraneWarzywoIlosc);
        if (ilosc > 0) {
            StringBuilder sb = new StringBuilder();
            ArrayList<HistoriaPozycjiMagazynowej> historia = (ArrayList<HistoriaPozycjiMagazynowej>) bazaDanych.historiaPozycjiMagazynowejDAO().findHistoryByName(wybraneWarzywoNazwa);
            Date latestDate = null;
            try {
                latestDate = dateFormat.parse("01-01-1970 12:12:12");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            for (HistoriaPozycjiMagazynowej h : historia) {
                sb.append(h);
                try {
                    Date date = dateFormat.parse(h.DATE);
                    if (date != null && date.compareTo(latestDate) > 0) latestDate = date;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                sb.append("\n");
            }
            assert latestDate != null;
            String dateText = dateFormat.format(latestDate).replace(" ", "\n");
            binding.unit.setText(dateText);
            binding.history.setText(sb.toString());
        } else {
            binding.history.setText("");
            binding.unit.setText("");
        }
    }

    private void zmienStan(OperacjaMagazynowa operacja) {

        Integer zmianaIlosci, nowaIlosc = null;
        Calendar calendar = Calendar.getInstance();
        String now = dateFormat.format(calendar.getTime());
        System.out.println(now);
        HistoriaPozycjiMagazynowej historiaPozycjiMagazynowej = new HistoriaPozycjiMagazynowej();

        try {
            zmianaIlosci = Integer.parseInt(binding.qunatity.getText().toString());
        } catch (NumberFormatException ex) {
            return;
        } finally {
            binding.qunatity.setText("");
        }

        switch (operacja) {
            case SKLADUJ:
                nowaIlosc = wybraneWarzywoIlosc + zmianaIlosci;
                break;
            case WYDAJ:
                nowaIlosc = wybraneWarzywoIlosc - zmianaIlosci;
                break;
        }
        nowaIlosc = nowaIlosc > 0 ? nowaIlosc : 0;
        historiaPozycjiMagazynowej.NAME = wybraneWarzywoNazwa;
        historiaPozycjiMagazynowej.DATE = now;
        historiaPozycjiMagazynowej.OLD_QUANTITY = wybraneWarzywoIlosc;
        historiaPozycjiMagazynowej.NEW_QUANTITY = nowaIlosc;
        bazaDanych.historiaPozycjiMagazynowejDAO().insert(historiaPozycjiMagazynowej);
        bazaDanych.pozycjaMagazynowaDAO().updateQuantityByName(wybraneWarzywoNazwa, nowaIlosc);
        aktualizuj();
    }
}