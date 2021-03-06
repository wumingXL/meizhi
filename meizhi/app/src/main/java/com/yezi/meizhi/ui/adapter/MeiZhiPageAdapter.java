package com.yezi.meizhi.ui.adapter;

import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.yezi.meizhi.MeiZhiApp;
import com.yezi.meizhi.Navigator;
import com.yezi.meizhi.R;
import com.yezi.meizhi.api.ServiceFactory;
import com.yezi.meizhi.model.MeiZhiDetail;
import com.yezi.meizhi.ui.activity.MainActivity;
import com.yezi.meizhi.ui.widget.ClickImgPopupWindow;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeiZhiPageAdapter extends PagerAdapter {
    private List<MeiZhiDetail> meiZhiList;

    public MeiZhiPageAdapter() {
        meiZhiList = new ArrayList<>();
    }

    public void updateData(List<MeiZhiDetail> list) {
        meiZhiList.clear();
        meiZhiList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return meiZhiList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = View.inflate(container.getContext(), R.layout.list_item_meizhi, null);
        bindItem(view, meiZhiList.get(position));
        container.addView(view);
        return view;
    }

    private void bindItem(View view, MeiZhiDetail meizhi) {
        SimpleDraweeView draweeView = (SimpleDraweeView) view.findViewById(R.id.img_meizhi);
        draweeView.setImageURI(UriUtil.parseUriOrNull(meizhi.url));

        draweeView.setOnLongClickListener(v -> {
            if (((MainActivity) view.getContext()).isMoveMeiZhiViews()) {
                return false;
            }
            ClickImgPopupWindow popupWindow = new ClickImgPopupWindow();
            popupWindow.showWindow(view.getContext(),
                    v1 -> {
                        Navigator.startImageScaleActivity(view.getContext(), meizhi.url);
                        popupWindow.dismissWindow();
                    },
                    v2 -> {
                        downloadImg(meizhi.url);
                        popupWindow.dismissWindow();
                    });
            return true;
        });
    }

    private void downloadImg(String url) {
        new Thread(() -> {
            ServiceFactory.getMeiZhiService().getMeiZhiImg(url).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        saveImg(response.body(), url);
                    } else {
                        MeiZhiApp.showToast(R.string.toast_download_img_failure);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    MeiZhiApp.showToast(R.string.toast_download_img_failure);
                }
            });
        }).start();
    }

    private File newFile(String url) {
        String[] strings = url.split("/");
        File imgFile = new File(Environment.getExternalStorageDirectory()
                + File.separator + strings[strings.length - 1]);
        if (imgFile.exists()) {
            imgFile.delete();
        } else {
            try {
                imgFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return imgFile;
    }

    private void saveImg(ResponseBody body, String url) {
        try {
            File imgFile = newFile(url);
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(imgFile);
                int read;
                while ((read = inputStream.read(fileReader)) != -1) {
                    outputStream.write(fileReader, 0, read);
                }
                outputStream.flush();
                MeiZhiApp.showToast(R.string.toast_download_img_success);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
