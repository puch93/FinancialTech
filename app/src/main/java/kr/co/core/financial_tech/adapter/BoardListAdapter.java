package kr.co.core.financial_tech.adapter;

import android.app.Activity;
import android.content.Intent;
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
import kr.co.core.financial_tech.activity.BoardDetailAct;
import kr.co.core.financial_tech.data.BoardData;
import kr.co.core.financial_tech.util.StringUtil;

public class BoardListAdapter extends RecyclerView.Adapter<BoardListAdapter.ViewHolder> {
    private Activity act;
    private ArrayList<BoardData> list;

    public boolean isSearch_result() {
        return search_result;
    }

    private boolean search_result = false;

    public void setSearch_result(boolean search_result) {
        this.search_result = search_result;
    }

    public BoardListAdapter(Activity act, ArrayList<BoardData> list) {
        this.act = act;
        this.list = list;
    }

    public void setList(ArrayList<BoardData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public ArrayList<BoardData> getList() {
        return list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_borad_category_list, parent, false);
        return new BoardListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        BoardData data = list.get(i);

        holder.title.setText(data.getTitle());
        holder.nick.setText(data.getNick());
        holder.view_count.setText(data.getView_count());
        holder.review_count.setText(data.getComment_count());
        holder.date.setText(data.getDate());

        if(!StringUtil.isNull(data.getImage())) {
            holder.image.setVisibility(View.VISIBLE);
            Glide.with(act).load(data.getImage()).into(holder.image);
        } else {
            holder.image.setVisibility(View.GONE);
        }

        if(!search_result) {
            if (i < 2) {
                holder.image_new.setVisibility(View.VISIBLE);
            } else {
                holder.image_new.setVisibility(View.GONE);
            }
        } else {
            holder.image_new.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(view -> {
            act.startActivity(new Intent(act, BoardDetailAct.class)
                    .putExtra("title", StringUtil.getBoardName(data.getB_type()))
                    .putExtra("b_type", data.getB_type())
                    .putExtra("b_idx", data.getB_idx()));
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image, image_new;
        TextView title, nick, view_count, review_count, date;
        View itemView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            title = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);
            image_new = itemView.findViewById(R.id.image_new);
            nick = itemView.findViewById(R.id.nick);
            view_count = itemView.findViewById(R.id.view_count);
            review_count = itemView.findViewById(R.id.review_count);
            date = itemView.findViewById(R.id.date);
        }
    }
}
