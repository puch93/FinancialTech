package kr.co.core.financial_tech.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import kr.co.core.financial_tech.R;
import kr.co.core.financial_tech.data.BoardAdminData;

public class BoardAdminAdapter extends RecyclerView.Adapter<BoardAdminAdapter.ViewHolder> {
    private ArrayList<BoardAdminData> list;
    private Activity act;

    public BoardAdminAdapter(Activity act, ArrayList<BoardAdminData> list) {
        this.act = act;
        this.list = list;
    }

    @NonNull
    @Override
    public BoardAdminAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sports_detail, parent, false);
        BoardAdminAdapter.ViewHolder viewHolder = new BoardAdminAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BoardAdminAdapter.ViewHolder holder, int i) {
        final BoardAdminData data = list.get(i);
        if (data.getType().equalsIgnoreCase("text")) {
            holder.textView.setVisibility(View.VISIBLE);
            holder.imageView.setVisibility(View.INVISIBLE);

            holder.textView.setText(data.getContents());
        } else {
            holder.textView.setVisibility(View.INVISIBLE);
            holder.imageView.setVisibility(View.VISIBLE);

            Glide.with(act)
                    .load(data.getContents())
                    .into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(ArrayList<BoardAdminData> list) {
        this.list = list;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;

        ViewHolder(@NonNull View view) {
            super(view);
            textView = view.findViewById(R.id.textView);
            imageView = view.findViewById(R.id.imageView);
        }
    }
}
