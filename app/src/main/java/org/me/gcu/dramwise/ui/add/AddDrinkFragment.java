package org.me.gcu.dramwise.ui.add;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.me.gcu.dramwise.R;
import org.me.gcu.dramwise.data.DrinkEntry;
import org.me.gcu.dramwise.data.DrinkRepository;
import org.me.gcu.dramwise.util.UnitsCalculator;

public class AddDrinkFragment extends Fragment {

    private EditText etName, etVolume, etAbv;
    private TextView tvPreview;
    private Button btnSave;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_drink, container, false);

        etName = v.findViewById(R.id.et_name);
        etVolume = v.findViewById(R.id.et_volume);
        etAbv = v.findViewById(R.id.et_abv);
        tvPreview = v.findViewById(R.id.tv_preview);
        btnSave = v.findViewById(R.id.btn_save);

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { updatePreview(); }
            @Override public void afterTextChanged(Editable s) {}
        };

        etVolume.addTextChangedListener(watcher);
        etAbv.addTextChangedListener(watcher);

        btnSave.setOnClickListener(view -> save());

        updatePreview();
        return v;
    }

    private void updatePreview() {
        double volume = parseDouble(etVolume.getText().toString());
        double abv = parseDouble(etAbv.getText().toString());
        double units = UnitsCalculator.calculateUnits(volume, abv);
        tvPreview.setText(getString(R.string.preview_units, units));
    }

    private void save() {
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

        Toast.makeText(requireContext(), getString(R.string.saved_message), Toast.LENGTH_SHORT).show();

        etName.setText("");
        etVolume.setText("");
        etAbv.setText("");
        updatePreview();
    }

    private double parseDouble(String s) {
        try {
            if (s == null) return 0.0;
            s = s.trim();
            if (s.isEmpty()) return 0.0;
            return Double.parseDouble(s);
        } catch (Exception e) {
            return 0.0;
        }
    }
}