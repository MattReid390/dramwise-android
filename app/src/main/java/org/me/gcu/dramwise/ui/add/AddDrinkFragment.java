package org.me.gcu.dramwise.ui.add;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

                    List<SectionedDrinkAdapter.SpinnerItem> items = new ArrayList<>();
                    items.add(new SectionedDrinkAdapter.SpinnerItem(
                            getString(R.string.select_predefined_drink), false));

                    String[] categories   = {"Beer",  "Wine",  "Spirit",  "Cocktail"};
                    String[] sectionLabels = {"Beers", "Wines", "Spirits", "Cocktails"};

                    if (drinkTypes != null) {
                        for (int i = 0; i < categories.length; i++) {
                            boolean headerAdded = false;
                            for (DrinkType dt : drinkTypes) {
                                if (categories[i].equals(dt.category)) {
                                    if (!headerAdded) {
                                        items.add(new SectionedDrinkAdapter.SpinnerItem(
                                                sectionLabels[i], true));
                                        headerAdded = true;
                                    }
                                    items.add(new SectionedDrinkAdapter.SpinnerItem(dt));
                                }
                            }
                        }
                    }

                    SectionedDrinkAdapter adapter =
                            new SectionedDrinkAdapter(requireContext(), items);
                    spinnerDrinkType.setAdapter(adapter);

                    spinnerDrinkType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            SectionedDrinkAdapter.SpinnerItem item = adapter.getItem(position);
                            if (item.drinkType == null) return; // prompt or header

                            etName.setText(item.drinkType.name);
                            etVolume.setText(String.format(Locale.UK, "%.0f", item.drinkType.defaultVolumeMl));
                            etAbv.setText(String.format(Locale.UK, "%.1f", item.drinkType.abv));
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
        tvPreview.setText(String.format(Locale.UK, "%.2f", units));
    }

    private void saveDrink() {
        String name = etName.getText().toString().trim();
        double volume = parseDouble(etVolume.getText().toString());
        double abv = parseDouble(etAbv.getText().toString());

        if (name.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.enter_drink_name), Toast.LENGTH_SHORT).show(); // FIXED
            return;
        }

        if (volume <= 0 || abv <= 0) {
            Toast.makeText(requireContext(), getString(R.string.enter_valid_volume_abv), Toast.LENGTH_SHORT).show(); // FIXED
            return;
        }

        double units = UnitsCalculator.calculateUnits(volume, abv);
        DrinkEntry entry = new DrinkEntry(name, volume, abv, units, System.currentTimeMillis());

        DrinkRepository.getInstance(requireContext()).insert(entry);

        Toast.makeText(requireContext(), getString(R.string.drink_saved), Toast.LENGTH_SHORT).show(); // FIXED

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