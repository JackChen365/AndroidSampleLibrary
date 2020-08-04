package com.cz.android.sample.library.component.document;

import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.cz.android.sample.component.CompanionComponentContainer;
import com.cz.android.sample.library.adapter.SimpleFragmentPagerAdapter;
import com.cz.android.sample.library.appcompat.R;
import com.cz.android.sample.library.appcompat.SampleWrapperViewFragment;
import com.cz.android.sample.library.component.code.SampleSourceCodeComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by cz
 * @date 2020-01-28 18:04
 * @email bingo110@126.com
 */
public class SampleDocumentComponent extends CompanionComponentContainer<FragmentActivity> {

    @Override
    public boolean isComponentAvailable(Object object) {
        SampleDocument sampleDocument = object.getClass().getAnnotation(SampleDocument.class);
        return null!=sampleDocument&&null!=sampleDocument.value();
    }

    @Override
    public View onCreateCompanionComponent(@NonNull FragmentActivity context, @NonNull Object object, @NonNull ViewGroup parentView, @NonNull View view) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View contentLayout = layoutInflater.inflate(R.layout.sample_fragment_tab, parentView, false);
        ViewPager sampleViewPager=contentLayout.findViewById(R.id.sampleViewPager);
        List<CharSequence> titleList=new ArrayList<>();
        titleList.add(context.getString(R.string.sample));
        List<Fragment> fragmentList=new ArrayList<>();
        fragmentList.add(SampleWrapperViewFragment.newFragment(view));
        sampleViewPager.setOffscreenPageLimit(3);
        sampleViewPager.setAdapter(SimpleFragmentPagerAdapter.create(context.getSupportFragmentManager(), fragmentList,titleList));
        return contentLayout;
    }

    @Override
    public Class<CompanionComponentContainer>[] getCompanionComponent() {
        return new Class[]{ SampleSourceCodeComponent.class };
    }

    @Override
    public View getComponentView(FragmentActivity context,Object object,ViewGroup container, View view) {
        SampleDocument sampleDocument = object.getClass().getAnnotation(SampleDocument.class);
        String url = sampleDocument.value();
        TabLayout sampleTabLayout=view.findViewById(R.id.sampleTabLayout);
        ViewPager sampleViewPager=view.findViewById(R.id.sampleViewPager);

        List<CharSequence> titleList=new ArrayList<>();
        List<Fragment> fragmentList=new ArrayList<>();
        PagerAdapter adapter = sampleViewPager.getAdapter();
        if(adapter instanceof FragmentPagerAdapter){
            FragmentPagerAdapter fragmentPagerAdapter = (FragmentPagerAdapter) adapter;
            for(int i=0;i<fragmentPagerAdapter.getCount();i++){
                Fragment fragment = fragmentPagerAdapter.getItem(i);
                fragmentList.add(fragment);
                CharSequence title = fragmentPagerAdapter.getPageTitle(i);
                titleList.add(title);
            }
        }
        String packageName = object.getClass().getPackage().getName();
        fragmentList.add(SampleDocumentFragment.newInstance(packageName,url));
        titleList.add(context.getString(R.string.sample_document));

        FragmentPagerAdapter fragmentPagerAdapter = SimpleFragmentPagerAdapter.create(context.getSupportFragmentManager(), fragmentList, titleList);
        sampleViewPager.setAdapter(fragmentPagerAdapter);
        sampleTabLayout.setupWithViewPager(sampleViewPager);

        return view;
    }

    @Override
    public void onCreatedView(FragmentActivity context,Object object, View view) {
    }

    @Override
    public int getComponentPriority() {
        return 0;
    }
}
