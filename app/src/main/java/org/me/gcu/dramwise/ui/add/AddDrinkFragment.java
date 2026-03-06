package org.me.gcu.dramwise.ui.add;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.me.gcu.dramwise.R;
import org.me.gcu.dramwise.data.DrinkEntry;
import org.me.gcu.dramwise.data.DrinkRepository;
import org.me.gcu.dramwise.data.DrinkType;
import org.me.gcu.dramwise.util.UnitsCalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddDrinkFragment extends Fragment {

    private Spinner spinnerDrinkType;
    private EditText etName, etVolume, etAbv;
    private TextView tvPreview;
    private Button btnSave;

    private final List<DrinkType> drinkTypeList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_drink, container, false);

        spinnerDrinkType = v.findViewById(R.id.spinner_drink_type);
        etName = v.findViewById(R.id.et_name);
        etVolume = v.findViewById(R.id.et_volume);
        etAbv = v.findViewById(R.id.et_abv);
        tvPreview = v.findViewById(R.id.tv_preview);
        btnSave = v.findViewById(R.id.btn_save);

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updatePreview();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        };

        etVolume.addTextChangedListener(watcher);
        etAbv.addTextChangedListener(watcher);

        btnSave.setOnClickListener(view -> saveDrink());

        loadDrinkTypes();
        updatePreview();

        return v;
    }

    private void loadDrinkTypes() {
        DrinkRepository.getInstance(requireContext()).getAllDrinkTypes()
                .observe(getViewLifecycleOwner(), drinkTypes -> {
                    drinkTypeList.clear();

                    List<String> names = new ArrayList<>();
                    names.add("Select a predefined drink");

                    if (drinkTypes != null) {
                        drinkTypeList.addAll(drinkTypes);
                        for (DrinkType drinkType : drinkTypes) {
                            names.add(drinkType.name);
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            names
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerDrinkType.setAdapter(adapter);

                    spinnerDrinkType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position == 0) return;

                            DrinkType selected = drinkTypeList.get(position - 1);
                            etName.setText(selected.name);
                            etVolume.setText(String.format(Locale.UK, "%.0f", selected.defaultVolumeMl));
                            etAbv.setText(String.format(Locale.UK, "%.1f", selected.abv));
                            updatePreview();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) { }
                    });
                });
    }

    private void updatePreview() {
        double volume = parseDouble(etVolume.getText().toString());
        double abv = parseDouble(etAbv.getText().toString());
        double units = UnitsCalculator.calculateUnits(volume, abv);
        tvPreview.setText(String.format(Locale.UK, "Preview units: %.2f", units));
    }

    private void saveDrink() {
        String name = etName.getText().toString().trim();
        double volume = parseDouble(etVolume.getText().toString());
        double abv = parseDouble(etAbv.getText().toString());

        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Enter a drink name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (volume <= 0 || abv <= 0) {
            Toast.makeText(requireContext(), "Enter valid volume and ABV", Toast.LENGTH_SHORT).show();
            return;
        }

        double units = UnitsCalculator.calculateUnits(volume, abv);
        DrinkEntry entry = new DrinkEntry(name, volume, abv, units, System.currentTimeMillis());

        DrinkRepository.getInstance(requireContext()).insert(entry);

        Toast.makeText(requireContext(), "Drink saved", Toast.LENGTH_SHORT).show();

        etName.setText("");
        etVolume.setText("");
        etAbv.setText("");
        spinnerDrinkType.setSelection(0);
        updatePreview();
    }

    private double parseDouble(String value) {
        try {
            if (value == null || value.trim().isEmpty()) {
                return 0.0;
            }
            return Double.parseDouble(value.trim());
        } catch (Exception e) {
            return 0.0;
        }
    }
}