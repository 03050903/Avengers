package saulmm.avengers.views.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import saulmm.avengers.AvengersApplication;
import saulmm.avengers.R;
import saulmm.avengers.injector.AppModule;
import saulmm.avengers.injector.components.DaggerAvengersComponent;
import saulmm.avengers.injector.modules.ActivityModule;
import saulmm.avengers.injector.modules.AvengersModule;
import saulmm.avengers.model.Character;
import saulmm.avengers.mvp.presenters.AvengersListPresenter;
import saulmm.avengers.mvp.views.AvengersView;
import saulmm.avengers.views.adapter.AvengersListAdapter;


public class AvengersListActivity extends Activity implements
    AvengersView {

    public final static String EXTRA_CHARACTER_ID = "character_id";

    @InjectView(R.id.activity_avengers_recycler) RecyclerView mAvengersRecycler;
    @Inject AvengersListPresenter mAvengersListPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avengers_list);
        ButterKnife.inject(this);

        initializeRecyclerView();
        initializeDependencyInjector();
        initializePresenter();
    }

    @Override
    protected void onStart() {

        super.onStart();
        mAvengersListPresenter.onStart();
    }

    private void initializePresenter() {

        mAvengersListPresenter.attachView(this);
    }

    private void initializeDependencyInjector() {

        AvengersApplication avengersApplication = (AvengersApplication) getApplication();

        DaggerAvengersComponent.builder()
            .avengersModule(new AvengersModule())
            .activityModule(new ActivityModule(this))
            .appModule(new AppModule(avengersApplication))
            .build().inject(this);
    }

    private void initializeRecyclerView() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mAvengersRecycler.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void showAvengersList(List<Character> avengers) {

        AvengersListAdapter avengersListAdapter = new AvengersListAdapter(avengers, this, mAvengersListPresenter);
        mAvengersRecycler.setAdapter(avengersListAdapter);
    }

    @Override
    protected void onStop() {

        super.onStop();
        mAvengersListPresenter.onStop();
    }
}
