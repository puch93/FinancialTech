package kr.co.core.financial_tech.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.co.core.financial_tech.R;
import kr.co.core.financial_tech.data.CommentDoubleData;

public class CommentDoubleAdapter extends RecyclerView.Adapter<CommentDoubleAdapter.ViewHolder> {
    private Activity act;

    private ArrayList<CommentDoubleData> list;

    public CommentDoubleAdapter(Activity act, ArrayList<CommentDoubleData> list) {
        this.act = act;
        this.list = list;
    }

    public void setList(ArrayList<CommentDoubleData> list) {
        this.list = list;
        notifyDataSetChanged();
    }
    public ArrayList<CommentDoubleData> getList() {
        return list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_double, parent, false);
        return new CommentDoubleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        CommentDoubleData data = list.get(i);

        holder.pre_nick.setText(data.getU_pre_nick());
        holder.nick.setText(data.getU_nick());
        holder.contents.setText(data.getC_comment());
        holder.date.setText(data.getC_date());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView pre_nick, nick, contents, date;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.pre_nick = itemView.findViewById(R.id.pre_nick);
            this.nick = itemView.findViewById(R.id.nick);
            this.contents = itemView.findViewById(R.id.contents);
            this.date = itemView.findViewById(R.id.date);
        }
    }
}
