package com.bameng.ui.base;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;


import com.bameng.BaseApplication;
import com.bameng.R;

import butterknife.ButterKnife;

/**
 * 基本的fragment
 */
public abstract class BaseFragment extends Fragment {

    public View rootView;
    public FrameLayout fgContainer;
    public Resources resources;
    public BaseApplication application;
    public WindowManager wManager;

    public abstract void onReshow();//再切换至要显示的frag,onReshow()不同于onResume()
    public abstract void onFragPasue();//暂时不可见
    public abstract void onClick(View view);

    protected boolean isViewInited = false;
    protected boolean isDataLoaded = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null==rootView){
            resources = getActivity().getResources();
            application = (BaseApplication) getActivity().getApplication();
            wManager = getActivity().getWindowManager();
            rootView = inflater.inflate(R.layout.base_fragment, container, false);
            fgContainer = (FrameLayout) rootView.findViewById(R.id.container);
            inflater.inflate(getLayoutRes(),fgContainer);
            ButterKnife.bind(this,rootView);
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        onReshow();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public abstract int getLayoutRes();

    protected void preLoadData(){
        if( getUserVisibleHint() && isViewInited && !isDataLoaded){
                loadData();
            isDataLoaded=true;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isViewInited = true;

        preLoadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isViewInited=false;
        isDataLoaded = false;

        //ButterKnife.unbind(this);
    }


    protected  void loadData(){

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        preLoadData();
    }

    public String getPageTitle(){
        return this.getClass().getName();
    }

}
