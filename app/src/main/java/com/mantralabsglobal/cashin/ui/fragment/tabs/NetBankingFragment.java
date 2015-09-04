package com.mantralabsglobal.cashin.ui.fragment.tabs;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.NetBankingService;
import com.mantralabsglobal.cashin.ui.Application;

import butterknife.InjectView;
import retrofit.Callback;

public class NetBankingFragment extends BaseBindableFragment<NetBankingService.NetBanking>
{

    NetBankingService netBankingService;

    @InjectView(R.id.net_banking_view)
    ViewGroup netBankingView;

    static int status = -1;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_net_banking, container, false);
    }

    public void createDialog(String message){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setTitle("Net banking information");
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void bindDataToForm(final NetBankingService.NetBanking value) {
        if (value != null) {
            if(value.getStatus() == 1) {
                /*Toast.makeText(getActivity(),"Information already retrieved", Toast.LENGTH_SHORT).show();*/
                status = 1;
            }
            else if(value.getStatus() == 0){
                //TODO make request to perfios
                status = 0;
             //   Toast.makeText(getActivity(),"Information not present\n"+value.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        else {
            save();
        }
    }

    @Override
    protected void onUpdate(NetBankingService.NetBanking updatedData, Callback<NetBankingService.NetBanking> saveCallback) {
        netBankingService.createNetBankingService(updatedData, saveCallback);
    }

    @Override
    protected void onCreate(NetBankingService.NetBanking updatedData, Callback<NetBankingService.NetBanking> saveCallback) {
        netBankingService.createNetBankingService(updatedData, saveCallback);
    }

    @Override
    public NetBankingService.NetBanking getDataFromForm(NetBankingService.NetBanking base) {
        return base;
    }

    @Override
    protected void loadDataFromServer(Callback<NetBankingService.NetBanking> dataCallback) {
        netBankingService.getNetBankingDetail(dataCallback);
    }


    @Override
    protected View getFormView() {
        return netBankingView;
    }

    @Override
    protected void handleDataNotPresentOnServer() {
        //call to perfios
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        netBankingService = ((Application)getActivity().getApplication()).getRestClient().getNetBankingService();

//        if(status == 0) {
//            //TODO make call to perfios for netbanking
//            createDialog("Data not present on server");
//        }
//        else if(status == 1)
//            createDialog("Data already present with us");

        reset(false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }
}