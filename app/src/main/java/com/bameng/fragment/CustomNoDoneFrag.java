package com.bameng.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bameng.BaseApplication;
import com.bameng.R;
import com.bameng.adapter.CustomDetailsAdapter;
import com.bameng.model.CustomListOutput;
import com.bameng.model.CustomerModel;
import com.bameng.model.OperateTypeEnum;
import com.bameng.model.RefreshCustomerEvent;
import com.bameng.service.ApiService;
import com.bameng.service.ZRetrofitUtil;
import com.bameng.ui.base.BaseFragment;
import com.bameng.ui.business.CustomerExamineActivity;
import com.bameng.utils.ActivityUtils;
import com.bameng.utils.AuthParamUtils;
import com.bameng.utils.ToastUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.baidu.location.h.j.m;

/**
 * 未处理的客户信息
 * Created by 47483 on 2016.11.09.
 */
public class CustomNoDoneFrag extends BaseFragment{

    @Bind(R.id.customDoneList)
    PullToRefreshListView customDoneList;
    public OperateTypeEnum operateType= OperateTypeEnum.REFRESH;
    public List<CustomerModel> Customers;
    public CustomDetailsAdapter adapter;
    int pageIndex= 1;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initList();
        loadData();
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        EventBus.getDefault().register(this);

        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public void onStop() {
        super.onStop();


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        EventBus.getDefault().unregister(this);
    }

    private void initList()
    {
        customDoneList.setMode(PullToRefreshBase.Mode.BOTH);
        customDoneList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                operateType = OperateTypeEnum.REFRESH;
                Customers.clear();
                pageIndex=1;
                loadData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                operateType = OperateTypeEnum.LOADMORE;
                loadData();

            }
        });
        Customers = new ArrayList<CustomerModel>();
        adapter = new CustomDetailsAdapter(Customers, getActivity(), getActivity());
        customDoneList.setAdapter(adapter);
    }


    private void loadData()
    {
        Map<String, String> map = new HashMap<>();
        map.put("version", BaseApplication.getAppVersion());
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("os", "android");
        map.put("type","1");
        map.put("pageIndex",String.valueOf(pageIndex));
        map.put("pageSize","20");
        AuthParamUtils authParamUtils = new AuthParamUtils();
        String sign = authParamUtils.getSign(map);
        map.put("sign", sign);
        ApiService apiService = ZRetrofitUtil.getInstance().create(ApiService.class);
        String token = BaseApplication.readToken();
        Call<CustomListOutput> call = apiService.customlist(token, map);
        call.enqueue(new Callback<CustomListOutput>() {
            @Override
            public void onResponse(Call<CustomListOutput> call, Response<CustomListOutput> response) {
                if (response.body() != null) {
                    customDoneList.onRefreshComplete();

                    if (response.body().getStatus() == 200 && response.body().getData() != null) {
                        Customers.addAll(response.body().getData().getRows());
                        adapter.notifyDataSetChanged();
                        pageIndex++;

                    } else if (response.body().getStatus()==70035){

                        ToastUtils.showLongToast(response.body().getStatusText());
                    }

                } else {
                    ToastUtils.showLongToast("连接服务器失败！！！");
                }
                return;


            }

            @Override
            public void onFailure(Call<CustomListOutput> call, Throwable t) {
                ToastUtils.showLongToast("失败");
            }
        });
    }

    @Override
    public void onReshow() {

    }

    @Override
    public void onFragPasue() {

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public int getLayoutRes() {
        return R.layout.frag_customdone;
    }

    @Subscribe( threadMode = ThreadMode.MAIN)
    public void onEventRefreshData(RefreshCustomerEvent event){
        if( event.getTabName().equals("NoDoneFrag")) {
            for (int i = 0; i < Customers.size(); i++) {
                if (Customers.get(i).getID() == event.getCustomerModel().getID()) {
                    Customers.remove(Customers.get(i));
                    break;
                }
            }
            adapter.notifyDataSetChanged();
        }
    }
}
