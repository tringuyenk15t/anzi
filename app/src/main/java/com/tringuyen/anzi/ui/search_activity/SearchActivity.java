package com.tringuyen.anzi.ui.search_activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.tringuyen.anzi.Constants;
import com.tringuyen.anzi.R;
import com.tringuyen.anzi.model.google.google_search_activity.GoogleResponse;
import com.tringuyen.anzi.model.google.google_search_activity.Result;
import com.tringuyen.anzi.network.GoogleAPI;
import com.tringuyen.anzi.network.GoogleServiceGenerator;
import com.tringuyen.anzi.ui.map.MapsActivity;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchActivity extends AppCompatActivity implements
        SearchView.OnQueryTextListener, GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener {
    private Toolbar mToolbar;
    private SearchView mSearchView;
    private MenuItem mSearchItem;
    private RecyclerView mResultListRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ResultListAdapter mResultAdapter;
    private GoogleAPI mGoogleAPI;
    private ProgressDialog mProgressDialog;
    private List<Result> mListResult;
    private Location mLocation;
    private LatLng mInitialLocation;

    private GoogleApiClient mGoogleApiClient;



    /**
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initinalizeScreen();

//        implement runtime permission
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[] {
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    Constants.PERMISSION_ACCESS_COARSE_LOCATION);
        }

        //start google api client
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case Constants.PERMISSION_ACCESS_COARSE_LOCATION:
                if(grantResults.length < 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED)
                {
                    //TODO notice user to turn on permission
                }
                break;
        }
    }

    /**
     * initialize screen components
     */
    private void initinalizeScreen() {
        //setup toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(Constants.TOOLBAR_TITLE);
        setSupportActionBar(mToolbar);
        //result list recycler view
        mListResult = new ArrayList<>();
        mResultListRecyclerView = (RecyclerView) findViewById(R.id.rc_result_list);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mResultListRecyclerView.setLayoutManager(mLayoutManager);

        mResultAdapter = new ResultListAdapter(mListResult, this,mInitialLocation);
        mResultListRecyclerView.setAdapter(mResultAdapter);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(getString(R.string.searchProgressTitle));
        mProgressDialog.setMessage(getString(R.string.searchProgressMessage));
        mProgressDialog.setCancelable(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        // Associate searchable configuration with the SearchView
        mSearchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
        mSearchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if(mLocation != null)
        {
            //show loading dialog
            mProgressDialog.show();
            mInitialLocation = new LatLng(mLocation.getLatitude(),mLocation.getLongitude());
            searchLocation(query);
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    /**
     * Search location base on query string inputed by user
     * @param query
     */
    private void searchLocation(String query) {
        //TODO update current location later
        String currentlocationVariable = mInitialLocation.latitude + "," + mInitialLocation.longitude;

        mGoogleAPI = GoogleServiceGenerator.createService(GoogleAPI.class);
        Call<GoogleResponse> call = mGoogleAPI.searchResult(currentlocationVariable, query);
        call.enqueue(new Callback<GoogleResponse>() {
            @Override
            public void onResponse(Call<GoogleResponse> call, Response<GoogleResponse> response) {
                //save result data
                GoogleResponse dataResult = response.body();
                //let user know if there no result match input text
                if (dataResult.getResults().size() < 1) {
                    makeToast(getString(R.string.nullResult));
                } else {
                    mListResult.clear();
                    mListResult.addAll(dataResult.getResults());
                    mResultAdapter.notifyDataSetChanged();
                }
                mProgressDialog.dismiss();
                mSearchView.clearFocus();
            }

            @Override
            public void onFailure(Call<GoogleResponse> call, Throwable t) {
                mProgressDialog.dismiss();
                Log.e("Network error: ", t.getMessage());
                mSearchView.clearFocus();
            }
        });
    }

    /**
     * Navigate to Map view with location results
     * @param view
     */
    public void onMapViewClick(View view) {
        //TODO should find the way to add results (using parcelable is an option)
        Intent intent = new Intent(this, MapsActivity.class);
        //put initial LatLng into intent
        intent.putExtra(Constants.INITIAL_LAT_LOCATION,mInitialLocation.latitude);
        intent.putExtra(Constants.INITIAL_LNG_LOCATION,mInitialLocation.longitude);
        startActivity(intent);
    }

    /**
     * Show toast message
     * @param message
     */
    private void makeToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //TODO implement runtime permission request for android 6.0 or greater
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        else {
            //get the location from service
            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
}
