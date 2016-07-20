package com.yezi.meizhi.ui.adapter;

import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.yezi.meizhi.Navigator;
import com.yezi.meizhi.R;
import com.yezi.meizhi.model.MeiZhiDetail;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MeiZhiDetail> mTextList;
    private List<MeiZhiDetail> mMeiZhiList;

    private boolean mShowFooter;

    public void updateTextData(List<MeiZhiDetail> list) {
        mTextList.clear();
        mTextList.addAll(list);
        notifyDataSetChanged();
    }

    public void updateMeiZhiData(List<MeiZhiDetail> list) {
        mMeiZhiList.clear();
        mMeiZhiList.addAll(list);
        notifyDataSetChanged();
    }

    public CategoryAdapter() {
        mTextList = new ArrayList<>();
        mMeiZhiList = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case R.layout.refresh_header:
                view = layoutInflater.inflate(R.layout.refresh_header, parent, false);
                return new FooterVH(view);
            default:
                view = layoutInflater.inflate(R.layout.list_item_category, parent, false);
                return new CategoryVH(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CategoryVH) {
            CategoryVH categoryVH = (CategoryVH) holder;
            categoryVH.bindText(mTextList.get(position));
            categoryVH.bindMeiZhi(getDataSafe(mMeiZhiList, position));
        }
        if(holder instanceof FooterVH) {
            FooterVH footerVH = (FooterVH) holder;
            footerVH.bind();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isFooterPosition(position))
            return R.layout.refresh_header;
        return R.layout.list_item_category;
    }

    @Override
    public int getItemCount() {
        return mTextList.size() + (isShowingFooter() ? 1 : 0);
    }

    private boolean isFooterPosition(int position) {
        return mShowFooter && getItemCount() - 1 == position;
    }

    public boolean isShowingFooter() {
        return mShowFooter;
    }

    public void showFooter() {
        mShowFooter = true;
        notifyDataSetChanged();
    }

    public void hideFooter() {
        mShowFooter = false;
        notifyDataSetChanged();
    }

    private MeiZhiDetail getDataSafe(List<MeiZhiDetail> list, int position) {
        return (list == null || list.size() <= position) ?
                new MeiZhiDetail() : list.get(position);
    }

    static class CategoryVH extends RecyclerView.ViewHolder {

        @Bind(R.id.img_avatar)
        SimpleDraweeView mImgAvatar;
        @Bind(R.id.text_title)
        TextView mTextTitle;
        @Bind(R.id.text_author)
        TextView mTextAuthor;

        public CategoryVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindText(final MeiZhiDetail meizhi) {
            mTextTitle.setText(meizhi.desc);
            mTextAuthor.setText(meizhi.who);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Navigator.startWebBrowserActivity(v.getContext(), meizhi.desc, meizhi.url);
                }
            });
        }

        public void bindMeiZhi(final MeiZhiDetail meizhi) {
            mImgAvatar.setImageURI(UriUtil.parseUriOrNull(meizhi.url));
        }
    }

    static class FooterVH extends RecyclerView.ViewHolder {
        @Bind(R.id.progress)
        ImageView mImageView;

        public FooterVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void bind() {
            ((AnimationDrawable) mImageView.getDrawable()).start();
        }
    }
}
