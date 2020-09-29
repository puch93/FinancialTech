package kr.co.core.financial_tech.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.co.core.financial_tech.R;
import kr.co.core.financial_tech.data.CommentData;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private Activity act;
    private ArrayList<CommentData> list;
    CommentClickListener commentClickListener;

    public CommentAdapter(Activity act, ArrayList<CommentData> list, CommentClickListener commentClickListener) {
        this.act = act;
        this.list = list;
        this.commentClickListener = commentClickListener;
    }

    public void setList(ArrayList<CommentData> list) {
        this.list = list;
        notifyDataSetChanged();
    }
    public ArrayList<CommentData> getList() {
        return list;
    }

    public interface CommentClickListener{
        void sendCommentData(CommentData data);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        CommentData data = list.get(i);

        holder.recycler_view.setLayoutManager(new LinearLayoutManager(act));
        holder.recycler_view.setAdapter(new CommentDoubleAdapter(act, data.getList_double()));
        holder.recycler_view.setItemViewCacheSize(20);

        holder.nick.setText(data.getU_nick());
        holder.contents.setText(data.getC_comment());
        holder.date.setText(data.getC_date());

        holder.area_comment.setOnClickListener(view -> {
            commentClickListener.sendCommentData(data);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout area_comment;
        TextView nick, date, contents;
        RecyclerView recycler_view;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.area_comment = itemView.findViewById(R.id.area_comment);
            this.nick = itemView.findViewById(R.id.nick);
            this.date = itemView.findViewById(R.id.date);
            this.contents = itemView.findViewById(R.id.contents);
            this.recycler_view = itemView.findViewById(R.id.recycler_view);
        }
    }
}
