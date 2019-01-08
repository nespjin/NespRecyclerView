package com.nesp.demo.nesprecyclerviewdemo.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.nesp.demo.nesprecyclerviewdemo.R;
import com.nesp.demo.nesprecyclerviewdemo.model.DataModel;

import java.util.List;

/**
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * @team NESP Technology
 * @time: Created 19-1-3 下午5:21
 * @project NespRecyclerView
 **/
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    private List<DataModel> dataModelList;
    private Context context;

    public RecyclerViewAdapter(Context context, List<DataModel> dataModelList) {
        this.dataModelList = dataModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_data_model, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvName.setText(dataModelList.get(position).getName());
        holder.tvDesc.setText(dataModelList.get(position).getDesc());
    }

    @Override
    public int getItemCount() {
        return dataModelList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvDesc;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.item_data_model_name);
            tvDesc = itemView.findViewById(R.id.item_data_model_desc);
        }
    }
}
