package fga.mds.gpp.trezentos.View.ServerOperation;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import org.json.JSONException;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import fga.mds.gpp.trezentos.View.Activity.ClassActivity;
import fga.mds.gpp.trezentos.View.Activity.StudentClassActivity;
import fga.mds.gpp.trezentos.View.Adapters.ClassFragmentAdapter;
import fga.mds.gpp.trezentos.Controller.UserClassControl;
import fga.mds.gpp.trezentos.Model.UserClass;
import fga.mds.gpp.trezentos.R;
import fga.mds.gpp.trezentos.View.Fragment.ClassFragment;
import fga.mds.gpp.trezentos.View.Fragment.ExploreFragment;
import fga.mds.gpp.trezentos.View.ViewHolder.ClassViewHolder;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ServerOperationClassFragment extends AsyncTask<String, Void, ArrayList<UserClass>> {

    private ClassFragmentAdapter classFragmentAdapter;
    public ProgressBar progressBar;
    public UserClassControl userClassControl;
    private LinearLayout noUserClass;
    private boolean isInit;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String userId;
    private ClassFragment classFragment;
    private RecyclerView recyclerView;

    public ServerOperationClassFragment(boolean isInit,
                                        SwipeRefreshLayout swipeRefreshLayout,
                                        ProgressBar progressBar,
                                        RecyclerView recyclerView,
                                        ClassFragment classFragment,
                                        LinearLayout noUserClass,
                                        ClassFragmentAdapter classFragmentAdapter){


        this.isInit = isInit;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.progressBar = progressBar;
        this.classFragment = classFragment;
        this.recyclerView = recyclerView;
        this.noUserClass = noUserClass;
        this.classFragmentAdapter = classFragmentAdapter;

    }

    @Override
    protected void onPreExecute() {

        userClassControl =
                UserClassControl.getInstance(getApplicationContext());
        SharedPreferences session = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        userId = session.getString("userId","");

        if(isInit){
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected ArrayList<UserClass> doInBackground(String... params) {

        try {
            return userClassControl.getClasses(userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<UserClass> result) {
        classFragment.setUserClasses(result);

        if(isInit){
            progressBar.setVisibility(View.GONE);
        }else{
            swipeRefreshLayout.setRefreshing(false);
        }
        recyclerView.setVisibility(View.VISIBLE);

        classFragmentAdapter.setFilteredList(result);

        classFragmentAdapter.setOnItemClickListener(callJoinClass());

        RecyclerView.LayoutManager layout = new LinearLayoutManager(classFragment.getContext(),
                LinearLayoutManager.VERTICAL,
                false);
        recyclerView.setLayoutManager(layout);
        recyclerView.setAdapter(classFragmentAdapter);

        if(result == null){
            noUserClass.setVisibility(View.VISIBLE);
        } else {
            noUserClass.setVisibility(View.GONE);
        }

        super.onPostExecute(result);

    }

    private ClassViewHolder.OnItemClickListener callJoinClass() {
        return new ClassViewHolder.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                UserClass userClass = classFragment.getUserClasses().get(position);
                showJoinClassFragment(userClass);
            }
        };
    }

    private void showJoinClassFragment(UserClass userClass){

        if(userClass.getIdClassCreator().equals(userId)){
            Log.d("INTENT","ClassOwner");
            Intent goClass = new  Intent(getApplicationContext(), ClassActivity.class);
            goClass.putExtra("Class", userClass);
            goClass.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(goClass);
        }else{
            Log.d("INTENT","Student");

            Intent goClass = new  Intent(getApplicationContext(), StudentClassActivity.class);
            goClass.putExtra("Class", userClass);
            goClass.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(goClass);
        }

    }


}
