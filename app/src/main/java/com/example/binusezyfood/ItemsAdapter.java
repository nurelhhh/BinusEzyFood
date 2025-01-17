package com.example.binusezyfood;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    private final int[] ids;
    private final int[] type_ids;
    private final String[] names;
    private final int[] prices;
    private final int[] images;

    public ItemsAdapter(int[] ids, int[] type_ids, String[] names, int[] prices, int[] images) {
        this.ids = ids;
        this.type_ids = type_ids;
        this.names = names;
        this.prices = prices;
        this.images = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MaterialCardView cardView = (MaterialCardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);

        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LinearLayout layout = holder.linearLayout;
        holder.imageView.setImageResource(images[position]);
        holder.nameText.setText(names[position]);
        holder.priceText.setText(Utils.toRupiah(prices[position]));

        layout.setOnClickListener(v -> {
            Intent intent = new Intent(layout.getContext(), ItemDetailActivity.class);
            intent.putExtra("ID", ids[position]);
            intent.putExtra("TYPE_ID", type_ids[position]);
            intent.putExtra("NAME", names[position]);
            intent.putExtra("PRICE", prices[position]);
            intent.putExtra("IMAGE", images[position]);
            layout.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return names.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout linearLayout;
        private final ImageView imageView;
        private final TextView nameText;
        private final TextView priceText;

        public ViewHolder(@NonNull MaterialCardView cardView) {
            super(cardView);

            linearLayout = (LinearLayout) cardView.getChildAt(0);
            imageView = (ImageView) linearLayout.getChildAt(0);

            LinearLayout innerLayout = (LinearLayout) linearLayout.getChildAt(1);
            nameText = (TextView) innerLayout.getChildAt(0);
            priceText = (TextView) innerLayout.getChildAt(1);


        }
    }
}
