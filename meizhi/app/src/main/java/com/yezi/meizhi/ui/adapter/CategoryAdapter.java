package com.yezi.meizhi.ui.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yezi.meizhi.MeiZhiApp;
import com.yezi.meizhi.R;
import com.yezi.meizhi.model.MeiZhiDetail;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryVH> {

    private List<MeiZhiDetail> mMeizhiList;

    public void updateData(List<MeiZhiDetail> list) {
        mMeizhiList.clear();
        mMeizhiList.addAll(list);
        notifyDataSetChanged();
    }

    public CategoryAdapter() {
        mMeizhiList = new ArrayList<>();
    }

    @Override
    public CategoryVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_category, parent, false);
        return new CategoryVH(view);
    }

    @Override
    public void onBindViewHolder(CategoryVH holder, int position) {
        holder.bind(mMeizhiList.get(position));
    }

    @Override
    public int getItemCount() {
        return mMeizhiList.size();
    }

    public static class CategoryVH extends RecyclerView.ViewHolder {

        @Bind(R.id.img_avator)
        SimpleDraweeView mImgAvator;
        @Bind(R.id.text_title)
        TextView mTextTitle;
        @Bind(R.id.text_author)
        TextView mTextAuthor;
        @Bind(R.id.cardview_category)
        CardView mCardView;

        public CategoryVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(MeiZhiDetail meizhi) {
            mTextTitle.setText(meizhi.desc);
            mTextAuthor.setText(meizhi.who);

            mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MeiZhiApp.showToast("点击");
                }
            });
        }
    }
}