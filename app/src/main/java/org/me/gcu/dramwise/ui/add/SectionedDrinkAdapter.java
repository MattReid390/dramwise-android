package org.me.gcu.dramwise.ui.add;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.me.gcu.dramwise.R;
import org.me.gcu.dramwise.data.DrinkType;

import java.util.List;

/**
 * Custom Spinner adapter that supports non-selectable section headers
 * between groups of drink items (Beers, Wines, Spirits, Cocktails).
 */
public class SectionedDrinkAdapter extends BaseAdapter {

    public static class SpinnerItem {
        public final boolean isHeader;
        public final String label;
        public final DrinkType drinkType;

        /** Section header — not selectable. */
        public SpinnerItem(String label, boolean isHeader) {
            this.isHeader = isHeader;
            this.label = label;
            this.drinkType = null;
        }

        /** Selectable drink item. */
        public SpinnerItem(DrinkType drinkType) {
            this.isHeader = false;
            this.label = drinkType.name;
            this.drinkType = drinkType;
        }
    }

    private final Context context;
    private final List<SpinnerItem> items;

    public SectionedDrinkAdapter(Context context, List<SpinnerItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public SpinnerItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /** Section headers are not selectable. */
    @Override
    public boolean isEnabled(int position) {
        return !items.get(position).isHeader;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    /** Closed spinner view — always shows plain item text. */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv = (TextView) LayoutInflater.from(context)
                .inflate(R.layout.spinner_item_drink, parent, false);
        tv.setText(items.get(position).label);
        return tv;
    }

    /** Dropdown view — section headers styled distinctly from items. */
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        SpinnerItem item = items.get(position);
        if (item.isHeader) {
            TextView tv = (TextView) LayoutInflater.from(context)
                    .inflate(R.layout.spinner_item_header, parent, false);
            tv.setText(item.label);
            return tv;
        } else {
            TextView tv = (TextView) LayoutInflater.from(context)
                    .inflate(R.layout.spinner_item_drink, parent, false);
            tv.setText(item.label);
            return tv;
        }
    }
}
